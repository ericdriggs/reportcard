# Roadmap: Karate JSON Support

## Overview

Adding Karate JSON support to Reportcard enables capturing wall clock execution time at the run level, distinct from sum-of-test-times already tracked. This 5-phase roadmap implements schema changes, JSON parsing, controller integration, client library updates, and dashboard display. The approach is additive and backwards-compatible, leveraging existing patterns for multipart uploads and test result processing.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [x] **Phase 1: Schema Foundation** - Database changes for run timing and KARATE storage type ✓
- [x] **Phase 2: Karate Parser** - JSON parsing and timing extraction from Karate summary files ✓
- [x] **Phase 3: API Integration** - Multipart upload handling and S3 storage ✓
- [x] **Phase 4: Client Library** - Java client support for Karate JSON uploads ✓
- [x] **Phase 4.1: Migrate Timing to Test Result** - Move timing columns from run to test_result table (INSERTED) ✓
- [x] **Phase 5: Dashboard Display** - UI presentation of job duration vs test execution time ✓
- [x] **Phase 6: reportcard-client-java Support** - Karate JSON upload support in sibling repository ✓
- [ ] **Phase 7: Tags Investigation** - Research extracting scenario tags from Karate JSON for test_result storage and searchability

## Phase Details

### Phase 1: Schema Foundation
**Goal**: Database supports run-level timing and KARATE storage type

**Depends on**: Nothing (first phase)

**Requirements**: SCHM-01, SCHM-02, SCHM-03, SCHM-04

**Success Criteria** (what must be TRUE):
  1. run table has start_time column (DATETIME, NULLable)
  2. run table has end_time column (DATETIME, NULLable)
  3. storage_type table contains KARATE entry
  4. JOOQ generated classes include new columns and storage type

**Plans**: 1 plan

Plans:
- [x] 01-01-PLAN.md — Add run timing columns, KARATE storage type, regenerate JOOQ code ✓

### Phase 2: Karate Parser
**Goal**: Parse Karate JSON summary files and extract timing data

**Depends on**: Phase 1 (needs JOOQ classes for persistence layer)

**Requirements**: PARS-01, PARS-02, PARS-03, PARS-04, PARS-05

**Success Criteria** (what must be TRUE):
  1. KarateConvertersUtil parses karate-summary-json.txt to extract timing
  2. Parser correctly extracts elapsedTime (milliseconds) from summary JSON
  3. Parser correctly extracts resultDate (end timestamp) from summary JSON
  4. Parser calculates start_time as resultDate minus elapsedTime
  5. Parser handles missing or malformed JSON gracefully (logs warning, continues processing)

**Plans**: 1 plan

Plans:
- [x] 02-01-PLAN.md — KarateSummary POJO, KarateConvertersUtil parser, unit tests ✓

### Phase 3: API Integration
**Goal**: Controller accepts Karate JSON uploads and persists timing data

**Depends on**: Phase 2 (needs parser) and Phase 1 (needs storage type)

**Requirements**: API-01, API-02, API-03, API-04, API-05

**Success Criteria** (what must be TRUE):
  1. JunitController accepts optional karate.tar.gz multipart parameter
  2. Controller validates at least one test result format is present (JUnit or Karate)
  3. Controller stores Karate tar.gz in S3 with KARATE storage type
  4. Controller persists start_time and end_time to run record when Karate JSON present
  5. Existing JUnit-only uploads continue working unchanged (backwards compatible)

**Plans**: 2 plans

Plans:
- [x] 03-01-PLAN.md — Infrastructure: KarateTarGzUtil, request model update, updateRunTiming persistence method ✓
- [x] 03-02-PLAN.md — Controller integration: endpoint update, validation, timing processing, S3 storage, tests ✓

### Phase 4: Client Library
**Goal**: Java client supports uploading Karate JSON files

**Depends on**: Phase 3 (needs server API endpoint)

**Requirements**: CLNT-01, CLNT-02, CLNT-03

**Success Criteria** (what must be TRUE):
  1. Client upload builder accepts optional karateJsonFile parameter
  2. Client constructs multipart request with both JUnit and Karate tar.gz files
  3. Client works with server whether Karate parameter is sent or not

**Plans**: 1 plan

Plans:
- [x] 04-01-PLAN.md — Add KARATE_REPORT_PATH parameter, create TarGzUtil, update PostWebClient for tar.gz uploads, fix endpoint URL ✓

### Phase 4.1: Migrate Timing to Test Result (INSERTED)
**Goal**: Move timing columns from run table to test_result table for stage-level timing

**Depends on**: Phase 4 (timing currently on run table)

**Requirements**: Schema migration, JOOQ regeneration, persistence layer update, controller update

**Success Criteria** (what must be TRUE):
  1. test_result table has start_time column (DATETIME, NULLable)
  2. test_result table has end_time column (DATETIME, NULLable)
  3. Controller updates test_result timing instead of run timing
  4. Existing data handled gracefully
  5. JOOQ generated classes include new test_result columns

