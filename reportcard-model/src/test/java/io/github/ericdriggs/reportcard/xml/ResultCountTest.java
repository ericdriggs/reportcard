package io.github.ericdriggs.reportcard.xml;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultCountTest {

    private static final Integer ERRORS_COUNT = 1;
    private static final Integer FAILURES_COUNT = 3;
    private static final Integer SKIPPED_COUNT = 5;
    private static final Integer TESTS_COUNT = 20;
    private static final BigDecimal TIME = BigDecimal.TEN;
    private static final Integer SUCCESS_COUNT = TESTS_COUNT - ERRORS_COUNT - FAILURES_COUNT - SKIPPED_COUNT; //11
    private static final BigDecimal PASSED_PERCENTAGE = BigDecimal.valueOf(100 * SUCCESS_COUNT.doubleValue() / (TESTS_COUNT - SKIPPED_COUNT)).setScale(2, RoundingMode.HALF_UP);
    private static ResultCount RESULT_COUNT = new ResultCount()
            .setTests(TESTS_COUNT)
            .setErrors(ERRORS_COUNT)
            .setTime(TIME)
            .setSkipped(SKIPPED_COUNT)
            .setFailures(FAILURES_COUNT)
            .setSuccesses(SUCCESS_COUNT);

    @Test
    public void testResultCountMethods() {
        assertEquals(ERRORS_COUNT, RESULT_COUNT.getErrors());
        assertEquals(FAILURES_COUNT, RESULT_COUNT.getFailures());
        assertEquals(SKIPPED_COUNT, RESULT_COUNT.getSkipped());

        assertEquals(FAILURES_COUNT, RESULT_COUNT.getFailures());
        assertEquals(SKIPPED_COUNT, RESULT_COUNT.getSkipped());
        assertEquals(SUCCESS_COUNT, RESULT_COUNT.getSuccesses());
        assertEquals(PASSED_PERCENTAGE, RESULT_COUNT.getPassedPercent());
        assertEquals(TESTS_COUNT, RESULT_COUNT.getTests());
        assertEquals(TIME, RESULT_COUNT.getTime());
    }

    @Test
    public void addResultCount() {
        final ResultCount added = RESULT_COUNT.add(RESULT_COUNT);
        final int copies = 2;
        assertEquals(ERRORS_COUNT * copies, added.getErrors());
        assertEquals(FAILURES_COUNT * copies, added.getFailures());
        assertEquals(PASSED_PERCENTAGE, added.getPassedPercent());
        assertEquals(SKIPPED_COUNT * copies, added.getSkipped());
        assertEquals(SUCCESS_COUNT * copies, added.getSuccesses());
        assertEquals(TESTS_COUNT * copies, added.getTests());
        assertEquals(TIME.multiply(new BigDecimal(copies)), added.getTime());


    }
}
