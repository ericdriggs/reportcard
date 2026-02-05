# Phase 3: Query Parameter Parity - Research

**Researched:** 2026-02-05
**Domain:** Spring Boot REST API query parameter handling
**Confidence:** HIGH

## Summary

This phase adds the `?runs=N` query parameter to BrowseJsonController endpoints to achieve feature parity with BrowseUIController. The research reveals a straightforward implementation path: the HTML controller already validates this parameter via `validateRuns(int runs)` (returns 60 if runs < 1), and BrowseService already has methods that accept the `runs` parameter (`getStageViewForBranch` and `getStageViewForJob`). The work is limited to adding the parameter to specific JSON endpoints and applying the same validation logic.

The existing BrowseJsonController uses a different data source pattern than BrowseUIController for the same endpoints. The HTML controller calls `browseService.getStageViewForBranch()` / `getStageViewForJob()` which return `BranchStageViewResponse`, while the JSON controller uses cache-backed methods (`BranchJobsRunsCacheMap.INSTANCE.getValue()` / `JobRunsStagesCacheMap.INSTANCE.getValue()`) which return nested `Map<Pojo, Map<Pojo, Set<Pojo>>>` structures. This means the JSON controller cannot simply call the same service methods - it must either adapt the response format or keep the cache-backed approach without run limiting.

**Primary recommendation:** Add `?runs=N` parameter to `getBranchJobsRuns` and `getJobRunsStages` endpoints in BrowseJsonController. The parameter should use the same validation as BrowseUIController (`validateRuns()`). Implementation requires deciding whether to: (a) switch to BranchStageViewResponse-returning methods, (b) add runs-limiting capability to the cache methods, or (c) post-filter cached results. Option (a) changes the response shape, option (c) is most pragmatic for maintaining backward compatibility.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring MVC | 5.3.x (Boot 2.6.15) | @RequestParam handling | Already in use, well-documented |
| Spring Boot | 2.6.15 | Parameter binding | Auto-configured |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| JUnit 5 | 5.8.x | Parameter validation tests | Test boundary values |
| Testcontainers | 1.17.x | Integration tests | Verify with real data |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Copy validateRuns() | Shared utility class | DRY vs simplicity - copy is acceptable for single method |
| Post-filter cached results | New service methods | Maintain response shape vs. code complexity |

**Installation:**
No new dependencies required - all libraries already in project.

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/.../
├── controller/browse/
│   └── BrowseJsonController.java   # Add @RequestParam runs to 2 endpoints
│   └── BrowseUIController.java     # Reference for validateRuns() pattern
└── persist/
    └── BrowseService.java          # Already has runs-aware methods
```

### Pattern 1: Spring MVC @RequestParam with Default Value
**What:** Optional query parameter with sensible default
**When to use:** All endpoints supporting `?runs=N`
**Example:**
```java
// Source: BrowseUIController.java lines 65, 81
@GetMapping(path = "...", produces = "application/json")
public ResponseEntity<?> getEndpoint(
        @PathVariable String company,
        // ... other path variables
        @RequestParam(required = false, defaultValue = "60") Integer runs,
        @RequestParam(required = false) Map<String, String> otherFilters) {
    runs = validateRuns(runs);
    // ... use runs parameter
}
```

### Pattern 2: Validation Method for runs Parameter
**What:** Ensure runs value is sensible (minimum 1, fallback to default)
**When to use:** Before passing runs to service/data layer
**Example:**
```java
// Source: BrowseUIController.java lines 175-180
Integer validateRuns(int runs) {
    if (runs < 1) {
        return 60;
    }
    return runs;
}
```

### Pattern 3: Post-Filter Cached Results for Run Limiting
**What:** Retrieve full cached results, then limit to N most recent runs
**When to use:** When maintaining existing response shape is priority
**Example:**
```java
// Pattern for limiting runs from cache response
public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getBranchJobsRuns(
        @PathVariable String company, @PathVariable String org,
        @PathVariable String repo, @PathVariable String branch,
        @RequestParam(required = false, defaultValue = "60") Integer runs,
        @RequestParam(required = false) Map<String, String> jobInfoFilters) {
    runs = validateRuns(runs);

    // Get full cached result
    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult =
        BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchDTO(company, org, repo, branch));

    // Limit runs per job to N most recent (highest run_id)
    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> limitedResult = limitRunsPerJob(fullResult, runs);

    return new ResponseEntity<>(limitedResult, HttpStatus.OK);
}

private Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> limitRunsPerJob(
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> input, int maxRuns) {
    // For each job, take only the N most recent runs (by run_id descending)
    // RunPojo implements comparable or use PojoComparators.RUN_DESCENDING
    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> result = new TreeMap<>(/* same comparator */);
    for (Map.Entry<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchEntry : input.entrySet()) {
        Map<JobPojo, Set<RunPojo>> limitedJobs = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);
        for (Map.Entry<JobPojo, Set<RunPojo>> jobEntry : branchEntry.getValue().entrySet()) {
            Set<RunPojo> runs = jobEntry.getValue();
            Set<RunPojo> limitedRuns = runs.stream()
                .sorted(Comparator.comparing(RunPojo::getRunId).reversed())
                .limit(maxRuns)
                .collect(Collectors.toCollection(() ->
                    new TreeSet<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER)));
            limitedJobs.put(jobEntry.getKey(), limitedRuns);
        }
        result.put(branchEntry.getKey(), limitedJobs);
    }
    return result;
}
```

### Anti-Patterns to Avoid
- **Different validation logic:** Don't create a new validation method - use/copy the existing `validateRuns()` pattern
- **Changing response shape:** Don't switch JSON endpoints to return `BranchStageViewResponse` - that breaks API contract
- **Ignoring the parameter:** Don't add parameter but not use it - tests will catch this
- **Negative/zero handling:** Don't allow runs < 1 to reach service layer - validate first

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Parameter validation | Custom validation logic | Copy `validateRuns()` from BrowseUIController | Proven, consistent with HTML behavior |
| Run ordering | Custom comparators | `PojoComparators.RUN_DESCENDING` / `RUN_CASE_INSENSITIVE_ORDER` | Existing comparator handles ordering |
| Default value | Manual null checking | `@RequestParam(defaultValue = "60")` | Spring handles null-to-default |
| Run limiting | Complex JOOQ query | Post-filter in Java | Cache already has data, avoid bypass |

**Key insight:** The `?runs=N` parameter limits how many runs are returned per job. The HTML controller uses service methods with SQL LIMIT clauses. The JSON controller can achieve the same result via post-filtering cached data, maintaining backward compatibility with existing response shapes.

## Common Pitfalls

### Pitfall 1: Inconsistent Validation Between Controllers
**What goes wrong:** JSON controller validates runs differently than HTML controller
**Why it happens:** Copy-pasting validation logic with modifications
**How to avoid:** Use identical validation logic - copy `validateRuns()` exactly or extract to shared utility
**Warning signs:** Different behavior for `?runs=0` or `?runs=-5` between HTML and JSON endpoints

### Pitfall 2: Changing Response Shape When Adding Parameter
**What goes wrong:** JSON endpoint returns different structure after adding runs parameter
**Why it happens:** Temptation to call service methods that return different types (BranchStageViewResponse vs Map structures)
**How to avoid:** Keep existing response shape, implement run limiting via post-filtering
**Warning signs:** API clients break after update, tests expecting Map structure fail

### Pitfall 3: Applying runs Limit to Wrong Scope
**What goes wrong:** Limit runs globally instead of per-job
**Why it happens:** Misunderstanding the semantics - `?runs=60` means 60 runs per job, not 60 runs total
**How to avoid:** Apply limit within each job's run set, not to total results
**Warning signs:** Jobs with many runs dominate results, jobs with few runs missing entirely

### Pitfall 4: Forgetting to Validate Before Use
**What goes wrong:** Negative runs value causes unexpected behavior or errors
**Why it happens:** Calling service/filter method before validating
**How to avoid:** Always call `validateRuns(runs)` before using the value
**Warning signs:** Empty results or errors when `?runs=-1` passed

### Pitfall 5: Cache Bypass Performance Impact
**What goes wrong:** Adding runs parameter bypasses cache, hitting database every request
**Why it happens:** Calling service methods directly instead of using cache
**How to avoid:** Use cache + post-filter pattern, don't bypass cache for this feature
**Warning signs:** Increased database load, slower response times

### Pitfall 6: Not Testing Parameter Edge Cases
**What goes wrong:** Boundary values (0, -1, null, very large) not tested
**Why it happens:** Only testing happy path with valid values
**How to avoid:** Test: runs=0 (should use default), runs=-1 (should use default), runs=1000000 (should work), runs omitted (should use default)
**Warning signs:** Production errors on edge case inputs

## Code Examples

Verified patterns from codebase analysis:

### Current HTML Controller Pattern (Reference)
```java
// Source: BrowseUIController.java lines 58-71
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
        "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "text/html;charset=UTF-8")
