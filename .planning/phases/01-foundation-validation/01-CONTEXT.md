# Phase 1: Foundation & Validation - Context

**Gathered:** 2025-02-05
**Status:** Ready for planning

<domain>
## Phase Boundary

Validate all existing JSON browse endpoints work correctly. Create integration tests for each endpoint at every hierarchy level using Testcontainers MySQL. Tests verify JSON serialization and error handling. No new endpoints or features — pure validation of existing code.

</domain>

<decisions>
## Implementation Decisions

### Test Organization
- Claude's discretion on test class structure (single class vs per-level)
- Shared test data setup — reuse AbstractBrowseServiceTest pattern with pre-seeded hierarchy
- Direct controller calls (existing pattern) — no MockMvc
- Claude's discretion on JSON serialization validation approach

### JSON Validation Depth
- Verify JSON serializes without error
- Verify response is non-empty for valid queries — check that known test data appears in response

### Error Scenarios
- Test 404 - Not found cases (non-existent company, org, repo, etc.)
- Test empty results (valid path but no data)
- Test invalid params (bad query parameters like ?runs=-1)
- Use ResponseDetails wrapper for consistent error format
- Verify error message text, not just status codes
- 404 if any ancestor in hierarchy is missing (company, org, repo, branch, job, run)

### Endpoint Coverage
- All hierarchy levels: company, org, repo, branch, job, run, stage
- Include SHA lookup endpoints: /sha/{sha}/run and /sha/{sha}/run/{runReference}
- Full coverage — not just core paths

### Claude's Discretion
- Test class structure (single vs multiple)
- JSON serialization validation implementation details
- Exact assertion patterns

</decisions>

<specifics>
## Specific Ideas

- Follow existing AbstractBrowseServiceTest patterns
- ?runs=N filter only relevant at branch and job levels (not all hierarchy levels)

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 01-foundation-validation*
*Context gathered: 2025-02-05*
