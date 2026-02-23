package io.github.ericdriggs.reportcard.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class NumberStringUtilTest {

    @Test
    void decimalSecondTest() {
        BigDecimal seconds = new BigDecimal("1.180");
        String str = NumberStringUtil.fromSecondBigDecimal(seconds);
        assertEquals("1.18s", str);
    }

    @Test
    void hmsMillisTest() {

        long dayPlusHourPlusMinute = 86400 + 3600 + 60;
        //day + hour +
        BigDecimal seconds = new BigDecimal(dayPlusHourPlusMinute).setScale(4, RoundingMode.HALF_UP)
                                                              .add(new BigDecimal("5.12345").setScale(4, RoundingMode.HALF_UP));
        String str = NumberStringUtil.fromSecondBigDecimal(seconds);
        assertEquals("1d 1h 1m 5.12s", str);
    }

    @Test
    void fromSecondBigDecimalPadded_duration_equivalentToBigDecimal() {
        // Duration version should produce same output as BigDecimal version
        Duration duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(45);
        BigDecimal seconds = BigDecimal.valueOf(duration.getSeconds());

        String fromDuration = NumberStringUtil.fromSecondBigDecimalPadded(duration);
        String fromBigDecimal = NumberStringUtil.fromSecondBigDecimalPadded(seconds);

        assertEquals(fromBigDecimal, fromDuration);
    }

    @Test
    void fromSecondBigDecimalPadded_duration_nullReturnsEmpty() {
        Duration duration = null;
        String result = NumberStringUtil.fromSecondBigDecimalPadded(duration);
        assertEquals("", result);
    }

    @Test
    void isoUtcTimestamp_validInstant() {
        Instant instant = Instant.parse("2026-02-23T10:45:00Z");
        String result = NumberStringUtil.isoUtcTimestamp(instant);
        assertEquals("2026-02-23T10:45:00Z", result);
    }

    @Test
    void isoUtcTimestamp_nullReturnsNoDataMessage() {
        String result = NumberStringUtil.isoUtcTimestamp(null);
        assertEquals("No data within range", result);
    }

    // Phase 2: Friendly date range tests
    @Test
    void friendlyDateRange_sameMonth() {
        Instant start = Instant.parse("2026-02-17T00:00:00Z");
        Instant end = Instant.parse("2026-02-23T23:59:59Z");
        String result = NumberStringUtil.friendlyDateRange(start, end);
        assertEquals("Feb 17-23, 2026", result);
    }

    @Test
    void friendlyDateRange_crossMonth() {
        Instant start = Instant.parse("2026-02-25T00:00:00Z");
        Instant end = Instant.parse("2026-03-03T23:59:59Z");
        String result = NumberStringUtil.friendlyDateRange(start, end);
        assertEquals("Feb 25 - Mar 3, 2026", result);
    }

    @Test
    void friendlyDateRange_nullReturnsPlaceholder() {
        String result = NumberStringUtil.friendlyDateRange(null, null);
        assertEquals("—", result);
    }

    // Phase 2: Delta formatting tests
    @Test
    void formatDeltaPercent_positive() {
        BigDecimal current = new BigDecimal("95");
        BigDecimal previous = new BigDecimal("92");
        String result = NumberStringUtil.formatDeltaPercent(current, previous);
        assertEquals("+3%↑", result);
    }

    @Test
    void formatDeltaPercent_negative() {
        BigDecimal current = new BigDecimal("88");
        BigDecimal previous = new BigDecimal("92");
        String result = NumberStringUtil.formatDeltaPercent(current, previous);
        assertEquals("-4%↓", result);
    }

    @Test
    void formatDeltaPercent_zero() {
        BigDecimal current = new BigDecimal("92");
        BigDecimal previous = new BigDecimal("92");
        String result = NumberStringUtil.formatDeltaPercent(current, previous);
        assertEquals("0%", result);
    }

    @Test
    void formatDeltaInteger_positive() {
        // 15 vs 10 = +50% change
        String result = NumberStringUtil.formatDeltaInteger(15, 10);
        assertEquals("+50%↑", result);
    }

    @Test
    void formatDeltaInteger_negative() {
        // 8 vs 12 = -33% change (rounded from -33.33%)
        String result = NumberStringUtil.formatDeltaInteger(8, 12);
        assertEquals("-33%↓", result);
    }

    @Test
    void formatDeltaInteger_zero() {
        String result = NumberStringUtil.formatDeltaInteger(100, 100);
        assertEquals("0%", result);
    }

    @Test
    void formatDeltaInteger_fromZero() {
        String result = NumberStringUtil.formatDeltaInteger(10, 0);
        assertEquals("+∞%↑", result);
    }

    @Test
    void formatDeltaDuration_positive() {
        // 120s vs 100s = +20% change
        String result = NumberStringUtil.formatDeltaDuration(new BigDecimal("120"), new BigDecimal("100"));
        assertEquals("+20%↑", result);
    }

    @Test
    void formatDeltaDuration_negative() {
        // 80s vs 100s = -20% change
        String result = NumberStringUtil.formatDeltaDuration(new BigDecimal("80"), new BigDecimal("100"));
        assertEquals("-20%↓", result);
    }

    @Test
    void formatDeltaDuration_zero() {
        String result = NumberStringUtil.formatDeltaDuration(new BigDecimal("100"), new BigDecimal("100"));
        assertEquals("0%", result);
    }

    @Test
    void isSignificantChange_aboveThreshold() {
        BigDecimal current = new BigDecimal("110");
        BigDecimal previous = new BigDecimal("100");
        boolean result = NumberStringUtil.isSignificantChange(current, previous, new BigDecimal("5"));
        assertTrue(result); // 10% change > 5% threshold
    }

    @Test
    void isSignificantChange_belowThreshold() {
        BigDecimal current = new BigDecimal("102");
        BigDecimal previous = new BigDecimal("100");
        boolean result = NumberStringUtil.isSignificantChange(current, previous, new BigDecimal("5"));
        assertFalse(result); // 2% change < 5% threshold
    }

    @Test
    void deltaDirection_positiveGood() {
        BigDecimal delta = new BigDecimal("5");
        String result = NumberStringUtil.deltaDirection(delta, true);
        assertEquals("positive", result);
    }

    @Test
    void deltaDirection_negativeBad() {
        BigDecimal delta = new BigDecimal("-5");
        String result = NumberStringUtil.deltaDirection(delta, true);
        assertEquals("negative", result);
    }
}
