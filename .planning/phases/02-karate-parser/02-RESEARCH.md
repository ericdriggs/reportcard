# Phase 2: Karate Parser - Research

**Researched:** 2026-01-26
**Domain:** JSON parsing for test framework output (Karate DSL summary files)
**Confidence:** HIGH

## Summary

This phase requires creating a JSON parser for Karate test framework summary files (`karate-summary-json.txt`). The codebase has well-established patterns for test result parsers in the `reportcard-model` module under `model/converter/` packages.

The existing architecture separates parser utilities (in `reportcard-model`) from persistence logic (in `reportcard-server`). Jackson ObjectMapper is the standard for JSON parsing, with shared configurations in `SharedObjectMappers`. Date/time parsing should use Java 8+ `DateTimeFormatter` following existing patterns in `StoragePath.java`.

**Primary recommendation:** Follow the surefire/junit converter pattern: create a `karate` subpackage under `model/converter/`, implement parser utility with static methods, create model POJOs for the JSON structure, and write unit tests using `ResourceReader` for test fixtures.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Jackson Databind | 2.17.1+ | JSON parsing and serialization | Universal JSON library in codebase, shared ObjectMapper instances |
| Jackson JSR310 | 2.17.1 | Java 8 time types support | Already configured for LocalDateTime, Instant serialization |
| Java DateTimeFormatter | Java 17 | Date/time parsing | Standard Java API, used in StoragePath for date formatting |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Lombok | (existing) | Reduce boilerplate (builders, getters) | Model classes and data structures |
| SLF4J | (existing) | Logging framework | Warning/error logging for parse failures |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Jackson | Gson | Jackson already integrated with shared mappers |
| SimpleDateFormat | DateTimeFormatter | DateTimeFormatter is thread-safe and modern |

**Installation:**
```bash
# No new dependencies needed - Jackson already in reportcard-model/build.gradle
# Verify:
grep "jackson-databind" reportcard-model/build.gradle
```

## Architecture Patterns

### Recommended Project Structure
```
reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/
├── karate/
│   ├── KarateConvertersUtil.java      # Main parser utility (static methods)
│   └── KarateSummary.java             # POJO for karate-summary-json.txt structure
└── JunitSurefireXmlParseUtil.java     # Example pattern to follow

reportcard-model/src/test/java/io/github/ericdriggs/reportcard/model/converter/
└── karate/
    └── KarateConvertersUtilTest.java  # Unit tests

reportcard-model/src/test/resources/format-samples/
└── karate/
    ├── karate-summary-valid.json
    ├── karate-summary-missing-fields.json
    └── karate-summary-malformed.json
```

### Pattern 1: Static Utility Class with Shared ObjectMapper
**What:** Parser class as enum with static-only methods, reusing `SharedObjectMappers`
**When to use:** All converter utilities in this codebase follow this pattern
**Example:**
```java
// Source: reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/surefire/SurefireConvertersUtil.java
package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum KarateConvertersUtil {
    ;//static methods only

    public static KarateSummary parseKarateSummary(String jsonContent) {
        return SharedObjectMappers.readValueOrDefault(
            jsonContent,
            KarateSummary.class,
            null
        );
    }
}
```

### Pattern 2: POJO Model Classes with Jackson Annotations
**What:** Create Plain Old Java Objects with Jackson annotations for JSON mapping
**When to use:** Represent the structure of external file formats
**Example:**
```java
// Structure matching karate-summary-json.txt
package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KarateSummary {
    @JsonProperty("elapsedTime")
    private Double elapsedTime;  // milliseconds

    @JsonProperty("totalTime")
    private Double totalTime;

    @JsonProperty("resultDate")
    private String resultDate;  // "2026-01-20 03:00:56 PM"

    @JsonProperty("featuresPassed")
    private Integer featuresPassed;

    @JsonProperty("featuresFailed")
    private Integer featuresFailed;

    // Other fields as needed
}
```

### Pattern 3: Test Fixtures with ResourceReader
**What:** Store sample files in `src/test/resources/format-samples/` and load with `ResourceReader`
**When to use:** All parser unit tests in this codebase
**Example:**
```java
// Source: reportcard-model/src/test/java/io/github/ericdriggs/reportcard/model/converter/JunitSurefireXmlParseUtilTest.java
@Test
void parseKarateSummaryTest() {
    String karateJson = ResourceReader.resourceAsString(
        "format-samples/karate/karate-summary-valid.json"
    );
    KarateSummary summary = KarateConvertersUtil.parseKarateSummary(karateJson);
    assertNotNull(summary);
    assertNotNull(summary.getElapsedTime());
}
```

