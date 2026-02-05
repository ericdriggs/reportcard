# External Integrations

**Analysis Date:** 2026-01-26

## APIs & External Services

**AWS S3:**
- Purpose: Store and retrieve uploaded test result files (JUnit XML, archives)
- SDK/Client: `software.amazon.awssdk:s3-transfer-manager` (AWS Java SDK v2.23.3)
- Auth: `DefaultCredentialsProvider` - supports IAM roles, credentials chain, environment variables
- Implementation: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java`
- Operations:
  - Upload files/directories with checksum validation (SHA1)
  - List objects with prefix filtering
  - Download file bytes
  - Support for tar.gz extraction before upload

## Data Storage

**Databases:**
- Type/Provider: MySQL 8.0+
  - Production: Remote MySQL instance configured via environment variables
  - Testing: MySQL 8.0.33 container via Testcontainers
  - Connection pool: MysqlDataSource from mysql-connector-java
  - Client: JOOQ (type-safe query builder)
  - ORM-style: ModelMapper bridges JOOQ records to POJOs

**JDBC Configuration:**
- Connection: `jdbc:mysql://{host}:{port}/{database}?serverTimezone=UTC&allowMultiQueries=true`
- Connection string template: `db.connection.string` property
- Port default: 3306

**Required Environment:**
- `DB_HOST` - Database hostname (required)
- `DB_USERNAME` - Database user (required)
- `DB_PASSWORD` - Database password (required)
- `DB_PORT` - Optional, defaults to 3306
- `DB_NAME` - Optional, defaults to "reportcard"

**File Storage:**
- Primary: AWS S3 bucket
- Fallback: Local filesystem (for development, controlled by `reportcard.local` property)
- Temp directory: System temp with prefix `reportcard-` (auto-cleaned after upload)

**Caching:**
- Not detected - application loads data on-demand

## Authentication & Identity

**Auth Provider:**
- Custom basic auth placeholder (not Spring Security)
- Static credentials in `application.properties`:
  - `service.username=test` (production: override via environment)
  - `service.password=test` (production: override via environment)
- TODO: Marked in config to secure via runtime flags

**Implementation:**
- No Spring Security dependency detected
- Basic auth likely implemented as servlet filter or interceptor
- Configuration: `reportcard-server/src/main/resources/application.properties`

## Monitoring & Observability

**Error Tracking:**
- Not detected - no Sentry, DataDog, or New Relic integration

**Logs:**
- Framework: Logback 1.4.12 (default Spring Boot logging)
- Implementation: `@Slf4j` Lombok annotation throughout codebase
- Level configuration: Default Spring Boot levels (INFO for most, DEBUG configurable)
- Example: `S3Service.java` logs upload attempts, ETags, failures

**Metrics:**
- Spring Boot Actuator 2.6.15 configured but endpoints not explicitly documented
- Likely available at `/actuator/*` for health checks

## CI/CD & Deployment

**Hosting:**
- Not specified in codebase - inferred to be AWS-compatible infrastructure
- Supports S3 and MySQL - typical cloud deployment

**CI Pipeline:**
- GitHub Actions (inferred from repository structure)
- Not detected in codebase - CI config likely in `.github/workflows/`

**Artifact Publishing:**
- Maven repositories: `mavenLocal()`, `mavenCentral()`
- Publishing configured for modules via Gradle publishing plugin
- Bootable JAR as primary artifact

## Environment Configuration

**Required env vars (Production):**
- `DB_HOST` - MySQL hostname
- `DB_USERNAME` - MySQL user
- `DB_PASSWORD` - MySQL password
- `MYSQL_PASSWORD` - Required for JOOQ code generation (dev only)

**Optional env vars:**
- `DB_PORT` - MySQL port (default: 3306)
- `DB_NAME` - MySQL database name (default: reportcard)
- `S3_REGION` - AWS region (default: us-east-1)
- `S3_BUCKET` - S3 bucket name (default: rc)
- `s3.endpoint` - S3 endpoint override for LocalStack/testing
- `REPORTCARD_LOCAL` - Use local filesystem instead of S3 (default: false)

**Secrets Location:**
- Production: AWS Secrets Manager or environment variables
- Testing: LocalStack container and embedded MySQL container via Testcontainers
- Credentials chain: Supports IAM roles on EC2/ECS without env var setup

## Webhooks & Callbacks

**Incoming:**
- REST API endpoints for:
  - JUnit XML upload: `POST /junit/storage`
  - Test result submission: `POST /junit/upload`
- OpenAPI/Swagger documentation: `/swagger-ui/index.html`

**Outgoing:**
- S3 operations (GetObject, PutObject, ListObjectsV2)
- MySQL queries via JOOQ
- No external webhook callbacks detected

## Test Infrastructure

**LocalStack (AWS S3 Emulation):**
- Docker image: `localstack/localstack:3.0.2`
- Services: S3 emulation
- Initialization: Creates test bucket via `awslocal s3 mb s3://testbucket`
- Configuration: `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/config/LocalStackConfig.java`
- Endpoint discovery: Via `@DynamicPropertySource` to override `s3.endpoint`

**Embedded MySQL (Testing):**
- Docker image: `mysql:8.0.33`
- Schema: Loaded from `db/migration/V1.0__reportcard_mysql_ddl.sql`
- Data: Loaded from `db/migration/V1.1__reportcard_mysql_dml.sql` and `db/test/test-data.dml.sql`
- Configuration: `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/config/MyEmbeddedMysql.java`
- JDBC URL: Mapped port provided by container

---

*Integration audit: 2026-01-26*
