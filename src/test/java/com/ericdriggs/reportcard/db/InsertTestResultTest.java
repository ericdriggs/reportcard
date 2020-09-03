package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class InsertTestResultTest extends AbstractDbTest {

    final String buildUniqueString = "9282be75-6ca5-424b-a7ec-13d13370ba90";


    @Autowired
    public InsertTestResultTest(ReportCardService reportCardService) {
        super(reportCardService);
    }


    @Test
    public void insertTestResultTest() {

        final TestResult testResultBefore = getInsertableTestResult();
        final TestResult testResultAfter = reportCardService.insertTestResult(testResultBefore);

        assertNotNull(testResultAfter.getTestResultId());
        assertFalse(testResultAfter.getTestSuites().isEmpty());

        TestSuite testSuite = testResultAfter.getTestSuites().get(0);
        assertNotNull(testSuite.getTestSuiteId());
        assertNotNull(testSuite.getTestCases());


        TestCase testCase = testSuite.getTestCases().get(0);
        assertNotNull(testCase);
        //TODO: more assertions on values

        //TODO: do get and validate matches, use fixture for validating values and comparing

    }


    TestResult getInsertableTestResult() {

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

        final int testResultErrorCount = 10;
        final int testResultFailureCount = 20;
        final int testResultSkippedCount = 30;
        final int testResultTestCount = 70;
        final long testResultTimeMillis = 3000l;

        TestResult testResult = new TestResult();
        testResult.setBuildStageFk(bsp.getBuildStage().getBuildStageId());
        testResult.setError(testResultErrorCount);
        testResult.setFailure(testResultFailureCount);
        testResult.setSkipped(testResultSkippedCount);
        testResult.setTests(testResultTestCount);
        testResult.setTime(testResultTimeMillis);

        List<TestSuite> testSuites = new ArrayList<>();
        {//TestSuite
            final int testSuiteErrorCount = 5;
            final int testSuiteFailureCount = 6;
            final int testSuiteSkippedCount = 7;
            final int testSuiteTestCount = 8;
            final long testSuiteTimeMillis = 1000l;
            final String testSuitePackage = "com.foo.bar";

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
                testCase.setClassName("classname1");
                testCase.setTestStatusFk(TestStatus.FAILURE.getStatusId().byteValue());
                testCase.setTestCaseName("test case name 1");
                testCase.setTime(500l);
                testCase.setTime(testSuiteTimeMillis);
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
