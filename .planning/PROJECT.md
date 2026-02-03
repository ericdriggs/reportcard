# Reportcard: Karate JSON Support

## What This Is

Adding Karate JSON support to Reportcard to capture wall clock execution time for test runs. Karate DSL outputs `.karate-json.txt` files containing timing data (elapsedTime, totalTime, resultDate) that JUnit XML lacks. This enables tracking how long CI jobs actually take to run tests, separate from the sum-of-test-times already captured.

## Core Value

Capture wall clock execution time at the run level so users can see how long their CI test jobs actually took, not just the sum of individual test durations.

## Requirements

### Validated

<!-- Existing capabilities from codebase -->

- ✓ Parse JUnit/Surefire/TestNG XML test results — existing
- ✓ Store test results in hierarchical structure (Company → Org → Repo → Branch → Job → Run → Stage) — existing
- ✓ Upload and store test artifacts (HTML, XML, tar.gz) in S3 — existing
- ✓ Browse test results via JSON and HTML endpoints — existing
- ✓ Track test trends and graphs over time — existing
- ✓ Sum-of-test-times duration from JUnit XML (`time` field) — existing
- ✓ Java client library for API integration — existing

### Active

<!-- New requirements for this milestone -->

- [ ] Parse Karate JSON (`.karate-json.txt`) summary files
- [ ] Extract timing data: `elapsedTime`, `totalTime`, `resultDate`
- [ ] Store wall clock timing at run level (new schema columns)
- [ ] Store Karate JSON tar.gz in S3 for archival
- [ ] Accept Karate JSON as optional multipart parameter alongside JUnit XML
- [ ] Update Java client to support uploading Karate JSON
- [ ] Display wall clock timing in dashboard views
- [ ] Backwards compatible: Karate JSON is optional, existing uploads unchanged
- [ ] Extract and store scenario tags from Karate JSON for searchability and functional traceability

### Out of Scope

- Standard cucumber JSON format — Karate uses its own format, not cucumber-jvm schema
- Individual test-level timing from Karate — only run-level summary needed
- Karate HTML report parsing — just storing as-is in S3
- Real-time streaming of test results — batch upload only

## Context

**Existing Codebase:**
- Java 17, Spring Boot 2.6.15, JOOQ, MySQL 8.0, AWS S3
- Database-first design with JOOQ code generation
- Hierarchical caching layer for browse queries
- Test infrastructure uses Testcontainers (MySQL 8.0.33, LocalStack 3.0.2)

**Karate Integration:**
- Using Karate DSL 1.2 for API testing
- Karate outputs `.karate-json.txt` files (not standard cucumber JSON)
- Summary file contains: `elapsedTime` (wall clock), `totalTime` (sum of all tests), `resultDate`
- Individual feature files contain `startTime`/`endTime` epoch millis
- Scenario-level tags in `scenarioResults[].tags` arrays (e.g., `["smoke", "env=staging"]`) - not available in JUnit XML

**Technical Notes:**
- Current `test_result.time` is sum of test durations from JUnit XML
- New timing goes at `run` table level to track job duration
- Schema changes should be additive (no column modifications)
- New storage_type needed for KARATE

## Constraints

- **Backwards Compatibility**: Existing API behavior must not change; Karate JSON is optional addition
- **Schema Changes**: Additive only — add columns/tables, don't modify existing
- **Karate Version**: Must work with Karate 1.2 output format
- **Tech Stack**: Stay within existing stack (Java 17, Spring Boot 2.6, JOOQ)

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Karate format only (not standard cucumber) | Karate uses proprietary JSON format; our use case is Karate-specific | ✓ Decided |
| Run-level timing (not test-level) | User wants job duration, not individual test times | ✓ Decided |
| Multipart upload (not separate endpoint) | Keep related data together in single request | ✓ Decided |
| Additive schema changes | Minimize migration risk, maintain backwards compatibility | ✓ Decided |
| `start_time` + `end_time` columns (not elapsed_time_millis) | Cleaner schema; duration derivable from end - start | ✓ Decided |

---
*Last updated: 2026-01-26 after initialization*
