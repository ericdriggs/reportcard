# Requirements: Karate JSON Support

**Defined:** 2026-01-26
**Core Value:** Capture wall clock execution time at run level so users can see how long their CI test jobs actually took

## v1 Requirements

Requirements for initial release. Each maps to roadmap phases.

### Schema

- [x] **SCHM-01**: Add `start_time` DATETIME column to `run` table (NULLable) ✓
- [x] **SCHM-02**: Add `end_time` DATETIME column to `run` table (NULLable) ✓
- [x] **SCHM-03**: Add `KARATE_JSON` storage type to `storage_type` reference table ✓
- [x] **SCHM-04**: Regenerate JOOQ classes after schema changes ✓

### Parsing

- [x] **PARS-01**: Parse Karate summary JSON (`karate-summary-json.txt`) to extract timing ✓
- [x] **PARS-02**: Extract `elapsedTime` (milliseconds) from summary JSON ✓
- [x] **PARS-03**: Extract `resultDate` (end timestamp) from summary JSON ✓
- [x] **PARS-04**: Calculate `start_time` as `resultDate - elapsedTime` ✓
- [x] **PARS-05**: Handle missing/malformed Karate JSON gracefully (log warning, continue) ✓

### API

- [x] **API-01**: Accept optional `karate.tar.gz` multipart parameter alongside JUnit XML ✓
- [x] **API-02**: Validate at least one test result format present (JUnit or Karate) ✓
- [x] **API-03**: Store Karate tar.gz contents in S3 with KARATE_JSON storage type ✓
- [x] **API-04**: Persist `start_time` and `end_time` to run record when Karate JSON present ✓
- [x] **API-05**: Existing JUnit-only uploads continue working unchanged (backwards compatible) ✓

### Client

- [x] **CLNT-01**: Add optional `karateJsonFile` parameter to Java client upload builder ✓
- [x] **CLNT-02**: Client handles multipart with both JUnit and Karate tar.gz files ✓
- [x] **CLNT-03**: Client works with server whether Karate parameter is sent or not ✓

### Display

- [ ] **DISP-01**: Show "Job Duration" (end_time - start_time) in run detail view
- [ ] **DISP-02**: Show "Test Execution Time" (existing time field) alongside Job Duration
- [ ] **DISP-03**: Gracefully handle NULL timing (display "N/A" or omit section)

## v2 Requirements

Deferred to future release. Tracked but not in current roadmap.

### Analytics

- **ANLYT-01**: Show overhead calculation (Job Duration - Test Execution Time)
- **ANLYT-02**: Duration trends over time (graph job duration per run)
- **ANLYT-03**: Slowest tests dashboard widget

### Extended Format Support

- **EXT-01**: Parse individual Karate feature JSON files (not just summary)
- **EXT-02**: Map Karate scenarios to test cases for detailed breakdown

## Out of Scope

Explicitly excluded. Documented to prevent scope creep.

| Feature | Reason |
|---------|--------|
| Standard cucumber JSON format | Karate uses proprietary format; not cucumber-jvm compatible |
| Individual test-level timing from Karate | Only run-level summary needed for job duration |
| Karate HTML report parsing | Store as-is in S3; no parsing needed |
| Real-time test streaming | Batch upload sufficient; adds significant complexity |
| Backfilling existing runs | Semantically different data; missing data is honest |
| `elapsed_time_millis` column | Derivable from end_time - start_time; cleaner schema |

## Traceability

Which phases cover which requirements. Updated during roadmap creation.

| Requirement | Phase | Status |
|-------------|-------|--------|
| SCHM-01 | Phase 1 | Complete |
| SCHM-02 | Phase 1 | Complete |
| SCHM-03 | Phase 1 | Complete |
| SCHM-04 | Phase 1 | Complete |
| PARS-01 | Phase 2 | Complete |
| PARS-02 | Phase 2 | Complete |
| PARS-03 | Phase 2 | Complete |
| PARS-04 | Phase 2 | Complete |
| PARS-05 | Phase 2 | Complete |
| API-01 | Phase 3 | Complete |
| API-02 | Phase 3 | Complete |
| API-03 | Phase 3 | Complete |
| API-04 | Phase 3 | Complete |
| API-05 | Phase 3 | Complete |
| CLNT-01 | Phase 4 | Complete |
| CLNT-02 | Phase 4 | Complete |
| CLNT-03 | Phase 4 | Complete |
| DISP-01 | Phase 5 | Pending |
| DISP-02 | Phase 5 | Pending |
| DISP-03 | Phase 5 | Pending |

**Coverage:**
- v1 requirements: 20 total
- Mapped to phases: 20
- Unmapped: 0 ✓

---
*Requirements defined: 2026-01-26*
*Last updated: 2026-01-27 after Phase 4 completion*
