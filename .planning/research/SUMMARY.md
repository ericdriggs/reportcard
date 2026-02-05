# Project Research Summary

**Project:** reportcard-browse-json — Expose JSON API endpoints in Reportcard test metrics dashboard
**Domain:** Spring Boot REST API enhancement (adding JSON endpoints to existing HTML dashboard)
**Researched:** 2026-02-05
**Confidence:** HIGH

## Executive Summary

This project adds production-ready JSON API endpoints to Reportcard's existing HTML browse/graph interfaces. The research reveals that Reportcard already has experimental JSON endpoints (marked `@Hidden` in Swagger) using a parallel controller architecture: `BrowseUIController` serves HTML at root paths while `BrowseJsonController` serves JSON at `/v1/api`. The key work is exposing these endpoints safely, adding missing features like `/run/latest` path resolution, and hardening testing/documentation.

The recommended approach leverages existing patterns: continue the parallel controller architecture, reuse the service layer completely, follow the established `@SpringBootTest` integration testing pattern, and standardize error responses using the existing `ResponseDetails` wrapper. The critical risk is path conflicts between HTML and JSON endpoints during exposure, which requires careful path auditing and MockMvc testing before removing `@Hidden` annotations.

Architecture research shows service layer reuse at 100%, with both controller types sharing `BrowseService` and hierarchical async cache layers. The main technical debt is returning JOOQ POJOs directly in JSON responses (coupling API contract to database schema), though this is acceptable for internal/admin use cases. Four critical pitfalls emerged: path routing conflicts, JOOQ circular reference serialization failures, cache staleness with live data, and missing null handling consistency.

## Key Findings

### Recommended Stack

**Core technologies already in place:**
- Spring Boot 2.6.15 with Spring MVC — existing REST controllers follow `@RestController` + `@RequestMapping` patterns
- Jackson (via spring-boot-starter-web) — handles JSON serialization with `@JsonInclude(NON_EMPTY)` convention
- JOOQ-generated POJOs — service layer returns domain POJOs directly, no DTO layer currently
- Testcontainers (MySQL 8.0 + LocalStack S3) — integration tests use full Spring context with `@SpringBootTest`
- OpenAPI/Swagger — GraphJsonController has annotations, BrowseJsonController needs completion

**Key decisions from stack research:**
- **Continue @SpringBootTest pattern** — All existing tests call controllers directly (not via HTTP), focus on integration over HTTP layer testing
- **No MockMvc currently** — Zero usage in codebase, but recommended for path conflict validation
- **No custom Jackson config** — Spring Boot defaults work, per-class `@JsonInclude` customization sufficient
- **Instant for timestamps** — Jackson's JavaTimeModule auto-serializes as ISO-8601

### Expected Features

**Must have (table stakes):**
- **Latest resource resolution** — `/job/{jobId}/run/latest` pattern matching GitHub API conventions
- **Nested resource support** — `/run/latest/stage/{stage}` must work transparently
- **Same response shape** — Latest endpoints return identical structure to ID-based endpoints
- **Consistent error handling** — Standardized JSON error responses across all endpoints

**Should have (competitive advantage):**
- **Query parameter filtering** — `?runs=N` filter with validation (HTML controller has this, JSON controller missing)
- **Cache-aware resolution** — Latest endpoints integrate with existing hierarchical cache layer
- **Complete OpenAPI docs** — `@Operation` and `@Parameter` annotations matching GraphJsonController quality
- **HTTP layer testing** — MockMvc tests validating path conflicts and content negotiation

**Defer (v2+):**
- **DTO layer** — Acceptable to return JOOQ POJOs for internal use, DTOs only needed for external API exposure
- **Multiple keyword resolution** — "first", "previous", "next" patterns beyond "latest"
- **Pagination** — Current `Set<RunPojo>` responses are unbounded, but not blocking for initial exposure
- **API versioning strategy** — Current `/v1/api` prefix exists but no v2 planning needed yet

### Architecture Approach

Reportcard uses a **parallel controller pair architecture**: HTML controllers at root paths serve browser UIs, JSON controllers at `/v1/api` serve API clients. Both controller types inject identical services (`BrowseService`, `GraphService`), achieving 100% service layer reuse. Controllers differentiate via `produces` attributes (`text/html` vs `application/json`). HTML generation delegates to static helper classes, while JSON responses serialize POJOs directly via Jackson.

