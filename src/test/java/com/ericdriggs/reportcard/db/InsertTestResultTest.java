package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class InsertTestResultTest extends AbstractDbTest {

    final static String buildUniqueString = "9282be75-6ca5-424b-a7ec-13d13370ba90";

    final static int testResultErrorCount = 10;
    final static int testResultFailureCount = 20;
    final static int testResultSkippedCount = 30;
    final static int testResultTestCount = 70;
    final static long testResultTimeMillis = 3000L;

    final static int testSuiteErrorCount = 5;
    final static int testSuiteFailureCount = 6;
    final static int testSuiteSkippedCount = 7;
    final static int testSuiteTestCount = 8;
    final static long testSuiteTimeMillis = 1000L;
    final static String testSuitePackage = "com.foo.bar";

    final static String testCaseClassName = "testCaseClassName1";
    final static String testCaseName = "testCaseName1";
    final static TestStatus testCaseStatus = TestStatus.FAILURE;
    final static long testCaseTime = 500l;


    @Autowired
    public InsertTestResultTest(ReportCardService reportCardService) {
        super(reportCardService);
    }


    @Test
    public void insertTestResultTest() {

        final TestResult testResultBefore = getInsertableTestResult();
        assertValues(testResultBefore);

        final TestResult testResultInsert = reportCardService.insertTestResult(testResultBefore);
        assertValues(testResultInsert);
        assertIdsandFks(testResultInsert);

        final List<TestResult> testResultsGet = reportCardService.getTestResults(testResultBefore.getBuildStageFk());
        assertEquals(1, testResultsGet.size());
        final TestResult testResultGet = testResultsGet.get(0);
        assertValues(testResultGet);
        assertIdsandFks(testResultGet);
    }

    private void assertValues(TestResult testResult) {

        List<TestSuite> testSuites = testResult.getTestSuites();
        assertFalse(CollectionUtils.isEmpty(testSuites));
        assertEquals(1, testSuites.size());
        final TestSuite testSuite = testResult.getTestSuites().get(0);

        List<TestCase> testCases = testSuite.getTestCases();
        assertFalse(CollectionUtils.isEmpty(testCases));
        assertEquals(1, testCases.size());
        final TestCase testCase = testSuite.getTestCases().get(0);

        assertEquals(testResultErrorCount, testResult.getError());
        assertEquals(testResultFailureCount, testResult.getFailure());
        assertEquals(testResultSkippedCount, testResult.getSkipped());
        assertEquals(testResultTestCount, testResult.getTests());
        assertEquals(testResultTimeMillis, testResult.getTime());

        assertEquals(testSuiteErrorCount, testSuite.getError());
        assertEquals(testSuiteFailureCount, testSuite.getFailure());
        assertEquals(testSuiteSkippedCount, testSuite.getSkipped());
        assertEquals(testSuiteTestCount, testSuite.getTests());
        assertEquals(testSuiteTimeMillis, testSuite.getTime());
        assertEquals(testSuitePackage, testSuite.getPackage());

        assertEquals(testCaseClassName, testCase.getClassName());
        assertEquals(testCaseName, testCase.getTestCaseName());
        assertEquals(testCaseStatus, testCase.getTestStatus());
        assertEquals(testCaseStatus.getStatusId().byteValue(), testCase.getTestStatusFk());
        assertEquals(testCaseTime, testCase.getTime());
    }

    private void assertIdsandFks(TestResult testResult) {

        final TestSuite testSuite = testResult.getTestSuites().get(0);
        final TestCase testCase = testSuite.getTestCases().get(0);

        assertNotNull(testResult.getTestResultId());
        assertNotNull(testSuite.getTestSuiteId());
        assertNotNull(testCase.getTestCaseId());

        assertEquals(testResult.getTestResultId(), testSuite.getTestResultFk());
        assertEquals(testSuite.getTestSuiteId(), testCase.getTestSuiteFk());
    }

    private TestResult getInsertableTestResult() {

        BuildStagePath bsp = null;
        {
            BuildStagePathRequest request =
                    new BuildStagePathRequest()
                            .setOrgName("default")
                            .setRepoName("default")
                            .setAppName("app1")
                            .setBranchName("master")
                            .setBuildUniqueString(buildUniqueString)
                            .setStageName("unit");
            bsp = reportCardService.getBuildStagePath(request);
            assertTrue(bsp.isComplete());
        }


        TestResult testResult = new TestResult();
        testResult.setBuildStageFk(bsp.getBuildStage().getBuildStageId());
        testResult.setError(testResultErrorCount);
        testResult.setFailure(testResultFailureCount);
        testResult.setSkipped(testResultSkippedCount);
        testResult.setTests(testResultTestCount);
        testResult.setTime(testResultTimeMillis);

        List<TestSuite> testSuites = new ArrayList<>();
        {//TestSuite

            TestSuite testSuite = new TestSuite();
            testSuite.setError(testSuiteErrorCount);
            testSuite.setFailure(testSuiteFailureCount);
            testSuite.setPackage(testSuitePackage);
            testSuite.setSkipped(testSuiteSkippedCount);
            testSuite.setTests(testSuiteTestCount);
            testSuite.setTime(testSuiteTimeMillis);


            List<TestCase> testCases = new ArrayList<>();
            {//TestCase


                TestCase testCase = new TestCase();
                testCase.setClassName(testCaseClassName);
                testCase.setTestStatusFk(testCaseStatus.getStatusId());
                testCase.setTestCaseName(testCaseName);
                testCase.setTime(testCaseTime);
                testCases.add(testCase);
            }
            testSuite.setTestCases(testCases);
            testSuites.add(testSuite);
        }
        testResult.setTestSuites(testSuites);

        assertFalse(testResult.getTestSuites().isEmpty());
        assertFalse(testResult.getTestSuites().get(0).getTestCases().isEmpty());
        return testResult;
    }

}
