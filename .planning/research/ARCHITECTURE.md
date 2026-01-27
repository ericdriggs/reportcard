# Architecture Patterns: Karate JSON Integration

**Domain:** Test result aggregation system (multipart upload integration)
**Researched:** 2026-01-26
**Confidence:** HIGH

## Executive Summary

This research examines how to integrate Karate JSON test results into an existing multipart upload REST API that currently handles JUnit XML. The existing architecture uses Spring Boot with a layered approach: Controller → Service → Database (JOOQ) + Storage (S3). The Karate JSON integration requires minimal architectural changes due to the existing extensibility patterns, focusing primarily on adding a new parser component and making the Karate multipart optional.

**Key architectural insight:** The system already follows a Format-Parser-Model pattern that naturally accommodates new input formats. Karate JSON integration is a straightforward extension, not a rewrite.

## Recommended Architecture

### Current Architecture (Baseline)

```
┌─────────────────────────────────────────────────────────────────┐
│                        REST Controller Layer                     │
│  JunitController: POST /v1/api/junit/storage/{label}/tar.gz    │
│    - Accepts multipart: junit.tar.gz (required)                 │
│    - Accepts multipart: storage.tar.gz (required)               │
│    - Request params: company, org, repo, branch, sha, stage     │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Format Detection Layer                      │
│  JunitSurefireXmlParseUtil.parseTestXml(xmlContents)           │
│    - Auto-detects: JUnit vs Surefire vs TestNG                 │
│    - Routes to appropriate converter                            │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Format-Specific Parsers                      │
│  JunitConvertersUtil.fromXmlContents()                          │
│  SurefireConvertersUtil.fromTestXmlContent()                    │
│    - Each returns: List<TestSuiteModel>                         │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Unified Internal Model                      │
│  TestResultModel                                                 │
│    └─ List<TestSuiteModel>                                      │
│         └─ List<TestCaseModel>                                  │
│              └─ List<TestCaseFaultModel>                        │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Service Layer                            │
│  TestResultPersistService.insertTestResult()                    │
│    - Business logic (merge, dedupe, aggregate)                  │
│    - Transaction management                                      │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Persistence Layer                           │
│  JOOQ (type-safe SQL) → MySQL 8.0                              │
│    Tables: test_result → test_suite → test_case → test_fault   │
└─────────────────────────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Storage Layer                            │
│  S3Service.uploadTarGz() → AWS S3                              │
│    - Stores original files                                       │
│    - StorageType: JUNIT | HTML | KARATE (new)                  │
└─────────────────────────────────────────────────────────────────┘
```

### Proposed Architecture (With Karate JSON)

```
┌─────────────────────────────────────────────────────────────────┐
│                     REST Controller Layer                        │
│  JunitController: POST /v1/api/junit/storage/{label}/tar.gz    │
│    - Multipart: junit.tar.gz (optional ✓)                      │
│    - Multipart: storage.tar.gz (required)                       │
│    - Multipart: karate.tar.gz (optional ✓ NEW)                 │
│    - Request params: company, org, repo, branch, sha, stage     │
│    - Constraint: At least one of junit.tar.gz or karate.tar.gz │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                 Format Detection & Routing                       │
│  ┌────────────────────────────────────────────┐                 │
│  │ IF junit.tar.gz present:                   │                 │
│  │   JunitSurefireXmlParseUtil.parseTestXml() │                 │
│  │   → List<TestSuiteModel>                   │                 │
│  └────────────────────────────────────────────┘                 │
│  ┌────────────────────────────────────────────┐                 │
│  │ IF karate.tar.gz present: (NEW)            │                 │
│  │   KarateJsonParseUtil.parseKarateJson()    │                 │
│  │   → List<TestSuiteModel>                   │                 │
│  └────────────────────────────────────────────┘                 │
│  ┌────────────────────────────────────────────┐                 │
│  │ Merge results:                             │                 │
│  │   TestResultModel.add(junit, karate)       │                 │
│  └────────────────────────────────────────────┘                 │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Format-Specific Parsers                        │
│  JunitConvertersUtil (existing)                                 │
│  SurefireConvertersUtil (existing)                              │
│  KarateConvertersUtil (NEW)                                     │
│    - Parses Karate JSON structure                               │
│    - Maps to TestSuiteModel/TestCaseModel                       │
│    - Handles Karate-specific fields (scenarios, features)       │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Unified Internal Model (unchanged)              │
│  TestResultModel → TestSuiteModel → TestCaseModel               │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Service Layer (unchanged)                     │
│  TestResultPersistService.insertTestResult()                    │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                 Persistence + Storage (minor change)             │
│  MySQL (unchanged) + S3 (add StorageType.KARATE)               │
└─────────────────────────────────────────────────────────────────┘
```

## Component Boundaries

### Component 1: REST Controller (`JunitController`)

**Responsibility:** HTTP request handling, multipart file extraction, request validation

