# Phase 5: Dashboard Display - Context

**Gathered:** 2026-01-27
**Status:** Ready (blocker resolved by Phase 4.1)

<domain>
## Phase Boundary

Display job/run duration in dashboard views. Show wall clock execution time (from Karate) alongside existing test execution time (from JUnit).

</domain>

<decisions>
## Implementation Decisions

### Views to Update
- **Pipelines dashboard** (`/company/{company}/org/{org}/pipelines`) — add "Avg Run Duration" column after Test Pass %
- **Run detail view** — add duration display next to run date/SHA

### Value Formatting
- Use existing `NumberStringUtil.fromSecondBigDecimalPadded()` for human-readable format with lexical sorting
- Format: `XXh XXm XXs` with transparent padding for table sorting
- Follow existing patterns in TrendHtmlHelper for sortable columns

### NULL Handling
- Show "-" (dash) for missing timing data
- For aggregate views: calculate average from runs that have timing, ignore NULLs
- Jobs with all-NULL timing still appear in dashboard with "-" for duration
- NULLs sort to bottom (existing pattern: opacity:0 hidden "z" for sort order)

### Labels
- Column header: "Avg Run Duration" (for pipelines dashboard)
- Single run label: "Run Duration" (for run detail view)

### Claude's Discretion
- Exact column placement within existing table structure
- CSS styling to match existing columns

</decisions>

<specifics>
## Specific Ideas

- Use existing sorting patterns from TrendHtmlHelper (transparent span for lexical sort)
- Jobs dashboard is more important than run detail
- Existing `fromSecondBigDecimalPadded()` handles hours/days/years already

</specifics>

<resolved>
## Blocker Resolved

**Original Issue:** Timing columns were on the `run` table, but user wanted timing at the `test_result` level (1:1 with stage).

**Resolution:** Phase 4.1 migrated timing to test_result table:
- `test_result.start_time` (DATETIME NULL)
- `test_result.end_time` (DATETIME NULL)
- Controller now writes timing to test_result instead of run
- Per-stage timing now supported for multi-stage runs

</resolved>

<deferred>
## Deferred Ideas

- P95 duration (for capacity planning) — add alongside average in future iteration
- Duration trends graph — visualize timing changes over time
- Overhead calculation (job duration - test execution time)

</deferred>

---

*Phase: 05-dashboard-display*
*Context gathered: 2026-01-27*
