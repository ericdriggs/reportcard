# Requirements: Reportcard Browse JSON API

**Defined:** 2026-02-06
**Core Value:** CI/CD pipelines can hit a stable JSON endpoint to get the latest test result for a specific job and stage

## 0.1.24 Requirements

Requirements for Response DTO milestone. Each maps to roadmap phases.

### Response DTOs

- [ ] **DTO-01**: Create CompanyOrgsResponse DTO for `/v1/api` endpoint
- [ ] **DTO-02**: Create CompanyOrgsReposResponse DTO for `/company/{company}` endpoint
- [ ] **DTO-03**: Create OrgReposBranchesResponse DTO for `/company/{company}/org/{org}` endpoint
- [ ] **DTO-04**: Create RepoBranchesJobsResponse DTO for `/.../repo/{repo}` endpoint
- [ ] **DTO-05**: Create BranchJobsRunsResponse DTO for `/.../branch/{branch}` endpoint
- [ ] **DTO-06**: Create JobRunsStagesResponse DTO for `/.../job/{jobId}` endpoint
- [ ] **DTO-07**: Create RunStagesTestResultsResponse DTO for `/.../run/{runId}` endpoint

### JSON Quality

- [ ] **JSON-01**: All JSON keys are field values (not toString())
- [ ] **JSON-02**: Nested wrapper structure: `{"entity": {...}, "children": [...]}`
- [ ] **JSON-03**: Consistent naming convention across all DTOs

### Testing

- [ ] **TEST-01**: Existing BrowseJsonControllerTest tests pass with new response structure
- [ ] **TEST-02**: New tests validate JSON key format (no "Pojo(" in output)
- [ ] **TEST-03**: Tests verify nested structure is parseable by JSON clients

## Future Requirements

Deferred to later milestones.

### Documentation

- **DOC-01**: Add @Operation annotations to BrowseJsonController
- **DOC-02**: Update Swagger UI with response schema examples

## Out of Scope

Explicitly excluded. Documented to prevent scope creep.

| Feature | Reason |
|---------|--------|
| Service layer changes | DTOs wrap existing Map returns, don't change internal logic |
| Cache layer changes | Transformation happens at controller boundary only |
| Breaking API changes | Response structure changes but keeps equivalent data |
| Pagination | Existing unbounded sets acceptable for now |

## Traceability

Which phases cover which requirements. Updated during roadmap creation.

| Requirement | Phase | Status |
|-------------|-------|--------|
| DTO-01 | Phase 5 | Pending |
| DTO-02 | Phase 5 | Pending |
| DTO-03 | Phase 5 | Pending |
| DTO-04 | Phase 6 | Pending |
| DTO-05 | Phase 6 | Pending |
| DTO-06 | Phase 6 | Pending |
| DTO-07 | Phase 6 | Pending |
| JSON-01 | Phase 5 | Pending |
| JSON-02 | Phase 5 | Pending |
| JSON-03 | Phase 5 | Pending |
| TEST-01 | Phase 7 | Pending |
| TEST-02 | Phase 7 | Pending |
| TEST-03 | Phase 7 | Pending |

**Coverage:**
- 0.1.24 requirements: 13 total
- Mapped to phases: 13
- Unmapped: 0

---
*Requirements defined: 2026-02-06*
*Last updated: 2026-02-06 after roadmap creation*
