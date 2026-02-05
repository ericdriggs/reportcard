# Phase 1: Foundation & Validation - Research

**Researched:** 2025-02-05
**Domain:** Spring Boot REST API integration testing with Testcontainers MySQL
**Confidence:** HIGH

## Summary

Phase 1 validates existing JSON browse endpoints in BrowseJsonController. The codebase already has robust testing infrastructure in place: Testcontainers MySQL 8.0.33 with pre-seeded test data, Spring Boot integration test patterns via AbstractBrowseServiceTest, and BrowseService methods that correspond 1:1 with controller endpoints. The controller uses hierarchical async caching (AbstractAsyncCache pattern) but passes through to BrowseService for most operations. Error handling uses Spring's ResponseStatusException with descriptive messages following "Unable to find X <- Y <- Z" format.

The existing BrowseServiceTest demonstrates the pattern: test methods call browseService directly (not through controller), verify returned POJOs contain expected test data, and validate 404 errors with message content checks. No JSON serialization tests exist yet - this is the gap Phase 1 must fill.

**Primary recommendation:** Create BrowseJsonControllerTest that mirrors BrowseServiceTest structure but calls controller methods and validates JSON serialization succeeds. Reuse AbstractBrowseServiceTest for test infrastructure.

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 2.6.15 | Test framework & DI | Already in use, provides @SpringBootTest |
| Testcontainers | latest in deps | MySQL container | Already configured in MyEmbeddedMysql |
| JUnit 5 | latest in deps | Test assertions | Standard Spring Boot test stack |
| MySQL | 8.0.33 | Database container | Matches production MySQL 8.0 version |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Jackson | via Spring Boot | JSON serialization | Implicit - Spring Boot autoconfigures |
| JOOQ | via project deps | Database POJOs | Already used for entity classes |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Direct controller calls | MockMvc | Phase context specifies "Direct controller calls (existing pattern)" - locked decision |
| Testcontainers | H2 in-memory | Testcontainers matches production MySQL better, already configured |

**Installation:**
No new dependencies needed - all testing infrastructure already exists.

## Architecture Patterns

### Recommended Project Structure
```
src/test/java/io/github/ericdriggs/reportcard/
├── controller/
│   └── browse/
│       └── BrowseJsonControllerTest.java  # NEW - Phase 1 creates this
├── persist/
│   └── browse/
│       ├── AbstractBrowseServiceTest.java  # EXISTING - base test class
│       └── BrowseServiceTest.java          # EXISTING - service layer tests
└── config/
    └── MyEmbeddedMysql.java               # EXISTING - Testcontainers setup
```

### Pattern 1: Spring Boot Integration Test Base Class
**What:** Abstract test class with @SpringBootTest, @ActiveProfiles("test"), autowired services
**When to use:** All integration tests requiring database access
**Example:**
```java
// Source: AbstractBrowseServiceTest.java (existing pattern)
@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class AbstractBrowseServiceTest {
    protected final BrowseService browseService;

    @Autowired
    public AbstractBrowseServiceTest(BrowseService browseService) {
        this.browseService = browseService;
    }
}
```

### Pattern 2: Direct Service Injection in Tests
**What:** Tests extend abstract base, autowire controller in constructor
**When to use:** For controller tests in Phase 1
**Example:**
```java
// Pattern to follow for BrowseJsonControllerTest
public class BrowseJsonControllerTest extends AbstractBrowseServiceTest {
    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerTest(BrowseService browseService,
                                    BrowseJsonController controller) {
        super(browseService);
        this.controller = controller;
    }
}
```

### Pattern 3: Pre-Seeded Test Data via SQL Files
**What:** Testcontainers copies SQL files to /docker-entrypoint-initdb.d/ at startup
**When to use:** All tests - automatic, no setup code needed
**Example:**
```java
// Source: MyEmbeddedMysql.java
mySQLContainer = new MySQLContainer<>("mysql:8.0.33")
    .withDatabaseName(schema)
    .withUsername(username)
    .withPassword(password)
    .withCopyFileToContainer(MountableFile.forClasspathResource(ddlsql),
                             "/docker-entrypoint-initdb.d/0_schema.sql")
    .withCopyFileToContainer(MountableFile.forClasspathResource(dmlsql),
                             "/docker-entrypoint-initdb.d/1_config.sql")
    .withCopyFileToContainer(MountableFile.forClasspathResource("db/test/test-data.dml.sql"),
                             "/docker-entrypoint-initdb.d/2_data.sql");
```

