package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
}