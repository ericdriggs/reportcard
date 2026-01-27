# Phase 2: Karate Parser - Context

**Gathered:** 2026-01-26
**Status:** Ready for planning

<domain>
## Phase Boundary

Parse Karate JSON summary files and extract timing data (elapsedTime, resultDate) to calculate start_time and end_time for run records. This phase creates the parser component only — API integration and persistence happen in Phase 3.

</domain>

<decisions>
## Implementation Decisions

### Input file handling
- Parse summary file only (`karate-summary-json.txt`), not individual feature files
- Default location: `build/karate-reports/karate-summary-json.txt` (follow conventions allowing override)
- All `-json.txt` files are in `build/karate-reports/` directory
- If summary file not found: warn and continue (don't fail the upload)

### Date/time parsing
- Karate resultDate format: `2026-01-20 03:00:56 PM`
- Treat as local time (server timezone when Karate ran) — published immediately after generation
- If date can't be parsed: set timing to null, log warning, continue

### Error handling
- Malformed JSON: log warning, continue with null timing
- Negative elapsedTime: treat as null (invalid data)
- Very large elapsedTime (> 24 hours): accept as-is (conceivable for long test suites)
- Parsing errors should be visible in API response as warnings (not just server logs)

### Claude's Discretion
- JSON validation approach (strict vs lenient field checking)
- Exact date parsing implementation
- Log level and message format

</decisions>

<specifics>
## Specific Ideas

- Summary file contains: `elapsedTime` (milliseconds), `totalTime` (milliseconds), `resultDate` (timestamp string)
- Calculate: `start_time = resultDate - elapsedTime`
- Follow existing converter patterns (like `JunitSurefireXmlParseUtil`)

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 02-karate-parser*
*Context gathered: 2026-01-26*
