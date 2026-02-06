# Roadmap: Reportcard Browse JSON API

## Milestones

- v1.0 Browse JSON API - Phases 1-4 (shipped 2026-02-05)
- **0.1.24 Response DTOs** - Phases 5-7 (in progress)

## Overview

Transform internal Map structures into clean JSON responses. The current BrowseJsonController returns nested Maps where keys are Java toString() representations of POJOs. This milestone creates Response DTOs that produce usable JSON with proper field-based keys and nested object structures. Phases progress from establishing the DTO pattern with initial endpoints, through completing all endpoint conversions, to validating JSON quality and backward compatibility.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [x] **Phase 5: Foundation DTOs** - Establish DTO pattern and convert first 3 endpoints
- [x] **Phase 6: Complete DTOs** - Convert remaining 4 endpoints using established pattern
- [ ] **Phase 7: Validation** - Test JSON quality and ensure backward compatibility

## Phase Details

### Phase 5: Foundation DTOs
**Goal**: Establish the Response DTO pattern and convert the first three endpoints to produce clean JSON
**Depends on**: Phase 4 (0.1.23 API Exposure)
**Requirements**: DTO-01, DTO-02, DTO-03, JSON-01, JSON-02, JSON-03
**Success Criteria** (what must be TRUE):
  1. GET /v1/api returns JSON with "company" object and "orgs" array (not Map keys with toString())
  2. GET /v1/api/company/{company} returns JSON with nested structure {"company": {...}, "orgs": [...]}
  3. GET /v1/api/company/{company}/org/{org} returns JSON with nested repos and branches
  4. All three endpoints use consistent naming convention (camelCase, singular entity + plural children)
  5. Jackson serializes DTOs without custom serializers (standard @JsonProperty annotations suffice)
**Plans**: 2 plans

Plans:
- [x] 05-01-PLAN.md — Create three Response DTO classes (CompanyOrgsResponse, CompanyOrgsReposResponse, OrgReposBranchesResponse)
- [x] 05-02-PLAN.md — Wire DTOs to controller endpoints and update tests

### Phase 6: Complete DTOs
**Goal**: Convert remaining four endpoints to use Response DTOs
**Depends on**: Phase 5
**Requirements**: DTO-04, DTO-05, DTO-06, DTO-07
**Success Criteria** (what must be TRUE):
  1. GET /.../repo/{repo} returns RepoBranchesJobsResponse with proper nested structure
  2. GET /.../branch/{branch} returns BranchJobsRunsResponse with jobs and runs arrays
  3. GET /.../job/{jobId} returns JobRunsStagesResponse with runs and stages
  4. GET /.../run/{runId} returns RunStagesTestResultsResponse with stages and test results
  5. All endpoints follow the pattern established in Phase 5 (entity + children structure)
**Plans**: TBD

Plans:
- [ ] 06-01: Create RepoBranchesJobsResponse and BranchJobsRunsResponse
- [ ] 06-02: Create JobRunsStagesResponse and RunStagesTestResultsResponse
- [ ] 06-03: Wire remaining DTOs to controller endpoints

### Phase 7: Validation
**Goal**: Validate JSON output quality and ensure backward compatibility
**Depends on**: Phase 6
**Requirements**: TEST-01, TEST-02, TEST-03
**Success Criteria** (what must be TRUE):
  1. All existing BrowseJsonControllerTest tests pass with new response structure
  2. No JSON keys contain "Pojo(" substring (validated by test assertions)
  3. JSON responses are parseable by standard JSON clients (ObjectMapper can deserialize)
  4. Response data is equivalent to previous Map-based responses (same information, better structure)
**Plans**: TBD

Plans:
- [ ] 07-01: Update existing tests for new response structure
- [ ] 07-02: Add JSON quality validation tests

## Progress

**Execution Order:**
Phases execute in numeric order: 5 -> 6 -> 7

| Phase | Milestone | Plans Complete | Status | Completed |
|-------|-----------|----------------|--------|-----------|
| 5. Foundation DTOs | 0.1.24 | 2/2 | Complete | 2026-02-06 |
| 6. Complete DTOs | 0.1.24 | 1/1 | Complete | 2026-02-06 |
| 7. Validation | 0.1.24 | 0/2 | Not started | - |

---
*Roadmap created: 2026-02-06*
*Milestone 0.1.24 started: 2026-02-06*
