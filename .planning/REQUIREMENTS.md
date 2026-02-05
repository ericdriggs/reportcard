# Requirements: Reportcard Browse JSON API

**Defined:** 2025-02-05
**Core Value:** CI/CD pipelines can hit a stable JSON endpoint to get the latest test result for a specific job and stage

## v1 Requirements

### API Exposure

- [ ] **API-01**: Remove `@Hidden` annotation from BrowseJsonController to expose `/v1/api/*` endpoints
- [x] **API-02**: All existing JSON endpoints return valid JSON (verified by tests)

### Latest Endpoints

- [x] **LATEST-01**: Add `/job/{jobId}/run/latest` endpoint — returns latest run for job
- [x] **LATEST-02**: Add `/job/{jobId}/run/latest/stage/{stage}` endpoint — returns latest run's specific stage test results

### Query Parameters

- [x] **FILTER-01**: `?runs=N` parameter works on JSON run endpoints (parity with HTML browse)

### Testing

- [x] **TEST-01**: Tests for each browse JSON endpoint using Testcontainers MySQL
- [x] **TEST-02**: Tests validate actual JSON serialization output

## v2 Requirements

(None — focused v1 scope)

## Out of Scope

| Feature | Reason |
|---------|--------|
| UI changes | API-only work |
| DTO layer refactoring | Use existing POJO patterns for consistency |
| OpenAPI documentation | Endpoint exposure is priority; docs can follow |
| Authentication changes | Use existing auth model |
| Pagination | Not needed for current use cases |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| API-01 | Phase 4 | Pending |
| API-02 | Phase 1 | Complete |
| LATEST-01 | Phase 2 | Complete |
| LATEST-02 | Phase 2 | Complete |
| FILTER-01 | Phase 3 | Complete |
| TEST-01 | Phase 1 | Complete |
| TEST-02 | Phase 1 | Complete |

**Coverage:**
- v1 requirements: 7 total
- Mapped to phases: 7
- Unmapped: 0

---
*Requirements defined: 2025-02-05*
*Last updated: 2026-02-05*
*Phase 3 complete: FILTER-01 marked Complete*