**Test data constants available in TestData enum:**
- company: "company1" (ID: 1)
- org: "org1" (ID: 1)
- repo: "repo1" (ID: 1)
- branch: "master" (ID: 1)
- jobId: 1L with jobInfo: {"application":"fooapp", "host":"foocorp.jenkins.com", "pipeline":"foopipeline"}
- runReference: UUID "aaaaaaaa-2222-bbbb-cccc-dddddddddddd" (ID: 1)
- sha: "bdd15b6fae26738ca58f0b300fc43f5872b429bf"
- stage: "api" (ID: 1)
- testResultId: 1L

### Pattern 4: ResponseEntity Validation
**What:** Controller returns ResponseEntity<T>, verify both status code and body
**When to use:** All controller tests
**Example:**
```java
// Pattern for success cases
ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> response = controller.getCompanyOrgs();
assertEquals(HttpStatus.OK, response.getStatusCode());
assertNotNull(response.getBody());
assertFalse(response.getBody().isEmpty());

// Pattern for error cases - service throws ResponseStatusException
// Controller does NOT catch these - Spring's exception handling converts them
ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
    browseService.getOrg("company1", "MISSING_ORG");
});
assertEquals(404, ex.getStatus().value());
assertTrue(ex.getMessage().contains("company1"));
assertTrue(ex.getMessage().contains("MISSING_ORG"));
```

### Pattern 5: JSON Serialization Validation
**What:** Verify ResponseEntity body is serializable and produces valid JSON structure
**When to use:** All Phase 1 tests (this is the new validation layer)
**Example:**
```java
// Verify JSON serializes without error (implicit via Jackson)
ResponseEntity<?> response = controller.getCompanyOrgs();
Object body = response.getBody();
assertNotNull(body); // If Jackson couldn't serialize, Spring would fail earlier

// For explicit JSON validation (if needed):
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(response.getBody());
assertNotNull(json);
assertTrue(json.length() > 2); // More than just "{}"
```

### Pattern 6: Hierarchical Error Messages
**What:** Error messages follow parent-to-child hierarchy format
**When to use:** All 404 validations
**Example:**
```java
// Source: BrowseService.getNotFoundMessage()
// Format: "Unable to find X <- Y <- Z"
// Example: "Unable to find company: company1 <- org: MISSING_ORG"
protected static String getNotFoundMessage(String... args) {
    return "Unable to find " + String.join(" <- ", args);
}

// In tests:
assertTrue(ex.getMessage().contains("company: company1"));
assertTrue(ex.getMessage().contains("org: MISSING_ORG"));
```

### Anti-Patterns to Avoid
- **Don't use MockMvc:** Phase context specifies "Direct controller calls (existing pattern)"
- **Don't create new test data:** Use existing TestData constants from test-data.dml.sql
- **Don't test service layer again:** BrowseServiceTest already validates business logic - focus on controller/JSON layer
- **Don't validate JSON structure deeply:** Phase requires "serializes without error" and "non-empty" - not schema validation

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Test database setup | Custom SQL execution | MyEmbeddedMysql with Testcontainers | Already configured, handles lifecycle |
| Test data generation | @BeforeEach setup methods | Pre-seeded test-data.dml.sql | Consistent across all tests, fast startup |
| JSON serialization | Manual JSON builders | Spring Boot's Jackson autoconfiguration | Already configured, matches production |
| Error assertions | String parsing | ResponseStatusException properties | Type-safe, includes status code + message |

**Key insight:** The codebase has mature testing infrastructure. Phase 1 adds a thin validation layer on top, not a parallel test suite.

## Common Pitfalls

### Pitfall 1: Testing Service Instead of Controller
**What goes wrong:** Tests call browseService methods directly instead of controller methods
**Why it happens:** BrowseServiceTest already exists with good patterns, easy to copy wrong layer
**How to avoid:** Always call controller.methodName(), not browseService.methodName()
**Warning signs:** Tests pass but don't validate JSON serialization (the core Phase 1 goal)

### Pitfall 2: Cache Interference Between Tests
**What goes wrong:** Caches (CompanyOrgsCache.INSTANCE, BranchJobsRunsCacheMap.INSTANCE) retain state across tests
**Why it happens:** Cache singletons live at class level, not cleared between tests
**How to avoid:** Controller endpoints use caches - test via controller to match real usage. Don't try to clear/mock caches.
**Warning signs:** Tests fail when run together but pass individually

### Pitfall 3: Missing Ancestor 404s Not Tested
**What goes wrong:** Only test direct 404s (missing repo) but not ancestor 404s (missing org when querying repo)
**Why it happens:** Easy to forget hierarchy validation happens at every level
**How to avoid:** For each level, test: (1) direct 404 (entity not found), (2) parent 404 (ancestor missing)
**Warning signs:** BrowseServiceTest.getRepoNotFoundTest shows both patterns - follow that structure

