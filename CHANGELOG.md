# Changelog


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
