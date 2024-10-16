package io.github.ericdriggs.reportcard.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

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

    public static int compareInteger(Integer val1, Integer val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return Integer.compare(val1, val2);
    }

    public static int compareLong(Long val1, Long val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return Long.compare(val1, val2);
    }

    public static int compareURI(URI val1, URI val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return val1.toString().compareTo(val2.toString());
    }

    public static int compareBigDecimalAsBigInteger(BigDecimal val1, BigDecimal val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        BigInteger b1 = val1.toBigInteger();
        BigInteger b2 = val2.toBigInteger();
        return b1.compareTo(b2);
    }

    public static <T extends Comparable<T>> int compareComparableCollection(Collection<T> val1, Collection<T> val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        if (CollectionUtils.isEqualCollection(val1, val2)) {
            return 0;
        }
        TreeSet<T> t1 = new TreeSet<T>(val1);
        TreeSet<T> t2 = new TreeSet<T>(val2);

        return chainCompare(
                ObjectUtils.compare(val1.size(), val2.size()),
                ObjectUtils.compare(t1.toString(), t2.toString())
        );
    }

    //Note: Requires toString for T and U to serialize contents not just object address. This method would need work before externalizing
    public static <T extends Comparable<T>, U extends Comparable<U>> int compareComparableMap(TreeMap<T,U> val1, TreeMap<T,U> val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        if (val1.equals(val2)) {
            return 0;
        }
        if (    ObjectUtils.compare(val1.size(), val2.size()) != 0) {
            return ObjectUtils.compare(val1.size(), val2.size());
        }

        return chainCompare(
                ObjectUtils.compare(val1.size(), val2.size()),
                compareComparableCollection(val1.keySet(), val1.keySet()),
                compareComparableCollection(val1.values(), val2.values()),
                ObjectUtils.compare(val1.toString(), val1.toString())
        );
    }

    public static int compareMultiValueStringMap(TreeMap<String, TreeSet<String>> val1, TreeMap<String, TreeSet<String>> val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        if (val1.equals(val2)) {
            return 0;
        }
        return ObjectUtils.compare(val1.toString(), val2.toString());
    }

}