**Current interface:**
```java
POST /v1/api/junit/storage/{label}/tar.gz
    @RequestPart("junit.tar.gz") MultipartFile junitXmls (required)
    @RequestPart("storage.tar.gz") MultipartFile reports (required)
    @RequestParam company, org, repo, branch, sha, stage, jobInfo
```

**Proposed interface:**
```java
POST /v1/api/junit/storage/{label}/tar.gz
    @RequestPart(value="junit.tar.gz", required=false) MultipartFile junitXmls
    @RequestPart(value="karate.tar.gz", required=false) MultipartFile karateJson
    @RequestPart("storage.tar.gz") MultipartFile reports (required)
    @RequestParam company, org, repo, branch, sha, stage, jobInfo

    Validation: At least one of junit.tar.gz or karate.tar.gz must be present
```

**Key changes:**
1. Make `junit.tar.gz` optional (`required=false`)
2. Add new optional `karate.tar.gz` part
3. Add validation: at least one test result format required
4. Maintain backwards compatibility: existing clients sending only `junit.tar.gz` continue working

**Communicates with:**
- `TestXmlTarGzUtil` (extract files from tar.gz)
- `JunitSurefireXmlParseUtil` (parse JUnit XML)
- `KarateJsonParseUtil` (NEW - parse Karate JSON)
- `TestResultPersistService` (persist merged results)
- `StoragePersistService` (store files in S3)

### Component 2: Format Parsers (`JunitSurefireXmlParseUtil`, `KarateJsonParseUtil`)

**Responsibility:** Convert format-specific files to unified `TestResultModel`

**Current pattern (JunitSurefireXmlParseUtil):**
```java
Input: List<String> xmlContents
Process:
  - Detect format (JUnit vs Surefire vs TestNG)
  - Parse XML to intermediate model
  - Convert to TestSuiteModel list
Output: TestResultModel (contains List<TestSuiteModel>)
```

**New parser (KarateJsonParseUtil):**
```java
Input: List<String> jsonContents
Process:
  - Parse JSON to Karate-specific model
  - Map Karate features → TestSuiteModel
  - Map Karate scenarios → TestCaseModel
  - Map Karate steps/failures → TestCaseFaultModel
Output: TestResultModel (contains List<TestSuiteModel>)
```

**Key design decisions:**
- **Location:** `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/karate/`
- **Pattern:** Mirror existing `junit/` and `surefire/` converter packages
- **Naming:** `KarateConvertersUtil.fromJsonContents(String json)` returns `List<TestSuiteModel>`
- **Error handling:** Follow existing pattern - throw descriptive exceptions on parse failure

**Communicates with:**
- Controller layer (receives raw file contents)
- Internal model layer (outputs `TestResultModel`)
- Does NOT communicate with database or storage directly

### Component 3: Internal Model (`TestResultModel`, `TestSuiteModel`, `TestCaseModel`)

**Responsibility:** Unified representation of test results, format-agnostic

**Current structure:**
```java
TestResultModel
  - tests, errors, failures, skipped (aggregates)
  - time (duration)
  - isSuccess, hasSkip (derived)
  - List<TestSuiteModel> testSuites

TestSuiteModel
  - name, tests, errors, failures, skipped, time
  - List<TestCaseModel> testCases

TestCaseModel
  - name, className, time
  - testStatus (SUCCESS | FAILURE | ERROR | SKIPPED)
  - List<TestCaseFaultModel> testCaseFaults

TestCaseFaultModel
  - faultContext (FAILURE | ERROR | SKIPPED)
  - type, message, value (error details)
```

**Karate mapping strategy:**

| Karate Concept | Maps To | Notes |
|----------------|---------|-------|
| Feature file | TestSuiteModel | Feature name → suite name |
| Scenario | TestCaseModel | Scenario name → test case name |
| Scenario status | TestStatus | passed → SUCCESS, failed → FAILURE |
| Step failure | TestCaseFaultModel | Failed step → fault with message |
| Scenario duration | TestCaseModel.time | Feature duration sums scenarios |
| Background steps | (ignore) | Not visible in test results |
| Tags | (future) | Could map to custom metadata |

**Why this works:** Karate's feature/scenario hierarchy maps naturally to JUnit's suite/test hierarchy. No schema changes needed.

**Communicates with:**
- Format parsers (receives converted data)
- Service layer (passes unified model)
- Database layer (via JOOQ code generation)

### Component 4: Service Layer (`TestResultPersistService`)

**Responsibility:** Business logic, transaction management, orchestration

**Current method:**
```java
StagePathTestResult insertTestResult(
    StageDetails stageDetails,
    TestResultModel testResultModel
)
```

**Changes required:** NONE (format-agnostic)

**Why no changes:** Service layer operates on `TestResultModel`, which is identical whether sourced from JUnit XML or Karate JSON. Existing logic handles:
- Creating hierarchy (company → org → repo → branch → job → run → stage)
- Upserting test_result, test_suite, test_case, test_fault rows
- Aggregating counts (tests, errors, failures, skipped)
- Managing database transactions

