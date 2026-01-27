# Requirements: Karate JSON Support

**Defined:** 2026-01-26
**Core Value:** Capture wall clock execution time at run level so users can see how long their CI test jobs actually took

## v1 Requirements

Requirements for initial release. Each maps to roadmap phases.

### Schema

- [ ] **SCHM-01**: Add `start_time` DATETIME column to `run` table (NULLable)
- [ ] **SCHM-02**: Add `end_time` DATETIME column to `run` table (NULLable)
- [ ] **SCHM-03**: Add `KARATE` storage type to `storage_type` reference table
- [ ] **SCHM-04**: Regenerate JOOQ classes after schema changes

### Parsing

- [ ] **PARS-01**: Parse Karate summary JSON (`karate-summary-json.txt`) to extract timing
- [ ] **PARS-02**: Extract `elapsedTime` (milliseconds) from summary JSON
- [ ] **PARS-03**: Extract `resultDate` (end timestamp) from summary JSON
- [ ] **PARS-04**: Calculate `start_time` as `resultDate - elapsedTime`
- [ ] **PARS-05**: Handle missing/malformed Karate JSON gracefully (log warning, continue)

### API

- [ ] **API-01**: Accept optional `karate.tar.gz` multipart parameter alongside JUnit XML
- [ ] **API-02**: Validate at least one test result format present (JUnit or Karate)
- [ ] **API-03**: Store Karate tar.gz contents in S3 with KARATE storage type
- [ ] **API-04**: Persist `start_time` and `end_time` to run record when Karate JSON present
- [ ] **API-05**: Existing JUnit-only uploads continue working unchanged (backwards compatible)

### Client

- [ ] **CLNT-01**: Add optional `karateJsonFile` parameter to Java client upload builder
- [ ] **CLNT-02**: Client handles multipart with both JUnit and Karate tar.gz files
- [ ] **CLNT-03**: Client works with server whether Karate parameter is sent or not

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
| SCHM-01 | Phase 1 | Pending |
| SCHM-02 | Phase 1 | Pending |
| SCHM-03 | Phase 1 | Pending |
| SCHM-04 | Phase 1 | Pending |
| PARS-01 | Phase 2 | Pending |
| PARS-02 | Phase 2 | Pending |
| PARS-03 | Phase 2 | Pending |
| PARS-04 | Phase 2 | Pending |
| PARS-05 | Phase 2 | Pending |
| API-01 | Phase 3 | Pending |
| API-02 | Phase 3 | Pending |
| API-03 | Phase 3 | Pending |
| API-04 | Phase 3 | Pending |
| API-05 | Phase 3 | Pending |
| CLNT-01 | Phase 4 | Pending |
| CLNT-02 | Phase 4 | Pending |
| CLNT-03 | Phase 4 | Pending |
| DISP-01 | Phase 5 | Pending |
| DISP-02 | Phase 5 | Pending |
| DISP-03 | Phase 5 | Pending |

**Coverage:**
- v1 requirements: 20 total
- Mapped to phases: 20
- Unmapped: 0 ✓

---
*Requirements defined: 2026-01-26*
*Last updated: 2026-01-26 after schema decision (start_time + end_time)*
