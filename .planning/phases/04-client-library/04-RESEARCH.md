# Phase 4: Client Library - Research

**Researched:** 2026-01-26
**Domain:** Java HTTP client multipart upload with tar.gz file generation
**Confidence:** HIGH

## Summary

Phase 4 updates the Java client library to support uploading Karate JSON files alongside JUnit XMLs. The client currently uses Spring WebClient for multipart uploads and sends individual XML files directly (not tar.gz as the server expects). This phase requires three key changes: (1) add optional `karateJsonFile` parameter to the client builder/request, (2) create tar.gz archives from directories before upload, and (3) update the endpoint URL from the incorrect `/v1/api/reports/` to the correct `/v1/api/junit/storage/{label}/tar.gz`.

The codebase has TarCompressor utilities in reportcard-server that can be reused or replicated in the client. The existing client uses Spring WebFlux's WebClient with MultipartBodyBuilder for multipart uploads. Apache Commons Compress 1.26.0 is already used server-side for tar.gz operations.

**Primary recommendation:** Add `KARATE_REPORT_PATH` optional parameter to ClientArg, add `karateJsonFile` field to ReportMetaData, create utility to tar.gz directories, update PostWebClient to include karate.tar.gz part when provided, and fix the endpoint URL to match the server's actual API.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring WebFlux | 5.3.x (from Spring Boot 2.6.15) | WebClient for HTTP multipart upload | Already used in PostWebClient |
| Apache Commons Compress | 1.26.0 | Tar.gz file creation | Used server-side, battle-tested for archive operations |
| Apache Commons IO | 2.8.0 | File utilities | Already in client dependencies |
| Lombok | (existing) | @Data, @SneakyThrows annotations | Project standard for boilerplate reduction |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Jackson | 2.x | JSON serialization for metadata | Already used for ReportMetaData serialization |
| SLF4J | (existing) | Logging | Error and debug messages |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Apache Commons Compress | Java 11 native tar API | Commons Compress is more mature, handles edge cases better |
| WebClient multipart | HttpClient MultipartBodyPublisher | WebClient already integrated, no reason to change |
| Separate endpoint | Extend existing multipart request | Server already provides unified endpoint, no need for two calls |

**Installation:**
```bash
# Add to reportcard-client/build.gradle dependencies:
implementation 'org.apache.commons:commons-compress:1.26.0'
# (commons-io and commons-lang3 already present)
```

## Architecture Patterns

### Recommended Project Structure
```
reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/
├── ClientArg.java                 # ADD: KARATE_REPORT_PATH optional enum value
├── ReportMetaData.java            # ADD: karateJsonFile String field
├── PostRequest.java               # UPDATE: getPostUrl() to use correct endpoint
├── PostWebClient.java             # UPDATE: add karate.tar.gz multipart if present
└── util/
    └── TarGzUtil.java            # NEW: createTarGzFromDirectory(Path dir) -> Path tarGz
```

### Pattern 1: Optional Client Parameter
**What:** Add optional KARATE_REPORT_PATH to ClientArg enum
**When to use:** When adding new optional upload sources
**Example:**
```java
// Source: Existing ClientArg pattern
public enum ClientArg {
    // ... existing required args ...

    /**
     * The path to a single folder containing Karate JSON reports. Will not search sub-folders.
     * (Optional)
     */
    KARATE_REPORT_PATH(false),

    // ... existing optional args ...
}
```

### Pattern 2: Directory to Tar.gz Conversion
**What:** Create tar.gz archive from all files in a directory
**When to use:** Before uploading JUnit XMLs or Karate JSON files
**Example:**
```java
// Source: Server TarCompressor pattern adapted for client
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class TarGzUtil {

    @SneakyThrows(IOException.class)
    public static Path createTarGzFromDirectory(Path directory) {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path must be a directory: " + directory);
        }

        List<Path> files = Files.list(directory)
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No files found in directory: " + directory);
        }

        Path tarGzOutput = Files.createTempFile("reportcard-", ".tar.gz");

        try (OutputStream fOut = Files.newOutputStream(tarGzOutput);
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            for (Path file : files) {
                TarArchiveEntry tarEntry = new TarArchiveEntry(
                    file.toFile(),
                    file.getFileName().toString()
                );
                tOut.putArchiveEntry(tarEntry);
                Files.copy(file, tOut);
                tOut.closeArchiveEntry();
            }
            tOut.finish();
        }

        return tarGzOutput;
    }
}
```