**Communicates with:**
- Controller (receives `TestResultModel`)
- JOOQ DAOs (persists to database)
- `StagePathPersistService` (creates hierarchy if not exists)

### Component 5: Storage Layer (`StoragePersistService`, `S3Service`)

**Responsibility:** Store raw files in S3, track storage metadata in database

**Current storage types:**
```java
enum StorageType {
    JUNIT,   // JUnit XML tar.gz
    HTML,    // HTML report tar.gz
    // ... others
}
```

**Proposed addition:**
```java
enum StorageType {
    JUNIT,
    HTML,
    KARATE,  // NEW: Karate JSON tar.gz
}
```

**Storage path pattern (existing):**
```
s3://bucket/rc/{company}/{org}/{repo}/{branch}/{date}/{jobId}/{runCount}/{stage}/{label}/
```

**New Karate storage:**
```
s3://bucket/rc/{company}/{org}/{repo}/{branch}/{date}/{jobId}/{runCount}/{stage}/karate/
```

**Changes required:**
1. Add `StorageType.KARATE` enum value
2. Controller calls `storeKarate(stageId, karateJsonFile)` (mirror existing `storeJunit()`)
3. Database: `storage` table already supports arbitrary types via `storage_type_fk` foreign key
4. No schema changes needed (storage_type is reference data, not schema)

**Communicates with:**
- Controller (receives multipart files)
- S3 (uploads tar.gz files)
- MySQL (tracks storage metadata: path, label, upload status)

## Data Flow

### Flow 1: JUnit XML Only (Existing, Backwards Compatible)

```
1. Client POSTs multipart:
   - junit.tar.gz ✓
   - storage.tar.gz ✓

2. Controller extracts junit.tar.gz → List<String> xmlContents

3. JunitSurefireXmlParseUtil.parseTestXml(xmlContents)
   → TestResultModel

4. TestResultPersistService.insertTestResult(stageDetails, testResultModel)
   → Saves to MySQL

5. S3Service.uploadTarGz("junit", junitXmls)
   → Saves to S3

6. Response: StagePathStorageResultCountResponse
```

### Flow 2: Karate JSON Only (New)

```
1. Client POSTs multipart:
   - karate.tar.gz ✓
   - storage.tar.gz ✓

2. Controller extracts karate.tar.gz → List<String> jsonContents

3. KarateJsonParseUtil.parseKarateJson(jsonContents)
   → TestResultModel

4. TestResultPersistService.insertTestResult(stageDetails, testResultModel)
   → Saves to MySQL (same path as JUnit)

5. S3Service.uploadTarGz("karate", karateJson)
   → Saves to S3

6. Response: StagePathStorageResultCountResponse
```

### Flow 3: Both JUnit and Karate (New, Advanced Use Case)

```
1. Client POSTs multipart:
   - junit.tar.gz ✓
   - karate.tar.gz ✓
   - storage.tar.gz ✓

2. Controller extracts both:
   - junit.tar.gz → List<String> xmlContents
   - karate.tar.gz → List<String> jsonContents

3. Parse both formats:
   - junitModel = JunitSurefireXmlParseUtil.parseTestXml(xmlContents)
   - karateModel = KarateJsonParseUtil.parseKarateJson(jsonContents)

4. Merge models:
   - mergedModel = junitModel.add(karateModel)
   - TestResultModel.add() already exists for merging

5. TestResultPersistService.insertTestResult(stageDetails, mergedModel)
   → Single test_result row with combined counts

6. S3Service.uploadTarGz("junit", junitXmls)
   S3Service.uploadTarGz("karate", karateJson)
   → Both stored separately in S3

7. Response: StagePathStorageResultCountResponse
   (single response with combined test counts)
```

**Why merge works:** `TestResultModel.add()` already exists (line 153-166 in TestResultModel.java) for combining multiple test suite lists. Originally designed for merging multiple XML files, naturally extends to merging different formats.

## Patterns to Follow

### Pattern 1: Optional Multipart Parts with Validation

**What:** Spring Boot `@RequestPart` with `required=false` and custom validation

**When:** Adding optional file uploads that have dependencies (at least one required)

**Implementation:**
```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> upload(
    @RequestPart(value="junit.tar.gz", required=false) MultipartFile junitXmls,
    @RequestPart(value="karate.tar.gz", required=false) MultipartFile karateJson,
    @RequestPart("storage.tar.gz") MultipartFile reports
) {
    // Validation
    if (junitXmls == null && karateJson == null) {
        throw new IllegalArgumentException(
            "At least one test result format required: junit.tar.gz or karate.tar.gz"
        );
    }

    // Process each present part
    TestResultModel combined = new TestResultModel();
    if (junitXmls != null) {
        combined = combined.add(parseJunit(junitXmls));
    }
    if (karateJson != null) {
        combined = combined.add(parseKarate(karateJson));
    }

    // Continue with merged results
}
```

**Why this pattern:**
- Maintains backwards compatibility (existing clients work unchanged)
- Clear error messages when constraints violated
- Explicit null checks prevent NullPointerException
- Follows Spring Boot best practices for optional parts