### Pattern 4: Date Parsing with DateTimeFormatter
**What:** Parse Karate's date format using DateTimeFormatter pattern
**When to use:** Converting "2026-01-20 03:00:56 PM" to LocalDateTime
**Example:**
```java
// Source: java.time.format.DateTimeFormatter API
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KarateDateParser {
    private static final DateTimeFormatter KARATE_DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

    public static LocalDateTime parseResultDate(String resultDate) {
        try {
            return LocalDateTime.parse(resultDate, KARATE_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse Karate resultDate: {}", resultDate, e);
            return null;
        }
    }
}
```

### Anti-Patterns to Avoid
- **Creating new ObjectMapper instances:** Use `SharedObjectMappers` instead - already configured for unknown properties and time modules
- **Using SimpleDateFormat:** Not thread-safe, use `DateTimeFormatter`
- **Throwing exceptions on parse failure:** Log warnings and return null (per requirements)
- **Placing parser in reportcard-server:** Parsers belong in reportcard-model module

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| JSON parsing | Custom string parsing | Jackson ObjectMapper (`SharedObjectMappers`) | Handles edge cases, configured for unknown properties, JSR310 time types |
| Reading test resources | Custom file I/O | ResourceReader.resourceAsString() | Standard pattern in all tests, handles classpath resolution |
| Date parsing | String manipulation | DateTimeFormatter | Thread-safe, handles AM/PM, validates dates |
| Null safety | Manual null checks | Lombok @NonNull, Objects.requireNonNullElse() | Consistent with codebase, less verbose |
| Logging | System.out | SLF4J (@Slf4j annotation) | Standard logging framework, configurable levels |

**Key insight:** Jackson's `readValueOrDefault()` method in `SharedObjectMappers` already handles malformed JSON gracefully - returns default value and logs warning, exactly matching requirements.

## Common Pitfalls

### Pitfall 1: Date Format Pattern Mismatch
**What goes wrong:** Using wrong DateTimeFormatter pattern fails to parse Karate dates
**Why it happens:** Karate format `"2026-01-20 03:00:56 PM"` has 12-hour time with AM/PM
**How to avoid:** Use pattern `"yyyy-MM-dd hh:mm:ss a"` (lowercase `hh` for 12-hour, `a` for AM/PM marker)
**Warning signs:** DateTimeParseException mentioning "could not be parsed"

### Pitfall 2: Missing @JsonIgnoreProperties(ignoreUnknown = true)
**What goes wrong:** Jackson fails to deserialize if Karate adds new fields to summary JSON
**Why it happens:** By default, Jackson throws exception on unknown properties
**How to avoid:** Add `@JsonIgnoreProperties(ignoreUnknown = true)` to KarateSummary class (or use SharedObjectMappers.permissiveObjectMapper which already configures this)
**Warning signs:** JsonMappingException mentioning "Unrecognized field"

### Pitfall 3: Timezone Assumptions
**What goes wrong:** Treating resultDate as UTC when it's local time
**Why it happens:** No timezone in Karate's date string, defaults to system timezone
**How to avoid:** Use `LocalDateTime.parse()` not `ZonedDateTime` or `Instant` - per requirements, treat as local time
**Warning signs:** Time discrepancies between expected and actual timestamps

### Pitfall 4: Null Handling in Double Math
**What goes wrong:** NullPointerException when elapsedTime is null
**Why it happens:** JSON may have missing fields, Double allows null
**How to avoid:** Check for null before arithmetic: `if (elapsedTime != null && resultDate != null)`
**Warning signs:** NPE in calculation methods

### Pitfall 5: Module Boundary Violation
**What goes wrong:** Placing parser code in reportcard-server module
**Why it happens:** Proximity to persistence layer seems convenient
**How to avoid:** Always place parsers/converters in reportcard-model module
**Warning signs:** Build failures due to circular dependencies (server can import model, not vice versa)

## Code Examples

Verified patterns from codebase:

### Using SharedObjectMappers for Lenient Parsing
```java
// Source: reportcard-model/src/main/java/io/github/ericdriggs/reportcard/mappers/SharedObjectMappers.java
public static <T> T readValueOrDefault(String json, Class clazz, T tDefault) {
    try {
        return (T) permissiveObjectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
        log.warn("unable to parse json: {}, returning default: {}", json, tDefault, e);
        return tDefault;
    }
}

// Usage in parser:
KarateSummary summary = SharedObjectMappers.readValueOrDefault(
    jsonContent,
    KarateSummary.class,
    null  // default value on parse failure
);
```

