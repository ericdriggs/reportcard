# Reportcard Browse JSON API

## What This Is

Enable programmatic JSON API access to browse test results in Reportcard. The browse UI exists at `/v1/browse/*` and the equivalent JSON API at `/v1/api/*` is now publicly exposed. CI/CD pipelines and tools can programmatically fetch test results, including the latest run for any job/stage.

## Core Value

CI/CD pipelines can hit a stable JSON endpoint to get the latest test result for a specific job and stage without knowing the run ID upfront.

## Current State (v1.0 Shipped)

**Version:** v1.0 Browse JSON API
**Shipped:** 2026-02-05
**Codebase:** ~9,800 lines of Java added (47 files modified)

**What shipped:**
- 12+ JSON browse endpoints exposed at `/v1/api/*`
- Latest run resolution: `/job/{jobId}/run/latest`
- Stage-specific results: `/job/{jobId}/run/latest/stage/{stage}`
- Query parameter parity: `?runs=N` filtering
- 27 integration tests with Testcontainers MySQL

## Requirements

### Validated

- API-01: Remove @Hidden from BrowseJsonController — v1.0
- API-02: All JSON endpoints return valid JSON — v1.0
- LATEST-01: Add /job/{id}/run/latest endpoint — v1.0
- LATEST-02: Add /run/latest/stage/{stage} endpoint — v1.0
- FILTER-01: ?runs=N parameter works on JSON endpoints — v1.0
- TEST-01: Integration tests for each browse JSON endpoint — v1.0
- TEST-02: Tests validate JSON serialization — v1.0

### Active

(None — planning next milestone)

### Out of Scope

- UI changes — this is API-only work
- Authentication/authorization changes — using existing auth model
- New data model fields — working with existing schema
- Mobile app — web-first approach

## Context

**Tech stack:** Java 17, Spring Boot 2.6.15, JOOQ, MySQL 8.0, Testcontainers
**LOC added:** 9,808 lines
**Test coverage:** 27 integration tests in BrowseJsonControllerTest

**Known issues:**
- Pre-existing test failures in JunitControllerTest and StorageControllerTest (4 tests) — unrelated to browse-json
- getBranch/getRunFromReference error handling needs improvement (uses LEFT JOINs/NPE instead of ResponseStatusException)

**Technical debt:**
- No @Operation annotations on BrowseJsonController endpoints (auto-generated Swagger docs only)
- Cache TTL strategy for latest endpoints not optimized

## Constraints

- **Tech stack**: Java 17, Spring Boot 2.6.15, JOOQ, MySQL 8.0
- **Testing**: Must use Testcontainers (MySQL) + LocalStack (S3) — no external dependencies
- **Compatibility**: JSON response structure mirrors existing patterns in BrowseJsonController

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Use `/run/latest` path pattern | Matches REST conventions, Spring MVC literal matching | Good |
| Expose all existing JSON endpoints | API parity with UI browse | Good |
| Direct RUN table query for getLatestRunId | Simpler/faster than JOIN through hierarchy | Good |
| Delegate latest endpoints to ID-based endpoints | Ensures identical response shapes | Good |
| Post-filter cache for runs parameter | Avoid cache explosion from N run values | Good |
| Remove @Hidden without adding @Operation | Minimal change scope, docs can follow | Good |
| 4-phase roadmap structure | Natural boundaries: foundation → feature → parity → exposure | Good |

---
*Last updated: 2026-02-05 after v1.0 milestone*
