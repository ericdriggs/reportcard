---
phase: 09-tag-query-api
plan: 02
subsystem: database
tags: [jooq, mysql, tags, boolean-parser, multi-value-index, union]

# Dependency graph
requires:
  - phase: 09-01
    provides: TagExpressionParser, AST nodes (TagExpr, SimpleTag, AndExpr, OrExpr)
  - phase: 07-tags-investigation
    provides: Research on multi-value index patterns (UNION for OR, single WHERE for AND)
provides:
  - TagQueryBuilder converts AST to JOOQ queries
  - TagQueryService coordinates parsing and query execution
  - Unit tests verify SQL generation patterns
affects: [09-03-tag-query-controller, tag-search-api]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - MEMBER OF condition for JSON array membership
    - UNION for OR queries (multi-value index compatibility)
    - Single WHERE for AND queries (index efficient)

key-files:
  created:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/TagQueryBuilder.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/TagQueryService.java
    - reportcard-server/src/test/java/io/github/ericdriggs/reportcard/persist/tags/TagQueryBuilderTest.java
    - reportcard-server/src/test/java/io/github/ericdriggs/reportcard/persist/tags/TagQueryServiceTest.java
  modified: []

key-decisions:
  - "UNION required for OR queries - single WHERE with OR does NOT use multi-value index"
  - "AND in single WHERE - index efficient, no UNION overhead"
  - "MEMBER OF condition format: {tag} MEMBER OF(tags)"

patterns-established:
  - "OR queries use UNION: each leg queries separately, results merged"
  - "AND queries use single WHERE: all conditions in one statement"
  - "Flat OR branches: nested ORs flatten to single UNION with multiple legs"

# Metrics
duration: 4min
completed: 2026-02-10
---

# Phase 9 Plan 02: TagQueryService Summary

**TagQueryBuilder converts AST to JOOQ queries using UNION for OR (multi-value index compatible) and single WHERE for AND expressions**

## Performance

- **Duration:** 4 min
- **Started:** 2026-02-10T18:45:09Z
- **Completed:** 2026-02-10T18:49:05Z
- **Tasks:** 3
- **Files modified:** 4

## Accomplishments
- TagQueryBuilder generates efficient SQL from TagExpr AST
- OR queries correctly use UNION (required for multi-value index usage)
- AND queries use single WHERE (index efficient, no UNION overhead)
- Comprehensive unit tests verify SQL generation patterns (716 lines of tests)
- TagQueryService coordinates parser and query builder

## Task Commits

Each task was committed atomically:

1. **Task 1: Create TagQueryBuilder** - `0511326` (feat)
2. **Task 2: Create TagQueryService** - `5d96538` (feat)
3. **Task 3: Create unit tests** - `5d60ca5` (test)

_Note: Tasks 1-2 were committed in a prior session, Task 3 completed the plan_

## Files Created/Modified
- `reportcard-server/src/main/java/.../persist/tags/TagQueryBuilder.java` - Converts TagExpr AST to JOOQ queries with UNION for OR
- `reportcard-server/src/main/java/.../persist/tags/TagQueryService.java` - Spring service coordinating parsing and query execution
- `reportcard-server/src/test/java/.../persist/tags/TagQueryBuilderTest.java` - Unit tests for SQL generation patterns (407 lines)
- `reportcard-server/src/test/java/.../persist/tags/TagQueryServiceTest.java` - Service integration tests (309 lines)

## Decisions Made
- **JOOQ uses parameterized queries** - Tag values appear as `?` bind variables, not literals in SQL
- **buildCondition throws for OR** - Forces callers to use buildQuery for proper UNION handling
- **Flat OR branches** - Nested OR expressions flatten to single UNION with multiple legs

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
- Initial test assertion checked for literal `'env=prod'` in SQL but JOOQ uses parameterized queries with `?` placeholders - fixed by asserting MEMBER OF pattern instead

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- TagQueryService ready for controller integration (09-03)
- Query builder handles all expression types from TagExpressionParser
- Tests verify critical index usage patterns

---
*Phase: 09-tag-query-api*
*Completed: 2026-02-10*
