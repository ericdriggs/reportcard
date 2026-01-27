# Phase 1: Schema Foundation - Research

**Researched:** 2026-01-26
**Domain:** MySQL schema evolution with JOOQ code generation
**Confidence:** HIGH

## Summary

This research investigated the established patterns for database schema changes in the reportcard codebase, which uses a database-first design with JOOQ code generation. The project does NOT use Flyway for migrations despite Flyway-style file naming; all schema changes are manual.

The standard approach is to directly edit the CREATE TABLE DDL in `V1.0__reportcard_mysql_ddl.sql`, manually apply changes to local MySQL, regenerate JOOQ code via Gradle task, and update business logic. For reference tables (like `storage_type`), the DML file `V1.1__reportcard_mysql_dml.sql` is updated with new INSERT statements.

**Primary recommendation:** Follow the established pattern of editing DDL/DML files directly, using MySQL 8.0's INSTANT ADD COLUMN algorithm for zero-downtime changes, and maintaining strict synchronization between database reference tables and Java enums.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| MySQL | 8.0.33 | Relational database | Project standard, INSTANT ALTER support |
| JOOQ | 3.19.8 | Type-safe SQL & code generation | Database-first design, generates DAOs/POJOs/Records |
| Gradle | N/A | Build & task execution | `generateJooqSchemaSource` task |
| Testcontainers | 1.20.0 | MySQL container for tests | Ephemeral test databases with schema init |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| MySQL Connector/J | 8.0.28 | JDBC driver | JOOQ code generation & runtime |
| JavaLombokGenerator | JOOQ builtin | Lombok-annotated generated code | Automatic via JOOQ config |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Manual DDL edits | Flyway migrations | Flyway was intentionally disabled (commented out in build.gradle). Manual approach gives direct control but requires discipline. |
| JOOQ Instant mapping | Timestamp or LocalDateTime | Instant is already configured via ForcedType mapping, provides timezone-neutral representation |

**Installation:**
Already configured in project. No additional dependencies needed.

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/
├── main/
│   ├── resources/db/migration/
│   │   ├── V1.0__reportcard_mysql_ddl.sql    # All CREATE TABLE statements
│   │   └── V1.1__reportcard_mysql_dml.sql    # Reference data INSERTs
│   └── java/.../persist/
│       └── StorageType.java                   # Java enum synced with DB
└── generated/java/.../gen/db/
    ├── tables/                                # JOOQ generated Table classes
    ├── records/                               # JOOQ generated Record classes
    ├── pojos/                                 # JOOQ generated POJO classes
    └── daos/                                  # JOOQ generated DAO classes
