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
- [ ] **Phase 4: Client Library** - Java client support for Karate JSON uploads
- [ ] **Phase 5: Dashboard Display** - UI presentation of job duration vs test execution time

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

**Plans**: TBD

Plans:
- [ ] 04-01: Client library updates

### Phase 5: Dashboard Display
**Goal**: UI shows job duration distinct from test execution time

**Depends on**: Phase 4 (needs complete end-to-end flow for testing)

**Requirements**: DISP-01, DISP-02, DISP-03

**Success Criteria** (what must be TRUE):
  1. Run detail view displays "Job Duration" calculated from end_time minus start_time
  2. Run detail view displays "Test Execution Time" (existing time field) alongside Job Duration
  3. UI gracefully handles NULL timing values (displays "N/A" or omits section)

**Plans**: TBD

Plans:
- [ ] 05-01: Dashboard timing display

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Schema Foundation | 1/1 | ✓ Complete | 2026-01-26 |
| 2. Karate Parser | 1/1 | ✓ Complete | 2026-01-27 |
| 3. API Integration | 2/2 | ✓ Complete | 2026-01-27 |
| 4. Client Library | 0/1 | Not started | - |
| 5. Dashboard Display | 0/1 | Not started | - |