**Major components:**
1. **Controller Layer** — Parallel pairs (UI/JSON) with shared service dependencies, separated by base path (`""` vs `/v1/api`)
2. **Service Layer** — Domain logic returning POJOs, consumed by both HTML and JSON controllers identically
3. **Cache Layer** — Hierarchical async caches (`AbstractAsyncCacheMap` pattern), accessed by both controller types via singleton instances
4. **Helper Classes** — Stateless HTML generation utilities (not needed for JSON endpoints)
5. **Response Wrappers** — `ResponseDetails` with RFC 7807 problem details for error handling

**Key pattern:** Services return domain models (`Map<CompanyPojo, Set<OrgPojo>>`), controllers handle transformation: JSON controllers serialize directly, HTML controllers delegate to helpers. Cache layer is controller-agnostic, operating at service data level.

### Critical Pitfalls

1. **Path routing conflicts** — Root path `""` in BrowseUIController can match `/v1/api` requests if patterns overlap. Must audit all `@RequestMapping` paths, add explicit `produces` attributes, and test with MockMvc before removing `@Hidden`. Detection: build failures with `AmbiguousMappingException`.

2. **JOOQ circular reference serialization** — Nested Maps of JOOQ POJOs risk bidirectional references causing `StackOverflowError` during Jackson serialization. Must test every endpoint with real Testcontainers data, configure `ObjectMapper` with circular reference detection, or introduce DTO layer for complex structures.

3. **Cache staleness with latest endpoints** — Hierarchical cache may serve outdated run counts when new tests uploaded. Must test cache invalidation with sequential POST-then-GET operations, document cache TTL strategy, and consider shorter TTL for "latest run ID" vs full run data.

4. **Null handling inconsistency** — JOOQ POJOs use nullable wrappers (Long, Integer) serialized inconsistently depending on `ObjectMapper` config. Must standardize on `SharedObjectMappers.permissiveObjectMapper`, test with NULL database fixtures, and document nullable fields in OpenAPI schemas.

5. **Missing query validation** — BrowseUIController validates `?runs=N` parameter, but JSON controller lacks equivalent. Must reuse `validateRuns()` method, add `@Min/@Max` annotations, and test boundary values to prevent database OOM.

## Implications for Roadmap

Based on research, suggested phase structure:

### Phase 1: Foundation & Exposure Prep
**Rationale:** Must establish safety measures before exposing JSON endpoints publicly. Path conflicts (Pitfall 1) and error handling inconsistencies (Pitfall 7) are blocking issues that could break existing HTML endpoints or create poor API experience.

**Delivers:**
- Path audit comparing BrowseUIController and BrowseJsonController mappings
- Global `@RestControllerAdvice` for standardized error responses using `ResponseDetails`
- Explicit `produces` attributes on all endpoints
- TODO resolution plan (document missing features or implement)

**Addresses:**
- Path routing conflicts (PITFALLS.md Pitfall 1)
- Inconsistent error response format (PITFALLS.md Pitfall 7)
- Incomplete TODOs (PITFALLS.md Pitfall 9)

**Avoids:** Breaking existing HTML UI when removing `@Hidden` from JSON controllers

**Research flag:** Standard Spring MVC path resolution — well-documented, skip phase research

---

### Phase 2: /run/latest Implementation
**Rationale:** Core feature requirement. Implementing "latest" resolution requires defining semantics (max run_id vs timestamp), database query optimization, and cache integration strategy. This validates the approach before expanding to other endpoints.

**Delivers:**
- `/job/{jobId}/run/latest` endpoint in BrowseJsonController
- `/job/{jobId}/run/latest/stage/{stage}` nested resource support
- Database query for latest run (with index verification)
- Cache integration with shorter TTL for latest-run-id lookups
- Service layer method for reuse: `BrowseService.getLatestRunId()`

**Uses:**
- Spring MVC path matching with literal segments (`/run/latest` before `/run/{runId}` in source order)
- Existing hierarchical cache pattern (`AbstractAsyncCache`)

**Implements:**
- Dedicated `/latest` path pattern (FEATURES.md Pattern 1 from REST API research)

