# Phase 3: API Integration - Research

**Researched:** 2026-01-26
**Domain:** Spring Boot multipart upload APIs with optional parameters
**Confidence:** HIGH

## Summary

This phase integrates Karate JSON uploads into the existing JunitController API. The codebase has well-established patterns for multipart file uploads, storage persistence, and run record management. Phase 1 delivered schema changes (start_time, end_time columns in run table, KARATE_JSON storage type ID 9) and Phase 2 delivered the parser utilities (KarateConvertersUtil with parseKarateSummary, parseResultDate, calculateStartTime methods).

The key challenge is making `karate.tar.gz` optional while maintaining backwards compatibility. Spring MVC's `@RequestPart` supports optional parameters via `required = false`. The controller must validate that at least one of JUnit XML or Karate JSON is present. LocalDateTime from the parser must be converted to Instant for database storage.

**Primary recommendation:** Add optional `karate.tar.gz` MultipartFile parameter to existing `postStageJunitStorageTarGZ` endpoint, validate input presence, extract karate-summary-json.txt from tar.gz, parse timing data, update run record with start_time/end_time, and store karate.tar.gz in S3 with KARATE_JSON storage type.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 2.6.15 | REST API framework | Project standard |
| Spring MVC | 5.3.x | @RequestPart multipart handling | Already used for junit.tar.gz uploads |
| JOOQ | 3.19.8 | Type-safe database operations | Project-wide database access pattern |
| AWS SDK | 2.x | S3 file storage | Existing S3Service infrastructure |
| Apache Commons Compress | (existing) | Tar.gz extraction | Used by TarExtractorCommonsCompress |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Lombok | (existing) | @Builder, @Value annotations | Request/response objects |
| Jackson | 2.17.1 | JSON parsing | KarateSummary deserialization via SharedObjectMappers |
| SLF4J | (existing) | Logging | Error and warning messages |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Optional @RequestPart | Separate endpoint | Would break unified upload pattern; requires two API calls |
| MultipartFile.isEmpty() | null check | isEmpty() also catches 0-byte uploads |
| LocalDateTime to Instant | Store as LocalDateTime | Inconsistent with existing Instant columns; JOOQ ForcedType maps DATETIME to Instant |

**Installation:**
```bash
# No new dependencies needed - all libraries already present
```

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/io/github/ericdriggs/reportcard/
├── controller/
│   ├── JunitController.java           # ADD: optional karate.tar.gz parameter
│   ├── model/
│   │   └── JunitHtmlPostRequest.java  # ADD: karateTarGz field
│   └── util/
│       └── KarateTarGzUtil.java       # NEW: extract karate-summary-json.txt from tar.gz
├── persist/
│   ├── StorageType.java               # ALREADY EXISTS: KARATE_JSON(9)
│   └── StagePathPersistService.java   # ADD: method to update run timing
└── model/
    └── StagePath.java                 # Uses RunPojo which has start_time/end_time
```

### Pattern 1: Optional MultipartFile Parameter
**What:** Make karate.tar.gz optional using `required = false`
**When to use:** When extending existing endpoints with additional optional uploads
**Example:**
```java
// Source: Spring MVC @RequestPart documentation pattern
@PostMapping(value = {"storage/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
public ResponseEntity<StagePathStorageResultCountResponse> postStageJunitStorageTarGZ(
    // ... existing parameters ...

    @Parameter(description = "Junit and/or surefire xml files")
    @RequestPart(value = "junit.tar.gz", required = false)  // CHANGED: now optional
    MultipartFile junitXmls,

    @Parameter(description = "Karate test reports tar.gz containing karate-summary-json.txt")
    @RequestPart(value = "karate.tar.gz", required = false)  // NEW: optional karate upload
    MultipartFile karateTarGz,

    @Parameter(description = "Files and folders to store in s3")
    @RequestPart("storage.tar.gz")
    MultipartFile reports
)
```

### Pattern 2: Input Validation - At Least One Test Result
**What:** Validate that at least one of junit.tar.gz or karate.tar.gz is present
**When to use:** When multiple optional parameters have mutual dependency
**Example:**
```java
// Validation pattern from existing ResponseStatusException usage
private void validateTestResultInputs(MultipartFile junitXmls, MultipartFile karateTarGz) {
    boolean hasJunit = junitXmls != null && !junitXmls.isEmpty();
    boolean hasKarate = karateTarGz != null && !karateTarGz.isEmpty();

    if (!hasJunit && !hasKarate) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "At least one of junit.tar.gz or karate.tar.gz must be provided"
        );
    }
}
```

### Pattern 3: Extract Specific File from Tar.gz
**What:** Extract karate-summary-json.txt from karate.tar.gz without extracting all files
**When to use:** When only one file from archive is needed for processing
**Example:**
```java
// Pattern following TestXmlTarGzUtil but for specific file extraction
public enum KarateTarGzUtil {
    ;//static methods only

