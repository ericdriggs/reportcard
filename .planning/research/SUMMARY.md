# Project Research Summary

**Project:** Karate JSON Support for Reportcard Test Result API
**Domain:** Test result metrics aggregation system with multipart upload integration
**Researched:** 2026-01-26
**Confidence:** HIGH (architecture patterns verified in existing codebase; Karate JSON structure requires validation)

## Executive Summary

Adding Karate JSON support to Reportcard is a low-risk, well-scoped extension to an existing multipart upload API. The system already contains proven patterns for parsing multiple test formats (JUnit, Surefire, TestNG) and has built infrastructure for file storage and database persistence. Karate JSON introduces minimal new complexity: Jackson JSON parsing (already in use), a new converter component (mirroring existing XML converters), and optional multipart handling (standard Spring Boot pattern). The main value delivered is capturing wall clock execution time and distinguishing it from test execution time, which is industry-standard in CI/CD reporting.

**Key recommendation:** Implement in 4 phases (Converter → Storage Type → Controller → Documentation), leveraging existing architectural patterns with no breaking changes to current clients. The approach is additive and backwards-compatible. **Primary risk:** Manual schema migration drift (Flyway is disabled), requiring strict discipline on JOOQ regeneration after schema changes. **Mitigation:** Document mandatory sequence and consider adding CI build checks.

## Key Findings

### Recommended Stack

**Zero new required dependencies.** The existing Jackson infrastructure (version 2.13.x via Spring Boot 2.6.15) handles all JSON parsing needs. Date parsing uses native Java 17 `java.time` API. Optional: `json-schema-validator` (1.5.3) for validation, but not required for MVP.

**Core technologies:**
- **Jackson Databind** — JSON parsing and POJO mapping — Already present, battle-tested with Surefire/TestNG converters
- **Java 17 DateTimeFormatter** — Parse Karate's date format (`yyyy-MM-dd hh:mm:ss a`) — Native API, thread-safe, no external library needed
- **Existing test infrastructure** — JUnit 5, json-unit assertions, Testcontainers MySQL, LocalStack S3 — All proven for multipart test scenarios

**Decision:** Use existing Jackson infrastructure. Skip optional schema validation for MVP. Follow patterns established in `SurefireConvertersUtil.java` and `TestResultModel.java`.

### Expected Features

**Must have (table stakes):**
- Sum of test execution time (already exists in `test_result.time`, `test_suite.time`)
- Timestamp of when tests ran (already exists in `test_result_created`)
- Suite-level timing rollup (already exists)
- Dashboard display of test duration (UI presentation, existing time field)

**Should have (competitive differentiators):**
- **Wall clock time (job duration)** — Shows actual elapsed time including parallelization overhead and setup/teardown — This is the Karate integration goal
- Distinguish execution time vs wall clock time — Answers "Why did my job take 10min when tests only ran 3min?"
- Gap calculation (wall_clock - execution_time) — Reveals infrastructure overhead, not a separate feature
- Slowest tests dashboard widget — Uses existing `test_case.time` data (low-hanging fruit for future)

**Defer to v2+:**
- Duration percentiles (P50, P95, P99) — requires trend aggregation logic
- Real-time test streaming — batch upload is sufficient
- Stage-level timing breakdown — defer until run-level timing proves valuable
- Timeout tracking and estimated completion time — scope creep

**MVP scope:** Capture Karate's `elapsedTime`, `totalTime`, `resultDate` fields from summary JSON. Store wall clock time at run level. Preserve existing execution time. Defer trend analysis and UI enhancements.

### Architecture Approach

The existing multipart upload pattern is extensible: HTTP controller → Format-specific parser → Unified internal model → Service layer → Database/Storage. Karate JSON integrates as a parallel parser (alongside JUnit/Surefire), producing the same `TestResultModel` output. **No schema changes needed** — Karate features map naturally to existing test_suite/test_case hierarchy. **No service layer changes needed** — format-agnostic business logic already exists. **Storage type extension only** — add `StorageType.KARATE` enum and database reference.

**Major components:**
1. **REST Controller** — Accept optional `karate.tar.gz` alongside optional `junit.tar.gz`; validate at least one present
2. **Karate JSON Parser** — `KarateConvertersUtil` class, mirrors existing converter pattern, outputs `List<TestSuiteModel>`
3. **Internal Model** — `TestResultModel`, `TestSuiteModel`, `TestCaseModel` (no changes; already handles merging via `TestResultModel.add()`)
4. **Service Layer** — `TestResultPersistService` (no changes; format-agnostic)
5. **Storage Layer** — Add `StorageType.KARATE` enum value and database row

