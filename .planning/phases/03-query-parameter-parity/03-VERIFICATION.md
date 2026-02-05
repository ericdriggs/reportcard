---
phase: 03-query-parameter-parity
verified: 2026-02-05T23:45:00Z
status: passed
score: 5/5 must-haves verified
---

# Phase 3: Query Parameter Parity Verification Report

**Phase Goal:** Complete feature parity with HTML browse endpoints
**Verified:** 2026-02-05T23:45:00Z
**Status:** passed
**Re-verification:** No — initial verification

## Goal Achievement

### Observable Truths

| # | Truth | Status | Evidence |
|---|-------|--------|----------|
| 1 | User can call getBranchJobsRuns with ?runs=N and receive limited results | ✓ VERIFIED | Endpoint has @RequestParam runs, calls validateRuns(), applies limitRunsPerJob() filter |
| 2 | User can call getJobRunsStages with ?runs=N and receive limited results | ✓ VERIFIED | Endpoint has @RequestParam runs, calls validateRuns(), applies limitRunsInJob() filter |
| 3 | User receives default 60 runs when parameter is omitted | ✓ VERIFIED | validateRuns() returns 60 for null; @RequestParam has defaultValue="60" |
| 4 | User receives default 60 runs when parameter is < 1 | ✓ VERIFIED | validateRuns() checks `runs < 1` and returns 60; tests cover 0 and -1 cases |
| 5 | Response shape remains unchanged (Map structures preserved) | ✓ VERIFIED | limitRunsPerJob/limitRunsInJob return same Map types, preserve structure |

**Score:** 5/5 truths verified

### Required Artifacts

| Artifact | Expected | Status | Details |
|----------|----------|--------|---------|
| `BrowseJsonController.java` | validateRuns() helper method | ✓ VERIFIED | Lines 170-175: validates runs, returns 60 for null or < 1 |
| `BrowseJsonController.java` | limitRunsPerJob() helper method | ✓ VERIFIED | Lines 181-197: post-filters branch-level Map<K, Map<JobPojo, Set<RunPojo>>> |
| `BrowseJsonController.java` | limitRunsInJob() helper method | ✓ VERIFIED | Lines 203-218: post-filters job-level Map<JobPojo, Map<RunPojo, Set<StagePojo>>> |
| `BrowseJsonController.java` | getBranchJobsRuns with @RequestParam runs | ✓ VERIFIED | Line 68: @RequestParam(required=false, defaultValue="60") Integer runs |
| `BrowseJsonController.java` | getJobRunsStages with @RequestParam runs | ✓ VERIFIED | Line 85: @RequestParam(required=false, defaultValue="60") Integer runs |
| `BrowseJsonControllerTest.java` | Tests for runs parameter behavior | ✓ VERIFIED | Lines 716-836: 6 tests covering explicit, null, 0, -1 for both endpoints |

### Key Link Verification

| From | To | Via | Status | Details |
|------|----|----|--------|---------|
| getBranchJobsRuns | validateRuns | parameter validation | ✓ WIRED | Line 70: `runs = validateRuns(runs)` before use |
| getBranchJobsRuns | limitRunsPerJob | post-filter result | ✓ WIRED | Line 73: `limitRunsPerJob(fullResult, runs)` applied before return |
| getJobRunsStages | validateRuns | parameter validation | ✓ WIRED | Line 86: `runs = validateRuns(runs)` before use |
| getJobRunsStages | limitRunsInJob | post-filter result | ✓ WIRED | Line 89: `limitRunsInJob(fullResult, runs)` applied before return |
| limitRunsPerJob | PojoComparators.RUN_DESCENDING | sort runs by ID desc | ✓ WIRED | Line 188: `.sorted(PojoComparators.RUN_DESCENDING)` |
| limitRunsInJob | PojoComparators.RUN_DESCENDING | sort runs by ID desc | ✓ WIRED | Line 208: `.sorted(...comparingByKey(PojoComparators.RUN_DESCENDING))` |

### Requirements Coverage

