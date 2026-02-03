# Phase 6: reportcard-client-java Support - Context

**Gathered:** 2026-02-03
**Status:** Ready for planning

<domain>
## Phase Boundary

Add Karate JSON upload support to the sibling repository reportcard-client-java (located at `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java`). This is a separate codebase with no shared code with the in-tree reportcard-client module.

</domain>

<decisions>
## Implementation Decisions

### Repository relationship
- Located at `/Users/eric.r.driggs/git/hulu/sublife/reportcard-client-java`
- Completely independent codebase, no shared code with in-tree reportcard-client
- Independent versioning (not tied to server versions)
- Auto-versioning via CI

### API design
- Same server endpoint as in-tree client (`/v1/api/junit/storage/html/tar.gz`)
- Add optional `karateFolderPath` field to existing `JunitHtmlPostRequest.builder()`
- Karate parameter is optional (works without it like before)
- Accept directory path, create tar.gz internally (like in-tree client)
- Response handling matches in-tree client

### Build & release
- Gradle-based project (build.gradle)
- Published to internal artifact repo
- Auto-versioning handles version bumps

### Testing strategy
- Add WireMock for mock tests simulating server responses
- Keep existing integration tests that hit real server
- Test scenarios: with Karate, without Karate (legacy), error cases, timeout handling
- Ensure backwards compatibility with legacy (no Karate) uploads

### Backwards compatibility
- Fully backwards compatible at API level
- Existing `JunitHtmlPostRequest.builder()` calls work unchanged
- Same endpoint works for both with/without Karate
- Error handling: Log and continue if Karate upload fails (don't fail the whole upload)

### Claude's Discretion
- Exact tar.gz creation implementation
- WireMock test setup details
- Internal error message formatting

</decisions>

<specifics>
## Specific Ideas

- "Should be able to test with and without JSON so will want to be able to test legacy behavior to ensure backwards compatible"
- "Would like to add mock tests from server"
- "Current integration tests are probably actually publishing and would like to have more robust testing in general"

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 06-reportcard-client-java-support*
*Context gathered: 2026-02-03*