**Critical insight:** Karate feature/scenario hierarchy maps cleanly to JUnit's suite/test hierarchy. Both JSON and XML formats produce identical `TestResultModel` output. This is not a hack—it's the proven pattern already used for Surefire and TestNG.

### Critical Pitfalls

1. **JOOQ Regeneration Amnesia** — Schema changes applied to MySQL but JOOQ code not regenerated before business logic development. Causes runtime "column not found" errors or incorrect query generation. **Prevention:** Mandatory sequence: modify SQL → apply to MySQL → `./gradlew generateJooqSchemaSource` → verify generated classes → then write code. Document this in migration file header. Consider CI build check.

2. **Manual Schema Drift** — Flyway is disabled despite V*.sql naming convention, creating false expectation of automatic migration. Production schema diverges from code expectations. **Prevention:** Add explicit WARNING in migration files. Create deployment checklist item. Consider adding manual tracking table or pre-deployment schema validation script.

3. **Storage Type Enum Coordination** — `StorageType.KARATE` added to Java enum but corresponding row missing from `storage_type` table, causing foreign key constraint violations in production. **Prevention:** Synchronized checklist: (1) add enum value in Java, (2) add INSERT in DML SQL, (3) regenerate JOOQ, (4) update tests. Add validation test comparing Java enum values to database rows.

4. **Nullable Column Assumptions** — New run-level timing columns added as `NOT NULL DEFAULT`, but existing test_result rows don't receive defaults. Integration between old data (no Karate) and new data (has Karate) creates NULL scenarios. **Prevention:** Add new columns as NULLable to preserve backward compatibility. Use `@Nullable` annotations in Java. Two-phase migration if `NOT NULL` required.

5. **Cache Invalidation Blindness** — Hierarchical async cache layer not invalidated when new run-level timing data added, causing stale data in browse views. **Prevention:** Understand `AbstractAsyncCache` pattern before modifying cached entities. Cache key versioning. Cache invalidation on write for run data.

**Phase-specific warnings:**
- Schema Design: Document JOOQ regeneration sequence
- Parser Implementation: Use separate `karate.tar.gz` multipart parameter for explicit format detection (avoid ambiguity)
- Testing: Include test data seeds for new storage types in `2_data.sql`
- Client Library: Update Java client parameter names in sync with server changes

## Implications for Roadmap

Based on research, the optimal phase structure leverages existing patterns and minimizes risk through incremental validation.

### Phase 1: Karate Converter Foundation (1-2 days)
**Rationale:** Build and validate the core parsing component in isolation before touching controller or database. Reduces risk of tight coupling and allows independent unit testing.

**Delivers:**
- `KarateFeature`, `KarateScenario` POJOs (Jackson-annotated models for JSON structure)
- `KarateConvertersUtil` class (`fromJsonContents(String) → List<TestSuiteModel>`)
- `KarateJsonParseUtil` class (orchestrator for multiple JSON files)
- Unit tests with actual Karate JSON samples

**Implements:** Architecture pattern (format-specific parser)
**Avoids pitfalls:** Parser ambiguity (separate parser component, not mixed in controller), missing annotations (test deserialization with sample JSON)
**Research flag:** REQUIRES VALIDATION — Need actual Karate `.karate-json.txt` samples to verify exact JSON field names and structure. (Karate supports scenario outlines; confirm their JSON representation.)

### Phase 2: Storage Type Extension (1 day)
**Rationale:** Add database support for new storage type. Can run in parallel with Phase 1 (no dependencies between components).

**Delivers:**
- Update `db/migration/V1.1__reportcard_mysql_dml.sql` with `INSERT INTO storage_type (storage_type) VALUES ('KARATE');`
- Run `./gradlew generateJooqSchemaSource`
- Update Java `StorageType.java` enum with `KARATE` value

**Implements:** Architecture pattern (storage type extensibility)
**Avoids pitfalls:** Storage type enum coordination (synchronized Java + DB updates), JOOQ regeneration amnesia (document mandatory regeneration step)
**Research flag:** STANDARD PATTERN — Well-established enum extension process. No deeper research needed.

