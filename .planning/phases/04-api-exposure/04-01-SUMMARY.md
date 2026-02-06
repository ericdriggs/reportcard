---
phase: 04-api-exposure
plan: 01
subsystem: api
tags: [swagger, openapi, springdoc, rest-controller]

# Dependency graph
requires:
  - phase: 01-foundation-validation
    provides: Verified JSON serialization and no path conflicts
  - phase: 02-latest-endpoints
    provides: Latest run endpoints for CI/CD automation
  - phase: 03-query-parameter-parity
    provides: Runs parameter filtering for JSON endpoints
provides:
  - Exposed BrowseJsonController in Swagger UI
  - Public JSON API at /v1/api/* discoverable via OpenAPI
affects: [production-deployment, api-documentation, client-integration]

# Tech tracking
tech-stack:
  added: []
  patterns: [exposed-rest-controller-pattern]

key-files:
  created: []
  modified:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java

key-decisions:
  - "Remove @Hidden without adding @Operation annotations (kept minimal change scope)"

patterns-established:
  - "JSON controller exposure: @RestController + @RequestMapping('/v1/api') without @Hidden"

# Metrics
duration: 5min
completed: 2026-02-05
---

# Phase 4 Plan 01: API Exposure Summary

**Removed @Hidden annotation from BrowseJsonController, exposing 12+ JSON browse endpoints in Swagger UI at /swagger-ui.html**

## Performance

- **Duration:** 5 min
- **Started:** 2026-02-05T23:57:23Z
- **Completed:** 2026-02-06T00:02:32Z
- **Tasks:** 2
- **Files modified:** 1

## Accomplishments
- Removed @Hidden annotation from BrowseJsonController
- Removed unused Hidden import statement
- Verified no path conflicts with HTML controllers (AmbiguousMappingException check)
- Confirmed BrowseJsonController now matches GraphJsonController exposure pattern

## Task Commits

Each task was committed atomically:

1. **Task 1: Remove @Hidden annotation and verify API exposure** - `77c68d1` (feat)
2. **Task 2: Validate OpenAPI documentation exposure** - verification only, no commit needed

**Plan metadata:** pending (docs: complete plan)

## Files Created/Modified
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java` - Removed @Hidden annotation and unused import

## Decisions Made
- Removed @Hidden without adding @Operation annotations - kept change minimal as plan specified "DO NOT add @Operation annotations (out of scope)"

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
- Pre-existing test failures in JunitControllerTest and StorageControllerTest (4 failures) - verified these fail both before and after the change, unrelated to @Hidden removal
- BrowseJsonControllerTest and all browse-related tests pass successfully

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- All browse-json feature requirements complete (7/7)
- JSON API now publicly accessible at /v1/api/*
- Endpoints discoverable via Swagger UI at /swagger-ui.html
- Ready for production deployment

---
*Phase: 04-api-exposure*
*Completed: 2026-02-05*
