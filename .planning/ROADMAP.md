# Roadmap: Company & Org Jobs Dashboard

## Overview

This roadmap transforms the existing "pipelines" dashboard into a properly-named "jobs" dashboard available at both org and company levels. Phase 1 renames the org-level implementation, Phase 2 extends it to the company level with org grouping.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [x] **Phase 1: Rename Org-Level Dashboard** - Update URLs and UI from "pipelines" to "jobs"
- [ ] **Phase 2: Add Company-Level Dashboard** - Extend dashboard to company level with org grouping

## Phase Details

### Phase 1: Rename Org-Level Dashboard
**Goal**: Org-level dashboard uses "jobs" terminology in URLs and UI
**Depends on**: Nothing (first phase)
**Requirements**: URL-01, URL-02, UI-01, UI-02
**Success Criteria** (what must be TRUE):
  1. User can access jobs dashboard at `/company/{company}/org/{org}/jobs`
  2. Old `/company/{company}/org/{org}/pipelines` URLs automatically redirect to `/jobs` endpoint
  3. Dashboard UI displays "Jobs" instead of "Pipelines" in titles and navigation
**Plans**: 1 plan

Plans:
- [x] 01-01-PLAN.md — Rename URLs and UI from "pipelines" to "jobs" with redirect

### Phase 2: Add Company-Level Dashboard
**Goal**: Company-level dashboard shows jobs grouped by org
**Depends on**: Phase 1
**Requirements**: URL-03, URL-04
**Success Criteria** (what must be TRUE):
  1. User can access company-level jobs dashboard at `/company/{company}/jobs`
  2. Company dashboard displays jobs organized by org (matching org-level structure)
  3. User can filter company dashboard using `?days=N` query parameter (same as org level)
**Plans**: TBD

Plans:
- [ ] TBD (will be defined during plan-phase)

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Rename Org-Level Dashboard | 1/1 | ✓ Complete | 2026-02-11 |
| 2. Add Company-Level Dashboard | 0/TBD | Not started | - |

---
*Roadmap created: 2026-02-11*
*Last updated: 2026-02-11 after Phase 1 completion*
