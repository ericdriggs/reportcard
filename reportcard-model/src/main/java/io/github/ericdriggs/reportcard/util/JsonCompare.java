package io.github.ericdriggs.reportcard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.internal.Diff;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;
import java.util.TreeMap;

import static net.javacrumbs.jsonunit.JsonAssert.when;

public enum JsonCompare {
    ; //static methods only

    private final static ObjectMapper mapper = new ObjectMapper();

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

    @SneakyThrows(JsonProcessingException.class)
    public static boolean equalsMap(String expected, Map actualMap) {
        String actual = mapper.writeValueAsString(actualMap);
        return equalsForConfiguration(expected, actual, when(Option.TREATING_NULL_AS_ABSENT));
    }

    @SneakyThrows(JsonProcessingException.class)
    public static boolean containsMap(Map<String,String> expectedMap, String actualJson) {
        Map<String,String> actualMap = mapper.readValue(actualJson, TreeMap.class);
        return actualMap.entrySet().containsAll(expectedMap.entrySet());
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