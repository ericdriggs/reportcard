# Domain Pitfalls: Adding Karate JSON Support to Test Result API

**Domain:** Test result ingestion and storage system
**Researched:** 2026-01-26

## Critical Pitfalls

Mistakes that cause rewrites or major issues.

### Pitfall 1: JOOQ Regeneration Amnesia
**What goes wrong:** Schema changes are made and applied to MySQL, but JOOQ code is not regenerated before implementing business logic. Code compiles against old generated classes, causing runtime failures or incorrect database access.

**Why it happens:**
- JOOQ generates code in `src/generated/` which isn't immediately visible
- Developers forget the schema → JOOQ regeneration → business logic sequence
- Build succeeds locally with old generated code, fails in CI or after clean build

**Consequences:**
- Runtime failures when accessing new columns ("column not found" errors)
- Type mismatches between code expectations and actual schema
- Incorrect query generation
- Regression bugs when old generated code masks new column access
- Wasted time debugging "invisible" issues

**Prevention:**
1. **Mandatory sequence:**
   - Modify `db/migration/V*.sql`
   - Apply SQL to local MySQL manually
   - Run `./gradlew generateJooqSchemaSource` BEFORE any code changes
   - Verify generated classes in `src/generated/java/io/github/ericdriggs/reportcard/gen/db/`
2. **Document in migration file:** Add comment header showing regeneration command
3. **CI check:** Consider adding build step that fails if schema and generated code are out of sync

**Detection:**
- Build passes but tests fail with database errors
- "Unknown column" exceptions at runtime
- Generated JOOQ classes missing expected fields
- Git diff shows schema changes but no corresponding generated code changes

---

### Pitfall 2: No Flyway, Manual Schema Drift
**What goes wrong:** Schema changes in `db/migration/V*.sql` files are not manually applied to production database, causing production to have different schema than expected. Flyway naming convention creates false expectation of automatic migration.

**Why it happens:**
- Files follow Flyway naming convention (`V1.X__description.sql`)
- Flyway is commented out in build.gradle but convention remains
- Developers assume migrations run automatically
- No automated tracking of which migrations have been applied

