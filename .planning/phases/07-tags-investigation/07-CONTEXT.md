# Phase 7: Tags Investigation - Context

**Gathered:** 2026-02-09
**Status:** Ready for planning

<domain>
## Phase Boundary

Research extracting scenario tags from Karate JSON into test_result JSON structure, and indexing strategy for tag-based search. This is a research phase to inform future implementation — outputs are documented recommendations, not code.

</domain>

<decisions>
## Implementation Decisions

### Tag Structure
- Mirror Karate JSON structure in test_result JSON — preserve original format
- Tags at both feature level (maps to test suite) and scenario level (maps to test)
- Top-level tags only — exclude nested call tags (too contextual)
- Strip `@` prefix from stored tags — cleaner for queries, can add back for display

### Search Use Cases
- Support single tag match, multiple tags with AND, and multiple tags with OR
- Support key-value filtering (e.g., `env=staging`)
- Index at test_result level with granular suite/test tags for precise filtering
- Primary use case: functional traceability — find latest run with data per job by tag
- Reject runs without data when searching

### Indexing Priority
- Prioritize query performance — accept storage/write overhead for fast searches
- Expected scale: medium (10K-100K test results)
- Compare all MySQL JSON indexing approaches:
  - Functional indexes
  - Generated columns
  - Multi-value indexes
  - Junction table (normalized)
- Research should benchmark for the three primary query patterns:
  1. OR list on tag keys
  2. OR list on key=value pairs
  3. AND list on key=value pairs

### API Design
- Both simple and advanced search:
  - Query params on existing browse endpoints for simple filtering
  - Dedicated endpoint for advanced tag search
- Return results down to test level (not just run/stage summaries)
- Show tags on test_result page and stage page (1:1 relationship)
- Expression syntax for advanced queries: `smoke AND (env=staging OR env=prod)`

### Claude's Discretion
- Specific JSON path extraction patterns
- Benchmark methodology and test data generation
- Expression parser implementation approach
- Index naming conventions

</decisions>

<specifics>
## Specific Ideas

- "Functional traceability" is the key use case — tags enable tracking tests across features/capabilities
- Key-value tags like `env=staging` are first-class, not just simple string tags
- Latest run with actual data per job — don't show empty/failed uploads in traceability views

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 07-tags-investigation*
*Context gathered: 2026-02-09*
