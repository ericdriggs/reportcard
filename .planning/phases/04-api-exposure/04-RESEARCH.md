# Phase 4: API Exposure - Research

**Researched:** 2026-02-05
**Domain:** Spring Boot REST API exposure, OpenAPI/Swagger documentation
**Confidence:** HIGH

## Summary

Phase 4 is the final phase of the browse-json feature implementation. The task is minimal but consequential: remove the `@Hidden` annotation from `BrowseJsonController` to expose the JSON API publicly via Swagger UI.

The codebase uses **springdoc-openapi-ui 1.8.0** for Swagger documentation, already configured with tag sorting (`springdoc.swagger-ui.tagsSorter: alpha`). The existing `GraphJsonController` (already exposed at `/v1/api`) demonstrates the target pattern with `@Operation` annotations on endpoints. Removing `@Hidden` will immediately expose all BrowseJsonController endpoints in Swagger UI at `/swagger-ui.html` and in the OpenAPI spec at `/v3/api-docs`.

**Primary recommendation:** Remove `@Hidden` annotation from BrowseJsonController line 25, verify Swagger UI displays all endpoints, and validate no path conflicts exist with BrowseUIController.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| springdoc-openapi-ui | 1.8.0 | OpenAPI 3 documentation generation | Already in use, Spring Boot 2.x compatible |
| io.swagger.v3.oas.annotations | (bundled) | `@Hidden`, `@Operation`, `@Parameter` annotations | Standard OpenAPI 3 annotations |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Spring Boot 2.6.15 | 2.6.15 | Web framework | Already in use |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| No `@Operation` annotations | Add `@Operation` to all endpoints | Better docs but more code changes; can be deferred |

**No installation needed:** All dependencies already present in `reportcard-server/build.gradle`.

## Architecture Patterns

### Existing Controller Pattern (GraphJsonController - model to follow)
```java
// Source: reportcard-server/src/main/java/.../controller/graph/GraphJsonController.java
@RestController
@RequestMapping("/v1/api")
public class GraphJsonController {

    @GetMapping(path = "metrics/all", produces = "application/json")
    @Operation(summary = "Get metrics using query parameters",
            description = "supports filtering and exclusion using lists...",
            operationId = "getMetricsJson")
    public ResponseEntity<TreeSet<MetricsIntervalResultCount>> getMetricsJson(...) { }
}
```

### Current BrowseJsonController Pattern (to be exposed)
```java
// Source: reportcard-server/src/main/java/.../controller/browse/BrowseJsonController.java:22-27
@RestController
@RequestMapping("/v1/api")
@Hidden  // <-- REMOVE THIS LINE
@SuppressWarnings("unused")
public class BrowseJsonController { }
```

### Pattern: Removing @Hidden
**What:** Delete the `@Hidden` annotation at class level
**When to use:** When the controller is ready for public exposure
**Result:** All endpoints become visible in Swagger UI and OpenAPI spec

### Anti-Patterns to Avoid
- **Removing @Hidden without testing path conflicts:** BrowseUIController uses root path `""` with similar endpoint patterns. Must verify no AmbiguousMappingException.
- **Adding @Operation without existing pattern:** GraphJsonController uses `@Operation` but BrowseJsonController does not. Adding all annotations in this phase creates scope creep. Can be a follow-up task.

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| API documentation | Custom docs page | springdoc-openapi Swagger UI | Already configured, auto-generates from annotations |
| Path conflict detection | Manual audit | Spring Boot startup (throws AmbiguousMappingException) | Framework detects conflicts automatically |
| OpenAPI spec | Manual JSON | `/v3/api-docs` endpoint | Auto-generated from controller annotations |

**Key insight:** The application already has full Swagger infrastructure. The only task is removing the annotation that hides BrowseJsonController.

## Common Pitfalls

### Pitfall 1: Path Conflicts Between JSON and HTML Controllers
**What goes wrong:** BrowseJsonController uses `/v1/api` prefix while BrowseUIController uses root `""`. Both have similar path patterns (`company/{company}/org/{org}...`). If `produces` attributes mismatch or paths overlap, Spring throws AmbiguousMappingException at startup.
**Why it happens:** Similar endpoint structures at different base paths; content negotiation configuration differences.
**How to avoid:**
1. BrowseJsonController already has `@RequestMapping("/v1/api")` - paths are distinct
2. All JSON endpoints have `produces = "application/json"`
3. All HTML endpoints have `produces = "text/html;charset=UTF-8"`
4. Application startup test will catch any conflicts
**Warning signs:** Application fails to start with "Ambiguous mapping" error.

### Pitfall 2: Forgetting to Test Swagger UI Access
**What goes wrong:** Removing `@Hidden` but not verifying endpoints appear in documentation. Endpoints might still be missing from Swagger UI due to other configuration issues.
**Why it happens:** Assuming annotation removal is sufficient without verification.
**How to avoid:**
1. After removal, access `/swagger-ui.html` and verify `/v1/api` browse endpoints appear
2. Check `/v3/api-docs` JSON contains BrowseJsonController operations
3. Test at least one endpoint via Swagger UI "Try it out" feature
**Warning signs:** Endpoints not appearing in Swagger UI despite annotation removal.

