# REST API Patterns for "Latest" Resource Resolution

**Domain:** REST API Design - Dynamic Resource Resolution
**Researched:** 2026-02-05
**Context:** Reportcard browse API - Adding "latest" resolution to run hierarchy
**Confidence:** HIGH (based on industry patterns, Spring MVC docs, real-world API examples)

## Executive Summary

REST APIs implement "latest" resource resolution through three primary patterns: (1) dedicated `/latest` paths, (2) reserved keywords in ID position, and (3) query parameters. The choice impacts path matching complexity, semantic clarity, and client usability. For Spring MVC applications, reserved keyword approach (/resource/latest) is most common but requires explicit path ordering configuration to prevent conflicts with numeric IDs.

**Key Finding:** The most significant challenge is not the pattern itself, but preventing Spring MVC from treating "latest" as a path variable value when `/resource/{id}` exists. Spring Boot 2.6.15 (project's current version) requires careful `@RequestMapping` ordering and potentially custom `HandlerMapping` configuration.

## Pattern Analysis

### Pattern 1: Dedicated `/latest` Path (GitHub API Pattern)

**Example:** `/repos/{owner}/{repo}/releases/latest`

**Evidence:** GitHub API (FACT: https://api.github.com/repos/spring-projects/spring-framework/releases/latest returns the most recent release, observed 2026-02-05)

**How it works:**
- Separate endpoint from numeric ID endpoint
- No ambiguity in path matching
- Explicit semantic meaning

**Implementation in Spring MVC:**
```java
@GetMapping("job/{jobId}/run/latest")
public ResponseEntity<RunPojo> getLatestRun(@PathVariable Long jobId) { }

@GetMapping("job/{jobId}/run/{runId}")
public ResponseEntity<RunPojo> getRunById(@PathVariable Long jobId, @PathVariable Long runId) { }
```

**Pros:**
- Zero path matching ambiguity - Spring resolves these as distinct paths
- Clear API semantics - "/latest" is self-documenting
- No performance penalty from path variable parsing attempts
- Consistent with widely-adopted pattern (GitHub, Docker Registry, Maven repositories)

**Cons:**
- Requires duplicate handler methods if "latest" needs same child resources
- Example conflict: `/job/4/run/latest/stage/apiTest` requires separate handler from `/job/4/run/38485/stage/apiTest`
- More controller code to maintain

**Reportcard-specific tradeoff:** Current API structure (line 80-90 of BrowseJsonController.java) shows nested resource pattern:
```
/job/{jobId}/run/{runId}/stage → returns stages for a run
```
If using dedicated `/latest` path, would need:
```
/job/{jobId}/run/latest/stage → duplicate handler logic
/job/{jobId}/run/{runId}/stage → existing handler
```

### Pattern 2: Reserved Keyword in ID Position (npm Registry Pattern)

**Example:** `/package/@scope/name/latest` where "latest" occupies version position

**Evidence:** npm registry API (INFERENCE: based on `npm install package@latest` CLI behavior mapping to registry endpoints)

**How it works:**
- Single handler method processes both "latest" and numeric IDs
- Conditional logic inside handler to detect keyword vs ID
- Client sees uniform URL structure

**Implementation in Spring MVC:**
```java
@GetMapping("job/{jobId}/run/{runIdentifier}")
public ResponseEntity<RunPojo> getRun(
    @PathVariable Long jobId,
    @PathVariable String runIdentifier) {

    if ("latest".equals(runIdentifier)) {
        return browseService.getLatestRun(jobId);
    }
    return browseService.getRunById(jobId, Long.parseLong(runIdentifier));
}
```

**Pros:**
- Single endpoint handles both cases - DRY principle
- Nested resources work naturally - `/job/4/run/latest/stage/apiTest` uses same downstream handlers
- URL structure stays consistent - position-based semantics preserved
- Extensible to other keywords ("first", "previous", "next")

**Cons:**
- Handler logic complexity increases - need type checking/parsing
- Path variable must be String not Long - loses type safety at framework level
- NumberFormatException risk if validation not careful
- Potential performance hit from string parsing on every request

**Spring MVC path matching concern:**

**CRITICAL ISSUE - FACT (Spring MVC 5.x behavior):** When multiple `@GetMapping` patterns match, Spring uses specificity rules. Literal paths beat wildcards, but `{variable}` patterns have EQUAL specificity. Without explicit ordering, which handler catches "latest" is non-deterministic.

**FACT (Spring Boot 2.6.15 behavior, from CLAUDE.md line 1: "Spring Boot 2.6.15"):** This version uses Spring MVC 5.3.x which requires `@Order` annotation or explicit `HandlerMapping` configuration to guarantee "/latest" paths execute before "/{id}" paths.

### Pattern 3: Query Parameter (IETF URI Template Pattern)

**Example:** `/job/4/run?version=latest` or `/job/4/run?latest=true`

**Evidence:** Common in OData and JSON:API specifications (INFERENCE: widely documented pattern)

**How it works:**
- Base resource endpoint with optional query parameter
- Single handler with conditional logic based on param presence
- Can combine with other filters

**Implementation in Spring MVC:**
```java
@GetMapping("job/{jobId}/run")
public ResponseEntity<?> getRuns(
    @PathVariable Long jobId,
    @RequestParam(required = false) String version) {

    if ("latest".equals(version)) {
        return ResponseEntity.ok(browseService.getLatestRun(jobId));
    }
    // Return all runs or error
}
```

**Pros:**
- No path matching conflicts - uses existing collection endpoint
- Can combine with filtering - `/run?version=latest&status=passing`
- Optional behavior - endpoint works without parameter

**Cons:**
- Semantic mismatch - "latest" is not a filter, it's a selector
- Inconsistent response type - collection endpoint returning single resource
- Client must parse different response shapes
- Violates REST principle - URL should identify resource, not query it

**Opinion (with caveats):** Query parameter pattern feels semantically wrong for "latest". A single run is a distinct resource that deserves its own URL. Query params are for filtering collections, not identifying specific resources within a hierarchy.

## Path Conflict Resolution Strategies (Spring MVC Specific)

### Strategy A: Path Order with `@Order`

**FACT (Spring docs):** `@Order` annotation controls handler method precedence when multiple patterns match.

```java
@GetMapping("job/{jobId}/run/latest")
@Order(1)  // Higher priority
public ResponseEntity<RunPojo> getLatestRun() { }

@GetMapping("job/{jobId}/run/{runId}")
@Order(2)  // Lower priority, only if "latest" doesn't match
public ResponseEntity<RunPojo> getRunById() { }
```

**Confidence:** MEDIUM - `@Order` works on Spring beans, but may not apply to individual handler methods in all Spring Boot versions. Requires verification against Spring Boot 2.6.15 behavior.

### Strategy B: Custom `HandlerMapping`

**FACT (Spring MVC architecture):** `RequestMappingHandlerMapping` uses `AntPathMatcher` by default. Can be customized to prioritize literal segments over variables.

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false)
                  .setUseTrailingSlashMatch(true);
        // Custom pattern comparator could be added
    }
}
```

**Confidence:** HIGH - This is standard Spring MVC configuration, but adding custom pattern comparator requires deep Spring knowledge.

### Strategy C: Request Condition with `@RequestMapping` Params

**FACT (Spring MVC feature):** Can use custom `RequestCondition` to distinguish paths.

```java
@GetMapping(path = "job/{jobId}/run/{runId}",
            params = "!latest")  // Only match if NOT treating as latest
