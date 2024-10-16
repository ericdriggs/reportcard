package io.github.ericdriggs.reportcard.xml;

import io.github.ericdriggs.reportcard.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultCountTest {

    private static final Integer ERRORS_COUNT = 1;
    private static final Integer FAILURES_COUNT = 3;
    private static final Integer SKIPPED_COUNT = 5;
    private static final Integer TESTS_COUNT = 20;
    private static final BigDecimal TIME = BigDecimal.TEN;
    private static final Integer SUCCESS_COUNT = TESTS_COUNT - ERRORS_COUNT - FAILURES_COUNT - SKIPPED_COUNT; //11
    private static final BigDecimal PASSED_PERCENTAGE = BigDecimal.valueOf(100 * SUCCESS_COUNT.doubleValue() / (TESTS_COUNT - SKIPPED_COUNT)).setScale(2, RoundingMode.HALF_UP);
    private static ResultCount RESULT_COUNT = ResultCount
            .builder()
            .tests(TESTS_COUNT)
            .errors(ERRORS_COUNT)
            .time(TIME)
            .skipped(SKIPPED_COUNT)
            .failures(FAILURES_COUNT)
            .successes(SUCCESS_COUNT).build();

    @Test
    public void testResultCountMethods() {
        assertEquals(ERRORS_COUNT, RESULT_COUNT.getErrors());
        assertEquals(FAILURES_COUNT, RESULT_COUNT.getFailures());
        assertEquals(SKIPPED_COUNT, RESULT_COUNT.getSkipped());

        assertEquals(FAILURES_COUNT, RESULT_COUNT.getFailures());
        assertEquals(SKIPPED_COUNT, RESULT_COUNT.getSkipped());
        assertEquals(SUCCESS_COUNT, RESULT_COUNT.getSuccesses());
        assertEquals(PASSED_PERCENTAGE, RESULT_COUNT.getTestSuccessPercent());
        assertEquals(TESTS_COUNT, RESULT_COUNT.getTests());
        assertEquals(TIME, RESULT_COUNT.getTime());
    }

    @Test
    public void addResultCount() {
        final ResultCount added = ResultCount.add(RESULT_COUNT, RESULT_COUNT);
        final int copies = 2;
        assertEquals(ERRORS_COUNT * copies, added.getErrors());
        assertEquals(FAILURES_COUNT * copies, added.getFailures());
        assertEquals(PASSED_PERCENTAGE, added.getTestSuccessPercent());
        assertEquals(SKIPPED_COUNT * copies, added.getSkipped());
        assertEquals(SUCCESS_COUNT * copies, added.getSuccesses());
        assertEquals(TESTS_COUNT * copies, added.getTests());
        assertEquals(TIME.multiply(new BigDecimal(copies)), added.getTime());
    }

    @Test
    public void passedPercentZeroTests() {
        final ResultCount resultCount= ResultCount.builder().build();
        assertEquals(BigDecimal.ZERO, resultCount.getTestSuccessPercent());
        assertEquals(BigDecimal.ZERO, resultCount.getTime());
        assertEquals(0, resultCount.getErrors());
        assertEquals(0, resultCount.getFailures());
        assertEquals(0, resultCount.getSkipped());
        assertEquals(0, resultCount.getTests());
        assertEquals(TestStatus.SUCCESS, resultCount.getTestStatus());
    }

    @Test
    public void addSuitesTest() {

        TestResultModel testResultModel = getTestResultModel();
        ResultCount testResult_resultCount = testResultModel.getResultCount();
        assertEquals(3, testResult_resultCount.getFailures());
    }

    public static TestResultModel getTestResultModel() {
        TestResultModel testResult = new TestResultModel();
        {

            List<TestCaseModel> testCases = new ArrayList<>();
            testCases.add(getTestCase(TestStatus.FAILURE));
            testCases.add(getTestCase(TestStatus.FAILURE));
            TestSuiteModel testSuite = TestSuiteModel.builder().build().setTestCases(testCases);
            testResult.getTestSuites().add(testSuite);

            ResultCount testSuite_resultCount = testSuite.getResultCount();
            assertEquals(2, testSuite_resultCount.getFailures());
        }

        {
            List<TestCaseModel> testCases = new ArrayList<>();
            testCases.add(getTestCase(TestStatus.FAILURE));
            TestSuiteModel testSuite = TestSuiteModel.builder().build().setTestCases(testCases);
            testResult.getTestSuites().add(testSuite);

            ResultCount testSuite_resultCount = testSuite.getResultCount();
            assertEquals(1, testSuite_resultCount.getFailures());
        }
        return testResult;
    }

    private final static Random random = new Random();

    static TestCaseModel getTestCase(TestStatus testStatus) {

        int randomInt = random.nextInt();
        TestCaseModel testCase = TestCaseModel.builder().build();

        testCase.setTestStatusFk(testStatus.getStatusId());
        testCase.setName("name-"+randomInt);
        if (testStatus.isErrorOrFailure()) {
            FaultContext faultContext = null;
            if (testStatus == TestStatus.ERROR) {
                faultContext  = FaultContext.ERROR;
            } else if (testStatus == TestStatus.FAILURE) {
                faultContext = FaultContext.FAILURE;
            } else {
                throw new IllegalStateException("not yet supported: " + testStatus);
            }
            TestCaseFaultModel testCaseFault = TestCaseFaultModel.builder().build();
            testCaseFault
                    .setFaultContextFk(faultContext.getFaultContextId())
                    .setMessage("message-" + randomInt)
                    .setType("type-"+randomInt)
                    .setValue("value-"+randomInt);

            testCase.addTestCaseFault(testCaseFault);
        }
        return testCase;

    }
}