**Addresses:**
- Missing /run/latest resolution (PITFALLS.md Pitfall 8)
- Cache inconsistency prevention (PITFALLS.md Pitfall 4)

**Research flag:** Consider deeper research on cache invalidation patterns if implementation reveals complexity

---

### Phase 3: Testing & Serialization Hardening
**Rationale:** Existing tests use direct controller method calls, bypassing HTTP layer. Must add MockMvc tests to catch path conflicts, content negotiation issues, and serialization failures before production.

**Delivers:**
- MockMvc integration tests for JSON endpoints (verify HTTP routing)
- Serialization tests for JOOQ POJOs (detect circular references)
- Null value test fixtures (verify consistent JSON output)
- Content negotiation tests (Accept headers, produces validation)
- Query parameter validation tests (boundary values for `?runs=N`)

**Addresses:**
- Testcontainers don't cover path conflicts (PITFALLS.md Pitfall 12)
- Missing content negotiation testing (PITFALLS.md Pitfall 10)
- JOOQ circular reference risk (PITFALLS.md Pitfall 2)
- Null handling inconsistency (PITFALLS.md Pitfall 3)
- Missing query validation (PITFALLS.md Pitfall 5)

**Avoids:** Production failures from serialization bugs, path routing errors, or invalid input handling

**Research flag:** Standard Spring Boot testing patterns — well-documented, skip phase research

---

### Phase 4: OpenAPI Documentation Completion
**Rationale:** BrowseJsonController lacks `@Operation` annotations present in GraphJsonController. Clients need comprehensive API documentation before removing `@Hidden`.

**Delivers:**
- `@Operation` annotations with summaries and descriptions
- `@Parameter` annotations with example values and constraints
- `@Schema` annotations for response models (including nullable fields)
- Updated Swagger UI with complete endpoint documentation
- Documentation review comparing with GraphJsonController for consistency

**Addresses:**
- Incomplete OpenAPI documentation (PITFALLS.md Pitfall 6)

**Research flag:** Standard Swagger/OpenAPI annotation usage — skip phase research

---

### Phase 5: Remove @Hidden & Production Hardening
**Rationale:** Final validation before exposing endpoints. This phase catches any missed issues from earlier phases and ensures production readiness.

**Delivers:**
- Remove `@Hidden` annotations from BrowseJsonController
- Verify Swagger UI shows all endpoints correctly
- Load testing with concurrent requests (validate cache behavior)
- Manual testing with curl/Postman for all endpoints
- Production deployment checklist

**Addresses:**
- Final validation of all earlier phases

**Avoids:** Deploying incomplete or broken API endpoints

**Research flag:** Skip phase research — validation-focused phase

---

### Phase Ordering Rationale

**Why this order:**

1. **Foundation first (Phase 1)** — Path conflicts and error handling are architectural concerns that affect all subsequent work. Must be solid before implementation begins.

2. **Feature implementation (Phase 2)** — `/run/latest` is the core new feature. Implementing it early validates the technical approach (path patterns, cache integration, service layer changes) before expanding to other areas.

3. **Testing hardens (Phase 3)** — MockMvc tests catch issues that direct method call tests miss (path routing, serialization, HTTP layer concerns). Testing after implementation validates the work done in Phases 1-2.

4. **Documentation enables clients (Phase 4)** — OpenAPI completion happens after endpoints are stable but before public exposure, ensuring clients have good developer experience.

5. **Exposure finalizes (Phase 5)** — Removing `@Hidden` is the last step after all validation passes, minimizing risk of production issues.

**Dependency chain:**
- Phase 2 depends on Phase 1 (path resolution strategy must be safe)
- Phase 3 validates Phase 2 (tests verify implementation correctness)
- Phase 4 documents Phase 2 output (OpenAPI describes stable endpoints)
- Phase 5 exposes Phases 1-4 work (remove @Hidden only when everything ready)

**How this avoids pitfalls:**
- **Path conflicts** addressed in Phase 1 before any implementation changes
- **Serialization failures** caught in Phase 3 before production exposure
- **Cache staleness** validated in Phase 2 during /run/latest implementation
- **Documentation gaps** filled in Phase 4 before client access

