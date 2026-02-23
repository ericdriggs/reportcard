package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineDashboardHtmlHelperTest {

    @Test
    void testRenderPipelineDashboard() {
        // Given
        List<JobDashboardMetrics> metrics = Arrays.asList(
            JobDashboardMetrics.builder()
                .company("hulu")
                .org("SubLife")
                .repo("test-service")
                .branch("main")
                .jobInfo("pipeline:build_acceptance")
                .daysSincePassingRun(5)
                .jobPassPercent(new BigDecimal("85.5"))
                .testPassPercent(new BigDecimal("92.3"))
                .build(),
            JobDashboardMetrics.builder()
                .company("hulu")
                .org("SubLife")
                .repo("other-service")
                .branch("main")
                .jobInfo("pipeline:build_acceptance")
                .daysSincePassingRun(null) // N/A case
                .jobPassPercent(new BigDecimal("75.0"))
                .testPassPercent(new BigDecimal("88.1"))
                .build()
        );

        // When
        String html = PipelineDashboardHtmlHelper.renderPipelineDashboard(metrics, "build_acceptance", 90);

        // Then
        assertNotNull(html);
        System.out.println("HTML output: " + html);
        assertTrue(html.contains("hulu"));
        assertTrue(html.contains("SubLife"));
        assertTrue(html.contains("test-service"));
        assertTrue(html.contains("other-service"));
    }

    @Test
    void testLastRunColumnRendered() {
        // Given - metric with lastRun set
        Instant lastRun = Instant.parse("2024-01-15T10:30:00Z");
        List<JobDashboardMetrics> metrics = Arrays.asList(
            JobDashboardMetrics.builder()
                .company("hulu")
                .org("SubLife")
                .repo("test-service")
                .branch("main")
                .jobInfo("pipeline:build")
                .daysSincePassingRun(5)
                .jobPassPercent(new BigDecimal("85.5"))
                .testPassPercent(new BigDecimal("92.3"))
                .lastRun(lastRun)
                .lastPassingRun(lastRun)
                .build()
        );

        // When
        String html = PipelineDashboardHtmlHelper.renderPipelineDashboard(metrics, "build", 90);

        // Then
        assertNotNull(html);
        assertTrue(html.contains("<th>Last Run</th>"), "Should have Last Run column header");
        assertTrue(html.contains("title=\"2024-01-15T10:30:00Z\""), "Should have tooltip with ISO timestamp");
    }

    @Test
    void testLastRunNullRendersPlaceholder() {
        // Given - metric with null lastRun
        List<JobDashboardMetrics> metrics = Arrays.asList(
            JobDashboardMetrics.builder()
                .company("hulu")
                .org("SubLife")
                .repo("test-service")
                .branch("main")
                .jobInfo("pipeline:build")
                .daysSincePassingRun(null)
                .jobPassPercent(new BigDecimal("75.0"))
                .testPassPercent(new BigDecimal("88.1"))
                .lastRun(null)
                .lastPassingRun(null)
                .build()
        );

        // When
        String html = PipelineDashboardHtmlHelper.renderPipelineDashboard(metrics, "build", 90);

        // Then
        assertNotNull(html);
        assertTrue(html.contains("—"), "Should contain em-dash placeholder for null lastRun");
        assertTrue(html.contains("No data within range"), "Should have null tooltip text");
    }
}