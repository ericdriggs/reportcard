package io.github.ericdriggs.reportcard.util.truncate;

import java.nio.charset.StandardCharsets;

public enum TruncateUtils {
    ;//static methods only

    public static String truncateBytes(String str, int maxStringBytes) {

        if (str == null) {
            return null;
        }

        //Nothing to do
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxStringBytes) {
            return str;
        }

        //Iteratively trim until below target length
        for (int i = maxStringBytes; i > 0; i--) {
            final String subString = str.substring(0, i);
            final byte[] subStringBytes = subString.getBytes(StandardCharsets.UTF_8);
            if (subStringBytes.length <= maxStringBytes) {
                return subString;
            }
        }
        throw new IllegalStateException("truncation coding error -- should be unreachable code");
    }

    public static String truncateLeft(String str, int maxLength) {
        return truncateLeft(str, maxLength, false);
    }

    public static String truncateLeft(String str, int maxLength, boolean shouldAddEllipsis) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return (shouldAddEllipsis ? "…" : "") + str.substring(0, maxLength);
    }

    public static String truncateRight(String str, int maxLength, boolean shouldAddEllipsis) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return (shouldAddEllipsis ? "…" : "") + str.substring(str.length()-maxLength);
    }
}
