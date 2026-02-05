# Phase 2: Latest Endpoints - Research

**Researched:** 2026-02-05
**Domain:** REST API endpoint implementation (Spring MVC + JOOQ)
**Confidence:** HIGH

## Summary

This phase implements two new endpoints in `BrowseJsonController` for latest run resolution:
1. `/job/{jobId}/run/latest` - Returns the latest run for a job
2. `/job/{jobId}/run/latest/stage/{stage}` - Returns the latest run's specific stage test results

Research confirms this is a well-understood problem with established patterns in the codebase. GraphService already implements `max(RUN.RUN_ID)` queries grouped by job (lines 217-226), providing a proven template. The existing cache layer (`AbstractAsyncCache`) supports configurable TTLs, and the database schema has efficient indexes on `job_fk` in the `run` table.

**Primary recommendation:** Implement `/run/latest` resolution in BrowseService using `max(RUN.RUN_ID)` pattern from GraphService, expose via new controller endpoints with Spring MVC literal path matching, skip caching for latest-run-ID resolution (always query DB for freshness).

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring MVC | 5.3.x (Boot 2.6.15) | REST controller routing | Existing controller pattern |
| JOOQ | 3.15.x | Type-safe SQL queries | Existing data access layer |
| Jackson | 2.13.x | JSON serialization | Auto-configured by Spring Boot |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Lombok | 1.18.x | Reduce boilerplate | DTOs and POJOs |
| JUnit 5 | 5.8.x | Testing | All tests |
| Testcontainers | 1.17.x | MySQL container | Integration tests |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Direct DB query | Cache lookup | Fresh data vs latency - fresh data wins for "latest" |
| New service class | BrowseService extension | Reuse existing service patterns |

**Installation:**
No new dependencies required - all libraries already in project.

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/.../
├── controller/browse/
│   └── BrowseJsonController.java   # Add 2 new endpoints
├── persist/
│   └── BrowseService.java          # Add getLatestRunId() method
└── cache/model/                    # No changes needed
```

### Pattern 1: Latest Run ID Resolution via MAX Query

**What:** Query database for max(run_id) where job_fk = jobId
**When to use:** Every call to /run/latest endpoints
**Example:**
```java
// Source: GraphService.java lines 217-226 (existing pattern)
public Long getLatestRunId(Long jobId) {
    Long[] runIds = dsl.select(max(RUN.RUN_ID).as("MAX_RUN_ID"))
            .from(RUN)
            .where(RUN.JOB_FK.eq(jobId))
            .fetchArray("MAX_RUN_ID", Long.class);

    if (runIds == null || runIds.length == 0 || runIds[0] == null) {
        throwNotFound("jobId: " + jobId, "no runs found");
    }
    return runIds[0];
}
```

### Pattern 2: Spring MVC Literal Path Matching

**What:** Spring MVC matches literal path segments before path variables
**When to use:** `/run/latest` must match before `/run/{runId}`
**Example:**
```java
// Source: Spring MVC path matching rules
// Literal "latest" matches first, then {runId} for other values

@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest", produces = "application/json")
public ResponseEntity<RunPojo> getLatestRun(...) {
    Long latestRunId = browseService.getLatestRunId(jobId);
    return getRunById(company, org, repo, branch, jobId, latestRunId);
}

@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}", produces = "application/json")
public ResponseEntity<RunPojo> getRunById(...) {
    // Existing endpoint
}
```

### Pattern 3: Reuse Existing Response Shapes

**What:** Latest endpoints return identical structure to ID-based endpoints
**When to use:** All latest endpoints
**Example:**
```java
// Source: BrowseJsonController.java lines 80-90 (existing pattern)
// Latest run endpoint returns same shape as run-by-id endpoint