public ResponseEntity<String> getBranchJobRuns(
        @PathVariable String company,
        @PathVariable String org,
        @PathVariable String repo,
        @PathVariable String branch,
        @RequestParam(required = false, defaultValue = "60") Integer runs,
        @RequestParam(required = false) Map<String, String> jobInfoFilters) {
    runs = validateRuns(runs);
    BranchStageViewResponse branchStageViewResponse = browseService.getStageViewForBranch(company, org, repo, branch, runs);
    // ... HTML generation
}

// Source: BrowseUIController.java lines 175-180
Integer validateRuns(int runs) {
    if (runs < 1) {
        return 60;
    }
    return runs;
}
```

### Current JSON Controller Pattern (No runs parameter)
```java
// Source: BrowseJsonController.java lines 57-67
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
        "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getBranchJobsRuns(
        @PathVariable String company,
        @PathVariable String org,
        @PathVariable String repo,
        @PathVariable String branch,
        @RequestParam(required = false) Map<String, String> jobInfoFilters) {
    return new ResponseEntity<>(BranchJobsRunsCacheMap.INSTANCE.getValue(
        new CompanyOrgRepoBranchDTO(company, org, repo, branch)), HttpStatus.OK);
    //TODO: use jobInfoFilters
}
```

### Target JSON Controller Pattern (With runs parameter)
```java
// Target: BrowseJsonController.java - updated getBranchJobsRuns
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
        "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getBranchJobsRuns(
        @PathVariable String company,
        @PathVariable String org,
        @PathVariable String repo,
        @PathVariable String branch,
        @RequestParam(required = false, defaultValue = "60") Integer runs,
        @RequestParam(required = false) Map<String, String> jobInfoFilters) {
    runs = validateRuns(runs);

    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult =
        BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchDTO(company, org, repo, branch));

    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> limitedResult = limitRunsPerJob(fullResult, runs);

    return new ResponseEntity<>(limitedResult, HttpStatus.OK);
    //TODO: use jobInfoFilters
}
```

### Endpoints Requiring runs Parameter

Based on BrowseUIController parity, these endpoints need the parameter:

| Endpoint | Current Parameter | HTML Equivalent |
|----------|-------------------|-----------------|
| `getBranchJobsRuns` | None | BrowseUIController.getBranchJobRuns (runs=60 default) |
| `getJobRunsStages` | None | BrowseUIController.getRunStagesFromJobId (runs=60 default) |

Note: Only branch-level and job-level endpoints have `?runs=N` in the HTML controller. Run-level and stage-level endpoints do not use this parameter.

### Test Pattern
```java
// Pattern for testing runs parameter
@Test
void getBranchJobsRunsWithRunsParameterTest() {
    // Test with explicit runs parameter
    ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
        controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
            TestData.branch, 10, null);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verify runs per job limited to 10
    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> result = response.getBody();
    assertNotNull(result);
    for (Map<JobPojo, Set<RunPojo>> jobRuns : result.values()) {
        for (Set<RunPojo> runs : jobRuns.values()) {
            assertTrue(runs.size() <= 10, "Each job should have at most 10 runs");
        }
    }
}

@Test
void getBranchJobsRunsDefaultRunsTest() {
    // Test without runs parameter (should use default 60)
    ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
        controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
            TestData.branch, null, null);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Response should be non-empty
    assertFalse(response.getBody().isEmpty());
}

@Test
void getBranchJobsRunsWithZeroRunsUsesDefaultTest() {
    // Test with runs=0 (should use default 60)
    ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
        controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
            TestData.branch, 0, null);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Should return results (not empty due to invalid runs value)
    assertFalse(response.getBody().isEmpty());
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| No runs parameter in JSON API | Parity with HTML browse | Phase 3 (this phase) | Feature completeness |
| Unlimited runs in response | Configurable limit | Phase 3 (this phase) | Better performance for large datasets |