### Research Flags

Phases likely needing deeper research during planning:
- **Phase 2 (cache integration)** — If cache invalidation proves more complex than expected, may need research on AbstractAsyncCache patterns and TTL strategies specific to "latest" resolution
- **None others** — Architecture and patterns are well-established in codebase

Phases with standard patterns (skip research-phase):
- **Phase 1** — Standard Spring MVC configuration and error handling patterns
- **Phase 3** — Well-documented Spring Boot testing with MockMvc and Testcontainers
- **Phase 4** — Swagger/OpenAPI annotation documentation is straightforward
- **Phase 5** — Validation and deployment phase, no research needed

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | **HIGH** | Existing patterns proven in codebase (4 JSON controller examples), Spring Boot 2.6.15 behavior well-documented |
| Features | **HIGH** | REST API "latest" resolution patterns extensively researched (GitHub API, Docker Registry examples), clear table stakes identified |
| Architecture | **HIGH** | Parallel controller architecture fully implemented in 2 feature domains (browse, graph), service layer reuse validated |
| Pitfalls | **HIGH** | 12 specific pitfalls identified from codebase analysis, each with detection signs and prevention strategies |

**Overall confidence:** HIGH

Research is comprehensive and based on actual codebase analysis (not theoretical). All four research dimensions (STACK, FEATURES, ARCHITECTURE, PITFALLS) aligned on recommended approach. No major unknowns blocking implementation.

### Gaps to Address

**During Phase 1 planning:**
- **Database index verification** — STACK.md mentions need to check if `job_id` indexed for efficient latest-run queries. Validate during Phase 2 implementation.
- **Cache TTL policy** — Current cache implementations don't document TTL strategy. Must define policy for "latest run ID" cache during Phase 2.
- **Null semantics** — Database NULL vs Java null vs JSON null handling needs consistent policy. Document in Phase 1, validate in Phase 3.

**During Phase 2 implementation:**
- **"Latest" definition** — Must choose between `max(run_id)` vs `max(created_date)` for latest resolution. Research suggests `max(run_id)` (auto-incrementing), but validate with product requirements.
- **Cache invalidation trigger** — How does cache refresh when new test results uploaded via JunitController? Must trace through upload flow during implementation.

**Post-implementation validation:**
- **JOOQ POJO DTO migration** — Long-term consideration (not blocking). If exposing to external clients, assess need for DTO layer to decouple API contract from database schema.
- **Load testing results** — Phase 5 load testing may reveal cache stampede or performance issues requiring cache strategy refinement.

## Sources

### Primary (HIGH confidence)

**Codebase analysis:**
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java` — Existing JSON controller with `@Hidden` annotation
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseUIController.java` — Parallel HTML controller showing path patterns
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphJsonController.java` — Production JSON controller with OpenAPI annotations
- `/reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/JunitControllerTest.java` — Established testing pattern with Testcontainers
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/model/ResponseDetails.java` — Error response wrapper pattern
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/BrowseService.java` — Service layer shared by both controllers
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/AbstractAsyncCacheMap.java` — Cache architecture patterns
- `CLAUDE.md` and `README_AI.md` — Project architecture documentation

**Spring Boot reference:**
- Spring Boot 2.6.15 documentation (Spring MVC 5.3.x behavior)
- Jackson 2.13.x serialization defaults (auto-registered JavaTimeModule)
- Spring Test MockMvc patterns

### Secondary (MEDIUM confidence)

**REST API patterns:**
- GitHub REST API v3 (verified `/releases/latest` pattern via direct observation)
- Docker Registry HTTP API V2 specification (reserved keyword pattern)
- Spring REST best practices (DTO patterns, error handling conventions)

**Inference from codebase:**
- Cache invalidation strategy (not explicitly documented, inferred from usage)
- "Latest" semantics (assumed auto-incrementing run_id, needs validation)
- Test startup time claims (not measured, inferred from Testcontainers usage)

### Tertiary (LOW confidence)

**Community patterns:**
- npm registry API (inferred from CLI behavior, not directly verified)
- Maven Central API (inferred from tooling, not REST-documented)
- General REST conventions (no single authoritative source)

---
*Research completed: 2026-02-05*
*Ready for roadmap: yes*
