---
phase: 05-foundation-dtos
plan: 01
subsystem: api
tags: [dto, jackson, lombok, json-serialization, response-mapping]

# Dependency graph
requires:
  - phase: 04-api-exposure
    provides: BrowseJsonController endpoints returning Map<Pojo, Map<Pojo, Set<Pojo>>>
provides:
  - CompanyOrgsResponse DTO for /v1/api endpoint
  - CompanyOrgsReposResponse DTO for /company/{company} endpoint
  - OrgReposBranchesResponse DTO for /company/{company}/org/{org} endpoint
  - Factory pattern fromMap() for transforming cache Maps to DTOs
  - Nested entity/children JSON structure pattern
affects: [05-02, 05-03, 06-controller-integration]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "DTO inner classes for entity wrappers (Company, Org, Repo, Branch)"
    - "Static fromMap() factory methods for Map-to-DTO transformation"
    - "@JsonInclude(NON_NULL) for clean JSON responses"
    - "Lombok @Data @Builder @NoArgsConstructor @AllArgsConstructor for DTOs"

key-files:
  created:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/response/CompanyOrgsResponse.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/response/CompanyOrgsReposResponse.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/response/OrgReposBranchesResponse.java
  modified: []

key-decisions:
  - "Parent object + children array format: {\"company\": {...}, \"orgs\": [...]}"
  - "Copy fields to DTO rather than embedding raw Pojo"
  - "Include FK fields (companyFk, orgFk, repoFk) for client references"
  - "Single-entry extraction for filtered endpoints (company/{company} returns one company)"

patterns-established:
  - "DTO Structure: Outer class with List<Entry> or single entity + List<ChildEntry>"
  - "Entry Pattern: XxxEntry contains entity wrapper + children list"
  - "Factory Method: static fromMap() transforms cache Map to DTO"
  - "Field Copying: fromPojo() helper methods on each inner class"

# Metrics
duration: 12min
completed: 2026-02-06
---

# Phase 05 Plan 01: Foundation DTOs Summary

**Three Response DTOs with nested entity/children JSON structure and static fromMap() factory methods for transforming cache Maps**

## Performance

- **Duration:** 12 min
- **Started:** 2026-02-06T12:00:00Z
- **Completed:** 2026-02-06T12:12:00Z
- **Tasks:** 3
- **Files created:** 3

## Accomplishments
- Created CompanyOrgsResponse DTO with companies array containing company+orgs structure
- Created CompanyOrgsReposResponse DTO with company object + orgs array (each with org+repos)
- Created OrgReposBranchesResponse DTO with org object + repos array (each with repo+branches)
- Established consistent DTO pattern with @JsonInclude(NON_NULL) and Lombok annotations
- All factory methods transform Map structures to clean nested JSON

## Task Commits

Each task was committed atomically:

1. **Task 1: Create CompanyOrgsResponse DTO** - `7657bc2` (feat)
2. **Task 2: Create CompanyOrgsReposResponse DTO** - `9e1f5c4` (feat)
3. **Task 3: Create OrgReposBranchesResponse DTO** - `48170c1` (feat)

## Files Created

- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/response/CompanyOrgsResponse.java` - DTO for /v1/api endpoint with CompanyEntry, Company, OrgEntry inner classes
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/response/CompanyOrgsReposResponse.java` - DTO for /company/{company} with CompanyEntry, OrgReposEntry, OrgEntry, RepoEntry inner classes
- `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/response/OrgReposBranchesResponse.java` - DTO for /company/{company}/org/{org} with OrgEntry, RepoBranchesEntry, RepoEntry, BranchEntry inner classes

## Decisions Made

- Used inner static classes for entity wrappers to keep related types together
- Each inner class has its own fromPojo() method for clean transformation
- Single-entry extraction pattern for filtered endpoints (e.g., first entry from Map when endpoint filters by company)
- BranchEntry includes Instant lastRun field - Jackson handles ISO-8601 serialization automatically

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

- JAVA_HOME was set to Java 11 but project requires Java 17. Resolved by explicitly setting JAVA_HOME for Gradle commands.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- Three foundation DTOs ready for controller integration
- Pattern established for remaining DTOs (RepoBranchesJobsResponse, BranchJobsRunsResponse, etc.)
- Plan 05-02 can create remaining DTOs following same pattern
- Plan 05-03 can integrate DTOs into BrowseJsonController endpoints

---
*Phase: 05-foundation-dtos*
*Completed: 2026-02-06*
