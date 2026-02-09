# Architecture

**Analysis Date:** 2026-01-26

## Pattern Overview

**Overall:** Layered Spring Boot application with Database-First Design (JOOQ) and Hierarchical Caching

**Key Characteristics:**
- Type-safe database access via JOOQ-generated classes
- Multi-tiered REST API with separate JSON and UI endpoints
- Hierarchical async caching layer for complex browse queries
- S3-based storage integration for test artifacts and HTML reports
- Hierarchical data model: Company > Org > Repo > Branch > Job > Run > Stage

## Layers

**API/Controller Layer:**
- Purpose: HTTP request handling, REST endpoint definitions, parameter validation
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/`
- Contains: REST controllers grouped by feature (badge, browse, graph, storage, junit)
- Depends on: Persist services, S3Service, model classes
- Used by: HTTP clients

**Persist/Business Logic Layer:**
- Purpose: Core business logic, JOOQ queries, database operations
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/`
- Contains: AbstractPersistService base class, TestResultPersistService, BrowseService, GraphService, StoragePersistService, StagePathPersistService
- Depends on: JOOQ-generated code, model classes, database
- Used by: Controllers, cache layer

**Cache Layer:**
- Purpose: Hierarchical async caching for expensive browse queries
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/`
- Contains: AbstractAsyncCache base class, AbstractAsyncCacheMap, specialized cache implementations (CompanyOrgsCache, RepoBranchesJobsCache, etc.)
- Depends on: Persist services, model classes
- Used by: Controllers, especially BrowseJsonController

**Storage Layer:**
- Purpose: AWS S3 integration for test artifacts and HTML reports
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/`
- Contains: S3Service for upload/download operations, upload request models
- Depends on: AWS SDK (S3), tar extraction utilities
- Used by: Controllers, test result processing

**Model/Domain Layer:**
- Purpose: Domain models, POJOs, data transfer objects
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/` and `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/`
- Contains: StagePath, StageDetails, test result models (TestResultModel, TestSuiteModel, TestCaseModel), response models
- Depends on: JOOQ generated POJOs (for database mapping)
- Used by: All layers

**Generated Data Access Layer:**
- Purpose: Type-safe database access code generated from MySQL schema
- Location: `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/`
- Contains: JOOQ table definitions, record classes, POJOs
- Depends on: JOOQ library
- Used by: Persist services
- **CRITICAL:** Never edit directly—always regenerate from schema using `./gradlew generateJooqSchemaSource`

**Configuration Layer:**
- Purpose: Spring configuration, bean definitions, Spring Boot setup
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/config/`
- Contains: ReportcardApplication (main entry point), Config, InitialConfiguration, WebMvcConfig, SwaggerBeanConfig
- Depends on: Spring framework, environment properties
- Used by: Spring container initialization

## Data Flow

**Test Result Ingestion Flow:**

1. Client uploads JUnit/Surefire XML + optional storage via `JunitController.postStageJunitStorageTarGZ()`
2. Controller extracts tar.gz contents via `TestXmlTarGzUtil`
3. XML is parsed to TestResultModel via `JunitSurefireXmlParseUtil.parseTestXml()`
4. Lock is acquired via `LockService` to prevent concurrent stage updates
5. `TestResultPersistService.insertTestResult()` executes:
   - Upserts Company > Org > Repo > Branch > Job > Run > Stage hierarchy
   - Inserts TestResult, TestSuite, TestCase records into MySQL
   - Updates Job.lastRun timestamp
6. If storage included:
   - `S3Service.uploadTarGz()` uploads files to S3 with calculated prefix
   - `StoragePersistService` records storage metadata in database
7. Response includes stage ID and test result counts

**Browse (Hierarchical Data) Flow:**

1. Client calls `BrowseJsonController` or `BrowseUIController` endpoints
2. Controller accesses cache via `StaticBrowseService` which holds multiple AbstractAsyncCache instances
3. Cache checks age:
   - If expired: blocks and synchronously refreshes via `BrowseService.getCompanyOrgs()`
   - If stale (> async threshold): returns stale data and async-refreshes in background
   - If fresh: returns cached data immediately
4. Data includes entire hierarchy: Company > Org > Repo > Branch > Job > Run > Stage
5. HTML or JSON response returned to client

**Graph/Trends Flow:**

1. Client calls `GraphJsonController.getJobStageTestTrend()` with date range
2. `GraphService` queries test results in time window via JOOQ
3. Results aggregated into CompanyGraph structures
4. Converted to trend data (success/failure ratios over time)
5. JSON response includes trend analysis and historical data

