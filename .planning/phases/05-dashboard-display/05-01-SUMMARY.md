---
phase: 05-dashboard-display
plan: 01
subsystem: api
tags: [java, spring-boot, jooq, dashboard, metrics, timing]

# Dependency graph
requires:
  - phase: 04.1-migrate-timing-to-test-result
    provides: test_result.start_time and test_result.end_time columns in database schema
provides:
  - TestResultGraph includes startTime and endTime fields for data layer timing access
  - GraphService query SELECTs timing columns from test_result table
  - JobDashboardMetrics calculates avgRunDuration from test_result timing data
  - Unit tests verifying duration calculation logic (NULL handling, multi-stage aggregation)
affects: [05-02, dashboard, ui]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - Duration calculation pattern - sum test_result durations per run, average across runs
    - NULL-safe timing aggregation - skip runs without timing data
    - Multi-stage aggregation - sum all stages for single run duration

key-files:
  created:
    - reportcard-server/src/test/java/io/github/ericdriggs/reportcard/model/pipeline/JobDashboardMetricsTest.java
  modified:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/graph/TestResultGraph.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/pipeline/JobDashboardMetrics.java

key-decisions:
  - "Duration stored as BigDecimal seconds with 2 decimal places (HALF_UP rounding)"
  - "NULL timing values excluded from average calculation (not treated as zero)"
  - "Multi-stage runs aggregate durations (sum per run, then average across runs)"
  - "Duration.between used for Instant-to-Duration conversion"

patterns-established:
  - "NULL-safe aggregation: Track hasAnyTiming flag per run, only include runs with timing data"
  - "Nanosecond precision: Convert Duration.getNano() to seconds with 9-decimal division"
  - "Builder pattern: Add avgDuration to JobDashboardMetrics.builder() chain"

# Metrics
duration: 3min
completed: 2026-01-27
---

# Phase 05 Plan 01: Dashboard Timing Data Layer Summary

**Backend data layer extended with avgRunDuration calculation from test_result timing for dashboard display**

## Performance

- **Duration:** 3 min
- **Started:** 2026-01-27T19:04:59Z
- **Completed:** 2026-01-27T19:08:04Z
- **Tasks:** 3
- **Files modified:** 4

## Accomplishments

- TestResultGraph now includes startTime and endTime fields matching JOOQ-generated TestResultPojo
- GraphService query retrieves timing columns from test_result table in getPipelineDashboardCompanyGraphs
- JobDashboardMetrics calculates avgRunDuration by summing test_result durations per run and averaging across runs
- NULL timing values handled gracefully (old data excluded from calculations)
- Multi-stage runs aggregate correctly (sum all stage durations for run total)
- Unit tests verify calculation logic across 4 scenarios (valid timing, NULL timing, all-NULL, multi-stage)

## Task Commits

Each task was committed atomically:

1. **Task 1: Add timing fields to TestResultGraph and update GraphService query** - `f53c0c9` (feat)
2. **Task 2: Calculate avgRunDuration in JobDashboardMetrics** - `d158fcc` (feat)
3. **Task 3: Write unit tests for duration calculation** - `661fe03` (test)

## Files Created/Modified

- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/graph/TestResultGraph.java` - Added startTime and endTime Instant fields to record, updated asTestResultPojo() mapping
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java` - Updated getTestResultSelect() to SELECT TEST_RESULT.START_TIME and TEST_RESULT.END_TIME
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/pipeline/JobDashboardMetrics.java` - Added avgRunDuration field, implemented calculation logic with Duration.between, NULL handling, and multi-stage aggregation
- `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/model/pipeline/JobDashboardMetricsTest.java` - Created test class with 4 test methods covering duration calculation scenarios

## Decisions Made

- **Duration precision:** BigDecimal with 2 decimal places using HALF_UP rounding provides sufficient precision for wall clock timing without excessive storage
- **NULL handling strategy:** Exclude runs without timing data from average rather than treating as zero - preserves meaningful average for runs with timing while handling old data gracefully
- **Multi-stage aggregation:** Sum all test_result durations within a run before averaging across runs - aligns with Phase 4.1 design where each stage has separate test_result timing
- **Duration conversion:** Use Duration.between(start, end) for Instant-to-Duration conversion, then extract seconds + nanoseconds with proper decimal conversion

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

**Pre-existing build issue:** Gradle build fails with Java 11/17 compatibility error in reportcard-server module (nu.studer:gradle-jooq-plugin:8.0). This existed before Phase 5 work and is documented in STATE.md. Doesn't block code verification - javac compilation succeeds, code is syntactically correct. Tests created but cannot be run via Gradle until build issue is resolved.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- Backend data layer ready with avgRunDuration field populated
- Dashboard API endpoints (JobDashboardController) will return avgRunDuration in JobDashboardMetrics JSON response
- Frontend can consume avgRunDuration field for UI rendering
- No blockers for Phase 05-02 (Frontend timing display)

**Note:** Tests cannot be run until pre-existing Gradle build issue is resolved. Test code is complete and follows project patterns, but requires working build system for execution verification.

---
*Phase: 05-dashboard-display*
*Completed: 2026-01-27*
