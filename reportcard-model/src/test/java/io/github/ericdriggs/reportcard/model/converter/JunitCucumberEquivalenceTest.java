package io.github.ericdriggs.reportcard.model.converter;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateCucumberConverter;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cross-format equivalence tests.
 * Verifies that parsing JUnit XML and Cucumber JSON from the same test run
 * produces equivalent TestResultModel outputs.
 *
 * <p>This catches drift between parsers and ensures consistency when
 * Karate produces both JUnit XML and its own JSON format.
 *
 * <p>Test data:
 * <ul>
 *   <li>delorean-create-dss-test: 1 test, 19.3s, single scenario</li>
 *   <li>cache-and-load-accounts-test: 2 tests, 0.004s, two scenarios</li>
 * </ul>
 */
public class JunitCucumberEquivalenceTest {

    private static final String KARATE_REPORTS_PATH = "format-samples/karate-reports/";

    // === DELOREAN TEST (1 test, 19.3s) ===

    @Test
    void delorean_testCount_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertEquals(1, junitResult.getTests());
        assertEquals(junitResult.getTests(), cucumberResult.getTests(),
            "Test count mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_failureCount_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertEquals(0, junitResult.getFailure());
        assertEquals(junitResult.getFailure(), cucumberResult.getFailure(),
            "Failure count mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_skipCount_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertEquals(0, junitResult.getSkipped());
        assertEquals(junitResult.getSkipped(), cucumberResult.getSkipped(),
            "Skip count mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_errorCount_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertEquals(0, junitResult.getError());
        assertEquals(junitResult.getError(), cucumberResult.getError(),
            "Error count mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_suiteCount_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertEquals(1, junitResult.getTestSuites().size());
        assertEquals(junitResult.getTestSuites().size(), cucumberResult.getTestSuites().size(),
            "Suite count mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_suiteName_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        String junitSuiteName = junitResult.getTestSuites().get(0).getName();
        String cucumberSuiteName = cucumberResult.getTestSuites().get(0).getName();

        assertEquals(junitSuiteName, cucumberSuiteName,
            "Suite name mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_testCaseName_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        String junitName = junitResult.getTestSuites().get(0).getTestCases().get(0).getName();
        String cucumberName = cucumberResult.getTestSuites().get(0).getTestCases().get(0).getName();

        assertEquals(junitName, cucumberName,
            "Test case name mismatch between JUnit and Cucumber");
    }

    @Test
    void delorean_isSuccess_matches() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertTrue(junitResult.getIsSuccess());
        assertEquals(junitResult.getIsSuccess(), cucumberResult.getIsSuccess(),
            "isSuccess mismatch between JUnit and Cucumber");
    }

    /**
     * Time parsing test - verifies both formats produce valid, non-negative times.
     *
     * <p>NOTE: JUnit and Cucumber times are NOT expected to match because:
     * <ul>
     *   <li>JUnit reports wall clock time for the test (~19.3s)</li>
     *   <li>Cucumber sums ALL step durations including nested feature calls (~57.8s)</li>
     * </ul>
     * This is a known behavioral difference between formats.
     */
    @Test
    void delorean_time_bothParseable() {
        TestResultModel junitResult = parseJunit("delorean-create-dss-test");
        TestResultModel cucumberResult = parseCucumber("delorean-create-dss-test");

        assertNotNull(junitResult.getTime(), "JUnit time should be parsed");
        assertNotNull(cucumberResult.getTime(), "Cucumber time should be parsed");

        // Both should be positive
        assertTrue(junitResult.getTime().compareTo(BigDecimal.ZERO) > 0,
            "JUnit time should be positive");
        assertTrue(cucumberResult.getTime().compareTo(BigDecimal.ZERO) > 0,
            "Cucumber time should be positive");

        // JUnit reports ~19.3s wall clock time
        assertTrue(junitResult.getTime().compareTo(new BigDecimal("15")) > 0,
            "JUnit time should be > 15 seconds");

        // Cucumber sums step durations (expected to be higher due to nested calls)
        assertTrue(cucumberResult.getTime().compareTo(junitResult.getTime()) >= 0,
            "Cucumber step duration sum should be >= JUnit wall clock time");
    }

    // === CACHE-AND-LOAD TEST (2 tests, 0.004s) ===

    @Test
    void cacheAndLoad_testCount_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertEquals(2, junitResult.getTests());
        assertEquals(junitResult.getTests(), cucumberResult.getTests(),
            "Test count mismatch between JUnit and Cucumber");
    }

    @Test
    void cacheAndLoad_failureCount_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertEquals(0, junitResult.getFailure());
        assertEquals(junitResult.getFailure(), cucumberResult.getFailure(),
            "Failure count mismatch between JUnit and Cucumber");
    }

    @Test
    void cacheAndLoad_skipCount_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertEquals(0, junitResult.getSkipped());
        assertEquals(junitResult.getSkipped(), cucumberResult.getSkipped(),
            "Skip count mismatch between JUnit and Cucumber");
    }

    @Test
    void cacheAndLoad_errorCount_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertEquals(0, junitResult.getError());
        assertEquals(junitResult.getError(), cucumberResult.getError(),
            "Error count mismatch between JUnit and Cucumber");
    }