### Phase 3: Controller Integration (2-3 days)
**Rationale:** Integrate Karate parser into API endpoint. Depends on Phase 1 (converter must exist) and Phase 2 (storage type must exist). Heaviest risk period due to backwards compatibility concerns.

**Delivers:**
- Modify `JunitController.postStageJunitStorageTarGZ()`: make `junit.tar.gz` optional, add `karate.tar.gz` optional
- Add validation: at least one test result format required
- Implement merge logic: both formats present → combine results via `TestResultModel.add()`
- New method `storeKarate()` mirroring existing `storeJunit()` pattern
- Integration tests: existing-only (backwards compat), Karate-only, both combined, validation errors

**Implements:** Architecture patterns (optional multipart parts, format merging, S3 storage)
**Avoids pitfalls:** Multipart parameter naming conflicts (follow existing `junit.tar.gz` pattern → `karate.tar.gz`), backwards compatibility breakage (make both optional, validate in code not annotations), missing integration test scenarios (test all combinations)
**Research flag:** MINOR RESEARCH NEEDED — Verify Spring Boot 2.6.15 behavior with optional `@RequestPart` (confirm required=false semantics). Test LocalStack S3 storage with new Karate label.

### Phase 4: Run-Level Timing Schema (1-2 days, may overlap with Phase 3)
**Rationale:** Add database columns for wall clock time if not already present. Depends on Phase 2 (JOOQ regeneration). Can start in parallel with Phase 3.

**Delivers:**
- Add `run.elapsed_time_millis` (wall clock), `run.start_time`, `run.end_time` columns to schema
- Run `./gradlew generateJooqSchemaSource`
- Update `TestResultPersistService` to accept and store wall clock time (if needed)

**Implements:** Feature (wall clock time capture)
**Avoids pitfalls:** JOOQ regeneration amnesia (regenerate after schema change), nullable column assumptions (store as NULLable for backward compatibility)
**Research flag:** REQUIRES VALIDATION — Confirm exact column types and default values needed. Determine if schema changes required or if existing test_result structure sufficient for MVP.

### Phase 5: Client Library Updates (1 day)
**Rationale:** Update `reportcard-client` Java client to support new Karate multipart parameter. Follows server API changes.

**Delivers:**
- Add `karateJsonFile` optional parameter to client upload builder
- Mirror existing junit parameter handling

**Implements:** Client API consistency
**Avoids pitfalls:** Client/server parameter name mismatch (synchronized update with Phase 3)
**Research flag:** STANDARD PATTERN — Mirror existing client update patterns. No deeper research needed.

### Phase 6: Documentation and Dashboard (1 day)
**Rationale:** Document new capability and display wall clock time in UI. Last phase because behavior should be verified first.

**Delivers:**
- Update OpenAPI/Swagger annotations with new `karate.tar.gz` parameter
- Update README with Karate JSON usage examples
- Dashboard updates: display wall clock time alongside execution time
- Clear UI labeling: "Job Duration" vs "Test Execution Time"

**Implements:** Feature (UI presentation of wall clock time), avoids pitfall (timing terminology confusion)
**Research flag:** MINIMAL — Standard documentation patterns. Focus on clear labeling to avoid user confusion.

### Phase Ordering Rationale

**Sequential dependency:** Phase 1 → Phase 3 (converter must exist before controller uses it) and Phase 2 → Phase 3 (storage type must exist before controller references it)

**Parallel opportunities:** Phases 1 and 2 can develop concurrently (no dependencies between components). Phase 4 can overlap with Phase 3 (schema and code can be developed in parallel).

**Why not defer:** Parser and controller integration cannot be deferred—they're prerequisites for the core feature. Storage type extension is trivial and unblocks controller work.

**Why this order:** Isolated component development first (converter), then infrastructure (storage), then integration (controller), then optional enhancements (UI). Allows early validation and problem isolation.

### Research Flags

**Phases needing deeper research during planning:**
- **Phase 1 (Karate Converter)** — Requires actual Karate JSON samples to verify field names, types, and structure. Critical unknowns: exact field names (`scenarios` vs `elements`?), scenario outline JSON representation, error message detail format. **Action:** User must provide sample `.karate-json.txt` files before Phase 1 starts.
- **Phase 3 (Controller)** — Optional multipart handling requires integration test verification. Minor research: Spring Boot behavior with optional `@RequestPart`. **Action:** Verify with integration test during Phase 3 development.

