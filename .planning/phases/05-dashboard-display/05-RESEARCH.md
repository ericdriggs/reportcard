# Phase 5: Dashboard Display - Research

**Researched:** 2026-01-27
**Domain:** Java server-side HTML generation with timing display
**Confidence:** HIGH

## Summary

This phase adds job/run duration display to existing dashboard views. The application uses Java-based HTML generation (not a frontend framework), building HTML strings in helper classes. After Phase 4.1, timing data exists at the test_result level (start_time, end_time as DATETIME NULL in MySQL, exposed as Instant in JOOQ).

The codebase has established patterns for sortable columns with transparent padding (`NumberStringUtil.fromSecondBigDecimalPadded()`), NULL handling ("-" or opacity:0 "z" for sort order), and percentage formatting with transparent padding for lexical sorting.

**Primary recommendation:** Extend existing JobDashboardMetrics and PipelineDashboardHtmlHelper to calculate average duration from test_result timing and display using existing NumberStringUtil patterns.

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Java | 17 | Server-side language | Project baseline |
| JOOQ | 3.x | Type-safe database access | Project uses generated code from schema |
| Spring Boot | 2.6.15 | Web framework | Project baseline |
| MySQL | 8.0 | Database | Project database |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Lombok | Latest | Builder pattern | Used throughout for @Builder, @Value |
| RecordBuilder | Latest | Immutable records | Used in graph models |

### UI Pattern
**No frontend framework** - Application generates HTML strings server-side in Java helper classes. Pattern:
- Controller receives request → calls service for data
- Helper class builds HTML string with placeholders
- Returns complete HTML page as String

**Installation:**
No new dependencies required - uses existing project stack.

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/
├── controller/graph/           # UI controllers and HTML helpers
│   ├── GraphUIController.java # Endpoints (already has /pipelines)
│   ├── PipelineDashboardHtmlHelper.java  # HTML generation
├── model/pipeline/             # Domain models
│   ├── JobDashboardMetrics.java # Metrics calculation
├── persist/                    # Database queries
│   ├── GraphService.java       # Query orchestration
├── gen/db/                     # JOOQ generated (DO NOT EDIT)
│   ├── tables/TestResultTable.java  # Has START_TIME, END_TIME fields
```

### Pattern 1: Server-Side HTML String Building
**What:** Build HTML as Java StringBuilder with placeholder replacement
**When to use:** All UI rendering in this application
**Example:**
```java
// Source: PipelineDashboardHtmlHelper.java lines 114-172
private static String renderPipelineTable(List<JobDashboardMetrics> metrics) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table class='sortable' id='pipeline-table'>").append(ls);
    sb.append("<thead>").append(ls);
    sb.append("<tr>").append(ls);
    sb.append("<th>Company</th>").append(ls);
    // ... more headers
    sb.append("</thead>").append(ls);

    sb.append("<tbody>").append(ls);
    for (JobDashboardMetrics metric : metrics) {
        sb.append("<td>").append(metric.getCompany()).append("</td>").append(ls);
        sb.append("<td class='percent'>").append(percentFromBigDecimal(metric.getTestPassPercent())).append("</td>").append(ls);
    }
    sb.append("</tbody>").append(ls);
    return sb.toString();
}
```

### Pattern 2: Sortable Column with Transparent Padding
**What:** Embed invisible leading characters for lexical sort in HTML tables
**When to use:** Any numeric column that needs to sort correctly as text
**Example:**
```java
// Source: NumberStringUtil.java lines 116-168
public static String fromSecondBigDecimalPadded(BigDecimal durationSeconds) {
    // ... calculate years, days, hours, minutes, seconds
    StringBuilder sb = new StringBuilder();
    boolean transparent = true;
    sb.append("<span class='transparent'>");

    if (years > 0 ) {
        transparent = false;
        sb.append("</span>");  // End transparency when significant digit appears
    }
    sb.append(paddedTransparent(years,2,"y"));

    // Pattern continues for days, hours, minutes, seconds
    return sb.toString();  // Returns: "<span class='transparent'>00</span>5h 30m"
}
```

**CSS:**
```css
/* Source: metrics.css line 53 */
.transparent {
    display:none;
}
```

Result: "5h 30m" displays but sorts like "005h30m" for correct ordering.

### Pattern 3: NULL Handling for Sortable Columns
**What:** Use "-" for display OR hidden "z" character for sort-to-bottom behavior
**When to use:** Missing/NULL data in sortable columns
**Example:**
```java
// Source: TrendHtmlHelper.java lines 212-218
static String renderFailSince(Instant failSince) {
    // Use a hidden value so when sort failSince in ascending order
    // the empty values will be at the bottom
    if (failSince == null) {
        return "<span style=\"opacity: 0\">z</span>";
    }
    return failSince.truncatedTo(ChronoUnit.SECONDS).toString();
}

