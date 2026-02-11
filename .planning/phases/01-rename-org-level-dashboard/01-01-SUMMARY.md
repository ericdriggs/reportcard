---
phase: 01-rename-org-level-dashboard
plan: 01
subsystem: ui
tags: [spring-boot, java, url-routing, redirect, ui-terminology]

# Dependency graph
requires:
  - phase: N/A
    provides: Initial codebase with /pipelines endpoint
provides:
  - New /jobs endpoint at org level for job metrics
  - HTTP 301 redirect from /pipelines to /jobs (backwards compatibility)
  - Updated UI terminology from "Pipelines" to "Jobs"
  - Query parameter preservation through redirects
affects: [02-company-level-dashboard]

# Tech tracking
tech-stack:
  added: []
  patterns: [RedirectView with HTTP 301 for permanent URL changes, query parameter preservation]

key-files:
  created: []
  modified:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphUIController.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseHtmlHelper.java

key-decisions:
  - "Use HTTP 301 MOVED_PERMANENTLY for /pipelines redirect (not 302 temporary)"
  - "Preserve query parameters through redirect (?days=90, ?jobInfo=...)"
  - "Keep internal code names unchanged (methods, variables still use 'pipeline' terminology)"
  - "Duplicate endpoint method instead of renaming for clearer backwards compatibility"

patterns-established:
  - "URL migration pattern: new endpoint + 301 redirect from old URL"
  - "RedirectView with explicit status code control for permanent moves"
  - "Query string preservation via HttpServletRequest.getQueryString()"

# Metrics
duration: 10min
completed: 2026-02-11
---

# Phase 1 Plan 1: Rename Org-Level Dashboard Summary

**Org-level job dashboard renamed from /pipelines to /jobs with HTTP 301 redirect preserving backwards compatibility**

## Performance

- **Duration:** 10 min
- **Started:** 2026-02-11T15:54:14-08:00
- **Completed:** 2026-02-11T23:57:12Z
- **Tasks:** 3 (2 auto, 1 human-verify checkpoint)
- **Files modified:** 3

## Accomplishments
- Created new /jobs endpoint maintaining identical functionality to /pipelines
- Implemented HTTP 301 permanent redirect from /pipelines to /jobs with query parameter preservation
- Updated page title from "Org Pipelines" to "Org Jobs"
- Updated navigation breadcrumbs from "Pipelines ⏲" to "Jobs ⏲"

## Task Commits

Each task was committed atomically:

1. **Task 1: Rename URL endpoint and add redirect** - `fee0652` (feat)
2. **Task 2: Update UI terminology** - `63db16f` (feat)
3. **Task 3: Human verification checkpoint** - Approved by user

**Plan metadata:** (pending final commit)

## Files Created/Modified
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphUIController.java` - Added /jobs endpoint, converted /pipelines to 301 redirect
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java` - Changed page title to "Org Jobs"
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseHtmlHelper.java` - Updated navigation link to "Jobs ⏲" pointing to /jobs

## Decisions Made

**1. HTTP 301 vs 302 for redirect**
- Chose HTTP 301 MOVED_PERMANENTLY to signal permanent URL change
- Informs browsers and search engines to update bookmarks/indexes
- Better UX than 302 temporary redirect

**2. Query parameter preservation**
- Used HttpServletRequest.getQueryString() to capture all parameters
- Ensures filters (?days=90, ?jobInfo=...) work through redirect
- Critical for user workflows relying on filtered views

**3. Internal code naming unchanged**
- Method names, variables, database tables still use "pipeline" terminology
- Avoids unnecessary code churn and migration complexity
- Database schema uses "pipeline" - aligning would require migrations
- UI-facing terminology is what matters to users

**4. Duplicate endpoint vs rename**
- Created new /jobs endpoint as duplicate of /pipelines logic
- Converted /pipelines to redirect-only endpoint
- Clearer backwards compatibility than in-place rename
- Easier to maintain both during transition period

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None - implementation proceeded smoothly, verification confirmed all functionality working as expected.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

**Ready for Phase 2:** Company-level dashboard can now be built with consistent /jobs terminology.

**Foundation established:**
- URL pattern established: /company/{company}/org/{org}/jobs
- Company level will follow: /company/{company}/jobs
- Redirect pattern available for future migrations
- UI terminology aligned with actual functionality

**No blockers:** Phase 2 can proceed immediately.

---
*Phase: 01-rename-org-level-dashboard*
*Completed: 2026-02-11*
