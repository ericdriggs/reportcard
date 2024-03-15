package io.github.ericdriggs.reportcard.util;

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

}