### Test Pattern with ResourceReader
```java
// Source: reportcard-model/src/test/java/io/github/ericdriggs/reportcard/model/converter/JunitSurefireXmlParseUtilTest.java
@Test
void karateValidSummaryTest() {
    String karateJson = ResourceReader.resourceAsString(
        "format-samples/karate/karate-summary-valid.json"
    );
    KarateSummary summary = KarateConvertersUtil.parseKarateSummary(karateJson);

    assertNotNull(summary);
    assertEquals(307767.0, summary.getElapsedTime());
    assertEquals("2026-01-20 03:00:56 PM", summary.getResultDate());
}

@Test
void karateMalformedJsonTest() {
    String malformedJson = "{invalid json";
    KarateSummary summary = KarateConvertersUtil.parseKarateSummary(malformedJson);

    assertNull(summary); // Should return null, not throw
}
```

### Date Parsing with Null Safety
```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

private static final DateTimeFormatter KARATE_DATE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

public static LocalDateTime parseResultDate(String resultDate) {
    if (resultDate == null || resultDate.isBlank()) {
        log.warn("Karate resultDate is null or blank");
        return null;
    }

    try {
        return LocalDateTime.parse(resultDate, KARATE_DATE_FORMATTER);
    } catch (DateTimeParseException e) {
        log.warn("Failed to parse Karate resultDate: {}", resultDate, e);
        return null;
    }
}
```

### Calculating Start Time
```java
public static LocalDateTime calculateStartTime(
    LocalDateTime resultDate,
    Double elapsedTimeMillis
) {
    if (resultDate == null || elapsedTimeMillis == null) {
        log.warn("Cannot calculate start time: resultDate={}, elapsedTime={}",
            resultDate, elapsedTimeMillis);
        return null;
    }

    if (elapsedTimeMillis < 0) {
        log.warn("Negative elapsedTime: {}, treating as null", elapsedTimeMillis);
        return null;
    }

    long elapsedSeconds = (long) (elapsedTimeMillis / 1000.0);
    long elapsedNanos = (long) ((elapsedTimeMillis % 1000.0) * 1_000_000);

    return resultDate.minusSeconds(elapsedSeconds).minusNanos(elapsedNanos);
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| XML-only test results | Multi-format support (XML + JSON) | Phase 2 (now) | Need JSON parsing alongside existing XML parsers |
| Per-format persistence logic | Unified TestResultModel | Established | Karate parser outputs should match existing model structure |
| Manual Jackson configuration | SharedObjectMappers | Established | Reuse pre-configured ObjectMapper instances |

**Deprecated/outdated:**
- SimpleDateFormat: Use DateTimeFormatter (thread-safe, modern)
- Creating ObjectMapper per parser: Use SharedObjectMappers singleton

## Open Questions

Things that couldn't be fully resolved:

1. **Locale for AM/PM Parsing**
   - What we know: Karate uses "AM"/"PM" in English
   - What's unclear: Whether to explicitly set Locale.US or rely on default
   - Recommendation: Use `DateTimeFormatter.ofPattern("...", Locale.US)` for consistency

2. **Warning Visibility in API Response**
   - What we know: Requirements state "parsing errors should be visible in API response as warnings"
   - What's unclear: Existing parsers only log warnings, don't return them in response objects
   - Recommendation: Phase 2 logs warnings (matches existing pattern), Phase 3 handles response structure if needed

3. **Handling Very Large elapsedTime**
   - What we know: Values > 24 hours should be accepted
   - What's unclear: Upper bound for sanity check (month? year?)
   - Recommendation: No upper bound validation - accept any positive value

## Sources

### Primary (HIGH confidence)
- Codebase files examined (all absolute paths):
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/JunitSurefireXmlParseUtil.java`
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/surefire/SurefireConvertersUtil.java`
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/mappers/SharedObjectMappers.java`
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-model/src/main/java/io/github/ericdriggs/reportcard/xml/ResourceReader.java`
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/StoragePath.java`
- Sample Karate JSON: `/Users/eric.r.driggs/github/ericdriggs/reportcard/cucumber-json/karate-reports/karate-summary-json.txt`
- Phase context: `.planning/phases/02-karate-parser/02-CONTEXT.md`

### Secondary (MEDIUM confidence)
- Java 17 DateTimeFormatter API documentation (standard library)
- Jackson 2.17.x behavior (version in use)

### Tertiary (LOW confidence)
- None - all findings verified with codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Jackson already integrated, versions confirmed in build.gradle
- Architecture: HIGH - Clear patterns in existing converter packages
- Pitfalls: HIGH - Date format parsing validated with sample data, null handling patterns from existing code

**Research date:** 2026-01-26
**Valid until:** 2026-03-26 (60 days - stable domain, established patterns)
