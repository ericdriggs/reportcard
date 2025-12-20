package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.model.pipeline.PipelineDashboardRequest;
import io.github.ericdriggs.reportcard.model.pipeline.PipelineDashboardMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GraphServicePipelineTest {

    @Mock
    private GraphService graphService;

    @Test
    public void testPipelineDashboardMethodExists() {
        // Test that the pipeline dashboard method exists and can be called
        PipelineDashboardRequest request = PipelineDashboardRequest.builder()
                .company("test")
                .org("test")
                .pipeline("build_acceptance")
                .days(90)
                .build();
        
        // These should not throw compilation errors
        assertNotNull(request);
        
        // Verify method signature exists (compilation test)
        List<PipelineDashboardMetrics> result = graphService.getPipelineDashboard(request);
        
        // Method exists and compiles successfully
    }
}