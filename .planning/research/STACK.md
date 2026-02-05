# STACK.md: JSON API Endpoints in Spring Boot 2.6.15

**Research Type:** Stack dimension - Adding JSON API endpoints to existing Spring Boot application
**Project:** Reportcard (Java 17, Spring Boot 2.6.15, JOOQ, MySQL 8.0)
**Date:** 2026-02-05

---

## Executive Summary

**Fact:** [JunitController.java:32-33, GraphJsonController.java:22-23] The codebase already has JSON API endpoints using `@RestController` with standard Spring MVC patterns.

**Inference:** [Test files analysis → controller testing pattern] The project uses `@SpringBootTest` with full application context and direct controller autowiring, NOT MockMvc or WebTestClient.

This research documents the existing patterns and recommends approaches for adding `/run/latest` endpoints based on what's already proven to work in this codebase.

---

## 1. Current JSON API Architecture

### 1.1 Controller Pattern (EXISTING)

**Fact:** [JunitController.java:32-34, GraphJsonController.java:22-24] Controllers use:
- `@RestController` annotation
- `@RequestMapping` for base paths (`/v1/api/junit`, `/v1/api`)
- Method-level `@GetMapping`, `@PostMapping` with path parameters
- Swagger/OpenAPI annotations (`@Operation`, `@Parameter`)

```java
@RestController
@RequestMapping("/v1/api/junit")
public class JunitController {
    @PostMapping(path = "tar.gz", produces = "application/json")
    public ResponseEntity<StagePathTestResultResponse> postJunitXml(...)
}
```

**Fact:** [GraphUIController.java:27, GraphJsonController.java:23] The codebase separates UI (HTML) and JSON controllers:
- `GraphUIController` at base path `""` produces `text/html`
- `GraphJsonController` at path `/v1/api` produces `application/json`

**Inference:** [Path structure → API design] For `/run/latest` endpoints, follow the established pattern:
- JSON endpoint: `/v1/api/company/{company}/org/{org}/repo/{repo}/branch/{branch}/run/latest`
- UI endpoint: `/company/{company}/org/{org}/repo/{repo}/branch/{branch}/run/latest`

### 1.2 Response Wrapper Pattern (EXISTING)

**Fact:** [ResponseDetails.java:1-97, StagePathTestResultResponse.java:1-54] The codebase uses a standardized response wrapper:

```java
@Builder
@Jacksonized
@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDetails {
    int httpStatus;
    String detail;
    String problemType;      // RFC 7807-style problem details
    String problemInstance;
    String stackTrace;       // Included on errors
    Map<String,String> createdUrls;  // For 201 responses
}
```

**Inference:** [Error handling pattern → consistency] This provides:
- Consistent error structure across all endpoints
- HTTP status embedded in JSON (allows flexible status code handling)
- Problem Details for Web APIs (RFC 7807) compatibility
- Stack traces in development (should be filtered in production)

**Confidence: Confident** - Pattern is consistently used across JunitController and StorageController.

---

## 2. JSON Serialization Configuration

### 2.1 Jackson Setup

**Fact:** [build.gradle:62] Spring Boot starter includes Jackson by default via `spring-boot-starter-web`.

**Fact:** [ResponseDetails.java:3-4, 8, 23] Jackson annotations used:
- `@JsonInclude(JsonInclude.Include.NON_EMPTY)` - Excludes null/empty fields
- `@JsonIgnore` - Excludes fields from serialization
- `@Jacksonized` - Lombok integration for immutable objects

**Fact:** [application.properties:1-49, WebMvcConfig.java:1-17] NO custom Jackson configuration found. Spring Boot defaults apply:
- ISO-8601 date/time format
- Camel case field names
- Null fields included by default (overridden per-class)

**Inference:** [No custom config → Spring defaults work] Spring Boot's Jackson autoconfiguration is sufficient. The existing codebase works without custom `ObjectMapper` beans.

### 2.2 Java Time Handling

**Fact:** [build.gradle:172, StagePath.java:16] The codebase uses `java.time.Instant`:
- JOOQ configured with `.withJavaTimeTypes(true)`
- Models use `Instant` for timestamps

**Inference:** [Jackson defaults → ISO-8601] Jackson's JavaTimeModule (auto-registered in Spring Boot 2.6.15) serializes `Instant` as ISO-8601 strings (e.g., `2026-02-05T10:30:00Z`).

