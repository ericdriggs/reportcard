---
phase: 03-query-parameter-parity
plan: 01
subsystem: browse-controller
tags: [spring-boot, rest-api, query-parameters, filtering]

dependency-graph:
  requires:
    - 02-latest-endpoints
  provides:
    - runs-parameter-json
    - filter-parity
  affects:
    - 04-api-exposure

tech-stack:
  added: []
  patterns:
    - post-filter-caching
    - parameter-validation

file-tracking:
  key-files:
    created: []
    modified:
      - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java
      - reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonControllerTest.java

decisions:
  - id: POST-FILTER-CACHE
    choice: "Post-filter cached results instead of adding parameter to cache key"
    rationale: "Avoids cache explosion from N run values; cached data is superset"
    date: 2026-02-05
  - id: VALIDATE-RUNS
    choice: "Handle null in validateRuns for JSON controller (vs primitive int in UI)"
    rationale: "JSON controller receives nullable Integer from @RequestParam"
    date: 2026-02-05

metrics:
  duration: "~8 minutes"
  completed: 2026-02-05
---

# Phase 3 Plan 01: Query Parameter Parity Summary

**One-liner:** Added ?runs=N query parameter to JSON browse endpoints with post-filter caching pattern

## Commits

| Hash | Type | Description |
|------|------|-------------|
| 722f370 | feat | Add runs parameter to JSON browse endpoints |
| d7f09d7 | test | Add integration tests for runs parameter |

## Changes Made

### Task 1: Helper Methods and Endpoint Updates
- Added `validateRuns(Integer runs)` method that returns 60 for null or values < 1
- Added `limitRunsPerJob()` helper for branch-level run limiting (Map<K, Map<JobPojo, Set<RunPojo>>>)
- Added `limitRunsInJob()` helper for job-level run limiting (Map<JobPojo, Map<RunPojo, Set<StagePojo>>>)
- Updated `getBranchJobsRuns` endpoint with `@RequestParam(required = false, defaultValue = "60") Integer runs`
- Updated `getJobRunsStages` endpoint with same parameter pattern
- Both endpoints now validate runs and post-filter cached results

### Task 2: Integration Tests
- Added 6 new tests for runs parameter behavior:
  - `getBranchJobsRunsWithRunsParameterTest` - explicit limit to 10
  - `getBranchJobsRunsDefaultRunsTest` - null uses default 60
  - `getBranchJobsRunsWithZeroRunsUsesDefaultTest` - 0 uses default 60
  - `getBranchJobsRunsWithNegativeRunsUsesDefaultTest` - -1 uses default 60
  - `getJobRunsStagesWithRunsParameterTest` - explicit limit to 5
  - `getJobRunsStagesDefaultRunsTest` - null uses default 60
- Updated existing tests to match new method signatures

### Task 3: Feature Parity Verification
- Confirmed `validateRuns()` behavior matches BrowseUIController
- All browse controller tests pass
- All browse service tests pass

## Verification Results

| Criterion | Status |
|-----------|--------|
| Both endpoints accept ?runs=N parameter | PASS |
| Default value (60) used when parameter omitted or invalid | PASS |
| Runs are limited per-job, not globally | PASS |
| Response shape unchanged (Map structures preserved) | PASS |
| All existing tests still pass | PASS |
| New parameter tests pass | PASS |

## Deviations from Plan

None - plan executed exactly as written.

## Files Modified

### BrowseJsonController.java
- Added imports: LinkedHashMap, TreeMap, TreeSet, Collectors
- Added `validateRuns()` helper method
- Added `limitRunsPerJob()` helper method (generic for branch-level maps)
- Added `limitRunsInJob()` helper method (for job-level maps)
- Modified `getBranchJobsRuns()` signature and implementation
- Modified `getJobRunsStages()` signature and implementation

### BrowseJsonControllerTest.java
- Updated `getBranchJobsRunsJsonSuccessTest` signature
- Updated `getJobRunsStagesJsonSuccessTest` signature
- Added 6 new tests in "Runs Parameter Tests" section

## Key Implementation Details

### Post-Filter Pattern
The implementation uses a post-filter pattern rather than caching per-run-limit:
1. Fetch full cached result (all runs)
2. Sort runs descending by run ID
3. Take first N runs per job
4. Return filtered result preserving original Map structure

This avoids cache explosion while keeping responses fast since the limiting operation is O(n) on already-fetched data.

### Null Handling Difference
BrowseUIController: `Integer validateRuns(int runs)` - receives defaultValue from Spring
BrowseJsonController: `Integer validateRuns(Integer runs)` - handles null explicitly

Both achieve the same behavior: return 60 for invalid inputs.

## Next Phase Readiness

Ready for Phase 4 (API Exposure):
- All JSON endpoints now have feature parity with HTML endpoints
- ?runs=N parameter works correctly
- Response shapes preserved for backwards compatibility
- Comprehensive test coverage in place
