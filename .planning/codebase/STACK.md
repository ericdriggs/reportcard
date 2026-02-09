# Technology Stack

**Analysis Date:** 2026-01-26

## Languages

**Primary:**
- Java 17 - Core application language (specified in `.java-version`)

**Secondary:**
- Groovy - Build scripting

## Runtime

**Environment:**
- JVM (Java 17)

**Package Manager:**
- Gradle 8.x (inferred from plugins)
- Lockfile: `gradle-wrapper.jar` included

## Frameworks

**Core:**
- Spring Boot 2.6.15 - Web application framework with embedded Tomcat
- Spring Data JOOQ - Type-safe database access layer
- Spring MVC - REST API and UI controllers

**Database/ORM:**
- JOOQ 3.19.8 - Type-safe SQL builder and code generator
- MySQL Connector Java 8.0.28 - MySQL driver
- ModelMapper 2.3.0 - Object-to-object mapping

**API Documentation:**
- SpringDoc OpenAPI 1.8.0 - Swagger/OpenAPI 3.0 documentation
- Swagger UI - Interactive API explorer at `/swagger-ui/index.html`

**Testing:**
- JUnit Jupiter 5.9.3 - Test framework
- Testcontainers 1.20.0 - Docker-based test infrastructure
  - MySQL container (8.0.33) for integration tests
  - LocalStack container (3.0.2) for S3 mocking
- JsonUnit 2.37.0 - JSON assertion library
- Spring Boot Test - Test utilities

**Build/Dev:**
- Gradle JOOQ Plugin 8.0 - Automatic code generation from database schema
- Spring Dependency Management - Transitive dependency management

## Key Dependencies

**Critical:**
- aws-java-sdk-bom 2.23.3 - AWS SDK v2 for S3 operations
- aws-crt 0.29.7 - AWS CRT for async S3 transfers
- spring-boot-starter-actuator 2.6.15 - Health checks and metrics

**Infrastructure:**
- mysql-connector-java 8.0.28 - Database connectivity
- commons-lang3 3.14.0 - Utility functions
- commons-io 2.11.0 - I/O operations
- commons-compress 1.26.0 - Tar/gz archive handling
- commons-codec 1.17.1 - Encoding/decoding utilities
- jackson-databind 2.15.2 - JSON serialization
- jakarta.xml.bind-api 2.3.3 - XML binding
- jaxb-impl 2.3.3 - XML implementation
- logback-classic 1.4.12 - Structured logging
- lombok 1.18.20 - Code generation annotations

**Build-time:**
- io.soabase.record-builder 42 - Fluent builders for records
- jaxb-api 2.3.1 - XML binding annotations

## Configuration

**Environment:**
- Properties-based configuration via `application.properties`
- Profile-specific overrides: `application-test.properties`, `application-integration-test.properties`
- Environment variable support with defaults:
  - `DB_HOST`, `DB_PORT` (default: 3306), `DB_NAME` (default: reportcard)
  - `DB_USERNAME`, `DB_PASSWORD` - required, no defaults
  - `S3_REGION` (default: us-east-1), `S3_BUCKET` (default: rc)
  - `s3.endpoint` - optional S3 endpoint override (used for LocalStack)
  - `REPORTCARD_LOCAL` (default: false)
  - `MYSQL_PASSWORD` - required for JOOQ code generation

**Build:**
- `build.gradle` (root) - Gradle wrapper configuration
- `gradle.properties` - Shared versions and build settings
- `settings.gradle` - Multi-module structure definition

**Database:**
- Manual migration scripts in `reportcard-server/src/main/resources/db/migration/`
  - `V1.0__reportcard_mysql_ddl.sql` - Schema definition
  - `V1.1__reportcard_mysql_dml.sql` - Initial data
- JOOQ generates Java classes from MySQL schema (not using Flyway)

## Platform Requirements

**Development:**
- Java 17 (via `jenv` with `.java-version` file)
- Gradle (included via wrapper)
- MySQL 8.0+ (local or via Docker)
- Docker (required for running tests with Testcontainers)

**Test:**
- Testcontainers requires Docker daemon
- MySQL 8.0.33 container pulled automatically
- LocalStack 3.0.2 container for S3 emulation
- Tests configured with `@ActiveProfiles("test")`

**Production:**
- Java 17 runtime
- MySQL 8.0+ database
- AWS S3 bucket (or S3-compatible service)
- AWS credentials via IAM role or environment

**Deployment:**
- Bootable JAR artifact: `reportcard-server-0.1.23-application.jar`
- Executable: `./gradlew bootRun` (local development)

## Multi-Module Structure

```
reportcard-parent/
├── reportcard-server/       - Main Spring Boot application
├── reportcard-model/        - Shared DTOs and domain models
├── reportcard-client/       - Java HTTP client library (WebFlux-based)
├── reportcard-jooq-generator/ - Custom JOOQ code generation config
└── test-data-generator/     - Data generation utility (not in build)
```

## Build Artifacts

**Generated:**
- `reportcard-server/src/generated/java/` - JOOQ generated classes (MySQL schema POJOs, DAOs, records)
  - Package: `io.github.ericdriggs.reportcard.gen.db`
  - Generated via: `./gradlew generateJooqSchemaSource`

**Output:**
- Runnable JAR: `build/libs/reportcard-server-0.1.23-application.jar`
- Versioning: 0.1.23 (from `gradle.properties`)

---

*Stack analysis: 2026-01-26*
