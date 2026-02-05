# Roadmap: Reportcard Browse JSON API

**Project:** Expose JSON API endpoints in Reportcard test metrics dashboard
**Core Value:** CI/CD pipelines can hit a stable JSON endpoint to get the latest test result for a specific job and stage
**Created:** 2026-02-05
**Depth:** Standard (4 phases)

## Overview

This roadmap delivers production-ready JSON API endpoints by validating existing infrastructure, implementing /run/latest resolution, completing feature parity, and safely exposing endpoints. The phases follow dependency order: foundation safety measures before implementation, core features before final exposure.

## Progress

| Phase | Goal | Requirements | Status |
|-------|------|--------------|--------|
| 1 - Foundation & Validation | Validate existing JSON endpoints work correctly | API-02, TEST-01, TEST-02 | **Complete** |
| 2 - Latest Endpoints | Enable latest run resolution for jobs and stages | LATEST-01, LATEST-02 | **Complete** |
| 3 - Query Parameter Parity | Complete feature parity with HTML browse endpoints | FILTER-01 | **Complete** |
| 4 - API Exposure | Safely expose JSON API to production clients | API-01 | Pending |

**Overall:** 3/4 phases complete (75%)

---

## Phase 1: Foundation & Validation

**Goal:** Validate existing JSON endpoints work correctly

**Dependencies:** None (foundation phase)

**Plans:** 3 plans

**Requirements:**
- API-02: All existing JSON endpoints return valid JSON (verified by tests)
- TEST-01: Tests for each browse JSON endpoint using Testcontainers MySQL
- TEST-02: Tests validate actual JSON serialization output

**Success Criteria:**

1. User can call any existing `/v1/api/browse/*` endpoint and receive valid JSON response (no serialization errors)
2. User can verify path routing works correctly without conflicts between HTML and JSON endpoints
3. User can examine integration test suite showing coverage for each JSON endpoint with Testcontainers
4. User can observe consistent error responses across all JSON endpoints using ResponseDetails wrapper
5. User can confirm JOOQ POJOs serialize without circular reference failures

Plans:
- [x] 01-01-PLAN.md — Test hierarchy endpoints (company -> org -> repo -> branch)
- [x] 01-02-PLAN.md — Test job/run/stage endpoints
- [x] 01-03-PLAN.md — Test SHA lookups and error cases

**Notes:**
- Foundation phase addresses path conflicts (Pitfall 1), serialization failures (Pitfall 2), and establishes testing patterns
- Tests use direct controller calls following existing patterns in AbstractBrowseServiceTest
- Validates safety before implementing new features or removing @Hidden annotation

---

## Phase 2: Latest Endpoints

**Goal:** Enable latest run resolution for jobs and stages

**Dependencies:** Phase 1 (validation patterns established)

**Plans:** 3 plans

**Requirements:**
- LATEST-01: Add `/job/{jobId}/run/latest` endpoint — returns latest run for job
- LATEST-02: Add `/job/{jobId}/run/latest/stage/{stage}` endpoint — returns latest run's specific stage test results

**Success Criteria:**

1. User can call `/job/{jobId}/run/latest` and receive the most recent run for that job
2. User can call `/job/{jobId}/run/latest/stage/{stage}` and receive test results for specific stage of latest run
3. User can observe that latest endpoints return identical JSON structure to ID-based endpoints
4. User can verify latest resolution uses max(run_id) query with proper database indexing

Plans:
- [x] 02-01-PLAN.md — Add getLatestRunId service method (Wave 1)
- [x] 02-02-PLAN.md — Add latest run endpoint (Wave 2)
- [x] 02-03-PLAN.md — Add latest stage endpoint (Wave 2)

**Notes:**
- Core feature enabling CI/CD automation without knowing run IDs upfront
- Implements dedicated /latest path pattern (Spring MVC literal segment matching)
- Skip caching for latest-run-ID resolution for freshness; use existing cache after resolution
- Service layer adds reusable BrowseService.getLatestRunId() method

---

## Phase 3: Query Parameter Parity

**Goal:** Complete feature parity with HTML browse endpoints

**Dependencies:** Phase 2 (all endpoints exist)

**Plans:** 1 plan

**Requirements:**
- FILTER-01: ?runs=N parameter works on JSON run endpoints (parity with HTML browse)

**Success Criteria:**

1. User can call JSON endpoints with `?runs=N` parameter and receive filtered results
2. User can verify query parameter validation matches BrowseUIController behavior
3. User can observe consistent parameter handling across both HTML and JSON endpoints

Plans:
- [x] 03-01-PLAN.md — Add ?runs=N parameter to getBranchJobsRuns and getJobRunsStages endpoints

**Notes:**
- Achieves feature parity between /v1/browse/* (HTML) and /v1/api/* (JSON)
- Reuses existing validateRuns() method from UI controller
- Tests validate boundary values and error cases

---

## Phase 4: API Exposure

**Goal:** Safely expose JSON API to production clients

**Dependencies:** Phase 3 (all features complete and tested)

**Plans:** 1 plan

**Requirements:**
- API-01: Remove @Hidden annotation from BrowseJsonController to expose /v1/api/* endpoints

**Success Criteria:**

1. User can view all JSON endpoints in Swagger UI documentation
2. User can access /v1/api/* endpoints without @Hidden restriction
3. User can verify no path routing conflicts exist between HTML and JSON controllers
4. User can observe OpenAPI documentation with operation summaries and parameter descriptions

Plans:
- [ ] 04-01-PLAN.md — Remove @Hidden annotation and verify API exposure

**Notes:**
- Final phase after all validation and implementation complete
- Minimal risk due to comprehensive testing in earlier phases
- OpenAPI documentation completion ensures good developer experience
- Manual validation with curl/Postman before marking complete

---

## Coverage

All 7 v1 requirements mapped:
- API-01 -> Phase 4
- API-02 -> Phase 1
- LATEST-01 -> Phase 2
- LATEST-02 -> Phase 2
- FILTER-01 -> Phase 3
- TEST-01 -> Phase 1
- TEST-02 -> Phase 1

No orphaned requirements.

---

## Notes

**Phase ordering rationale:**
- Phase 1 establishes safety and testing patterns before any implementation changes
- Phase 2 implements core feature (/run/latest) after foundation validated
- Phase 3 completes feature set before exposure
- Phase 4 exposes to production only after comprehensive validation

**Research flags:**
- Phase 2 may need deeper research on cache invalidation if AbstractAsyncCache integration proves complex
- All other phases use well-documented Spring Boot patterns

**Key constraints:**
- Must use Testcontainers (MySQL 8.0) + LocalStack (S3) for integration tests
- JSON response structure must mirror existing BrowseJsonController patterns
- No UI changes, authentication changes, or schema modifications

---
*Last updated: 2026-02-05*
*Phase 2 complete: 2026-02-05*
*Phase 3 complete: 2026-02-05*
