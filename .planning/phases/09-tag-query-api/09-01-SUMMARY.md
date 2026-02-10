---
phase: 09-tag-query-api
plan: 01
subsystem: api
tags: [parser, ast, boolean-expression, recursive-descent, tdd]

# Dependency graph
requires:
  - phase: 07-tags-investigation
    provides: API design spec with query syntax grammar
provides:
  - TagExpressionParser recursive descent parser
  - TagExpr sealed interface AST hierarchy
  - SimpleTag, AndExpr, OrExpr record types
  - ParseException for invalid inputs
affects: [09-02-sql-generation, 09-03-tag-query-controller]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - Sealed interface hierarchy for AST nodes
    - Record types for immutable AST nodes
    - Recursive descent parsing pattern

key-files:
  created:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/TagExpressionParser.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/TagExpr.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/SimpleTag.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/AndExpr.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/OrExpr.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/ParseException.java
    - reportcard-server/src/test/java/io/github/ericdriggs/reportcard/persist/tags/TagExpressionParserTest.java
  modified: []

key-decisions:
  - "AND/OR operators are case-sensitive (lowercase 'and'/'or' are valid tag names)"
  - "Tags support: letters, digits, underscore, hyphen, equals, colon, dot"
  - "Sealed interface with records for type-safe AST pattern matching"

patterns-established:
  - "Recursive descent parser: expr -> term (OR term)*, term -> factor (AND factor)*"
  - "AST nodes as records for immutable, equals/hashCode/toString free"

# Metrics
duration: 5min
completed: 2026-02-10
---

# Phase 9 Plan 1: Boolean Expression Parser Summary

**Recursive descent parser for tag queries with AND/OR operators, parentheses, and proper precedence (AND > OR)**

## Performance

- **Duration:** 5 min
- **Started:** 2026-02-10T18:31:16Z
- **Completed:** 2026-02-10T18:36:32Z
- **Tasks:** 2 (TDD RED + GREEN)
- **Files created:** 7

## Accomplishments

- Created TagExpressionParser with recursive descent implementation
- Implemented TagExpr sealed interface hierarchy (SimpleTag, AndExpr, OrExpr)
- ParseException for comprehensive error handling
- 40+ comprehensive unit tests covering all grammar rules
- TDD approach: tests first, then implementation

## Task Commits

Each task was committed atomically (TDD pattern):

1. **Task 1: RED - Failing tests** - `9509c6d` (test)
   - TagExpressionParserTest with 40+ test cases
   - Stub implementations for compile but fail
2. **Task 2: GREEN - Implementation** - `2c73e46` (feat)
   - Full recursive descent parser implementation
   - All tests pass

## Files Created

- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/TagExpressionParser.java` - Recursive descent parser (240 lines)
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/TagExpr.java` - Sealed interface for AST
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/SimpleTag.java` - Leaf node (tag identifier)
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/AndExpr.java` - AND binary expression
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/OrExpr.java` - OR binary expression
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/tags/ParseException.java` - Parse error exception
- `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/persist/tags/TagExpressionParserTest.java` - Unit tests (586 lines)

## Test Coverage

Tests cover all grammar rules and edge cases:
- Simple tags: `smoke`, `env=prod`, `smoke-test`, `test123`
- AND expressions: `smoke AND regression`, chained `a AND b AND c`
- OR expressions: `smoke OR regression`, chained `a OR b OR c`
- Precedence: `a OR b AND c` parses as `a OR (b AND c)`
- Parentheses: `(a OR b) AND c` overrides precedence
- Nested: `((a AND b) OR c) AND d`
- Invalid inputs: empty, leading/trailing operators, unbalanced parens, empty parens
- Edge cases: lowercase `and`/`or` as tags, multiple equals signs, deeply nested

## Decisions Made

- **AND/OR case-sensitive:** Only uppercase AND/OR are operators. Lowercase `and`/`or` are valid tag names.
- **Tag character set:** Letters, digits, underscore, hyphen, equals, colon, dot. Stops at whitespace or parentheses.
- **Sealed interface:** Java 17 sealed interface pattern for type-safe AST matching.
- **Records for AST nodes:** Immutable with free equals/hashCode/toString.

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None - TDD workflow proceeded smoothly.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- TagExpressionParser ready for SQL generation (09-02)
- AST visitor pattern support for SQL conversion
- Parser can be used directly: `TagExpressionParser.parse("smoke AND env=prod")`

---
*Phase: 09-tag-query-api*
*Plan: 01*
*Completed: 2026-02-10*
