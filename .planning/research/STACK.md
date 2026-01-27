# Technology Stack: Karate JSON Integration

**Project:** Reportcard - Karate JSON Support
**Researched:** 2026-01-26

## Executive Summary

Adding Karate JSON parsing to Reportcard requires minimal new dependencies. The existing Jackson infrastructure handles JSON parsing. The primary focus is on creating POJO models for Karate's specific JSON structure and validation logic.

## Core Analysis

### Karate JSON Format Structure

From examining actual Karate 1.2.0 output files in the repository:

**Summary format** (`karate-summary-json.txt`):
```json
{
  "featureSummary": [...],
  "efficiency": 0.22618474365347813,
  "featuresPassed": 5,
  "featuresFailed": 1,
  "totalTime": 348061.0,
  "threads": 5,
  "featuresSkipped": 1882,
  "resultDate": "2026-01-20 03:00:56 PM",
  "scenariosPassed": 6,
  "version": "1.2.0",
  "scenariosfailed": 1,
  "elapsedTime": 307767.0
}
```

**Detailed format** (`.karate-json.txt`): Nested structure with scenario results, step results, and timing information.

### Critical Fields for Extraction

Per project requirements:
- `elapsedTime` (double) - actual execution time in milliseconds
- `totalTime` (double) - total time including overhead in milliseconds
- `resultDate` (string) - timestamp in format "yyyy-MM-dd hh:mm:ss a"
- `version` (string) - Karate version (e.g., "1.2.0")
- Test counts: `featuresPassed`, `featuresFailed`, `scenariosPassed`, `scenariosfailed`

## Recommended Stack

### JSON Parsing (EXISTING - No New Dependencies)

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Jackson Databind | 2.13.x (via Spring Boot 2.6.15) | JSON parsing and mapping | Already in use throughout Reportcard. Well-tested with SharedObjectMappers utility. Handles nested structures and custom date formats. |
| Jackson JavaTimeModule | 2.13.x | Date/time handling | Already registered in SharedObjectMappers. Will handle resultDate parsing with custom deserializer if needed. |

**Rationale:** Zero new dependencies required. Jackson is industry standard, thread-safe, and already proven in this codebase for XML-to-JSON conversions in Surefire/TestNG parsers.

**Confidence:** HIGH - Jackson presence verified in build.gradle and SharedObjectMappers.java

### Date Parsing (EXISTING - No New Dependencies)

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| java.time.* (JDK 17) | Built-in | Parse Karate's date format | Native Java 17 API. DateTimeFormatter can handle "yyyy-MM-dd hh:mm:ss a" format. No external library needed. |

**Rationale:** Karate's `resultDate` format ("2026-01-20 03:00:56 PM") is standard and parseable with:
```java
DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
```

**Confidence:** HIGH - Standard Java date parsing

### JSON Schema Validation (RECOMMENDED - New Optional Dependency)

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| json-schema-validator | 1.5.3 | Validate Karate JSON structure | Prevents silent parsing failures. Validates against JSON Schema Draft 7. Widely used (4.5k stars), actively maintained. |

**Installation:**
```gradle
implementation 'com.networknt:json-schema-validator:1.5.3'
```

**Alternative: Skip Schema Validation**

For MVP, schema validation is optional. Use Jackson's fail-fast deserializer with required fields:
```java
@JsonProperty(required = true)
private Double elapsedTime;
```

**Confidence:** MEDIUM - Schema validation is best practice but not strictly required for MVP

### Model Definition (EXISTING PATTERNS)

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Lombok | 1.18.x (existing) | Reduce boilerplate | Already used throughout model layer. @Data, @Builder for POJOs. |
| Record Builders | 42 (existing) | Immutable models | Already in use for domain models. Records are preferred for DTOs in Java 17. |

**Rationale:** Follow existing patterns from reportcard-model module. Use Java 17 records for immutability:
```java
public record KarateSummary(
    @JsonProperty("elapsedTime") double elapsedTime,
    @JsonProperty("totalTime") double totalTime,
    @JsonProperty("resultDate") String resultDate,
    @JsonProperty("version") String version,
    // ...
) {}
```

**Confidence:** HIGH - Pattern already established in codebase

### Testing (EXISTING)

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| JUnit 5 | 5.8.x (existing) | Unit tests | Standard testing framework already in use |
| json-unit | 2.x (existing) | JSON assertion | Already in reportcard-server dependencies. Perfect for validating parsed JSON structures. |

**Rationale:** Existing test infrastructure supports JSON comparison. Use JsonAssert pattern already established in codebase.

**Confidence:** HIGH - json-unit already present in build.gradle line 40

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| JSON Parsing | Jackson | Gson | Jackson already present and more feature-rich. Gson would duplicate functionality. |
| JSON Parsing | Jackson | org.json | org.json lacks type-safe binding. Jackson's ObjectMapper is superior. |
| Schema Validation | json-schema-validator | everit-json-schema | everit is older, less maintained. networknt is actively developed. |
| Date Parsing | java.time.* | Joda-Time | Joda-Time is obsolete. java.time is built-in and superior. |
| Date Parsing | java.time.* | SimpleDateFormat | SimpleDateFormat is not thread-safe. DateTimeFormatter is modern replacement. |

## NOT Recommended

### Do NOT Use

| Library | Why Avoid |
|---------|-----------|
| Karate's internal JSON classes | Karate is a test framework, not a parsing library. Using internal classes creates unnecessary dependency coupling. |
| org.json | Lacks type safety, primitive API, inferior to Jackson |
| GSON | Redundant with Jackson, adds dependency bloat |
| SimpleDateFormat | Not thread-safe, obsolete, replaced by java.time |
| JSON-B (Jakarta JSON Binding) | Overkill for this use case, Jackson is sufficient |

