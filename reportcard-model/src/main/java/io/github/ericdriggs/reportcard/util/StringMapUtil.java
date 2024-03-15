package io.github.ericdriggs.reportcard.util;

import lombok.extern.slf4j.Slf4j;

import java.util.TreeMap;


@Slf4j
public enum StringMapUtil {
    ;//static methods only

    public static TreeMap<String,String> stringToMap(String str) {
        if (str == null) {
            return new TreeMap<>();
        }
        String[] kvs = str.split(",");
        if (kvs.length == 0) {
            return new TreeMap<>();
        }
        TreeMap<String,String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String s : kvs) {
            String[] kv = str.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else {
                log.warn("unable to parse as key=value: " + s);
            }
        }
        return map;
    }
}
