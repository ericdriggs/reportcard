# Codebase Structure

**Analysis Date:** 2026-01-26

## Directory Layout

```
reportcard/
├── reportcard-server/              # Main Spring Boot application
│   ├── src/main/java/              # Application source code
│   │   └── io/github/ericdriggs/reportcard/
│   │       ├── controller/         # REST API endpoints
│   │       ├── persist/            # Database services (business logic)
│   │       ├── model/              # Domain models and DTOs
│   │       ├── cache/              # Async caching layer
│   │       ├── storage/            # S3 integration
│   │       ├── service/            # Utility services (lock, etc.)
│   │       ├── config/             # Spring configuration
│   │       ├── util/               # Helper utilities
│   │       ├── aws/                # AWS-specific utilities
│   │       ├── lock/               # Concurrency control
│   │       ├── interfaces/         # Interface definitions
│   │       └── gen/db/             # JOOQ-generated code (AUTO-GENERATED)
│   ├── src/generated/              # JOOQ generated database classes (AUTO-GENERATED)
│   ├── src/main/resources/         # Configuration and data files
│   │   ├── application.properties  # Spring Boot configuration
│   │   ├── db/migration/           # Manual SQL schema files (V*.sql)
│   │   ├── static/                 # CSS, JS, favicon for UI
│   │   ├── junit/                  # JUnit XML schema/utilities
│   │   ├── surefire/               # Surefire XML schema/utilities
│   │   ├── testng/                 # TestNG XML schema/utilities
│   │   └── logback.xml             # Logging configuration
│   ├── src/test/java/              # Unit tests
│   ├── src/integrationTest/java/   # Integration tests
│   └── build.gradle.kts            # Gradle build configuration
│
├── reportcard-model/               # Shared domain models
│   ├── src/main/java/              # XML parsing models, DTOs, utilities
│   │   └── io/github/ericdriggs/reportcard/
│   │       ├── dto/                # Data transfer objects
│   │       ├── xml/                # XML parsing utilities and POJOs
│   │       └── util/               # Shared utilities
│   ├── src/test/java/              # Model unit tests
│   └── build.gradle.kts
│
├── reportcard-client/              # Java client library for the API
│   ├── src/main/java/              # Client code
│   ├── src/test/java/              # Client tests
│   ├── src/integrationTest/java/   # Client integration tests
│   └── build.gradle.kts
│
├── reportcard-jooq-generator/      # Custom JOOQ code generation
│   ├── src/main/java/              # Generator configuration
│   ├── src/test/java/              # Generator tests
│   └── build.gradle.kts
│
├── docs/                           # Documentation
│   ├── schema/                     # Database schema documentation
│   │   ├── schema.mermaid          # ER diagram
│   │   └── SETUP.md                # Schema setup guide
│   └── images/                     # Documentation images
│
├── shell/                          # Shell scripts
├── transform/                      # Data transformation utilities
├── test-data-generator/            # Test data generation tool
├── build.gradle.kts                # Root gradle configuration
├── gradle.properties               # Gradle properties
├── settings.gradle.kts             # Gradle multi-project configuration
├── CLAUDE.md                       # AI guidance for codebase
├── README.adoc                     # Human documentation
└── README_AI.md                    # Detailed AI patterns guide
```

## Directory Purposes

**reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/:**
- Purpose: REST API endpoint definitions organized by feature
- Contains: Annotated controller classes using Spring @RestController and @RequestMapping
- Key files:
  - `JunitController.java`: Main endpoint for test result uploads
  - `BrowseJsonController.java`, `BrowseUIController.java`: Hierarchical data browsing
  - `GraphJsonController.java`, `GraphUIController.java`: Trend and graph queries
  - `StorageController.java`: HTML/artifact storage management
  - `BadgeController.java`: Status badge generation
  - Subdirectories: `badge/`, `browse/`, `graph/`, `html/`, `model/`, `util/`

**reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/:**
- Purpose: Database access layer with business logic
- Contains: Spring @Service classes extending AbstractPersistService
- Key files:
  - `TestResultPersistService.java`: Main service for inserting/querying test results
  - `BrowseService.java`: Hierarchical data queries (Company → Org → Repo → Branch → Job)
  - `GraphService.java`: Trend analysis and graph data generation
  - `StoragePersistService.java`: Storage metadata and S3 coordination
  - `StagePathPersistService.java`: Intermediate service for stage path operations

**reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/:**
- Purpose: Domain models, DTOs, composite value objects
- Contains: Lombok @Data classes, response models, composite objects
- Key files:
  - `StagePath.java`: Composite object representing Company → Org → Repo → Branch → Job → Run → Stage
  - `StageDetails.java`: Request parameter container with builder pattern
  - `StagePathTestResult.java`, `StagePathStorages.java`: Response models
  - Subdirectories: `branch/`, `graph/`, `metrics/`, `pipeline/`, `trend/`, `orgdashboard/` contain domain-specific models

**reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/:**
- Purpose: Hierarchical async caching for expensive queries
- Contains: Abstract cache base classes and specialized cache implementations
- Key files:
  - `AbstractAsyncCache.java`: Template base class for async-refreshable caches
  - `AbstractAsyncCacheMap.java`: Extension for map-based caches
  - `CompanyOrgsCache.java`, `RepoBranchesJobsCache.java`: Specialized cache instances
  - `StaticBrowseService.java`: Singleton holding all browse caches
  - Subdirectories: `dto/` (cache DTOs), `model/` (cache implementations, model objects)

**reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/:**
- Purpose: AWS S3 integration for test artifacts
- Contains: S3Service with upload/download logic, request models
- Key files:
  - `S3Service.java`: Main service for S3 operations (using AWS SDK v2)
  - `StorageUploadRequest.java`, `HtmlUploadRequest.java`: Request models
  - `DirectoryUploadResponse.java`, `FailedFileUploadResponse.java`: Response models

**reportcard-server/src/main/java/io/github/ericdriggs/reportcard/config/:**
- Purpose: Spring Boot configuration and bean definitions
- Contains: Configuration classes and initialization logic
- Key files:
  - `ReportcardApplication.java`: Main @SpringBootApplication entry point
  - `Config.java`: Custom bean configurations
  - `InitialConfiguration.java`: Application startup initialization
  - `WebMvcConfig.java`: Spring Web MVC configuration
  - `SwaggerBeanConfig.java`: OpenAPI/Swagger documentation setup

**reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/:**
- Purpose: JOOQ-generated type-safe database access code
- Contains: Auto-generated table definitions, record classes, POJOs
- Generated by: `./gradlew generateJooqSchemaSource` from MySQL schema
- **CRITICAL:** Never edit directly; delete and regenerate to apply schema changes

**reportcard-server/src/main/resources/db/migration/:**
- Purpose: Database schema definitions (manual migration, not Flyway-managed)
- Contains:
  - `V1.0__reportcard_mysql_ddl.sql`: Initial schema (tables, foreign keys)
  - `V1.1__reportcard_mysql_dml.sql`: Initial data (seed data)
- Process: Edit SQL files → Apply to local MySQL manually → Run `./gradlew generateJooqSchemaSource`

**reportcard-model/src/main/java/io/github/ericdriggs/reportcard/:**
- Purpose: Shared domain models used by API clients and parsers
- Contains:
  - `dto/`: Test result DTOs (TestResult, TestSuite, TestCase, TestStatus)
  - `xml/`: XML parsing POJOs and utilities for JUnit/Surefire/TestNG
  - `util/`: Shared utilities (JSON comparison, string mapping, truncation)

## Key File Locations

**Entry Points:**
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/ReportcardApplication.java`: Spring Boot main class
- `reportcard-server/src/main/resources/application.properties`: Configuration file

**Core Controllers:**
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java`: Test result ingestion
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseJsonController.java`: Browse API
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/GraphJsonController.java`: Graph/trend API

**Core Services:**
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/TestResultPersistService.java`: Test result business logic
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/BrowseService.java`: Browse queries
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java`: Graph/trend queries
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java`: S3 operations

**Configuration:**
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/config/Config.java`: Spring beans
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/config/InitialConfiguration.java`: Initialization

**Testing:**
- `reportcard-server/src/test/java/`: Unit tests (use Testcontainers + LocalStack)
- `reportcard-server/src/integrationTest/java/`: Integration tests

## Naming Conventions