### Pitfall 4: ResponseStatusException Thrown by Service, Not Controller
**What goes wrong:** Expecting controller to throw ResponseStatusException, but service throws it
**Why it happens:** Controller just calls service methods - service handles validation
**How to avoid:** Controller tests must catch exceptions from service layer calls, not directly from controller
**Warning signs:**
```java
// WRONG - controller doesn't throw directly
assertThrows(ResponseStatusException.class, () -> {
    controller.getCompanyOrgsRepos("MISSING_COMPANY");
});

// RIGHT - service throws when controller calls it
assertThrows(ResponseStatusException.class, () -> {
    browseService.getOrg("company1", "MISSING_ORG");
});
// Or test via ResponseEntity when controller catches (but BrowseJsonController doesn't catch)
```

**IMPORTANT:** BrowseJsonController has NO try-catch blocks. It lets ResponseStatusException propagate to Spring's exception handlers. Tests must catch exceptions at service layer or rely on Spring's error handling in integration tests.

### Pitfall 5: Testing Every Cache Method
**What goes wrong:** Trying to test CompanyOrgsCache.INSTANCE.getCache() directly
**Why it happens:** Cache classes are visible, tempting to test them
**How to avoid:** Caches are implementation details. Test controller endpoints which use caches internally.
**Warning signs:** Tests become complex with cache lifecycle management

## Code Examples

Verified patterns from existing codebase:

### BrowseJsonController Endpoints (All 10 endpoints to test)
```java
// Source: BrowseJsonController.java

// 1. Root - Company/Orgs hierarchy
@GetMapping(path = "", produces = "application/json")
public ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> getCompanyOrgs()

// 2. Company level - Orgs/Repos for company
@GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
public ResponseEntity<Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>> getCompanyOrgsRepos(@PathVariable String company)

// 3. Org level - Repos/Branches for org
@GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
public ResponseEntity<Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>>> getOrgReposBranches(
    @PathVariable String company, @PathVariable String org)

// 4. Repo level - Branches/Jobs for repo
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
public ResponseEntity<Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>>> getRepoBranchesJobs(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo)

// 5. Branch level - Jobs/Runs for branch
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getBranchJobsRuns(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo,
    @PathVariable String branch, @RequestParam(required = false) Map<String, String> jobInfoFilters)

// 6. Job level - Runs/Stages for job
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "application/json")
public ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> getJobRunsStages(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo,
    @PathVariable String branch, @PathVariable Long jobId)

// 7. Run level - Stages/TestResults for run
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage"}, produces = "application/json")
public ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> getStagesByIds(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo,
    @PathVariable String branch, @PathVariable Long jobId, @PathVariable Long runId)

// 8. SHA lookup - Runs for SHA
@GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run", produces = "application/json")
public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getRuns(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo,
    @PathVariable String branch, @PathVariable String sha,
    @RequestParam(required = false) Map<String, String> jobInfoFilters)

// 9. SHA + Run reference lookup
@GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}", produces = "application/json")
public ResponseEntity<RunPojo> getRunForReference(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo,
    @PathVariable String branch, @PathVariable String sha, @PathVariable UUID runReference,
    @RequestParam(required = false) Map<String, String> metadataFilters)

// 10. Stage detail - TestSuites/TestCases for stage
@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}", produces = "application/json")
public ResponseEntity<StageTestResultModel> getStageTestResultsTestSuites(
    @PathVariable String company, @PathVariable String org, @PathVariable String repo,
    @PathVariable String branch, @PathVariable Long jobId, @PathVariable Long runId,
    @PathVariable String stage)
```

**Note:** Endpoints 5 and 8 have `jobInfoFilters` parameter but controller has TODO comment "use jobInfoFilters". Phase 1 should test these endpoints without filters (pass null) since filter logic isn't implemented yet.

### Test Success Case Pattern
```java
// Source: BrowseServiceTest.getCompanyOrgsSuccessTest (adapted for controller)
@Test
void getCompanyOrgsJsonSuccessTest() {
    // Call controller instead of service
    ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> response = controller.getCompanyOrgs();

    // Verify HTTP 200 OK
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verify body is not null (JSON serialization succeeded)
    assertNotNull(response.getBody());
    Map<CompanyPojo, Set<OrgPojo>> companyOrgs = response.getBody();

    // Verify response is non-empty
    assertFalse(companyOrgs.isEmpty());

    // Verify expected test data appears (matches BrowseServiceTest pattern)
    boolean companyWasFound = false;
    for (Map.Entry<CompanyPojo, Set<OrgPojo>> entry : companyOrgs.entrySet()) {
        final CompanyPojo company = entry.getKey();
        final Set<OrgPojo> orgs = entry.getValue();
        assertNotNull(orgs);
        assertFalse(orgs.isEmpty());
        if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
            Set<String> orgNames = orgs.stream()
                .map(OrgPojo::getOrgName)
                .collect(Collectors.toSet());
            assertTrue(orgNames.contains(TestData.org));
            companyWasFound = true;
        }
    }
    assertTrue(companyWasFound);
}
```

