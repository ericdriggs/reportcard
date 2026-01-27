# Phase 5: Dashboard Display - Context

**Gathered:** 2026-01-27
**Status:** Blocked - schema decision needed

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

<blocker>
## Blocker: Schema Decision Needed

**Issue:** Timing columns (`start_time`, `end_time`) are on the `run` table, but user wants timing at the `test_result` level (1:1 with stage).

**Why this matters:**
- A run can have multiple stages (each from a separate upload)
- Current implementation: if multiple stages provide Karate timing, last one overwrites the run's timing
- User expects each stage's timing to be independent

**Resolution options:**
1. **Migrate timing to test_result table** — requires schema change, parser update, controller update
2. **Keep run-level timing** — simpler, but "last one wins" behavior for multi-stage runs
3. **Add timing to BOTH tables** — most flexible but more complex

**Before Phase 5 can proceed:** Decide on schema location for timing data. If moving to test_result, add a new phase (5.1?) for schema migration before display work.

</blocker>

<deferred>
## Deferred Ideas

- P95 duration (for capacity planning) — add alongside average in future iteration
- Duration trends graph — visualize timing changes over time
- Overhead calculation (job duration - test execution time)

</deferred>

---

*Phase: 05-dashboard-display*
*Context gathered: 2026-01-27*
