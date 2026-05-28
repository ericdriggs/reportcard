package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GraphJsonController.class)
public class GraphJsonControllerFallbackTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GraphService graphService;

    @MockBean
    private BrowseService browseService;

    @Test
    void fallbackInvokedWhenPrimaryThrows() throws Exception {
        when(graphService.getJobStageTestTrend(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenThrow(new RuntimeException("simulated aggregation failure"));

        JobStageTestTrend stubTrend = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .build();
        when(graphService.getJobStageTestTrendWithFallback(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenReturn(stubTrend);

        mockMvc.perform(get("/v1/api/company/c/org/o/repo/r/branch/b/job/1/stage/s/trend")
                .param("runs", "30"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Trend-Source", "fallback"));

        verify(graphService).getJobStageTestTrendWithFallback(
                eq("c"), eq("o"), eq("r"), eq("b"),
                eq(1L), eq("s"), isNull(), isNull(), eq(30));
    }
}