### Pattern 3: Multipart Upload with Optional Karate Part
**What:** Add karate.tar.gz to multipart request when karateJsonFile provided
**When to use:** In PostWebClient.postTestReport when building multipart body
**Example:**
```java
// Source: Existing PostWebClient pattern extended
protected Mono<String> postTestReport(PostRequest scannerPostRequest) {

    // Create JUnit tar.gz from XML files
    Path junitTarGz = createJunitTarGz(scannerPostRequest.getReportMetaData().getTestReportPath());

    // Create Karate tar.gz if Karate path provided
    Path karateTarGz = null;
    String karatePath = scannerPostRequest.getReportMetaData().getKarateJsonFile();
    if (!StringUtils.isEmpty(karatePath)) {
        karateTarGz = TarGzUtil.createTarGzFromDirectory(Path.of(karatePath));
    }

    MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
    try {
        multipartBodyBuilder.part("reportMetaData",
            objectMapper.writeValueAsString(scannerPostRequest),
            MediaType.APPLICATION_JSON);

        multipartBodyBuilder.part("junit.tar.gz",
            new FileSystemResource(junitTarGz.toFile()),
            MediaType.APPLICATION_OCTET_STREAM);

        if (karateTarGz != null) {
            multipartBodyBuilder.part("karate.tar.gz",
                new FileSystemResource(karateTarGz.toFile()),
                MediaType.APPLICATION_OCTET_STREAM);
        }

    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }

    // ... rest of WebClient request ...
}
```

### Pattern 4: Correct Endpoint URL
**What:** Update getPostUrl() to use server's actual endpoint
**When to use:** In PostRequest.getPostUrl()
**Example:**
```java
// Source: Server JunitController endpoint mapping
public String getPostUrl() {
    // OLD: return reportCardServerData.getReportCardHost() + "/v1/api/reports/";
    // NEW: Use the actual combined upload endpoint
    String label = "html"; // or make this configurable
    return reportCardServerData.getReportCardHost() +
           "/v1/api/junit/storage/" + label + "/tar.gz";
}
```

### Pattern 5: Temp File Cleanup
**What:** Delete temporary tar.gz files after upload completes
**When to use:** After WebClient request finishes (success or failure)
**Example:**
```java
// Pattern using try-finally for cleanup
protected Mono<String> postTestReport(PostRequest scannerPostRequest) {
    Path junitTarGz = null;
    Path karateTarGz = null;

    try {
        junitTarGz = createJunitTarGz(...);
        karateTarGz = createKarateTarGz(...);

        // ... build and send request ...

        return monoResponse;

    } finally {
        // Cleanup temp files
        deleteSafely(junitTarGz);
        deleteSafely(karateTarGz);
    }
}

private void deleteSafely(Path path) {
    if (path != null) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temp file: {}", path, e);
        }
    }
}
```

### Anti-Patterns to Avoid
- **Sending individual files instead of tar.gz:** Server expects tar.gz format, not individual files
- **Ignoring URL mismatch:** Client URL `/v1/api/reports/` doesn't exist on server; must use `/v1/api/junit/storage/{label}/tar.gz`
- **Not cleaning up temp tar.gz files:** Will leak disk space over time
- **Making karate.tar.gz required:** Must remain optional for backwards compatibility
- **Creating tar.gz in-memory:** Large test reports should stream to temp files

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Tar.gz creation | Custom archive code | Apache Commons Compress TarArchiveOutputStream + GzipCompressorOutputStream | Handles entry ordering, compression levels, file permissions |
| Multipart file upload | Manual boundary generation | Spring WebClient MultipartBodyBuilder | Handles content-type headers, boundaries, streaming |
| File walking | Manual directory traversal | Files.list() or Files.walk() | Handles symlinks, permissions errors |
| Temp file management | Manual temp paths | Files.createTempFile() | Platform-independent, collision-free, automatic prefix |
| Path manipulation | String concatenation | Path.resolve() and Path.of() | Handles separators, normalization across platforms |

**Key insight:** The client currently sends individual files when the server expects tar.gz archives. This fundamental mismatch must be fixed for both JUnit and Karate uploads. The server's TarCompressor pattern provides the correct approach.

## Common Pitfalls

### Pitfall 1: URL Endpoint Mismatch
**What goes wrong:** Client sends to `/v1/api/reports/` which doesn't exist; 404 error
**Why it happens:** PostRequest.getPostUrl() returns wrong path
**How to avoid:** Update to `/v1/api/junit/storage/{label}/tar.gz` matching server JunitController
**Warning signs:** 404 Not Found errors in integration tests

### Pitfall 2: Sending Individual Files Instead of Tar.gz
**What goes wrong:** Server expects tar.gz but receives individual XML/JSON files
**Why it happens:** Current client sends files directly without archiving
**How to avoid:** Always create tar.gz archive first, then upload the archive
**Warning signs:** Server returns 400 or cannot parse uploaded files

### Pitfall 3: Temp File Leak
**What goes wrong:** Disk fills up with unreleased .tar.gz temp files
**Why it happens:** Exception thrown before cleanup, or forgetting to delete
**How to avoid:** Use try-finally pattern to ensure cleanup:
```java
Path tarGz = null;
try {
    tarGz = createTarGz(...);
    upload(tarGz);
} finally {
    deleteSafely(tarGz);
}
```
**Warning signs:** Disk space decreasing, many reportcard-*.tar.gz in temp directory

