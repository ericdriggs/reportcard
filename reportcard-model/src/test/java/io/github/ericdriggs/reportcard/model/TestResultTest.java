package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestResultTest {

    private static final int ERROR_COUNT = 1;
    private static final int FAILURE_COUT = 2;
    private static final int SKIPPED_COUNT = 3;
    private static final int SUCCESS_COUNT = 4;
    private static final int TEST_COUNT = 10;
    private static final BigDecimal TEST_TIME = new BigDecimal(TEST_COUNT - SKIPPED_COUNT);

    public static TestSuiteModel generateTestSuite(ResultCount resultCount) {
        TestSuiteModel testSuite = new TestSuiteModel();
        testSuite.setError(resultCount.getErrors());
        testSuite.setFailure(resultCount.getFailures());
        testSuite.setSkipped(resultCount.getSkipped());
        testSuite.setTime(resultCount.getTime());
        testSuite.setTests(resultCount.getTests());

        testSuite.setHasSkip(resultCount.getSkipped() > 0);
        testSuite.setIsSuccess(resultCount.getFailures() > 0 || resultCount.getErrors() > 0);

        List<TestCaseModel> testCases = new ArrayList<>();
        for (int i = 0; i < resultCount.getErrors(); i++) {
            TestCaseModel testCase = new TestCaseModel();
            testCase.setTestStatus(TestStatus.ERROR).setTime(BigDecimal.ONE);
            testCases.add(testCase);
        }
        for (int i = 0; i < resultCount.getFailures(); i++) {
            TestCaseModel testCase = new TestCaseModel();
            testCase.setTestStatus(TestStatus.FAILURE).setTime(BigDecimal.ONE);
            testCases.add(testCase);
        }
        for (int i = 0; i < resultCount.getSkipped(); i++) {
            TestCaseModel testCase = new TestCaseModel();
            testCase.setTestStatus(TestStatus.SKIPPED).setTime(BigDecimal.ZERO);
            testCases.add(testCase);
        }
        for (int i = 0; i < resultCount.getTests() - resultCount.getErrors() - resultCount.getFailures() - resultCount.getSkipped(); i++) {
            TestCaseModel testCase = new TestCaseModel();
            testCase.setTestStatus(TestStatus.SUCCESS).setTime(BigDecimal.ONE);
            testCases.add(testCase);
        }
        testSuite.setTestCases(testCases);
        return testSuite;
    }

    @Test
    public void addTest() {

        final Integer suiteCopies = 2;
        List<TestSuiteModel> testSuites = getTestSuites(suiteCopies);


        final Integer totalCopies = 2*suiteCopies;

        TestResultModel testResult1 = new TestResultModel().setTestSuites(testSuites);
        TestResultModel testResult2 = new TestResultModel().setTestSuites(testSuites);

        TestResultModel testResult = testResult1.add(testResult2);

        { //assert testResult
            assertEquals(totalCopies * ERROR_COUNT, testResult.getError());
            assertEquals(totalCopies * FAILURE_COUT, testResult.getFailure());
            assertEquals(totalCopies * SKIPPED_COUNT, testResult.getSkipped());
            assertEquals(TEST_TIME.multiply(new BigDecimal(totalCopies)), testResult.getTime());
            assertEquals(false, testResult.getIsSuccess());
            assertEquals(true, testResult.getHasSkip());
        }

        { //assert resultCout
            ResultCount resultCout = testResult.getResultCount();

            assertEquals(totalCopies * ERROR_COUNT, resultCout.getErrors());
            assertEquals(totalCopies * FAILURE_COUT, resultCout.getFailures());
            assertEquals(totalCopies * SKIPPED_COUNT, resultCout.getSkipped());
            assertEquals(TEST_TIME.multiply(new BigDecimal(totalCopies)), resultCout.getTime());
            assertEquals(new BigDecimal("57.14"), resultCout.getPassedPercent());
        }
    }

    @Test
    public void getResultCountTest() {

        final Integer suiteCopies = 2;
        List<TestSuiteModel> testSuites = getTestSuites(suiteCopies);


        TestResultModel testResult = new TestResultModel().setTestSuites(testSuites);
        testResult.updateTotalsFromTestSuites();
        { //assert testResult
            assertEquals(suiteCopies * ERROR_COUNT, testResult.getError());
            assertEquals(suiteCopies * FAILURE_COUT, testResult.getFailure());
            assertEquals(suiteCopies * SKIPPED_COUNT, testResult.getSkipped());
            assertEquals(TEST_TIME.multiply(new BigDecimal(suiteCopies)), testResult.getTime());
            assertEquals(false, testResult.getIsSuccess());
            assertEquals(true, testResult.getHasSkip());
        }

        { //assert resultCout
            ResultCount resultCout = testResult.getResultCount();

            assertEquals(suiteCopies * ERROR_COUNT, resultCout.getErrors());
            assertEquals(suiteCopies * FAILURE_COUT, resultCout.getFailures());
            assertEquals(suiteCopies * SKIPPED_COUNT, resultCout.getSkipped());
            assertEquals(TEST_TIME.multiply(new BigDecimal(suiteCopies)), resultCout.getTime());
            assertEquals(new BigDecimal("57.14"), resultCout.getPassedPercent());
        }
    }

    protected List<TestSuiteModel> getTestSuites(int suiteCopies) {

        List<TestSuiteModel> testSuites = new ArrayList<>();
        {
            ResultCount resultCount = ResultCount
                    .builder()
                    .errors(ERROR_COUNT)
                    .failures(FAILURE_COUT)
                    .successes(SUCCESS_COUNT)
                    .skipped(SKIPPED_COUNT)
                    .tests(TEST_COUNT)
                    .time(TEST_TIME)
                    .build();

            TestSuiteModel testSuite = generateTestSuite(resultCount);

            for (int i = 0; i < suiteCopies; i++) {
                testSuites.add(testSuite);
            }
        }
        return testSuites;
    }
}
