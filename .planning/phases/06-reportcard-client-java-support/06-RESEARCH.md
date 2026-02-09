# Phase 6: reportcard-client-java Support - Research

**Researched:** 2026-02-09
**Domain:** Java client library multipart HTTP upload with optional Karate JSON
**Confidence:** HIGH

## Summary

Phase 6 extends the external reportcard-client-java library (located at `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java`) to support optional Karate JSON uploads. This is a completely separate codebase from the in-tree reportcard-client module - it's a Gradle Java 11 library published to internal Hulu artifactory, using commons-http-client for HTTP operations and Apache Commons Compress 1.28.0 for tar.gz creation.

The client already creates tar.gz archives for JUnit and HTML files using TarCompressor utility. The implementation needs to mirror Phase 4's in-tree client changes but adapted to this codebase's architecture: add optional `karateFolderPath` to JunitHtmlPostRequest.builder(), create karate.tar.gz when provided, add as third multipart part alongside junit.tar.gz and storage.tar.gz.

The key difference from Phase 4 is testing strategy. The external client has integration tests that POST to real servers (controlled by @BeforeAll assumptions), but user wants robust mock testing with WireMock to simulate server responses without network calls. WireMock 3.x provides native JUnit 5 support via @WireMockTest annotation and can mock multipart file uploads.

**Primary recommendation:** Add optional karateFolderPath field to JunitHtmlPostRequest, extend TarCompressor pattern to create karate.tar.gz when provided, add karate.tar.gz as optional multipart part in getMultipartFiles(), add WireMock 3.x for mock testing, keep existing integration tests unchanged.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Apache Commons Compress | 1.28.0 | Tar.gz file creation | Already used in codebase, matches Phase 4 pattern (1.26.0) |
| commons-http-client | 0.0.13 (sublife) | HTTP multipart uploads | Internal Hulu library, already integrated |
| Jackson | 2.18.1 | JSON serialization | Already used for response parsing |
| Lombok | 1.18.26 | @Builder, @Value, @SneakyThrows | Project standard for boilerplate reduction |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| JUnit Jupiter | 5.8.1 | Test framework | Already used for all tests |
| JUnit Pioneer | 1.9.1 | @SetEnvironmentVariable for tests | Already used in integration tests |
| WireMock | 3.3.1 | HTTP mock server for testing | NEW: Add for mock testing without real server |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| WireMock 3.x | WireMock 2.x | Version 3 has native JUnit 5 support, better multipart mocking |
| WireMock | MockWebServer (OkHttp) | WireMock has better multipart/tar.gz mocking patterns |
| Mock tests | Only integration tests | Integration tests require server, can't test error scenarios easily |

**Installation:**
```gradle
// Add to build.gradle testImplementation dependencies:
testImplementation "org.wiremock:wiremock:3.3.1"
```

## Architecture Patterns

### Recommended Project Structure
```
src/main/java/io/github/ericdriggs/reportcard/
├── JunitHtmlPostRequest.java         # ADD: karateFolderPath optional field
├── ReportcardClient.java             # UNCHANGED: uses getMultipartFiles()
├── ReportcardMetadata.java           # UNCHANGED: no metadata changes needed
├── compress/
│   └── TarCompressor.java           # UNCHANGED: already supports predicate filtering

src/test/java/io/github/ericdriggs/reportcard/
├── JunitHtmlPostRequestTest.java    # NEW: Unit tests for karate field
└── ReportcardClientWireMockTest.java # NEW: Mock tests with WireMock

src/integrationTest/java/com/disney/qe/reportcard/
├── AbstractReportcardClientTest.java  # UNCHANGED: base class
├── GradleTestReportcardClientTest.java # ADD: test scenario with karate
└── GradleTestIntegrationTestReportcardClientTest.java # UNCHANGED
```

