# Requirements: Company & Org Jobs Dashboard

**Defined:** 2025-02-11
**Core Value:** Users can view job metrics at both company and org levels with correct naming

## v1 Requirements

### URL Changes

- [x] **URL-01**: Rename `/pipelines` to `/jobs` at org level (`/company/{company}/org/{org}/jobs`)
- [x] **URL-02**: Add redirect from old `/pipelines` URLs to `/jobs`
- [ ] **URL-03**: Add company-level endpoint at `/company/{company}/jobs`
- [ ] **URL-04**: Support `?days=N` query parameter at company level

### UI Changes

- [x] **UI-01**: Update dashboard title from "Pipelines" to "Jobs"
- [x] **UI-02**: Update navigation links from "pipelines" to "jobs"

## v2 Requirements

(None planned)

## Out of Scope

| Feature | Reason |
|---------|--------|
| Code internals rename | Unnecessary churn, DB uses "pipeline" terminology |
| Database schema changes | Data model is fine, only presentation changing |
| New dashboard functionality | This is purely rename + scope expansion |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| URL-01 | Phase 1 | Complete |
| URL-02 | Phase 1 | Complete |
| URL-03 | Phase 2 | Pending |
| URL-04 | Phase 2 | Pending |
| UI-01 | Phase 1 | Complete |
| UI-02 | Phase 1 | Complete |

**Coverage:**
- v1 requirements: 6 total
- Mapped to phases: 6
- Unmapped: 0

---
*Requirements defined: 2025-02-11*
*Last updated: 2026-02-11 after Phase 1 completion*