// Source: PipelineDashboardHtmlHelper.java lines 141-148
String daysSince;
if (metric.getDaysSincePassingRun() == null) {
    daysSince = "N/A";
} else if (metric.getDaysSincePassingRun() == 0) {
    daysSince = "0 (SUCCESS)";
} else {
    daysSince = metric.getDaysSincePassingRun().toString();
}
```

**Decision:** Use "-" for user-visible "no data" (context says use dash), use opacity:0 "z" only if need sort-to-bottom.

### Pattern 4: Model-Driven Metrics Calculation
**What:** Calculate aggregations in domain model from graph data
**When to use:** Dashboard metrics from database query results
**Example:**
```java
// Source: JobDashboardMetrics.java lines 37-105
public static List<JobDashboardMetrics> fromCompanyGraphs(List<CompanyGraph> companyGraphs, JobDashboardRequest request) {
    List<JobDashboardMetrics> results = new ArrayList<>();

    for (CompanyGraph companyGraph : emptyIfNull(companyGraphs)) {
        for (OrgGraph orgGraph : emptyIfNull(companyGraph.orgs())) {
            for (RepoGraph repoGraph : emptyIfNull(orgGraph.repos())) {
                for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                    for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                        List<RunGraph> runs = emptyIfNull(jobGraph.runs());

                        // Calculate metrics from runs
                        int totalTests = 0;
                        int passingTests = 0;
                        for (RunGraph run : runs) {
                            for (StageGraph stage : emptyIfNull(run.stages())) {
                                for (TestResultGraph testResult : emptyIfNull(stage.testResults())) {
                                    totalTests += testResult.tests();
                                    int passing = testResult.tests() - testResult.error() - testResult.failure() - testResult.skipped();
                                    passingTests += passing;
                                }
                            }
                        }
                        BigDecimal testPassPercent = totalTests > 0 ?
                            BigDecimal.valueOf(passingTests * 100.0 / totalTests) : BigDecimal.ZERO;

                        results.add(JobDashboardMetrics.builder()
                            .testPassPercent(testPassPercent)
                            .build());
                    }
                }
            }
        }
    }
    return results;
}
```

**Key insight:** Metrics calculated in model's static factory method, not in helper or service.

### Anti-Patterns to Avoid
- **Editing JOOQ generated code:** All `gen/db/` files regenerated from schema - changes lost on regeneration
- **Direct SQL in controllers:** Use GraphService for all database queries
- **Frontend framework patterns:** No React/Vue - this is server-side HTML generation

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Duration formatting | Custom string builder for time | `NumberStringUtil.fromSecondBigDecimalPadded()` | Handles all units (years, days, hours, minutes), transparent padding for sorting |
| Percentage display | String.format for percents | `NumberStringUtil.percentFromBigDecimal()` | Transparent padding for sort, consistent formatting |
| NULL duration handling | Custom logic | Dash "-" (per context) | Established pattern, user expectation |
| Sortable tables | Custom JS | sortable.min.js (already included) | Already in basePage template |
| HTML page structure | New template | `BrowseHtmlHelper.getPage()` | Provides header, breadcrumb, CSS links |

**Key insight:** NumberStringUtil already handles duration formatting with transparent padding. Don't reimplement - BigDecimal in seconds → formatted display with sort support.

## Common Pitfalls

### Pitfall 1: Forgetting NULL Timing Exists
**What goes wrong:** Crash on .getStartTime() or .getEndTime() when NULL
**Why it happens:** Phase 4.1 made timing nullable - old data has NULL, new Karate data populates it
**How to avoid:**
- Always check NULL before calculating duration
- Calculate average only from non-NULL durations
- Count non-NULL values separately for denominator
**Warning signs:** NullPointerException in JobDashboardMetrics calculation

### Pitfall 2: Wrong Level for Timing Data
**What goes wrong:** Looking for timing on Run or Stage when it's on TestResult
**Why it happens:** Phase 4.1 moved timing FROM run table TO test_result table
**How to avoid:**
- Access timing via `testResult.getStartTime()` and `testResult.getEndTime()`
- Aggregate UP from test_result level to run/job level
- Duration = test_result.end_time - test_result.start_time (per test_result)
**Warning signs:** Compile error "Run has no method getStartTime()"

### Pitfall 3: Duration Unit Confusion
**What goes wrong:** Display seconds when hours expected, or milliseconds when seconds expected
**Why it happens:** MySQL DATETIME → Java Instant, Duration.between returns Duration, must convert to seconds
**How to avoid:**
```java
// Correct pattern:
if (startTime != null && endTime != null) {
    Duration duration = Duration.between(startTime, endTime);
    BigDecimal seconds = BigDecimal.valueOf(duration.getSeconds())
        .add(BigDecimal.valueOf(duration.getNano()).divide(BigDecimal.valueOf(1_000_000_000)));
    String display = NumberStringUtil.fromSecondBigDecimalPadded(seconds);
}
```
**Warning signs:** "Duration: 3600s" when expecting "1h 00m"

### Pitfall 4: Including Timing in Sortable Column Header Wrong
**What goes wrong:** Column doesn't sort, or sorts alphabetically wrong (10h before 2h)
**Why it happens:** HTML table sort is lexical - "10" < "2" as strings
**How to avoid:** Use fromSecondBigDecimalPadded which embeds transparent padding automatically
**Warning signs:** User clicks sort, 10h appears before 2h 00m

### Pitfall 5: Modifying Graph Models Without Testing Round-Trip
**What goes wrong:** Adding fields to TestResultGraph breaks JSON serialization or database mapping
**Why it happens:** Graph models use @RecordBuilder, serialize to JSON for caching
**How to avoid:**
- Don't modify TestResultGraph for Phase 5 (timing already in POJO)
- Calculate duration in metrics aggregation layer, not graph layer
- Test with existing integration tests
**Warning signs:** Jackson deserialization errors in logs

## Code Examples

Verified patterns from official sources:

### Duration Calculation from test_result Timing
```java
// JOOQ provides these fields (verified in generated code):
// TEST_RESULT.START_TIME (Instant)
// TEST_RESULT.END_TIME (Instant)