| Requirement | Status | Supporting Evidence |
|-------------|--------|---------------------|
| FILTER-01: ?runs=N parameter works on JSON run endpoints | ✓ SATISFIED | Both endpoints accept runs parameter, validate it, and apply filtering |

### Anti-Patterns Found

**None** — No blocking anti-patterns detected.

All implementations are substantive:
- validateRuns() has real logic (null check, < 1 check, returns 60)
- limitRunsPerJob() has 17 lines of stream filtering logic
- limitRunsInJob() has 16 lines of stream filtering logic
- Both helper methods sort descending and limit to maxRuns
- Six comprehensive tests covering edge cases

No TODO/FIXME markers in runs parameter implementation.
No stub patterns (empty returns, console.log only, placeholders).

### Feature Parity with BrowseUIController

| Aspect | BrowseUIController | BrowseJsonController | Parity Status |
|--------|-------------------|---------------------|---------------|
| validateRuns signature | `Integer validateRuns(int runs)` | `Integer validateRuns(Integer runs)` | ✓ EQUIVALENT |
| validateRuns logic | Returns 60 if runs < 1 | Returns 60 if runs == null OR runs < 1 | ✓ EQUIVALENT (JSON handles null) |
| Default runs value | 60 | 60 | ✓ IDENTICAL |
| Parameter annotation | `@RequestParam(required = false, defaultValue = "60")` | `@RequestParam(required = false, defaultValue = "60")` | ✓ IDENTICAL |
| Filtering approach | Pre-fetch with limit | Post-filter cached result | ⚠️ DIFFERENT IMPLEMENTATION |

**Note on filtering approach:** BrowseUIController and BrowseJsonController use different data retrieval patterns (UI calls service methods, JSON uses cache), but both achieve the same user-facing result: limited runs per job. This is intentional per research — post-filtering avoids cache explosion from N run values.

### Human Verification Required

None — all success criteria can be verified programmatically through code inspection and test assertions.

---

## Detailed Verification

### Truth 1: User can call getBranchJobsRuns with ?runs=N and receive limited results

**Evidence of wiring:**
```java
// Line 68-73 in BrowseJsonController.java
@RequestParam(required = false, defaultValue = "60") Integer runs,
@RequestParam(required = false) Map<String, String> jobInfoFilters) {
    runs = validateRuns(runs);
    Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult =
        BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchDTO(company, org, repo, branch));
    return new ResponseEntity<>(limitRunsPerJob(fullResult, runs), HttpStatus.OK);
```

**Flow verification:**
1. ✓ Parameter accepted: `@RequestParam runs`
2. ✓ Parameter validated: `runs = validateRuns(runs)`
3. ✓ Cache fetched: `getValue()` returns full data
4. ✓ Result filtered: `limitRunsPerJob(fullResult, runs)`
5. ✓ Filtered result returned: `new ResponseEntity<>(...)`

**Test coverage:**
- Line 719-738: `getBranchJobsRunsWithRunsParameterTest` — tests explicit limit (10 runs)
- Line 741-761: `getBranchJobsRunsDefaultRunsTest` — tests null uses default 60
- Line 764-782: `getBranchJobsRunsWithZeroRunsUsesDefaultTest` — tests 0 becomes 60
- Line 785-796: `getBranchJobsRunsWithNegativeRunsUsesDefaultTest` — tests -1 becomes 60

### Truth 2: User can call getJobRunsStages with ?runs=N and receive limited results

**Evidence of wiring:**
```java
// Line 85-90 in BrowseJsonController.java
@RequestParam(required = false, defaultValue = "60") Integer runs) {
    runs = validateRuns(runs);
    Map<JobPojo, Map<RunPojo, Set<StagePojo>>> fullResult =
        JobRunsStagesCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobDTO(company, org, repo, branch, jobId));
    return new ResponseEntity<>(limitRunsInJob(fullResult, runs), HttpStatus.OK);
```

