# Requirements: Reportcard Browse JSON API

**Defined:** 2025-02-05
**Core Value:** CI/CD pipelines can hit a stable JSON endpoint to get the latest test result for a specific job and stage

## v1 Requirements

### API Exposure

- [ ] **API-01**: Remove `@Hidden` annotation from BrowseJsonController to expose `/v1/api/*` endpoints
- [ ] **API-02**: All existing JSON endpoints return valid JSON (verified by tests)

### Latest Endpoints

- [ ] **LATEST-01**: Add `/job/{jobId}/run/latest` endpoint — returns latest run for job
- [ ] **LATEST-02**: Add `/job/{jobId}/run/latest/stage/{stage}` endpoint — returns latest run's specific stage test results

### Query Parameters

- [ ] **FILTER-01**: `?runs=N` parameter works on JSON run endpoints (parity with HTML browse)

### Testing

- [ ] **TEST-01**: Tests for each browse JSON endpoint using Testcontainers MySQL
- [ ] **TEST-02**: Tests validate actual JSON serialization output

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
| API-01 | TBD | Pending |
| API-02 | TBD | Pending |
| LATEST-01 | TBD | Pending |
| LATEST-02 | TBD | Pending |
| FILTER-01 | TBD | Pending |
| TEST-01 | TBD | Pending |
| TEST-02 | TBD | Pending |

**Coverage:**
- v1 requirements: 7 total
- Mapped to phases: 0
- Unmapped: 7 ⚠️

---
*Requirements defined: 2025-02-05*
*Last updated: 2025-02-05 after initial definition*