### Pitfall 3: Missing @Operation Annotations Leading to Poor Documentation
**What goes wrong:** Endpoints appear in Swagger UI but lack descriptions, making the API hard to use.
**Why it happens:** BrowseJsonController has no `@Operation` annotations (unlike GraphJsonController).
**How to avoid:** Accept this limitation for Phase 4. Adding `@Operation` to all 12+ endpoints is scope creep. Document as future enhancement.
**Warning signs:** Swagger UI shows endpoints but with auto-generated (unhelpful) descriptions.

## Code Examples

Verified patterns from the codebase:

### Removing @Hidden (the task)
```java
// BEFORE (current state - BrowseJsonController.java lines 22-27):
@RestController
@RequestMapping("/v1/api")
@Hidden
@SuppressWarnings("unused")
public class BrowseJsonController {

// AFTER (target state):
@RestController
@RequestMapping("/v1/api")
@SuppressWarnings("unused")
public class BrowseJsonController {
```

### Existing @Operation Pattern (GraphJsonController reference)
```java
// Source: GraphJsonController.java lines 51-55
@GetMapping(path = "metrics/all", produces = "application/json")
@Operation(summary = "Get metrics using query parameters",
        description = "supports filtering and exclusion using lists...",
        operationId = "getMetricsJson")
public ResponseEntity<TreeSet<MetricsIntervalResultCount>> getMetricsJson(...) { }
```

### Path Separation Verification
```java
// BrowseUIController (HTML - root path):
@RestController
@RequestMapping("")
public class BrowseUIController {
    @GetMapping(path = {"company/{company}/org/{org}"}, produces = "text/html;charset=UTF-8")
    ...
}

// BrowseJsonController (JSON - /v1/api prefix):
@RestController
@RequestMapping("/v1/api")
public class BrowseJsonController {
    @GetMapping(path = {"company/{company}/org/{org}"}, produces = "application/json")
    ...
}
```
The `/v1/api` prefix and different `produces` attributes ensure no path conflicts.

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Hide incomplete APIs with @Hidden | Expose after testing complete | Phase 4 | Safe exposure after Phase 1-3 validation |
| Manual API documentation | springdoc-openapi auto-generation | Already in place | No manual docs maintenance |

**Deprecated/outdated:**
- Nothing deprecated for this task. springdoc-openapi 1.8.0 is current for Spring Boot 2.x.

## Verification Checklist

After removing `@Hidden`:

1. **Application starts:** `./gradlew bootRun` or test startup confirms no AmbiguousMappingException
2. **Swagger UI shows endpoints:** Access `/swagger-ui.html`, verify `/v1/api` browse endpoints appear
3. **OpenAPI spec contains operations:** Access `/v3/api-docs`, verify BrowseJsonController operations present
4. **Existing tests pass:** `./gradlew test` (all 30+ BrowseJsonController tests should pass)
5. **Manual endpoint test:** Use Swagger UI "Try it out" on at least one endpoint

## Open Questions

Things that couldn't be fully resolved:

1. **Should @Operation annotations be added in Phase 4?**
   - What we know: BrowseJsonController lacks `@Operation` annotations, GraphJsonController has them
   - What's unclear: Is complete API documentation required for Phase 4 success criteria?
   - Recommendation: Defer to future phase. Phase 4 success criteria focuses on visibility, not documentation quality.

2. **Should path conflict tests be added?**
   - What we know: Existing tests don't use MockMvc with Accept headers to verify routing
   - What's unclear: Whether additional HTTP-layer tests are needed beyond startup validation
   - Recommendation: Application startup implicitly tests for path conflicts. Additional MockMvc tests optional.

## Sources

### Primary (HIGH confidence)
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java` - Current state with @Hidden annotation
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphJsonController.java` - Reference pattern for @Operation annotations
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/build.gradle` - springdoc-openapi-ui 1.8.0 dependency
- `/Users/eric.r.driggs/github/ericdriggs/reportcard-browse-json/reportcard-server/src/main/resources/application.properties` - Swagger UI configuration

### Secondary (MEDIUM confidence)
- springdoc.org official documentation - @Hidden annotation behavior confirmed
- Project PITFALLS.md research - Path conflict risks documented

### Tertiary (LOW confidence)
- None - all findings verified against codebase

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - verified in build.gradle (springdoc-openapi-ui 1.8.0)
- Architecture: HIGH - verified in existing controllers (GraphJsonController pattern)
- Pitfalls: HIGH - verified against prior PITFALLS.md research and codebase analysis

**Research date:** 2026-02-05
**Valid until:** 90 days (stable, minimal change expected in this domain)
