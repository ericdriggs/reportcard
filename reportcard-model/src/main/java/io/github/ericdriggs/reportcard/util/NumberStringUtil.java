package io.github.ericdriggs.reportcard.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public enum NumberStringUtil {

    ; //static methods only

    /**
     * Null safe long to string
     * @param val val
     * @return long as string or "" if <code>null</code>
     */
    public static String toString(Long val) {
        if (val == null) {
            return "";
        }
        return Long.toString(val);
    }

    /**
     * Null safe integer to string
     * @param val val
     * @return integer as string or "" if <code>null</code>
     */
    public static String toString(Integer val) {
        if (val == null) {
            return "";
        }
        return Integer.toString(val);
    }

    public static String bigDecimalToIntString(BigDecimal val) {
        if (val == null) {
            return "";
        }
        return val.toBigInteger().toString();
    }

    public static String percentFromBigDecimal(BigDecimal val) {
        if (val == null) {
            return "";
        }
        return val.setScale(1, RoundingMode.HALF_UP).toString() + "%";
    }

    public static String fromSecondBigDecimal(BigDecimal durationSeconds) {

        if (durationSeconds == null) {
            return "";
        }

        Duration duration = Duration.ofMillis(durationSeconds.multiply(BigDecimal.valueOf(1000)).longValue());

        long days = duration.toDaysPart();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        BigDecimal seconds = getDecimalSeconds(duration);

        final String dayString = days > 0 ? days + "d " : "";
        final String hourString = hours > 0 ? hours + "h " : "";
        final String minuteString = minutes > 0 || hours > 0 ? minutes + "m " : "";
        final String secondString = seconds.toPlainString() + "s";
        return dayString + hourString + minuteString + secondString;
    }

    static BigDecimal getDecimalSeconds(Duration duration) {
        int seconds = duration.toSecondsPart();

        BigDecimal secondDecimalOnly = new BigDecimal(
                duration.toMillisPart()).setScale(2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);

        return BigDecimal.valueOf(seconds).add(secondDecimalOnly).setScale(2, RoundingMode.HALF_UP);
    }

}
