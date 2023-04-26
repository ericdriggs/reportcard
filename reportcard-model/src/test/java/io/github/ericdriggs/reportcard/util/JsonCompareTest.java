package io.github.ericdriggs.reportcard.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonCompareTest {

    @Test
    void equalsTest() {
        String expected = "{`foo`:`bar`, `baz`:`qux`}".replaceAll("`","\"");
        String actual = "{ `baz`:`qux`, `foo`:`bar` }".replaceAll("`","\"");

        assertTrue(JsonCompare.equals(expected, actual));
    }
}
