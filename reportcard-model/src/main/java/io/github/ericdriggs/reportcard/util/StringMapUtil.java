package io.github.ericdriggs.reportcard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.TreeMap;


@Slf4j
public enum StringMapUtil {
    ;//static methods only

    private final static ObjectMapper  mapper = SharedObjectMappers.simpleObjectMapper;

    @SneakyThrows(JsonProcessingException.class)
    public static TreeMap<String,String> jsonToMap(String json) {
        TypeReference<TreeMap<String, String>> typeRef
                = new TypeReference<TreeMap<String, String>>() {};
        return mapper.readValue(json, typeRef);
    }

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
            String[] kv = s.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else {
                log.warn("unable to parse as key=value: " + s);
            }
        }
        return map;
    }

    public static TreeMap<String,String> lower(TreeMap<String,String> map) {
        if (map == null || map .isEmpty()) {
            return map;
        }

        TreeMap<String,String> ret = new TreeMap<>();
        for (Map.Entry<String,String> entry : map.entrySet()) {
            ret.put(entry.getKey().toLowerCase(), entry.getValue().toLowerCase());
        }
        return ret;
    }
}