// In metrics calculation:
BigDecimal totalDurationSeconds = BigDecimal.ZERO;
int durationCount = 0;

for (RunGraph run : runs) {
    for (StageGraph stage : run.stages()) {
        for (TestResultGraph testResult : stage.testResults()) {
            // testResult doesn't have timing in graph - must access from POJO
            // OR query timing separately in GraphService

            // If startTime and endTime available:
            Instant start = testResult.startTime();  // If added to graph
            Instant end = testResult.endTime();

            if (start != null && end != null) {
                Duration d = Duration.between(start, end);
                BigDecimal seconds = BigDecimal.valueOf(d.getSeconds())
                    .add(BigDecimal.valueOf(d.getNano())
                    .divide(BigDecimal.valueOf(1_000_000_000), 2, RoundingMode.HALF_UP));
                totalDurationSeconds = totalDurationSeconds.add(seconds);
                durationCount++;
            }
        }
    }
}

BigDecimal avgDuration = durationCount > 0
    ? totalDurationSeconds.divide(BigDecimal.valueOf(durationCount), 2, RoundingMode.HALF_UP)
    : null;
```

### Display Formatted Duration with NULL Handling
```java
// Source: Existing pattern from NumberStringUtil and context requirements
String durationDisplay;
if (avgDuration == null) {
    durationDisplay = "-";  // Per DISP-03 requirement
} else {
    durationDisplay = NumberStringUtil.fromSecondBigDecimalPadded(avgDuration);
}

// In HTML:
sb.append("<td>").append(durationDisplay).append("</td>").append(ls);
```

### Add Column to Existing Pipelines Table
```java
// Source: PipelineDashboardHtmlHelper.java pattern
// In renderPipelineTable method:

// 1. Add header (after Test Pass % per context):
sb.append("<th>Test Pass %</th>").append(ls);
sb.append("<th>Avg Run Duration</th>").append(ls);  // NEW

