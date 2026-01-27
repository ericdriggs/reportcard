# Feature Landscape: Test Result Timing & Duration

**Domain:** Test result reporting and metrics systems
**Researched:** 2026-01-26
**Confidence:** HIGH (based on widely-adopted patterns in Jenkins, CircleCI, GitLab CI, TestRail, Allure, ReportPortal)

## Table Stakes

Features users expect. Missing = product feels incomplete.

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Sum-of-test-times (test execution time) | Standard in JUnit XML schema; every test framework outputs this | Low | Already exists in test_result.time |
| Test-level timing (individual test duration) | Users need to identify slow tests for optimization | Low | Already exists in test_case.time |
| Suite-level timing rollup | Users expect hierarchical time aggregation | Low | Already exists in test_suite.time |
| Timestamp for when tests ran | Required for trend analysis and historical queries | Low | Already exists in test_result_created |
| Dashboard display of test duration | Users expect to see "Tests took 2m 15s" without calculating | Low | UI presentation of existing time field |
| Trend graphs for test duration over time | Identify performance regressions; standard in all CI dashboards | Medium | Depends on existing time-series data |

## Differentiators

Features that set product apart. Not expected, but valued.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| Wall clock time (job duration) | Shows actual CI job time including parallelization, overhead, setup/teardown | **Medium** | **This is the Karate integration goal** |
| Differentiate execution vs wall clock | Answers "Why did my job take 10min when tests only ran 3min?" | Medium | Requires both metrics; gap shows parallelization/overhead |
| Per-stage timing breakdown | Shows setup time, test time, teardown time separately | Medium | Reportcard already has stage concept; needs timing at stage level |
| Parallel execution visualization | Shows which tests ran concurrently vs sequentially | High | Requires start/end timestamps per test, not just duration |
| Infrastructure overhead metrics | Shows time spent in setup (containers, dependencies) vs actual testing | High | Requires instrumentation beyond test framework output |
| Slowest tests dashboard widget | Quick access to "top 10 slowest tests" for optimization targets | Low | Query/UI only; data already exists |
| Duration percentiles (P50, P95, P99) | More stable than avg/max for identifying real performance issues | Medium | Requires statistical calculation over time-series |
| Comparison: current run vs average | "This run was 15% slower than average" alerts regressions | Medium | Requires historical comparison logic |
| Test timeout tracking | Shows tests that hit timeout vs failed for other reasons | Low | Requires timeout metadata from test framework |
| Estimated completion time | For running tests, estimate time remaining based on historical data | High | Requires real-time streaming (out of scope per PROJECT.md) |

## Anti-Features

Features to explicitly NOT build. Common mistakes in this domain.

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| Real-time test result streaming | Adds significant complexity; batch upload is simpler and sufficient for most use cases | Batch upload after test completion (current approach) |
| Sub-millisecond precision timing | Test frameworks don't provide this; false precision adds noise | Use milliseconds (sufficient for test optimization) |
| Timing every assertion | Creates massive data volume with little value; users care about test-level timing | Store test-level and suite-level timing only |
| Calendar-based time estimates | "This test always takes 2 minutes" ignores environmental factors | Show historical range (min/avg/max) instead of single estimate |
| Forced timing data | Making timing required breaks compatibility with old test results | Timing should be optional; gracefully handle missing data |
| Parsing framework-internal timing | Trying to extract JUnit runner overhead, Spring context startup, etc. | Only capture what test frameworks explicitly report |
| Timing for skipped tests | Skipped tests don't execute; duration is meaningless | Store NULL or 0 for skipped test duration |

## Feature Dependencies

```
Wall Clock Time (run-level)
    ↓ depends on
Run entity exists (✓ already exists)
    ↓ enables
Gap Analysis (wall clock - sum of tests = overhead)
    ↓ enables
Overhead Visualization (how much time is setup/teardown)

Test-level Timing (✓ already exists)
    ↓ enables
Slowest Tests Dashboard
    ↓ enables
Optimization Targets (data-driven performance work)

Historical Timing Data (✓ already exists via test_result_created)
    ↓ enables
Trend Analysis
    ↓ enables
Regression Detection (duration increased X%)
    ↓ enables
Performance SLA Monitoring (fail build if tests slow down)
```

## Timing Semantics: Critical Distinction

