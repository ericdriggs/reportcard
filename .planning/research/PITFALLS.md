# Domain Pitfalls: Spring Boot REST JSON APIs

**Domain:** Adding JSON REST endpoints to existing Spring Boot application
**Project:** Reportcard (Java 17, Spring Boot 2.6.15, JOOQ, MySQL)
**Researched:** 2026-02-05

## Critical Pitfalls

Mistakes that cause rewrites, data corruption, or major production issues.

### Pitfall 1: Path Conflicts Between JSON and HTML Endpoints
**What goes wrong:** BrowseJsonController uses `/v1/api` while BrowseUIController uses root `/`. Both share similar path patterns like `company/{company}/org/{org}`. When @Hidden is removed, overlapping paths can cause Spring to route requests incorrectly, sending JSON requests to HTML handlers or vice versa.

**Why it happens:** Spring's RequestMapping resolution prioritizes specificity, but similar patterns at different base paths can create ambiguous mappings. The root path (`""`) in BrowseUIController can inadvertently match `/v1/api` requests if patterns overlap.

**Consequences:**
- Clients expecting JSON receive HTML (content negotiation failures)
- Runtime routing errors (AmbiguousMappingException) prevent startup
- Subtle bugs where wrong handler executes, returning wrong content type
- Breaking existing HTML UI when exposing JSON API

**Prevention:**
1. **Audit all RequestMapping paths** before removing @Hidden
2. Use MockMvc tests with explicit Accept headers (application/json vs text/html)
3. Add @RequestMapping `produces` attribute to ALL endpoints (mandatory, not optional)
4. Test path resolution conflicts: `./gradlew build` will catch AmbiguousMappingException
5. Document path hierarchy: `/v1/api/*` = JSON, `/*` = HTML, never mix

**Detection warning signs:**
- Build failures with "Ambiguous mapping" errors
- Integration tests fail with wrong Content-Type headers
- Curl tests return HTML when expecting JSON

**Phase mapping:** Phase 1 (Foundation) - Path audit and resolution strategy must happen before any endpoint exposure.

---

### Pitfall 2: JOOQ POJO Circular Reference Serialization Failures
**What goes wrong:** BrowseJsonController returns nested Maps containing JOOQ POJOs (CompanyPojo, OrgPojo, etc.). JOOQ generates bidirectional references between entities. Jackson's default serialization encounters circular references, causing StackOverflowError or infinite recursion during JSON serialization.

**Why it happens:**
- JOOQ-generated POJOs lack @JsonIgnore annotations
- Nested Map<ParentPojo, Map<ChildPojo, Set<GrandchildPojo>>> structures amplify circular reference risk
- Spring Boot's default ObjectMapper doesn't handle JOOQ's object graphs
- Example: CompanyPojo references OrgPojo, OrgPojo references CompanyPojo back

**Consequences:**
- Runtime StackOverflowError when serializing responses
- 500 Internal Server Error for all JSON endpoints
- Production outages if not caught in testing
- Memory exhaustion from infinite serialization loops

**Prevention:**
1. **Configure global ObjectMapper** with circular reference detection:
   ```java
   @Bean
   public ObjectMapper objectMapper() {
       return SharedObjectMappers.permissiveObjectMapper
           .copy()
           .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, true);
   }
   ```
2. **Test EVERY endpoint** with Testcontainers + real database data (not mocks)
3. **Use DTOs instead of POJOs** for complex nested structures
4. **Add integration tests** that serialize actual responses to JSON strings
5. Check existing SharedObjectMappers configuration compatibility

**Detection warning signs:**
- StackOverflowError in logs during JSON serialization
- Response times spike to timeout (infinite loop)
- HTTP 500 with no clear error message
- Local manual testing with curl returns errors

**Phase mapping:** Phase 1 (Foundation) - Serialization testing required before any endpoint exposure. Phase 2 (Testing) - Integration tests must catch this.

---

### Pitfall 3: Missing Null Handling in JOOQ-Generated Fields
**What goes wrong:** JOOQ POJOs use primitive wrappers (Long, Integer) which can be null. When serialized to JSON, null fields may be included or excluded inconsistently depending on ObjectMapper config. Clients expecting consistent schema receive different structures (field present vs absent), breaking client-side parsing.

**Why it happens:**
- SharedObjectMappers uses `.setSerializationInclusion(JsonInclude.Include.NON_NULL)`
- Some endpoints may use different ObjectMapper configurations
- JOOQ queries return null for missing foreign keys (e.g., runId without stages)
- Database NULL vs Java null vs JSON null semantics differ

**Consequences:**
- Client applications crash on NullPointerException
- API contract violations (breaking semantic versioning)
- Inconsistent JSON schemas across similar endpoints
- Downstream consumers need defensive null checks everywhere

