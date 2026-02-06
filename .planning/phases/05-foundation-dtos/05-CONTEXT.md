# Phase 5: Foundation DTOs - Context

**Gathered:** 2026-02-06
**Status:** Ready for planning

<domain>
## Phase Boundary

Create Response DTOs that transform internal `Map<Pojo, Map<Pojo, Set<Pojo>>>` structures into clean nested JSON. Convert the first three endpoints (`/v1/api`, `/company/{company}`, `/company/{company}/org/{org}`) to establish the pattern for remaining endpoints.

</domain>

<decisions>
## Implementation Decisions

### DTO Structure
- Entity fields + children array at same level: `{"companyId": 3, "companyName": "hulu", "orgs": [{"orgId": 1, "orgName": "foo", "repos": [...]}]}`
- No redundant wrapper objects — parent array name makes type clear
- Match Pojo field names (camelCase): `companyId`, `companyName`, `orgId`, etc.
- Match endpoint depth: `/company/{company}` returns company + orgs + repos (2 levels of children)
- Preserve ordering from internal Maps (TreeMap/TreeSet order carries through to JSON arrays)

### Pojo Field Exposure
- Include foreign key fields (companyFk, orgFk) — clients may need explicit references
- Always include ID fields (companyId, orgId, repoId) — clients need them for API calls
- Omit null values using `@JsonInclude(NON_NULL)` — cleaner responses
- Copy fields to DTO (not embed raw Pojo) — more control over serialization

### Claude's Discretion
- Transformation location (controller vs converter class)
- Package organization for DTO classes
- Exact Lombok annotations vs manual getters
- Builder pattern vs constructor

</decisions>

<specifics>
## Specific Ideas

- Current problematic output: `{"CompanyPojo(companyId=3, companyName=hulu)": {...}}`
- Target output: `{"company": {"companyId": 3, "companyName": "hulu"}, "orgs": [...]}`
- Internal cache/service logic must remain unchanged — transformation at controller boundary only

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 05-foundation-dtos*
*Context gathered: 2026-02-06*
