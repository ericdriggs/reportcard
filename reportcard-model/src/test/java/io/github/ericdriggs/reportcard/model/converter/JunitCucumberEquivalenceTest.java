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
 */
public class JunitCucumberEquivalenceTest {

    private static final String KARATE_REPORTS_PATH = "format-samples/karate-reports/";
    private static final String SAMPLE_TEST = "sample-api-test";

    @Test
    void testCount_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertEquals(1, junitResult.getTests());
        assertEquals(junitResult.getTests(), cucumberResult.getTests(),
            "Test count mismatch between JUnit and Cucumber");
    }

    @Test
    void failureCount_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertEquals(0, junitResult.getFailure());
        assertEquals(junitResult.getFailure(), cucumberResult.getFailure(),
            "Failure count mismatch between JUnit and Cucumber");
    }

    @Test
    void skipCount_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertEquals(0, junitResult.getSkipped());
        assertEquals(junitResult.getSkipped(), cucumberResult.getSkipped(),
            "Skip count mismatch between JUnit and Cucumber");
    }

    @Test
    void errorCount_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertEquals(0, junitResult.getError());
        assertEquals(junitResult.getError(), cucumberResult.getError(),
            "Error count mismatch between JUnit and Cucumber");
    }

    @Test
    void suiteCount_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertEquals(1, junitResult.getTestSuites().size());
        assertEquals(junitResult.getTestSuites().size(), cucumberResult.getTestSuites().size(),
            "Suite count mismatch between JUnit and Cucumber");
    }

    @Test
    void suiteName_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        String junitSuiteName = junitResult.getTestSuites().get(0).getName();
        String cucumberSuiteName = cucumberResult.getTestSuites().get(0).getName();

        assertEquals(junitSuiteName, cucumberSuiteName,
            "Suite name mismatch between JUnit and Cucumber");
    }

    @Test
    void testCaseName_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        String junitName = junitResult.getTestSuites().get(0).getTestCases().get(0).getName();
        String cucumberName = cucumberResult.getTestSuites().get(0).getTestCases().get(0).getName();

        assertEquals(junitName, cucumberName,
            "Test case name mismatch between JUnit and Cucumber");
    }

    @Test
    void isSuccess_matches() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertTrue(junitResult.getIsSuccess());
        assertEquals(junitResult.getIsSuccess(), cucumberResult.getIsSuccess(),
            "isSuccess mismatch between JUnit and Cucumber");
    }

    /**
     * Time parsing test - verifies both formats produce valid, non-negative times.
     * Times may differ slightly due to format precision differences.
     */
    @Test
    void time_bothParseable() {
        TestResultModel junitResult = parseJunit(SAMPLE_TEST);
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        assertNotNull(junitResult.getTime(), "JUnit time should be parsed");
        assertNotNull(cucumberResult.getTime(), "Cucumber time should be parsed");

        assertTrue(junitResult.getTime().compareTo(BigDecimal.ZERO) > 0,
            "JUnit time should be positive");
        assertTrue(cucumberResult.getTime().compareTo(BigDecimal.ZERO) > 0,
            "Cucumber time should be positive");
    }

    @Test
    void tags_extractedFromCucumber() {
        TestResultModel cucumberResult = parseCucumber(SAMPLE_TEST);

        TestSuiteModel suite = cucumberResult.getTestSuites().get(0);
        assertNotNull(suite.getTags(), "Suite should have tags");
        assertTrue(suite.getTags().contains("api"), "Suite should have @api tag");

        TestCaseModel testCase = suite.getTestCases().get(0);
        assertNotNull(testCase.getTags(), "Test case should have tags");
        assertTrue(testCase.getTags().contains("smoke"), "Test case should have @smoke tag");
        assertTrue(testCase.getTags().contains("auth"), "Test case should have @auth tag");
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