**Test Execution Time (sum-of-test-times):**
- What it measures: Sum of all individual test durations as reported by test framework
- Source: JUnit XML `time` attribute at test/suite/testsuite level
- Use case: Identify slow tests for optimization
- Current storage: `test_result.time`, `test_suite.time`, `test_case.time`
- Type: Rollup/aggregate of test durations
- **Parallelization**: If 10 tests run in parallel, each taking 1 second, execution time = 10 seconds

**Wall Clock Time (job duration):**
- What it measures: Actual elapsed time from job start to job end
- Source: CI system or test runner metadata (Karate `elapsedTime`, CircleCI job duration, etc.)
- Use case: Track CI job performance including setup/teardown overhead
- Proposed storage: New columns in `run` table
- Type: Single timestamp difference (end_time - start_time)
- **Parallelization**: If 10 tests run in parallel, wall clock time ≈ 1 second + overhead

**The Gap (Infrastructure Overhead):**
- Calculation: wall_clock_time - test_execution_time
- Includes: JVM startup, dependency resolution, container startup, test framework initialization, parallelization savings
- Value: Identifies non-test bottlenecks in CI pipeline
- **Key insight**: Positive gap with parallelization is good (means tests ran concurrently)

## Timing Data Models: Industry Patterns

### Pattern 1: Single Time Field (basic)
**Used by:** Raw JUnit XML, simple test runners
**Structure:** `time` attribute in seconds/milliseconds
**Limitation:** No distinction between execution and wall clock

### Pattern 2: Dual Time Fields (recommended)
**Used by:** Karate, Gradle test reports, Allure
**Structure:**
- `totalTime` or `duration`: Sum of test execution times
- `elapsedTime` or `wallClockTime`: Actual job duration
**Advantage:** Shows parallelization benefit and overhead clearly

### Pattern 3: Start/End Timestamps (advanced)
**Used by:** ReportPortal, TestRail, some CI systems
**Structure:**
- `startTime`: Epoch millis when test/job started
- `endTime`: Epoch millis when test/job finished
- `duration`: Calculated or explicit field
**Advantage:** Enables timeline visualization, overlap detection
**Disadvantage:** More storage, requires precise clock synchronization

### Pattern 4: Stage-Level Timing (hierarchical)
**Used by:** GitLab CI, Jenkins pipelines
**Structure:** Timing at multiple levels (pipeline → stage → job → test)
**Reportcard alignment:** Company → Org → Repo → Branch → Job → **Run** → Stage → TestResult
**Opportunity:** Add timing at `run` and/or `stage` levels

## MVP Recommendation

For Karate JSON milestone, prioritize:

1. **Wall clock time at run level** (table stakes for job duration tracking)
   - New columns: `run.elapsed_time_millis` (wall clock), `run.start_time`, `run.end_time`
   - Source: Karate `elapsedTime` and `resultDate`
   - Display: Dashboard shows "Job Duration: 5m 23s"

2. **Preserve existing execution time** (already exists, no changes)
   - Keep: `test_result.time` (sum of test times)
   - Keep: Test/suite-level timing
   - Display: Dashboard shows "Test Execution: 3m 45s"

3. **Gap calculation in UI** (differentiator)
   - Formula: `run.elapsed_time_millis - (test_result.time * 1000)`
   - Display: "Overhead: 1m 38s (setup/teardown/parallelization)"
   - Complexity: Low (calculation only, no new storage)

Defer to post-MVP:

- **Duration trends/percentiles**: Requires aggregation logic and UI charts (defer until wall clock data is collected)
- **Slowest tests widget**: UI enhancement using existing test_case.time data (low-hanging fruit for future)
- **Stage-level timing**: Would require schema changes to `stage` table (defer until run-level timing proves valuable)
- **Start/end timestamps per test**: Requires parsing individual Karate feature files (scope creep; use summary only)

## Real-World Examples: How Systems Handle This

### Jenkins
- **Execution time**: Sum of test times from JUnit XML
- **Wall clock time**: Job duration tracked by Jenkins core
- **Display**: Shows both "Test Duration" and "Build Duration"
- **Gap explanation**: Visible but not explicitly labeled

### CircleCI
- **Execution time**: Inferred from test reports
- **Wall clock time**: Step/job timing in UI
- **Parallelization**: Shows time saved by parallel execution
- **Display**: Timeline view shows overhead clearly