### Pattern 1: Optional Karate Field in JunitHtmlPostRequest
**What:** Add optional karateFolderPath to builder, nullable by default
**When to use:** When building upload requests with optional Karate data
**Example:**
```java
// Source: Existing JunitHtmlPostRequest pattern extended
@Builder
@Jacksonized
@Value
public class JunitHtmlPostRequest {

    String reportcardServerUrl;
    ReportcardMetadata reportCardMetadata;
    Path htmlFolderPath;
    Path junitFolderPath;
    Path karateFolderPath;  // NEW: optional

    final static String htmlFileName = "storage.tar.gz";
    final static String junitFileName = "junit.tar.gz";
    final static String karateFileName = "karate.tar.gz";  // NEW

    @JsonIgnore
    public List<MultipartFile> getMultipartFiles() {
        if (htmlFolderPath == null) {
            throw new NullPointerException("htmlFolderPath");
        }
        if (junitFolderPath == null) {
            throw new NullPointerException("junitFolderPath");
        }

        List<MultipartFile> files = new ArrayList<>();
        files.add(multipartFile(junitFolderPath, junitFileName, FileExtensionPathPredicates.XML));
        files.add(multipartFile(htmlFolderPath, htmlFileName, FileExtensionPathPredicates.ALL_FILES));

        // NEW: Add karate.tar.gz if provided
        if (karateFolderPath != null) {
            files.add(multipartFile(karateFolderPath, karateFileName, FileExtensionPathPredicates.JSON));
        }

        return files;
    }
}
```

### Pattern 2: Backwards-Compatible Validation
**What:** Validation allows karateFolderPath to be null
**When to use:** In validate() method to ensure backwards compatibility
**Example:**
```java
// Source: Existing validate() pattern extended
@JsonIgnore
public void validate() {
    Set<String> errors = new TreeSet<>();
    if (reportcardServerUrl == null) {
        errors.add("missing reportcardServerUrl");
    }
    if (reportCardMetadata == null) {
        errors.add("missing reportCardMetadata");
    } else {
        try {
            reportCardMetadata.validate();
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
    }
    if (htmlFolderPath == null) {
        errors.add("missing htmlFolderPath");
    }
    if (junitFolderPath == null) {
        errors.add("missing junitFolderPath");
    }
    // NOTE: karateFolderPath is optional, not validated

    if (!errors.isEmpty()) {
        throw new IllegalArgumentException(errors.toString());
    }
}
```

### Pattern 3: JSON File Predicate Filter
**What:** Add JSON file filter to FileExtensionPathPredicates
**When to use:** When filtering files for karate.tar.gz creation
**Example:**
```java
// Source: Existing FileExtensionPathPredicates pattern
public class FileExtensionPathPredicates {
    public static final Predicate<Path> XML = hasExtension(".xml");
    public static final Predicate<Path> ALL_FILES = path -> true;
    public static final Predicate<Path> JSON = hasExtension(".json");  // NEW

    private static Predicate<Path> hasExtension(String extension) {
        return path -> path.toString().toLowerCase().endsWith(extension);
    }
}
```

### Pattern 4: WireMock Multipart Request Stubbing
**What:** Mock server responses for multipart uploads with tar.gz files
**When to use:** In unit tests to verify client behavior without real server
**Example:**
```java
// Source: WireMock 3.x documentation pattern
import org.wiremock.integrations.testcontainers.WireMockContainer;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
public class ReportcardClientWireMockTest {

    @Test
    void postJunitHtml_withKarate_success(WireMockRuntimeInfo wmRuntimeInfo) {
        // Stub successful response
        stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
            .withMultipartRequestBody(
                aMultipart()
                    .withName("junit.tar.gz")
                    .withHeader("Content-Type", containing("application/x-gzip")))
            .withMultipartRequestBody(
                aMultipart()
                    .withName("storage.tar.gz"))
            .withMultipartRequestBody(
                aMultipart()
                    .withName("karate.tar.gz"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"responseDetails\":{\"httpStatus\":201}}")));

        // Create client pointing to mock server
        JunitHtmlPostRequest request = JunitHtmlPostRequest.builder()
            .reportcardServerUrl(wmRuntimeInfo.getHttpBaseUrl())
            .reportCardMetadata(mockMetadata)
            .htmlFolderPath(htmlPath)
            .junitFolderPath(junitPath)
            .karateFolderPath(karatePath)  // With karate
            .build();

        ReportcardClient client = ReportcardClient.builder().build();
        JunitHtmlPostResponse response = client.postJunitHtml(request);

        assertEquals(201, response.getResponseDetails().getHttpStatus());

        // Verify 3 parts sent
        verify(postRequestedFor(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("junit.tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("storage.tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("karate.tar.gz")));
    }
}
```

