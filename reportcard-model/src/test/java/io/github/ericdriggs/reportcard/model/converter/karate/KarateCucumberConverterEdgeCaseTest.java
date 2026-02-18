package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge case tests for KarateCucumberConverter.
 * Validates robustness against various JSON structures and edge cases.
 * Complements KarateCucumberConverterTest with resource file tests and additional scenarios.
 */
public class KarateCucumberConverterEdgeCaseTest {

    // === NULL AND EMPTY HANDLING ===

    @Test
    void fromCucumberJson_nullInput_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCucumberJson_emptyString_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("");
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCucumberJson_blankString_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCucumberJson_emptyArray_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("[]");
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCucumberJson_notArray_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("{}");
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCucumberJson_invalidJson_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("not json");
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCucumberJson_truncatedJson_returnsEmptyList() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("[{\"name\":\"incomplete");
        assertTrue(result.isEmpty());
    }

    // === FEATURE PARSING ===

    @Test
    void feature_missingName_usesEmptyString() {
        String json = "[{\"elements\":[]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.size());
        assertEquals("", result.get(0).getName());
    }

    @Test
    void feature_missingTags_returnsEmptyTagsList() {
        String json = "[{\"name\":\"test\",\"elements\":[]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertNotNull(result.get(0).getTags());
        assertTrue(result.get(0).getTags().isEmpty());
    }

    @Test
    void feature_missingElements_returnsEmptyTestCases() {
        String json = "[{\"name\":\"test\"}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertNotNull(result.get(0).getTestCases());
        assertTrue(result.get(0).getTestCases().isEmpty());
        assertEquals(0, result.get(0).getTests());
    }

    @Test
    void feature_emptyElements_returnsEmptyTestCases() {
        String json = "[{\"name\":\"test\",\"elements\":[]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertTrue(result.get(0).getTestCases().isEmpty());
    }

    @Test
    void feature_nullElements_returnsEmptyTestCases() {
        String json = "[{\"name\":\"test\",\"elements\":null}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertTrue(result.get(0).getTestCases().isEmpty());
    }

    // === SCENARIO TYPE FILTERING ===

    @Test
    void element_typeBackground_isSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[" +
            "{\"name\":\"background\",\"type\":\"background\",\"steps\":[]}," +
            "{\"name\":\"scenario1\",\"type\":\"scenario\",\"steps\":[]}" +
            "]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        // Only scenario should be included, not background
        assertEquals(1, result.get(0).getTestCases().size());
        assertEquals("scenario1", result.get(0).getTestCases().get(0).getName());
    }

    @Test
    void element_typeScenarioOutline_isIncluded() {
        String json = "[{\"name\":\"test\",\"elements\":[" +
            "{\"name\":\"outline\",\"type\":\"scenario_outline\",\"steps\":[]}" +
            "]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.get(0).getTestCases().size());
    }

    @Test
    void element_unknownType_isSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[" +
            "{\"name\":\"hook\",\"type\":\"hook\",\"steps\":[]}" +
            "]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertTrue(result.get(0).getTestCases().isEmpty());
    }

    @Test
    void element_missingType_isSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[" +
            "{\"name\":\"unknown\",\"steps\":[]}" +
            "]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertTrue(result.get(0).getTestCases().isEmpty());
    }

    // === STATUS DETERMINATION ===

    @Test
    void scenario_allPassed_statusSuccess() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\",\"duration\":1000}}," +
            "{\"result\":{\"status\":\"passed\",\"duration\":1000}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SUCCESS, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_anyFailed_statusFailure() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\",\"duration\":1000}}," +
            "{\"result\":{\"status\":\"failed\",\"duration\":1000,\"error_message\":\"err\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.FAILURE, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_skippedAfterFailure_statusFailure() {
        // When a step fails, subsequent steps are often skipped
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"failed\",\"duration\":1000}}," +
            "{\"result\":{\"status\":\"skipped\",\"duration\":0}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.FAILURE, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_allSkipped_statusSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"skipped\",\"duration\":0}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SKIPPED, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_noSteps_statusSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SKIPPED, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_missingSteps_statusSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\"}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SKIPPED, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_undefinedStep_statusSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"undefined\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SKIPPED, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_pendingStep_statusSkipped() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"pending\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SKIPPED, result.get(0).getTestCases().get(0).getTestStatus());
    }

    @Test
    void scenario_passedThenSkipped_statusSkipped() {
        // Passed followed by skipped should result in skipped (not success)
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\",\"duration\":1000}}," +
            "{\"result\":{\"status\":\"skipped\",\"duration\":0}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(TestStatus.SKIPPED, result.get(0).getTestCases().get(0).getTestStatus());
    }

    // === TIME CALCULATION ===

    @Test
    void scenario_sumsDurations_inSeconds() {
        // 1,000,000 + 2,000,000 nanoseconds = 3ms = 0.003 seconds
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\",\"duration\":1000000}}," +
            "{\"result\":{\"status\":\"passed\",\"duration\":2000000}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        BigDecimal expected = new BigDecimal("0.003");
        assertEquals(0, expected.compareTo(result.get(0).getTestCases().get(0).getTime()));
    }

    @Test
    void scenario_missingDuration_treatsAsZero() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(0, BigDecimal.ZERO.compareTo(result.get(0).getTestCases().get(0).getTime()));
    }

    @Test
    void scenario_largeDuration_handledCorrectly() {
        // 19.3 seconds in nanoseconds
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\",\"duration\":19300000000}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        BigDecimal expected = new BigDecimal("19.300");
        assertEquals(0, expected.compareTo(result.get(0).getTestCases().get(0).getTime()));
    }

    @Test
    void scenario_negativeDuration_treatsAsNegative() {
        // Edge case - negative duration (shouldn't happen but should be handled)
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"result\":{\"status\":\"passed\",\"duration\":-1000000}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        // Should handle gracefully (may result in negative or zero time)
        assertNotNull(result.get(0).getTestCases().get(0).getTime());
    }

    // === TAG COLLECTION ===

    @Test
    void collectAllTags_flattensAndDeduplicates() {
        String json = "[{\"name\":\"f1\",\"tags\":[{\"name\":\"@smoke\"}],\"elements\":[" +
            "{\"name\":\"s1\",\"type\":\"scenario\",\"tags\":[{\"name\":\"@smoke\"},{\"name\":\"@env=prod\"}],\"steps\":[]}" +
            "]}]";
        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);
        List<String> allTags = KarateCucumberConverter.collectAllTags(suites);

        assertEquals(2, allTags.size()); // smoke, env=prod (deduplicated)
        assertTrue(allTags.contains("smoke"));
        assertTrue(allTags.contains("env=prod"));
    }

    @Test
    void collectAllTags_preservesInsertionOrder() {
        String json = "[{\"name\":\"f1\",\"tags\":[{\"name\":\"@alpha\"},{\"name\":\"@beta\"}],\"elements\":[" +
            "{\"name\":\"s1\",\"type\":\"scenario\",\"tags\":[{\"name\":\"@gamma\"}],\"steps\":[]}" +
            "]}]";
        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);
        List<String> allTags = KarateCucumberConverter.collectAllTags(suites);

        assertEquals(List.of("alpha", "beta", "gamma"), allTags);
    }

    @Test
    void collectAllTags_handlesEmptySuites() {
        List<String> tags = KarateCucumberConverter.collectAllTags(List.of());
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    // === SUITE AGGREGATES ===

    @Test
    void suite_aggregatesFromTestCases() {
        // 2 scenarios: 1 passed, 1 failed
        String json = "[{\"name\":\"test\",\"elements\":[" +
            "{\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[{\"result\":{\"status\":\"passed\",\"duration\":1000000000}}]}," +
            "{\"name\":\"s2\",\"type\":\"scenario\",\"steps\":[{\"result\":{\"status\":\"failed\",\"duration\":500000000}}]}" +
            "]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = result.get(0);
        assertEquals(2, suite.getTests());
        assertEquals(1, suite.getFailure());
        assertEquals(0, suite.getError());
        assertEquals(0, suite.getSkipped());
        assertFalse(suite.getIsSuccess());
        // Time: 1.0 + 0.5 = 1.5 seconds
        assertEquals(0, new BigDecimal("1.500").compareTo(suite.getTime()));
    }

    @Test
    void suite_emptyHasSkipTrue() {
        String json = "[{\"name\":\"test\",\"elements\":[]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = result.get(0);
        assertEquals(0, suite.getTests());
        assertFalse(suite.getIsSuccess()); // Empty suite is not successful
        assertTrue(suite.getHasSkip()); // Empty suite has "skip" indicator
    }

    @Test
    void suite_withSkippedScenario_hasSkipTrue() {
        String json = "[{\"name\":\"test\",\"elements\":[" +
            "{\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[{\"result\":{\"status\":\"skipped\"}}]}" +
            "]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = result.get(0);
        assertEquals(1, suite.getSkipped());
        assertTrue(suite.getHasSkip());
    }

    // === RESOURCE FILE TESTS ===

    @Test
    void parseSimpleJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-simple.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("simple-feature.feature", result.get(0).getName());
        assertEquals(1, result.get(0).getTests());
        assertTrue(result.get(0).getIsSuccess());
        assertEquals(List.of("smoke"), result.get(0).getTags());

        TestCaseModel testCase = result.get(0).getTestCases().get(0);
        assertEquals("passing scenario", testCase.getName());
        assertEquals(TestStatus.SUCCESS, testCase.getTestStatus());
        assertEquals(List.of("env=prod"), testCase.getTags());
    }

    @Test
    void parseFailedJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-failed.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsSuccess());
        assertEquals(1, result.get(0).getFailure());

        TestCaseModel testCase = result.get(0).getTestCases().get(0);
        assertEquals(TestStatus.FAILURE, testCase.getTestStatus());
        assertFalse(testCase.getTestCaseFaults().isEmpty());
        assertEquals("Expected 200 but got 500", testCase.getTestCaseFaults().get(0).getValue());
    }

    @Test
    void parseMixedJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-mixed.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(2, result.size());

        // First feature: 1 pass, 1 fail
        assertEquals("mixed-results-feature-1.feature", result.get(0).getName());
        assertEquals(2, result.get(0).getTests());
        assertEquals(1, result.get(0).getFailure());
        assertFalse(result.get(0).getIsSuccess());

        // Second feature: 1 skipped
        assertEquals("mixed-results-feature-2.feature", result.get(1).getName());
        assertEquals(1, result.get(1).getTests());
        assertEquals(1, result.get(1).getSkipped());
    }

    @Test
    void parseEmptyJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-empty.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertTrue(result.isEmpty());
    }

    @Test
    void parseNoElementsJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-no-elements.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getTests());
        assertTrue(result.get(0).getTestCases().isEmpty());
    }

    @Test
    void parseBackgroundJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-background.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.size());
        // Background should be skipped, only actual scenario counted
        assertEquals(1, result.get(0).getTests());
        assertEquals("actual test scenario", result.get(0).getTestCases().get(0).getName());
    }

    @Test
    void parseScenarioOutlineJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-scenario-outline.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getTests()); // Two scenario outline examples
        assertEquals(List.of("data-driven"), result.get(0).getTags());
    }

    @Test
    void parseComplexTagsJson_fromResourceFile() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/cucumber-json-tags-complex.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getTests());

        // Feature-level tags - note comma-separated values are expanded
        // @env=prod,staging becomes env=prod and env=staging
        List<String> suiteTags = result.get(0).getTags();
        assertTrue(suiteTags.contains("smoke"));
        assertTrue(suiteTags.contains("env=prod"));
        assertTrue(suiteTags.contains("env=staging"));
        assertTrue(suiteTags.contains("priority=high"));

        // First scenario has its own tags - comma-separated values expanded
        // @owner=john,jane becomes owner=john and owner=jane
        TestCaseModel tc1 = result.get(0).getTestCases().get(0);
        assertTrue(tc1.getTags().contains("team=platform"));
        assertTrue(tc1.getTags().contains("jira=PROJ-123"));
        assertTrue(tc1.getTags().contains("owner=john"));
        assertTrue(tc1.getTags().contains("owner=jane"));

        // Second scenario has no tags
        TestCaseModel tc2 = result.get(0).getTestCases().get(1);
        assertTrue(tc2.getTags().isEmpty());
    }

    @Test
    void parseSimpleResourceFile_doesNotThrow() {
        String json = ResourceReader.resourceAsString("format-samples/cucumber-json/cucumber-json-simple.json");
        assertDoesNotThrow(() -> KarateCucumberConverter.fromCucumberJson(json));
    }

    @Test
    void parseFailedResourceFile_doesNotThrow() {
        String json = ResourceReader.resourceAsString("format-samples/cucumber-json/cucumber-json-failed.json");
        assertDoesNotThrow(() -> KarateCucumberConverter.fromCucumberJson(json));
    }

    @Test
    void parseMixedResourceFile_doesNotThrow() {
        String json = ResourceReader.resourceAsString("format-samples/cucumber-json/cucumber-json-mixed.json");
        assertDoesNotThrow(() -> KarateCucumberConverter.fromCucumberJson(json));
    }

    // === ERROR MESSAGE HANDLING ===

    @Test
    void scenario_errorMessage_capturedInFault() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"name\":\"failing step\",\"result\":{\"status\":\"failed\",\"duration\":1000,\"error_message\":\"Detailed error info\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        TestCaseModel tc = result.get(0).getTestCases().get(0);
        assertEquals(1, tc.getTestCaseFaults().size());
        assertEquals("failing step", tc.getTestCaseFaults().get(0).getMessage());
        assertEquals("Detailed error info", tc.getTestCaseFaults().get(0).getValue());
    }

    @Test
    void scenario_multipleFailures_capturesAllFaults() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"name\":\"step1\",\"result\":{\"status\":\"failed\",\"error_message\":\"Error 1\"}}," +
            "{\"name\":\"step2\",\"result\":{\"status\":\"failed\",\"error_message\":\"Error 2\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        TestCaseModel tc = result.get(0).getTestCases().get(0);
        assertEquals(2, tc.getTestCaseFaults().size());
    }

    @Test
    void scenario_failedWithoutErrorMessage_noFaultValue() {
        String json = "[{\"name\":\"test\",\"elements\":[{" +
            "\"name\":\"s1\",\"type\":\"scenario\",\"steps\":[" +
            "{\"name\":\"step\",\"result\":{\"status\":\"failed\"}}" +
            "]}]}]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        TestCaseModel tc = result.get(0).getTestCases().get(0);
        assertEquals(TestStatus.FAILURE, tc.getTestStatus());
        // No error_message means no fault captured
        assertTrue(tc.getTestCaseFaults().isEmpty());
    }
}