**Prevention:**
1. **Standardize on ONE ObjectMapper** across all controllers (use SharedObjectMappers.permissiveObjectMapper)
2. **Test with NULL database values** explicitly (add test fixtures with missing foreign keys)
3. **Document null handling policy** in OpenAPI spec (@Schema(nullable = true))
4. Consider using Optional<> in response DTOs for clarity
5. Validate with json-schema-validator in integration tests

**Detection warning signs:**
- Intermittent client failures with "property not found"
- Different JSON structure between test and production
- Missing fields in some responses but not others

**Phase mapping:** Phase 2 (Testing) - Null value test coverage. Phase 3 (Documentation) - OpenAPI schema updates.

---

### Pitfall 4: Cache Inconsistency with Live JSON Responses
**What goes wrong:** BrowseJsonController uses `*CacheMap.INSTANCE.getValue()` pattern from hierarchical async cache. Cache layers (CompanyOrgsCache, BranchJobsRunsCacheMap, etc.) may serve stale data when new test results are uploaded. JSON API returns outdated run counts, missing jobs, or incorrect test statuses.

**Why it happens:**
- Cache invalidation strategy unclear in existing code
- HTML endpoints may have different cache behavior than JSON
- TODOs in code indicate incomplete cache implementation (line 66: "//TODO: use jobInfoFilters")
- Testcontainers tests use ephemeral databases, masking cache staleness

**Consequences:**
- Clients see stale data (missing recent test runs)
- Cache stampede when multiple endpoints invalidate simultaneously
- Race conditions between cache refresh and new data insertion
- Data inconsistency between JSON and HTML endpoints

**Prevention:**
1. **Audit AbstractAsyncCache** usage before exposing JSON endpoints
2. **Test cache invalidation** with sequential POST (upload) then GET (retrieve) operations
3. **Add explicit cache keys** for each endpoint (document in code)
4. Consider cache-control headers (max-age, must-revalidate)
5. Load test with concurrent uploads + reads to expose race conditions

**Detection warning signs:**
- GET returns data missing recently POSTed test results
- Inconsistent counts between HTML dashboard and JSON API
- Cache hit rates unusually high (100%) indicating no invalidation

**Phase mapping:** Phase 1 (Foundation) - Cache strategy documentation. Phase 2 (Testing) - Cache invalidation integration tests.

---

## Moderate Pitfalls

Mistakes that cause delays, technical debt, or require refactoring.

### Pitfall 5: Missing Query Parameter Validation
**What goes wrong:** BrowseUIController has `?runs=N` filter with validation (`validateRuns()`), but BrowseJsonController endpoints lack equivalent parameter validation. Clients can send negative runs, excessively large values (runs=999999), or invalid types, causing slow queries or OutOfMemoryError.

**Why it happens:**
- Copy-paste from HTML controller didn't include validation logic
- @RequestParam(required = false) defaults to null without bounds checking
- Database queries with unbounded LIMIT can OOM
- No input sanitization for numeric parameters

**Consequences:**
- Slow queries impacting database performance
- Memory exhaustion from loading millions of rows
- HTTP 400 errors without helpful messages
- API abuse potential (intentional or accidental DOS)

**Prevention:**
1. **Reuse validateRuns() method** from BrowseUIController in JSON controller
2. **Add @Min/@Max annotations** to @RequestParam parameters
3. **Standardize defaults** across HTML and JSON endpoints (both use runs=60)
4. Add integration tests with boundary values (runs=-1, runs=0, runs=10000)
5. Document limits in OpenAPI spec

**Detection warning signs:**
- Slow query logs with huge LIMIT clauses
- OutOfMemoryError during JSON serialization
- Client complaints about inconsistent behavior

**Phase mapping:** Phase 1 (Foundation) - Parameter validation before endpoint exposure.

---

### Pitfall 6: Incomplete @Operation and @Parameter OpenAPI Documentation
**What goes wrong:** GraphJsonController has @Operation annotations, but BrowseJsonController has none. When @Hidden is removed, Swagger UI generates incomplete documentation. Clients lack parameter descriptions, example values, and error response documentation.

**Why it happens:**
- @Hidden marker deferred documentation work
- HTML endpoints don't need OpenAPI annotations
- Copy-paste fatigue leads to missing annotations
- No documentation review gate in PR process

**Consequences:**
- Poor developer experience for API consumers
- Support burden from unclear API usage
- Integration delays as clients guess parameter formats
- Inconsistent documentation quality across API

**Prevention:**
1. **Add @Operation to every JSON endpoint** with summary and description
2. **Add @Parameter with descriptions** and example values
3. **Document response schemas** with @Schema annotations
4. Generate OpenAPI spec and review in CI/CD
5. Compare with GraphJsonController for consistency