**Files:**
- Controllers: `*Controller.java` (e.g., `JunitController.java`)
- Services: `*Service.java` (e.g., `TestResultPersistService.java`)
- Models/DTOs: `*Model.java`, `*Pojo.java`, `*DTO.java` (e.g., `TestResultModel.java`, `CompanyPojo.java`)
- Request/Response: `*Request.java`, `*Response.java` (e.g., `JunitHtmlPostRequest.java`)
- Configuration: `*Config.java` (e.g., `WebMvcConfig.java`)
- Utilities: `*Util.java`, `*Helper.java` (e.g., `TestXmlTarGzUtil.java`)
- Cache: `*Cache.java`, `*CacheMap.java` (e.g., `CompanyOrgsCache.java`)
- Generated: Prefix `generated/` for JOOQ code; files auto-named by JOOQ (e.g., `Tables.java`, `CompanyRecord.java`)

**Directories:**
- Feature-based: `controller/`, `persist/`, `model/`, `cache/`, `storage/`
- Domain packages: `model/branch/`, `model/graph/`, `model/metrics/`, `model/pipeline/`, `model/trend/`, `model/orgdashboard/`
- Utility packages: `util/badge/`, `util/db/`, `util/tar/`, `util/list/`
- Sub-features: `controller/badge/`, `controller/browse/`, `controller/graph/`, `controller/html/`

**Class Naming:**
- DAOs/Services: Suffix with `Service` or `Pojo` (e.g., `BrowseService`, `CompanyPojo`)
- Value Objects: Named after what they represent (e.g., `StagePath`, `StageDetails`)
- Collections/Maps: `*Map`, `*Set`, `*List` (e.g., `CompanyOrgsReposCacheMap`)
- Responses: `*Response` (e.g., `StagePathTestResultResponse`)
- Exceptions: Usually use Spring's `ResponseStatusException`

## Where to Add New Code

**New Feature (Complete API + Database):**
- Primary code: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/{feature}Controller.java`
- Business logic: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/{feature}Service.java`
- Models: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/{feature}/`
- Database: `reportcard-server/src/main/resources/db/migration/V{N}.0__*.sql` (DDL) and `V{N}.1__*.sql` (DML)
- Tests: `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/{feature}ControllerTest.java`
- Generate JOOQ: `./gradlew generateJooqSchemaSource`

**New Component/Module:**
- Create new directory under appropriate layer (e.g., `persist/`, `controller/`)
- Name with feature suffix (e.g., `DashboardService.java`)
- Extend appropriate base class (e.g., `extends AbstractPersistService`)
- Register as Spring @Service if it's a service, @RestController if it's a controller

**Shared Utilities:**
- Generic utilities: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/util/{category}/`
- Parsing/Conversion: `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/util/` or `xml/`
- Test utilities: `reportcard-server/src/test/java/` with `*Test` suffix on files
- Example: `TarExtractor.java` in `util/tar/`, `JunitSurefireXmlParseUtil.java` in model package

**New Model/DTO:**
- Domain-specific: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/{domain}/`
- Shared DTOs: `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/dto/`
- Use Lombok `@Data` annotation for POJOs
- Use builder pattern for complex objects: `@Builder public class YourModel { ... }`

**Cache Implementation:**
- Extend `AbstractAsyncCache<K, V>` or `AbstractAsyncCacheMap<K, L, V>`
- Place in: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/model/`
- Register in: `StaticBrowseService.java` as a new cache instance
- Define: `SyncAsyncDuration` for refresh/expire timing

## Special Directories

**reportcard-server/src/generated/:**
- Purpose: JOOQ-generated database code
- Generated: Yes, by `./gradlew generateJooqSchemaSource`
- Committed: Yes (but should not be manually edited)
- **NEVER EDIT** - Delete and regenerate to reflect schema changes

**reportcard-server/src/main/resources/static/:**
- Purpose: Static web assets (CSS, JS, images)
- Contains: Frontend resources for UI endpoints
- Committed: Yes

**reportcard-server/src/test/java/ and src/integrationTest/java/:**
- Purpose: Unit and integration tests
- Base class: Test classes extend from `AbstractTestResultPersistTest` (uses Testcontainers)
- Containers: MySQL 8.0.33 from `MyEmbeddedMysql.java`, LocalStack for S3
- Committed: Yes

**build/ and target/:**
- Purpose: Build output directories
- Generated: Yes (gradle/Maven build artifacts)
- Committed: No (in .gitignore)

---

*Structure analysis: 2026-01-26*