### Pitfall 4: Directory Path Confusion
**What goes wrong:** Client creates tar.gz from wrong directory or includes subdirectories
**Why it happens:** Using Files.walk() instead of Files.list(), or Path parameter interpretation
**How to avoid:** Use Files.list() (single level only) matching TEST_REPORT_PATH behavior:
```java
List<Path> files = Files.list(directory)
    .filter(Files::isRegularFile)
    .collect(Collectors.toList());
```
**Warning signs:** Unexpected subdirectories in tar.gz, missing expected files

### Pitfall 5: Missing Label Parameter
**What goes wrong:** Server endpoint requires {label} path parameter but client doesn't provide it
**Why it happens:** Endpoint URL has variable path segment
**How to avoid:** Make label configurable or use default "html":
```java
// Either add to ClientArg/ReportMetaData or use default
String label = reportMetaData.getLabel() != null ? reportMetaData.getLabel() : "html";
```
**Warning signs:** 404 errors or incorrect URL in logs

### Pitfall 6: Regex Filter Not Applied to Tar.gz Creation
**What goes wrong:** All files included in tar.gz instead of just matching regex
**Why it happens:** Forgetting to apply TEST_REPORT_REGEX filter when listing files
**How to avoid:** Apply regex filter before creating tar.gz:
```java
Pattern pattern = Pattern.compile(reportMetaData.getTestReportRegex());
List<Path> files = Files.list(directory)
    .filter(Files::isRegularFile)
    .filter(p -> pattern.matcher(p.getFileName().toString()).matches())
    .collect(Collectors.toList());
```
**Warning signs:** Non-test files included in upload, unexpected file count

### Pitfall 7: Storage.tar.gz Missing
**What goes wrong:** Server endpoint requires storage.tar.gz part but client doesn't provide it
**Why it happens:** Endpoint `/v1/api/junit/storage/{label}/tar.gz` expects junit.tar.gz, optional karate.tar.gz, AND storage.tar.gz
**How to avoid:** Check server endpoint signature - may need to provide HTML report archive too
**Warning signs:** 400 Bad Request, missing required part error

## Code Examples

Verified patterns from codebase:

### Updated ReportMetaData
```java
// Source: Existing ReportMetaData.java pattern
@Data
public class ReportMetaData {
    // ... existing fields ...
    private String testReportPath;
    private String testReportRegex;
    private String karateJsonFile;  // NEW: optional Karate report directory

    @SneakyThrows(JsonProcessingException.class)
    public ReportMetaData(Map<ClientArg, String> argMap) {
        // ... existing initialization ...
        final String karateJsonFile = argMap.get(ClientArg.KARATE_REPORT_PATH);

        // ... existing validation ...
        this.karateJsonFile = karateJsonFile;
    }
}
```

### TarGzUtil Helper
```java
// Source: Server TarCompressor.java adapted for client
package io.github.ericdriggs.reportcard.client.util;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TarGzUtil {

    @SneakyThrows(IOException.class)
    public static Path createTarGzFromDirectory(Path directory, String fileRegex) {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }

        List<Path> files = Files.list(directory)
            .filter(Files::isRegularFile)
            .filter(p -> p.getFileName().toString().matches(fileRegex))
            .collect(Collectors.toList());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No matching files found in: " + directory);
        }

        return createTarGzFromFiles(files);
    }

    @SneakyThrows(IOException.class)
    public static Path createTarGzFromFiles(List<Path> files) {
        Path tarGzOutput = Files.createTempFile("reportcard-", ".tar.gz");

        try (OutputStream fOut = Files.newOutputStream(tarGzOutput);
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            for (Path file : files) {
                if (!Files.isRegularFile(file)) {
                    throw new IOException("Not a regular file: " + file);
                }
                TarArchiveEntry tarEntry = new TarArchiveEntry(
                    file.toFile(),
                    file.getFileName().toString()
                );
                tOut.putArchiveEntry(tarEntry);
                Files.copy(file, tOut);
                tOut.closeArchiveEntry();
            }
            tOut.finish();
        }

        return tarGzOutput;
    }
}
```

