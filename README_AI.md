# Reportcard - AI README

## Purpose
This README is designed for AI assistants working with the reportcard codebase. It provides:
- **Codebase structure** - Module organization and file location patterns
- **Architecture context** - Database design, security, key patterns and their rationale
- **Configuration differences** - Test vs production environments
- **Navigation efficiency** - Reduce file searches by knowing where to look
- **Danger zones** - What not to modify without understanding implications

For human-oriented documentation, see `README.adoc`.

---

## Quick Start by Task

| I need to... | Go to section | Key files |
|--------------|---------------|----------|
| Add REST endpoint | [Controllers](#controllers-rest-api-endpoints) | `controller/*Controller.java` |
| Change database schema | [Database Schema](#database-schema) | `db/migration/V*.sql` → regenerate JOOQ |
| Understand test vs prod config | [Key Differences](#key-differences-test-vs-standalone) | Test: Testcontainers, Prod: Real MySQL |
| Modify business logic | [Services](#services-business-logic) | `persist/*Service.java` |
| Check if I can edit a file | [Danger Zones](#danger-zones-ask-before-modifying) | Generated code, schema files, cache |
| Add test result format | [Controllers](#controllers-rest-api-endpoints) | `JunitController.java` |
| Understand database relationships | [Schema Visualization](#schema-visualization) | `docs/schema/schema.mermaid` |
| Run/build the project | [Build Commands](#build-commands) | `./gradlew` commands |

---

## Critical Rules for AI Assistants

1. **Always check module context** - Don't search all modules when the task is clearly in reportcard-server
2. **JOOQ is generated** - If you need to change database access, change the schema and regenerate
3. **No Flyway** - Schema changes are manual; update SQL files, run manually, regenerate JOOQ
4. **Test vs Production** - Remember the different database/S3 configurations for tests
5. **Test schema init** - Tests use MySQL container's docker-entrypoint-initdb.d, not Flyway
6. **Cache complexity** - The cache layer is intricate; understand the pattern before modifying
7. **Security is basic** - Current auth is placeholder; don't assume Spring Security features exist

---

## Danger Zones (Ask Before Modifying)

1. **Generated JOOQ code** (`src/generated/`) - Never edit directly, regenerate instead
2. **Schema SQL files** (`db/migration/V*.sql`) - Changes require manual DB updates + JOOQ regeneration
3. **Cache hierarchy** (`cache/`) - Complex async patterns, easy to break
4. **PersistenceContext.java** - Core database connection setup, affects entire app
5. **Test database setup** (`MyEmbeddedMysql.java`) - Changes affect all tests

---

## Project Overview

**Tech Stack:**
- Java 17
- Spring Boot (REST API)
- JOOQ (database access, code generation)
- MySQL (database)
- Gradle (multi-module build)
- AWS S3 (storage)
- Testcontainers (testing with MySQL + LocalStack)

**Purpose:** Test result metrics APIs and dashboards - stores and analyzes test results (JUnit, TestNG, Surefire) with trend analysis and reporting.

---

## Module Structure

### reportcard-server
**Purpose:** Main Spring Boot application - REST API and web UI  
**When to look here:** API endpoints, business logic, database access, controllers, services

### reportcard-model
**Purpose:** Shared domain models and DTOs  
**When to look here:** Data transfer objects, domain models used across modules

### reportcard-client
**Purpose:** Java client library for the API  
**When to look here:** Client-side API consumption code

### reportcard-jooq-generator
**Purpose:** Custom JOOQ code generation configuration  
**When to look here:** Database code generation customization

---

## File Location Patterns (reportcard-server)

### Controllers (REST API Endpoints)
**Path:** `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/`  
**Pattern:** `*Controller.java`  
**When to look:** Adding/modifying REST endpoints, API behavior changes  
**Key files:**
- `JunitController.java` - JUnit test result upload
- `StorageController.java` - File storage operations
- `browse/BrowseJsonController.java` - Browse API (JSON)
- `browse/BrowseUIController.java` - Browse UI (HTML)
- `graph/GraphJsonController.java` - Metrics/graph data API

### Services (Business Logic)
**Path:** `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/`  
**Pattern:** `*Service.java`, `*PersistService.java`  
**When to look:** Core business logic, orchestration between layers  
**Key files:**
- `TestResultPersistService.java` - Test result persistence logic
- `StoragePersistService.java` - Storage persistence logic
- `BrowseService.java` - Browse/query operations
- `GraphService.java` - Metrics and graph data

### Database Access (JOOQ)
**Path:** `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/`  
**Pattern:** Generated JOOQ classes (DAOs, Records, POJOs, Tables)  
**When to look:** Database queries, table access  
**Note:** This is GENERATED code - do not edit directly. Regenerate via `generateJooqSchemaSource` task.

### Database Schema
**Path:** `reportcard-server/src/main/resources/db/migration/`  
**Pattern:** `V*.sql` (Flyway naming convention, but Flyway is NOT used)  
**When to look:** Schema changes, table definitions, reference data  
**Key files:**
- `V1.0__reportcard_mysql_ddl.sql` - Schema DDL (tables, indexes)
- `V1.1__reportcard_mysql_dml.sql` - Reference data (test_status, storage_type, fault_context)

**Note:** Despite Flyway-style naming, migrations are NOT run by Flyway (it's commented out in build.gradle)

### Models/DTOs
**Path:** `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/`  
**When to look:** Domain models, response objects, internal data structures  
**Subdirectories:**
- `graph/` - Graph/metrics models
- `trend/` - Trend analysis models
- `branch/` - Branch-related models

### Cache Layer
**Path:** `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/`  
**When to look:** Caching logic, async cache implementations  
**Note:** Complex hierarchical caching for browse/navigation performance

### Configuration
**Path:** `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/config/`  
**When to look:** Spring configuration, Swagger setup, web MVC config  
**Key files:**
- `InitialConfiguration.java` - Initial app setup
- `SwaggerBeanConfig.java` - OpenAPI/Swagger configuration
- `WebMvcConfig.java` - Web MVC configuration

### Storage (S3)
**Path:** `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/`  
**When to look:** S3 upload/download, file storage operations  
**Key files:**
- `S3Service.java` - AWS S3 operations

---

## Database Architecture

### Technology
- **Database:** MySQL 8.0
- **Access Layer:** JOOQ (type-safe SQL)
- **Schema Management:** Manual SQL scripts (Flyway naming convention but NOT using Flyway)
- **Code Generation:** JOOQ generates DAOs, Records, POJOs from schema

### Schema Visualization

**ER Diagram:** See [schema.mermaid](docs/schema/schema.mermaid) for the complete entity-relationship diagram

**Regenerate diagram:** `./generate-schema-diagram.sh` (requires Docker + mermerd)  
**Setup instructions:** [docs/schema/SETUP.md](docs/schema/SETUP.md)

**Key relationships:** See the [ER diagram](docs/schema/schema.mermaid) for complete visualization

### Connection Configuration

#### Standalone Service (Production/Local)
**Config:** `reportcard-server/src/main/resources/application.properties`
```properties
db.host=${DB_HOST}
db.port=${DB_PORT:3306}
db.name=${DB_NAME:reportcard}
db.username=${DB_USERNAME}
db.password=${DB_PASSWORD}
db.url=jdbc:mysql://${db.host}:${db.port}/${db.name}?serverTimezone=UTC&allowMultiQueries=true
```
**Connection Bean:** `PersistenceContext.java` creates MysqlDataSource from properties

#### Unit Tests (Testcontainers)
**Config:** `reportcard-server/src/test/resources/application-test.properties`
```properties
db.host=localhost
db.name=reportcard
db.username=test
db.password=${MYSQL_PASSWORD}
db.ddlsql=db/migration/V1.0__reportcard_mysql_ddl.sql
db.dmlsql=db/migration/V1.1__reportcard_mysql_dml.sql
```
**Test Setup:** Tests use `@SpringBootTest` with `@ActiveProfiles("test")`  
**Container:** Testcontainers MySQL 8.0.33 (see `MyEmbeddedMysql.java`)  
**Schema:** SQL files copied to container's `/docker-entrypoint-initdb.d/` and run on startup:
  - `0_schema.sql` (DDL from V1.0)
  - `1_config.sql` (DML from V1.1)
  - `2_data.sql` (test data)

#### LocalStack (S3 Testing)
**Config:** `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/config/LocalStackConfig.java`
```java
@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer = newLocalStackContainer();
    // Creates S3 bucket "testbucket" in LocalStack
    // Overrides s3.endpoint property for tests
}
```
**Purpose:** Tests S3 operations without AWS credentials  
**Container:** LocalStack 3.0.2 with S3 service  
**Bucket:** `testbucket` (created automatically)

### Key Differences: Test vs Standalone

| Aspect | Unit Tests | Standalone Service |
|--------|-----------|-------------------|
| Database | Testcontainers MySQL (ephemeral) | Real MySQL instance |
| S3 | LocalStack (Docker container) | Real AWS S3 |
| Config Profile | `test` | `default` |
| Schema Setup | SQL via docker-entrypoint-initdb.d | Manual schema setup required |
| Credentials | Hardcoded test values | Environment variables |

---

## Security Architecture

### Current State: Basic Auth
**Location:** `application.properties`
```properties
service.username=test
service.password=test
```
**Note:** TODOs in code indicate this needs to be secured via runtime flags/environment variables

**No Spring Security:** Currently using basic username/password, not Spring Security framework

**Future:** Migrate to proper authentication/authorization (see TODOs in application.properties)

---

## Key Architectural Patterns

### JOOQ Code Generation
**Why:** Type-safe SQL queries, compile-time validation, automatic mapping  
**Process:**
1. Write/modify SQL schema files in `db/migration/`
2. Manually run SQL against local MySQL to create/update schema
3. Run `generateJooqSchemaSource` Gradle task
4. JOOQ generates Java classes in `src/generated/java`

**Never edit generated code directly** - regenerate from schema

**Note:** Flyway is NOT used - schema changes are manual

### Hierarchical Caching
**Why:** Browse/navigation queries are expensive (deep joins)  
**Pattern:** Async cache with sync/async duration settings  
**Location:** `cache/` package with `AbstractAsyncCache` base class

### Test Result Storage
**Pattern:** 
1. Upload test results (JUnit XML, etc.) via REST API
2. Parse and persist to MySQL (test cases, suites, results)
3. Store raw files in S3
4. Generate HTML reports and dashboards

### Schema Management (No Flyway)
**Current State:** Flyway is commented out in build.gradle - NOT USED  
**Pattern:** SQL files follow Flyway naming (`V<version>__<description>.sql`) but are run manually  
**Location:** `src/main/resources/db/migration/`  
**Test Setup:** `MyEmbeddedMysql.java` copies SQL files to MySQL container's init directory  
**Production:** Schema must be manually applied to database

**Schema Change Process:**
1. Modify SQL files in `db/migration/`
2. Manually run against local MySQL
3. Regenerate JOOQ code
4. Manually apply to production database

---

## Maintaining This Document

**Update this README when:**
- New module added to the project
- Database schema changes significantly (new major tables/relationships)
- Major architectural pattern changes (e.g., switching from JOOQ, adding Spring Security)
- New environment configuration added (e.g., staging environment)
- Build/deployment process changes

**Don't update for:**
- Individual file additions/changes within existing structure
- Dependency version updates
- Bug fixes that don't change architecture
- Minor refactoring within existing patterns

**How to update:**
1. Identify which section is affected (use Quick Start table as guide)
2. Update that section with new information
3. If adding new major concept, consider adding to Quick Start table
4. Regenerate schema diagram if database changes: `./generate-schema-diagram.sh`

---

## Testing Patterns

### Unit Tests
**Location:** `reportcard-server/src/test/`  
**Base Class:** `AbstractTestResultPersistTest` (common test setup)  
**Profile:** `@ActiveProfiles("test")`  
**Database:** Testcontainers MySQL  
**S3:** LocalStack

### Integration Tests
**Location:** `reportcard-server/src/integrationTest/`  
**Purpose:** Full end-to-end testing with real containers  
**Run:** `./gradlew integrationTest`

---

## Build Commands

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate JOOQ code (requires local MySQL with schema)
./gradlew generateJooqSchemaSource

# Run server locally
./gradlew bootRun

# Publish to Maven Central
./deploy.sh
```

---

## Environment Variables (Standalone Service)

Required for running the service:
- `DB_HOST` - MySQL host
- `DB_PORT` - MySQL port (default: 3306)
- `DB_NAME` - Database name (default: reportcard)
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `REPORTCARD_LOCAL` - Set to `true` for local development (default: false)

---

## Quick Reference: Package Purposes

| Package | Purpose |
|---------|---------|
| `controller/` | REST API endpoints, request/response handling |
| `persist/` | Business logic, database operations |
| `model/` | Domain models, DTOs, internal data structures |
| `cache/` | Async caching layer for performance |
| `storage/` | S3 file storage operations |
| `config/` | Spring configuration, app setup |
| `gen/db/` | JOOQ generated database access code |
| `util/` | Utility classes (badge generation, tar/gzip, etc.) |
| `lock/` | Database locking for concurrency control |
| `aws/` | AWS-specific utilities (S3 comparators) |

---

## Additional Documentation

- **Requirements:** `docs/requirements.adoc`
- **Data Model:** `docs/data-model.adoc`
- **Schema ER Diagram:** `docs/schema/schema.mermaid` (auto-generated from DDL)
- **Schema Setup:** `docs/schema/SETUP.md`
- **Roadmap:** `docs/roadmap.adoc`
- **Supported Formats:** `docs/supported-data-fomats.adoc`
- **Human README:** `README.adoc`