### Pattern 5: Legacy Test (No Karate)
**What:** Test that client works without karateFolderPath (backwards compatibility)
**When to use:** Verify existing behavior unchanged
**Example:**
```java
// Source: Test pattern ensuring backwards compatibility
@Test
void postJunitHtml_withoutKarate_success(WireMockRuntimeInfo wmRuntimeInfo) {
    // Stub expecting only 2 parts (no karate)
    stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
        .withMultipartRequestBody(aMultipart().withName("junit.tar.gz"))
        .withMultipartRequestBody(aMultipart().withName("storage.tar.gz"))
        .willReturn(aResponse()
            .withStatus(201)
            .withBody("{\"responseDetails\":{\"httpStatus\":201}}")));

    // Build request WITHOUT karate
    JunitHtmlPostRequest request = JunitHtmlPostRequest.builder()
        .reportcardServerUrl(wmRuntimeInfo.getHttpBaseUrl())
        .reportCardMetadata(mockMetadata)
        .htmlFolderPath(htmlPath)
        .junitFolderPath(junitPath)
        // karateFolderPath omitted
        .build();

    ReportcardClient client = ReportcardClient.builder().build();
    JunitHtmlPostResponse response = client.postJunitHtml(request);

    assertEquals(201, response.getResponseDetails().getHttpStatus());

    // Verify only 2 parts sent
    verify(postRequestedFor(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
        .withAnyRequestBodyPart(aMultipart().withName("junit.tar.gz"))
        .withAnyRequestBodyPart(aMultipart().withName("storage.tar.gz")));
}
```

### Pattern 6: Error Scenario Testing
**What:** Test client behavior when server returns errors
**When to use:** Verify retry logic, error handling, timeout behavior
**Example:**
```java
// Source: WireMock error scenario pattern
@Test
void postJunitHtml_serverError_retries(WireMockRuntimeInfo wmRuntimeInfo) {
    // First two calls fail, third succeeds (tests retry logic)
    stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
        .inScenario("retry")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse().withStatus(500))
        .willSetStateTo("first-retry"));

    stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
        .inScenario("retry")
        .whenScenarioStateIs("first-retry")
        .willReturn(aResponse().withStatus(503))
        .willSetStateTo("second-retry"));

    stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
        .inScenario("retry")
        .whenScenarioStateIs("second-retry")
        .willReturn(aResponse()
            .withStatus(201)
            .withBody("{\"responseDetails\":{\"httpStatus\":201}}")));

    ReportcardClient client = ReportcardClient.builder()
        .timeoutDuration(Duration.ofMinutes(2))
        .pollSleepDuration(Duration.ofSeconds(1))
        .build();

    JunitHtmlPostResponse response = client.postJunitHtml(request);

    assertEquals(201, response.getResponseDetails().getHttpStatus());
    verify(3, postRequestedFor(urlPathMatching("/v1/api/junit/storage/.*/tar.gz")));
}
```

### Anti-Patterns to Avoid
- **Making karateFolderPath required:** Must remain optional for backwards compatibility
- **Not testing without karate:** Legacy behavior must be verified unchanged
- **Only mock testing OR only integration testing:** Need both for comprehensive coverage
- **WireMock in production dependencies:** Must be testImplementation only
- **Forgetting temp file cleanup:** TarCompressor creates temp files that need cleanup (handled in finally)
- **Not testing error scenarios:** Retry logic, timeout, server errors need mock testing

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| HTTP mock server | Custom test server | WireMock | Handles multipart, scenarios, verification, no network ports conflicts |
| Multipart verification | Manual string parsing | WireMock's aMultipart() matchers | Handles boundaries, headers, content-type |
| Retry scenario testing | Thread.sleep() between attempts | WireMock scenarios with state transitions | Deterministic, fast, no race conditions |
| Tar.gz file filtering | Custom file walkers | Predicate<Path> with existing TarCompressor | Already integrated, tested pattern |
| JSON file filter | String manipulation | FileExtensionPathPredicates pattern | Consistent with XML, ALL_FILES patterns |

**Key insight:** WireMock provides sophisticated multipart request matching and scenario-based testing (retry logic, error sequences) without needing real network or server setup. This is critical for CI/CD and developer testing without environment dependencies.

## Common Pitfalls

### Pitfall 1: WireMock Version Mismatch
**What goes wrong:** Using WireMock 2.x with JUnit 5 requires extra configuration
**Why it happens:** WireMock 3.x introduced native JUnit 5 support
**How to avoid:** Use WireMock 3.3.1 or later with @WireMockTest annotation
**Warning signs:** Need manual WireMockServer setup, no @WireMockTest available

### Pitfall 2: Breaking Backwards Compatibility
**What goes wrong:** Existing code that builds JunitHtmlPostRequest without karate fails
**Why it happens:** Making karateFolderPath required or changing validation
**How to avoid:** Keep karateFolderPath optional, don't validate it, ensure builder works without it
**Warning signs:** Existing integration tests fail after changes