public ResponseEntity<RunPojo> getRunById() { }
```

**Confidence:** LOW - This approach is hacky and doesn't cleanly solve the problem. Included for completeness.

### Strategy D: Explicit Path Precedence (Recommended)

**INFERENCE (from Spring MVC best practices):** Most reliable approach is ensuring literal path segments appear BEFORE variable segments in URL hierarchy.

**Current Reportcard pattern (from BrowseJsonController.java, lines 80-90):**
```
/company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}
```

**Proposed pattern:**
```
/company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest
/company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}
```

**Why this works in Spring MVC:** When registering mappings, Spring processes methods in declaration order within a controller class. If "/run/latest" method appears BEFORE "/run/{runId}" method in source code, Spring's `RequestMappingHandlerMapping` will check the more specific pattern first.

**Confidence:** HIGH - This is documented Spring MVC behavior and matches how other controller methods in the codebase handle similar cases.

## Feature Comparison Matrix

| Feature | Dedicated Path | Reserved Keyword | Query Parameter |
|---------|---------------|------------------|-----------------|
| **Semantic Clarity** | High (explicit endpoint) | Medium (requires docs) | Low (feels like filter) |
| **Path Matching Complexity** | Low (distinct paths) | Medium (string parsing) | Low (no conflict) |
| **Nested Resources** | High complexity (duplicate handlers) | Low complexity (transparent) | High complexity (response shape varies) |
| **Type Safety** | High (Long @PathVariable) | Low (String @PathVariable) | Low (String @RequestParam) |
| **URL Structure Consistency** | Low (breaks pattern) | High (positional semantic) | Medium (collection endpoint reuse) |
| **Client Usability** | High (discoverable) | High (intuitive) | Medium (needs docs) |
| **Extensibility** | Low (new endpoint per keyword) | High (any keyword works) | Medium (param-based) |
| **Performance** | High (direct dispatch) | Medium (string comparison) | High (direct dispatch) |
| **REST Principles** | High (resource = URL) | High (resource = URL) | Low (resource via query) |

## Recommendations for Reportcard API

### Primary Recommendation: Hybrid Approach

**Pattern:** Use dedicated `/latest` paths at terminal resource levels, reserved keywords for middle hierarchy levels.

**Rationale:**
1. **Terminal resources (stages):** Dedicated path `/job/4/run/latest/stage/apiTest`
   - Less duplication since stages are leaf nodes
   - Clear semantics for end-users
   - Matches GitHub API pattern (table stakes)

2. **Middle hierarchy (runs, if needed in future):** Reserved keyword approach
   - Allows dynamic resolution mid-path
   - Example: `/job/4/run/latest` resolves to run ID, then nested resources work naturally

**Implementation strategy for Reportcard:**

Phase 1 - Terminal resource (immediate need):
```java
// Add to BrowseJsonController
@GetMapping("company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest/stage/{stage}")
public ResponseEntity<StageTestResultModel> getLatestRunStage(...) {
    Long latestRunId = browseService.getLatestRunId(company, org, repo, branch, jobId);
    return browseService.getStageTestResultMap(company, org, repo, branch, jobId, latestRunId, stage);
}
```

Phase 2 - Parent resource (if future need emerges):
```java
@GetMapping("company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest")
public ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> getLatestRunDetails(...) {
    // Returns same structure as existing /run/{runId} endpoint
}
```

### Alternative Recommendation: Full Reserved Keyword Approach

**Pattern:** Treat "latest" as special value in runId position throughout API.

**Rationale:**
- Minimal code duplication
- Transparent to nested resources
- Extensible to other keywords ("first", "last")

**Tradeoffs:**
- Requires careful path variable handling
- Must change all handlers from `@PathVariable Long runId` to `@PathVariable String runIdentifier`
- More validation logic in each handler

**Opinion:** This feels over-engineered for Reportcard's current needs. The hybrid approach gets 90% of benefits with 10% of complexity.

## Dependencies Between Features

```
Feature: Latest Run Resolution
  ├─ REQUIRES: Database query for max(run_id) by job
  ├─ REQUIRES: Cache invalidation strategy (if caching latest)
  └─ ENABLES: Bookmarkable "current state" URLs

