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

    @Test
    void preservesHyphensAndDots() {
        TreeMap<String, String> map = StringMapUtil.stringToMap("application=foo-app,host=build.corp.jenkins.com");
        assertEquals("foo-app", map.get("application"));
        assertEquals("build.corp.jenkins.com", map.get("host"));
    }

    @Test
    void preservesUnderscoresAndDigits() {
        TreeMap<String, String> map = StringMapUtil.stringToMap("env=prod_us-east-1,pipeline=release-candidate");
        assertEquals("prod_us-east-1", map.get("env"));
        assertEquals("release-candidate", map.get("pipeline"));
    }

}
