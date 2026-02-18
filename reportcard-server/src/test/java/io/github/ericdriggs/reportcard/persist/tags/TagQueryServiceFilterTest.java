package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.model.TagQueryResponse.TestInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for tag filtering logic in TagQueryService.
 * Tests the extractMatchingTestInfos method via reflection since it's private.
 * Each test includes both matching and non-matching data to verify filtering.
 */
class TagQueryServiceFilterTest {

    @Test
    void extractMatchingTestInfos_suiteTagMatches_includesOnlyTestsInMatchingSuite() throws Exception {
        // Suite with matching tag AND suite without matching tag
        String json = """
            [{
                "name": "SmokeTests",
                "tags": ["smoke", "regression"],
                "testCases": [
                    {"name": "smokeTest1", "testStatus": "SUCCESS"},
                    {"name": "smokeTest2", "testStatus": "SUCCESS"}
                ]
            },
            {
                "name": "UnitTests",
                "tags": ["unit"],
                "testCases": [
                    {"name": "unitTest1", "testStatus": "SUCCESS"},
                    {"name": "unitTest2", "testStatus": "SUCCESS"}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include tests from smoke suite
        assertEquals(2, results.size(), "Only tests from matching suite should be included");
        assertTrue(results.stream().anyMatch(t -> "smokeTest1".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "smokeTest2".equals(t.getTestName())));

        // Should NOT include tests from unit suite
        assertFalse(results.stream().anyMatch(t -> "unitTest1".equals(t.getTestName())),
            "Non-matching suite tests should be excluded");
        assertFalse(results.stream().anyMatch(t -> "unitTest2".equals(t.getTestName())),
            "Non-matching suite tests should be excluded");
    }

    @Test
    void extractMatchingTestInfos_testCaseTagMatches_includesOnlyMatchingTests() throws Exception {
        // Suite without tags, tests with different tags
        String json = """
            [{
                "name": "MixedTests",
                "tags": [],
                "testCases": [
                    {"name": "smokeTest", "testStatus": "SUCCESS", "tags": ["smoke"]},
                    {"name": "unitTest", "testStatus": "SUCCESS", "tags": ["unit"]},
                    {"name": "integrationTest", "testStatus": "SUCCESS", "tags": ["integration"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include only smoke test
        assertEquals(1, results.size(), "Only test with matching tag should be included");
        assertEquals("smokeTest", results.get(0).getTestName());

        // Should NOT include other tests
        assertFalse(results.stream().anyMatch(t -> "unitTest".equals(t.getTestName())),
            "Non-matching test should be excluded");
        assertFalse(results.stream().anyMatch(t -> "integrationTest".equals(t.getTestName())),
            "Non-matching test should be excluded");
    }

    @Test
    void extractMatchingTestInfos_noTagsMatch_returnsEmptyExcludesAll() throws Exception {
        String json = """
            [{
                "name": "UnitTests",
                "tags": ["unit"],
                "testCases": [
                    {"name": "test1", "testStatus": "SUCCESS", "tags": ["unit"]},
                    {"name": "test2", "testStatus": "SUCCESS", "tags": ["integration"]}
                ]
            },
            {
                "name": "IntegrationTests",
                "tags": ["integration"],
                "testCases": [
                    {"name": "test3", "testStatus": "SUCCESS"}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        assertTrue(results.isEmpty(), "No tests should match when tag not present anywhere");
    }

    @Test
    void extractMatchingTestInfos_andExpression_requiresBothTagsExcludesPartial() throws Exception {
        String json = """
            [{
                "name": "Tests",
                "tags": [],
                "testCases": [
                    {"name": "hasBoth", "testStatus": "SUCCESS", "tags": ["smoke", "regression"]},
                    {"name": "hasSmokeOnly", "testStatus": "SUCCESS", "tags": ["smoke"]},
                    {"name": "hasRegressionOnly", "testStatus": "SUCCESS", "tags": ["regression"]},
                    {"name": "hasNeither", "testStatus": "SUCCESS", "tags": ["unit"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke AND regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include only test with both tags
        assertEquals(1, results.size(), "Only test with both tags should match");
        assertEquals("hasBoth", results.get(0).getTestName());

        // Should NOT include tests with only one tag or neither
        assertFalse(results.stream().anyMatch(t -> "hasSmokeOnly".equals(t.getTestName())),
            "Test with only smoke should be excluded");
        assertFalse(results.stream().anyMatch(t -> "hasRegressionOnly".equals(t.getTestName())),
            "Test with only regression should be excluded");
        assertFalse(results.stream().anyMatch(t -> "hasNeither".equals(t.getTestName())),
            "Test with neither tag should be excluded");
    }

    @Test
    void extractMatchingTestInfos_orExpression_matchesEitherTagExcludesNeither() throws Exception {
        String json = """
            [{
                "name": "Tests",
                "tags": [],
                "testCases": [
                    {"name": "hasSmoke", "testStatus": "SUCCESS", "tags": ["smoke"]},
                    {"name": "hasRegression", "testStatus": "SUCCESS", "tags": ["regression"]},
                    {"name": "hasBoth", "testStatus": "SUCCESS", "tags": ["smoke", "regression"]},
                    {"name": "hasUnit", "testStatus": "SUCCESS", "tags": ["unit"]},
                    {"name": "hasIntegration", "testStatus": "SUCCESS", "tags": ["integration"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke OR regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include tests with either or both tags
        assertEquals(3, results.size(), "Tests with either tag should match");
        assertTrue(results.stream().anyMatch(t -> "hasSmoke".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "hasRegression".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "hasBoth".equals(t.getTestName())));

        // Should NOT include tests with neither tag
        assertFalse(results.stream().anyMatch(t -> "hasUnit".equals(t.getTestName())),
            "Test with unit tag should be excluded");
        assertFalse(results.stream().anyMatch(t -> "hasIntegration".equals(t.getTestName())),
            "Test with integration tag should be excluded");
    }

    @Test
    void extractMatchingTestInfos_suiteTagInherited_matchesAllTestsExcludesOtherSuites() throws Exception {
        // Suite has the tag, individual tests don't - all tests in suite should match
        String json = """
            [{
                "name": "SmokeTestSuite",
                "tags": ["smoke"],
                "testCases": [
                    {"name": "inheritedTest1", "testStatus": "SUCCESS", "tags": []},
                    {"name": "inheritedTest2", "testStatus": "SUCCESS"}
                ]
            },
            {
                "name": "OtherSuite",
                "tags": ["other"],
                "testCases": [
                    {"name": "otherTest1", "testStatus": "SUCCESS"},
                    {"name": "otherTest2", "testStatus": "SUCCESS"}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include all tests from smoke suite
        assertEquals(2, results.size(), "All tests in suite with tag should be included");
        assertTrue(results.stream().anyMatch(t -> "inheritedTest1".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "inheritedTest2".equals(t.getTestName())));

        // Should NOT include tests from other suite
        assertFalse(results.stream().anyMatch(t -> "otherTest1".equals(t.getTestName())),
            "Tests from non-matching suite should be excluded");
        assertFalse(results.stream().anyMatch(t -> "otherTest2".equals(t.getTestName())),
            "Tests from non-matching suite should be excluded");
    }

    @Test
    void extractMatchingTestInfos_keyValueTag_matchesExactlyExcludesOtherValues() throws Exception {
        String json = """
            [{
                "name": "EnvTests",
                "tags": [],
                "testCases": [
                    {"name": "prodTest", "testStatus": "SUCCESS", "tags": ["env=prod"]},
                    {"name": "stagingTest", "testStatus": "SUCCESS", "tags": ["env=staging"]},
                    {"name": "devTest", "testStatus": "SUCCESS", "tags": ["env=dev"]},
                    {"name": "noEnvTest", "testStatus": "SUCCESS", "tags": ["smoke"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("env=prod");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include only prod test
        assertEquals(1, results.size(), "Only test with exact key=value tag should match");
        assertEquals("prodTest", results.get(0).getTestName());

        // Should NOT include tests with other values or no env tag
        assertFalse(results.stream().anyMatch(t -> "stagingTest".equals(t.getTestName())),
            "Test with env=staging should be excluded");
        assertFalse(results.stream().anyMatch(t -> "devTest".equals(t.getTestName())),
            "Test with env=dev should be excluded");
        assertFalse(results.stream().anyMatch(t -> "noEnvTest".equals(t.getTestName())),
            "Test without env tag should be excluded");
    }

    @Test
    void extractMatchingTestInfos_complexExpression_filtersCorrectly() throws Exception {
        // (smoke AND env=prod) OR regression
        String json = """
            [{
                "name": "Tests",
                "tags": [],
                "testCases": [
                    {"name": "smokeProd", "testStatus": "SUCCESS", "tags": ["smoke", "env=prod"]},
                    {"name": "smokeStaging", "testStatus": "SUCCESS", "tags": ["smoke", "env=staging"]},
                    {"name": "regressionOnly", "testStatus": "SUCCESS", "tags": ["regression"]},
                    {"name": "unitTest", "testStatus": "SUCCESS", "tags": ["unit"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("(smoke AND env=prod) OR regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Should include smokeProd (has smoke AND env=prod) and regressionOnly
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(t -> "smokeProd".equals(t.getTestName())),
            "Test with smoke AND env=prod should match");
        assertTrue(results.stream().anyMatch(t -> "regressionOnly".equals(t.getTestName())),
            "Test with regression should match");

        // Should NOT include smokeStaging (has smoke but wrong env) or unitTest
        assertFalse(results.stream().anyMatch(t -> "smokeStaging".equals(t.getTestName())),
            "Test with smoke but env=staging should be excluded");
        assertFalse(results.stream().anyMatch(t -> "unitTest".equals(t.getTestName())),
            "Test with only unit should be excluded");
    }

    @Test
    void extractMatchingTestInfos_andExpression_suiteLevel_requiresBothTags() throws Exception {
        String json = """
            [{
                "name": "SmokeRegressionSuite",
                "tags": ["smoke", "regression"],
                "testCases": [
                    {"name": "test1", "testStatus": "SUCCESS"}
                ]
            },
            {
                "name": "SmokeOnlySuite",
                "tags": ["smoke"],
                "testCases": [
                    {"name": "test2", "testStatus": "SUCCESS"}
                ]
            },
            {
                "name": "RegressionOnlySuite",
                "tags": ["regression"],
                "testCases": [
                    {"name": "test3", "testStatus": "SUCCESS"}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke AND regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        assertEquals(1, results.size(), "Only suite with both tags should match");
        assertEquals("test1", results.get(0).getTestName());

        assertFalse(results.stream().anyMatch(t -> "test2".equals(t.getTestName())),
            "Suite with only smoke should be excluded");
        assertFalse(results.stream().anyMatch(t -> "test3".equals(t.getTestName())),
            "Suite with only regression should be excluded");
    }

    @Test
    void extractMatchingTestInfos_orExpression_suiteLevel_matchesEither() throws Exception {
        String json = """
            [{
                "name": "SmokeSuite",
                "tags": ["smoke"],
                "testCases": [
                    {"name": "smokeTest", "testStatus": "SUCCESS"}
                ]
            },
            {
                "name": "RegressionSuite",
                "tags": ["regression"],
                "testCases": [
                    {"name": "regressionTest", "testStatus": "SUCCESS"}
                ]
            },
            {
                "name": "UnitSuite",
                "tags": ["unit"],
                "testCases": [
                    {"name": "unitTest", "testStatus": "SUCCESS"}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke OR regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        assertEquals(2, results.size(), "Suites with either tag should match");
        assertTrue(results.stream().anyMatch(t -> "smokeTest".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "regressionTest".equals(t.getTestName())));

        assertFalse(results.stream().anyMatch(t -> "unitTest".equals(t.getTestName())),
            "Suite with neither tag should be excluded");
    }

    @Test
    void extractMatchingTestInfos_nestedAndOr_filtersCorrectly() throws Exception {
        // (smoke AND env=prod) OR (regression AND env=staging)
        String json = """
            [{
                "name": "Tests",
                "tags": [],
                "testCases": [
                    {"name": "smokeProd", "testStatus": "SUCCESS", "tags": ["smoke", "env=prod"]},
                    {"name": "smokeStaging", "testStatus": "SUCCESS", "tags": ["smoke", "env=staging"]},
                    {"name": "regressionProd", "testStatus": "SUCCESS", "tags": ["regression", "env=prod"]},
                    {"name": "regressionStaging", "testStatus": "SUCCESS", "tags": ["regression", "env=staging"]},
                    {"name": "unitTest", "testStatus": "SUCCESS", "tags": ["unit"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("(smoke AND env=prod) OR (regression AND env=staging)");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(t -> "smokeProd".equals(t.getTestName())),
            "smoke AND env=prod should match");
        assertTrue(results.stream().anyMatch(t -> "regressionStaging".equals(t.getTestName())),
            "regression AND env=staging should match");

        assertFalse(results.stream().anyMatch(t -> "smokeStaging".equals(t.getTestName())),
            "smoke AND env=staging should NOT match");
        assertFalse(results.stream().anyMatch(t -> "regressionProd".equals(t.getTestName())),
            "regression AND env=prod should NOT match");
        assertFalse(results.stream().anyMatch(t -> "unitTest".equals(t.getTestName())),
            "unit should NOT match");
    }

    @Test
    void extractMatchingTestInfos_mixedSuiteAndTestTags_andExpression() throws Exception {
        // Suite has "smoke", test needs to have "regression" for AND to match
        String json = """
            [{
                "name": "SmokeSuite",
                "tags": ["smoke"],
                "testCases": [
                    {"name": "smokeRegression", "testStatus": "SUCCESS", "tags": ["regression"]},
                    {"name": "smokeOnly", "testStatus": "SUCCESS", "tags": []},
                    {"name": "smokeUnit", "testStatus": "SUCCESS", "tags": ["unit"]}
                ]
            },
            {
                "name": "OtherSuite",
                "tags": ["other"],
                "testCases": [
                    {"name": "otherRegression", "testStatus": "SUCCESS", "tags": ["regression"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke AND regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // smokeRegression: suite has smoke, test has regression -> combined = smoke + regression -> matches
        // smokeOnly: suite has smoke, test has nothing -> combined = smoke -> doesn't match (needs both)
        // smokeUnit: suite has smoke, test has unit -> combined = smoke + unit -> doesn't match
        // otherRegression: suite has other, test has regression -> combined = other + regression -> doesn't match

        assertEquals(1, results.size(), "Only test with combined suite+test tags matching AND should match");
        assertEquals("smokeRegression", results.get(0).getTestName());
    }

    @Test
    void extractMatchingTestInfos_mixedSuiteAndTestTags_orExpression() throws Exception {
        String json = """
            [{
                "name": "SmokeSuite",
                "tags": ["smoke"],
                "testCases": [
                    {"name": "smokeTest", "testStatus": "SUCCESS", "tags": []}
                ]
            },
            {
                "name": "UnitSuite",
                "tags": ["unit"],
                "testCases": [
                    {"name": "unitWithRegression", "testStatus": "SUCCESS", "tags": ["regression"]},
                    {"name": "unitOnly", "testStatus": "SUCCESS", "tags": []}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke OR regression");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // smokeTest: suite has smoke -> matches OR
        // unitWithRegression: test has regression -> matches OR
        // unitOnly: neither suite nor test has smoke or regression -> doesn't match

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(t -> "smokeTest".equals(t.getTestName())),
            "Test in suite with smoke should match");
        assertTrue(results.stream().anyMatch(t -> "unitWithRegression".equals(t.getTestName())),
            "Test with regression tag should match");

        assertFalse(results.stream().anyMatch(t -> "unitOnly".equals(t.getTestName())),
            "Test with neither tag should be excluded");
    }

    @Test
    void extractMatchingTestInfos_nullJson_returnsEmpty() throws Exception {
        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(null, expr);

        assertTrue(results.isEmpty());
    }

    @Test
    void extractMatchingTestInfos_emptyJson_returnsEmpty() throws Exception {
        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos("", expr);

        assertTrue(results.isEmpty());
    }

    @Test
    void extractMatchingTestInfos_emptyArray_returnsEmpty() throws Exception {
        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos("[]", expr);

        assertTrue(results.isEmpty());
    }

    @Test
    void extractMatchingTestInfos_noTagsInJson_returnsAllTests() throws Exception {
        // When JSON has no tags anywhere, return all tests
        // (the tag match was at test_result level, not in the JSON)
        String json = """
            [{
                "name": "TestSuite",
                "tags": [],
                "testCases": [
                    {"name": "test1", "testStatus": "SUCCESS", "tags": []},
                    {"name": "test2", "testStatus": "SUCCESS"},
                    {"name": "test3", "testStatus": "FAILURE", "tags": []}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // All tests should be included since JSON has no tags
        assertEquals(3, results.size(), "All tests should be returned when JSON has no tags");
        assertTrue(results.stream().anyMatch(t -> "test1".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "test2".equals(t.getTestName())));
        assertTrue(results.stream().anyMatch(t -> "test3".equals(t.getTestName())));
    }

    @Test
    void extractMatchingTestInfos_someTagsInJson_filtersNormally() throws Exception {
        // When JSON has SOME tags, filtering should apply normally
        String json = """
            [{
                "name": "TestSuite",
                "tags": [],
                "testCases": [
                    {"name": "smokeTest", "testStatus": "SUCCESS", "tags": ["smoke"]},
                    {"name": "noTagTest", "testStatus": "SUCCESS", "tags": []},
                    {"name": "unitTest", "testStatus": "SUCCESS", "tags": ["unit"]}
                ]
            }]
            """;

        TagExpr expr = TagExpressionParser.parse("smoke");
        List<TestInfo> results = invokeExtractMatchingTestInfos(json, expr);

        // Only smoke test should match since JSON has tags
        assertEquals(1, results.size(), "Only matching tests should be returned when JSON has tags");
        assertEquals("smokeTest", results.get(0).getTestName());
    }

    /**
     * Invokes the private extractMatchingTestInfos method via reflection.
     */
    @SuppressWarnings("unchecked")
    private List<TestInfo> invokeExtractMatchingTestInfos(String json, TagExpr expr) throws Exception {
        // Create a TagQueryService instance with null DSL (not needed for this method)
        TagQueryService service = new TagQueryService(null);

        Method method = TagQueryService.class.getDeclaredMethod(
            "extractMatchingTestInfos", String.class, TagExpr.class);
        method.setAccessible(true);

        return (List<TestInfo>) method.invoke(service, json, expr);
    }
}
