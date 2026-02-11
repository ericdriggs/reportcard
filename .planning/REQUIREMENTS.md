# Requirements: Company & Org Jobs Dashboard

**Defined:** 2025-02-11
**Core Value:** Users can view job metrics at both company and org levels with correct naming

## v1 Requirements

### URL Changes

- [ ] **URL-01**: Rename `/pipelines` to `/jobs` at org level (`/company/{company}/org/{org}/jobs`)
- [ ] **URL-02**: Add redirect from old `/pipelines` URLs to `/jobs`
- [ ] **URL-03**: Add company-level endpoint at `/company/{company}/jobs`
- [ ] **URL-04**: Support `?days=N` query parameter at company level

### UI Changes

- [ ] **UI-01**: Update dashboard title from "Pipelines" to "Jobs"
- [ ] **UI-02**: Update navigation links from "pipelines" to "jobs"

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
| URL-01 | TBD | Pending |
| URL-02 | TBD | Pending |
| URL-03 | TBD | Pending |
| URL-04 | TBD | Pending |
| UI-01 | TBD | Pending |
| UI-02 | TBD | Pending |

**Coverage:**
- v1 requirements: 6 total
- Mapped to phases: 0
- Unmapped: 6

---
*Requirements defined: 2025-02-11*
*Last updated: 2025-02-11 after initial definition*