**Source:** Spring Framework documentation on `@RequestPart` (current as of Spring Boot 2.6.15 used in this project)

### Pattern 2: Format-Specific Converter with Common Output

**What:** Each input format has dedicated converter, all output same model

**When:** Multiple input formats need unified processing downstream

**Structure:**
```
reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/
├── JunitSurefireXmlParseUtil.java (orchestrator)
├── junit/
│   └── JunitConvertersUtil.java → List<TestSuiteModel>
├── surefire/
│   └── SurefireConvertersUtil.java → List<TestSuiteModel>
└── karate/ (NEW)
    └── KarateConvertersUtil.java → List<TestSuiteModel>
```

**Pattern template:**
```java
public enum KarateConvertersUtil {
    ;//static methods only

    public static List<TestSuiteModel> fromJsonContents(String jsonContent) {
        // 1. Parse format-specific structure
        KarateFeature feature = parseKarateFeature(jsonContent);

        // 2. Convert to common model
        TestSuiteModel suite = TestSuiteModel.builder()
            .name(feature.getName())
            .tests(feature.getScenarios().size())
            .build();

        for (KarateScenario scenario : feature.getScenarios()) {
            suite.addTestCase(convertScenario(scenario));
        }

        return List.of(suite);
    }

    private static TestCaseModel convertScenario(KarateScenario scenario) {
        // Format-specific → common model mapping
    }
}
```

**Why this pattern:**
- Separation of concerns: each converter handles one format
- Easy to add new formats without modifying existing converters
- Common output model ensures downstream compatibility
- Mirrors existing architecture (JUnit, Surefire converters)

### Pattern 3: Additive Storage Types

**What:** New storage types added via enum extension, not schema changes

**When:** Supporting new file types in S3 storage

**Current (DML reference data):**
```sql
-- V1.1__reportcard_mysql_dml.sql
INSERT INTO storage_type (storage_type) VALUES
    ('HTML'),
    ('JUNIT');
```

**Addition for Karate:**
```sql
INSERT INTO storage_type (storage_type) VALUES
    ('KARATE');
```

**Java enum (updated):**
```java
public enum StorageType {
    HTML,
    JUNIT,
    KARATE;  // NEW
}
```

**Why this pattern:**
- No schema changes (storage_type table already exists)
- Enum provides type safety in code
- Database constraint (foreign key) enforces valid types
- Easy to add new types in future (e.g., PYTEST, CYPRESS)

**Critical note:** After adding to DML file, must:
1. Apply manually to local MySQL
2. Regenerate JOOQ code (`./gradlew generateJooqSchemaSource`)
3. Update Java enum in `StorageType.java`

## Anti-Patterns to Avoid

### Anti-Pattern 1: Required Multipart Change Breaking Backwards Compatibility

**What:** Changing existing `@RequestPart("junit.tar.gz")` to `required=true` when adding Karate support

**Why bad:** Existing clients sending only `junit.tar.gz` would start receiving 400 errors

**Instead:** Make both optional, validate at least one present in controller logic

**Example (WRONG):**
```java
// BREAKS EXISTING CLIENTS
@RequestPart("junit.tar.gz") MultipartFile junitXmls,           // implicitly required
@RequestPart("karate.tar.gz") MultipartFile karateJson,        // implicitly required
```

**Example (CORRECT):**
```java
@RequestPart(value="junit.tar.gz", required=false) MultipartFile junitXmls,
@RequestPart(value="karate.tar.gz", required=false) MultipartFile karateJson,

// In method body:
if (junitXmls == null && karateJson == null) {
    throw new IllegalArgumentException("At least one test result format required");
}
```

**Detection:** Test with existing client payload (only junit.tar.gz) after changes. Should succeed, not 400.

### Anti-Pattern 2: Format-Specific Logic in Service Layer

**What:** Adding Karate-specific handling in `TestResultPersistService`

**Why bad:** Violates separation of concerns. Service layer should be format-agnostic.

**Example (WRONG):**
```java
// IN TestResultPersistService - BAD
public StagePathTestResult insertTestResult(
    StageDetails stageDetails,
    TestResultModel junitModel,
    TestResultModel karateModel  // Format leaking into service layer
) {
    // Format-specific merge logic here - WRONG PLACE
}
```

**Example (CORRECT):**
```java
// IN Controller - RIGHT PLACE
TestResultModel combined = new TestResultModel();
if (junitXmls != null) {
    combined = combined.add(parseJunit(junitXmls));
}
if (karateJson != null) {
    combined = combined.add(parseKarate(karateJson));
}

// Service receives unified model - format-agnostic
testResultPersistService.insertTestResult(stageDetails, combined);
```

**Prevention:** Service layer should NEVER mention format names (JUnit, Karate, Surefire). Only works with `TestResultModel`.

### Anti-Pattern 3: Schema Changes for Format-Specific Data

**What:** Adding `karate_feature_name`, `karate_scenario_tags` columns to database

