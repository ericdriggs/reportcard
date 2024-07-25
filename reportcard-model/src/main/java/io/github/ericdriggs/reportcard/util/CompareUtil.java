package io.github.ericdriggs.reportcard.util;

import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

public enum CompareUtil {
    ;//static methods only

    public static int chainCompare(int... compares) {
        for (int compare : compares) {
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    public static int compareLowerNullSafe(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return ObjectUtils.compare(s1, s2);
        }
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }

    public static int compareNullSafe(URI s1, URI s2) {
        if (s1 == null || s2 == null) {
            return ObjectUtils.compare(s1, s2);
        }
        return s1.toString().compareTo(s2.toString());
    }


    public static String toLower(String string) {
        if (string == null) {
            return null;
        } else {
            return string.toLowerCase();
        }
    }

    public static int compareLong(Long val1, Long val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return Long.compare(val1, val2);
    }

    public static int compareURI(URI val1, URI val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return val1.toString().compareTo(val2.toString());
    }

    public static int compareBigDecimalAsBigInteger(BigDecimal val1, BigDecimal val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        BigInteger b1 = val1.toBigInteger();
        BigInteger b2 = val2.toBigInteger();
        return b1.compareTo(b2);
    }
}