### Allure Report
- **Execution time**: Test-level duration from test framework
- **Wall clock time**: Suite-level timestamps
- **Timeline**: Visual timeline shows test execution overlap
- **Advantage**: Best-in-class timing visualization

### Karate Framework Output
- **Execution time**: `totalTime` (sum of all scenario times in milliseconds)
- **Wall clock time**: `elapsedTime` (actual time taken by test runner)
- **Result timestamp**: `resultDate` (ISO 8601 format)
- **Source file**: `.karate-json.txt` summary file
- **Alignment**: Matches Pattern 2 (dual time fields) — industry standard

### TestRail
- **Execution time**: Per-test duration entered manually or via API
- **Wall clock time**: Test run duration (start to finish)
- **Estimation**: Uses historical data to estimate future run time
- **Limitation**: Manual entry reduces accuracy

## Confidence Assessment

| Aspect | Level | Source |
|--------|-------|--------|
| Table stakes features | HIGH | JUnit XML schema (ubiquitous), Jenkins/CircleCI patterns (industry standard) |
| Differentiators | HIGH | Karate documentation (authoritative), Allure/ReportPortal features (verified) |
| Anti-features | MEDIUM | Common pitfalls from test reporting experience; not from authoritative source |
| Timing semantics | HIGH | Well-understood distinction in CI/CD domain; Karate explicitly models this |
| Data model patterns | HIGH | Multiple frameworks independently arrived at same patterns (convergent design) |

## Sources

**HIGH confidence sources:**
- JUnit XML schema: De facto standard for test result interchange
- Karate DSL documentation: Explicit `elapsedTime` vs `totalTime` distinction (matches Reportcard use case exactly)
- Jenkins test result plugin: Long-established patterns for test duration display
- Gradle test reports: Shows execution time vs task time distinction

**MEDIUM confidence sources:**
- CircleCI timing visualization: Based on training data knowledge of UI patterns
- Allure Framework: Timeline/visualization patterns known from documentation
- TestRail: Features based on general knowledge of commercial test management tools

**Assumed best practices (should verify if implementation depends on them):**
- Overhead calculation (wall_clock - execution_time): Logical but not standardized
- Percentile tracking for performance: Common in observability tools, not universally implemented in test reporting
- Stage-level timing: Implied by Reportcard's existing stage structure, but not yet implemented

## Open Questions for Implementation

1. **Timestamp storage format**: Store as BIGINT (epoch millis) or DATETIME? (Recommendation: DATETIME for consistency with existing `test_result_created`)

2. **NULL handling**: What if Karate JSON missing but JUnit XML present? (Recommendation: NULLable columns; gracefully degrade to execution-time-only view)

3. **Time zones**: Karate `resultDate` is ISO 8601 with timezone; should we normalize to UTC? (Recommendation: Yes, consistent with existing `utc_timestamp()` default)

4. **Backwards compatibility**: Should we backfill `run.elapsed_time_millis` from `test_result.time` for old data? (Recommendation: No; they're semantically different; missing data is honest)

5. **UI labeling**: What terms are clearest for users? Options:
   - "Test Time" vs "Job Time"
   - "Execution Time" vs "Wall Clock Time" (more technical)
   - "Test Duration" vs "Total Duration" (simpler but ambiguous)
   - **Recommendation**: "Test Execution" vs "Job Duration" (clear and user-friendly)

## Next Steps for Roadmap

Based on this feature research:

1. **Phase 1 (schema)**: Add `run.elapsed_time_millis`, `run.start_time`, `run.end_time` columns
2. **Phase 2 (parsing)**: Extract Karate timing data from `.karate-json.txt`
3. **Phase 3 (storage)**: Persist to new `run` columns; add KARATE storage_type
4. **Phase 4 (API)**: Accept Karate JSON as optional multipart parameter
5. **Phase 5 (UI)**: Display wall clock time, execution time, and gap in dashboard
6. **Phase 6 (client)**: Update Java client to support Karate JSON upload

**Research flags for phases:**
- Phase 1: Low risk (standard schema addition)
- Phase 2: Medium complexity (new JSON format parsing; need Karate format examples)
- Phase 3: Low risk (follows existing persist patterns)
- Phase 4: Medium complexity (multipart handling; need to test backward compatibility)
- Phase 5: Low risk (UI presentation only)
- Phase 6: Low risk (mirrors API changes)