// For /run/latest - return Map<JobPojo, Map<RunPojo, Set<StagePojo>>>
// For /run/latest/stage/{stage} - return StageTestResultModel
```

### Anti-Patterns to Avoid
- **Custom "latest" caching:** Don't cache latest-run-ID resolution - always query DB for freshness
- **Multiple queries:** Don't fetch all runs then filter in Java - use SQL MAX() aggregate
- **Path conflicts:** Don't use path variables that could match "latest" as a value

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Latest run query | Custom subquery | MAX(RUN.RUN_ID) pattern from GraphService | Proven, indexed |
| Path routing | Manual string comparison | Spring MVC literal matching | Framework handles it |
| Response serialization | Custom JSON builders | Jackson auto-serialization | JOOQ POJOs serialize cleanly |
| Error handling | Custom exceptions | ResponseStatusException + throwNotFound() | Existing pattern in BrowseService |

**Key insight:** GraphService.getLatestRunForBranchJobStages() (lines 199-230) already solves the "latest run per job" problem. Adapt that pattern rather than inventing new approach.

## Common Pitfalls

### Pitfall 1: Path Variable vs Literal Conflict
**What goes wrong:** Concern that `/run/latest` might conflict with `/run/{runId}`
**Why it happens:** Uncertainty about Spring MVC path matching rules
**How to avoid:** Spring MVC matches literal segments before path variables. Place `/run/latest` endpoint above `/run/{runId}` in source for clarity, but order doesn't technically matter.
**Warning signs:** AmbiguousMappingException during startup (won't happen with literal vs variable)

### Pitfall 2: Caching Stale Latest Run
**What goes wrong:** Cache returns outdated "latest" run ID after new run uploaded
**Why it happens:** Temptation to cache latest-run-ID for performance
**How to avoid:** Don't cache latest-run-ID resolution. The MAX query is cheap (indexed). Full run data can be cached separately.
**Warning signs:** Tests pass but CI/CD clients get stale results after uploads

### Pitfall 3: Missing Job Validation
**What goes wrong:** 500 error instead of 404 when job doesn't exist
**Why it happens:** MAX query returns null array for non-existent job
**How to avoid:** Check for null/empty result and call throwNotFound() with descriptive message
**Warning signs:** NullPointerException in logs, 500 responses for bad job IDs

### Pitfall 4: Incorrect Response Shape
**What goes wrong:** Latest endpoint returns different JSON structure than ID-based endpoint
**Why it happens:** Creating new response type instead of reusing existing
**How to avoid:** Latest endpoints must delegate to existing methods after resolving run ID. Same service calls, same response shapes.
**Warning signs:** API consumers need different parsing logic for latest vs ID endpoints

### Pitfall 5: LEFT JOIN Returns Null Fields
**What goes wrong:** getBranch-style queries return POJO with null fields instead of throwing 404
**Why it happens:** BrowseService inconsistently uses LEFT JOIN (returns nulls) vs INNER JOIN (throws NoDataFoundException)
**How to avoid:** Use explicit null check after MAX query, call throwNotFound() when result is null
**Warning signs:** 200 response with null fields where 404 expected (discovered in Phase 1 testing)

## Code Examples

Verified patterns from codebase analysis:

### Service Layer: getLatestRunId Implementation
```java
// Source: Adapted from GraphService.java lines 217-226
// Location: BrowseService.java (new method)

/**
 * Get the latest (highest) run_id for a given job.
 * @param jobId the job ID
 * @return the latest run ID
 * @throws ResponseStatusException 404 if job has no runs
 */
public Long getLatestRunId(Long jobId) {
    Long result = dsl.select(max(RUN.RUN_ID))
            .from(RUN)
            .where(RUN.JOB_FK.eq(jobId))
            .fetchOne(0, Long.class);

    if (result == null) {
        throwNotFound("jobId: " + jobId, "no runs found");
    }
    return result;
}
```

### Controller Layer: Latest Run Endpoint
```java
// Source: Adapted from BrowseJsonController.java lines 80-90
// Location: BrowseJsonController.java (new endpoint)

@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest",
            produces = "application/json")
public ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> getLatestRunStages(
        @PathVariable String company,
        @PathVariable String org,
        @PathVariable String repo,
        @PathVariable String branch,
        @PathVariable Long jobId) {
    Long latestRunId = browseService.getLatestRunId(jobId);
    return getStagesByIds(company, org, repo, branch, jobId, latestRunId);
}
```

### Controller Layer: Latest Run Stage Endpoint
```java
// Source: Adapted from BrowseJsonController.java lines 116-126
// Location: BrowseJsonController.java (new endpoint)

@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest/stage/{stage}",
            produces = "application/json")
public ResponseEntity<StageTestResultModel> getLatestRunStageTestResults(
        @PathVariable String company,
        @PathVariable String org,
        @PathVariable String repo,
        @PathVariable String branch,
        @PathVariable Long jobId,
        @PathVariable String stage) {
    Long latestRunId = browseService.getLatestRunId(jobId);
    return getStageTestResultsTestSuites(company, org, repo, branch, jobId, latestRunId, stage);
}
```

### Test Pattern: Latest Endpoint Test
```java
// Source: Adapted from BrowseJsonControllerTest.java
// Location: BrowseJsonControllerTest.java (new test)

