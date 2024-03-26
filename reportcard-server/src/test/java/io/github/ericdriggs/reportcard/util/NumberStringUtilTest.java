package io.github.ericdriggs.reportcard.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