    @Test
    void cacheAndLoad_suiteCount_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertEquals(1, junitResult.getTestSuites().size());
        assertEquals(junitResult.getTestSuites().size(), cucumberResult.getTestSuites().size(),
            "Suite count mismatch between JUnit and Cucumber");
    }

    @Test
    void cacheAndLoad_suiteName_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        String junitSuiteName = junitResult.getTestSuites().get(0).getName();
        String cucumberSuiteName = cucumberResult.getTestSuites().get(0).getName();

        assertEquals(junitSuiteName, cucumberSuiteName,
            "Suite name mismatch between JUnit and Cucumber");
    }

    @Test
    void cacheAndLoad_testCaseNames_match() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        List<String> junitNames = junitResult.getTestSuites().get(0).getTestCases().stream()
            .map(TestCaseModel::getName)
            .toList();

        List<String> cucumberNames = cucumberResult.getTestSuites().get(0).getTestCases().stream()
            .map(TestCaseModel::getName)
            .toList();

        assertEquals(junitNames.size(), cucumberNames.size(),
            "Test case count mismatch");

        // Both test cases have same name in this test file
        for (String junitName : junitNames) {
            assertTrue(cucumberNames.contains(junitName),
                "Test case '" + junitName + "' from JUnit not found in Cucumber");
        }
    }

    @Test
    void cacheAndLoad_isSuccess_matches() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertTrue(junitResult.getIsSuccess());
        assertEquals(junitResult.getIsSuccess(), cucumberResult.getIsSuccess(),
            "isSuccess mismatch between JUnit and Cucumber");
    }

    /**
     * Time parsing test - verifies both formats produce valid, non-negative times.
     * For short tests, we verify both are parseable and reasonable.
     */
    @Test
    void cacheAndLoad_time_bothParseable() {
        TestResultModel junitResult = parseJunit("cache-and-load-accounts-test");
        TestResultModel cucumberResult = parseCucumber("cache-and-load-accounts-test");

        assertNotNull(junitResult.getTime(), "JUnit time should be parsed");
        assertNotNull(cucumberResult.getTime(), "Cucumber time should be parsed");

        // Both should be non-negative
        assertTrue(junitResult.getTime().compareTo(BigDecimal.ZERO) >= 0,
            "JUnit time should be non-negative");
        assertTrue(cucumberResult.getTime().compareTo(BigDecimal.ZERO) >= 0,
            "Cucumber time should be non-negative");
    }

    // === HELPER METHODS ===

    private TestResultModel parseJunit(String baseName) {
        String junitXml = ResourceReader.resourceAsString(KARATE_REPORTS_PATH + baseName + ".xml");
        List<TestSuiteModel> suites = JunitConvertersUtil.fromXmlContents(junitXml);
        return new TestResultModel(suites);
    }

    private TestResultModel parseCucumber(String baseName) {
        String cucumberJson = ResourceReader.resourceAsString(KARATE_REPORTS_PATH + baseName + ".json");
        List<TestSuiteModel> suites = KarateCucumberConverter.fromCucumberJson(cucumberJson);
        return new TestResultModel(suites);
    }
}
