package io.github.ericdriggs.reportcard.xml;

import java.util.Collection;

public enum IsEmptyUtil {
    ; //static methods only

    public static boolean isCollectionEmpty(Collection<?> col) {
        if (col == null) {
            return true;
        }
        return col.isEmpty();
    }


}
