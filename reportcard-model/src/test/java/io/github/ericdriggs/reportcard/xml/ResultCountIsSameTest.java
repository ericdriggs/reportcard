package io.github.ericdriggs.reportcard.xml;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ResultCountIsSameTest {

    private static ResultCount base() {
        return ResultCount.builder()
                .errors(0).failures(1).skipped(2).successes(3).tests(6)
                .time(BigDecimal.ZERO)
                .build();
    }

    @Test
    void isSame_ignoresTime() {
        ResultCount karateTime = base().toBuilder().time(new BigDecimal("193.887")).build();
        assertTrue(base().isSame(karateTime), "time-only difference must be same");
        assertTrue(karateTime.isSame(base()), "symmetric");
    }

    @Test
    void isSame_trueOnIdentical() {
        assertTrue(base().isSame(base()));
    }

    @Test
    void isSame_falseOnAnyCountDifference() {
        assertFalse(base().isSame(base().toBuilder().errors(1).build()), "errors");
        assertFalse(base().isSame(base().toBuilder().failures(2).build()), "failures");
        assertFalse(base().isSame(base().toBuilder().skipped(3).build()), "skipped");
        assertFalse(base().isSame(base().toBuilder().successes(4).build()), "successes");
        assertFalse(base().isSame(base().toBuilder().tests(7).build()), "tests");
    }

    @Test
    void isSame_falseOnNull() {
        assertFalse(base().isSame(null));
    }
}