**Detection warning signs:**
- Missing endpoints in generated swagger.json
- Empty parameter descriptions in Swagger UI
- Clients asking for API documentation via support tickets

**Phase mapping:** Phase 3 (Documentation) - OpenAPI annotation completeness.

---

### Pitfall 7: Inconsistent Error Response Format
**What goes wrong:** JunitController uses StagePathTestResultResponse with ResponseDetails (httpStatus, detail, stackTrace fields). BrowseJsonController returns raw ResponseEntity with no standardized error structure. Clients receive different error formats from different endpoints.

**Why it happens:**
- BrowseJsonController throws exceptions caught by MissingParameterExceptionHandler
- MissingParameterExceptionHandler returns simple String body, not structured JSON
- GraphJsonController lets Spring default exception handling apply
- No global @RestControllerAdvice for consistent error responses

**Consequences:**
- Client error handling requires multiple parsers
- Debugging harder without consistent stack trace format
- Breaking semantic versioning expectations
- API feels inconsistent and unprofessional

**Prevention:**
1. **Create global @RestControllerAdvice** for all /v1/api endpoints
2. **Standardize error response** using ResponseDetails model
3. **Return structured JSON** for all 4xx/5xx errors
4. Add integration tests validating error response structure
5. Update MissingParameterExceptionHandler to use ResponseDetails

**Detection warning signs:**
- Error responses inconsistent between endpoints
- String error messages instead of JSON objects
- Missing error details in client-side logs

**Phase mapping:** Phase 1 (Foundation) - Error response standardization before endpoint exposure.

---

### Pitfall 8: Missing /run/latest Path Resolution
**What goes wrong:** Requirements specify "/run/latest path resolution alongside /run/{runId}", but no implementation exists. Clients must query to find latest runId, then make second request. This increases latency and API call volume.

**Why it happens:**
- Feature deferred during initial development
- Unclear if "latest" means newest timestamp or highest runId
- Cache layer may complicate latest-run logic
- No clear specification of "latest" semantics

**Consequences:**
- Poor API ergonomics (2 calls instead of 1)
- Increased latency for common use case
- Cache inefficiency (querying all runs to find latest)
- Clients implement inconsistent "latest" logic locally

**Prevention:**
1. **Define "latest" semantics** early (max(run_id) vs max(created_date))
2. **Add database index** on relevant columns for performance
3. **Implement in BrowseService layer** (not controller) for reuse
4. Add integration tests comparing /run/latest vs /run/{maxRunId} consistency
5. Document behavior in OpenAPI spec (what if no runs exist?)

**Detection warning signs:**
- Client code with "get all runs then sort" anti-patterns
- Feature request tickets for "latest" shortcut
- Performance issues from clients polling for latest

**Phase mapping:** Phase 2 (Implementation) - /run/latest endpoint alongside existing endpoints.

---

### Pitfall 9: TODOs Indicate Incomplete Implementation
**What goes wrong:** BrowseJsonController has multiple TODOs (line 18: "add reports endpoint", line 66: "use jobInfoFilters"). These incomplete features are hidden by @Hidden. When exposed, clients discover missing functionality referenced in similar HTML endpoints.

**Why it happens:**
- @Hidden allowed deferring completion
- Feature parity with HTML endpoints not tracked
- No requirements review before removing @Hidden
- Technical debt accumulated during initial development

