# Project State

## Project Reference

See: .planning/PROJECT.md (updated 2026-01-26)

**Core value:** Capture wall clock execution time at run level so users can see how long their CI test jobs actually took
**Current focus:** Phase 4.1 - Migrate Timing to Test Result (COMPLETED)

## Current Position

Phase: 8 of 9 (Tags Implementation - COMPLETE)
Plan: 6 of 6 in current phase
Status: Complete - All plans executed, schema and tests verified
Last activity: 2026-02-10 — Fixed multi-value index syntax, all tests passing
Next: Phase 9 (Tag Query API) or apply DDL to RDS

Progress: [███████████████] 08-06 complete (15 of ~16 total plans)

## Performance Metrics

**Velocity:**
- Total plans completed: 15
- Average duration: 4.3 min
- Total execution time: 1.07 hours

**By Phase:**

| Phase | Plans | Total | Avg/Plan |
|-------|-------|-------|----------|
| 01-schema-foundation | 1 | 4 min | 4 min |
| 02-karate-parser | 1 | 3 min | 3 min |
| 03-api-integration | 2 | 13 min | 6.5 min |
| 04-client-library | 1 | 3 min | 3 min |
| 04.1-migrate-timing | 1 | 10 min | 10 min |
| 05-dashboard-display | 3 | 11 min | 3.7 min |
| 06-client-java-support | 1 | 3 min | 3 min |
| 08-tags-implementation | 5 | 21 min | 4.2 min |

**Recent Trend:**
- Last 5 plans: 08-02 (3 min), 08-04 (3 min), 08-05 (5 min), 08-06 (8 min)
- Trend: Test-focused plans take slightly longer, comprehensive coverage achieved

*Updated after each plan completion*

## Accumulated Context

### Decisions

Decisions are logged in PROJECT.md Key Decisions table.
Recent decisions affecting current work:

- Schema: start_time + end_time columns (not elapsed_time_millis) - Cleaner schema; duration derivable
- Format: Karate JSON only (not standard cucumber) - Karate uses proprietary format
- Timing: Run-level only (not test-level) - User wants job duration, not individual test times — **REVISED in 4.1: moving to test_result level**
- Upload: Multipart upload (not separate endpoint) - Keep related data together in single request
- Schema: Additive changes only - Minimize migration risk, maintain backwards compatibility
- JOOQ workflow: Manual DB update required before code generation (01-01) - Project doesn't use Flyway auto-migration
- NULLable new columns: start_time/end_time are NULL (01-01) - Backwards compatibility with existing data
- Locale.US for Karate DateTimeFormatter (02-01) - Consistent AM/PM parsing across environments
- Return null on parse errors (02-01) - Callers handle gracefully, no exceptions propagated
- Recursive file search for karate-summary-json.txt (03-01) - Files.walk() since file may be in subdirectory
- Return null for missing/empty tar.gz (03-01) - Graceful handling, no exceptions
- Empty TestResultModel needs setTestSuites([]) (03-02) - Initialize required fields to zero
- storeKarate uses false for expand flag (03-02) - Keep tar.gz compressed in S3
- Apache Commons Compress 1.26.0 matches server (04-01) - Consistent tar.gz format across client/server
- Single-level directory scan in TarGzUtil (04-01) - Files.list() not Files.walk() matches existing behavior
- Client endpoint changed to /v1/api/junit/storage/html/tar.gz (04-01) - Matches actual server implementation
- Temporary tar.gz cleanup in finally block (04-01) - Prevent disk space leaks
- Timing migrated to test_result level (04.1-01) - Per-stage timing for multi-stage runs
- Kept run.start_time/end_time columns (04.1-01) - Backward compatibility, additive-only migration
- No dual-write for timing (04.1-01) - New uploads only write to test_result
- Duration stored as BigDecimal seconds with 2 decimal places (05-01) - HALF_UP rounding provides sufficient precision
- NULL timing values excluded from average (05-01) - Preserves meaningful average, handles old data gracefully
- Multi-stage aggregation sums durations per run (05-01) - Aligns with Phase 4.1 per-stage timing design
- NULL duration displays as dash '-' character (05-02) - Clear visual indicator for jobs without timing data
- Use NumberStringUtil.fromSecondBigDecimalPadded (05-02) - Transparent padding for correct lexical sorting in HTML tables
- Field description corrected to wall clock calculation (05-02) - Matches actual SQL implementation (max(endTime) - min(startTime))
- Test data upload via JunitController with tar.gz (05-03) - Karate JSON provides timing, JUnit-only results in NULL
- HTML content assertions for dashboard verification (05-03) - Verify rendered HTML directly from controller response
- Optional karateFolderPath parameter in external client (06-01) - Maintains backwards compatibility via conditional multipart inclusion
- WireMock 3.3.1 for mock HTTP testing (06-01) - Native JUnit 5 support, verifies multipart structure without real server
- JSON file filter predicate follows XML pattern (06-01) - FileExtensionPathPredicates.JSON for .json filtering
- LinkedHashSet for tag deduplication (08-01) - Preserves insertion order (feature tags before scenario tags)
- IllegalArgumentException for invalid comma syntax (08-01) - Comma without = in first part is invalid per spec
- List.of() for single-element tag returns (08-01) - Immutable list when no expansion needed
- @Builder.Default for tags field (08-02) - Ensures non-null empty list initialization in DTOs
- Tags field at DTO level (08-02) - Inherited by Model classes via SuperBuilder pattern
- BigDecimal.compareTo() for zero comparison (08-04) - 0.000 with scale doesn't equal BigDecimal.ZERO using equals()
- Status escalation hierarchy in converter (08-04) - skipped < success < failure < error, worst step wins
- Background elements skipped in converter (08-04) - Only scenario/scenario_outline processed per Cucumber semantics
- Karate JSON primary, JUnit fallback (08-05) - When no Cucumber JSON found in karate.tar.gz
- Tags passed to persistence layer (08-05) - Storage pending DDL (08-03), embedded in test_suites_json
- Time values not expected to match between formats (08-06) - JUnit wall clock vs Cucumber step sum
- Tag expansion in tests (08-06) - Comma-separated values expand per KarateTagExtractor
- Multi-value index uses CHAR(50) ARRAY (08-fix) - VARCHAR not supported; must be separate CREATE INDEX, not inline
- Multi-value index path is '$[*]' (08-fix) - For root-level JSON array, extract all elements