**Why bad:**
- Breaks format-agnostic data model
- Requires schema migration for feature addition
- Creates format-specific columns that remain NULL for other formats
- Doesn't scale (what about Pytest? Cypress? future formats?)

**Instead:**
- Use JSON columns for format-specific metadata (already exists: `external_links` column)
- Map Karate concepts to existing fields (feature → suite name, scenario → case name)
- Store original Karate JSON in S3 for later retrieval if needed

**Example (WRONG):**
```sql
-- Adding format-specific columns - BAD
ALTER TABLE test_case ADD COLUMN karate_scenario_tag VARCHAR(255);
ALTER TABLE test_suite ADD COLUMN karate_feature_file VARCHAR(255);
```

**Example (CORRECT):**
```java
// Map to existing fields - GOOD
TestSuiteModel.builder()
    .name(karateFeature.getUri())           // Feature file path → suite name
    .packageName(extractPackage(uri))        // Derive from path
    .build();

TestCaseModel.builder()
    .name(karateScenario.getName())          // Scenario name → test name
    .className(karateFeature.getName())      // Feature name → class name
    .build();
```

**Prevention:** Before schema changes, check if existing columns can represent the data. 90% of test result concepts (name, status, duration, error message) are universal.

### Anti-Pattern 4: Tight Coupling Between Parser and Controller

**What:** Putting Karate JSON parsing logic directly in controller method

**Why bad:**
- Makes parser logic untestable without HTTP context
- Mixes HTTP concerns (multipart handling) with parsing logic
- Difficult to reuse parser in other contexts (CLI, batch jobs)

**Example (WRONG):**
```java
// IN JunitController - BAD
@PostMapping(...)
public ResponseEntity<?> upload(@RequestPart("karate.tar.gz") MultipartFile file) {
    // Parsing logic directly in controller - WRONG
    ObjectMapper mapper = new ObjectMapper();
    KarateResults results = mapper.readValue(file.getInputStream(), KarateResults.class);
    List<TestSuiteModel> suites = new ArrayList<>();
    for (KarateFeature feature : results.getFeatures()) {
        // Complex conversion logic in controller - WRONG PLACE
    }
}
```

**Example (CORRECT):**
```java
// IN JunitController - RIGHT
@PostMapping(...)
public ResponseEntity<?> upload(@RequestPart("karate.tar.gz") MultipartFile file) {
    List<String> jsonContents = extractFilesFromTarGz(file);
    TestResultModel model = KarateJsonParseUtil.parseKarateJson(jsonContents);
    // Rest of controller logic
}

// IN KarateJsonParseUtil - RIGHT PLACE FOR PARSING
public static TestResultModel parseKarateJson(List<String> jsonContents) {
    // All parsing and conversion logic here
}
```

**Prevention:** Controller should only handle HTTP concerns (extract multipart files, validate, return response). Parsing logic goes in converter package.

## Scalability Considerations

| Concern | Current State | With Karate JSON | At Scale (10K+ results/day) |
|---------|---------------|------------------|----------------------------|
| **Parse performance** | XML DOM parsing (moderate) | JSON streaming parse (fast) | Both adequate. Bottleneck is DB write, not parse. |
| **Database writes** | Batched inserts via JOOQ | No change | Consider async processing queue for >1K results/hour |
| **S3 storage** | Direct upload in request | No change | Works fine. S3 auto-scales. |
| **Multiple formats in one upload** | N/A | Merge in-memory before persist | Fine for <100MB combined. For larger, consider streaming merge. |
| **Format detection** | XML tag inspection | Separate endpoints (explicit) | No detection needed. Client declares format via multipart name. |

**Current bottleneck:** Database writes, not parsing or storage. Adding Karate JSON does not change bottleneck.

**Recommended enhancements (future, not required for initial Karate support):**
1. **Async processing queue:** For high-volume ingestion (>1K uploads/hour), introduce message queue (SQS) between controller and service layer. Controller immediately returns 202 Accepted, background worker persists to DB.
2. **Format-specific optimization:** Karate JSON can be parsed with streaming parser (Jackson streaming API) for very large files (>10MB). Current approach (parse entire string) works fine for typical Karate result files (<1MB).
3. **Caching:** Browse queries already cached (`cache/` package). No changes needed for Karate support.

## Build Order and Dependencies

### Phase 1: Foundation (No Dependencies)

**Components:**
1. **Internal Karate models** (`KarateFeature`, `KarateScenario`, etc.)
   - Location: `reportcard-model/src/main/java/.../model/converter/karate/models/`
   - Purpose: Jackson-annotated POJOs for Karate JSON structure
   - Dependencies: None (pure data classes)
   - Testable: Yes (unit tests with sample JSON)

2. **Karate converter** (`KarateConvertersUtil`)
   - Location: `reportcard-model/src/main/java/.../model/converter/karate/`
   - Purpose: `fromJsonContents(String) → List<TestSuiteModel>`
   - Dependencies: Karate models, existing `TestSuiteModel`/`TestCaseModel`
   - Testable: Yes (unit tests with Karate JSON samples → verify TestSuiteModel output)

