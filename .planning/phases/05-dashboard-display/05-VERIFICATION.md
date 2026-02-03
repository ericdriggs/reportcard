---
phase: 05-dashboard-display
verified: 2026-02-03T13:30:00Z
status: passed
score: 3/3 must-haves verified
---

# Phase 5: Dashboard Display Verification Report

**Phase Goal:** Pipelines dashboard shows job duration calculated from test_result timing
**Verified:** 2026-02-03T13:30:00Z
**Status:** passed
**Re-verification:** No — initial verification

## Goal Achievement

### Observable Truths

| # | Truth | Status | Evidence |
|---|-------|--------|----------|
| 1 | Pipelines dashboard displays "Avg Run Duration" calculated from test_result timing | ✓ VERIFIED | JobDashboardMetrics.avgRunDuration field exists, calculated from TestResultGraph.startTime/endTime via Duration.between; column header present in PipelineDashboardHtmlHelper line 130 |
| 2 | Duration displays in human-readable format (XXh XXm XXs) with sortable transparent padding | ✓ VERIFIED | formatDuration helper method calls NumberStringUtil.fromSecondBigDecimalPadded (line 189); rendering at line 157 |
| 3 | UI gracefully handles NULL timing values (displays "-") | ✓ VERIFIED | formatDuration returns "-" for null input (line 187); field description documents this behavior (line 173) |

**Score:** 3/3 truths verified

### Required Artifacts

| Artifact | Expected | Status | Details |
|----------|----------|--------|---------|
| `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/graph/TestResultGraph.java` | startTime and endTime fields | ✓ VERIFIED | Lines 24-25: `Instant startTime, Instant endTime` in record definition; mapped in asTestResultPojo() lines 45-46 |
| `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java` | Query includes START_TIME, END_TIME | ✓ VERIFIED | Lines 507-508, 524-525: `key("startTime").value(isoDateFormat(TEST_RESULT.START_TIME))` and endTime in both shouldIncludeTestJson branches of getTestResultSelect() |
| `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/pipeline/JobDashboardMetrics.java` | avgRunDuration field and calculation | ✓ VERIFIED | Line 33: `BigDecimal avgRunDuration` field; lines 84-121: calculation logic using Duration.between with NULL handling and multi-stage aggregation; line 137: field set in builder |
| `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardHtmlHelper.java` | formatDuration helper and column rendering | ✓ VERIFIED | Lines 185-190: formatDuration method with NULL check; line 130: table header "Avg Run Duration"; line 157: data cell rendering; lines 172-173: field description |
| `reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/graph/PipelineDashboardTest.java` | Controller tests | ✓ VERIFIED | 257 lines with 3 test methods: testPipelineDashboardWithTiming (lines 59-98), testPipelineDashboardWithoutTiming (lines 104-136), testPipelineDashboardFieldDescription (lines 142-167) |

### Key Link Verification

| From | To | Via | Status | Details |
|------|----|----|--------|---------|
| GraphService | TEST_RESULT.START_TIME, TEST_RESULT.END_TIME | JOOQ query SELECT | ✓ WIRED | Lines 507-508, 524-525 in getTestResultSelect() method include timing columns in JSON object keys; columns present in both shouldIncludeTestJson=true and false branches |
| TestResultGraph | TestResultPojo timing fields | asTestResultPojo() mapping | ✓ WIRED | Lines 45-46: `.startTime(startTime).endTime(endTime)` in builder chain |
| JobDashboardMetrics | TestResultGraph.startTime(), endTime() | Duration calculation | ✓ WIRED | Lines 94-95: `testResult.startTime()` and `testResult.endTime()` accessed in calculation loop; line 109: `Duration.between(minStart, maxEnd)` computes wall clock time |
| PipelineDashboardHtmlHelper | NumberStringUtil.fromSecondBigDecimalPadded | formatDuration method | ✓ WIRED | Line 189: direct call to NumberStringUtil.fromSecondBigDecimalPadded(durationSeconds) |
| PipelineDashboardHtmlHelper | JobDashboardMetrics.getAvgRunDuration() | Table cell rendering | ✓ WIRED | Line 157: `formatDuration(metric.getAvgRunDuration())` called in table data cell generation |

### Requirements Coverage

| Requirement | Status | Supporting Infrastructure |
|-------------|--------|---------------------------|
| DISP-03: Pipelines dashboard with duration | ✓ SATISFIED | All truths verified; column header at line 130; data cell at line 157; field description at lines 172-173 |

### Anti-Patterns Found

**None found.** 

No TODO/FIXME/placeholder comments in modified files. All implementations are substantive:
- TestResultGraph: 54 lines with full record definition and mapping
- JobDashboardMetrics: 150 lines with complete calculation logic (lines 84-121 for avgRunDuration)
- GraphService: Timing columns added to existing comprehensive query structure
- PipelineDashboardHtmlHelper: formatDuration helper (6 lines), table column (2 lines), field description (2 lines)
- PipelineDashboardTest: 257 lines with 3 complete test methods and helper functions

### Human Verification Required

#### 1. Visual Dashboard Inspection

**Test:** 
1. Start reportcard-server locally
2. Upload test data with Karate JSON (contains timing)
3. Navigate to `/company/{company}/org/{org}/pipelines` in browser
4. Observe "Avg Run Duration" column

**Expected:** 
- Column header "Avg Run Duration" visible after "Test Pass %" column
- Duration values display in human-readable format (e.g., "5m 7s", "1h 23m 45s")
- Jobs without timing data show "-" character
- Values appear properly aligned in table
- Field description at bottom of page explains the metric

**Why human:** Visual appearance, table layout, and readability require human judgment. Automated tests verify HTML content but not visual rendering.

#### 2. NULL Timing Display

**Test:**
1. Upload JUnit XML only (no Karate JSON)
2. View dashboard for that job

**Expected:**
- Duration column shows "-" character (not "null", not blank)
- No JavaScript errors in browser console
- Field description mentions "-" display

**Why human:** Browser rendering of NULL state may differ from HTML string inspection.

#### 3. Multi-Stage Run Duration

**Test:**
1. Upload a run with multiple stages (different test_result records)
2. Each stage has different start/end times
3. View dashboard

**Expected:**
- Duration shows wall clock time (earliest start to latest end), not sum of individual stage durations
- Matches calculation: max(endTime) - min(startTime) across all stages

**Why human:** Verifying correct wall clock aggregation across stages requires understanding of test data timing and comparing to displayed value.

### Gaps Summary

**None.** All must-haves verified. Phase goal achieved.

---

_Verified: 2026-02-03T13:30:00Z_
_Verifier: Claude (gsd-verifier)_
