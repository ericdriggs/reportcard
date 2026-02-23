package io.github.ericdriggs.reportcard.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public enum NumberStringUtil {

    ; //static methods only

    /**
     * Null safe long to string
     *
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
     *
     * @param val val
     * @return integer as string or "" if <code>null</code>
     */
    public static String toString(Integer val) {
        if (val == null) {
            return "";
        }
        return Integer.toString(val);
    }

    public static String percentFromBigDecimal(BigDecimal val) {
        if (val == null) {
            return "";
        }
        Long percent = val.setScale(0, RoundingMode.HALF_UP).longValue();
        StringBuilder sb = new StringBuilder();
        sb.append("<span class='transparent'>");
        if (percent >= 100) {
            sb.append("</span>" + percent);
        } else if (percent >= 10) {
            sb.append("0</span>" + percent);
        } else {
            sb.append("00</span>" + percent);
        }
        sb.append("%");

        return sb.toString();
    }

    /**
     * second as string with starting 0's for lexical sorting of numbber
     * @param durationSeconds
     * @return
     */
    public static String zeroPaddedSecond(BigDecimal durationSeconds) {
        if (durationSeconds == null) {
            return "";
        }
        BigDecimal rounded = durationSeconds.setScale(0, RoundingMode.HALF_UP);

        String padding = "";
        if (rounded.compareTo(new BigDecimal(10)) < 0) {
            padding = "000";
        } else if (rounded.compareTo(new BigDecimal(100)) < 0) {
            padding = "00";
        } else if (rounded.compareTo(new BigDecimal(1000)) < 0) {
            padding = "0";
        }
        return padding + durationSeconds.setScale(0, RoundingMode.HALF_UP) + "s";
    }

    public static String fromSecondBigDecimal(BigDecimal durationSeconds) {

        if (durationSeconds == null) {
            return "";
        }

        Duration duration = Duration.ofMillis(durationSeconds.multiply(BigDecimal.valueOf(1000)).longValue());

        long days = duration.toDaysPart();
        long years = 0;
        if (days > 365) {
            years = days / 365;
            days = days - years * 365;
        }
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        BigDecimal seconds = getDecimalSeconds(duration);

        final String yearString = years > 0 ? years + "y " : "";
        final String dayString = days > 0 ? days + "d " : "";
        final String hourString = hours > 0 ? hours + "h " : "";
        final String minuteString = minutes > 0 || hours > 0 ? minutes + "m " : "";
        final String secondString = seconds.toPlainString() + "s";
        return yearString+ dayString + hourString + minuteString + secondString;
    }

    public static String fromIntegerPadded(Integer val) {
        StringBuilder sb = new StringBuilder();
        sb.append("<span class='transparent'>");
        final String padding = "0,000,000,000";
        final String commaSeparated = String.format("%,d", val);
        int position = padding.length() - commaSeparated.length();
        sb.append(padding, 0, position);
        sb.append("</span>");
        sb.append(commaSeparated);
        return sb.toString();
    }

    /**
     * Format duration as padded string for lexical sorting.
     * Duration is the primary implementation - use this for elapsed time.
     */
    public static String fromSecondBigDecimalPadded(Duration duration) {
        if (duration == null) {
            return "";
        }

        int days = (int)duration.toDaysPart();
        int years = 0;
        if (days > 365) {
            years = days / 365;
            days = days - years * 365;
        }
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        boolean transparent = true;
        sb.append("<span class='transparent'>");

        if (years > 0 ) {
            transparent = false;
            sb.append("</span>");
        }
        sb.append(paddedTransparent(years,2,"y"));

        if (days > 0 && transparent) {
            transparent = false;
            sb.append("</span>");
        }
        sb.append(paddedTransparent(days,3,"d"));

        if (hours > 0 && transparent) {
            transparent = false;
            sb.append("</span>");
        }
        sb.append(padded(hours,2,"h"));

        if (minutes > 0 && transparent) {
            sb.append("</span>");
        }
        sb.append(padded(minutes,2,"m"));

        if (transparent) {
            sb.append("</span>");
        }
        if (years == 0 && days == 0 && hours == 0 && minutes == 0) {
            sb.append(padded(seconds,2,"s"));
        }
        return sb.toString();
    }

    /**
     * Format seconds as padded duration string for lexical sorting.
     * Delegates to Duration version.
     */
    public static String fromSecondBigDecimalPadded(BigDecimal durationSeconds) {
        if (durationSeconds == null) {
            return "";
        }
        return fromSecondBigDecimalPadded(Duration.ofMillis(durationSeconds.multiply(BigDecimal.valueOf(1000)).longValue()));
    }

    static String paddedTransparent(int num, int columns, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append("<span class='transparent'>");
        String ret = String.format("%0" + columns + "d", num);
        ret = ret.replaceAll(num + "$", "</span>" + num);
        sb.append(ret);
        sb.append(suffix);
        return sb.toString();
    }

    static String padded(int num, int columns, String suffix) {
        return String.format("%0" + columns + "d", num) + suffix;
    }

    static BigDecimal getDecimalSeconds(Duration duration) {
        int seconds = duration.toSecondsPart();

        BigDecimal secondDecimalOnly = new BigDecimal(
                duration.toMillisPart()).setScale(2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);

        return BigDecimal.valueOf(seconds).add(secondDecimalOnly).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Format an Instant as ISO 8601 UTC timestamp for tooltip display.
     * @param instant the instant to format
     * @return ISO 8601 string (e.g., "2026-02-23T10:45:00Z") or "No data within range" if null
     */
    public static String isoUtcTimestamp(Instant instant) {
        if (instant == null) {
            return "No data within range";
        }
        return instant.toString();
    }

    /**
     * Format a date range as friendly string.
     * Same month: "Feb 17-23, 2026"
     * Cross-month: "Feb 17 - Mar 2, 2026"
     */
    public static String friendlyDateRange(Instant start, Instant end) {
        if (start == null || end == null) {
            return "—";
        }
        LocalDate startDate = start.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = end.atZone(ZoneOffset.UTC).toLocalDate();

        String startMonth = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
        String endMonth = endDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);

        if (startDate.getMonth() == endDate.getMonth() && startDate.getYear() == endDate.getYear()) {
            return String.format("%s %d-%d, %d",
                startMonth, startDate.getDayOfMonth(), endDate.getDayOfMonth(), startDate.getYear());
        } else {
            return String.format("%s %d - %s %d, %d",
                startMonth, startDate.getDayOfMonth(),
                endMonth, endDate.getDayOfMonth(), endDate.getYear());
        }
    }

    /**
     * Format delta for percentage values with arrow indicator.
     * Returns "+3%↑", "-2%↓", or "0%"
     */
    public static String formatDeltaPercent(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null) {
            return "—";
        }
        BigDecimal delta = current.subtract(previous).setScale(0, RoundingMode.HALF_UP);
        int cmp = delta.compareTo(BigDecimal.ZERO);
        String arrow = cmp > 0 ? "↑" : cmp < 0 ? "↓" : "";
        String sign = cmp > 0 ? "+" : "";
        return sign + delta + "%" + arrow;
    }

    /**
     * Format delta for integer values as percentage change with arrow indicator.
     * Returns "+10%↑", "-5%↓", or "0%"
     */
    public static String formatDeltaInteger(Integer current, Integer previous) {
        if (current == null || previous == null) {
            return "—";
        }
        if (previous == 0) {
            if (current == 0) return "0%";
            return current > 0 ? "+∞%↑" : "-∞%↓";
        }
        return formatPercentChange(BigDecimal.valueOf(current), BigDecimal.valueOf(previous));
    }

    /**
     * Format delta for duration (BigDecimal seconds) as percentage change with arrow indicator.
     * Returns "+15%↑", "-10%↓", or "0%"
     */
    public static String formatDeltaDuration(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null) {
            return "—";
        }
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            int cmp = current.compareTo(BigDecimal.ZERO);
            if (cmp == 0) return "0%";
            return cmp > 0 ? "+∞%↑" : "-∞%↓";
        }
        return formatPercentChange(current, previous);
    }

    /**
     * Calculate and format percentage change between two values.
     * Returns "+10%↑", "-5%↓", or "0%"
     */
    private static String formatPercentChange(BigDecimal current, BigDecimal previous) {
        BigDecimal percentChange = current.subtract(previous)
            .multiply(BigDecimal.valueOf(100))
            .divide(previous.abs(), 0, RoundingMode.HALF_UP);
        int cmp = percentChange.compareTo(BigDecimal.ZERO);
        String arrow = cmp > 0 ? "↑" : cmp < 0 ? "↓" : "";
        String sign = cmp > 0 ? "+" : "";
        return sign + percentChange + "%" + arrow;
    }

    /**
     * Check if change is significant (greater than threshold percentage).
     * @param current current value
     * @param previous previous value (baseline)
     * @param thresholdPercent threshold percentage (e.g., 5 for 5%)
     * @return true if |change| > threshold%
     */
    public static boolean isSignificantChange(BigDecimal current, BigDecimal previous, BigDecimal thresholdPercent) {
        if (current == null || previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        BigDecimal percentChange = current.subtract(previous).abs()
            .multiply(BigDecimal.valueOf(100))
            .divide(previous.abs(), 1, RoundingMode.HALF_UP);
        return percentChange.compareTo(thresholdPercent) > 0;
    }

    /**
     * Determine if delta is positive (good), negative (bad), or neutral.
     * @param delta the delta value
     * @param higherIsGood true if higher values are better
     * @return "positive" if good change, "negative" if bad change, "neutral" if zero/insignificant
     */
    public static String deltaDirection(BigDecimal delta, boolean higherIsGood) {
        if (delta == null) return "neutral";
        int cmp = delta.compareTo(BigDecimal.ZERO);
        if (cmp == 0) return "neutral";
        boolean isIncrease = cmp > 0;
        boolean isGood = (isIncrease && higherIsGood) || (!isIncrease && !higherIsGood);
        return isGood ? "positive" : "negative";
    }

}