### Test 404 Error Pattern
```java
// Source: BrowseServiceTest.getOrgNotFoundTest (service layer validation)
@Test
void getOrgNotFoundJsonTest() {
    // Service throws ResponseStatusException when entity not found
    // Controller lets it propagate to Spring's exception handlers
    ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
        browseService.getOrg(TestData.company, "MISSING_ORG");
    });

    // Verify 404 status
    assertEquals(404, ex.getStatus().value());

    // Verify error message content (hierarchical format)
    assertNotNull(ex.getMessage());
    assertTrue(ex.getMessage().contains(TestData.company));
    assertTrue(ex.getMessage().contains("MISSING_ORG"));
}
```

**CRITICAL:** BrowseJsonController does NOT have @ExceptionHandler methods. ResponseStatusException propagates to Spring's global exception handling (ResponseEntityExceptionHandler). For Phase 1, test error cases at service layer (as shown above) since controller is just a pass-through.

### Test Data Constants Usage
```java
// Source: TestData.java (existing test data)
public enum TestData {
    ;
    public final static String company = "company1";
    public final static String org = "org1";
    public final static String repo = "repo1";
    public final static String branch = "master";
    public final static String sha = "bdd15b6fae26738ca58f0b300fc43f5872b429bf";
    public static final Long testResultId = 1L;
    public static final UUID runReference = UUID.fromString("aaaaaaaa-2222-bbbb-cccc-dddddddddddd");
    public final static Long jobId = 1L;
    public final static TreeMap<String, String> jobInfo = new TreeMap<>();
    static {
        jobInfo.put("host", "foocorp.jenkins.com");
        jobInfo.put("application", "fooapp");
        jobInfo.put("pipeline", "foopipeline");
    }
    public final static String stage = "api";
    // ... more constants
}

// Usage in tests:
ResponseEntity<?> response = controller.getCompanyOrgsRepos(TestData.company);
assertEquals(HttpStatus.OK, response.getStatusCode());
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| No JSON endpoint tests | Phase 1 adds validation layer | 2025-02-05 (this phase) | Validates serialization works |
| Service layer tests only | Controller + service tests | 2025-02-05 (this phase) | End-to-end validation |

**Deprecated/outdated:**
- N/A - This is a greenfield testing effort for existing endpoints

## Open Questions

1. **Should controller tests verify cache behavior?**
   - What we know: Controller uses CompanyOrgsCache.INSTANCE and various CacheMap singletons
   - What's unclear: Whether Phase 1 should test cache refreshing or just endpoint correctness
   - Recommendation: Test endpoint correctness only. Caches are implementation details. If controller returns correct data, cache worked correctly.

2. **How to handle jobInfoFilters parameter?**
   - What we know: getBranchJobsRuns and getRuns have jobInfoFilters parameter but controller has "//TODO: use jobInfoFilters"
   - What's unclear: Should tests pass filters or always pass null?
   - Recommendation: Pass null (existing behavior). Filter logic not implemented yet - out of scope for Phase 1 validation.

3. **Should tests verify specific JSON structure or just serializability?**
   - What we know: Phase requires "JSON serializes without error" and "response is non-empty"
   - What's unclear: Whether to validate JSON keys/structure or just that serialization succeeds
   - Recommendation: Basic validation only - verify body is not null, not empty. Spring Boot handles JSON serialization automatically via Jackson. If ResponseEntity.getBody() returns data, JSON serialization worked.

## Sources

### Primary (HIGH confidence)
- Codebase inspection: BrowseJsonController.java (10 endpoints identified)
- Codebase inspection: AbstractBrowseServiceTest.java (test base class pattern)
- Codebase inspection: BrowseServiceTest.java (existing test patterns for 13 service methods)
- Codebase inspection: MyEmbeddedMysql.java (Testcontainers MySQL 8.0.33 setup)
- Codebase inspection: test-data.dml.sql (pre-seeded test data)
- Codebase inspection: TestData.java (test data constants)
- Codebase inspection: BrowseService.java (error handling with ResponseStatusException)
- Codebase inspection: ResponseDetails.java (error response model - not used by BrowseJsonController)

### Secondary (MEDIUM confidence)
- Spring Boot 2.6.15 documentation (inferred from project dependencies)
- Testcontainers documentation (inferred from usage patterns)

### Tertiary (LOW confidence)
- None required for this phase

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All dependencies already in project, verified via code inspection
- Architecture: HIGH - Existing test patterns clearly established in BrowseServiceTest
- Pitfalls: HIGH - Identified from code inspection (no exception handlers, cache singletons, service vs controller layer)

**Research date:** 2025-02-05
**Valid until:** 2025-03-05 (30 days - stable mature stack)