3. **Karate parse util** (`KarateJsonParseUtil`)
   - Location: `reportcard-model/src/main/java/.../model/converter/`
   - Purpose: `parseKarateJson(List<String>) → TestResultModel`
   - Dependencies: `KarateConvertersUtil`
   - Testable: Yes (unit tests with multiple JSON files → verify aggregation)

**Build command:** `./gradlew :reportcard-model:test`

**Exit criteria:** All converter unit tests pass. Can convert Karate JSON to `TestResultModel` without controller.

### Phase 2: Storage Type (Depends on Phase 1)

**Components:**
1. **Database DML update** (`V1.1__reportcard_mysql_dml.sql`)
   - Add: `INSERT INTO storage_type (storage_type) VALUES ('KARATE');`
   - Dependencies: None (DML file change)
   - Testable: Manual verification (run against local MySQL)

2. **JOOQ regeneration**
   - Command: `./gradlew generateJooqSchemaSource`
   - Dependencies: Updated database schema
   - Testable: Verify `gen/db/tables/StorageType.java` includes KARATE

3. **StorageType enum update**
   - Location: `reportcard-server/src/main/java/.../persist/StorageType.java`
   - Add: `KARATE` enum value
   - Dependencies: JOOQ regeneration
   - Testable: Compilation (enum references valid database values)

**Build command:** `./gradlew generateJooqSchemaSource && ./gradlew :reportcard-server:compileJava`

**Exit criteria:** `StorageType.KARATE` compiles. JOOQ code references valid database value.

### Phase 3: Controller Integration (Depends on Phases 1 & 2)

**Components:**
1. **Controller method update** (`JunitController.postStageJunitStorageTarGZ`)
   - Change: Make `junit.tar.gz` optional, add `karate.tar.gz` optional
   - Add: Validation (at least one required)
   - Add: Karate parse and merge logic
   - Dependencies: `KarateJsonParseUtil`, updated method signature
   - Testable: Integration tests (MockMvc + Testcontainers)

2. **Storage helper** (`JunitController.storeKarate`)
   - Add: New method mirroring `storeJunit()`
   - Purpose: Persist Karate tar.gz to S3 with label "karate"
   - Dependencies: `S3Service`, `StoragePersistService`, `StorageType.KARATE`
   - Testable: Integration tests (LocalStack S3)

**Build command:** `./gradlew :reportcard-server:integrationTest`

**Exit criteria:**
- Existing integration tests pass (backwards compatibility verified)
- New integration test passes (Karate-only upload)
- New integration test passes (JUnit + Karate combined upload)

### Phase 4: Client Library (Depends on Phase 3)

**Components:**
1. **Client builder update** (if applicable in `reportcard-client`)
   - Add: Optional `karateJsonFile` parameter to upload builder
   - Purpose: Convenience for Java clients
   - Dependencies: Updated server endpoint signature
   - Testable: Client integration tests against running server

**Build command:** `./gradlew :reportcard-client:test`

**Exit criteria:** Client can upload Karate JSON via builder API. Existing client code unchanged.

### Dependency Graph

```
Phase 1: Converter Layer (parallel)
  ├─ KarateFeature, KarateScenario models
  ├─ KarateConvertersUtil
  └─ KarateJsonParseUtil
     └─ Unit tests

Phase 2: Storage Type (parallel with Phase 1)
  ├─ DML update
  ├─ JOOQ regeneration
  └─ StorageType enum

Phase 3: Controller Integration (AFTER 1 & 2)
  ├─ Controller method update
  │   ├─ Requires: KarateJsonParseUtil (Phase 1)
  │   └─ Requires: StorageType.KARATE (Phase 2)
  ├─ storeKarate() method
  └─ Integration tests

Phase 4: Client Library (AFTER 3)
  └─ Client builder update
```

**Critical path:** Phase 1 → Phase 3 (converter must exist before controller uses it)

**Parallel work:** Phase 1 and Phase 2 can be developed concurrently (no dependencies between them)

## Testing Strategy

### Unit Tests (Phase 1)

**Location:** `reportcard-model/src/test/java/.../model/converter/karate/`

**Test cases:**
1. **Single Karate feature parse**
   - Input: Valid Karate JSON with 3 scenarios (2 pass, 1 fail)
   - Output: `TestSuiteModel` with 3 `TestCaseModel`s, correct statuses
   - Verifies: Basic parsing, status mapping

2. **Multiple features merge**
   - Input: List of 2 Karate JSON strings
   - Output: `TestResultModel` with 2 `TestSuiteModel`s, aggregated counts
   - Verifies: Multi-file handling, count aggregation

3. **Error handling**
   - Input: Invalid JSON
   - Output: Throws descriptive exception
   - Verifies: Graceful error handling

4. **Edge cases**
   - Empty feature (0 scenarios) → skip or 0-count suite?
   - Scenario with no steps → skip status
   - Missing fields (robust parsing)

