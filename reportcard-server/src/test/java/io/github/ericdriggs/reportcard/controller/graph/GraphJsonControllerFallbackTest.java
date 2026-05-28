package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GraphJsonControllerFallbackTest {

    @Mock
    private GraphService graphService;

    @Mock
    private BrowseService browseService;

    @InjectMocks
    private GraphJsonController controller;

    @Test
    void returnsOkWhenPrimarySucceeds() {
        JobStageTestTrend stubTrend = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .build();
        when(graphService.getJobStageTestTrend(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenReturn(stubTrend);

        ResponseEntity<JobStageTestTrend> response = controller.getJobStageTestTrend(
                "c", "o", "r", "b", 1L, "s", null, null, 30);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(stubTrend, response.getBody());
        verify(graphService, never()).getJobStageTestTrendWithFallback(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt());
    }

    @Test
    void fallbackInvokedWhenPrimaryThrows() {
        when(graphService.getJobStageTestTrend(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenThrow(new RuntimeException("simulated aggregation failure"));

        JobStageTestTrend fallbackTrend = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .build();
        when(graphService.getJobStageTestTrendWithFallback(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenReturn(fallbackTrend);

        ResponseEntity<JobStageTestTrend> response = controller.getJobStageTestTrend(
                "c", "o", "r", "b", 1L, "s", null, null, 30);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(fallbackTrend, response.getBody());
        verify(graphService).getJobStageTestTrendWithFallback(
                eq("c"), eq("o"), eq("r"), eq("b"),
                eq(1L), eq("s"), isNull(), isNull(), eq(30));
    }

    @Test
    void fallbackCapsRunsAt30() {
        when(graphService.getJobStageTestTrend(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenThrow(new RuntimeException("simulated aggregation failure"));

        JobStageTestTrend fallbackTrend = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .build();
        when(graphService.getJobStageTestTrendWithFallback(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenReturn(fallbackTrend);

        controller.getJobStageTestTrend("c", "o", "r", "b", 1L, "s", null, null, 100);

        verify(graphService).getJobStageTestTrendWithFallback(
                eq("c"), eq("o"), eq("r"), eq("b"),
                eq(1L), eq("s"), isNull(), isNull(), eq(30));
    }
}