**Deprecated/outdated:**
- None identified for this domain

## Open Questions

Things that couldn't be fully resolved:

1. **Should runs parameter apply to total runs or per-job?**
   - What we know: BrowseUIController uses it as "top N runs across all jobs" via SQL query
   - What's unclear: Whether JSON API should match exactly or do "N runs per job"
   - Recommendation: Match HTML controller behavior - top N runs across all jobs for the scope (branch or job level). This requires more complex post-filtering but maintains parity.

2. **Should getJobRunsStages use runs parameter?**
   - What we know: HTML controller `getRunStagesFromJobId` has runs parameter (line 81)
   - What's unclear: Whether this is commonly used at job level
   - Recommendation: Add for parity. The job endpoint with runs parameter limits how many historical runs to show for that specific job.

3. **Performance impact of post-filtering cached data?**
   - What we know: Cache returns all runs, filtering happens in Java
   - What's unclear: Memory impact for branches/jobs with thousands of runs
   - Recommendation: Acceptable for initial implementation. If performance issues arise, consider adding runs-aware cache keys or service methods.

## Sources

### Primary (HIGH confidence)

**Codebase analysis:**
- `/reportcard-server/src/main/java/.../controller/browse/BrowseUIController.java` - Lines 65, 81 show `@RequestParam runs` usage; lines 175-180 show `validateRuns()` implementation
- `/reportcard-server/src/main/java/.../controller/browse/BrowseJsonController.java` - Current endpoints without runs parameter
- `/reportcard-server/src/main/java/.../persist/BrowseService.java` - Lines 370-404 (`getStageViewForBranch`) and 406-445 (`getStageViewForJob`) show runs parameter usage
- `/reportcard-server/src/main/java/.../cache/model/BranchStageViewResponse.java` - Response type used by HTML controller (different from JSON controller response)
- `/reportcard-server/src/test/java/.../controller/browse/BrowseJsonControllerTest.java` - Established test patterns from Phase 1

**Spring Boot patterns:**
- Spring MVC 5.3.x `@RequestParam` documentation (defaultValue handling, type conversion)
- Established patterns in codebase for optional parameters

### Secondary (MEDIUM confidence)

**Inferred from codebase:**
- PojoComparators ordering (RUN_DESCENDING vs RUN_CASE_INSENSITIVE_ORDER) for proper run limiting
- Cache behavior (assumed data is already sorted by run_id or needs sorting)

### Tertiary (LOW confidence)

None - all findings verified against codebase.

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - @RequestParam is standard Spring MVC, well-documented
- Architecture: HIGH - Pattern clear from BrowseUIController reference
- Pitfalls: HIGH - Edge cases identified from code analysis (validation, response shape, scope)
- Implementation approach: MEDIUM - Post-filter pattern is sound but untested in this codebase

**Research date:** 2026-02-05
**Valid until:** 2026-03-05 (30 days - stable patterns, no external dependencies)

---

## Implementation Checklist for Planner

Based on research, planner should create tasks for:

1. **Add `validateRuns(Integer runs)` method to BrowseJsonController**
   - Copy from BrowseUIController or extract to shared utility
   - Return 60 if runs < 1

2. **Add helper method `limitRunsPerJob()` to BrowseJsonController**
   - Takes full cache result and max runs count
   - Returns filtered result with N runs per job maximum
   - Maintains existing comparator ordering

3. **Update `getBranchJobsRuns` endpoint signature**
   - Add `@RequestParam(required = false, defaultValue = "60") Integer runs`
   - Apply `validateRuns()` before use
   - Apply `limitRunsPerJob()` to cache result

4. **Update `getJobRunsStages` endpoint signature** (if adding runs support)
   - Add same parameter pattern
   - Note: May need different limiting logic (runs within single job)

5. **Add tests for runs parameter**
   - Default value test (null input uses 60)
   - Explicit value test (runs=10 limits results)
   - Zero/negative value test (uses default)
   - Verify consistency with HTML controller behavior

6. **Documentation/OpenAPI** (if Phase 4 scope)
   - Add `@Parameter` annotation describing runs parameter
   - Document default value and validation behavior