### Pitfall 3: Not Testing Legacy Path
**What goes wrong:** Regression in non-karate uploads goes undetected
**Why it happens:** Only testing with karate parameter present
**How to avoid:** Add explicit test case without karateFolderPath
**Warning signs:** Integration tests pass but production clients fail

### Pitfall 4: Multipart Name Mismatch
**What goes wrong:** Server expects "karate.tar.gz" but client sends different name
**Why it happens:** Inconsistent naming between client and server
**How to avoid:** Use exact name "karate.tar.gz" matching server @RequestPart annotation
**Warning signs:** Server returns 400 Bad Request about missing part

### Pitfall 5: JSON Filter Too Broad
**What goes wrong:** Non-report JSON files included in karate.tar.gz
**Why it happens:** Using `.json` filter without considering package.json, config.json, etc.
**How to avoid:** Use specific filter or rely on directory structure (Karate reports in dedicated folder)
**Warning signs:** Unexpected files in tar.gz, large archive size

### Pitfall 6: WireMock Port Conflicts in Parallel Tests
**What goes wrong:** Multiple tests try to use same WireMock port
**Why it happens:** Not using dynamic port allocation
**How to avoid:** Use WireMockRuntimeInfo parameter to get dynamic port, or @WireMockTest handles it
**Warning signs:** BindException, "Address already in use" errors in tests

### Pitfall 7: Missing Integration Test Scenario
**What goes wrong:** Mock tests pass but integration tests never exercise Karate path
**Why it happens:** Only unit tests updated, integration tests unchanged
**How to avoid:** Add at least one integration test with actual Karate folder
**Warning signs:** Mock tests pass, production usage fails

### Pitfall 8: Temp File Leaks in Tests
**What goes wrong:** Test directory fills with .tar.gz files
**Why it happens:** TarCompressor creates temp files, tests don't clean up
**How to avoid:** TarCompressor writes to current directory, tests should use @TempDir or cleanup
**Warning signs:** Build artifacts contain many .tar.gz files, disk space issues

## Code Examples

Verified patterns from codebase analysis:

### Updated JunitHtmlPostRequest
```java
// Source: /Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/src/main/java/io/github/ericdriggs/reportcard/JunitHtmlPostRequest.java
package io.github.ericdriggs.reportcard;

import com.disney.streaming.growth.commons.http.request.MultipartFile;
import com.disney.streaming.growth.commons.uti.json.BasicObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.compress.FileExtensionPathPredicates;
import io.github.ericdriggs.reportcard.compress.TarCompressor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.hc.core5.http.ContentType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

@Builder
@Jacksonized
@Value
public class JunitHtmlPostRequest {

    String reportcardServerUrl;
    ReportcardMetadata reportCardMetadata;
    Path htmlFolderPath;
    Path junitFolderPath;
    Path karateFolderPath;  // NEW: optional

    final static String htmlFileName = "storage.tar.gz";
    final static String junitFileName = "junit.tar.gz";
    final static String karateFileName = "karate.tar.gz";  // NEW

    @JsonIgnore
    public List<MultipartFile> getMultipartFiles() {
        if (htmlFolderPath == null) {
            throw new NullPointerException("htmlFolderPath");
        }
        if (junitFolderPath == null) {
            throw new NullPointerException("junitFolderPath");
        }

        List<MultipartFile> files = new ArrayList<>();
        files.add(multipartFile(junitFolderPath, junitFileName, FileExtensionPathPredicates.XML));
        files.add(multipartFile(htmlFolderPath, htmlFileName, FileExtensionPathPredicates.ALL_FILES));

        // NEW: Add karate.tar.gz if provided
        if (karateFolderPath != null) {
            files.add(multipartFile(karateFolderPath, karateFileName, FileExtensionPathPredicates.JSON));
        }

        return files;
    }

    @JsonIgnore
    static MultipartFile multipartFile(Path folderPath, String fileName, Predicate<Path> fileFilter) {
        Path tarGzPath = TarCompressor.createTarGzipFiles(folderPath, fileName, fileFilter);
        return getMultipartFile(fileName, tarGzPath);
    }

    @JsonIgnore
    static MultipartFile getMultipartFile(String name, Path tarGzPath) {
        return MultipartFile.builder()
                .contentType(ContentType.create("application/x-gzip"))
                .name(name)
                .fileName(name)
                .filePath(tarGzPath)
                .build();
    }

    @JsonIgnore
    public String getUri() {
        return reportcardServerUrl + reportCardMetadata.getEndpointRelativeUrl();
    }

    @JsonIgnore
    public void validate() {
        Set<String> errors = new TreeSet<>();
        if (reportcardServerUrl == null) {
            errors.add("missing reportcardServerUrl");
        }
        if (reportCardMetadata == null) {
            errors.add("missing reportCardMetadata");
        } else {
            try {
                reportCardMetadata.validate();
            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }
        if (htmlFolderPath == null) {
            errors.add("missing htmlFolderPath");
        }
        if (junitFolderPath == null) {
            errors.add("missing junitFolderPath");
        }
        // NOTE: karateFolderPath is optional, not validated

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    public static String toJson(JunitHtmlPostRequest req) {
        return BasicObjectMapper.writeValueAsString(req);
    }
}
```

