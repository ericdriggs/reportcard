# Changelog

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
