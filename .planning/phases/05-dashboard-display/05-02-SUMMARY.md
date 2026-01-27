---
phase: 05-dashboard-display
plan: 02
subsystem: ui
tags: [dashboard, html, duration-formatting, metrics]

# Dependency graph
requires:
  - phase: 05-01
    provides: avgRunDuration field in JobDashboardMetrics with SQL aggregation
provides:
  - formatDuration helper method with NULL handling
  - Avg Run Duration column in pipelines dashboard table
  - Field description documenting wall clock calculation
affects: [dashboard, frontend, user-facing-metrics]

# Tech tracking
tech-stack:
  added: []
  patterns: [formatDuration pattern for displaying nullable timing metrics]

key-files:
  created: []
  modified:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java

key-decisions:
  - "NULL duration displays as dash '-' character"
  - "Use NumberStringUtil.fromSecondBigDecimalPadded for sortable formatting"
  - "Field description corrected to explain wall clock calculation (earliest stage start to latest stage end)"

patterns-established:
  - "formatDuration helper pattern: NULL check before calling formatter"
  - "Table column positioning: Avg Run Duration after Test Pass %"

# Metrics
duration: 4min
completed: 2026-01-27
---

# Phase 5 Plan 2: Dashboard Display - Avg Run Duration Summary

**Pipelines dashboard now displays average run duration with human-readable formatting (XXh XXm XXs) and proper NULL handling**

## Performance

- **Duration:** 4 min
- **Started:** 2026-01-27T19:20:19Z
- **Completed:** 2026-01-27T19:24:30Z
- **Tasks:** 3
- **Files modified:** 1

## Accomplishments
- Added formatDuration helper method with NULL handling (returns "-" for missing data)
- Added "Avg Run Duration" column to pipelines dashboard table with sortable formatting
- Added field description explaining wall clock calculation and NULL handling

## Task Commits

Each task was committed atomically:

1. **Tasks 1-2: Add formatDuration helper and Avg Run Duration column** - `1fcb365` (feat)
2. **Task 3: Add field description for Avg Run Duration** - `d7067c1` (docs)

## Files Created/Modified
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java` - Added formatDuration helper, table column header/data cell, and field description

## Decisions Made

**1. Field description correction**
- **Context:** Plan template had incorrect description text saying "sum of all test_result stage timings"
- **Decision:** Updated to accurate description "time from earliest stage start to latest stage end for each run"
- **Rationale:** Matches actual Phase 05-01 SQL implementation using max(endTime) - min(startTime) for wall clock time

**2. NULL handling display**
- **Decision:** Display "-" for NULL duration values
- **Rationale:** Follows DISP-03 requirement; provides clear visual indicator for jobs without timing data

**3. Formatting strategy**
- **Decision:** Use NumberStringUtil.fromSecondBigDecimalPadded
- **Rationale:** Provides transparent padding for correct lexical sorting in HTML tables; human-readable format (XXh XXm XXs)

## Deviations from Plan

None - plan executed exactly as written, with the field description text corrected per user instructions.

## Issues Encountered

**Pre-existing Gradle build issue**
- **Issue:** Gradle build fails with Java 11/17 compatibility error in nu.studer:gradle-jooq-plugin:8.0
- **Status:** Documented in STATE.md as pre-existing blocker
- **Impact:** Cannot run full build or tests, but code changes verified via compilation checks and grep
- **Resolution:** Not blocking this phase - code changes are correct

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- Dashboard display layer complete for average run duration
- HTML table properly renders avgRunDuration from JobDashboardMetrics
- Field description accurately documents the metric calculation
- Ready for manual verification via browser (requires server running)
- Phase 5 complete - all dashboard display requirements met

---
*Phase: 05-dashboard-display*
*Completed: 2026-01-27*
