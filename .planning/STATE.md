# Project State

## Project Reference

See: .planning/PROJECT.md (updated 2026-02-11)

**Core value:** Users can view job metrics at both the company level (grouped by org) and org level, with URLs that accurately reflect what they're viewing.
**Current focus:** Phase 2 - Add Company-Level Dashboard

## Current Position

Phase: 2 of 2 (Add Company-Level Dashboard)
Plan: 1 of 2 complete
Status: In progress
Last activity: 2026-02-11 — Completed 02-01-PLAN.md

Progress: [███████░░░] 75%

## Performance Metrics

**Velocity:**
- Total plans completed: 2
- Average duration: 7 min
- Total execution time: 0.23 hours

**By Phase:**

| Phase | Plans | Total | Avg/Plan |
|-------|-------|-------|----------|
| 01-rename-org-level-dashboard | 1/1 | 10min | 10min |
| 02-add-company-level-dashboard | 1/2 | 4min | 4min |

**Recent Trend:**
- Last plan: 02-01 (4min)
- Previous: 01-01 (10min)
- Trend: Improving - backend refactoring faster than UI changes

*Updated after each plan completion*

## Accumulated Context

### Decisions

Decisions are logged in PROJECT.md Key Decisions table.
Recent decisions affecting current work:

- Don't rename code internals (unnecessary churn, DB still uses "pipeline" terminology)
- Redirect old URLs instead of removing (avoid breaking existing bookmarks/links)
- Group by org at company level (user wants to see org context when viewing company-wide)
- Use HTTP 301 MOVED_PERMANENTLY for /pipelines redirect (01-01: permanent URL change signal)
- Preserve query parameters through redirects (01-01: critical for filtered views)
- Duplicate endpoint instead of rename (01-01: clearer backwards compatibility)
- Use trueCondition() for null org (02-01: established JOOQ pattern for optional filters)
- Guard tableConditionMap.put(ORG, ...) with null check (02-01: prevents query builder issues)
- Render company-only title when org is null (02-01: avoids "company/null" display)

### Pending Todos

None yet.

### Blockers/Concerns

None yet.

## Session Continuity

Last session: 2026-02-12T02:03:01Z
Stopped at: Completed 02-01-PLAN.md (Phase 2 Plan 1 complete)
Resume file: None

---
*State initialized: 2026-02-11*
*Last updated: 2026-02-12T02:03:01Z*
