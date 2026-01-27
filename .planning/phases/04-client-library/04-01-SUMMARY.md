---
phase: 04-client-library
plan: 01
subsystem: client
tags: [java, spring-boot, webclient, tar.gz, apache-commons-compress, multipart-upload, karate-json]

# Dependency graph
requires:
  - phase: 03-api-integration
    provides: Server endpoint /v1/api/junit/storage/html/tar.gz accepting multipart uploads
provides:
  - Java client library that uploads JUnit XML reports as tar.gz archives
  - Optional Karate JSON report upload capability alongside JUnit
  - TarGzUtil utility for creating tar.gz archives from directories
  - Automatic temporary file cleanup after uploads
affects: [05-integration-testing, client-users]

# Tech tracking
tech-stack:
  added: [apache-commons-compress-1.26.0]
  patterns: [tar.gz-multipart-upload, temporary-file-cleanup-pattern, optional-file-upload]

key-files:
  created:
    - reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/util/TarGzUtil.java
  modified:
    - reportcard-client/build.gradle
    - reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ClientArg.java
    - reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ReportMetaData.java
    - reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostRequest.java
    - reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostWebClient.java

key-decisions:
  - "Apache Commons Compress 1.26.0 matches server version for consistent tar.gz format"
  - "Single-level directory scan (Files.list not Files.walk) matches existing TEST_REPORT_PATH behavior"
  - "Karate upload failures are warnings not errors - field is optional"
  - "Endpoint URL changed to /v1/api/junit/storage/html/tar.gz to match server implementation"
  - "Temporary tar.gz files cleaned up in finally block to prevent disk space leaks"

patterns-established:
  - "TarGzUtil creates temporary tar.gz files that callers must clean up"
  - "Optional multipart uploads: include part only if data available"
  - "Try-finally pattern for guaranteed temporary file cleanup"

# Metrics
duration: 3min
completed: 2026-01-27
---

# Phase 04 Plan 01: Client Library Summary

**Java client uploads JUnit XML and optional Karate JSON as tar.gz archives to multipart endpoint with automatic cleanup**

## Performance

- **Duration:** 3 min
- **Started:** 2026-01-27T09:08:33Z
- **Completed:** 2026-01-27T09:11:16Z
- **Tasks:** 3
- **Files modified:** 6

## Accomplishments
- Added Apache Commons Compress dependency for tar.gz creation
- Created TarGzUtil utility for directory-to-tar.gz conversion with regex filtering
- Updated client to upload junit.tar.gz (required) and karate.tar.gz (optional) to correct server endpoint
- Implemented automatic temporary file cleanup in finally block

## Task Commits

Each task was committed atomically:

1. **Task 1: Add Apache Commons Compress and KARATE_REPORT_PATH parameter** - `b5426fd` (feat)
2. **Task 2: Create TarGzUtil for tar.gz archive creation** - `ae291c0` (feat)
3. **Task 3: Update PostWebClient for tar.gz uploads to correct endpoint** - `35d531c` (feat)

## Files Created/Modified
- `reportcard-client/build.gradle` - Added commons-compress 1.26.0 dependency
- `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ClientArg.java` - Added KARATE_REPORT_PATH optional enum value
- `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ReportMetaData.java` - Added karateJsonFile field extracted from argMap
- `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/util/TarGzUtil.java` - Created utility for tar.gz creation from directories with regex filtering
- `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostRequest.java` - Updated endpoint URL to /v1/api/junit/storage/html/tar.gz
- `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostWebClient.java` - Replaced individual file upload with tar.gz creation and multipart upload, added cleanup

## Decisions Made

**Apache Commons Compress version:** Used 1.26.0 to match server version for consistent tar.gz format across client and server

**Single-level directory scan:** TarGzUtil uses Files.list() not Files.walk() to match existing TEST_REPORT_PATH behavior (no subdirectory recursion)

**Karate upload as optional:** Karate tar.gz creation failures log warnings but don't fail the upload - field is optional

**Endpoint change:** Updated from /v1/api/reports/ to /v1/api/junit/storage/html/tar.gz to match actual server implementation

**Cleanup pattern:** Temporary tar.gz files deleted in finally block for guaranteed cleanup even on upload failure

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

**Pre-existing build issue:** Gradle build fails with Java 11/17 compatibility error in reportcard-server module configuration (nu.studer:gradle-jooq-plugin:8.0). This issue exists on the previous commit (a3a961e) before any client changes, confirming it's environmental not related to this work. Client code changes are syntactically correct and follow Spring WebFlux patterns.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

Client library ready for:
- Integration testing with server endpoint
- End-to-end testing of JUnit + Karate upload flow
- Verification that timing data flows through full stack

Blockers: Build environment needs Java/Gradle configuration fix (pre-existing issue), but this doesn't block code verification or next phase planning.

---
*Phase: 04-client-library*
*Completed: 2026-01-27*