**Recommendation:** Continue using `Instant` for timestamps in new endpoints. NO custom Jackson configuration needed.

**Confidence: Certain** - This is standard Spring Boot behavior since 2.0.

---

## 3. Testing Patterns for JSON Endpoints

### 3.1 Current Testing Approach: @SpringBootTest with Direct Injection

**Fact:** [JunitControllerTest.java:44-50, JobDashboardEndpointsIntegrationTest.java:17-27] ALL controller tests use:

```java
@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JunitControllerTest {
    @Autowired
    JunitController junitController;  // Direct injection, NOT via HTTP

    @Test
    void postJunitTest() {
        StagePathTestResult result = junitController.doPostJunitXml(...);
        assertNotNull(result);
    }
}
```

**Fact:** [Grep results] NO usage of `MockMvc`, `WebTestClient`, `RestTemplate`, or `RestAssured` found in test files.

**Inference:** [Pattern consistency → architectural decision] This project deliberately tests controllers by:
1. Calling controller methods directly (not via HTTP)
2. Using full Spring context with Testcontainers (MySQL + LocalStack)
3. Testing business logic integration, not HTTP serialization

### 3.2 Tradeoffs Analysis

#### Current Approach: @SpringBootTest + Direct Injection

**Strengths:**
- **Fact:** [JunitControllerTest.java:95-377] Tests verify end-to-end behavior including database and S3
- **Inference:** Catches integration issues between controller, service, persistence, and storage layers
- **Inference:** Simpler test code - no need to serialize/deserialize or construct HTTP requests

**Weaknesses:**
- **Inference:** Does NOT test HTTP aspects:
  - Request parameter binding
  - Path variable extraction
  - Content negotiation
  - HTTP status codes (relies on manual ResponseEntity creation)
- **Inference:** Slower startup (full Spring context + Testcontainers)
- **Inference:** Tests are technically integration tests, not unit tests

**Confidence: Confident** - Tradeoffs are inherent to the testing approach chosen.

#### Alternative: MockMvc (NOT USED)

**What it would provide:**
- Tests HTTP layer (serialization, parameter binding, status codes)
- Faster than full integration tests (can use `@WebMvcTest` for sliced tests)
- Verifies Spring MVC configuration

**Why NOT to add it:**
- **[OPINION]** Inconsistent with existing codebase patterns
- **Inference:** Would require duplicating tests (both direct + HTTP layer)
- **Inference:** Spring Boot 2.6.15's parameter binding is reliable - low risk

**Recommendation:** CONTINUE using existing pattern for consistency. MockMvc adds complexity without significant benefit given Testcontainers setup already exists.

**Confidence: Likely** - Based on project patterns, but MockMvc could add value if HTTP-level bugs emerge.

#### Alternative: WebTestClient (NOT USED, Spring 5+)

**Fact:** Spring Boot 2.6.15 supports WebTestClient (reactive Spring).

**Why NOT to use:**
- **Inference:** Project is NOT reactive (uses Spring MVC, not WebFlux)
- **Inference:** Would require additional dependencies (`spring-boot-starter-webflux` for testing)
- **[OPINION]** Overkill for non-reactive application

**Recommendation:** DO NOT use WebTestClient.

**Confidence: Certain** - WebTestClient is designed for reactive apps.

---

## 4. Recommendations for /run/latest Endpoints

### 4.1 Controller Structure

**Recommendation:** Follow existing pattern from `GraphJsonController`:

```java
@RestController
@RequestMapping("/v1/api")
public class GraphJsonController {

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/run/latest",
                produces = "application/json")
    public ResponseEntity<StagePathTestResultResponse> getLatestRun(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) List<String> jobInfo  // Filter by job
    ) {
        // Service call to resolve latest run
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
```

**Rationale:**
- **Fact:** [GraphJsonController.java:98-118] Pattern matches existing job dashboard endpoint
- **Inference:** Consistent with REST conventions (path = resource hierarchy)
- **Inference:** `@RequestParam` for optional filters matches existing patterns

**Confidence: Confident** - Proven pattern in codebase.

### 4.2 Response Model

**Recommendation:** Reuse existing response wrappers:

```java
// For single run
ResponseEntity<StagePathTestResultResponse>

// For run with multiple stages
ResponseEntity<StagePathStorageResultCountResponse>

// Or create new wrapper following same pattern
@Builder
@Jacksonized
@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LatestRunResponse {
    StagePath stagePath;
    RunMetadata runMetadata;  // New domain model
    ResponseDetails responseDetails;
}
```

**Rationale:**
- **Fact:** [StagePathTestResultResponse.java:16-54] Existing wrappers provide `ResponseDetails` + domain model
- **Inference:** Consistent error handling across all endpoints
- **Inference:** Allows evolution (can add metadata without breaking clients)

**Confidence: Confident** - Pattern is well-established.

### 4.3 Testing Approach

**Recommendation:** Use existing `@SpringBootTest` pattern:

```java
@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LatestRunEndpointTest {

    @Autowired
    private GraphJsonController controller;  // Or new controller

    @Autowired
    private TestResultPersistService persistService;

    @Test
    void testGetLatestRun_withMultipleRuns_returnsNewest() {
        // Setup: Insert test data with known timestamps
        persistService.insertTestResult(..., runReference1);
        persistService.insertTestResult(..., runReference2);

        // Execute: Call controller directly
        ResponseEntity<LatestRunResponse> response =
            controller.getLatestRun("company1", "org1", "repo1", "main", null);

        // Verify: Assert latest run returned
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(runReference2, response.getBody().getStagePath().getRun().getRunReference());
    }
}
```

**Rationale:**
- **Fact:** [JunitControllerTest.java:44-50] Pattern matches all existing controller tests
- **Inference:** Consistency trumps theoretical testing purity
- **Inference:** Testcontainers setup already handles complexity of full integration

**Confidence: Confident** - Consistent with project architecture.

---

## 5. What NOT to Use (and Why)

### 5.1 DO NOT: Add MockMvc

**Rationale:**
- **Fact:** [Grep results] Zero usage in existing codebase
- **Inference:** Introduces inconsistency
- **[OPINION]** HTTP layer testing provides minimal value given direct controller testing + production validation

**Exception:** If HTTP-level bugs emerge (parameter binding issues, serialization failures), THEN consider adding MockMvc for specific failing scenarios.

**Confidence: Likely** - Based on cost/benefit for this specific project.

### 5.2 DO NOT: Create Custom Jackson ObjectMapper

**Rationale:**
- **Fact:** [application.properties:1-49, WebMvcConfig.java:1-17] No custom Jackson config exists
- **Fact:** [ResponseDetails.java:23] Per-class customization via `@JsonInclude` works fine
- **Inference:** Spring Boot defaults handle all current needs

**When to reconsider:** If you need global Jackson settings (e.g., always exclude nulls, custom date format), THEN create a `@Configuration` with `@Bean ObjectMapper`.

**Confidence: Confident** - Spring Boot Jackson defaults are battle-tested.

### 5.3 DO NOT: Use @WebMvcTest for Sliced Testing

**Rationale:**
- **Fact:** [JunitControllerTest.java:44] Project uses `@SpringBootTest` with full context
- **Inference:** Tests intentionally verify controller + service + persistence integration
- **Inference:** `@WebMvcTest` would require extensive mocking (service layer, JOOQ, S3)

**When to reconsider:** If test startup time becomes problematic (>30 seconds), THEN consider adding fast unit tests with `@WebMvcTest` for critical paths.

**Confidence: Likely** - Current approach prioritizes integration coverage over speed.

### 5.4 DO NOT: Add RestAssured

**Rationale:**
- **Fact:** [build.gradle:38-116] Not in dependencies
- **Inference:** Project doesn't test via actual HTTP calls
- **[OPINION]** RestAssured excels at API contract testing, but this project tests business logic directly

**When to reconsider:** If you add consumer-driven contract tests (e.g., Pact) or need BDD-style API tests.

**Confidence: Likely** - Overkill for current testing strategy.

---

## 6. Potential Issues and Mitigations

### 6.1 Issue: JSON Serialization Failures Not Tested

**Problem:**
- **Inference:** Direct controller calls bypass Jackson serialization
- **Inference:** Could ship endpoints that fail at runtime due to serialization issues

**Example Scenario:**
```java
// This passes tests but fails at runtime
@Data
public class BadModel {
    private final String value;  // No no-arg constructor
}
```