**Consequences:**
- Production schema out of sync with code expectations
- Silent failures in production (new columns don't exist)
- Rollback complexity (no automated version tracking)
- Manual coordination required across environments
- Risk of applying migrations out of order

**Prevention:**
1. **Explicit documentation:** Add WARNING in each migration file stating it's manual
2. **Deployment checklist:** Manual schema verification step before deployment
3. **Schema version table:** Consider adding manual tracking table showing applied migrations
4. **Pre-deployment validation:** Script to compare expected schema (from SQL) vs actual database
5. **Environment parity:** Test schema changes in test environment before production

**Detection:**
- Production errors mentioning unknown columns
- JOOQ exceptions about missing tables/columns
- Feature works in test/dev but fails in production
- Database queries return unexpected null values

---

### Pitfall 3: Type Coercion Blindness (JSON, DECIMAL, TINYINT)
**What goes wrong:** MySQL type conversions between schema and JOOQ-generated Java code cause data loss, precision issues, or runtime type errors. Common with JSON, DECIMAL, and TINYINT columns.

**Why it happens:**
- MySQL `JSON` → Java `String` or `JSONB` depending on JOOQ configuration
- MySQL `DECIMAL(9,3)` → Java `BigDecimal` (not `Double`)
- MySQL `TINYINT(1)` → Java `Byte` or `Boolean` depending on JOOQ generator config
- Implicit conversions hide problems until runtime

**Consequences:**
- JSON data stored as String requires manual parsing (double serialization)
- Precision loss if DECIMAL treated as floating point
- Boolean logic breaks if TINYINT mapped as Byte
- Data truncation on write
- Query failures with type mismatches

**Prevention:**
1. **JOOQ configuration:** Verify `jooq-codegen-maven` configuration for type mappings
2. **Type verification:** After JOOQ regeneration, inspect generated Record classes
3. **Test with real data:** Integration tests with actual JSON/DECIMAL/TINYINT values
4. **Document mappings:** Create table of MySQL type → JOOQ type → Java type mappings
5. **Karate JSON specific:** Verify `elapsedTime`/`totalTime` map to correct numeric type

**Detection:**
- ClassCastException at runtime
- JSON parsing errors
- Precision loss in timing calculations
- Boolean fields returning numeric values
- Query builder type errors

---

### Pitfall 4: Nullable Column Assumptions
**What goes wrong:** New additive columns are added as `NOT NULL` with defaults, but existing rows don't get defaults applied correctly, or NULL handling in Java code causes NullPointerException.

**Why it happens:**
- SQL defaults apply only to NEW inserts, not existing rows
- Developers assume `NOT NULL DEFAULT X` backfills existing data
- Java code doesn't null-check new optional fields
- Integration between JUnit (no Karate data) and Karate (has Karate data) creates NULL scenarios

**Consequences:**
- NullPointerException when accessing new columns on old data
- Data inconsistency between old and new runs
- Query failures when filtering on new columns
- Dashboard display errors for historical data

**Prevention:**
1. **Additive columns should be NULL-able:** Accept `NULL` for new columns on existing data
2. **Backfill strategy:** If `NOT NULL` required, explicit UPDATE to backfill existing rows
3. **Java null safety:** Use `@Nullable` annotations, Optional<T>, or null checks
4. **Two-phase migration:**
   - Phase 1: Add column as NULL-able
   - Phase 2: Backfill data, then ALTER to NOT NULL
5. **Test with existing data:** Integration tests using pre-existing test_result rows

**Detection:**
- NullPointerException in logs
- SQL constraint violations on INSERT
- UI displaying "null" or blanks for new fields on old data
- Integration tests fail but unit tests pass

---

### Pitfall 5: Storage Type Enum Coordination
**What goes wrong:** New `StorageType.KARATE` added in Java enum but corresponding row not inserted into `storage_type` table, causing foreign key constraint violations.

**Why it happens:**
- Enum exists in two places: Java code AND database reference table
- Manual DML required to sync the two
- Easy to forget `V1.1__reportcard_mysql_dml.sql` update
- Tests may mock storage type, masking the issue

**Consequences:**
- Foreign key constraint violations on INSERT into `storage` table
- Storage operations fail silently or with cryptic FK errors
- Production failure when code references new storage type
- Inconsistent behavior between test (mocked) and production (real DB)

**Prevention:**
1. **Synchronized update checklist:**
   - Add enum value in Java `StorageType.java`
   - Add INSERT into `storage_type` table in migration SQL
   - Regenerate JOOQ (picks up new storage_type rows)
   - Update tests with new storage type
2. **Validation test:** Integration test verifying Java enum values match database rows
3. **Documentation:** Comment in Java enum listing corresponding DB IDs

**Detection:**
- Foreign key constraint violation errors
- `storage_type` value not found
- Tests pass but production fails
- Storage upload succeeds but storage row not created

---

## Moderate Pitfalls

Mistakes that cause delays or technical debt.

### Pitfall 6: Parser Ambiguity Between Formats
**What goes wrong:** Logic to distinguish between JUnit XML and Karate JSON is fragile or incorrect, causing wrong parser to be used on input files.

**Why it happens:**
- Both formats may be in same tar.gz upload
- File extension alone is insufficient (`.karate-json.txt` vs `.xml`)
- Content-based detection can misfire on edge cases
- Lazy evaluation tries first parser, falls back on error (brittle)

**Consequences:**
- Wrong parser used, throws exceptions
- Partial data loss (some files parsed, others skipped)
- User confusion about which format is expected
- Difficult to debug (logs show parse errors, not format detection issues)

**Prevention:**
1. **Explicit detection:** Check file extension AND content structure
2. **Separate multipart parameters:** `junit.tar.gz` and `karate.tar.gz` as distinct uploads
3. **Early validation:** Fail fast if format detection ambiguous
4. **Clear error messages:** "Expected JSON format but received XML" vs generic parse failure
5. **Test edge cases:** Empty files, mixed formats, malformed content

**Detection:**
- Parse exceptions in logs
- Test results missing from expected upload
- Wrong StorageType assigned to uploaded files
- User reports "upload succeeded" but no test data visible

---

### Pitfall 7: Test Data Seed Incompleteness
**What goes wrong:** Test schema includes new columns/tables, but test data seed files (`2_data.sql`) don't include sample data for new entities, causing integration tests to fail.

**Why it happens:**
- Schema DDL updated (`V1.0__reportcard_mysql_ddl.sql`)
- Reference data updated (`V1.1__reportcard_mysql_dml.sql`)
- Test seed data (`2_data.sql`) forgotten
- Tests use Testcontainers which runs all three files on startup

**Consequences:**
- Integration tests fail with "not found" errors
- Tests can't reference new storage types or run-level timing
- False negatives (test fails due to missing seed data, not actual bug)
- Slower test development (must manually create test data each time)

**Prevention:**
1. **Synchronized update:** When adding reference data row, add to both DML and test seed
2. **Separate test seed section:** Mark sections in `2_data.sql` for reference data vs test scenarios
3. **Test data validation:** Integration test verifying seed data completeness
4. **Documentation:** Comment in DDL linking to test seed file

**Detection:**
- Integration tests fail with "storage_type not found"
- Foreign key constraint violations in tests
- Tests pass individually but fail when run together
- Testcontainers startup logs show INSERT failures

---

### Pitfall 8: Run-Level vs Test-Level Attribute Confusion
**What goes wrong:** Wall clock timing (run-level) and sum-of-test-times (test-level) are confused, stored in wrong table, or displayed incorrectly in UI.

**Why it happens:**
- Existing `test_result.time` is sum of test durations
- New Karate `elapsedTime` is wall clock for entire run
- Both are "duration" but different scopes
- UI/API conflates the two concepts

**Consequences:**
- Dashboard displays wrong timing value
- Users can't distinguish between test execution time and job overhead time
- Metrics/trends compare incompatible values
- Queries return misleading results

**Prevention:**
1. **Clear naming:** `run.elapsed_time_millis` vs `test_result.time` (sum of tests)
2. **Documentation:** Schema comments explaining difference
3. **UI labeling:** "Wall Clock Time" vs "Test Execution Time"
4. **Separate API fields:** Don't overload "time" field with multiple meanings
5. **Example scenarios:** Document when they differ (parallel tests, setup/teardown overhead)

**Detection:**
- Timing values don't make sense (run time < sum of test times for serial execution)
- Dashboard comparisons show inconsistent trends
- User confusion about which timing is which

---

### Pitfall 9: Multipart Parameter Naming Conflicts
**What goes wrong:** New `karate.tar.gz` multipart parameter conflicts with existing parameter names or is inconsistently named across endpoints.

**Why it happens:**
- Existing endpoint uses `junit.tar.gz` and `storage.tar.gz`
- New parameter adds `karate.tar.gz`
- Inconsistent naming (kebab-case vs camelCase vs snake_case)
- Client library uses different parameter names than REST API

**Consequences:**
- API returns "missing parameter" errors
- Client library doesn't match server expectations
- Documentation shows wrong parameter names
- Backwards compatibility broken if parameter renamed

**Prevention:**
1. **Consistent naming convention:** Follow existing pattern (`junit.tar.gz` → `karate.tar.gz`)
2. **Optional parameter:** Ensure new parameter is optional for backwards compatibility
3. **Client library sync:** Update Java client with new parameter before server deployment
4. **OpenAPI documentation:** Update Swagger annotations with correct parameter names
5. **Integration tests:** Test all combinations (with/without Karate JSON)

**Detection:**
- 400 Bad Request with "missing required parameter"
- Client library upload fails
- API documentation doesn't match implementation

---

### Pitfall 10: Cache Invalidation Blindness
**What goes wrong:** Hierarchical async cache layer (`AbstractAsyncCache` pattern) not invalidated when new run-level timing data is added, causing stale data in browse/dashboard views.

**Why it happens:**
- Cache layer optimizes browse queries with deep joins
- New run-level columns added to `run` table
- Cache keys don't include new columns
- Cache TTL longer than test cycle, serving stale data

**Consequences:**
- Users see old data without new timing information
- Inconsistent data between cached and non-cached views
- Cache invalidation requires manual intervention or restart
- Performance optimization (cache) becomes correctness problem

**Prevention:**
1. **Cache key versioning:** Include schema version in cache keys
2. **Cache invalidation on write:** When run data updated, invalidate related cache entries
3. **Shorter TTL during development:** Reduce cache duration for new features
4. **Cache bypass parameter:** Allow query parameter to skip cache for testing
5. **Understand cache pattern:** Review `AbstractAsyncCache` before modifying cached entities

**Detection:**
- New data not appearing in browse views
- Different results between direct DB query and API response
- Cache hits in logs but missing expected data
- Data appears after cache expiration or server restart

---

## Minor Pitfalls

Mistakes that cause annoyance but are fixable.

### Pitfall 11: JOOQ Generated Code Not in .gitignore
**What goes wrong:** Generated JOOQ code is committed to version control, causing merge conflicts and repo bloat.

**Why it happens:**
- `.gitignore` doesn't include `src/generated/`
- Generated code looks like regular code
- IDE auto-adds files to git

**Consequences:**
- Frequent merge conflicts on generated files
- Repo size increases unnecessarily
- False code review noise (reviewing generated code)

**Prevention:**
1. **Verify .gitignore:** Ensure `src/generated/` is excluded
2. **Build configuration:** Generate code as part of build process
3. **Documentation:** Clarify that generated code should not be committed

**Detection:**
- Merge conflicts in `gen/db/` files
- Large diffs showing generated code changes
- Pull requests including generated files

---

### Pitfall 12: Missing Jackson/Gson Annotations on Models
**What goes wrong:** Karate JSON deserialization fails or produces incorrect Java objects due to missing or incorrect JSON mapping annotations.

**Why it happens:**
- New model classes for Karate JSON don't have `@JsonProperty` annotations
- Field names don't match JSON keys (Java camelCase vs JSON snake_case)
- Null handling not configured properly

**Consequences:**
- JSON parsing throws exceptions
- Fields populated with null despite data in JSON
- Incorrect type conversions
- Silent data loss

**Prevention:**
1. **Explicit annotations:** Use `@JsonProperty("field_name")` on all fields
2. **Null handling:** Configure `@JsonInclude(JsonInclude.Include.NON_NULL)`
3. **Test deserialization:** Unit tests with sample Karate JSON files
4. **Document format:** Include sample JSON in test resources

**Detection:**
- JSON parsing exceptions
- Null fields after deserialization
- Type conversion errors
- Missing data in persisted records

---

### Pitfall 13: S3 Bucket Prefix Collision
**What goes wrong:** New Karate storage files overwrite or conflict with existing JUnit storage due to identical S3 prefix generation logic.

**Why it happens:**
- `StoragePath` generates prefix from stagePath + label
- Label choice conflicts with existing labels
- No validation preventing duplicate prefixes per stage

**Consequences:**
- Files overwritten in S3
- Wrong files returned when querying storage
- Storage links point to incorrect content

**Prevention:**
1. **Unique labels:** Use distinct labels per storage type (`junit`, `karate`, `html`)
2. **Validation:** Enforce unique constraint on `storage.label` per stage
3. **Prefix testing:** Integration test verifying prefix uniqueness

**Detection:**
- Files in S3 don't match expected content
- Storage retrieval returns wrong file types
- Storage table shows duplicate labels

---

### Pitfall 14: Missing Integration Test Scenarios
**What goes wrong:** New Karate JSON functionality works in isolation but breaks when combined with existing JUnit upload or when Karate JSON is omitted.

**Why it happens:**
- Unit tests mock dependencies
- Integration tests only test "happy path"
- Edge cases not covered (upload with/without Karate, mixed formats, malformed JSON)

**Consequences:**
- Production bugs that tests didn't catch
- Regression in existing functionality
- Poor user experience with unclear error messages

**Prevention:**
1. **Combination testing:** Test all parameter combinations
   - JUnit only (existing behavior)
   - JUnit + Karate (new behavior)
   - Karate only (if supported)
   - Neither (error case)
2. **Edge case scenarios:**
   - Empty Karate JSON
   - Malformed JSON
   - Missing timing fields
   - Multiple Karate files in tar.gz
3. **Backwards compatibility test:** Ensure existing uploads still work

**Detection:**
- Production errors not caught by tests
- User reports of specific failure scenarios
- Regression in existing functionality

---

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation |
|-------------|---------------|------------|
| Schema Design | JOOQ regeneration forgotten | Document sequence: SQL → JOOQ → code |
| Schema Migration | Manual Flyway forgotten | Add deployment checklist step |
| Model Design | Type coercion on JSON/DECIMAL | Verify generated JOOQ type mappings |
| Parser Implementation | Format detection ambiguity | Separate multipart parameters or explicit detection |
| Storage Implementation | StorageType enum out of sync | Synchronized Java enum + DB insert |
| API Design | Multipart parameter naming | Follow existing convention (junit.tar.gz pattern) |
| Testing | Missing test data seeds | Update `2_data.sql` with new reference data |
| Cache Integration | Cache invalidation missed | Review `AbstractAsyncCache` before entity changes |
| Client Library | Parameter name mismatch | Update Java client in sync with server |
| Documentation | Timing terminology confusion | Clear labeling: wall clock vs sum-of-tests |

---

## Sources

**HIGH CONFIDENCE (codebase evidence):**
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` - Existing upload pattern
- `/reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql` - Schema structure
- `README_AI.md` - JOOQ generation process, manual schema management, cache architecture
- `docs/schema/schema.mermaid` - Database relationships and constraints
- `.planning/PROJECT.md` - Karate integration requirements

**MEDIUM CONFIDENCE (domain knowledge):**
- Common pitfalls in additive schema changes based on database versioning patterns
- JOOQ code generation workflow from MySQL schemas
- Multipart file upload REST API patterns

**LOW CONFIDENCE (needs verification):**
- Specific Karate JSON format (`.karate-json.txt` structure)
- Exact JOOQ type mapping configuration for this project
- Current cache invalidation strategy in `AbstractAsyncCache`

---

## Verification Needed

The following areas need deeper investigation during implementation:

1. **JOOQ Configuration:** Verify type mappings in `reportcard-jooq-generator` for JSON, DECIMAL, TINYINT
2. **Cache Strategy:** Understand `AbstractAsyncCache` invalidation logic before modifying cached entities
3. **Karate JSON Format:** Obtain sample `.karate-json.txt` files to verify field names and types
4. **Storage Path Logic:** Review `StoragePath.java` to understand prefix generation and collision prevention
5. **Client Library:** Review `reportcard-client` to understand existing upload API and required changes
