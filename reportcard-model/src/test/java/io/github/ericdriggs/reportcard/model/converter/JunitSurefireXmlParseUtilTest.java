package io.github.ericdriggs.reportcard.model.converter;

import io.github.ericdriggs.reportcard.model.TestCaseFaultModel;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JunitSurefireXmlParseUtilTest {

    @Test
    void mixedJunitSurefireTest() {
        String sureFireTestSuiteXml = ResourceReader.resourceAsString("format-samples/sample-surefire.xml");
        String junitTestSuiteXml = ResourceReader.resourceAsString("format-samples/sample-junit-small.xml");
        String junitTestSuitesXml = ResourceReader.resourceAsString("format-samples/sample-junit-testsuites-small.xml");

        List<String> testXmlContents = List.of(junitTestSuiteXml, junitTestSuitesXml, sureFireTestSuiteXml);
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);
        assertEquals(4, testResultModel.getTestSuites().size());
        assertEquals(4, testResultModel.getTestSuites().size());
    }

    @Test
    void junitFaultsTest() {
        String junitFaultXml = ResourceReader.resourceAsString("format-samples/fault/junit-faults.xml");

        List<String> testXmlContents = List.of(junitFaultXml);
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);
        assertEquals(1, testResultModel.getTestSuites().size());
        final TestSuiteModel testSuite = testResultModel.getTestSuites().get(0);
        assertEquals(5, testSuite.getTests());
        assertEquals(5, testSuite.getTestCases().size());
        assertEquals(3, testSuite.getFailure());
        assertEquals(1, testSuite.getError());
        assertEquals(1, testSuite.getSkipped());

        for (TestCaseModel testCase : testSuite.getTestCasesWithFaults()) {
            assertNotNull(testCase.getTestCaseFaults());
            assertEquals(1, testCase.getTestCaseFaults().size());

            for (TestCaseFaultModel testCaseFault : testCase.getTestCaseFaults()) {
                assertNotNull(testCaseFault.getType());
                assertNotNull(testCaseFault.getMessage());
                assertNotNull(testCaseFault.getValue());
            }
        }
    }

    @Test
    void surefireFaultsTest() {
        String junitFaultXml = ResourceReader.resourceAsString("format-samples/fault/surefire-faults.xml");

        List<String> testXmlContents = List.of(junitFaultXml);
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);
        assertEquals(1, testResultModel.getTestSuites().size());
        final TestSuiteModel testSuite = testResultModel.getTestSuites().get(0);
        assertEquals(4, testSuite.getTests());
        assertEquals(4, testSuite.getTestCases().size());
        assertEquals(2, testSuite.getFailure());
        assertEquals(1, testSuite.getError());
        assertEquals(1, testSuite.getSkipped());

        for (TestCaseModel testCase : testSuite.getTestCasesWithFaults()) {
            assertNotNull(testCase.getTestCaseFaults());
            assertEquals(1, testCase.getTestCaseFaults().size());

            for (TestCaseFaultModel testCaseFault : testCase.getTestCaseFaults()) {
                assertNotNull(testCaseFault.getType());
                assertNotNull(testCaseFault.getMessage());
                assertNotNull(testCaseFault.getValue());
            }

        }
    }

}
