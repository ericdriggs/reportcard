# Phase 2: Latest Endpoints - Context

**Created:** 2026-02-05
**Phase Goal:** Enable latest run resolution for jobs and stages

## Vision

CI/CD pipelines need to fetch the latest test results for a job without knowing the run ID upfront. This phase implements two endpoints that resolve "latest" to an actual run ID and return the same response shape as ID-based endpoints.

## Essential Features

1. **Latest Run for Job** (`/run/latest`)
   - Returns latest run's stages and test results for a given job ID
   - Same response shape as `/run/{runId}` endpoint

2. **Latest Run Stage** (`/run/latest/stage/{stage}`)
   - Returns specific stage test results from the latest run
   - Same response shape as `/run/{runId}/stage/{stage}` endpoint

## Decisions Made

| Decision | Rationale |
|----------|-----------|
| Add `getLatestRunId(Long jobId)` to BrowseService | Reusable method for both endpoints; follows existing service layer pattern |
| Use MAX(RUN.RUN_ID) query | Proven pattern from GraphService (lines 217-226); indexed on job_fk |
| No caching for latest-run-ID lookup | Freshness is critical for CI/CD; query is cheap with index |
| Delegate to existing methods after resolution | Response shapes must match ID-based endpoints exactly |
| 404 for job with no runs | Explicit error handling; follows existing throwNotFound() pattern |

## Technical Approach

From 02-RESEARCH.md findings:

1. **Service Layer**: Add `getLatestRunId(Long jobId)` using `MAX(RUN.RUN_ID)` pattern
2. **Controller Layer**: Add two endpoints that resolve latest run ID, then delegate
3. **Path Routing**: Spring MVC literal segments (`latest`) match before path variables (`{runId}`)
4. **Error Handling**: Use existing `throwNotFound()` for missing job/no runs cases

## Boundaries

- No caching for latest-run-ID resolution (always query DB)
- Response shapes MUST match existing ID-based endpoints exactly
- Tests follow patterns established in Phase 1 (BrowseJsonControllerTest)

## Test Data

From TestData.java:
- `TestData.jobId = 1L` - Job with known runs
- `TestData.stage = "api"` - Known stage name
- `runId = 1L` - Known run ID (for comparison)

## Files to Modify

- `BrowseService.java` - Add getLatestRunId() method
- `BrowseJsonController.java` - Add two new endpoints
- `BrowseServiceTest.java` - Add service layer test
- `BrowseJsonControllerTest.java` - Add integration tests

---
*Captured from /gsd:discuss-phase and research findings*
