package io.github.ericdriggs.reportcard.util;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringMapUtilTest {

    @Test
    void commaSeparatedTest() {
        TreeMap<String, String> map = StringMapUtil.stringToMap("FOO=foo1,BAR=bar1,BAZ=baz1");
        assertValues(map);
    }
    @Test
    void trailingCommaTest() {
        TreeMap<String, String> map =StringMapUtil.stringToMap("FOO=foo1,BAR=bar1,BAZ=baz1,");
        assertValues(map);
    }

    void assertValues(TreeMap<String,String> map) {
        assertEquals(3, map.size());
        assertEquals("foo1", map.get("FOO"));
        assertEquals("bar1", map.get("BAR"));
        assertEquals("baz1", map.get("BAZ"));
    }

}