**Mitigation:**
1. **Recommended:** Add explicit serialization tests for new models:
   ```java
   @Test
   void testLatestRunResponse_serializesToJson() throws Exception {
       ObjectMapper mapper = new ObjectMapper();
       LatestRunResponse response = ...;
       String json = mapper.writeValueAsString(response);
       assertNotNull(json);
       // Optionally: deserialize and verify
   }
   ```

2. **Stronger (but more work):** Add one MockMvc test per new endpoint to verify HTTP layer:
   ```java
   @Autowired
   private MockMvc mockMvc;

   @Test
   void testLatestRunEndpoint_viaHttp() throws Exception {
       mockMvc.perform(get("/v1/api/company/foo/org/bar/repo/baz/branch/main/run/latest"))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON));
   }
   ```

**Confidence: Certain** - This is a real gap in current testing strategy.

### 6.2 Issue: Parameter Binding Edge Cases

**Problem:**
- **Inference:** Tests don't verify Spring's handling of:
  - Special characters in path variables (e.g., `branch/feature/foo`)
  - Invalid parameter types
  - Missing required parameters

**Mitigation:**
- **Fact:** [JunitController.java:73-74] Existing endpoints use string path variables with no encoding issues reported
- **Inference:** Spring Boot's default parameter binding is reliable for simple cases
- **Recommended:** Document assumptions (e.g., branch names must not contain `/`) and handle in service layer

**Confidence: Possible** - Issue may not materialize, but worth documenting.

### 6.3 Issue: HTTP Status Codes Not Enforced

**Problem:**
- **Fact:** [StagePathTestResultResponse.java:43-45] Controllers manually create `ResponseEntity` with status
- **Inference:** Easy to return wrong status code (e.g., `200` instead of `201` for POST)

**Mitigation:**
- **Recommended:** Use static factory methods (already exists):
  ```java
  return StagePathTestResultResponse.created(result).toResponseEntity();
  ```
- **Alternative:** Use `@ResponseStatus` annotation where applicable

**Confidence: Likely** - Manual status code creation is error-prone.

---

## 7. Testing Pattern Decision Matrix

| Approach | Startup Speed | HTTP Layer Coverage | Integration Coverage | Consistency with Codebase | Recommendation |
|----------|---------------|---------------------|----------------------|---------------------------|----------------|
| **@SpringBootTest + Direct Injection** (current) | Slow (~5-10s) | None | Full (DB + S3 + Services) | Perfect | **USE THIS** |
| **@SpringBootTest + MockMvc** | Slow (~5-10s) | Full | Full | Medium (hybrid) | Optional for critical paths |
| **@WebMvcTest + Mocking** | Fast (~1-2s) | Full | None (mocked) | Poor (new pattern) | DO NOT use |
| **RestAssured + @SpringBootTest** | Slow (~5-10s) | Full (real HTTP) | Full | Poor (new dependency) | DO NOT use |

**Key Decision:** Prioritize consistency. The existing pattern works and is understood by the team.

**Confidence: Confident** - Architectural consistency reduces cognitive load.

---

## 8. Implementation Checklist

For adding `/run/latest` endpoints:

- [ ] Create controller method following `GraphJsonController` pattern
- [ ] Use `@GetMapping` with path `/v1/api/company/{company}/org/{org}/repo/{repo}/branch/{branch}/run/latest`
- [ ] Add `@Operation` Swagger annotation
- [ ] Return `ResponseEntity<XxxResponse>` wrapping domain model + `ResponseDetails`
- [ ] Implement service layer method to resolve "latest run" (query by max timestamp or run_id)
- [ ] Write `@SpringBootTest` integration test with:
  - Testcontainers setup
  - Test data insertion with known timestamps
  - Verification of latest run returned
- [ ] OPTIONAL: Add one serialization unit test for new response model
- [ ] OPTIONAL: Add one MockMvc test if HTTP layer validation needed
- [ ] Update Swagger UI (auto-generated, just verify it appears)
- [ ] Test manually with curl/Postman

---

## 9. Adversarial Self-Review

### What evidence am I ignoring that would change my conclusion?

**Missing data:**
- No information on test execution time (claimed "slow" without measurement)
- No data on production HTTP-level bugs that might justify MockMvc
- No team feedback on whether direct controller testing causes maintenance issues

