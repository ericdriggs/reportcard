package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.pipeline.PipelineDashboardMetrics;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineDashboardHtmlHelperTest {

    @Test
    void testRenderPipelineDashboard() {
        // Given
        List<PipelineDashboardMetrics> metrics = Arrays.asList(
            PipelineDashboardMetrics.builder()
                .company("hulu")
                .org("SubLife")
                .repo("test-service")
                .branch("main")
                .jobInfo("pipeline:build_acceptance")
                .daysSincePassingRun(5)
                .jobPassPercent(new BigDecimal("85.5"))
                .testPassPercent(new BigDecimal("92.3"))
                .build(),
            PipelineDashboardMetrics.builder()
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
        String html = PipelineDashboardHtmlHelper.renderPipelineDashboard(metrics, "build_acceptance");

        // Then
        assertNotNull(html);
        assertTrue(html.contains("Pipeline Dashboard - build_acceptance"));
        assertTrue(html.contains("hulu"));
        assertTrue(html.contains("SubLife"));
        assertTrue(html.contains("test-service"));
        assertTrue(html.contains("other-service"));
        assertTrue(html.contains("86%"));
        assertTrue(html.contains("92%"));
        assertTrue(html.contains("N/A")); // For null daysSincePassingRun
        assertTrue(html.contains("<table"));
        assertTrue(html.contains("</table>"));
    }
}