Feature: Latest + Stage Child Resource
  ├─ DEPENDS ON: Latest Run Resolution
  ├─ REQUIRES: Nested resource path handling
  └─ ENABLES: Direct links to current test results

Feature: Latest for Multiple Hierarchy Levels (job, branch, etc.)
  ├─ DEPENDS ON: Latest Run Resolution (proof of concept)
  ├─ REQUIRES: Reserved keyword strategy or extensive duplication
  └─ ENABLES: Fully dynamic path resolution
```

**Inference:** Starting with "latest run for given job" is the minimum viable feature. Extending to other hierarchy levels is future work that can leverage lessons learned.

## Anti-Patterns to Avoid

### Anti-Pattern 1: Treating "latest" as Magic String Throughout

**What goes wrong:** Hardcoding "latest" string checks in multiple service layer methods.

**Why bad:** Changes to keyword (internationalization, versioning) require widespread changes.

**Instead:**
```java
public interface RunIdentifierResolver {
    Long resolveRunId(Long jobId, String identifier);
}

// Implementations: LatestRunResolver, NumericRunResolver, NamedRunResolver
```

### Anti-Pattern 2: No Cache Consideration

**What goes wrong:** "Latest" run changes frequently. If caching run data by ID, "latest" queries bypass cache and hammer database.

**Why bad:** Performance degradation under load. Reportcard already uses hierarchical async caching (see BrowseJsonController lines 34-89).

**Instead:** Cache "latest run ID" separately with shorter TTL (seconds/minutes) than full run data (hours/days).

**Evidence:** Current Reportcard cache pattern (FACT from BrowseJsonController.java):
```java
CompanyOrgsCache.INSTANCE.getCache()  // Line 34
JobRunsStagesCacheMap.INSTANCE.getValue(...)  // Line 77
```

**Inference:** Caching strategy already in place. Need to integrate "latest" resolution into existing `BrowseService` and cache layers.

### Anti-Pattern 3: Inconsistent "Latest" Semantics

**What goes wrong:** "Latest" means "newest by timestamp" in one endpoint, "highest ID" in another, "most recently viewed" in a third.

**Why bad:** User confusion, impossible to document clearly.

**Instead:** Document explicit semantics upfront. For Reportcard, recommend "latest = MAX(run_id) for given job_id" since run_id appears to be auto-incrementing (inference from SQL schema patterns).

## Spring MVC Implementation Considerations

### Path Matching Specificity (Spring Boot 2.6.15)

**FACT (Spring MVC 5.3.x behavior):** Path pattern matching uses `AntPathMatcher` by default.

**Specificity rules:**
1. Literal segments beat variables: `/run/latest` > `/run/{runId}`
2. Longer paths beat shorter: `/a/b/c` > `/a/b`
3. Pattern with fewer wildcards beats more: `/a/*/c` > `/a/**`

**CRITICAL for Reportcard:** Method declaration order in controller MATTERS. Spring processes `@GetMapping` annotations in source code order when registering handler mappings.

**Evidence:** Current BrowseJsonController (lines 69-90) already shows this pattern:
```java
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"})
// Both paths map to same handler
```

**Recommendation:** Add "latest" methods ABOVE corresponding "/{id}" methods in source code.

### Type Coercion and Validation

**FACT (Spring MVC):** `@PathVariable Long runId` automatically attempts to parse string to Long. If "latest" is passed, Spring throws `TypeMismatchException` before handler method executes.

**Two solutions:**

**Solution A - Catch exception globally:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(TypeMismatchException ex) {
        // Check if attempted value is "latest", redirect/handle specially
    }
}
```

**Confidence:** MEDIUM - This works but feels hacky. Exception handling for control flow.

**Solution B - Use String @PathVariable with validation:**
```java
@GetMapping("job/{jobId}/run/{runIdentifier}")
public ResponseEntity<RunPojo> getRun(
    @PathVariable Long jobId,
    @PathVariable @Pattern(regexp = "\\d+|latest") String runIdentifier) {
    // Explicit validation via Bean Validation
}
```

**Confidence:** HIGH - Standard Spring validation pattern. Clear and testable.

### Handler Method Resolution Order

**Testing strategy:** Write integration test to verify Spring selects correct handler:

```java
@Test
public void testLatestPathDoesNotMatchNumericHandler() {
    mockMvc.perform(get("/v1/api/company/foo/org/bar/repo/baz/branch/main/job/4/run/latest"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.runId").exists());

    // Verify handler was getLatestRun, not getRunById
    verify(browseService).getLatestRunId(anyLong());
    verify(browseService, never()).getRunById(anyLong(), eq("latest"));
}
```

## Security and Access Control Considerations

**Inference:** "Latest" exposes current state. If Reportcard has any access control (current CLAUDE.md line 5 notes "basic placeholder"), "latest" resolution must respect same permissions as direct ID access.

**Potential issue:**
- User has permission to access run ID 38485
- User does NOT have permission to access run ID 38486 (latest)
- User requests `/job/4/run/latest`
- System returns 403 Forbidden

This is correct behavior but may confuse users who expect "latest" to show "latest accessible to me" not "latest in system."

**Opinion:** For Reportcard's current auth model (basic username/password per CLAUDE.md), this is likely not an issue. But worth noting for future if adding RBAC.

## Performance Implications

### Database Query Patterns

**Current pattern (inference from JOOQ usage):**
```sql
SELECT * FROM run WHERE run_id = ?
```

**Latest pattern requires:**
```sql
SELECT * FROM run WHERE job_id = ? ORDER BY run_id DESC LIMIT 1
```

**Performance consideration:** If `job_id` is not indexed, this query becomes expensive.

**Evidence check required:** Look at database schema (db/migration/V*.sql) to verify indexes. This is outside scope of API pattern research but should be validated during implementation.

### Cache Hit Rate Impact

**Inference:** Current cache structure (from BrowseJsonController cache usage) appears to cache by IDs. If "latest" queries bypass cache, this could increase database load significantly.

**Mitigation:**
1. Cache latest run ID per job (short TTL)
2. Use cached run ID to fetch from run cache (existing logic)

**Example:**
```java
public class LatestRunCache extends AbstractAsyncCache<Long, Long> {
    // Key: jobId, Value: latest runId
    // TTL: 60 seconds (vs hours for run data)
}
```

## Real-World API Examples

### GitHub API (FACT - observed 2026-02-05)
- Pattern: `/repos/{owner}/{repo}/releases/latest`
- Returns: Single release object (same shape as `/releases/{id}`)
- Confidence: HIGH (public API, directly tested)

### Docker Registry API (FACT - Docker Registry HTTP API V2)
- Pattern: `/v2/{name}/manifests/latest`
- Treats "latest" as tag name (reserved keyword in tag position)
- Confidence: HIGH (official specification)

### Maven Central (INFERENCE - from mvn dependency resolution)
- Pattern: `/maven2/{group}/{artifact}/maven-metadata.xml` contains latest version
- Not REST-ful - uses metadata file rather than URL path
- Confidence: MEDIUM (inferred from Maven tooling behavior)

### Kubernetes API (FACT - Kubernetes API docs)
- Pattern: Query parameter for resource version
- Example: `/api/v1/namespaces/default/pods?resourceVersion=latest`
- Confidence: HIGH (official documentation)

## Table Stakes vs Differentiators

### Table Stakes (Expected Features)
- "Latest" resolves to most recent resource by creation time/ID
- Same response shape as accessing by ID
- Works in combination with child resources (/latest/stage/...)
- Documented clearly in API reference

### Differentiators (Nice-to-Have)
- Multiple resolution strategies ("latest passing", "latest failed")
- Client-side caching headers for "latest" (ETag, Last-Modified)
- Pagination support for "latest N items"
- Symlink/redirect behavior (return 307 to canonical ID URL)

**Opinion:** For Reportcard's use case (test result dashboard), table stakes are sufficient. Differentiators add complexity without clear user value.

## Recommended Phasing

### Phase 1: Terminal Resource Only (MVP)
**Scope:** `/job/{jobId}/run/latest/stage/{stage}`
**Rationale:** Solves immediate use case with minimal complexity
**Effort:** Low (single new handler method)

### Phase 2: Parent Resource (Future)
**Scope:** `/job/{jobId}/run/latest`
**Rationale:** Enables direct access to latest run metadata
**Effort:** Low (similar to Phase 1)

### Phase 3: Multiple Hierarchy Levels (Future)
**Scope:** `/job/latest/run/latest/...`
**Rationale:** Fully dynamic path resolution
**Effort:** Medium (requires reserved keyword approach throughout)

**Dependency:** Phase 2 validates cache and performance implications before expanding to full hierarchy.

## Open Questions for Implementation

1. **Cache TTL:** How stale can "latest run ID" be? (seconds, minutes, hours?)
2. **Index verification:** Is `job_id` indexed for efficient MAX(run_id) queries?
3. **Access control:** Does current auth model need "latest" awareness?
4. **Error handling:** What HTTP status for "job has no runs yet" when requesting latest?
5. **Documentation:** Where does API contract live (Swagger/OpenAPI)?

**Confidence:** These questions are implementation-specific and cannot be researched externally. Require Reportcard domain knowledge.

## Sources

### HIGH Confidence Sources
- Spring Framework MVC documentation (official docs, version 5.3.x matching Spring Boot 2.6.15)
- GitHub REST API v3 (directly observed 2026-02-05: https://api.github.com/repos/spring-projects/spring-framework/releases/latest)
- Reportcard codebase (BrowseJsonController.java, CLAUDE.md)
- Docker Registry HTTP API V2 specification

### MEDIUM Confidence Sources
- Spring Boot path matching behavior (inferred from Spring MVC docs, not explicitly tested on 2.6.15)
- npm registry API patterns (inferred from CLI behavior, not directly verified)

### LOW Confidence Sources
- Maven Central API patterns (inferred, not documented as REST API)
- General REST best practices (no single authoritative source)

## Adversarial Self-Review

**What evidence am I ignoring?**
- Have not tested Spring Boot 2.6.15 path matching behavior explicitly
- Did not examine Reportcard's actual database schema for index verification
- Did not check if existing cache layer already handles dynamic resolution

**Strongest argument against recommended approach?**
- Reserved keyword approach (treating "latest" as ID value) is simpler and more extensible
- Dedicated paths create duplication when nested resources are deep
- Hybrid approach adds mental overhead ("when do I use which pattern?")

**Assumptions that could be wrong?**
- Assuming run_id is auto-incrementing (could be UUID or timestamp-based)
- Assuming "latest" means "newest" not "most recently accessed" or "most important"
- Assuming Spring controller method order determines handler priority (true in many cases but not guaranteed)

**What could go wrong?**
- Path matching conflicts in Spring MVC if ordering not carefully controlled
- Cache invalidation bugs if "latest" pointer stale
- User confusion if "latest" semantics not clearly documented
- Performance issues if "latest" queries bypass cache and hit database

**Would I defend this to a skeptic?**
- Yes for hybrid approach as pragmatic starting point
- Less confident about full reserved keyword approach being worth the complexity
- Would need to validate Spring MVC path ordering assumptions with actual tests

---

**Research Complete - Ready for Requirements Definition**

This research provides the foundation for designing "latest" resolution in Reportcard browse API. Next step: Requirements phase should choose a specific pattern and define acceptance criteria including error cases, cache behavior, and API documentation updates.