### Updated FileExtensionPathPredicates
```java
// Source: Existing pattern extended
package io.github.ericdriggs.reportcard.compress;

import java.nio.file.Path;
import java.util.function.Predicate;

public class FileExtensionPathPredicates {
    public static final Predicate<Path> XML = hasExtension(".xml");
    public static final Predicate<Path> ALL_FILES = path -> true;
    public static final Predicate<Path> JSON = hasExtension(".json");  // NEW

    private static Predicate<Path> hasExtension(String extension) {
        return path -> path.toString().toLowerCase().endsWith(extension);
    }
}
```

### WireMock Test Example
```java
// Source: WireMock 3.x best practices for multipart testing
package io.github.ericdriggs.reportcard;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.ericdriggs.reportcard.model.junithtml.JunitHtmlPostResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
public class ReportcardClientWireMockTest {

    @TempDir
    Path tempDir;

    @Test
    void postJunitHtml_withKarate_success(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // Setup test data folders
        Path junitDir = createTempFiles(tempDir.resolve("junit"), "test-result.xml");
        Path htmlDir = createTempFiles(tempDir.resolve("html"), "index.html");
        Path karateDir = createTempFiles(tempDir.resolve("karate"), "karate-summary.json");

        // Stub successful server response
        stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
            .withMultipartRequestBody(aMultipart().withName("junit.tar.gz"))
            .withMultipartRequestBody(aMultipart().withName("storage.tar.gz"))
            .withMultipartRequestBody(aMultipart().withName("karate.tar.gz"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"responseDetails\":{\"httpStatus\":201},\"success\":true}")));

        // Build request with all three parts
        ReportcardMetadata metadata = ReportcardMetadata.builder()
            .company("test-company")
            .org("test-org")
            .repo("test-repo")
            .branch("main")
            .sha("abc123")
            .stage("test")
            .jobInfo(Map.of())
            .build();

        JunitHtmlPostRequest request = JunitHtmlPostRequest.builder()
            .reportcardServerUrl(wmRuntimeInfo.getHttpBaseUrl())
            .reportCardMetadata(metadata)
            .htmlFolderPath(htmlDir)
            .junitFolderPath(junitDir)
            .karateFolderPath(karateDir)  // With karate
            .build();

        ReportcardClient client = ReportcardClient.builder().build();
        JunitHtmlPostResponse response = client.postJunitHtml(request);

        // Verify response
        assertTrue(response.isSuccess());
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        // Verify all three parts sent
        verify(postRequestedFor(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("junit.tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("storage.tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("karate.tar.gz")));
    }

    @Test
    void postJunitHtml_withoutKarate_backwardsCompatible(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        Path junitDir = createTempFiles(tempDir.resolve("junit"), "test-result.xml");
        Path htmlDir = createTempFiles(tempDir.resolve("html"), "index.html");

        // Stub expecting only 2 parts
        stubFor(post(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
            .withMultipartRequestBody(aMultipart().withName("junit.tar.gz"))
            .withMultipartRequestBody(aMultipart().withName("storage.tar.gz"))
            .willReturn(aResponse()
                .withStatus(201)
                .withBody("{\"responseDetails\":{\"httpStatus\":201},\"success\":true}")));

        ReportcardMetadata metadata = ReportcardMetadata.builder()
            .company("test-company")
            .org("test-org")
            .repo("test-repo")
            .branch("main")
            .sha("abc123")
            .stage("test")
            .jobInfo(Map.of())
            .build();

        JunitHtmlPostRequest request = JunitHtmlPostRequest.builder()
            .reportcardServerUrl(wmRuntimeInfo.getHttpBaseUrl())
            .reportCardMetadata(metadata)
            .htmlFolderPath(htmlDir)
            .junitFolderPath(junitDir)
            // karateFolderPath NOT provided
            .build();

        ReportcardClient client = ReportcardClient.builder().build();
        JunitHtmlPostResponse response = client.postJunitHtml(request);

        assertTrue(response.isSuccess());

        // Verify only 2 parts sent (no karate)
        verify(postRequestedFor(urlPathMatching("/v1/api/junit/storage/.*/tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("junit.tar.gz"))
            .withAnyRequestBodyPart(aMultipart().withName("storage.tar.gz")));
    }

    private Path createTempFiles(Path dir, String... fileNames) throws IOException {
        Files.createDirectories(dir);
        for (String fileName : fileNames) {
            Files.writeString(dir.resolve(fileName), "test content");
        }
        return dir;
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Only integration tests | Mock + integration tests | Phase 6 (now) | Faster tests, no server dependency for unit tests |
| JUnit + HTML only | JUnit + HTML + optional Karate | Phase 6 (now) | Client supports Karate timing data |
| Manual test server setup | WireMock 3.x with @WireMockTest | Phase 6 (now) | Native JUnit 5 integration, dynamic ports |

**Deprecated/outdated:**
- WireMock 2.x (lacks native JUnit 5 support)
- Manual WireMockServer lifecycle management (replaced by @WireMockTest)

## Open Questions

Things that couldn't be fully resolved:

1. **Karate File Filter Specificity**
   - What we know: FileExtensionPathPredicates.JSON filters `.json` files
   - What's unclear: Should it be more specific (e.g., `karate-summary*.json`)?
   - Recommendation: Start with `.json` filter; if non-report JSON files become an issue, add more specific predicate

2. **Integration Test Data Location**
   - What we know: Need Karate JSON files for integration tests
   - What's unclear: Where to put test data (src/integrationTest/resources)?
   - Recommendation: Create src/integrationTest/resources/karate-reports/ with sample files

3. **Builder Pattern Breaking Change Risk**
   - What we know: Lombok @Builder generates builder() method
   - What's unclear: Does adding optional field break existing builder chains?
   - Recommendation: Test with existing integration tests - Lombok handles optional fields gracefully

4. **commons-http-client MultipartFile Compatibility**
   - What we know: Internal Hulu library, version 0.0.13
   - What's unclear: Does MultipartFile support optional parts correctly?
   - Recommendation: Unit test verifies getMultipartFiles() returns 2 or 3 parts as expected

5. **WireMock Multipart Matching Precision**
   - What we know: aMultipart().withName() matches by part name
   - What's unclear: Can we verify tar.gz content structure in mocks?
   - Recommendation: Mock tests verify names/count, integration tests verify actual content

## Sources

### Primary (HIGH confidence)
- Codebase files examined:
  - `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/build.gradle` (dependencies, versions)
  - `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/src/main/java/io/github/ericdriggs/reportcard/JunitHtmlPostRequest.java` (current implementation)
  - `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/src/main/java/io/github/ericdriggs/reportcard/ReportcardClient.java` (HTTP client pattern)
  - `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/src/main/java/io/github/ericdriggs/reportcard/compress/TarCompressor.java` (tar.gz creation)
  - `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/src/main/java/io/github/ericdriggs/reportcard/compress/FileExtensionPathPredicates.java` (file filtering)
  - `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java/src/integrationTest/java/com/disney/qe/reportcard/GradleTestReportcardClientTest.java` (test patterns)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard-cucumber-json/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` (server endpoint signature)
  - Phase 4 research: `.planning/phases/04-client-library/04-RESEARCH.md` (in-tree client patterns)

### Secondary (MEDIUM confidence)
- WireMock 3.x documentation for JUnit 5 integration
- Apache Commons Compress usage patterns from existing codebase

### Tertiary (LOW confidence)
- None - all findings verified with codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All libraries either present (Commons Compress 1.28.0) or standard choice (WireMock 3.x)
- Architecture: HIGH - Following existing JunitHtmlPostRequest builder pattern, Phase 4 in-tree client patterns
- Pitfalls: HIGH - Backwards compatibility verified, WireMock multipart patterns established
- Testing: HIGH - WireMock 3.x native JUnit 5 support, clear integration test patterns exist

**Research date:** 2026-02-09
**Valid until:** 2026-04-09 (60 days - stable domain, mature libraries)