**Pattern:** Mirror existing `JunitMapperTests.java` structure

### Integration Tests (Phase 3)

**Location:** `reportcard-server/src/integrationTest/java/.../controller/`

**Test cases:**
1. **Backwards compatibility**
   - Upload: Only `junit.tar.gz` + `storage.tar.gz`
   - Verify: Success (existing behavior unchanged)

2. **Karate only**
   - Upload: Only `karate.tar.gz` + `storage.tar.gz`
   - Verify: Test results persisted, S3 files stored, response correct

3. **Combined upload**
   - Upload: `junit.tar.gz` + `karate.tar.gz` + `storage.tar.gz`
   - Verify: Single test_result with merged counts, both stored in S3

4. **Validation**
   - Upload: Only `storage.tar.gz` (no test results)
   - Verify: 400 error with clear message

5. **S3 storage**
   - Upload: `karate.tar.gz`
   - Verify: File exists at correct S3 path with label "karate"

**Infrastructure:**
- Testcontainers MySQL (existing)
- LocalStack S3 (existing)
- MockMvc for HTTP (existing)

**Pattern:** Extend `AbstractTestResultPersistTest` base class

### Sample Karate JSON (For Testing)

**Minimal valid Karate JSON:**
```json
[
  {
    "uri": "classpath:features/example.feature",
    "name": "Example Feature",
    "scenarios": [
      {
        "name": "Successful scenario",
        "passed": true,
        "failed": false,
        "duration": 1234,
        "steps": [
          {"name": "Given setup", "result": {"status": "passed"}}
        ]
      },
      {
        "name": "Failed scenario",
        "passed": false,
        "failed": true,
        "duration": 567,
        "steps": [
          {"name": "When action", "result": {"status": "failed", "error": "Expected 200 but got 500"}}
        ]
      }
    ]
  }
]
```

**Expected conversion:**
- 1 `TestSuiteModel` (name="Example Feature", tests=2, failures=1)
  - 1 `TestCaseModel` (name="Successful scenario", status=SUCCESS)
  - 1 `TestCaseModel` (name="Failed scenario", status=FAILURE)
    - 1 `TestCaseFaultModel` (message="Expected 200 but got 500")

## Sources and Confidence

### HIGH Confidence Sources

