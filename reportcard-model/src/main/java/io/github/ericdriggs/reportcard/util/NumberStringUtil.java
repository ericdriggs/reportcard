package io.github.ericdriggs.reportcard.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

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
        Long percent = val.setScale(1, RoundingMode.HALF_UP).toBigInteger().longValue();
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

        public static String fromSecondBigDecimalPadded(BigDecimal durationSeconds) {

        if (durationSeconds == null) {
            return "";
        }

        Duration duration = Duration.ofMillis(durationSeconds.multiply(BigDecimal.valueOf(1000)).longValue());

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

}
