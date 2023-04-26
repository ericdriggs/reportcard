package io.github.ericdriggs.reportcard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.core.Option;

import java.util.Map;

import static net.javacrumbs.jsonunit.JsonAssert.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonAssert {

    final static ObjectMapper objectMapper = new ObjectMapper();

    public static void assertJsonEquals(final String expected, final String actual) {
        net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals(expected, actual, when(Option.IGNORING_ARRAY_ORDER));
    }

    public static void assertJsonEquals(Map<String, String> map, String json) {
        assertJsonEquals(fromMap(map), json);
    }

    public static void assertJsonEquals(String json, Map<String, String> map) {
        assertJsonEquals(json, fromMap(map));
    }

    public static void assertJsonEquals(Map<String, String> map1, Map<String, String> map2) {
        assertEquals(fromMap(map1), fromMap(map2));
    }

    @SneakyThrows(JsonProcessingException.class)
    protected static String fromMap(Map<String, String> map) {
        return objectMapper.writeValueAsString(map);
    }

}
