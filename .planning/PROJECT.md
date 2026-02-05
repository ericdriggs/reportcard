# Reportcard Browse JSON API

## What This Is

Enable programmatic JSON API access to browse test results in Reportcard. The browse UI exists at `/v1/browse/*` but the equivalent JSON API at `/v1/api/*` is currently hidden and incomplete. This exposes and completes the JSON API so CI/CD pipelines and tools can programmatically fetch test results, including the latest run for any job/stage.

## Core Value

CI/CD pipelines can hit a stable JSON endpoint to get the latest test result for a specific job and stage without knowing the run ID upfront.

## Requirements

### Validated

(None yet — ship to validate)

### Active

- [ ] Remove `@Hidden` annotation from BrowseJsonController to expose `/v1/api/*` endpoints
- [ ] Add integration tests for each browse JSON endpoint (Testcontainers MySQL + LocalStack S3)
- [ ] Fix `?runs=N` filter parameter on JSON endpoints (currently only works on UI endpoints)
- [ ] Add `/run/latest` endpoint at job level — returns latest run for a job
- [ ] Add `/run/latest/stage/{stage}` endpoint — returns latest run's specific stage test results

### Out of Scope

- UI changes — this is API-only work
- Authentication/authorization changes — using existing auth model
- New data model fields — working with existing schema
- Performance optimization — functional correctness first

## Context

**Existing patterns:**
- BrowseJsonController exists at `reportcard-server/src/main/java/.../controller/browse/BrowseJsonController.java`
- Controller is marked `@Hidden` (Swagger annotation) hiding it from API docs
- BrowseUIController at `/v1/browse/*` has working patterns to reference
- "Latest" logic exists in GraphService using `MAX(RUN.RUN_ID)` grouped by job/stage
- Test base class `AbstractBrowseServiceTest` provides setup patterns

**Technical notes:**
- Spring may have path conflicts with `/run/{runId}` vs `/run/latest` — alternate paths acceptable if needed
- JSON endpoints return nested maps: `Map<CompanyPojo, Map<OrgPojo, ...>>`
- Caching layer exists in `cache/model/` package

## Constraints

- **Tech stack**: Java 17, Spring Boot 2.6.15, JOOQ, MySQL 8.0
- **Testing**: Must use Testcontainers (MySQL) + LocalStack (S3) — no external dependencies
- **Compatibility**: JSON response structure should mirror existing patterns in BrowseJsonController

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Use `/run/latest` path pattern | Matches REST conventions, intuitive | — Pending |
| Expose all existing JSON endpoints | API parity with UI browse | — Pending |

---
*Last updated: 2025-02-05 after initialization*