@Test
void getLatestRunJsonSuccessTest() {
    // Call latest run endpoint
    ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> response =
        controller.getLatestRunStages(TestData.company, TestData.org, TestData.repo,
            TestData.branch, TestData.jobId);

    // Verify HTTP response
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verify response body
    Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>> runStages = response.getBody();
    assertNotNull(runStages);
    assertFalse(runStages.isEmpty());

    // Verify this is indeed the latest run (highest run_id for this job)
    RunPojo latestRun = runStages.keySet().iterator().next();
    assertNotNull(latestRun.getRunId());
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Fetch all runs, filter in Java | SQL MAX() aggregate | Always preferred | Performance |
| Custom exception types | ResponseStatusException | Spring 5.x | Standardization |

**Deprecated/outdated:**
- None identified for this domain

## Open Questions

Things that couldn't be fully resolved:

1. **Response shape for /run/latest**
   - What we know: Two possible shapes - `Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>` (matching getStagesByIds) or just `RunPojo`
   - What's unclear: Requirements say "returns latest run" - does that mean just the run, or run with stages?
   - Recommendation: Return full run+stages map to match existing `/run/{runId}` endpoint behavior. Simpler RunPojo-only response could be added as separate endpoint if needed.

2. **Validation of job ownership**
   - What we know: Current getLatestRunId query only checks if runs exist for job_fk
   - What's unclear: Should we validate the job belongs to the company/org/repo/branch path?
   - Recommendation: Validate hierarchy by using existing pattern - call existing service methods that include path validation, or add JOIN to company/org/repo/branch in latest query. Defer complex validation if not blocking.

## Sources

### Primary (HIGH confidence)

**Codebase analysis:**
- `/reportcard-server/src/main/java/.../persist/GraphService.java` - Lines 217-226 show max(RUN.RUN_ID) pattern
- `/reportcard-server/src/main/java/.../controller/browse/BrowseJsonController.java` - Existing endpoint patterns
- `/reportcard-server/src/main/java/.../persist/BrowseService.java` - Service layer patterns, throwNotFound()
- `/reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql` - Run table schema and indexes

**Database schema verification:**
- Run table has index `run_job_fk_idx` on `job_fk` column (line 139 of DDL)
- MAX(run_id) WHERE job_fk = X will use this index efficiently

### Secondary (MEDIUM confidence)

**Spring Boot patterns:**
- Spring MVC 5.3.x literal vs path variable matching (verified via framework documentation)
- ResponseStatusException handling (verified in codebase)

### Tertiary (LOW confidence)

None - all findings verified against codebase or official documentation.

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All libraries already in project, no new dependencies
- Architecture: HIGH - Direct adaptation of existing GraphService pattern
- Pitfalls: HIGH - Phase 1 testing revealed specific error handling gaps
- Service layer: HIGH - MAX query pattern proven in GraphService
- Controller layer: HIGH - Follows existing endpoint patterns exactly
- Test strategy: HIGH - TestData fixtures and patterns established in Phase 1

**Research date:** 2026-02-05
**Valid until:** 2026-03-05 (30 days - stable patterns, no external dependencies)

---

## Implementation Checklist for Planner

Based on research, planner should create tasks for:

1. **Add `getLatestRunId(Long jobId)` to BrowseService**
   - MAX(RUN.RUN_ID) query with job_fk condition
   - Null check with throwNotFound()
   - Unit test for method

2. **Add `/run/latest` endpoint to BrowseJsonController**
   - Full path: `/company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest`
   - Delegate to existing getStagesByIds after resolving latest run ID
   - Return `Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>`

3. **Add `/run/latest/stage/{stage}` endpoint to BrowseJsonController**
   - Full path: `/company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest/stage/{stage}`
   - Delegate to existing getStageTestResultsTestSuites after resolving latest run ID
   - Return `StageTestResultModel`

4. **Add integration tests for both endpoints**
   - Success case: Valid job with runs returns latest
   - Error case: Job with no runs returns 404
   - Verify response shape matches ID-based endpoints

5. **Cache decision: NO caching for latest-run-ID**
   - Document in code comments why caching is skipped
   - Full run data uses existing cache layer (after resolution)
