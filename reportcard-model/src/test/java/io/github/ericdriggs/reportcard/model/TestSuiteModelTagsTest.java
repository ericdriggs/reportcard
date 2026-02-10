package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuiteModelTagsTest {

    private final ObjectMapper mapper = SharedObjectMappers.ignoreUnknownObjectMapper;

    @Test
    void testSuiteModel_tagsSerializeToJson() throws Exception {
        TestSuiteModel suite = TestSuiteModel.builder()
            .name("test-feature.feature")
            .tags(Arrays.asList("smoke", "env=staging"))
            .build();

        String json = mapper.writeValueAsString(suite);

        assertTrue(json.contains("\"tags\""));
        assertTrue(json.contains("\"smoke\""));
        assertTrue(json.contains("\"env=staging\""));
    }

    @Test
    void testCaseModel_tagsSerializeToJson() throws Exception {
        TestCaseModel testCase = TestCaseModel.builder()
            .name("test scenario")
            .tags(Arrays.asList("regression", "browser=chrome"))
            .build();

        String json = mapper.writeValueAsString(testCase);

        assertTrue(json.contains("\"tags\""));
        assertTrue(json.contains("\"regression\""));
        assertTrue(json.contains("\"browser=chrome\""));
    }

    @Test
    void testSuiteModel_fromJson_deserializesTags() throws Exception {
        String json = "{\"name\":\"test.feature\",\"tags\":[\"smoke\",\"env=prod\"]}";

        TestSuiteModel suite = mapper.readValue(json, TestSuiteModel.class);

        assertNotNull(suite.getTags());
        assertEquals(2, suite.getTags().size());
        assertTrue(suite.getTags().contains("smoke"));
        assertTrue(suite.getTags().contains("env=prod"));
    }

    @Test
    void testCaseModel_fromJson_deserializesTags() throws Exception {
        String json = "{\"name\":\"test scenario\",\"tags\":[\"ui\",\"critical\"]}";

        TestCaseModel testCase = mapper.readValue(json, TestCaseModel.class);

        assertNotNull(testCase.getTags());
        assertEquals(2, testCase.getTags().size());
        assertTrue(testCase.getTags().contains("ui"));
        assertTrue(testCase.getTags().contains("critical"));
    }

    @Test
    void testSuiteModel_emptyTagsDefaultsToEmptyList() throws Exception {
        TestSuiteModel suite = TestSuiteModel.builder()
            .name("test.feature")
            .build();

        assertNotNull(suite.getTags());
        assertEquals(0, suite.getTags().size());
    }

    @Test
    void testCaseModel_emptyTagsDefaultsToEmptyList() throws Exception {
        TestCaseModel testCase = TestCaseModel.builder()
            .name("test scenario")
            .build();

        assertNotNull(testCase.getTags());
        assertEquals(0, testCase.getTags().size());
    }
}