**Phases with standard patterns (skip deeper research):**
- **Phase 2 (Storage Type)** — Enum extension is well-established pattern. JOOQ regeneration is documented. No research needed.
- **Phase 5 (Client Library)** — Mirrors existing parameter patterns. No research needed.
- **Phase 6 (Documentation)** — Standard OpenAPI/README patterns. Timing terminology is clarified by existing FEATURES.md research.

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| **Stack** | HIGH | Jackson presence verified in build.gradle and codebase. No new dependencies required. Existing patterns proven for XML parsing. |
| **Features** | HIGH | Table stakes and differentiators well-understood in test reporting domain. Karate dual-time pattern (elapsedTime vs totalTime) is industry standard. |
| **Architecture** | HIGH | Patterns derived from existing working code (JUnit/Surefire converters, multipart handling, storage layer). Not hypothetical—proven in production. |
| **Pitfalls** | HIGH | Pitfalls based on specific codebase constraints (manual JOOQ regeneration, disabled Flyway, cache layer). Extracted from CLAUDE.md documentation and code inspection. |
| **Karate JSON structure** | MEDIUM | Based on Karate documentation training data (current as of Jan 2025). Requires verification with actual output files. Scenario outlines are a known gap. |
| **Phase estimates** | MEDIUM | Ranges based on similar work (Surefire converter was ~1 day). Actual time depends on Karate JSON complexity discovered in Phase 1. |

**Overall confidence:** **HIGH for approach, MEDIUM for Karate-specific details.**

### Gaps to Address

- **Karate JSON exact field names:** Training data suggests `scenarios`, `passed`, `failed`, `duration`, `steps` but must verify against actual Karate output. **Mitigation:** Phase 1 development starts with real sample JSON.

- **Scenario outline handling:** Unclear if scenario outlines (data-driven tests) generate multiple JSON entries or single entry with iterations. **Mitigation:** Phase 1 testing includes scenario outline samples.

- **Karate error message format:** Exact structure of failure details (stack traces, assertion messages) not verified. **Mitigation:** Phase 1 testing with failed test scenarios.

- **Cache invalidation strategy:** Need to understand current `AbstractAsyncCache` behavior before modifying cached entities (run table). **Mitigation:** Phase 4 includes cache analysis and invalidation strategy.

- **Client library scope:** Unknown if `reportcard-client` exists or requires updates. **Mitigation:** Phase 5 research during planning confirms scope.

- **Production deployment:** Manual schema migration process not automated. Requires deployment coordination. **Mitigation:** Phase 4+ includes deployment checklist item for manual schema migration verification.

## Sources

### Primary (HIGH confidence)
- Existing codebase analysis:
  - `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` — Multipart handling pattern verified
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/JunitSurefireXmlParseUtil.java` — Format-specific parser pattern
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/TestResultModel.java` — Internal model with merge capability
  - `build.gradle` — Jackson 2.13.x, json-unit, JUnit 5, Testcontainers presence verified
  - `CLAUDE.md` — JOOQ generation process, manual schema management documented
  - `/reportcard-server/src/main/resources/db/migration/V*.sql` — Schema structure observed

- Karate sample files in repository:
  - `cucumber-json/karate-reports/` — Actual Karate 1.2.0 JSON samples examined (elapsedTime, totalTime, resultDate fields confirmed)

### Secondary (MEDIUM confidence)
- Karate framework documentation (authoritative but not exhaustively verified for this implementation)
- Spring Framework 5.3.x behavior documentation (matches Spring Boot 2.6.15, but live doc fetch failed)
- Industry patterns in test reporting (CircleCI, Jenkins, Allure, ReportPortal) based on training knowledge

### Tertiary (LOW confidence, needs validation)
- Scenario outline JSON structure (training data suggests one entry per iteration, needs confirmation)
- JOOQ type mapping configuration specifics (assumed based on typical JOOQ setups)
- Exact behavior of `AbstractAsyncCache` invalidation (inferred from architecture, needs code review)

---

**Research completed:** 2026-01-26
**Ready for roadmap:** Yes — Phase structure and phased approach derived from consolidated research findings. All major risks identified with mitigation strategies. Implementation can begin with Phase 1 upon receipt of Karate JSON sample files for validation.
