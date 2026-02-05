# Spring Boot JSON API Controller Architecture

**Research Type:** Project Research — Architecture dimension for JSON API controller structure
**Date:** 2026-02-05
**Context:** Structuring JSON API endpoints alongside existing HTML endpoints in Reportcard

---

## Executive Summary

**Fact:** [BrowseUIController.java:18-21, BrowseJsonController.java:19-23, GraphUIController.java:26-30, GraphJsonController.java:22-25] — Reportcard uses **parallel controller pairs** where HTML and JSON controllers:
- Share identical path structures (relative to their base)
- Use different `@RequestMapping` base paths (HTML: `""`, JSON: `"/v1/api"`)
- Both use `@RestController` annotation
- Differentiate via `produces` parameter (`"text/html;charset=UTF-8"` vs `"application/json"`)

**Inference:** [Observed patterns across 2 controller pairs] → This architecture enables:
- Complete code reuse at service layer
- Clean separation of presentation concerns
- Independent evolution of HTML/JSON responses
- Type-safe JSON responses vs HTML template rendering

---

## 1. Controller Organization Patterns

### 1.1 Parallel Controller Pair Pattern

**Fact:** [File structure in `/controller/browse/` and `/controller/graph/`] — Controllers organized into subdirectories by feature domain:

```
controller/
├── browse/
│   ├── BrowseUIController.java      (@RestController, @RequestMapping(""))
│   ├── BrowseJsonController.java    (@RestController, @RequestMapping("/v1/api"))
│   └── BrowseHtmlHelper.java        (HTML generation)
├── graph/
│   ├── GraphUIController.java       (@RestController, @RequestMapping(""))
│   ├── GraphJsonController.java     (@RestController, @RequestMapping("/v1/api"))
│   └── *HtmlHelper.java             (HTML generation utilities)
├── html/
│   ├── StorageHtmlHelper.java
│   ├── TestResultHtmlHelper.java
│   └── ExtensionImage.java
└── JunitController.java             (@RestController, @RequestMapping("/v1/api/junit"))
    StorageController.java           (@RestController, @RequestMapping("/v1/api/storage"))
```

**Fact:** [BrowseUIController.java:32-34, BrowseJsonController.java:32-34] — Identical endpoint paths with different base mappings:

| Path | HTML Controller | JSON Controller |
|------|----------------|-----------------|
| Base | `GET /` | `GET /v1/api` |
| Company | `GET /company/{company}` | `GET /v1/api/company/{company}` |
| Org | `GET /company/{company}/org/{org}` | `GET /v1/api/company/{company}/org/{org}` |

### 1.2 Single-Purpose REST Controllers

**Fact:** [JunitController.java:32-34, StorageController.java:32-34] — Some controllers only provide JSON APIs:
- `JunitController`: `@RestController`, `@RequestMapping("/v1/api/junit")` — test result ingestion
- `StorageController`: `@RestController`, `@RequestMapping("/v1/api/storage")` — file storage operations (with embedded HTML browsing for S3 at line 120-122)

**Inference:** [Controller organization] → Use parallel pairs for **browse/read operations** where both HTML and JSON views are valuable. Use single JSON controllers for **write/operational APIs**.

---

## 2. Service Layer Reuse Strategies

### 2.1 Complete Service Layer Sharing

**Fact:** [BrowseUIController.java:23-29, BrowseJsonController.java:25-29] — Both HTML and JSON controllers inject identical services:

```java
// HTML Controller
@Autowired
public BrowseUIController(BrowseService browseService, GraphService graphService) {
    this.browseService = browseService;
    this.graphService = graphService;
}

// JSON Controller
@Autowired
public BrowseJsonController(BrowseService browseService) {
    this.browseService = browseService;
}
```

**Fact:** [BrowseService.java:33-43] — Service returns domain POJOs/models, not controller-specific DTOs:

```java
@Service
public class BrowseService extends AbstractPersistService {
    public Set<CompanyPojo> getCompanies() { ... }
    public Map<CompanyPojo, Set<OrgPojo>> getCompanyOrgs() { ... }
    public Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> getCompanyOrgsRepos(String companyName) { ... }
}
```

**Inference:** [Service method signatures] → Services return **domain models** (`Map<CompanyPojo, Set<OrgPojo>>`), not presentation-specific formats. Controllers handle transformation:
- JSON Controller: Direct serialization of POJOs via Jackson
- HTML Controller: POJOs → HTML via Helper classes

### 2.2 Caching Layer Integration

