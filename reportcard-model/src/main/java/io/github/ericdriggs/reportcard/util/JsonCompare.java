package io.github.ericdriggs.reportcard.util;

import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.internal.Diff;
import org.apache.commons.lang3.ObjectUtils;

import static net.javacrumbs.jsonunit.JsonAssert.when;

public enum JsonCompare {
    ; //static methods only

    public static int compareTo(String expected, String actual) {
        if (expected != null && actual != null) {
            if (equalsIgnoreArrayOrder(expected, actual)) {
                return 0;
            }
        }
        return ObjectUtils.compare(expected, actual);
    }

    public static boolean equals(String expected, String actual) {
        return equalsForConfiguration(expected, actual, when(Option.TREATING_NULL_AS_ABSENT));
    }

    public static boolean equalsIgnoreArrayOrder(String expected, String actual) {
        return equalsForConfiguration(expected, actual, when(Option.TREATING_NULL_AS_ABSENT, Option.IGNORING_ARRAY_ORDER));
    }

    protected static boolean equalsForConfiguration(String expected, String actual, Configuration configuration) {
        Diff diff = Diff.create(expected,
                actual,
                "fullJson",
                "",
                configuration);
        return diff.similar();
    }
}