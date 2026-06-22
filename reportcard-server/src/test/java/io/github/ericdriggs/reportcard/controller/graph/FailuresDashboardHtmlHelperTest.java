package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.failures.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FailuresDashboardHtmlHelperTest {

    @Test
    void renderHtml_containsExpectedElements() {
        FailuresDashboard dashboard = FailuresDashboard.builder()
                .request(FailuresDashboardRequest.builder()
                        .company("acme")
                        .org("platform")
                        .days(7)
                        .minFailurePercent(50)
                        .build())
                .failingTests(List.of(
                        TestCaseFailureSummary.builder()
                                .packageName("com.acme")
                                .suiteName("FooTest")
                                .caseName("testBar")
                                .successPercent(new BigDecimal("20.00"))
                                .lastPassedAt(Instant.parse("2024-06-01T00:00:00Z"))
                                .totalRuns(10)
                                .successCount(2)
                                .failureCount(8)
                                .repo("my-repo")
                                .branch("main")
                                .jobInfo("pipeline=build")
                                .build()))
                .dailyAggregationsByOrg(Map.of("platform", List.of(
                        DailyTestAggregation.builder()
                                .date(LocalDate.of(2024, 6, 15))
                                .totalTests(100)
                                .passCount(80)
                                .failCount(20)
                                .passPercent(new BigDecimal("80.00"))
                                .build())))
                .generated(Instant.parse("2024-06-15T12:00:00Z"))
                .build();

        String html = FailuresDashboardHtmlHelper.renderHtml(dashboard);

        assertNotNull(html);
        assertTrue(html.contains("acme"), "Should contain company name");
        assertTrue(html.contains("platform"), "Should contain org name");
        assertTrue(html.contains("FooTest"), "Should contain suite name");
        assertTrue(html.contains("testBar"), "Should contain case name");
        assertTrue(html.contains("20.00%"), "Should contain success percent");
        assertTrue(html.contains("initFailuresDashboard()"), "Should have onload");
        assertTrue(html.contains("failures.css"), "Should link CSS");
        assertTrue(html.contains("failures.js"), "Should link JS");
    }

    @Test
    void renderEmptyHtml_containsFormAndHint() {
        String html = FailuresDashboardHtmlHelper.renderEmptyHtml("acme", "platform");

        assertNotNull(html);
        assertTrue(html.contains("acme"), "Should contain company");
        assertTrue(html.contains("failures-form"), "Should contain form");
        assertTrue(html.contains("Enter filters above"), "Should contain hint text");
        assertTrue(html.contains("initFailuresDashboard()"), "Should have onload");
    }

    @Test
    void renderEmptyHtml_nullOrg() {
        String html = FailuresDashboardHtmlHelper.renderEmptyHtml("acme", null);

        assertNotNull(html);
        assertTrue(html.contains("acme"), "Should contain company");
        assertTrue(html.contains("failures-form"), "Should contain form");
    }

    @Test
    void renderFailingTestRows_escapesHtml() {
        List<TestCaseFailureSummary> tests = List.of(
                TestCaseFailureSummary.builder()
                        .packageName("<script>xss</script>")
                        .suiteName("Suite")
                        .caseName("test1")
                        .successPercent(new BigDecimal("0.00"))
                        .totalRuns(5)
                        .successCount(0)
                        .failureCount(5)
                        .repo("repo")
                        .branch("main")
                        .jobInfo("info")
                        .build());

        String html = FailuresDashboardHtmlHelper.renderFailingTestRows(tests);

        assertTrue(html.contains("&lt;script&gt;"), "Should escape HTML in package name");
        assertFalse(html.contains("<script>xss</script>"), "Should NOT contain raw script tag");
    }

    @Test
    void renderFailingTestRows_emptyList() {
        String html = FailuresDashboardHtmlHelper.renderFailingTestRows(List.of());

        assertTrue(html.contains("No failing tests found"), "Should show empty message");
    }

    @Test
    void renderChartSection_withDailyAggregationsByOrg() {
        FailuresDashboard dashboard = FailuresDashboard.builder()
                .request(FailuresDashboardRequest.builder()
                        .company("acme")
                        .days(7)
                        .minFailurePercent(50)
                        .build())
                .failingTests(List.of())
                .dailyAggregationsByOrg(Map.of(
                        "org1", List.of(
                                DailyTestAggregation.builder()
                                        .date(LocalDate.of(2024, 6, 15))
                                        .totalTests(50)
                                        .passCount(40)
                                        .failCount(10)
                                        .passPercent(new BigDecimal("80.00"))
                                        .build())))
                .generated(Instant.now())
                .build();

        String html = FailuresDashboardHtmlHelper.renderChartSection(dashboard);

        assertTrue(html.contains("org1"), "Should contain org name");
        assertTrue(html.contains("chart-container"), "Should have chart container");
        assertTrue(html.contains("data-daily"), "Should have data-daily attribute");
    }

    @Test
    void renderChartSection_withSingleOrgEntry() {
        FailuresDashboard dashboard = FailuresDashboard.builder()
                .request(FailuresDashboardRequest.builder()
                        .company("acme")
                        .org("platform")
                        .days(7)
                        .minFailurePercent(50)
                        .build())
                .failingTests(List.of())
                .dailyAggregationsByOrg(Map.of("platform", List.of(
                        DailyTestAggregation.builder()
                                .date(LocalDate.of(2024, 6, 15))
                                .totalTests(100)
                                .passCount(90)
                                .failCount(10)
                                .passPercent(new BigDecimal("90.00"))
                                .build())))
                .generated(Instant.now())
                .build();

        String html = FailuresDashboardHtmlHelper.renderChartSection(dashboard);

        assertTrue(html.contains("chart-container"), "Should have chart container");
        assertTrue(html.contains("data-daily"), "Should have data-daily attribute");
        assertTrue(html.contains("platform"), "Should contain org name");
    }
}
