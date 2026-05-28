package io.github.ericdriggs.reportcard.model.trend;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class JobStageTestTrendTest {

    @Test
    void withUsedFallbackReturnsCopyWithFieldSet() {
        JobStageTestTrend original = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .maxRuns(30)
                .build();

        assertFalse(original.isUsedFallback());

        JobStageTestTrend withFallback = original.withUsedFallback(true);

        assertTrue(withFallback.isUsedFallback());
        assertEquals(original.getMaxRuns(), withFallback.getMaxRuns());
        assertEquals(original.getTestCaseTrends(), withFallback.getTestCaseTrends());
        assertNotSame(original, withFallback);
    }

    @Test
    void withUsedFallbackFalseIsNoOp() {
        JobStageTestTrend original = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .build();

        JobStageTestTrend result = original.withUsedFallback(false);

        assertFalse(result.isUsedFallback());
        assertEquals(original, result);
    }

    @Test
    void builderDefaultsUsedFallbackToFalse() {
        JobStageTestTrend trend = JobStageTestTrend.builder()
                .testCaseTrends(new TreeMap<>())
                .build();

        assertFalse(trend.isUsedFallback());
    }
}