```

### Pattern 1: Column Addition to Existing Table
**What:** Add new columns to an existing table by editing the CREATE TABLE DDL directly
**When to use:** Adding new fields (like `start_time`, `end_time` to `run` table)
**Example:**
```sql
-- Source: V1.0__reportcard_mysql_ddl.sql (project pattern)
CREATE TABLE IF NOT EXISTS `reportcard`.`run` (
  `run_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `run_reference` VARCHAR(255) NOT NULL,
  `job_fk` BIGINT UNSIGNED NOT NULL,
  `job_run_count` MEDIUMINT NULL DEFAULT NULL,
  `sha` VARCHAR(128) NULL DEFAULT NULL,
  `run_date` DATETIME NOT NULL DEFAULT (utc_timestamp()),
  `is_success` tinyint(1) NOT NULL DEFAULT 1,
  -- NEW COLUMNS ADDED HERE:
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  PRIMARY KEY (`run_id`),
  -- ... rest of table definition
)
```

**Implementation steps:**
1. Edit `V1.0__reportcard_mysql_ddl.sql` CREATE TABLE statement
2. Apply manually to local MySQL: `mysql -u root -p reportcard < V1.0__reportcard_mysql_ddl.sql`
3. Run `./gradlew generateJooqSchemaSource`
4. Verify generated code in `src/generated/java/`

### Pattern 2: Reference Table Value Addition
**What:** Add new enum values to reference tables (test_status, storage_type, fault_context)
**When to use:** Adding new types like `KARATE` to `storage_type`
**Example:**
```sql
-- Source: V1.1__reportcard_mysql_dml.sql (project pattern)
INSERT `reportcard`.`storage_type`
(`storage_type_id`, `storage_type_name`)
VALUES (1, 'HTML'),
       (2, 'JSON'),
       (3, 'LOG'),
       (4, 'OTHER'),
       (5, 'TAR_GZ'),
       (6, 'XML'),
       (7, 'ZIP'),
       (8, 'JUNIT'),
       (9, 'KARATE');  -- NEW VALUE
```

**Java enum must be synchronized:**
```java
// Source: reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StorageType.java
public enum StorageType {
    HTML(1),
    JSON(2),
    LOG(3),
    OTHER(4),
    TAR_GZ(5),
    XML(6),
    ZIP(7),
    JUNIT(8),
    KARATE(9),  // NEW VALUE - ID must match database
    ;

    StorageType(int storageTypeId) {
        this.storageTypeId = storageTypeId;
    }
    // ... rest of enum implementation
}
```

### Pattern 3: JOOQ Code Regeneration
**What:** Regenerate type-safe Java classes after schema changes
**When to use:** After ANY database schema change (DDL or DML)
**Example:**
```bash
# Source: project build.gradle task
./gradlew generateJooqSchemaSource

# This task:
# 1. Connects to local MySQL (localhost:3306/reportcard)
# 2. Reads current schema metadata
# 3. Generates Java classes in src/generated/java/io/github/ericdriggs/reportcard/gen/db/
# 4. Uses JavaLombokGenerator for Lombok annotations
# 5. Maps DATETIME to Java Instant via ForcedType configuration
```

**JOOQ configuration highlights from build.gradle:**
- Generator: `org.jooq.codegen.JavaLombokGenerator`
- ForcedType: `DATETIME` → `Instant` (all datetime columns)
- ForcedType: `tinyint` → `Boolean` (columns matching `is_*` or `has_*`)
- Target package: `io.github.ericdriggs.reportcard.gen.db`
- Generates: DAOs, Records, POJOs with fluent setters

### Pattern 4: Test Schema Initialization
**What:** Testcontainers MySQL loads schema via docker-entrypoint-initdb.d
**When to use:** Understanding how tests get schema changes
**Example:**
```java
// Source: MyEmbeddedMysql.java
mySQLContainer = new MySQLContainer<>("mysql:8.0.33")
    .withDatabaseName(schema)
    .withCopyFileToContainer(
        MountableFile.forClasspathResource(ddlsql),
        "/docker-entrypoint-initdb.d/0_schema.sql"
    )
    .withCopyFileToContainer(
        MountableFile.forClasspathResource(dmlsql),
        "/docker-entrypoint-initdb.d/1_config.sql"
    )
    .withCopyFileToContainer(
        MountableFile.forClasspathResource("db/test/test-data.dml.sql"),
        "/docker-entrypoint-initdb.d/2_data.sql"
    );
```

**Key insight:** Tests automatically get schema changes because they run the same DDL/DML files. No separate test schema maintenance needed.

### Anti-Patterns to Avoid
- **Editing generated code directly:** JOOQ code in `src/generated/` is overwritten on regeneration
- **Creating ALTER TABLE scripts:** Project doesn't use ALTER TABLE; edit CREATE TABLE directly
- **Skipping JOOQ regeneration:** Business logic will compile against old schema and fail at runtime
- **Mismatched enum IDs:** Java enum IDs must match database reference table IDs exactly

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Database access layer | Custom JDBC with SQL strings | JOOQ generated code | Type-safe queries, compile-time validation, automatic refactoring on schema changes |
| SQL type mapping | Manual ResultSet.getX() calls | JOOQ Records/POJOs | Automatic type conversion (DATETIME→Instant), null handling, generated getters/setters |
| Test database setup | Manual schema scripts in @BeforeEach | Testcontainers with docker-entrypoint-initdb.d | Automatic initialization, isolation, uses same DDL/DML as production |
| Reference table sync | Separate documentation | Java enum with static map | Runtime lookup, compile-time validation, single source of truth |

**Key insight:** JOOQ code generation eliminates an entire class of runtime SQL errors by catching schema mismatches at compile time. Don't bypass this by using raw SQL strings.

## Common Pitfalls

### Pitfall 1: JOOQ Regeneration Amnesia
**What goes wrong:** Schema changes applied to database but JOOQ code not regenerated, leading to "column not found" or "type mismatch" errors at runtime
**Why it happens:** The `generateJooqSchemaSource` task is a manual step, not automatic on build
**How to avoid:**
- Always run `./gradlew generateJooqSchemaSource` after schema changes
- Verify generated code changes in git diff before committing
- Consider adding a CI check that generated code is up-to-date
**Warning signs:**
- Compilation succeeds but runtime SQL errors
- Missing setter/getter methods for new columns
- Type mismatches in persistence layer

### Pitfall 2: Manual Schema Drift
**What goes wrong:** Local MySQL schema differs from DDL files, causing "table already exists" or "column already exists" errors when applying DDL
**Why it happens:** Developers manually ALTER TABLE for testing, forgetting DDL files are the source of truth
**How to avoid:**
- Always edit DDL files first, then apply
- Use Docker MySQL containers that can be destroyed/rebuilt
- Run full DDL from scratch periodically to verify it works
**Warning signs:**
- DDL script fails with "already exists" errors
- Different developers have different schemas
- Tests pass locally but fail in CI

### Pitfall 3: Storage Type Enum Coordination
**What goes wrong:** Java enum and database reference table get out of sync, causing "unknown storage type ID" errors
**Why it happens:** Two sources of truth (SQL INSERT and Java enum) must be manually synchronized
**How to avoid:**
- Update BOTH in the same commit
- Use sequential IDs, never reuse deleted IDs
- Add a test that validates enum IDs match database IDs
**Warning signs:**
- NullPointerException in `StorageType.fromStorageTypeId()`
- Enum constant exists but database lookup fails
- Database has more storage_type rows than enum values

### Pitfall 4: NULLable vs NOT NULL Confusion
**What goes wrong:** Adding NOT NULL columns breaks existing rows, causing "column cannot be null" errors
**Why it happens:** Forgetting that existing rows need values for new columns
**How to avoid:**
- Make new columns NULLable for backwards compatibility
- If NOT NULL needed, add in two phases: (1) NULLable with backfill, (2) ALTER to NOT NULL
- Use DEFAULT clause for NOT NULL columns
**Warning signs:**
- Migration fails on non-empty tables
- "Cannot add or update a child row" foreign key errors
- Data loss from failed migrations

### Pitfall 5: DATETIME vs TIMESTAMP Confusion
**What goes wrong:** Expecting timezone conversion from DATETIME columns (which don't support it)
**Why it happens:** Misunderstanding MySQL's DATETIME behavior (stores as-is, no timezone conversion)
**How to avoid:**
- Understand DATETIME is timezone-naive, always interpret in same timezone (project uses UTC)
- Rely on JOOQ's Instant mapping, which is timezone-neutral
- Document that all DATETIME columns are UTC in schema comments
**Warning signs:**
- Timezone-related bugs when users in different timezones
- Confusion about "local time" vs "UTC time"
- DateTime values shifting when timezone changes

## Code Examples

Verified patterns from official sources:

### Adding Columns (MySQL INSTANT Algorithm)
```sql
-- Source: MySQL 8.0 ALTER TABLE documentation
-- INSTANT algorithm is default for InnoDB in MySQL 8.0.12+
-- Adds column metadata without table rebuild - instant operation

ALTER TABLE `reportcard`.`run`
  ADD COLUMN `start_time` DATETIME NULL,
  ADD COLUMN `end_time` DATETIME NULL;

-- Multiple columns in single statement is more efficient
-- NULL allows backwards compatibility with existing rows
-- DATETIME maps to Java Instant via JOOQ ForcedType
```

### JOOQ Generated Code Usage
```java
// Source: JOOQ documentation pattern
// Generated code provides type-safe access

import static io.github.ericdriggs.reportcard.gen.db.tables.RunTable.RUN;

// Type-safe query with new columns
Result<RunRecord> runs = dslContext
    .selectFrom(RUN)
    .where(RUN.START_TIME.isNotNull())
    .and(RUN.END_TIME.isNotNull())
    .fetch();

// Type-safe setters (generated after schema change)
RunRecord run = dslContext.newRecord(RUN);
run.setRunReference("build-123");
run.setStartTime(Instant.now());
run.setEndTime(Instant.now().plusSeconds(300));
run.store();

// Java types are enforced: setStartTime(Instant) not String
```

### Reference Table Synchronization
```java
// Source: Project StorageType.java pattern
// Enum provides type-safe lookup by ID from database

public enum StorageType {
    KARATE(9);  // ID must match storage_type.storage_type_id

    final int storageTypeId;
    final static Map<Integer, StorageType> idStorageTypeMap = new HashMap<>();

    static {
        for (StorageType s : StorageType.values()) {
            idStorageTypeMap.put(s.getStorageTypeId(), s);
        }
    }

    public static StorageType fromStorageTypeId(int storageTypeId) {
        return idStorageTypeMap.get(storageTypeId);
    }
}

// Usage in business logic:
Integer storageTypeId = storageRecord.getStorageType();
StorageType type = StorageType.fromStorageTypeId(storageTypeId);
// type is now type-safe enum, not just an integer
```

### Test Schema Verification
```java
// Source: Project test pattern with Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class RunPersistTest extends AbstractTestResultPersistTest {

    @Test
    void testNewColumns() {
        // Test automatically gets schema changes via Testcontainers
        // No manual schema setup needed
        RunRecord run = dslContext.newRecord(RUN);
        run.setStartTime(Instant.parse("2024-01-15T10:30:00Z"));
        run.setEndTime(Instant.parse("2024-01-15T10:35:00Z"));
        run.store();

        // Verify columns exist and values persist
        RunRecord fetched = dslContext.selectFrom(RUN)
            .where(RUN.RUN_ID.eq(run.getRunId()))
            .fetchOne();
        assertNotNull(fetched.getStartTime());
        assertNotNull(fetched.getEndTime());
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Flyway migrations | Manual DDL/DML files | Early in project (commented out in build.gradle) | More manual but direct control; no automatic migration on startup |
| ALTER TABLE scripts | Edit CREATE TABLE directly | Project standard since inception | Requires drop/recreate for fresh installs; simpler version control |
| TIMESTAMP columns | DATETIME columns | Project standard | No timezone conversion; app must handle UTC explicitly |
| Manual SQL strings | JOOQ generated code | Project standard since inception | Type-safe queries, compile-time validation |

**Deprecated/outdated:**
- Flyway: Plugin is commented out in `build.gradle` line 22. Do not attempt to re-enable without understanding implications.
- Separate ALTER TABLE scripts: Project uses edit-in-place for CREATE TABLE. Adding ALTER TABLE scripts will break the established pattern.

## Open Questions

Things that couldn't be fully resolved:

1. **Production Schema Migration Process**
   - What we know: Local development uses manual DDL application
   - What's unclear: How production databases are updated (manual SQL execution? deployment scripts?)
   - Recommendation: Document production migration process separately; assume manual DBA execution for this phase

2. **JOOQ Regeneration in CI/CD**
   - What we know: Local developers must run `./gradlew generateJooqSchemaSource` manually
   - What's unclear: Is there a CI check that generated code is up-to-date?
   - Recommendation: Verify generated code in git diff during planning; consider adding CI verification

3. **Storage Type Enum Test Coverage**
   - What we know: Enum must sync with database reference table
   - What's unclear: Are there automated tests that validate ID synchronization?
   - Recommendation: Add test verification task to plan to prevent future drift

## Sources

### Primary (HIGH confidence)
- Official JOOQ documentation: https://www.jooq.org/doc/latest/manual/code-generation/ (Code generation workflow and best practices)
- Official JOOQ documentation: https://www.jooq.org/doc/latest/manual/getting-started/ (Database-first development pattern)
- Official MySQL 8.0 documentation: https://dev.mysql.com/doc/refman/8.0/en/alter-table.html (ALTER TABLE best practices, INSTANT algorithm)
- Official MySQL 8.0 documentation: https://dev.mysql.com/doc/refman/8.0/en/datetime.html (DATETIME behavior, timezone handling)
- Project source code: `reportcard-server/build.gradle` (JOOQ configuration, ForcedType mappings)
- Project source code: `reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql` (DDL patterns)
- Project source code: `reportcard-server/src/main/resources/db/migration/V1.1__reportcard_mysql_dml.sql` (DML patterns)
- Project source code: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StorageType.java` (Enum pattern)

### Secondary (MEDIUM confidence)
- Git commit history: Commits `b51d5c8`, `7b7f22a` demonstrate established pattern of editing CREATE TABLE directly
- Project documentation: `README_AI.md` section on "Database Schema" and "Danger Zones"

### Tertiary (LOW confidence)
- None - all findings verified with project code or official documentation

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Verified from build.gradle and JOOQ/MySQL official documentation
- Architecture: HIGH - Verified from actual codebase patterns and git history
- Pitfalls: HIGH - Derived from project patterns and MySQL/JOOQ documentation

**Research date:** 2026-01-26
**Valid until:** 2026-02-26 (30 days - MySQL/JOOQ are stable technologies, but verify current project state)