**Fact:** [AbstractAsyncCacheMap.java:10-38, BrowseJsonController.java:34, BrowseHtmlHelper.java:43] — Both controllers access shared cache layer:

```java
// JSON Controller uses cache
return new ResponseEntity<>(CompanyOrgsCache.INSTANCE.getCache(), HttpStatus.OK);

// HTML Helper uses same cache
final Map<CompanyPojo, Set<OrgPojo>> companyOrgs = CompanyOrgsCache.INSTANCE.getCache();
```

**Fact:** [Grep results: 15 *CacheMap classes] — Cache layer follows `AbstractAsyncCacheMap` pattern with:
- Generic key-value typing: `AbstractAsyncCacheMap<K, V, C extends AbstractAsyncCache<K, V>>`
- Thread-safe synchronized access (line 23)
- Lazy cache initialization per key (lines 24-26)

**Inference:** [Cache architecture] → Caching is **controller-agnostic** and operates at the service data level, not presentation level.

---

## 3. JSON Response Structure Conventions

### 3.1 Nested Map Responses

**Fact:** [BrowseJsonController.java:33-90] — JSON endpoints return nested Map structures with JOOQ POJOs as keys/values:

```java
// Returns Map<CompanyPojo, Set<OrgPojo>>
@GetMapping(path = "", produces = "application/json")
public ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> getCompanyOrgs()

// Returns Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>
@GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
public ResponseEntity<Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>> getCompanyOrgsRepos(@PathVariable String company)

// Returns Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>>
@GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
public ResponseEntity<Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>>> getOrgReposBranches(...)
```

**Observation — Potential Issues:**
1. **Map keys in JSON:** POJOs as Map keys serialize to JSON objects (not ideal for client parsing)
2. **Hierarchical nesting:** Three levels of nesting (`Map<X, Map<Y, Set<Z>>>`) creates complex client code
3. **No pagination:** `Set<RunPojo>`, `Set<StagePojo>` return unbounded collections

**Inference:** [JSON response patterns] → Current structure optimizes for **backend convenience** (matches JOOQ query results) over **API usability**. Clients must navigate nested structures and handle object keys.

### 3.2 Alternative: Domain Model Responses

**Fact:** [BrowseJsonController.java:116-126, GraphJsonController.java:36-48] — Some endpoints return domain models directly:

```java
// Returns StageTestResultModel (domain model)
@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}",
            produces = "application/json")
public ResponseEntity<StageTestResultModel> getStageTestResultsTestSuites(...)

// Returns JobStageTestTrend (domain model)
@GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/stage/{stage}/trend",
            produces = "application/json")
public ResponseEntity<JobStageTestTrend> getJobStageTestTrend(...)
```

**Inference:** [Mixed response patterns] → The codebase uses **two JSON response strategies**:
1. **Nested Maps**: For hierarchical browse navigation (company → org → repo → branch)
2. **Domain Models**: For specific entities with complex structure (test results, trends)

---

## 4. Component Boundaries

### 4.1 Layer Responsibilities

**Fact:** [Observed across controllers and services] — Clear separation of concerns:

| Layer | Responsibility | Example |
|-------|---------------|---------|
| **Controller** | HTTP concerns, path mapping, content negotiation | `@GetMapping`, `produces`, `@PathVariable` |
| **Service** | Business logic, database queries, domain models | `BrowseService.getCompanyOrgs()` |
| **Cache** | Async caching, thread-safety, lazy initialization | `CompanyOrgsCache.INSTANCE.getCache()` |
| **Helper** | Presentation logic (HTML generation, formatting) | `BrowseHtmlHelper.getCompaniesHtml()` |
| **Persist** | JOOQ queries, database access | `AbstractPersistService`, JOOQ DSL |

### 4.2 Helper Class Pattern

**Fact:** [BrowseHtmlHelper.java:27-773, StorageHtmlHelper.java:21-108] — HTML generation delegated to static helper methods:

```java
// Controller delegates to helper
return new ResponseEntity<>(BrowseHtmlHelper.getCompaniesHtml(), HttpStatus.OK);

// Helper builds HTML from domain models
public static String getCompaniesHtml() {
    final Map<CompanyPojo, Set<OrgPojo>> companyOrgs = CompanyOrgsCache.INSTANCE.getCache();
    StringBuilder sb = new StringBuilder();
    // ... HTML generation ...
    return getPage(main, getBreadCrumb(null));
}
```

**Fact:** [BrowseHtmlHelper extends no class, all methods static] — Helpers are **utility classes**, not Spring components.

