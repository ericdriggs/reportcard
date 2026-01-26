package io.github.ericdriggs.reportcard.controller.graph;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JobInfoParserTest {

    @Test
    void testWildcardConversion() {
        List<String> jobInfo = Arrays.asList("application:foo*");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals(1, result.size());
        assertEquals("foo%", result.get("application"));
    }

    @Test
    void testMultipleWildcards() {
        List<String> jobInfo = Arrays.asList("application:*foo*");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals("%foo%", result.get("application"));
    }

    @Test
    void testNoWildcard() {
        List<String> jobInfo = Arrays.asList("pipeline:dev-cp3");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals("dev-cp3", result.get("pipeline"));
    }

    @Test
    void testMultipleParams() {
        List<String> jobInfo = Arrays.asList("application:foo*", "pipeline:dev*");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals(2, result.size());
        assertEquals("foo%", result.get("application"));
        assertEquals("dev%", result.get("pipeline"));
    }

    @Test
    void testInvalidFormat_NoColon() {
        List<String> jobInfo = Arrays.asList("invalid");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testInvalidFormat_MultipleColons() {
        List<String> jobInfo = Arrays.asList("key:value:extra");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals(1, result.size());
        assertEquals("value:extra", result.get("key"));
    }

    @Test
    void testTrimming() {
        List<String> jobInfo = Arrays.asList(" application : foo ");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals("foo", result.get("application"));
    }

    @Test
    void testNullInput() {
        Map<String, String> result = JobInfoParser.parseJobInfoParams(null);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testEmptyList() {
        Map<String, String> result = JobInfoParser.parseJobInfoParams(Arrays.asList());
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMixedValidInvalid() {
        List<String> jobInfo = Arrays.asList("valid:value", "invalid", "another:test*");
        Map<String, String> result = JobInfoParser.parseJobInfoParams(jobInfo);
        
        assertEquals(2, result.size());
        assertEquals("value", result.get("valid"));
        assertEquals("test%", result.get("another"));
    }
}
