---
phase: 01-schema-foundation
plan: 01
subsystem: database
tags: [mysql, jooq, schema, datetime, karate]

# Dependency graph
requires:
  - phase: none
    provides: N/A (first phase)
provides:
  - run table with start_time and end_time columns for wall clock timing
  - KARATE storage type (ID 9) in database and Java enum
  - JOOQ generated code with Instant-typed timing accessors
affects: [02-karate-parser, 03-api-upload, 04-dashboard]

# Tech tracking
tech-stack:
  added: []
  patterns: [additive-schema-changes, nullable-new-columns, jooq-regeneration-workflow]

key-files:
  created: []
  modified:
    - reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql
    - reportcard-server/src/main/resources/db/migration/V1.1__reportcard_mysql_dml.sql
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StorageType.java
    - reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/records/RunRecord.java
    - reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/RunTable.java

key-decisions:
  - "Added start_time and end_time as NULLable DATETIME columns for backwards compatibility"
  - "KARATE assigned ID 9 following sequential numbering pattern"
  - "JOOQ regeneration requires manual database schema update before code generation"

patterns-established:
  - "Schema change workflow: 1) Edit DDL/DML files, 2) Apply to local MySQL, 3) Regenerate JOOQ"
  - "New columns must be NULLable to avoid breaking existing data"
  - "Enum IDs must exactly match database reference data IDs"

# Metrics
duration: 4min
completed: 2026-01-27
---

# Phase 1 Plan 1: Schema Foundation Summary

**Added run-level timing columns (start_time, end_time) and KARATE storage type to MySQL schema with synchronized JOOQ code generation**

## Performance

- **Duration:** 4 min
- **Started:** 2026-01-27T02:04:55Z
- **Completed:** 2026-01-27T02:08:42Z
- **Tasks:** 3
- **Files modified:** 8

## Accomplishments
- Added start_time and end_time DATETIME NULL columns to run table DDL
- Added KARATE storage type with ID 9 to database DML and Java StorageType enum
- Successfully regenerated JOOQ code with Instant-typed timing accessors
- Verified full build succeeds with new schema changes

## Task Commits

Each task was committed atomically:

1. **Task 1: Add timing columns to run table DDL** - `44f5f49` (feat)
   - Added start_time and end_time DATETIME NULL columns after is_success
   - Positioned before PRIMARY KEY declaration
   - NULLable for backwards compatibility

2. **Task 2: Add KARATE storage type** - `ecb1b80` (feat)
   - Added (9, 'KARATE') to storage_type INSERT in DML
   - Added KARATE(9) enum constant in StorageType.java
   - Synchronized IDs between database and Java enum

3. **Task 3: Regenerate JOOQ code** - `e896c91` (feat)
   - Applied schema changes to local MySQL database
   - Regenerated RunTable, RunRecord, RunDao, RunPojo with timing fields
   - Methods use Java Instant type (DATETIME → Instant mapping)

## Files Created/Modified
- `reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql` - Run table with timing columns
- `reportcard-server/src/main/resources/db/migration/V1.1__reportcard_mysql_dml.sql` - KARATE storage type reference data
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StorageType.java` - KARATE enum constant
- `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/records/RunRecord.java` - Timing accessors (generated)
- `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/RunTable.java` - START_TIME and END_TIME fields (generated)
- `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/daos/RunDao.java` - Updated DAO (generated)
- `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/pojos/RunPojo.java` - Updated POJO (generated)

## Decisions Made

**Schema workflow discovered:** JOOQ regeneration requires three-step process:
1. Edit SQL DDL/DML files in db/migration/
2. Manually apply changes to local MySQL database via ALTER TABLE or INSERT
3. Run `./gradlew generateJooqSchemaSource` to regenerate Java code

This differs from Flyway-based workflows where migrations auto-apply. The project's V*.sql files are templates, not auto-executed migrations.

**Java version requirement:** JOOQ plugin (nu.studer 8.0) requires Java 17 but Gradle was running with Java 11. Set `JAVA_HOME` to Java 17 before running JOOQ generation task.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Manual database schema update required before JOOQ regeneration**
- **Found during:** Task 3 (JOOQ regeneration)
- **Issue:** JOOQ code generation failed to include new columns because local MySQL schema was not updated. First generation attempt connected to database but saw old schema (no start_time/end_time columns), generating unchanged Record7 instead of Record9.
- **Fix:** Manually applied schema changes via MySQL CLI:
  - `ALTER TABLE run ADD COLUMN start_time DATETIME NULL AFTER is_success, ADD COLUMN end_time DATETIME NULL AFTER start_time;`
  - `INSERT INTO storage_type (storage_type_id, storage_type_name) VALUES (9, 'KARATE');`
  - Then re-ran `./gradlew generateJooqSchemaSource`
- **Files modified:** Local MySQL database, then generated Java files
- **Verification:** RunRecord.java became Record9 with getStartTime/setStartTime/getEndTime/setEndTime methods
- **Committed in:** e896c91 (Task 3 commit)

**2. [Rule 3 - Blocking] Java 17 required for JOOQ Gradle plugin**
- **Found during:** Task 3 (JOOQ regeneration)
- **Issue:** Initial JOOQ generation failed with "No matching variant of nu.studer:gradle-jooq-plugin:8.0" error. Gradle was running with Java 11 (from PATH) but plugin requires Java 17.
- **Fix:** Set `JAVA_HOME` to Java 17 installation before running Gradle: `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`
- **Files modified:** None (environment variable only)
- **Verification:** JOOQ generation succeeded with BUILD SUCCESSFUL
- **Committed in:** N/A (environment config, not code change)

---

**Total deviations:** 2 auto-fixed (2 blocking issues)
**Impact on plan:** Both deviations were necessary blockers to complete Task 3. Manual database update is standard workflow for this project (not Flyway-automated). No scope creep.

## Issues Encountered

**Project workflow differs from typical Flyway pattern:** Despite V*.sql naming convention suggesting Flyway migrations, this project does NOT auto-apply migrations. DDL/DML files are templates that must be manually executed against local MySQL before JOOQ generation. This is now documented as a pattern for future phases.

**Gradle daemon Java version mismatch:** System had multiple Java versions installed (11, 17). Gradle wrapper picked up Java 11 by default, causing plugin compatibility failures. Resolved by explicitly setting JAVA_HOME for JOOQ tasks.

## Next Phase Readiness

**Ready for Phase 2 (Karate Parser):**
- Schema foundation complete with timing columns in place
- KARATE storage type available for classifying Karate test result files
- JOOQ generated code provides type-safe access to new columns
- Build passes cleanly with all schema changes

**Note for next phases:**
- Remember to apply DDL/DML changes to local MySQL before JOOQ regeneration
- Use Java 17 for Gradle tasks requiring JOOQ plugin
- Tests will automatically pick up schema changes via Testcontainers (docker-entrypoint-initdb.d)

**No blockers or concerns.**

---
*Phase: 01-schema-foundation*
*Completed: 2026-01-27*
