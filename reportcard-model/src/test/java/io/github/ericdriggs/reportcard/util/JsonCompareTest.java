package io.github.ericdriggs.reportcard.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonCompareTest {

    @Test
    void equalsTest() {
        String expected = "{`foo`:`bar`, `baz`:`qux`}".replaceAll("`","\"");
        String actual = "{ `baz`:`qux`, `foo`:`bar` }".replaceAll("`","\"");

        assertTrue(JsonCompare.equals(expected, actual));
    }

    @Test
    void givenDifferentOrderButSame_WhenCompare_ThenZero() {
        final String json1 = """
                             {"PIPELINE": "pipeline1", "APPLICATION": "app1"}
                             """;

        final String json2 = """
                             {"APPLICATION": "app1", "PIPELINE": "pipeline1"}
                             """;
        assertEquals(0, JsonCompare.compareTo(json1, json2));
    }
}