**State Management:**

- **Persistent State:** Test results, stages, runs, jobs stored in MySQL
- **Transient State:** Async cache held in memory with refresh timers
- **Request State:** Lock service ensures only one concurrent update per stage
- **Artifact Storage:** S3 holds tar.gz files with hierarchical prefix paths

## Key Abstractions

**StagePath:**
- Purpose: Represents the complete hierarchical path to a Stage (Company → Org → Repo → Branch → Job → Run → Stage)
- Examples: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/StagePath.java`
- Pattern: Composite value object containing all parent POJOs; used for navigation and validation

**StageDetails:**
- Purpose: Request parameters for identifying where test results should be stored
- Examples: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/StageDetails.java`
- Pattern: Builder pattern; includes company, org, repo, branch, job info, run reference, sha, stage name, external links

**AbstractAsyncCache<K, V>:**
- Purpose: Base class for hierarchical async caching with configurable sync/async refresh durations
- Examples: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/AbstractAsyncCache.java`
- Pattern: Template method; subclasses implement `getUpdatedCacheValue()` and `getSyncAsyncDuration()` for specific cache types

**AbstractPersistService:**
- Purpose: Base class for all database service classes with JOOQ DSLContext dependency injection
- Examples: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/AbstractPersistService.java`
- Pattern: Template method; all concrete services extend this and use `dsl` field for queries

**TestResultModel & Related:**
- Purpose: Hierarchical model representing parsed test results (TestResult → TestSuite → TestCase)
- Examples: Model classes in `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/dto/`
- Pattern: Tree structure matching XML schema; converted from JUnit/Surefire XML via `JunitSurefireXmlParseUtil`

## Entry Points

**ReportcardApplication:**
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/ReportcardApplication.java`
- Triggers: `java -jar reportcard-server.jar` or Spring Boot startup
- Responsibilities: Spring Boot initialization, component scanning, transaction management, scheduling setup

**JunitController:**
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java`
- Triggers: POST `/v1/api/junit/tar.gz` and `/v1/api/junit/storage/{label}/tar.gz`
- Responsibilities: Accept test result uploads, orchestrate parse → persist → store flow

**BrowseJsonController:**
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java`
- Triggers: GET `/v1/api/browse/*` endpoints
- Responsibilities: Return hierarchical test data in JSON format using cache

**GraphJsonController:**
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphJsonController.java`
- Triggers: GET `/v1/api/graph/*` endpoints
- Responsibilities: Return trend/graph data for test results over time

**StorageController:**
- Location: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/StorageController.java`
- Triggers: GET/POST `/v1/api/storage/*` endpoints
- Responsibilities: Manage storage metadata (HTML reports, JUnit archives)

## Error Handling

**Strategy:** Synchronous try-catch with ResponseEntity conversion; custom exception handlers for parameter validation

**Patterns:**

- `ResponseStatusException(HttpStatus.NOT_FOUND)` thrown by persist services when single-object queries return null (e.g., stage not found)
- Controllers catch exceptions and convert to ResponseEntity with appropriate HTTP status via `StagePathTestResultResponse.fromException()` and similar methods
- `MissingParameterExceptionHandler` intercepts missing required @RequestParam to return 400 Bad Request
- All controller methods include try-catch at request level with logging and error response conversion
- Lock service wraps critical sections and propagates exceptions up to controller

## Cross-Cutting Concerns

**Logging:** SLF4J with Logback; all services use `@Slf4j` annotation; critical operations logged at INFO level with stage/request details

**Validation:**
- Request parameter validation via Spring @RequestParam with required=true
- StagePath includes `validate()` method returning Map<String, String> of validation errors
- StageDetails builder ensures required fields set before use

**Authentication:**
- No Spring Security configured
- Basic placeholder: `service.username` and `service.password` properties in application.properties
- Currently set to "test/test" - noted as TODO to secure with runtime flags

**Transaction Management:**
- `@EnableTransactionManagement` on ReportcardApplication
- AbstractPersistService uses Spring-managed JOOQ DSLContext (transactional)
- Lock service uses ReentrantReadWriteLock for concurrency control (not Spring transactions)

**Scheduling:**
- `@EnableScheduling` on ReportcardApplication
- Async cache refresh happens in background threads via ScheduledExecutorService
- No scheduled tasks currently defined in configuration

---

*Architecture analysis: 2026-01-26*