**Existing codebase analysis:**
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` (multipart handling pattern)
- `/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/JunitSurefireXmlParseUtil.java` (converter pattern)
- `/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/TestResultModel.java` (internal model, merge method exists)
- `/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StorageType.java` (storage type extensibility)

**Architectural patterns verified:**
- Format-Parser-Model pattern (observed in existing JUnit/Surefire handling)
- Optional multipart parts (Spring Boot @RequestPart with required=false)
- JOOQ code generation workflow (documented in CLAUDE.md, README_AI.md)
- Testcontainers test infrastructure (observed in test base classes)

**Confidence rationale:** Architecture patterns derived from existing working code in same codebase. Not hypothetical—these patterns already work for JUnit/Surefire, Karate is parallel implementation.

### MEDIUM Confidence Sources

**Karate JSON format:**
- Based on Karate Labs documentation (karatelabs.github.io)
- Structure: Feature → Scenario → Steps (verified from Karate repo)
- Note: Did not fetch live documentation due to WebFetch limitations. Karate format derived from training data (current as of January 2025). **Recommendation:** Verify exact JSON structure with actual Karate output during Phase 1 (converter development).

**Spring Boot multipart handling:**
- Spring Framework 5.3.x behavior (matches Spring Boot 2.6.15 used in project)
- @RequestPart with required=false verified from training data
- Note: Spring documentation fetch failed (404). Behavior verified from training knowledge, which is current for Spring Boot 2.6.15.

### LOW Confidence Areas (Require Validation)

**Karate JSON exact field names:**
- Training data suggests: `scenarios`, `passed`, `failed`, `duration`, `steps`
- **Must verify:** Actual Karate output may differ (e.g., `scenarios` vs `elements`, `duration` vs `durationMillis`)
- **Mitigation:** Phase 1 development should start with real Karate JSON sample from user's CI/CD

**Karate feature/scenario hierarchy completeness:**
- Assumption: Simple feature → scenario mapping (no nested scenarios, no scenario outlines)
- **Risk:** Karate supports scenario outlines (data-driven tests) which may have different structure
- **Mitigation:** Phase 1 testing should include scenario outline samples

## Gaps and Open Questions

### Gap 1: Karate JSON Structure Verification

**What's missing:** Authoritative sample of actual Karate JSON output format

**Why it matters:** Parser depends on exact field names and structure. Misalignment = parse failures.

**When to address:** Before Phase 1 (converter development)

**How to address:** User provides sample Karate JSON from their actual tests. Verify fields: `scenarios` vs `elements`, `passed`/`failed` vs `status`, duration units.

### Gap 2: Scenario Outline Handling

**What's missing:** How Karate represents parameterized tests (scenario outlines) in JSON

**Why it matters:** Scenario outlines generate multiple test executions from one scenario definition. Unclear if JSON shows:
- One entry per parameter set (N test cases)
- One entry with execution summary (1 test case with N iterations)

**When to address:** Phase 1 testing

**How to address:** Include scenario outline example in test suite. If structure differs significantly, may need separate converter path.

### Gap 3: Karate Error Message Format

**What's missing:** Exact structure of Karate failure details (stack traces, assertion details)

**Why it matters:** Must map to `TestCaseFaultModel` (type, message, value fields). Rich error info improves debuggability.

**When to address:** Phase 1 development

**How to address:** Examine failed Karate test JSON, map detailed error info to fault model. May need custom formatter for Karate-specific error structures.

### Gap 4: Backwards Compatibility Testing Scope

**What's missing:** Confirmation that existing client integrations (if any) will be tested

**Why it matters:** Controller signature changes (`required=false`) could break poorly-written clients

**When to address:** Phase 3 integration testing

**How to address:**
- Test existing payload (only junit.tar.gz) against updated endpoint
- If client library exists, run client integration tests
- Document endpoint behavior change in API docs (OpenAPI/Swagger)

## Recommendations for Roadmap

### Phase Structure Recommendations

**Phase 1: Karate Converter (1-2 days)**
- Develop Karate JSON → TestResultModel converter
- Unit tests with sample Karate JSON
- **No controller changes** (isolated component)
- **Exit criteria:** Converter tested and working

**Phase 2: Storage Type Extension (1 day)**
- Add StorageType.KARATE to database
- Regenerate JOOQ code
- Update Java enum
- **Exit criteria:** Storage type infrastructure ready

**Phase 3: Controller Integration (2-3 days)**
- Make junit.tar.gz optional
- Add karate.tar.gz optional parameter
- Add validation (at least one required)
- Implement merge logic
- **Exit criteria:** Integration tests pass

**Phase 4: Documentation and Rollout (1 day)**
- Update OpenAPI/Swagger docs
- Update README with Karate JSON usage examples
- Deployment to staging/production
- **Exit criteria:** Feature documented and deployed

**Total estimated effort:** 5-7 days for full integration

### Ordering Rationale

**Why converter first:** Allows isolated development and testing without touching controller/database. Can verify Karate JSON parsing independently.

**Why storage type in parallel:** No dependency on converter. Can be developed concurrently with Phase 1.

**Why controller last:** Depends on both converter (Phase 1) and storage type (Phase 2). Integration point that ties everything together.

**Why documentation last:** Actual behavior should be verified before documenting. Avoids documenting incorrect assumptions.

### Research Flags for Phases

**Phase 1 (Converter) - Likely needs deeper research:**
- Actual Karate JSON structure (get real samples from user)
- Scenario outline handling (if user uses parameterized tests)
- Error message format (for rich fault details)

**Phase 2 (Storage Type) - Standard, unlikely to need research:**
- Well-established pattern (adding enum value)
- Database reference data addition is straightforward
- JOOQ regeneration is documented process

**Phase 3 (Controller) - Minor research likely:**
- Spring Boot optional multipart behavior (verify with integration test)
- Validation error message clarity (UX consideration)
- Performance testing for combined uploads (if large files expected)

**Phase 4 (Documentation) - Minimal research:**
- Standard documentation patterns
- OpenAPI/Swagger generation already in place

## Confidence Assessment Summary

| Area | Confidence | Reason |
|------|------------|--------|
| **Overall architecture approach** | HIGH | Based on existing working patterns in codebase |
| **Controller integration pattern** | HIGH | @RequestPart with required=false is standard Spring Boot |
| **Converter pattern** | HIGH | Mirrors existing JUnit/Surefire converters |
| **Internal model compatibility** | HIGH | TestResultModel.add() method already exists for merging |
| **Storage layer extension** | HIGH | StorageType enum pattern already established |
| **Karate JSON exact structure** | MEDIUM | Based on Karate documentation, needs real sample verification |
| **Scenario outline handling** | LOW | Unclear from documentation, needs testing with actual samples |
| **Performance at scale** | MEDIUM | Current system handles scale, Karate adds minimal overhead |

**Overall confidence:** HIGH for architecture, MEDIUM for Karate-specific details (requires sample verification)

## Summary for Roadmap Creation

**What's ready for immediate implementation:**
- Controller multipart handling (optional parts pattern)
- Converter infrastructure (package structure, unit test approach)
- Storage type extension (database + enum)
- Service layer (no changes needed)

**What needs investigation during Phase 1:**
- Exact Karate JSON field names and structure
- Scenario outline representation
- Error message detail format

**Critical success factors:**
1. **Backwards compatibility:** Existing clients must continue working unchanged
2. **Format isolation:** Karate-specific logic stays in converter layer
3. **Testing rigor:** Integration tests verify all combinations (JUnit-only, Karate-only, both)
4. **Sample-driven development:** Phase 1 must start with real Karate JSON samples from user

**Recommended first action:** Request sample Karate JSON output from user's actual test suite before starting Phase 1.