    private static final String KARATE_SUMMARY_FILENAME = "karate-summary-json.txt";

    @SneakyThrows(IOException.class)
    public static String extractKarateSummaryJson(MultipartFile tarGz) {
        Path tempDir = Files.createTempDirectory("reportcard-karate-");
        try {
            InputStream inputStream = tarGz.getInputStream();
            TarExtractorCommonsCompress tarExtractor =
                new TarExtractorCommonsCompress(inputStream, true, tempDir);
            tarExtractor.untar();

            // Find karate-summary-json.txt recursively
            return FileUtils.fileContentsFromPathAndRegex(tempDir, KARATE_SUMMARY_FILENAME)
                .stream()
                .findFirst()
                .orElse(null);
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
        }
    }
}
```

### Pattern 4: LocalDateTime to Instant Conversion
**What:** Convert LocalDateTime from Karate parser to Instant for database storage
**When to use:** When interfacing between parser output and JOOQ-generated types
**Example:**
```java
// JOOQ ForcedType maps DATETIME columns to Instant
// Karate dates have no timezone - treat as UTC per project convention
private Instant toInstant(LocalDateTime localDateTime) {
    if (localDateTime == null) {
        return null;
    }
    return localDateTime.atZone(ZoneOffset.UTC).toInstant();
}
```

### Pattern 5: Update Run Timing
**What:** Update existing run record with start_time and end_time
**When to use:** After parsing Karate JSON and calculating timing
**Example:**
```java
// Pattern following existing dsl.update() usage in StagePathPersistService
public void updateRunTiming(Long runId, Instant startTime, Instant endTime) {
    if (startTime == null && endTime == null) {
        log.debug("No timing data to update for runId: {}", runId);
        return;
    }

    var updateStep = dsl.update(RUN)
            .set(RUN.START_TIME, startTime)
            .set(RUN.END_TIME, endTime)
            .where(RUN.RUN_ID.eq(runId));

    int rowsUpdated = updateStep.execute();
    if (rowsUpdated != 1) {
        log.warn("Expected 1 row updated for run timing, actual: {}", rowsUpdated);
    }
}
```

### Pattern 6: Store Karate Tar.gz in S3
**What:** Store karate.tar.gz in S3 with KARATE_JSON storage type
**When to use:** Preserving original Karate reports for later access
**Example:**
```java
// Pattern following existing storeJunit() method
protected StagePathStorages storeKarate(Long stageId, MultipartFile tarGz) {
    final String label = "karate";
    StorageType storageType = StorageType.KARATE_JSON;

    final StagePath stagePath = storagePersistService.getStagePath(stageId);
    final String prefix = new StoragePath(stagePath, label).getPrefix();

    StagePathStorages stagePathStorages =
        storagePersistService.upsertStoragePath(null, label, prefix, stageId, storageType);
    if (!stagePathStorages.isComplete()) {
        s3Service.uploadTarGz(prefix, false, tarGz);  // false = don't expand
        storagePersistService.setUploadCompleted(null, label, prefix, stageId);
        stagePathStorages.setComplete();
    }
    return stagePathStorages;
}
```

### Anti-Patterns to Avoid
- **Extracting all files from karate.tar.gz:** Only need karate-summary-json.txt; extracting everything wastes resources
- **Throwing exception on missing timing data:** Missing timing should log warning, not fail upload
- **Modifying JUnit-only flow:** Existing junit-only uploads must continue working unchanged
- **Creating new endpoint:** Extending existing endpoint maintains API consistency
- **Storing karate as HTML storage type:** Use dedicated KARATE_JSON(9) type for proper categorization

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Tar.gz extraction | Custom archive parsing | TarExtractorCommonsCompress | Already handles gzip + tar, edge cases handled |
| File content reading | Manual file I/O | FileUtils.fileContentsFromPathAndRegex | Standard pattern for reading extracted files |
| S3 upload | Direct AWS SDK calls | S3Service.uploadTarGz() | Handles retries, checksum, error cases |
| Storage record creation | Manual INSERT | StoragePersistService.upsertStoragePath() | Handles idempotency, deduplication |
| JSON parsing | Custom deserialization | KarateConvertersUtil.parseKarateSummary() | Phase 2 deliverable with error handling |
| Date parsing | Manual parsing | KarateConvertersUtil.parseResultDate() | Handles Karate's date format correctly |
| Temp dir cleanup | Manual try-finally | Existing pattern in TestXmlTarGzUtil | Prevents disk space leaks |

**Key insight:** The codebase has mature patterns for multipart uploads, tar.gz handling, S3 storage, and database updates. Reuse these patterns exactly to maintain consistency.

## Common Pitfalls

### Pitfall 1: Forgetting MultipartFile.isEmpty() Check
**What goes wrong:** NullPointerException when multipart file exists but is empty (0 bytes)
**Why it happens:** Checking only for null doesn't catch empty file uploads
**How to avoid:** Always check both null and isEmpty():
```java
boolean hasFile = file != null && !file.isEmpty();
```
**Warning signs:** NPE in getInputStream() or when reading file contents

### Pitfall 2: LocalDateTime Timezone Assumption
**What goes wrong:** Incorrect start_time/end_time values in database
**Why it happens:** Karate's resultDate has no timezone; LocalDateTime.atZone() with wrong zone shifts times
**How to avoid:** Use ZoneOffset.UTC consistently (project convention for all DATETIME columns):
```java
localDateTime.atZone(ZoneOffset.UTC).toInstant();
```
**Warning signs:** Times shifted by hours when comparing to Karate reports

### Pitfall 3: Temp Directory Leak
**What goes wrong:** Disk fills up with undeleted temp directories
**Why it happens:** Exception thrown before cleanup in finally block
**How to avoid:** Use try-finally pattern from TestXmlTarGzUtil:
```java
Path tempDir = Files.createTempDirectory("reportcard-");
try {
    // ... processing ...
} finally {
    if (tempDir != null) {
        FileUtils.deleteDirectory(tempDir.toFile());
    }
}
```
**Warning signs:** Disk space warnings, many reportcard-* directories in temp folder

### Pitfall 4: Breaking Backwards Compatibility
**What goes wrong:** Existing JUnit-only uploads start failing
**Why it happens:** Making junit.tar.gz required or changing request structure
**How to avoid:**
- Keep existing parameter names exactly
- Add karate.tar.gz as separate optional parameter
- Test with existing JUnit-only payloads
**Warning signs:** 400 errors on previously working requests

### Pitfall 5: Run Timing Update Race Condition
**What goes wrong:** start_time/end_time not saved because run doesn't exist yet
**Why it happens:** Trying to update run before insertTestResult creates it
**How to avoid:** Update timing AFTER insertTestResult returns, using runId from stagePath:
```java
StagePathTestResult result = testResultPersistService.insertTestResult(...);
Long runId = result.getStagePath().getRun().getRunId();
updateRunTiming(runId, startTime, endTime);
```
**Warning signs:** Run record has null start_time/end_time despite Karate upload

### Pitfall 6: Karate Summary File Not Found
**What goes wrong:** Timing data not extracted from karate.tar.gz
**Why it happens:** File might be in subdirectory or have slightly different name
**How to avoid:** Search recursively for karate-summary-json.txt:
```java
FileUtils.fileContentsFromPathAndRegex(tempDir, "karate-summary-json\\.txt")
```
**Warning signs:** Null timing data despite valid Karate archive

## Code Examples

Verified patterns from codebase:

### JunitHtmlPostRequest with Karate Field
```java
// Source: Pattern from existing JunitHtmlPostRequest.java
@Builder
@Jacksonized
@Value
public class JunitHtmlPostRequest {
    StageDetails stageDetails;
    String label;
    String indexFile;
    MultipartFile junitXmls;      // existing - now optional
    MultipartFile karateTarGz;    // NEW field
    MultipartFile reports;
}
```

### Controller Method Signature Update
```java
// Source: Pattern from existing postStageJunitStorageTarGZ
@PostMapping(value = {"storage/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
public ResponseEntity<StagePathStorageResultCountResponse> postStageJunitStorageTarGZ(
    // ... existing path/query params unchanged ...

    @Parameter(description = "Junit/surefire xml files in tar.gz. Required if karate.tar.gz not provided.")
    @RequestPart(value = "junit.tar.gz", required = false)
    MultipartFile junitXmls,

    @Parameter(description = "Karate reports tar.gz with karate-summary-json.txt for timing data.")
    @RequestPart(value = "karate.tar.gz", required = false)
    MultipartFile karateTarGz,

    @Parameter(description = "Files and folders to store in s3.")
    @RequestPart("storage.tar.gz")
    MultipartFile reports
)
```

### Process Karate Timing Data
```java
// Source: Pattern combining Phase 2 parser with JOOQ update
private void processKarateTiming(Long runId, MultipartFile karateTarGz) {
    if (karateTarGz == null || karateTarGz.isEmpty()) {
        return;
    }

    String summaryJson = KarateTarGzUtil.extractKarateSummaryJson(karateTarGz);
    if (summaryJson == null) {
        log.warn("karate-summary-json.txt not found in karate.tar.gz for runId: {}", runId);
        return;
    }

    KarateSummary summary = KarateConvertersUtil.parseKarateSummary(summaryJson);
    if (summary == null) {
        log.warn("Failed to parse karate-summary-json.txt for runId: {}", runId);
        return;
    }

    LocalDateTime endTime = KarateConvertersUtil.parseResultDate(summary.getResultDate());
    LocalDateTime startTime = KarateConvertersUtil.calculateStartTime(endTime, summary.getElapsedTime());

    Instant startInstant = toInstant(startTime);
    Instant endInstant = toInstant(endTime);

    updateRunTiming(runId, startInstant, endInstant);
}

private Instant toInstant(LocalDateTime localDateTime) {
    return localDateTime != null ? localDateTime.atZone(ZoneOffset.UTC).toInstant() : null;
}
```

### Updated doPostStageJunitStorageTarGZ
```java
// Source: Pattern from existing method with Karate additions
public StagePathStorageResultCountResponse doPostStageJunitStorageTarGZ(JunitHtmlPostRequest req) {
    // Validate at least one test result source
    boolean hasJunit = req.getJunitXmls() != null && !req.getJunitXmls().isEmpty();
    boolean hasKarate = req.getKarateTarGz() != null && !req.getKarateTarGz().isEmpty();

    if (!hasJunit && !hasKarate) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "At least one of junit.tar.gz or karate.tar.gz must be provided");
    }

    // Parse JUnit if present (existing behavior)
    TestResultModel testResultModel;
    if (hasJunit) {
        List<String> testXmlContents = TestXmlTarGzUtil.getFileContentsFromTarGz(req.getJunitXmls());
        testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);
    } else {
        // Empty test result when only Karate provided
        testResultModel = TestResultModel.empty();
    }

    StagePathTestResult stagePathTestResult =
        testResultPersistService.insertTestResult(req.getStageDetails(), testResultModel);
    StagePath stagePath = stagePathTestResult.getStagePath();
    final Long stageId = stagePath.getStage().getStageId();
    final Long runId = stagePath.getRun().getRunId();

    // Process Karate timing data (NEW)
    if (hasKarate) {
        processKarateTiming(runId, req.getKarateTarGz());
    }

    // Store files in S3
    StagePathStorages stagePathStorages;
    {
        List<StagePathStorages> storagesList = new ArrayList<>();

        if (hasJunit) {
            storagesList.add(storeJunit(stageId, req.getJunitXmls()));
        }
        if (hasKarate) {
            storagesList.add(storeKarate(stageId, req.getKarateTarGz()));
        }
        storagesList.add(storeHtml(stageId, req.getLabel(), req.getReports(), req.getIndexFile()));

        stagePathStorages = StagePathStorages.merge(storagesList.toArray(new StagePathStorages[0]));
    }

    StagePathStorageResultCount stagePathStorageResultCount =
        new StagePathStorageResultCount(stagePathStorages.getStagePath(),
            stagePathStorages.getStorages(), stagePathTestResult);
    return StagePathStorageResultCountResponse.created(stagePathStorageResultCount);
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| JUnit-only test uploads | JUnit + optional Karate timing | Phase 3 (now) | Run records can have precise timing data |
| No run timing data | start_time/end_time columns | Phase 1 | Enables duration calculations, trend analysis |
| Single test format | Multiple test format support | Phase 3 (now) | API accepts JUnit, Karate, or both |

