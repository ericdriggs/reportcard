# Company & Org Jobs Dashboard

## What This Is

Renaming the existing "pipelines" dashboard to "jobs" and extending it to work at the company level. Currently the dashboard only exists at the org level (`/company/{company}/org/{org}/pipelines`). After this work, it will be available at both company and org levels with correct naming.

## Core Value

Users can view job metrics at both the company level (grouped by org) and org level, with URLs that accurately reflect what they're viewing.

## Requirements

### Validated

(None yet — ship to validate)

### Active

- [ ] Rename URL path from `/pipelines` to `/jobs` at org level
- [ ] Rename UI labels/text from "Pipelines" to "Jobs"
- [ ] Add redirect from old `/pipelines` URLs to `/jobs` for backwards compatibility
- [ ] Add company-level jobs endpoint at `/company/{company}/jobs`
- [ ] Company-level dashboard shows jobs grouped by org
- [ ] Support existing `?days=N` query parameter at company level

### Out of Scope

- Code internals rename (method names, class names, variables) — unnecessary churn
- Database schema changes — "pipeline" terminology in DB is fine
- New functionality beyond what exists — this is a rename + scope expansion

## Context

- Existing "pipelines" dashboard at org level works correctly, just named wrong
- This is part of the Reportcard test metrics system (Java 17, Spring Boot, JOOQ, MySQL)
- Codebase already mapped in `.planning/codebase/`
- The dashboard shows job-level test result trends over configurable time periods

## Constraints

- **Tech stack**: Java 17, Spring Boot 2.6.15, JOOQ, MySQL 8.0 — existing stack
- **Backwards compatibility**: Old `/pipelines` URLs must redirect to `/jobs`
- **Consistency**: Company-level dashboard should match org-level look and feel

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Redirect old URLs instead of removing | Avoid breaking existing bookmarks/links | — Pending |
| Group by org at company level | User wants to see org context when viewing company-wide | — Pending |
| Don't rename code internals | Unnecessary churn, DB still uses "pipeline" terminology | — Pending |

---
*Last updated: 2025-02-11 after initialization*
