# Project State

## Project Reference

See: .planning/PROJECT.md (updated 2026-01-26)

**Core value:** Capture wall clock execution time at run level so users can see how long their CI test jobs actually took
**Current focus:** Phase 4.1 - Migrate Timing to Test Result (COMPLETED)

## Current Position

Phase: 5 of 6 (Dashboard Display)
Plan: 2 of 2 in current phase
Status: Phase complete
Last activity: 2026-01-27 — Completed 05-02-PLAN.md

Progress: [█████████░] 89%

## Performance Metrics

**Velocity:**
- Total plans completed: 8
- Average duration: 5.0 min
- Total execution time: 0.67 hours

**By Phase:**

| Phase | Plans | Total | Avg/Plan |
|-------|-------|-------|----------|
| 01-schema-foundation | 1 | 4 min | 4 min |
| 02-karate-parser | 1 | 3 min | 3 min |
| 03-api-integration | 2 | 13 min | 6.5 min |
| 04-client-library | 1 | 3 min | 3 min |
| 04.1-migrate-timing | 1 | 10 min | 10 min |
| 05-dashboard-display | 2 | 7 min | 3.5 min |

**Recent Trend:**
- Last 5 plans: 04-01 (3 min), 04.1-01 (10 min), 05-01 (3 min), 05-02 (4 min)
- Trend: Excellent execution speed, Phase 5 completed cleanly

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

### Pending Todos

1. ~~**SQL migration script for deployed databases**~~ — DONE: Created V1.2__add_test_result_timing.sql

2. **reportcard-client-java support** — Add phase for sibling repository `reportcard-client-java` to support Karate JSON uploads. This is a separate codebase from the in-tree `reportcard-client` module.

3. **Remove dead run timing code** — Remove unused `run.start_time` and `run.end_time` columns from V1.0 DDL, and remove dead `updateRunTiming()` method from StagePathPersistService.java. These are leftovers from Phase 1 before Phase 4.1 moved timing to test_result.

### Roadmap Evolution

- Phase 4.1 inserted after Phase 4: Migrate timing columns from run to test_result table (COMPLETED)
- Phase 6 added: reportcard-client-java Support — Karate JSON upload support in sibling repository
  - Reason: Multi-stage runs need independent timing per stage, not shared run-level timing
  - Impact: Phase 5 now depends on 4.1; schema/parser/controller changes required
  - Result: test_result table now has start_time/end_time, controller writes there

### Blockers/Concerns

Pre-existing build issue: Gradle build fails with Java 11/17 compatibility error in reportcard-server module (nu.studer:gradle-jooq-plugin:8.0). This existed before Phase 4 work. Doesn't block code verification or phase planning.

Pre-existing test isolation issue: Two JunitControllerTest tests fail in full suite but pass individually. Unrelated to timing migration.

## Session Continuity

Last session: 2026-01-27T19:24:30Z
Stopped at: Completed 05-02-PLAN.md (Phase 5 complete)
Resume file: None