**Inference:** [Helper design] → HTML generation is:
- **Stateless**: Static methods, no instance state
- **Testable**: No Spring context required
- **Reusable**: Shared across multiple controller methods (breadcrumbs, tables, etc.)

---

## 5. Code Reuse Patterns

### 5.1 Service Method Reuse (100%)

**Fact:** [BrowseService method calls in both controllers] — Identical service calls in HTML vs JSON controllers:

```java
// HTML Controller (BrowseUIController.java:68-70)
BranchStageViewResponse branchStageViewResponse = browseService.getStageViewForBranch(company, org, repo, branch, runs);
return new ResponseEntity<>(BrowseHtmlHelper.getBranchHtml(..., branchStageViewResponse, ...), HttpStatus.OK);

// No equivalent in JSON controller - demonstrates differentiation
// JSON controller (BrowseJsonController.java:59-66) uses cache instead:
return new ResponseEntity<>(BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchDTO(company, org, repo, branch)), HttpStatus.OK);
```

**Inference:** [Service usage patterns] → **Not all service methods are exposed in both controllers**. HTML controllers may:
- Aggregate multiple service calls
- Add GraphService data for charts/trends
- Apply different query parameters (e.g., `runs` limit)

### 5.2 Cache Access Pattern

**Fact:** [Cache usage across controllers] — Both controller types directly access singleton cache instances:

```java
// Direct cache access pattern (no service method)
CompanyOrgsCache.INSTANCE.getCache()
CompanyOrgsReposCacheMap.INSTANCE.getValue(dto)
BranchJobsRunsCacheMap.INSTANCE.getValue(dto)
```

**Fact:** [StaticBrowseService.java:8-41] — Cache implementations use Spring-injected service via static holder:

```java
@Component
public class StaticBrowseService {
    @Autowired
    public void setReportCardService(BrowseService browseService) {
        StaticBrowseService.INSTANCE = browseService;
    }
    private static BrowseService INSTANCE;
}
```

**Inference:** [Cache architecture] → Cache layer bypasses service methods for **performance**, calling underlying `BrowseService` methods directly via static singleton pattern. This creates **implicit coupling** between cache implementations and service layer.

---

## 6. Build Order and Dependency Implications

### 6.1 Module Dependencies

**Fact:** [CLAUDE.md module structure] — Module dependency chain:

```
reportcard-model (domain POJOs)
    ↑
reportcard-server (controllers, services, JOOQ generated code)
    ↑
reportcard-client (API client library)
```

### 6.2 JOOQ Generated Code Implications

**Fact:** [CLAUDE.md, BrowseService.java imports] — Controllers depend on JOOQ-generated POJOs:

```java
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
```

**Inference:** [Build order] → Any JSON API structure change requires:
1. SQL schema changes (manual)
2. JOOQ code regeneration: `./gradlew generateJooqSchemaSource`
3. Recompilation of controllers using generated POJOs

**Critical:** [CLAUDE.md:19] — "NEVER edit generated JOOQ code" means API responses are **tightly coupled** to database schema. Adding JSON-specific DTOs would decouple this.

---

## 7. Adversarial Analysis

### 7.1 What Could Go Wrong?

**Question:** What's the strongest argument against the nested Map pattern?

**Answer:**
1. **Client complexity**: Parsing `Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>` in JavaScript/TypeScript requires nested iteration and type guards
2. **No versioning**: Direct POJO serialization makes API versioning impossible without database changes
3. **Over-fetching**: No pagination on Sets means large branches return thousands of runs
4. **Deserialization issues**: JOOQ POJOs may have circular references or non-serializable fields

**Evidence ignored that changes conclusion:**
- **Fact:** [BrowseJsonController.java:21 `@Hidden`] — JSON APIs marked as hidden from Swagger, suggesting they're **internal-only** or **not production-ready**
- **Fact:** [Project context mentions "existing JSON endpoints"] — These may be **experimental** implementations

### 7.2 Alternative Architecture

**What assumptions could be wrong?**

**Assumption:** "Controllers should return service layer POJOs directly"

**Counter-evidence:**
- **Industry standard:** Most REST APIs use DTOs (Data Transfer Objects) separate from domain models
- **API evolution:** DTOs allow changing JSON structure without database changes
- **Best practice:** [Spring REST docs] Controller → DTO → Service → Entity pattern

