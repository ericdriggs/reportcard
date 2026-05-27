package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ericdriggs.reportcard.controller.graph.trend.*;
import io.github.ericdriggs.reportcard.model.trend.TestPackageSuiteCase;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestTrendResponse {

    private Map<Long, RunHeaderEntry> runs;
    private List<TestCaseTrendEntry> testCases;
    private TestTrendSummary summary;

    @Value
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RunHeaderEntry {
        Long runId;
        String runUri;
        Instant runDate;
        Integer totalTests;
        BigDecimal successPercent;
    }

    @Value
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TestCaseTrendEntry {
        String testPackageName;
        String testSuiteName;
        String testCaseName;
        BigDecimal successPercent;
        BigDecimal averageDurationSeconds;
        Instant failSince;
        Map<String, List<Long>> runStates;
        Map<String, List<Long>> failureMessages;
    }

    @Value
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TestTrendSummary {
        Integer totalTests;
        Integer successCount;
        Integer failCount;
    }

    public static BigDecimal calculateSuccessPercent(int passed, int total) {
        if (total == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(passed * 100).divide(new BigDecimal(total), 1, RoundingMode.HALF_UP);
    }

    public static BigDecimal ratioToPercent(BigDecimal ratio) {
        if (ratio == null) {
            return null;
        }
        return ratio.multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
    }

    static TestCaseTrendEntry buildTestCaseEntry(
            TestCaseTrendRow row,
            Map<Long, Long> runIdToJobRunCount,
            Map<Long, Integer> runTotalTests,
            Map<Long, Integer> runPassCount) {

        TestPackageSuiteCase tpsc = row.getTestPackageSuiteCase();

        Map<String, List<Long>> runStates = new LinkedHashMap<>();
        Map<String, List<Long>> failureMessages = new LinkedHashMap<>();

        TreeMap<Long, TestCaseRunGroupedState> testRunGroupedStates = row.getTestRunGroupedStates();
        if (testRunGroupedStates == null || testRunGroupedStates.isEmpty()) {
            return TestCaseTrendEntry.builder()
                    .testPackageName(tpsc != null ? tpsc.getTestPackageName() : null)
                    .testSuiteName(tpsc != null ? tpsc.getTestSuiteName() : null)
                    .testCaseName(tpsc != null ? tpsc.getTestCaseName() : null)
                    .successPercent(ratioToPercent(row.getSuccessPercent()))
                    .averageDurationSeconds(row.getAverageDurationSeconds())
                    .failSince(row.getFailSince())
                    .build();
        }

        FailureMessageIndexMap failureMessageIndexMap = row.getFailureMessageIndexMap();
        TreeMap<Integer, String> indexToMessage = (failureMessageIndexMap != null)
                ? failureMessageIndexMap.getIndexFailureMessageMap()
                : new TreeMap<>();

        for (Map.Entry<Long, TestCaseRunGroupedState> entry : testRunGroupedStates.entrySet()) {
            Long runId = entry.getKey();
            TestCaseRunGroupedState state = entry.getValue();
            if (state == null || state.getTestCaseRunState() == null) {
                continue;
            }

            Long jobRunCount = runIdToJobRunCount.get(runId);
            if (jobRunCount == null) {
                continue;
            }

            runTotalTests.merge(runId, 1, Integer::sum);
            if (state.getTestCaseRunState() == TestCaseRunState.SUCCESS) {
                runPassCount.merge(runId, 1, Integer::sum);
            }

            String stateName = state.getTestCaseRunState().name();
            runStates.computeIfAbsent(stateName, k -> new ArrayList<>()).add(jobRunCount);

            if (state.getTestCaseRunState() == TestCaseRunState.FAIL && state.getTestCaseRunStateGroup() != null) {
                String message = indexToMessage.get(state.getTestCaseRunStateGroup());
                if (message != null) {
                    failureMessages.computeIfAbsent(message, k -> new ArrayList<>()).add(jobRunCount);
                }
            }
        }

        runStates.values().removeIf(List::isEmpty);

        return TestCaseTrendEntry.builder()
                .testPackageName(tpsc != null ? tpsc.getTestPackageName() : null)
                .testSuiteName(tpsc != null ? tpsc.getTestSuiteName() : null)
                .testCaseName(tpsc != null ? tpsc.getTestCaseName() : null)
                .successPercent(ratioToPercent(row.getSuccessPercent()))
                .averageDurationSeconds(row.getAverageDurationSeconds())
                .failSince(row.getFailSince())
                .runStates(runStates.isEmpty() ? null : runStates)
                .failureMessages(failureMessages.isEmpty() ? null : failureMessages)
                .build();
    }

    public static TestTrendResponse fromTestTrendTable(TestTrendTable testTrendTable) {
        if (testTrendTable == null) {
            return emptyResponse();
        }

        TreeSet<TestRunHeader> testRunHeaders = testTrendTable.getTestRunHeaders();
        if (testRunHeaders == null || testRunHeaders.isEmpty()) {
            return emptyResponse();
        }

        Map<Long, Long> runIdToJobRunCount = new LinkedHashMap<>();
        Map<Long, TestRunHeader> runIdToHeader = new LinkedHashMap<>();
        for (TestRunHeader header : testRunHeaders) {
            runIdToJobRunCount.put(header.getRunId(), header.getJobRunCount());
            runIdToHeader.put(header.getRunId(), header);
        }

        Map<Long, Integer> runTotalTests = new LinkedHashMap<>();
        Map<Long, Integer> runPassCount = new LinkedHashMap<>();
        for (Long runId : runIdToJobRunCount.keySet()) {
            runTotalTests.put(runId, 0);
            runPassCount.put(runId, 0);
        }

        List<TestCaseTrendEntry> testCases = new ArrayList<>();
        List<TestCaseTrendRow> rows = testTrendTable.getTestCaseTrendRows();
        if (rows != null) {
            for (TestCaseTrendRow row : rows) {
                if (row == null) {
                    continue;
                }
                testCases.add(buildTestCaseEntry(row, runIdToJobRunCount, runTotalTests, runPassCount));
            }
        }

        Map<Long, RunHeaderEntry> runs = new LinkedHashMap<>();
        for (Map.Entry<Long, TestRunHeader> entry : runIdToHeader.entrySet()) {
            Long runId = entry.getKey();
            TestRunHeader header = entry.getValue();
            int total = runTotalTests.getOrDefault(runId, 0);
            int passed = runPassCount.getOrDefault(runId, 0);

            runs.put(header.getJobRunCount(), RunHeaderEntry.builder()
                    .runId(header.getRunId())
                    .runUri(header.getRunUri())
                    .runDate(header.getRunDate())
                    .totalTests(total)
                    .successPercent(calculateSuccessPercent(passed, total))
                    .build());
        }

        TestRowSummary rowSummary = TestRowSummary.fromTestTrendTable(testTrendTable);
        TestTrendSummary summary = TestTrendSummary.builder()
                .totalTests(rowSummary.getTests())
                .successCount(rowSummary.getSuccess())
                .failCount(rowSummary.getFail())
                .build();

        return TestTrendResponse.builder()
                .runs(runs)
                .testCases(testCases)
                .summary(summary)
                .build();
    }

    public TestTrendResponse withFilters(TrendDetail detail, boolean onlyShowFailures) {
        TestTrendResponse result = this;
        if (onlyShowFailures && result.testCases != null) {
            List<TestCaseTrendEntry> filtered = new ArrayList<>();
            for (TestCaseTrendEntry entry : result.testCases) {
                if (entry.getSuccessPercent() == null || entry.getSuccessPercent().compareTo(new BigDecimal("100.0")) < 0) {
                    filtered.add(entry);
                }
            }
            result = TestTrendResponse.builder()
                    .runs(result.runs)
                    .testCases(filtered)
                    .summary(result.summary)
                    .build();
        }
        if (detail == TrendDetail.summary && result.testCases != null) {
            List<TestCaseTrendEntry> summarized = new ArrayList<>();
            for (TestCaseTrendEntry entry : result.testCases) {
                summarized.add(TestCaseTrendEntry.builder()
                        .testPackageName(entry.getTestPackageName())
                        .testSuiteName(entry.getTestSuiteName())
                        .testCaseName(entry.getTestCaseName())
                        .successPercent(entry.getSuccessPercent())
                        .averageDurationSeconds(entry.getAverageDurationSeconds())
                        .failSince(entry.getFailSince())
                        .build());
            }
            result = TestTrendResponse.builder()
                    .runs(result.runs)
                    .testCases(summarized)
                    .summary(result.summary)
                    .build();
        }
        return result;
    }

    private static TestTrendResponse emptyResponse() {
        return TestTrendResponse.builder()
                .runs(Map.of())
                .testCases(List.of())
                .summary(TestTrendSummary.builder()
                        .totalTests(0).successCount(0).failCount(0).build())
                .build();
    }
}