**Consequences:**
- Feature disparity between HTML and JSON APIs
- Client confusion (HTML has filters, JSON doesn't)
- Rushed implementation when clients request missing features
- API versioning complexity if added later

**Prevention:**
1. **Audit all TODOs** in BrowseJsonController before exposing
2. **Create tickets** for each TODO with priority assessment
3. **Document known limitations** in OpenAPI spec
4. Decide: implement missing features OR document as future work
5. Add tests for TODO features (initially @Disabled with ticket reference)

**Detection warning signs:**
- TODO comments in production code
- Feature requests immediately after API launch
- Discrepancies between HTML and JSON capabilities

**Phase mapping:** Phase 1 (Foundation) - TODO audit and resolution plan.

---

## Minor Pitfalls

Mistakes that cause annoyance but are fixable without major refactoring.

### Pitfall 10: Missing Content Negotiation Testing
**What goes wrong:** Controllers use `produces = "application/json"` but tests may not verify Content-Type headers. Clients sending Accept: text/html to JSON endpoints may receive unexpected responses or 406 Not Acceptable errors.

**Why it happens:**
- Integration tests use direct method calls (bypassing Spring MVC layer)
- MockMvc tests don't set Accept headers explicitly
- Manual testing uses browser (defaults to accepting HTML)

**Consequences:**
- Client integration issues after deployment
- 406 errors confuse developers
- Content negotiation edge cases untested

**Prevention:**
1. **Use MockMvc with explicit Accept headers** in all integration tests
2. Test multiple Accept header scenarios (*/*, application/json, text/html)
3. Verify Content-Type in response assertions
4. Add negative test cases for unsupported content types

**Detection warning signs:**
- No MockMvc tests in BrowseJsonController test suite
- Missing Content-Type assertions in existing tests

**Phase mapping:** Phase 2 (Testing) - Content negotiation test coverage.

---

### Pitfall 11: JOOQ POJOs as API Contract Leak Implementation Details
**What goes wrong:** Returning JOOQ-generated POJOs (CompanyPojo, OrgPojo) directly in JSON responses couples API contract to database schema. Schema changes (adding columns, renaming fields) break API clients even if semantics unchanged.

**Why it happens:**
- Expedient to return POJOs directly (no mapping layer)
- DTO creation seen as boilerplate
- JOOQ regeneration happens frequently (schema-first design)

**Consequences:**
- API breaking changes from innocent schema additions
- Exposing internal database structure to external clients
- Difficult API versioning (can't evolve independently)
- Security risk (accidentally exposing sensitive fields)

**Prevention:**
1. **Create API-specific DTOs** for external-facing endpoints
2. **Map POJOs to DTOs** in service layer (not controller)
3. Consider using MapStruct for maintainable mapping
4. Add tests verifying API contract stability despite schema changes
5. Use @JsonView to control field visibility if DTOs too heavy

**Detection warning signs:**
- JOOQ field names (created_date) appearing in JSON responses
- Database refactoring requires API version bump
- Clients seeing fields they shouldn't access

**Phase mapping:** Phase 2 (Implementation) - DTO layer for external APIs (optional but recommended).

---

### Pitfall 12: Testcontainers Tests Don't Cover Path Conflicts
**What goes wrong:** Existing JunitControllerTest uses @SpringBootTest and calls controller methods directly (not via HTTP). Path conflict issues only appear when Spring MVC dispatches real HTTP requests through DispatcherServlet.

**Why it happens:**
- Direct method calls bypass request mapping resolution
- Tests focus on business logic, not HTTP layer
- Testcontainers setup doesn't include MockMvc configuration

**Consequences:**
- Path conflicts undetected until deployment
- Runtime AmbiguousMappingException in production
- False confidence from passing test suite

**Prevention:**
1. **Add MockMvc-based integration tests** for JSON endpoints
2. Use @AutoConfigureMockMvc in @SpringBootTest
3. Test HTTP layer (perform GET/POST) not just method calls
4. Include path conflict scenarios (both HTML and JSON endpoints loaded)
5. Verify Spring context starts successfully with all controllers active

**Detection warning signs:**
- No tests using mockMvc.perform() in test suite
- Tests call junitController.doPostJunitXml() directly
- Missing @WebMvcTest tests for controller layer

**Phase mapping:** Phase 2 (Testing) - HTTP layer integration test coverage.

---

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation |
|-------------|---------------|------------|
| Phase 1: Remove @Hidden | Path conflict with HTML endpoints (Pitfall 1) | Path audit + MockMvc tests before removal |
| Phase 1: Endpoint exposure | Missing error handling standardization (Pitfall 7) | Global @RestControllerAdvice implementation |
| Phase 2: Add tests | Testcontainers don't test HTTP layer (Pitfall 12) | MockMvc-based integration tests |
| Phase 2: Add ?runs=N filter | Missing query validation (Pitfall 5) | Copy validateRuns() from BrowseUIController |
| Phase 2: /run/latest endpoint | Cache inconsistency (Pitfall 4) | Test cache invalidation scenarios |
| Phase 3: JOOQ serialization | Circular references (Pitfall 2) | Integration tests with real nested data |
| Phase 3: Null handling | Inconsistent null semantics (Pitfall 3) | Test fixtures with NULL foreign keys |
| Phase 4: OpenAPI documentation | Missing annotations (Pitfall 6) | Documentation review checklist |
| Phase 5: TODO resolution | Incomplete features (Pitfall 9) | TODO audit + feature parity analysis |

## Sources

**Project-specific research:**
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java`
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseUIController.java`
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphJsonController.java`
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/mappers/SharedObjectMappers.java`
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/JunitControllerTest.java`
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/config/MissingParameterExceptionHandler.java`

**Project documentation:**
- `CLAUDE.md` - Project architecture and critical constraints
- `README_AI.md` - AI collaboration patterns and troubleshooting

**Spring Boot REST API best practices:**
- Spring Boot 2.6.x documentation on RequestMapping resolution
- Jackson ObjectMapper configuration for circular references
- Content negotiation and produces/consumes attributes
- MockMvc testing patterns for REST controllers

**JOOQ-specific concerns:**
- JOOQ code generation and POJO structure
- Serialization challenges with bidirectional references
- DTO mapping patterns for JOOQ entities