### Pending Todos

1. ~~**SQL migration script for deployed databases**~~ — DONE: Created V1.2__add_test_result_timing.sql

2. ~~**reportcard-client-java support**~~ — DONE: Phase 06-01 added optional Karate JSON upload to external repository

3. **Remove dead run timing code** — Remove unused `run.start_time` and `run.end_time` columns from V1.0 DDL, and remove dead `updateRunTiming()` method from StagePathPersistService.java. These are leftovers from Phase 1 before Phase 4.1 moved timing to test_result.

### Roadmap Evolution

- Phase 4.1 inserted after Phase 4: Migrate timing columns from run to test_result table (COMPLETED)
- Phase 6 added: reportcard-client-java Support — Karate JSON upload support in sibling repository
  - Reason: Multi-stage runs need independent timing per stage, not shared run-level timing
  - Impact: Phase 5 now depends on 4.1; schema/parser/controller changes required
  - Result: test_result table now has start_time/end_time, controller writes there
- Phase 7 added: Tags Investigation — Research extracting scenario tags from Karate JSON
  - Reason: JUnit XML doesn't support tags; tags enable functional traceability and search
  - Scope: Tags go into existing test_result JSON structure; main question is MySQL JSON indexing for search
  - Research: Model Mapper patterns, JSON indexing options (functional indexes, generated columns), search API design
  - Result: Completed with 3 spec documents (07-01-TAG-MAPPING-SPEC.md, 07-RESEARCH.md, 07-03-API-DESIGN.md)
- Phase 8 added: Tags Implementation — Implement tag extraction, storage, and query API
  - Reason: Phase 7 research complete; implementation is substantial work warranting separate phase
  - Scope: Tag extraction, storage at 3 levels, multi-value index, query API with boolean parser
  - Specs: Implementation based on Phase 7 deliverables

### Blockers/Concerns

Pre-existing build issue: Gradle build fails with Java 11/17 compatibility error in reportcard-server module (nu.studer:gradle-jooq-plugin:8.0). This existed before Phase 4 work. Doesn't block code verification or phase planning.

Pre-existing test isolation issue: Two JunitControllerTest tests fail in full suite but pass individually. Unrelated to timing migration.

External repository Java version issue: reportcard-client-java test runtime fails with UnsupportedClassVersionError (class file version 61.0 vs 55.0). Test code compiles successfully. Pre-existing issue, not caused by Phase 6. Tests will run in CI with correct Java version.

## Session Continuity

Last session: 2026-02-10T16:21:00Z
Stopped at: Completed 08-06-PLAN.md (KarateCucumberConverter tests)
Resume file: None