**Plans**: 1 plan

Plans:
- [x] 04.1-01-PLAN.md — Add timing columns to test_result, regenerate JOOQ, update persistence and controller layers ✓

### Phase 5: Dashboard Display
**Goal**: Pipelines dashboard shows job duration calculated from test_result timing

**Depends on**: Phase 4.1 (needs timing at test_result level)

**Requirements**: DISP-03 (pipelines dashboard with duration)

**Requirements Deferred**: DISP-01, DISP-02 (run detail view) - Deferred to future phase per user priority on pipelines dashboard

**Scope Note**: Phase 5 focuses on the pipelines dashboard (`/company/{company}/org/{org}/pipelines`), which is the user's primary concern. Run detail view enhancements (DISP-01, DISP-02) are deferred for a future iteration after the main dashboard is validated. This scoping reflects:
- User stated "Jobs dashboard is more important than run detail" in CONTEXT.md
- Phase 4.1 changed timing from run-level to test_result-level, requiring reconsideration of run detail view design
- Value-first delivery: Pipelines dashboard provides maximum user value for initial release

**Success Criteria** (what must be TRUE):
  1. Pipelines dashboard displays "Avg Run Duration" calculated from test_result timing
  2. Duration displays in human-readable format (XXh XXm XXs) with sortable transparent padding
  3. UI gracefully handles NULL timing values (displays "-")

**Plans**: 3 plans

Plans:
- [x] 05-01-PLAN.md — Backend data layer: TestResultGraph timing fields, GraphService query, JobDashboardMetrics calculation ✓
- [x] 05-02-PLAN.md — Frontend display: PipelineDashboardHtmlHelper rendering, column addition, field description ✓
- [x] 05-03-PLAN.md — Automated test: PipelineDashboardTest for timing display, NULL handling, field description ✓

### Phase 6: reportcard-client-java Support
**Goal**: Sibling repository reportcard-client-java supports Karate JSON uploads

**Depends on**: Phase 3 (needs server API endpoint)

**Requirements**: Add optional Karate JSON upload to external reportcard-client-java library

**Success Criteria** (what must be TRUE):
  1. reportcard-client-java upload supports optional Karate JSON parameter
  2. Client constructs multipart request with both JUnit and Karate tar.gz files
  3. Client works with server whether Karate parameter is sent or not
  4. WireMock tests verify multipart structure without real server
  5. Backwards compatible with existing client usage

**Plans**: 1 plan

Plans:
- [x] 06-01-PLAN.md — Add optional karateFolderPath to JunitHtmlPostRequest, JSON file filter, WireMock mock tests ✓

### Phase 7: Tags Investigation
**Goal**: Research extracting scenario tags from Karate JSON into test_result JSON structure, and indexing strategy for tag-based search

**Depends on**: Phase 3 (Karate JSON parsing infrastructure)

**Requirements**: Research-focused phase to inform future implementation

**Research Questions**:
  1. How to extract tags from Karate JSON `scenarioResults[].tags` arrays
  2. Model Mapper patterns for mapping Karate JSON tags into existing test_result JSON structure
  3. MySQL JSON indexing options for tag searchability (functional indexes, generated columns, etc.)
  4. Index design tradeoffs: query flexibility vs index maintenance cost vs storage
  5. API design for tag-based search queries

**Context**:
- Tags go into existing test_result JSON structure (not a separate column or table)
- JUnit XML does not support tags - this is Karate-specific metadata
- Tags enable functional traceability (e.g., `@smoke`, `@regression`, `@feature-xyz`)
- Tags can be simple (`smoke`) or key-value (`env=staging`)
- Nested call results also have tags that may need aggregation
- Main challenge: how to efficiently index JSON array for search queries

**Success Criteria** (what must be TRUE):
  1. Model Mapper patterns documented for Karate JSON → test_result JSON mapping
  2. Clear understanding of MySQL JSON indexing options and limitations
  3. Recommendation on indexing strategy (or decision to defer indexing)
  4. API design proposal for tag-based queries
  5. Decision on whether to split into multiple implementation phases

**Plans**: TBD

Plans:
- [ ] TBD (run /gsd:plan-phase 7 to break down)

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 4.1 → 5 → 6 → 7

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Schema Foundation | 1/1 | ✓ Complete | 2026-01-26 |
| 2. Karate Parser | 1/1 | ✓ Complete | 2026-01-27 |
| 3. API Integration | 2/2 | ✓ Complete | 2026-01-27 |
| 4. Client Library | 1/1 | ✓ Complete | 2026-01-27 |
| 4.1 Migrate Timing | 1/1 | ✓ Complete | 2026-01-27 |
| 5. Dashboard Display | 3/3 | ✓ Complete | 2026-02-03 |
| 6. reportcard-client-java | 1/1 | ✓ Complete | 2026-02-09 |
| 7. Tags Investigation | 0/? | Not started | - |