**Alternative approach:**
```java
// Instead of:
public ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> getCompanyOrgs()

// Use DTO pattern:
public ResponseEntity<List<CompanyDTO>> getCompanyOrgs() {
    Map<CompanyPojo, Set<OrgPojo>> data = service.getCompanyOrgs();
    return ok(CompanyDTOMapper.toList(data));
}
```

---

## 8. Recommendations for JSON API Structure

### 8.1 Short-term (Keep existing patterns)

**If treating current JSON APIs as internal/experimental:**

**Confident** — Continue using direct POJO serialization for:
- Internal microservice communication
- Admin tools
- Quick prototyping

**Rationale:** Low client count, controlled consumers, direct database access pattern aligns with JOOQ philosophy.

### 8.2 Long-term (Production-ready APIs)

**If exposing JSON APIs to external clients:**

**Likely** — Introduce DTO layer for public APIs:

1. **Create DTO package**: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/dto/api/`
2. **Flatten hierarchies**:
   ```json
   // Instead of nested Map
   {
     "companies": [
       {"id": 1, "name": "acme", "orgCount": 5}
     ]
   }

   // Instead of current: Map<CompanyPojo, Set<OrgPojo>>
   ```
3. **Add pagination**:
   ```java
   @GetMapping("/v1/api/company/{company}/jobs")
   public ResponseEntity<Page<JobDTO>> getJobs(
       @PathVariable String company,
       @RequestParam(defaultValue = "0") int page,
       @RequestParam(defaultValue = "20") int size
   )
   ```
4. **Version endpoints**: `/v1/api/*` → `/v2/api/*` when structure changes

**What could go wrong:**
- Maintenance burden: Maintaining both POJO and DTO structures
- Mapping complexity: Converting nested Maps to flat DTOs requires custom mappers
- Performance: Additional object creation and mapping overhead

### 8.3 Hybrid Approach

**Possible** — Keep dual strategy:

| API Type | Pattern | Use Case |
|----------|---------|----------|
| `/v1/api/browse/*` | Direct POJOs, nested Maps | Internal UIs, admin tools |
| `/v1/api/public/*` | DTOs, pagination, versioning | External integrations |

---

## 9. Summary for Roadmap Planning

### Component Boundaries

**Fact-based:**
- **Controllers**: Handle HTTP, delegate to services OR caches directly
- **Services**: Return domain POJOs, no presentation logic
- **Caches**: Thread-safe, async, singleton pattern via Spring injection
- **Helpers**: Stateless HTML generation (not needed for JSON)

### Code Reuse Patterns

**Observed:**
- ✅ **100% service layer reuse** (when both controllers call same methods)
- ✅ **100% cache layer reuse** (singleton instances)
- ❌ **0% controller code reuse** (different response types require separate implementations)
- ⚠️ **Partial service method coverage** (HTML controllers use methods not exposed in JSON, e.g., `getStageViewForBranch` with `runs` parameter)

### Build Order Implications

**Critical path:**
1. Schema changes (SQL) → 2. JOOQ regeneration → 3. Controller changes → 4. Client updates

**Decoupling strategies:**
- Introduce DTO layer between controllers and JOOQ POJOs
- Use DTO → POJO mappers (e.g., MapStruct) to isolate schema changes

---

## 10. Confidence Levels

| Statement | Confidence | Evidence |
|-----------|-----------|----------|
| Parallel controller pairs are the established pattern | **Certain** | 2 feature domains (browse, graph) both use this structure |
| Service layer is fully reusable | **Confident** | Identical `@Autowired` services in both controller types |
| Nested Map responses are suboptimal for external APIs | **Likely** | Industry standards + `@Hidden` annotation suggests experimental state |
| Current JSON APIs are production-ready | **Speculative** | `@Hidden` annotation implies otherwise, but actual usage unknown |

---

## References

**File citations:**
- `/controller/browse/BrowseUIController.java` (lines 18-21, 23-29, 32-173)
- `/controller/browse/BrowseJsonController.java` (lines 19-23, 25-29, 32-127)
- `/controller/graph/GraphUIController.java` (lines 26-30)
- `/controller/graph/GraphJsonController.java` (lines 22-25)
- `/persist/BrowseService.java` (lines 35-753)
- `/cache/AbstractAsyncCacheMap.java` (lines 10-38)
- `/cache/model/StaticBrowseService.java` (lines 8-41)
- `/controller/html/StorageHtmlHelper.java` (lines 1-108)

**Pattern count:**
- 2 parallel controller pairs (browse, graph)
- 15+ cache implementations
- 3 HTML helper classes