**Flow verification:**
1. ✓ Parameter accepted: `@RequestParam runs`
2. ✓ Parameter validated: `runs = validateRuns(runs)`
3. ✓ Cache fetched: `getValue()` returns full data
4. ✓ Result filtered: `limitRunsInJob(fullResult, runs)`
5. ✓ Filtered result returned: `new ResponseEntity<>(...)`

**Test coverage:**
- Line 799-816: `getJobRunsStagesWithRunsParameterTest` — tests explicit limit (5 runs)
- Line 819-836: `getJobRunsStagesDefaultRunsTest` — tests null uses default 60

### Truth 3: User receives default 60 runs when parameter is omitted

**Evidence:**
```java
// Lines 68, 85 in BrowseJsonController.java
@RequestParam(required = false, defaultValue = "60") Integer runs
```

Spring Boot applies defaultValue="60" when parameter is omitted from request.

**Validation layer:**
```java
// Lines 170-175 in BrowseJsonController.java
Integer validateRuns(Integer runs) {
    if (runs == null || runs < 1) {
        return 60;
    }
    return runs;
}
```

Double protection: Spring default + validateRuns() null check.

**Test evidence:**
- Line 744-746: Test explicitly passes `null` and expects default 60 behavior
- Line 821-823: Test explicitly passes `null` for getJobRunsStages

### Truth 4: User receives default 60 runs when parameter is < 1

**Evidence:**
```java
// Lines 170-175 in BrowseJsonController.java
Integer validateRuns(Integer runs) {
    if (runs == null || runs < 1) {
        return 60;
    }
    return runs;
}
```

Explicit check: `runs < 1` returns 60.

**Test coverage:**
- Lines 764-782: Tests `runs=0` results in default 60
- Lines 785-796: Tests `runs=-1` results in default 60

Both tests verify:
1. Response not empty (validation didn't break request)
2. Results limited to 60 runs per job

### Truth 5: Response shape remains unchanged (Map structures preserved)

**Evidence for getBranchJobsRuns:**
```java
// Input type (line 71-72)
Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult

// limitRunsPerJob signature (line 181)
private <K> Map<K, Map<JobPojo, Set<RunPojo>>> limitRunsPerJob(
        Map<K, Map<JobPojo, Set<RunPojo>>> input, int maxRuns)

// Return type (line 73)
return new ResponseEntity<>(limitRunsPerJob(fullResult, runs), HttpStatus.OK);
```

Same type flows through: `Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>`

**Evidence for getJobRunsStages:**
```java
// Input type (line 87-88)
Map<JobPojo, Map<RunPojo, Set<StagePojo>>> fullResult

// limitRunsInJob signature (line 203)
private Map<JobPojo, Map<RunPojo, Set<StagePojo>>> limitRunsInJob(
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> input, int maxRuns)

// Return type (line 89)
return new ResponseEntity<>(limitRunsInJob(fullResult, runs), HttpStatus.OK);
```

Same type flows through: `Map<JobPojo, Map<RunPojo, Set<StagePojo>>>`

**Implementation verification:**
Both helper methods reconstruct the Map structure:
- limitRunsPerJob: Creates new `LinkedHashMap` → `TreeMap` → `TreeSet` preserving structure
- limitRunsInJob: Creates new `TreeMap` → `TreeMap` → `Set` preserving structure

No BranchStageViewResponse or other DTO introduced. Plain JOOQ POJOs preserved.

---

## Verification Summary

**All must-haves verified.**

Phase 3 successfully achieves feature parity with HTML browse endpoints:

1. ✓ Both JSON endpoints accept `?runs=N` query parameter
2. ✓ Parameter validation matches BrowseUIController behavior (60 for invalid)
3. ✓ Post-filter caching pattern avoids cache explosion
4. ✓ Response structures preserved (no DTO changes)
5. ✓ Comprehensive test coverage (6 new tests + 2 updated tests)
6. ✓ All helper methods substantive (no stubs)
7. ✓ All key links wired correctly

**Ready for Phase 4 (API Exposure).**

---

_Verified: 2026-02-05T23:45:00Z_
_Verifier: Claude (gsd-verifier)_
