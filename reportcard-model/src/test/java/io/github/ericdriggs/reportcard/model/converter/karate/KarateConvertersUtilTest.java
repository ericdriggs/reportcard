package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KarateConvertersUtil.
 * Tests cover:
 * - PARS-01: parseKarateSummary parses valid JSON
 * - PARS-02: elapsedTime extracted correctly
 * - PARS-03: resultDate extracted correctly
 * - PARS-04: calculateStartTime computes start = end - elapsed
 * - PARS-05: Malformed JSON, null inputs, invalid dates all return null
 */
public class KarateConvertersUtilTest {

    // === parseKarateSummary tests ===

    @Test
    void parseKarateSummary_validJson_returnsSummary() {
        String karateJson = ResourceReader.resourceAsString("format-samples/karate/karate-summary-valid.json");

        KarateSummary summary = KarateConvertersUtil.parseKarateSummary(karateJson);

        assertNotNull(summary);
        assertEquals(307767.0, summary.getElapsedTime());
        assertEquals("2026-01-20 03:00:56 PM", summary.getResultDate());
        assertEquals(5, summary.getFeaturesPassed());
        assertEquals(1, summary.getFeaturesFailed());
        assertEquals(6, summary.getScenariosPassed());
        assertEquals(1, summary.getScenariosFailed());
        assertEquals("1.2.0", summary.getVersion());
    }

    @Test
    void parseKarateSummary_malformedJson_returnsNull() {
        String malformedJson = ResourceReader.resourceAsString("format-samples/karate/karate-summary-malformed.json");

        KarateSummary summary = KarateConvertersUtil.parseKarateSummary(malformedJson);

        assertNull(summary);
    }

    @Test
    void parseKarateSummary_nullInput_returnsNull() {
        KarateSummary summary = KarateConvertersUtil.parseKarateSummary(null);
        assertNull(summary);
    }

    @Test
    void parseKarateSummary_blankInput_returnsNull() {
        KarateSummary summary = KarateConvertersUtil.parseKarateSummary("   ");
        assertNull(summary);
    }

    @Test
    void parseKarateSummary_emptyJson_returnsSummaryWithNulls() {
        KarateSummary summary = KarateConvertersUtil.parseKarateSummary("{}");

        assertNotNull(summary);
        assertNull(summary.getElapsedTime());
        assertNull(summary.getResultDate());
    }

    // === parseResultDate tests ===

    @Test
    void parseResultDate_validFormat_returnsLocalDateTime() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate("2026-01-20 03:00:56 PM");

        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(20, result.getDayOfMonth());
        assertEquals(15, result.getHour());  // 3 PM = 15:00
        assertEquals(0, result.getMinute());
        assertEquals(56, result.getSecond());
    }

    @Test
    void parseResultDate_amTime_returnsCorrectHour() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate("2026-01-20 09:30:00 AM");

        assertNotNull(result);
        assertEquals(9, result.getHour());
    }

    @Test
    void parseResultDate_noon_returnsCorrectHour() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate("2026-01-20 12:00:00 PM");

        assertNotNull(result);
        assertEquals(12, result.getHour());
    }

    @Test
    void parseResultDate_midnight_returnsCorrectHour() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate("2026-01-20 12:00:00 AM");

        assertNotNull(result);
        assertEquals(0, result.getHour());
    }

    @Test
    void parseResultDate_invalidFormat_returnsNull() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate("2026/01/20 15:00:56");
        assertNull(result);
    }

    @Test
    void parseResultDate_nullInput_returnsNull() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate(null);
        assertNull(result);
    }

    @Test
    void parseResultDate_blankInput_returnsNull() {
        LocalDateTime result = KarateConvertersUtil.parseResultDate("  ");
        assertNull(result);
    }

    // === calculateStartTime tests ===

    @Test
    void calculateStartTime_validInputs_returnsCorrectStartTime() {
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 20, 15, 0, 56);
        Double elapsedMillis = 307767.0;  // ~5 minutes 7 seconds

        LocalDateTime startTime = KarateConvertersUtil.calculateStartTime(endTime, elapsedMillis);

        assertNotNull(startTime);
        // 15:00:56 - 307.767 seconds = 14:55:48.233
        assertEquals(2026, startTime.getYear());
        assertEquals(1, startTime.getMonthValue());
        assertEquals(20, startTime.getDayOfMonth());
        assertEquals(14, startTime.getHour());
        assertEquals(55, startTime.getMinute());
        assertEquals(48, startTime.getSecond());
    }

    @Test
    void calculateStartTime_nullEndTime_returnsNull() {
        LocalDateTime result = KarateConvertersUtil.calculateStartTime(null, 1000.0);
        assertNull(result);
    }

    @Test
    void calculateStartTime_nullElapsedTime_returnsNull() {
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 20, 15, 0, 0);
        LocalDateTime result = KarateConvertersUtil.calculateStartTime(endTime, null);
        assertNull(result);
    }

    @Test
    void calculateStartTime_negativeElapsedTime_returnsNull() {
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 20, 15, 0, 0);
        LocalDateTime result = KarateConvertersUtil.calculateStartTime(endTime, -1000.0);
        assertNull(result);
    }

    @Test
    void calculateStartTime_zeroElapsedTime_returnsEndTime() {
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 20, 15, 0, 0);
        LocalDateTime result = KarateConvertersUtil.calculateStartTime(endTime, 0.0);

        assertNotNull(result);
        assertEquals(endTime, result);
    }

    @Test
    void calculateStartTime_veryLargeElapsedTime_acceptsAsIs() {
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 20, 15, 0, 0);
        Double twentyFiveHoursInMillis = 25.0 * 60 * 60 * 1000;  // > 24 hours

        LocalDateTime result = KarateConvertersUtil.calculateStartTime(endTime, twentyFiveHoursInMillis);

        assertNotNull(result);
        // Should be previous day, 14:00
        assertEquals(19, result.getDayOfMonth());
        assertEquals(14, result.getHour());
    }

    // === Integration test: full parsing flow ===

    @Test
    void fullParsingFlow_validSummary_extractsTimingCorrectly() {
        String karateJson = ResourceReader.resourceAsString("format-samples/karate/karate-summary-valid.json");

        KarateSummary summary = KarateConvertersUtil.parseKarateSummary(karateJson);
        assertNotNull(summary);

        LocalDateTime endTime = KarateConvertersUtil.parseResultDate(summary.getResultDate());
        assertNotNull(endTime);

        LocalDateTime startTime = KarateConvertersUtil.calculateStartTime(endTime, summary.getElapsedTime());
        assertNotNull(startTime);

        // Verify start is before end
        assertTrue(startTime.isBefore(endTime));
    }
}
