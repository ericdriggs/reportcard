---
phase: 02-add-company-level-dashboard
plan: 01
subsystem: api
tags: [jooq, java, spring-boot, dashboard, query-optimization]

# Dependency graph
requires:
  - phase: 01-rename-org-level-dashboard
    provides: URL structure and terminology foundation (Jobs vs Pipelines)
provides:
  - Optional org filtering in GraphService using trueCondition() pattern
  - HTML title rendering that adapts to company-level vs org-level views
  - Backend ready for company-level dashboard endpoint
affects:
  - 02-02-add-company-controller-endpoint
  - future dashboard features requiring flexible filtering

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "trueCondition() for optional JOOQ filters (company-level aggregation)"
    - "Conditional tableConditionMap.put() for optional parameters"
    - "Ternary operators for conditional title rendering"

key-files:
  created: []
  modified:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java

key-decisions:
  - "Use trueCondition() pattern for null org (established JOOQ pattern per research)"
  - "Guard tableConditionMap.put(ORG, ...) with null check to prevent query builder issues"
  - "Render company-only title when org is null to avoid 'company/null' display"

patterns-established:
  - "Optional parameter pattern: ternary with trueCondition() for filters"
  - "Optional parameter pattern: conditional tableConditionMap.put()"
  - "Title rendering: conditional based on parameter presence"

# Metrics
duration: 4min
completed: 2026-02-11
---

# Phase 02 Plan 01: Backend Optional Org Filtering Summary

**GraphService and HTML helper support optional org parameter using trueCondition() pattern for company-wide aggregation**

## Performance

- **Duration:** 4 min
- **Started:** 2026-02-12T01:59:24Z
- **Completed:** 2026-02-12T02:03:01Z
- **Tasks:** 2
- **Files modified:** 2

## Accomplishments
- GraphService.getPipelineDashboardCompanyGraphs() handles null org parameter without crashing
- When org is null, query aggregates data across all orgs for the company (via trueCondition())
- HTML titles render "company" for company-level, "company/org" for org-level (no "company/null")
- Existing org-level endpoint functionality unchanged (regression-free verification)

## Task Commits

Each task was committed atomically:

1. **Task 1: Make org filter conditional in GraphService** - `2f96821` (refactor)
2. **Task 2: Update HTML title logic for null org** - `63d9928` (refactor)

## Files Created/Modified
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java` - Optional org filtering using trueCondition() pattern
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java` - Conditional title rendering based on org presence

## Decisions Made

**Use trueCondition() for null org:**
- Established JOOQ pattern for "always true" no-op filters (identified in research)
- More idiomatic than alternative approaches (empty string, special handling)
- Allows natural FK relationship traversal for company-wide aggregation

**Conditional tableConditionMap.put():**
- Prevents null org from breaking query builder
- Cleaner than putting null values in the map
- Makes intent explicit: "org filter is optional"

**Ternary for title logic:**
- Simple, readable conditional rendering
- Avoids method duplication or complex branching
- Preserves existing behavior when org is present

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None - changes were straightforward refactoring using established JOOQ patterns.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

**Ready for Plan 02 (Add company-level controller endpoint):**
- Service layer can accept JobDashboardRequest with null org
- Service returns company-wide aggregated data when org is null
- HTML rendering produces appropriate titles for both views
- No breaking changes to existing org-level endpoint

**Test status:**
- Build passes without compilation errors
- Pre-existing test failures unrelated to changes (4 failures, down from 5 before changes)
- No regressions introduced by optional org filtering

**Next step:** Add GET endpoint at `/company/{company}/jobs` that passes null org to service layer

---
*Phase: 02-add-company-level-dashboard*
*Completed: 2026-02-11*
