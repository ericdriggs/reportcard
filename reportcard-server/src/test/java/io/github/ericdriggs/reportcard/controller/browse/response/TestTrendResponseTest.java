package io.github.ericdriggs.reportcard.controller.browse.response;

import io.github.ericdriggs.reportcard.controller.graph.trend.*;
import io.github.ericdriggs.reportcard.model.trend.TestPackageSuiteCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTrendResponseTest {

    @Test
    void fromTestTrendTable_sparseMatrix() {
        // Build test run headers (3 runs, descending) with jobRunCount as the build number
        TreeSet<TestRunHeader> headers = new TreeSet<>();
        headers.add(TestRunHeader.builder().runId(300L).jobRunCount(160L).runUri("/run/300").runDate(Instant.parse("2025-05-03T00:00:00Z")).build());
        headers.add(TestRunHeader.builder().runId(200L).jobRunCount(159L).runUri("/run/200").runDate(Instant.parse("2025-05-02T00:00:00Z")).build());
        headers.add(TestRunHeader.builder().runId(100L).jobRunCount(158L).runUri("/run/100").runDate(Instant.parse("2025-05-01T00:00:00Z")).build());

        // Build one test case row: SUCCESS in run 160, FAIL in run 159, SUCCESS in run 158
        FailureMessageIndexMap failMap = FailureMessageIndexMap.builder().build();
        TreeMap<Long, TestCaseRunGroupedState> states = new TreeMap<>(Collections.reverseOrder());
        states.put(300L, TestCaseRunGroupedState.builder().runId(300L).testCaseRunState(TestCaseRunState.SUCCESS).build());
        states.put(200L, TestCaseRunGroupedState.builder().runId(200L).testCaseRunState(TestCaseRunState.FAIL).testCaseRunStateGroup(failMap.getFailureIndex("NullPointer")).build());
        states.put(100L, TestCaseRunGroupedState.builder().runId(100L).testCaseRunState(TestCaseRunState.SUCCESS).build());

        TestCaseTrendRow row = TestCaseTrendRow.builder()
                .testPackageSuiteCase(TestPackageSuiteCase.builder().testPackageName("com.example").testSuiteName("MySuite").testCaseName("testFoo").build())
                .successPercent(new BigDecimal("0.67"))
                .averageDurationSeconds(new BigDecimal("2"))
                .hasSkip(false)
                .failSince(Instant.parse("2025-05-02T00:00:00Z"))
                .failureMessageIndexMap(failMap)
                .testRunGroupedStates(states)
                .build();

        TestTrendTable table = TestTrendTable.builder()
                .testRunHeaders(headers)
                .testCaseTrendRows(List.of(row))
                .build();

        // Convert
        TestTrendResponse response = TestTrendResponse.fromTestTrendTable(table);

        // Verify runs is a map keyed by jobRunCount (build number)
        assertNotNull(response.getRuns());
        assertEquals(3, response.getRuns().size());
        assertTrue(response.getRuns().containsKey(160L));
        assertTrue(response.getRuns().containsKey(159L));
        assertTrue(response.getRuns().containsKey(158L));

        // Verify run entry fields
        TestTrendResponse.RunHeaderEntry run160 = response.getRuns().get(160L);
        assertEquals(300L, run160.getRunId());
        assertEquals("/run/300", run160.getRunUri());
        assertEquals("2025-05-03T00:00:00Z", run160.getRunDate());
        assertEquals(1, run160.getTotalTests());
        assertEquals(new BigDecimal("100.0"), run160.getSuccessPercent());

        TestTrendResponse.RunHeaderEntry run159 = response.getRuns().get(159L);
        assertEquals(200L, run159.getRunId());
        assertEquals(1, run159.getTotalTests());
        assertEquals(new BigDecimal("0.0"), run159.getSuccessPercent());

        // Verify test cases
        assertNotNull(response.getTestCases());
        assertEquals(1, response.getTestCases().size());

        TestTrendResponse.TestCaseTrendEntry entry = response.getTestCases().get(0);
        assertEquals("com.example", entry.getTestPackageName());
        assertEquals("MySuite", entry.getTestSuiteName());
        assertEquals("testFoo", entry.getTestCaseName());
        assertEquals(new BigDecimal("67.0"), entry.getSuccessPercent());

        // Verify sparse runStates uses build numbers (jobRunCount)
        Map<String, List<Long>> runStates = entry.getRunStates();
        assertNotNull(runStates);
        assertEquals(List.of(160L, 158L), runStates.get("SUCCESS")); // build 160 and 158
        assertEquals(List.of(159L), runStates.get("FAIL"));           // build 159
        assertNull(runStates.get("SKIPPED"));                          // omitted when empty

        // Verify failureMessages uses build numbers
        Map<String, List<Long>> failMessages = entry.getFailureMessages();
        assertNotNull(failMessages);
        assertEquals(List.of(159L), failMessages.get("NullPointer")); // build 159 had this failure

        // Verify summary
        assertNotNull(response.getSummary());
        assertEquals(1, response.getSummary().getTotalTests());
        assertEquals(0, response.getSummary().getSuccessCount());
        assertEquals(1, response.getSummary().getFailCount());
    }

    @Test
    void calculateSuccessPercent_zeroDivision() {
        assertEquals(BigDecimal.ZERO, TestTrendResponse.calculateSuccessPercent(0, 0));
    }

    @Test
    void calculateSuccessPercent_allPass() {
        assertEquals(new BigDecimal("100.0"), TestTrendResponse.calculateSuccessPercent(10, 10));
    }

    @Test
    void calculateSuccessPercent_partial() {
        assertEquals(new BigDecimal("66.7"), TestTrendResponse.calculateSuccessPercent(2, 3));
    }

    @Test
    void calculateSuccessPercent_noneFail() {
        assertEquals(new BigDecimal("0.0"), TestTrendResponse.calculateSuccessPercent(0, 5));
    }

    @Test
    void fromTestTrendTable_nullInput() {
        TestTrendResponse response = TestTrendResponse.fromTestTrendTable(null);
        assertNotNull(response);
        assertTrue(response.getRuns().isEmpty());
        assertTrue(response.getTestCases().isEmpty());
        assertEquals(0, response.getSummary().getTotalTests());
    }

    @Test
    void fromTestTrendTable_emptyHeaders() {
        TestTrendTable table = TestTrendTable.builder()
                .testRunHeaders(new TreeSet<>())
                .testCaseTrendRows(List.of())
                .build();

        TestTrendResponse response = TestTrendResponse.fromTestTrendTable(table);
        assertNotNull(response);
        assertTrue(response.getRuns().isEmpty());
        assertTrue(response.getTestCases().isEmpty());
    }

    @Test
    void fromTestTrendTable_nullRunStatesInRow() {
        TreeSet<TestRunHeader> headers = new TreeSet<>();
        headers.add(TestRunHeader.builder().runId(1L).jobRunCount(10L).runUri("/run/1").runDate(Instant.parse("2025-05-01T00:00:00Z")).build());

        TestCaseTrendRow row = TestCaseTrendRow.builder()
                .testPackageSuiteCase(TestPackageSuiteCase.builder().testPackageName("pkg").testSuiteName("suite").testCaseName("test").build())
                .successPercent(BigDecimal.ZERO)
                .averageDurationSeconds(BigDecimal.ONE)
                .hasSkip(false)
                .failureMessageIndexMap(FailureMessageIndexMap.builder().build())
                .testRunGroupedStates(null)
                .build();

        TestTrendTable table = TestTrendTable.builder()
                .testRunHeaders(headers)
                .testCaseTrendRows(List.of(row))
                .build();

        TestTrendResponse response = TestTrendResponse.fromTestTrendTable(table);
        assertNotNull(response);
        assertEquals(1, response.getTestCases().size());
        assertNull(response.getTestCases().get(0).getRunStates());
    }

    @Test
    void withFilters_summaryStripsRunStatesAndFailureMessages() {
        TestTrendResponse response = buildResponseWithMixedTests();
        TestTrendResponse summary = response.withFilters(TrendDetail.summary, false);

        assertEquals(2, summary.getTestCases().size());
        for (TestTrendResponse.TestCaseTrendEntry entry : summary.getTestCases()) {
            assertNull(entry.getRunStates());
            assertNull(entry.getFailureMessages());
            assertNotNull(entry.getTestCaseName());
            assertNotNull(entry.getSuccessPercent());
        }
        assertNotNull(summary.getRuns());
        assertNotNull(summary.getSummary());
    }

    @Test
    void withFilters_onlyShowFailuresFiltersPassingTests() {
        TestTrendResponse response = buildResponseWithMixedTests();
        TestTrendResponse filtered = response.withFilters(TrendDetail.full, true);

        assertEquals(1, filtered.getTestCases().size());
        assertEquals("testFailing", filtered.getTestCases().get(0).getTestCaseName());
    }

    @Test
    void withFilters_summaryAndOnlyShowFailuresCombined() {
        TestTrendResponse response = buildResponseWithMixedTests();
        TestTrendResponse result = response.withFilters(TrendDetail.summary, true);

        assertEquals(1, result.getTestCases().size());
        assertEquals("testFailing", result.getTestCases().get(0).getTestCaseName());
        assertNull(result.getTestCases().get(0).getRunStates());
        assertNull(result.getTestCases().get(0).getFailureMessages());
    }

    private TestTrendResponse buildResponseWithMixedTests() {
        Map<String, List<Long>> runStates = new LinkedHashMap<>();
        runStates.put("SUCCESS", List.of(160L, 159L));

        TestTrendResponse.TestCaseTrendEntry passing = TestTrendResponse.TestCaseTrendEntry.builder()
                .testPackageName("com.example")
                .testSuiteName("Suite")
                .testCaseName("testPassing")
                .successPercent(new BigDecimal("100.0"))
                .runStates(runStates)
                .build();

        Map<String, List<Long>> failRunStates = new LinkedHashMap<>();
        failRunStates.put("FAIL", List.of(160L));
        failRunStates.put("SUCCESS", List.of(159L));
        Map<String, List<Long>> failMessages = new LinkedHashMap<>();
        failMessages.put("NullPointer", List.of(160L));

        TestTrendResponse.TestCaseTrendEntry failing = TestTrendResponse.TestCaseTrendEntry.builder()
                .testPackageName("com.example")
                .testSuiteName("Suite")
                .testCaseName("testFailing")
                .successPercent(new BigDecimal("50.0"))
                .runStates(failRunStates)
                .failureMessages(failMessages)
                .build();

        return TestTrendResponse.builder()
                .runs(Map.of(160L, TestTrendResponse.RunHeaderEntry.builder().runId(1L).build()))
                .testCases(List.of(passing, failing))
                .summary(TestTrendResponse.TestTrendSummary.builder().totalTests(2).successCount(1).failCount(1).build())
                .build();
    }
}