### Updated PostWebClient
```java
// Source: Existing PostWebClient.postTestReport with tar.gz creation
protected Mono<String> postTestReport(PostRequest scannerPostRequest) {

    Path junitTarGz = null;
    Path karateTarGz = null;

    try {
        // Create JUnit tar.gz
        junitTarGz = TarGzUtil.createTarGzFromDirectory(
            Path.of(scannerPostRequest.getReportMetaData().getTestReportPath()),
            scannerPostRequest.getReportMetaData().getTestReportRegex()
        );

        // Create Karate tar.gz if path provided
        String karatePath = scannerPostRequest.getReportMetaData().getKarateJsonFile();
        if (!StringUtils.isEmpty(karatePath)) {
            karateTarGz = TarGzUtil.createTarGzFromDirectory(
                Path.of(karatePath),
                ".*\\.json$"  // or make this configurable
            );
        }

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("reportMetaData",
            objectMapper.writeValueAsString(scannerPostRequest),
            MediaType.APPLICATION_JSON);

        multipartBodyBuilder.part("junit.tar.gz",
            new FileSystemResource(junitTarGz.toFile()),
            MediaType.APPLICATION_OCTET_STREAM);

        if (karateTarGz != null) {
            multipartBodyBuilder.part("karate.tar.gz",
                new FileSystemResource(karateTarGz.toFile()),
                MediaType.APPLICATION_OCTET_STREAM);
        }

        WebClient.RequestHeadersSpec<?> requestHeadersSpec = client.post()
            .uri(scannerPostRequest.getPostUrl())
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()));

        Mono<String> monoResponse = requestHeadersSpec.exchangeToMono(resp -> {
            if (resp.statusCode().equals(HttpStatus.OK)) {
                return resp.bodyToMono(String.class);
            } else {
                throw new ResponseStatusException(
                    HttpStatus.valueOf(resp.statusCode().value()),
                    resp.bodyToMono(String.class).block()
                );
            }
        });

        return monoResponse;

    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    } finally {
        // Cleanup temp files
        deleteSafely(junitTarGz);
        deleteSafely(karateTarGz);
    }
}

private void deleteSafely(Path path) {
    if (path != null) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temp file: {}", path, e);
        }
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Send individual XML files | Send tar.gz archives | Phase 4 (now) | Client matches server's expected format |
| JUnit-only uploads | JUnit + optional Karate | Phase 4 (now) | Client supports Karate timing data upload |
| Wrong endpoint URL | Correct `/v1/api/junit/storage/{label}/tar.gz` | Phase 4 (now) | Client can actually reach server API |

**Deprecated/outdated:**
- Sending individual files without tar.gz (current behavior - must be replaced)
- Using `/v1/api/reports/` endpoint (doesn't exist on server)

## Open Questions

Things that couldn't be fully resolved:

1. **Storage.tar.gz Requirement**
   - What we know: Server endpoint `/v1/api/junit/storage/{label}/tar.gz` requires storage.tar.gz part
   - What's unclear: Current client doesn't create or upload HTML reports - is this a required parameter?
   - Recommendation: Check server endpoint signature; if storage.tar.gz is required, client may need HTML report generation or empty archive

2. **Label Parameter Source**
   - What we know: Server endpoint has {label} path variable
   - What's unclear: Should client allow configurable label or always use "html"?
   - Recommendation: Make label configurable via ClientArg or default to "html"

3. **Endpoint Mismatch Severity**
   - What we know: Client points to `/v1/api/reports/` which doesn't exist
   - What's unclear: Has client ever successfully uploaded? Or is this broken?
   - Recommendation: Check if there's a separate legacy endpoint, or if client is currently non-functional

4. **Commons Compress Version Sync**
   - What we know: Server uses 1.26.0
   - What's unclear: Should client use exact same version for consistency?
   - Recommendation: Use 1.26.0 to match server for consistent tar.gz format

5. **Karate File Regex**
   - What we know: Need to filter Karate files like TEST_REPORT_REGEX for JUnit
   - What's unclear: Should regex be configurable or default to `.*\.json$`?
   - Recommendation: Add optional KARATE_REPORT_REGEX to ClientArg, default to `.*\.json$`

## Sources

### Primary (HIGH confidence)
- Codebase files examined:
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ClientArg.java` (existing parameter pattern)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ReportMetaData.java` (metadata structure)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostWebClient.java` (multipart upload pattern)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostRequest.java` (URL construction - INCORRECT)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/util/tar/TarCompressor.java` (tar.gz creation pattern)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/JunitController.java` (server endpoint signature)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/JunitControllerKarateTest.java` (server test patterns)
  - `/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-client/build.gradle` (dependencies)

### Secondary (MEDIUM confidence)
- Phase 3 Research: `.planning/phases/03-api-integration/03-RESEARCH.md` (server API patterns)
- Spring WebFlux WebClient multipart documentation patterns

### Tertiary (LOW confidence)
- None - all findings verified with codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All libraries either already present or standard choices (Commons Compress)
- Architecture: HIGH - Following server TarCompressor pattern, existing client patterns
- Pitfalls: HIGH - URL mismatch discovered in code, tar.gz requirement clear from server

**Research date:** 2026-01-26
**Valid until:** 2026-03-26 (60 days - stable domain, established patterns)