// 2. Add data cell in loop:
sb.append("<td class='percent'>").append(percentFromBigDecimal(metric.getTestPassPercent())).append("</td>").append(ls);
sb.append("<td>").append(formatDuration(metric.getAvgRunDuration())).append("</td>").append(ls);  // NEW

private static String formatDuration(BigDecimal seconds) {
    if (seconds == null) {
        return "-";
    }
    return NumberStringUtil.fromSecondBigDecimalPadded(seconds);
}
```

### TestResultGraph Extension (If Needed)
```java
// Source: Existing TestResultGraph.java structure
// Current record does NOT include timing - would need to add:

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record TestResultGraph(
    Long testResultId,
    Long stageFk,
    Integer tests,
    Integer skipped,
    Integer error,
    Integer failure,
    BigDecimal time,
    Instant testResultCreated,
    String externalLinks,
    Boolean isSuccess,
    Boolean hasSkip,
    Instant startTime,      // ADD if needed for graph
    Instant endTime,        // ADD if needed for graph
    @JsonProperty("testSuitesJson")
    List<TestSuiteGraph> testSuites
) {
    // Builder auto-generated by @RecordBuilder
}
```

**NOTE:** May not need to modify TestResultGraph if GraphService query can join timing fields directly into result.

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Timing on run table | Timing on test_result table | Phase 4.1 (Jan 2026) | Duration now per-stage, not per-run |
| Single execution time | Job duration separate from test execution time | Phase 5 (this phase) | User sees wall clock vs test execution |
| Manual duration formatting | fromSecondBigDecimalPadded() | Already exists | Handles all time units with sort padding |

**Deprecated/outdated:**
- Run.start_time, Run.end_time: Removed in Phase 4.1, now on test_result table
- Simple duration display: Need transparent padding for sorting in HTML tables

## Open Questions

### 1. **Does TestResultGraph need startTime/endTime fields?**
   - What we know: TestResultPojo has startTime/endTime (JOOQ generated), TestResultGraph does NOT currently
   - What's unclear: Does GraphService query include timing in result set, or must we modify TestResultGraph?
   - Recommendation:
     - Check GraphService.getPipelineDashboardCompanyGraphs() query - if SELECT includes start_time/end_time, add fields to TestResultGraph
     - If not included, either add to SELECT or calculate timing in separate query
     - Prefer adding to graph if query already fetches test_result rows

### 2. **Should duration average exclude runs with partial NULL timing?**
   - What we know: test_result.start_time and end_time are both nullable
   - What's unclear: If run has 3 stages, 2 with timing and 1 NULL, include that run's average or skip entire run?
   - Recommendation:
     - Calculate per-test_result, average only non-NULL durations
     - Don't exclude entire runs - user wants "average of available timing"
     - Document in field description that average is "from runs with timing data"

### 3. **Pipelines dashboard shows job-level average - how to aggregate multi-stage runs?**
   - What we know: Job can have multiple stages per run (1:1 test_result:stage)
   - What's unclear: For "Avg Run Duration", average per-run sum, or average all test_result durations?
   - Recommendation:
     - "Run Duration" = SUM of all test_result durations for that run
     - "Avg Run Duration" = AVG of run durations across all runs
     - This matches user expectation: "how long does a complete run take?"

## Sources

### Primary (HIGH confidence)
- Existing codebase - PipelineDashboardHtmlHelper.java (verified structure)
- Existing codebase - JobDashboardMetrics.java (verified calculation pattern)
- Existing codebase - NumberStringUtil.java (verified formatting methods)
- Existing codebase - TrendHtmlHelper.java (verified NULL handling pattern)
- JOOQ generated code - TestResultTable.java (verified START_TIME, END_TIME fields exist)
- Phase 5 CONTEXT.md - User decisions on formatting, labels, NULL handling

### Secondary (MEDIUM confidence)
- Phase 4.1 completion (inferred from CONTEXT.md and JOOQ generated fields)

### Tertiary (LOW confidence)
- None - all findings from direct codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Direct inspection of existing project dependencies
- Architecture: HIGH - Verified patterns from multiple existing helper classes
- Pitfalls: HIGH - Derived from actual code structure and Phase 4.1 schema changes
- Code examples: HIGH - All examples from existing codebase or direct JOOQ fields

**Research date:** 2026-01-27
**Valid until:** 60 days (stable server-side Java patterns, no fast-moving frontend dependencies)
