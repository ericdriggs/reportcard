# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

* see .planning/codebase for a deeper analysis on code base than this document

## Project Overview

Reportcard is a test result metrics API and dashboard application. It stores, analyzes, and reports on test results (JUnit XML, TestNG, Surefire) with trend analysis.

**Tech Stack:** Java 17, Spring Boot 2.6.15, JOOQ (database access), MySQL 8.0, Gradle, AWS S3, Testcontainers



## Build Commands

```bash
./gradlew build                      # Build all modules
./gradlew test                       # Run unit tests
./gradlew integrationTest            # Run integration tests
./gradlew generateJooqSchemaSource   # Regenerate JOOQ code (requires local MySQL with schema)
./gradlew bootRun                    # Run server locally
```

To run a single test class:
```bash
./gradlew test --tests "fully.qualified.TestClassName"
```

## Module Structure

| Module | Purpose |
|--------|---------|
| reportcard-server | Main Spring Boot app - REST API, business logic, database access |
| reportcard-model | Shared domain models and DTOs |
| reportcard-client | Java client library for the API |
| reportcard-jooq-generator | Custom JOOQ code generation configuration |

## Key Architecture

### Database-First Design with JOOQ
- JOOQ generates type-safe Java classes from MySQL schema
- **Generated code location:** `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/`
- **Schema files:** `reportcard-server/src/main/resources/db/migration/V*.sql`
- **NEVER edit generated JOOQ code** - always regenerate from schema

### Schema Change Process
Flyway is NOT used. Schema changes are manual:
1. Modify SQL files in `db/migration/`
2. Manually apply to local MySQL
3. Run `./gradlew generateJooqSchemaSource`
4. Update business logic in `persist/` package

### Package Layout (reportcard-server)
| Package | Purpose |
|---------|---------|
| `controller/` | REST API endpoints |
| `persist/` | Business logic and database operations |
| `model/` | Domain models and DTOs |
| `cache/` | Hierarchical async caching for browse queries |
| `storage/` | S3 file operations |
| `gen/db/` | JOOQ generated code (DO NOT EDIT) |

### Testing
- Unit tests use Testcontainers (MySQL 8.0.33) and LocalStack (S3)
- Test base class: `AbstractTestResultPersistTest`
- Tests use `@ActiveProfiles("test")`
- Test schema init: SQL files copied to MySQL container's `/docker-entrypoint-initdb.d/`

## Critical Constraints

1. **JOOQ is generated** - Change schema and regenerate, never edit generated code
2. **No Flyway** - Schema migrations are manual despite V*.sql naming convention
3. **Cache layer is complex** - Understand `AbstractAsyncCache` pattern before modifying
4. **No Spring Security** - Current auth is basic placeholder (username/password in application.properties)
5. **Test vs Production** - Tests use Testcontainers/LocalStack; production uses real MySQL/S3

## Danger Zones

These files require extra care:
- `src/generated/` - Never edit directly, regenerate instead
- `db/migration/V*.sql` - Changes require manual DB updates + JOOQ regeneration
- `cache/` - Complex async patterns
- `PersistenceContext.java` - Core database connection setup
- `MyEmbeddedMysql.java` - Changes affect all tests

## Additional Documentation

- **Detailed AI guide:** `README_AI.md` (comprehensive patterns and troubleshooting)
- **Schema ER diagram:** `docs/schema/schema.mermaid`
- **Human documentation:** `README.adoc`
