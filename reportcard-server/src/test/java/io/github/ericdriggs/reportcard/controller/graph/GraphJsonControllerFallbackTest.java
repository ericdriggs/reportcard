package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
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

    @InjectMocks
    private GraphJsonController controller;

    @Test
    void returnsServiceResultDirectly() {
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
        assertFalse(response.getBody().isUsedFallback());
    }

    @Test
    void returnsFallbackResultFromService() {
        JobStageTestTrend fallbackTrend = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .usedFallback(true)
                .build();
        when(graphService.getJobStageTestTrend(
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), any(), any(), anyInt()
        )).thenReturn(fallbackTrend);

        ResponseEntity<JobStageTestTrend> response = controller.getJobStageTestTrend(
                "c", "o", "r", "b", 1L, "s", null, null, 30);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isUsedFallback());
    }
}
