# Changelog

## [0.2.0] - 2026-05-28 - Dashboard endpoints, trend API, and performance

Major additions to the JSON API surface: org/repo dashboards, flat denormalized endpoints, jobInfo natural-key routing, JSON trend endpoint with chunked fallback, and significant query performance improvements.

### Added

**Dashboard Endpoints**
- JSON org dashboard at `/json/company/{company}/org/{org}/dashboard` with repo/branch/days filtering (#146)
- Repo dashboard at `/json/repo/{repoName}/dashboard` and `/repo/{repoName}/dashboard` — query by repo name across all company/org pairs (#147)
- Flat dashboard endpoints (`/json/repo/{repoName}/dashboard/flat`, `/json/company/{company}/org/{org}/dashboard/flat`) returning denormalized stage-level entries with storage URLs (#151, #152)
- Repo dashboard jobInfo filtering at `/json/repo/{repoName}/jobinfo/{jobInfo}/dashboard/flat` and HTML equivalent (#151)

**JobInfo Natural-Key Endpoints**
- 9 new endpoints across Browse JSON/UI and Graph JSON/UI controllers allowing access by jobInfo natural key (e.g., `application=fooapp,host=foocorp.jenkins.com,pipeline=foopipeline`) instead of requiring internal jobId lookup (#150)
- 20 new tests covering jobInfo endpoint equivalence with jobId variants (#150)

**JSON Trend Endpoint**
- `/json/.../stage/{stage}/trend` endpoints (by jobId and by jobInfo) returning sparse matrix of test case results across recent runs (#153)
- Progressive disclosure via `detail` (summary/full) and `onlyShowFailures` query params (#153)
- `runs` query parameter to control how many runs to include (#156)
- OpenAPI `@Operation` summaries and `@Parameter` descriptions on all `BrowseJsonController` endpoints (#153)

**Chunked Fallback for Trend**
- When primary `JSON_ARRAYAGG` query fails (packet-too-large), service fetches skeleton graph then retrieves test suites individually in parallel (#156)
- `JobStageTestTrend.usedFallback` field signals degraded fetch to JSON consumers (#156)
- Per-future `.exceptionally()` and 60s timeout on parallel fetches (#156)

### Changed

**Performance**
- Exclude `test_suites_json` (~100KB+ per stage) from branch/job stage view queries and dashboard responses — reduces org dashboard payload from ~113KB to ~32KB (#146, #148)
- Reusable field lists `STAGE_VIEW_FIELDS_ALL` and `STAGE_VIEW_FIELDS_EXCLUDE_TEST_RESULT_JSON` for query variants (#148)

**Metrics Dashboard UI**
- Flattened 3-row-per-entity layout into single row with inline delta annotations (#155)
- Added `data-sort` attributes for client-side column sorting (#155)
- CSS `data-tooltip` tooltips (instant, no hover delay) replacing browser-native `title` tooltips (#155)
- Period summary section above first table; conditional Job Time Avg column hiding (#155)
- Removed `intervalCount` query parameter (hardcoded to 2 periods) (#155)
- `+∞%↑` / `-∞%↓` display when previous period is null (#155)

**Security & Validation**
- Strip HTML-unsafe characters (`<>"'&`) from all user-supplied entity names and jobInfo keys/values in `StageDetails` validation (#154)
- SQL injection hardening in `SqlJsonUtil.jobInfoContainsKeyValue` — bind parameters for values, regex-validated inlined keys (#151)
- HTML-escape user-provided values in rendered dashboard output (#147)
- `days` parameter clamped to minimum 1; `jobInfo` format validation returns 400 on invalid input (#151)

### Fixed

- Filter empty repos from org dashboard to prevent rendering empty entries (#149)
- Filter jobs with no runs from org dashboard HTML (#154)
- NPEs in `TestRowSummary` when rows or failure maps are null (#153)
- Precision loss in `SuccessAverage` — switched from float division to `BigDecimal.divide` (#153)
- Null-safe `getTestSuitesGraph` returns `emptyList()` instead of NPE on null JSON (#156)
- Testcontainers upgraded from 1.20.0 to 1.21.4 to fix Docker Desktop 4.67+ API version negotiation (#146)

---

## [0.1.28] - 2026-03-18 - Cucumber tar.gz download and clean display names

Store original cucumber HTML tar.gz archive for download alongside expanded HTML. Improve storage link display names and add download support for tar.gz files.

### Added

- **Cucumber tar.gz archive storage**: When `cucumber_html` reports are uploaded, the original tar.gz is stored as a separate `cucumber_html_tar_gz` record, enabling archive download
- **Download link rendering**: Tar.gz storage types render with a download tooltip in the browse UI
- **`StorageType.toLabel()`**: Helper to generate derived labels (e.g., `cucumber_html` → `cucumber_html_tar_gz`)
- **Tests**: `BrowseHtmlHelperTest` (11 unit tests), `StorageTypeTest`, updated integration tests

### Changed

- **Display names**: Storage links now show `indexFile` when present (e.g., `index.html`, `storage.tar.gz`) instead of raw labels (e.g., `cucumber_html_tar_gz`). Falls back to label for legacy records with null `indexFile`
- **indexFile for junit/karate**: `storeJunit()` and `storeKarate()` now set `indexFile` to `junit.tar.gz` and `karate.tar.gz` respectively
- **Link styling**: Added `white-space: nowrap` to keep link icon+text together when wrapping; removed `<br>` tags between links

---

## [0.1.27] - 2026-03-11 - Fix Trend Report NPE for Null testStatusFk

Fixes a production bug where trend reports failed with `NullPointerException` caused by null `testStatusFk` values. Root cause: Lombok's `@SuperBuilder` bypasses setter methods, so `testStatusFk` wasn't synced when `testStatus` was set during Karate test ingestion.

### Fixed

- **Trend Report NPE**: Fixed `NullPointerException` in trend reports
  - Root cause: Lombok `@SuperBuilder` bypasses setters, so `testStatusFk` wasn't synced when `testStatus` was set in `KarateCucumberConverter`
  - Added `@JsonCreator` factory method to `TestCaseGraph` to derive `testStatusFk` from `testStatus` string during JSON deserialization (handles existing data)
  - Added null handling in `TestStatus.testStatusNameFromStatusId()` to accept `Byte` instead of primitive `byte`
  - Added null checks in `TestTrendTable`, `SuccessAverage`, `JobStageTestTrend`, and `GraphUIController`
  - Added defensive null handling in `TestCaseModel.getResultCount()` and `setTestStatus()`

- **Division by Zero**: Fixed `ArithmeticException` in `SuccessAverage.successPercent()` when `totalCount` is zero

- **Empty Hierarchy Handling**: Fixed NPE when job hierarchy (runs, stages, etc.) is empty in trend analysis

### Changed

- **Code Simplification**: Refactored `JobStageTestTrend` to reduce nesting from 8 levels to 5, extracted helper methods `getSingleOrNull()` and `findStageName()`

### Added

- **Logging**: Added warning logs for silent failure cases:
  - Unknown `testStatus` strings in `TestCaseGraph.fromJson()`
  - Skipped test cases with null `testStatusFk` in `TestTrendTable`

### Documentation

- Added "Lombok Gotchas" section to CLAUDE.md documenting builder bypass issue
- Added "Data Safety Rules" section to CLAUDE.md about not committing tokens/credentials

---

## [0.1.26] - Karate JSON Support for run duration and tags

Added Karate JSON support to capture wall clock execution time and scenario tags from test runs. Karate DSL outputs timing data (elapsedTime, totalTime, resultDate) that JUnit XML lacks, enabling tracking of how long CI jobs actually take to run.

### Added

**Timing Support**
- `start_time` and `end_time` columns on test_result table for wall clock timing
- `KARATE` storage type (ID 9) for Karate JSON artifacts
- KarateConvertersUtil parser for extracting timing from Karate JSON summaries
- KarateTarGzUtil for extracting `karate-summary-json.txt` from tar.gz uploads
- Average run duration (`avgRunDuration`) in dashboard metrics
- Optional `karateTarGz` multipart parameter in upload endpoint

**Tag Query System**
- Extract and store scenario tags from Karate JSON (`scenarioResults[].tags`)
- Boolean expression parser for tag queries (`smoke AND env=prod`, `a OR b`)
- Tag query REST API at all hierarchy levels (company, org, repo, branch, SHA)
- Tag query HTML interface with search forms and hierarchical results
- XSS protection on all rendered tag content
- Tag transformation: @ stripping, whitespace removal, comma expansion (`env=dev,test` → `env=dev`, `env=test`)

**Client Library**
- `KARATE_REPORT_PATH` parameter for optional Karate JSON uploads
- TarGzUtil for creating tar.gz archives from directories
- External reportcard-client-java library with optional Karate JSON upload
- WireMock-based mock testing infrastructure

### Technical Details

- Additive schema changes only (no column modifications)
- Backwards compatible: Karate JSON is optional, existing uploads unchanged
- Timing stored at test_result level for multi-stage aggregation
- Tags deduplicated with insertion order preserved (LinkedHashSet)
- Recursive descent parser with sealed interface AST hierarchy

### API Endpoints

**Upload** (existing endpoint, new optional parameter)
```
POST /v1/api/junit/storage/html/tar.gz
  - junit.tar.gz (required)
  - storage.tar.gz (required)
  - karate.tar.gz (optional)
```

**Tag Query - JSON API**
```
GET /json/{company}/tags?tags={expression}
GET /json/{company}/{org}/tags?tags={expression}
GET /json/{company}/{org}/{repo}/tags?tags={expression}
GET /json/{company}/{org}/{repo}/{branch}/tags?tags={expression}
GET /json/{company}/{org}/{repo}/{branch}/{sha}/tags?tags={expression}
```

**Tag Query - HTML UI**
```
GET /{company}/tags?tags={expression}
GET /{company}/{org}/tags?tags={expression}
GET /{company}/{org}/{repo}/tags?tags={expression}
GET /{company}/{org}/{repo}/{branch}/tags?tags={expression}
GET /{company}/{org}/{repo}/{branch}/{sha}/tags?tags={expression}
```

Tag expression syntax:
- Simple: `smoke`, `env=prod`
- AND (higher precedence): `smoke AND regression`
- OR: `smoke OR regression`
- Parentheses: `(a OR b) AND c`


High-level feature changes for Browse JSON API. See git history for detailed commits.

---

## [0.1.25] - 2026-02-09

### Browse JSON API

**Goal:** Enable programmatic JSON API access to test results for CI/CD pipelines.

**Features:**
- 12+ JSON browse endpoints exposed at `/json/*`
- Latest run resolution: `GET /job/{jobId}/run/latest`
- Stage-specific results: `GET /job/{jobId}/run/latest/stage/{stage}`
- Query parameter parity: `?runs=N` filtering matches HTML browse behavior
- 27 integration tests with Testcontainers MySQL

**Design Decisions:**
- `/run/latest` path pattern chosen for REST conventions and Spring MVC literal matching
- Latest endpoints delegate to ID-based endpoints to ensure identical response shapes
- Post-filter cache strategy for `?runs=N` to avoid cache explosion from varying N values
- Direct RUN table query for `getLatestRunId` — simpler/faster than JOIN through hierarchy

**Known Limitations:**
- No `@Operation` annotations (auto-generated Swagger docs only)
- Cache TTL strategy for latest endpoints not optimized
- Pre-existing test failures in JunitControllerTest/StorageControllerTest unrelated to this work

---

### Response DTOs

**Goal:** Transform internal `Map<Pojo, Map<Pojo, Set<Pojo>>>` structures into clean JSON responses.

**Features:**
- Response DTOs for all BrowseJsonController endpoints (7 DTOs total)
- JSON keys are now field values instead of Java `toString()` representations
- Nested wrapper structure: `{"entity": {...}, "children": [...]}`

**Design Decisions:**
- DTOs wrap existing service/cache layer returns — no internal logic changes
- Transformation happens at controller boundary only
- Consistent naming: camelCase, singular entity + plural children
- Standard Jackson `@JsonProperty` annotations — no custom serializers needed

---

### Planning Infrastructure

This branch also established `.planning/` structure for AI-assisted development:
- Codebase analysis documents in `.planning/codebase/`
- Milestone-based roadmaps with phased execution
- Research → Plan → Execute → Verify workflow
- Traceability from requirements to phases to commits