## Installation

### Required (None - Zero New Dependencies for MVP)

All parsing can use existing Jackson infrastructure:

```gradle
// Already in reportcard-server/build.gradle
// No changes needed
```

### Optional (Schema Validation - Recommended)

Add to `reportcard-server/build.gradle`:

```gradle
dependencies {
    // Karate JSON schema validation (optional but recommended)
    implementation 'com.networknt:json-schema-validator:1.5.3'
}
```

## Implementation Strategy

### Phase 1: Core Parsing (MVP)

1. **Create Model Classes** (reportcard-model/src/main/java)
   - `KarateSummary.java` - Record for summary JSON
   - `KarateFeatureSummary.java` - Record for feature details
   - `KarateResultDate.java` - Custom deserializer for date format

2. **Create Parser** (reportcard-model/src/main/java)
   - `KarateJsonParser.java` - Uses SharedObjectMappers.permissiveObjectMapper
   - Extract elapsedTime, totalTime, resultDate from JSON
   - Map to TestResultModel (existing structure)

3. **Date Handling**
   ```java
   DateTimeFormatter KARATE_DATE_FORMAT =
       DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
   ```

### Phase 2: Validation (Post-MVP)

1. **JSON Schema Definition**
   - Create `karate-summary-schema.json` in resources
   - Define required fields and types
   - Validate before parsing

2. **Error Handling**
   - Graceful failures for malformed JSON
   - Clear error messages for missing fields
   - Log schema violations

## Testing Strategy

### Unit Tests

Use existing test patterns from Surefire/TestNG parsers:

```java
@Test
void testParseKarateSummary() throws Exception {
    String json = Files.readString(
        Path.of("src/test/resources/karate-summary-json.txt")
    );
    KarateSummary summary = KarateJsonParser.parse(json);

    assertEquals(307767.0, summary.elapsedTime());
    assertEquals(348061.0, summary.totalTime());
    assertEquals("1.2.0", summary.version());
}
```

### Integration Tests

Follow AbstractTestResultPersistTest pattern:
- Load actual Karate JSON from cucumber-json directory
- Parse and persist to test database (Testcontainers MySQL)
- Verify round-trip data integrity

## Version Compatibility

| Component | Version | Notes |
|-----------|---------|-------|
| Karate | 1.2.0+ | JSON format tested with 1.2.0. Minor format changes possible in future versions. |
| Jackson | 2.13.x | Locked by Spring Boot 2.6.15 |
| Java | 17 | Records require Java 16+, already using 17 |

### Future Karate Versions

Karate JSON format is relatively stable. Changes would likely be:
- Additional fields (non-breaking if using FAIL_ON_UNKNOWN_PROPERTIES=false)
- Field renames (breaking - would need parser version detection)

**Mitigation:** Check `version` field and branch parsing logic if needed.

## Performance Considerations

| Concern | Impact | Mitigation |
|---------|--------|------------|
| Large JSON files | Karate reports can be 5-10 MB for large test suites | Use Jackson streaming API (JsonParser) for files >1 MB |
| Date parsing | DateTimeFormatter parsing called for every test run | Cache formatter instance (thread-safe) |
| Schema validation | 10-20ms overhead per file | Make validation optional, only enable for debugging |

### Recommended Thresholds

- Files <1 MB: Use ObjectMapper.readValue (simplest)
- Files >1 MB: Use streaming JsonParser (memory efficient)
- Files >10 MB: Consider rejecting or chunking

## Sources

### Verified Sources (HIGH Confidence)

- Karate 1.2.0 JSON samples in repository: `/Users/eric.r.driggs/github/ericdriggs/reportcard/cucumber-json/karate-reports/`
- Jackson configuration in SharedObjectMappers.java (verified present)
- Existing dependency in build.gradle (line 40: json-unit)
- Java 17 DateTimeFormatter API (JDK standard)

### Community Sources (MEDIUM Confidence)

- json-schema-validator GitHub: https://github.com/networknt/json-schema-validator (1.5.3 current as of Jan 2026)
- Jackson Databind documentation: https://github.com/FasterXML/jackson-databind

### Notes on Confidence

- **HIGH:** All core parsing uses existing Jackson dependency (verified in codebase)
- **MEDIUM:** Schema validation library recommendation (not in current dependencies)
- **LOW:** None - all recommendations based on existing infrastructure or verified sources

## Migration Path

### For Existing Test Formats

Karate support is additive, does not affect:
- JUnit XML parsing (existing)
- TestNG XML parsing (existing)
- Surefire XML parsing (existing)

All existing parsers continue to work unchanged.

### Database Schema

No database changes required if using existing test_result table structure. Karate metrics map to:
- `elapsedTime` → test_result.time (BigDecimal)
- `resultDate` → test_result.test_date (Instant)
- Test counts → standard pass/fail/skip counts

## Decision Matrix

| Requirement | Solution | Confidence |
|-------------|----------|------------|
| Parse Karate JSON | Jackson (existing) | HIGH |
| Extract elapsedTime | POJO field mapping | HIGH |
| Extract totalTime | POJO field mapping | HIGH |
| Parse resultDate | DateTimeFormatter | HIGH |
| Validate schema | json-schema-validator (optional) | MEDIUM |
| Test parsing | JUnit + json-unit (existing) | HIGH |

## Conclusion

**Recommended Approach:** Use existing Jackson infrastructure with custom POJOs for Karate's JSON structure. Zero new required dependencies for MVP. Optional schema validation adds safety but increases complexity.

**Key Success Factor:** Follow existing patterns from SurefireConvertersUtil.java and TestResultModel.java for consistency with codebase architecture.

**Risk Level:** LOW - Leverages proven Jackson stack already battle-tested in production for XML parsing.