**Deprecated/outdated:**
- None for this phase; all patterns are additions to existing infrastructure

## Open Questions

Things that couldn't be fully resolved:

1. **TestResultModel.empty() Implementation**
   - What we know: Need to create empty TestResultModel when only Karate provided (no JUnit)
   - What's unclear: Does TestResultModel have a factory method or builder for empty state?
   - Recommendation: Inspect TestResultModel for empty/default constructor; may need to create one

2. **StagePathStorages.merge() with Variable Arguments**
   - What we know: Current merge() takes two arguments
   - What's unclear: Whether it can chain multiple merges or needs refactoring
   - Recommendation: Check if merge() is associative; chain calls if needed: `merge(merge(a, b), c)`

3. **Error Visibility in Response**
   - What we know: Parse errors should be visible as warnings per requirements
   - What's unclear: Current response doesn't have a warnings field
   - Recommendation: Log warnings for now (matches existing pattern); consider adding warnings to response in future phase

## Sources

### Primary (HIGH confidence)
- Codebase files examined (all absolute paths):
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` (existing controller patterns)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/util/TestXmlTarGzUtil.java` (tar.gz extraction pattern)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StagePathPersistService.java` (run update patterns)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StoragePersistService.java` (storage patterns)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java` (upload patterns)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/records/RunRecord.java` (JOOQ generated with start_time/end_time)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/tables/pojos/RunPojo.java` (JOOQ generated with start_time/end_time)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/karate/KarateConvertersUtil.java` (Phase 2 parser)

### Secondary (MEDIUM confidence)
- Phase 1 Research: `.planning/phases/01-schema-foundation/01-RESEARCH.md` (schema patterns)
- Phase 2 Research: `.planning/phases/02-karate-parser/02-RESEARCH.md` (parser patterns)
- Phase 2 Verification: `.planning/phases/02-karate-parser/02-VERIFICATION.md` (confirmed parser works)
- Spring MVC documentation for @RequestPart optional parameters

### Tertiary (LOW confidence)
- None - all findings verified with codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All libraries already in use, patterns verified from codebase
- Architecture: HIGH - Following exact patterns from existing JunitController
- Pitfalls: HIGH - Based on actual codebase patterns and Phase 1/2 research

**Research date:** 2026-01-26
**Valid until:** 2026-03-26 (60 days - stable domain, established patterns)