**Counter-evidence that could matter:**
- If Spring Boot's parameter binding has edge cases in 2.6.15 that cause production bugs
- If future requirements include API contract testing (Pact, Spring Cloud Contract)
- If new team members find direct controller testing confusing

### What's the strongest argument AGAINST this approach?

**Against direct controller testing:**
- **Principle:** Tests should exercise code as close to production as possible
- **Risk:** Serialization bugs won't be caught until production
- **Industry practice:** Many Spring teams use MockMvc as standard

**Counter-argument:**
- **Fact:** This project has shipped endpoints using this pattern without reported serialization issues
- **Pragmatism:** Testcontainers setup already incurs startup cost, so MockMvc adds little value
- **Risk mitigation:** Can add serialization tests for new models without full HTTP testing

### What assumptions am I making that could be wrong?

1. **Assumption:** Test startup time is acceptable
   - **Could be wrong if:** Team runs tests frequently and values fast feedback
   - **Check:** Measure actual test execution time

2. **Assumption:** HTTP layer bugs are rare/low-impact
   - **Could be wrong if:** Parameter binding or serialization has caused production issues
   - **Check:** Review production logs and incident reports

3. **Assumption:** Team prefers consistency over "best practices"
   - **Could be wrong if:** New lead wants to align with industry standards
   - **Check:** Discuss with team before implementing

### What could go wrong if they follow this advice?

**Worst-case scenario:**
1. Ship endpoint with serialization bug (e.g., circular reference, missing getter)
2. Endpoint returns 500 error in production
3. Team scrambles to add MockMvc tests after the fact
4. Conclusion: Should have tested HTTP layer from the start

**Likelihood: Speculative** - No evidence of this happening in existing endpoints.

**Mitigation:** Add explicit serialization unit tests for new models.

### Would I defend this to a skeptic?

**Skeptic's challenge:** "You're recommending NOT using MockMvc, which is Spring's recommended testing approach. Why?"

**My defense:**
1. **Fact:** Project has 4 existing controller tests, all using direct injection, with no reported issues
2. **Principle:** Consistency within a codebase trumps external "best practices"
3. **Pragmatism:** Testcontainers setup already provides high-fidelity integration testing
4. **Risk mitigation:** Recommended adding serialization tests for new models
5. **Flexibility:** Explicitly stated when to reconsider (if HTTP bugs emerge)

**Confidence: Confident** - I'd defend this, but I acknowledge it's a tradeoff, not an absolute truth.

---

## 10. Confidence Summary

| Topic | Confidence | Rationale |
|-------|-----------|-----------|
| Use `@RestController` pattern | **Certain** | Established in existing controllers |
| Continue `@SpringBootTest` testing | **Confident** | 4 examples, zero MockMvc usage |
| Avoid custom Jackson config | **Confident** | None exists, defaults work |
| Serialization testing gap | **Certain** | Logical consequence of direct testing |
| Don't use WebTestClient | **Certain** | Not a reactive app |
| Don't use RestAssured | **Likely** | No evidence of need, adds complexity |
| Test startup time acceptable | **Possible** | Not measured, assumption |

---

## 11. Sources

**Code Analysis:**
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` (lines 32-273)
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphJsonController.java` (lines 22-120)
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphUIController.java` (lines 27-228)
- `/reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/JunitControllerTest.java` (lines 44-424)
- `/reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/graph/JobDashboardEndpointsIntegrationTest.java` (lines 17-73)
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/model/ResponseDetails.java` (lines 1-97)
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/model/StagePathTestResultResponse.java` (lines 1-54)
- `/reportcard-server/src/main/resources/application.properties`
- `/reportcard-server/build.gradle` (lines 38-116)

**Search Results:**
- Glob for controllers: 4 controllers found, all using `@RestController`
- Grep for test patterns: Zero usage of MockMvc, WebTestClient, RestTemplate, RestAssured
- Grep for Jackson config: Found annotations but no custom `@Bean ObjectMapper`

**External Knowledge (Spring Boot 2.6.15):**
- Spring Boot 2.6.15 includes Jackson 2.13.x with JavaTimeModule auto-registered
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` starts embedded Tomcat but doesn't require HTTP calls
- MockMvc available since Spring 3.2, standard for controller testing in Spring documentation

---

## 12. Changelog

**2026-02-05:** Initial research for `/run/latest` endpoint milestone

---

**End of STACK.md**
