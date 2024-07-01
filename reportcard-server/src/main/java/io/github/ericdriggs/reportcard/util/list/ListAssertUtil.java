package io.github.ericdriggs.reportcard.util.list;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public enum ListAssertUtil {
    ;//static methods only

    public static void assertSize1(List<?> col, String name) {
        if (CollectionUtils.isEmpty(col)) {
            throw new NullPointerException(name);
        }

        if (col.size() != 1) {
            throw new IllegalArgumentException("expected size 1. actual " + name + ".size(): " + col.size());
        }
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

}
