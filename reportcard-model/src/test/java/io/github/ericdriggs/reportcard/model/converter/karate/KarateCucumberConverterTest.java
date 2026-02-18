package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KarateCucumberConverter.
 * Tests conversion from Karate/Cucumber JSON to TestSuiteModel/TestCaseModel.
 */
class KarateCucumberConverterTest {

    @Test
    void testFromCucumberJson_null() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCucumberJson_empty() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCucumberJson_blank() {
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson("   ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCucumberJson_notArray() {
        String json = "{\"name\": \"not an array\"}";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCucumberJson_emptyArray() {
        String json = "[]";
        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCucumberJson_singleFeatureWithOnePassingScenario() {
        String json = """
            [
              {
                "name": "test-feature.feature",
                "tags": [{"name": "@smoke"}],
                "elements": [
                  {
                    "name": "passing scenario",
                    "type": "scenario",
                    "tags": [{"name": "@priority=high"}],
                    "steps": [
                      {"result": {"status": "passed", "duration": 1000000000}},
                      {"result": {"status": "passed", "duration": 500000000}}
                    ]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        assertNotNull(suites);
        assertEquals(1, suites.size());

        TestSuiteModel suite = suites.get(0);
        assertEquals("test-feature.feature", suite.getName());
        assertEquals(List.of("smoke"), suite.getTags());
        assertEquals(1, suite.getTests());
        assertEquals(0, suite.getError());
        assertEquals(0, suite.getFailure());
        assertEquals(0, suite.getSkipped());
        assertTrue(suite.getIsSuccess());
        assertFalse(suite.getHasSkip());
        assertEquals(new BigDecimal("1.500"), suite.getTime());

        assertEquals(1, suite.getTestCases().size());
        TestCaseModel testCase = suite.getTestCases().get(0);
        assertEquals("passing scenario", testCase.getName());
        assertEquals(List.of("priority=high"), testCase.getTags());
        assertEquals(TestStatus.SUCCESS, testCase.getTestStatus());
        assertEquals(new BigDecimal("1.500"), testCase.getTime());
        assertTrue(testCase.getTestCaseFaults().isEmpty());
    }

    @Test
    void testFromCucumberJson_scenarioWithFailedStep() {
        String json = """
            [
              {
                "name": "failing.feature",
                "elements": [
                  {
                    "name": "failing scenario",
                    "type": "scenario",
                    "tags": [],
                    "steps": [
                      {"name": "Given setup", "result": {"status": "passed", "duration": 100000000}},
                      {"name": "When action", "result": {"status": "failed", "duration": 50000000, "error_message": "Expected 200 but got 500"}},
                      {"name": "Then verify", "result": {"status": "skipped", "duration": 0}}
                    ]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, suites.size());
        TestSuiteModel suite = suites.get(0);
        assertEquals("failing.feature", suite.getName());
        assertEquals(1, suite.getTests());
        assertEquals(0, suite.getError());
        assertEquals(1, suite.getFailure());
        assertEquals(0, suite.getSkipped());
        assertFalse(suite.getIsSuccess());
        assertFalse(suite.getHasSkip());

        TestCaseModel testCase = suite.getTestCases().get(0);
        assertEquals("failing scenario", testCase.getName());
        assertEquals(TestStatus.FAILURE, testCase.getTestStatus());
        assertEquals(new BigDecimal("0.150"), testCase.getTime());
        assertEquals(1, testCase.getTestCaseFaults().size());
        assertEquals("Expected 200 but got 500", testCase.getTestCaseFaults().get(0).getValue());
    }

    @Test
    void testFromCucumberJson_allSkippedScenario() {
        String json = """
            [
              {
                "name": "skipped.feature",
                "elements": [
                  {
                    "name": "skipped scenario",
                    "type": "scenario",
                    "tags": [],
                    "steps": [
                      {"result": {"status": "skipped"}},
                      {"result": {"status": "skipped"}}
                    ]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = suites.get(0);
        assertEquals(1, suite.getSkipped());
        assertTrue(suite.getHasSkip());

        TestCaseModel testCase = suite.getTestCases().get(0);
        assertEquals(TestStatus.SKIPPED, testCase.getTestStatus());
        assertEquals(0, testCase.getTime().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testFromCucumberJson_emptyFeature() {
        String json = """
            [
              {
                "name": "empty.feature",
                "tags": [{"name": "@empty"}],
                "elements": []
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(1, suites.size());
        TestSuiteModel suite = suites.get(0);
        assertEquals("empty.feature", suite.getName());
        assertEquals(List.of("empty"), suite.getTags());
        assertEquals(0, suite.getTests());
        assertTrue(suite.getTestCases().isEmpty());
        assertFalse(suite.getIsSuccess());
        assertTrue(suite.getHasSkip());
    }

    @Test
    void testFromCucumberJson_featureWithBackgroundElement() {
        String json = """
            [
              {
                "name": "background.feature",
                "elements": [
                  {
                    "name": "Background",
                    "type": "background",
                    "steps": [{"result": {"status": "passed"}}]
                  },
                  {
                    "name": "actual scenario",
                    "type": "scenario",
                    "steps": [{"result": {"status": "passed", "duration": 200000000}}]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = suites.get(0);
        // Background should be skipped, only scenario counted
        assertEquals(1, suite.getTests());
        assertEquals(1, suite.getTestCases().size());
        assertEquals("actual scenario", suite.getTestCases().get(0).getName());
    }

    @Test
    void testFromCucumberJson_scenarioOutline() {
        String json = """
            [
              {
                "name": "outline.feature",
                "elements": [
                  {
                    "name": "scenario outline example",
                    "type": "scenario_outline",
                    "tags": [{"name": "@outline"}],
                    "steps": [{"result": {"status": "passed", "duration": 100000000}}]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = suites.get(0);
        assertEquals(1, suite.getTests());
        TestCaseModel testCase = suite.getTestCases().get(0);
        assertEquals("scenario outline example", testCase.getName());
        assertEquals(List.of("outline"), testCase.getTags());
    }

    @Test
    void testFromCucumberJson_multipleFeatures() {
        String json = """
            [
              {
                "name": "feature1.feature",
                "elements": [
                  {
                    "name": "scenario1",
                    "type": "scenario",
                    "steps": [{"result": {"status": "passed", "duration": 100000000}}]
                  }
                ]
              },
              {
                "name": "feature2.feature",
                "elements": [
                  {
                    "name": "scenario2",
                    "type": "scenario",
                    "steps": [{"result": {"status": "passed", "duration": 200000000}}]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        assertEquals(2, suites.size());
        assertEquals("feature1.feature", suites.get(0).getName());
        assertEquals("feature2.feature", suites.get(1).getName());
    }

    @Test
    void testFromCucumberJson_missingDuration() {
        String json = """
            [
              {
                "name": "no-duration.feature",
                "elements": [
                  {
                    "name": "scenario without duration",
                    "type": "scenario",
                    "steps": [
                      {"result": {"status": "passed"}}
                    ]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestSuiteModel suite = suites.get(0);
        assertEquals(0, suite.getTime().compareTo(BigDecimal.ZERO));

        TestCaseModel testCase = suite.getTestCases().get(0);
        assertEquals(0, testCase.getTime().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testFromCucumberJson_noSteps() {
        String json = """
            [
              {
                "name": "no-steps.feature",
                "elements": [
                  {
                    "name": "scenario without steps",
                    "type": "scenario",
                    "tags": [],
                    "steps": []
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestCaseModel testCase = suites.get(0).getTestCases().get(0);
        assertEquals(TestStatus.SKIPPED, testCase.getTestStatus());
    }

    @Test
    void testFromCucumberJson_undefinedStep() {
        String json = """
            [
              {
                "name": "undefined.feature",
                "elements": [
                  {
                    "name": "scenario with undefined step",
                    "type": "scenario",
                    "steps": [
                      {"result": {"status": "undefined"}}
                    ]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestCaseModel testCase = suites.get(0).getTestCases().get(0);
        assertEquals(TestStatus.SKIPPED, testCase.getTestStatus());
    }

    @Test
    void testFromCucumberJson_pendingStep() {
        String json = """
            [
              {
                "name": "pending.feature",
                "elements": [
                  {
                    "name": "scenario with pending step",
                    "type": "scenario",
                    "steps": [
                      {"result": {"status": "pending"}}
                    ]
                  }
                ]
              }
            ]
            """;

        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(json);

        TestCaseModel testCase = suites.get(0).getTestCases().get(0);
        assertEquals(TestStatus.SKIPPED, testCase.getTestStatus());
    }

    @Test
    void testCollectAllTags_empty() {
        List<String> tags = KarateCucumberConverter.collectAllTags(List.of());
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    void testCollectAllTags_suiteTagsOnly() {
        TestSuiteModel suite = TestSuiteModel.builder()
            .tags(List.of("smoke", "regression"))
            .testCases(List.of())
            .build();

        List<String> tags = KarateCucumberConverter.collectAllTags(List.of(suite));
        assertEquals(2, tags.size());
        assertTrue(tags.contains("smoke"));
        assertTrue(tags.contains("regression"));
    }

    @Test
    void testCollectAllTags_testCaseTagsOnly() {
        TestCaseModel testCase = TestCaseModel.builder()
            .tags(List.of("priority=high"))
            .build();

        TestSuiteModel suite = TestSuiteModel.builder()
            .tags(List.of())
            .testCases(List.of(testCase))
            .build();

        List<String> tags = KarateCucumberConverter.collectAllTags(List.of(suite));
        assertEquals(1, tags.size());
        assertEquals("priority=high", tags.get(0));
    }

    @Test
    void testCollectAllTags_deduplication() {
        TestCaseModel testCase1 = TestCaseModel.builder()
            .tags(List.of("smoke", "priority=high"))
            .build();

        TestCaseModel testCase2 = TestCaseModel.builder()
            .tags(List.of("smoke", "priority=low"))
            .build();

        TestSuiteModel suite = TestSuiteModel.builder()
            .tags(List.of("smoke", "regression"))
            .testCases(List.of(testCase1, testCase2))
            .build();

        List<String> tags = KarateCucumberConverter.collectAllTags(List.of(suite));
        // smoke appears once despite being in suite + 2 test cases
        assertEquals(4, tags.size());
        assertTrue(tags.contains("smoke"));
        assertTrue(tags.contains("regression"));
        assertTrue(tags.contains("priority=high"));
        assertTrue(tags.contains("priority=low"));
    }

    @Test
    void testCollectAllTags_multipleSuites() {
        TestSuiteModel suite1 = TestSuiteModel.builder()
            .tags(List.of("smoke"))
            .testCases(List.of())
            .build();

        TestSuiteModel suite2 = TestSuiteModel.builder()
            .tags(List.of("regression"))
            .testCases(List.of())
            .build();

        List<String> tags = KarateCucumberConverter.collectAllTags(List.of(suite1, suite2));
        assertEquals(2, tags.size());
        assertTrue(tags.contains("smoke"));
        assertTrue(tags.contains("regression"));
    }

    @Test
    void testCollectAllTags_preservesOrder() {
        TestCaseModel testCase = TestCaseModel.builder()
            .tags(List.of("zebra"))
            .build();

        TestSuiteModel suite = TestSuiteModel.builder()
            .tags(List.of("apple", "banana"))
            .testCases(List.of(testCase))
            .build();

        List<String> tags = KarateCucumberConverter.collectAllTags(List.of(suite));
        // Should preserve insertion order (suite tags first, then test case tags)
        assertEquals(List.of("apple", "banana", "zebra"), tags);
    }

    @Test
    void testCollectAllTags_nullTagsHandled() {
        TestCaseModel testCase = TestCaseModel.builder()
            .tags(null)
            .build();

        TestSuiteModel suite = TestSuiteModel.builder()
            .tags(null)
            .testCases(List.of(testCase))
            .build();

        List<String> tags = KarateCucumberConverter.collectAllTags(List.of(suite));
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
}
