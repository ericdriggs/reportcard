package io.github.ericdriggs.reportcard.xml;

import io.github.ericdriggs.reportcard.model.TestStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static io.github.ericdriggs.reportcard.util.CompareUtil.chainCompare;

@Value
@Builder(toBuilder = true)
public class ResultCount implements Comparable<ResultCount> {

    @Builder.Default
    Integer errors = 0;
    @Builder.Default
    Integer failures = 0;
    @Builder.Default
    Integer skipped = 0;
    @Builder.Default
    Integer successes = 0;
    @Builder.Default
    Integer tests = 0;
    @Builder.Default
    BigDecimal time = BigDecimal.ZERO;

    public TestStatus getTestStatus() {
        if (errors > 0) {
            return TestStatus.ERROR;
        } else if (failures > 0) {
            return TestStatus.FAILURE;
        } else if (skipped > 0) {
            return TestStatus.SKIPPED;
        }
        return TestStatus.SUCCESS;
    }

    public Integer getErrorsAndFailures() {
        return errors + failures;
    }

    /**
     * Sums the fields of a resultCount
     *
     * @param r1 a ResultCount
     * @param r2 a ResultCount
     * @return a new ResultCount sum of this and that
     */
    public static ResultCount add(ResultCount r1, ResultCount r2) {
        if (r1 == null) {
            return r2;
        }

        ResultCount result = ResultCount
                .builder()
                .errors(addIntegers(r1.getErrors(), r2.getErrors()))
                .failures(addIntegers(r1.getFailures(), r2.getFailures()))
                .skipped(addIntegers(r1.getSkipped(), r2.getSkipped()))
                .successes(addIntegers(r1.getSuccesses(), r2.getSuccesses()))
                .tests(addIntegers(r1.getTests(), r2.getTests()))
                .time(addBigDecimal(r1.getTime(), r2.getTime()))
                .build();
        return result;
    }

    public static ResultCount aggregate(List<ResultCount> resultCounts) {
        ResultCount resultCount = ResultCount.builder().build();
        for (ResultCount r : resultCounts) {
            resultCount = add(resultCount, r);
        }
        return resultCount;
    }

    /**
     * @return percent of tests which passed (skipped tests are excluded from total)
     */
    public BigDecimal getPassedPercent() {

        if (tests == 0) {
            return BigDecimal.ZERO;
        }

        final Integer passedCount = getSuccesses();

        @SuppressWarnings("WrapperTypeMayBePrimitive")
        final Integer failureErrorsTotal = getFailures() + getErrors();

        return BigDecimal.valueOf(
                (100 * passedCount.doubleValue()) /
                (passedCount.doubleValue() + failureErrorsTotal.doubleValue())
        ).setScale(2, RoundingMode.HALF_UP);
    }

    protected Integer zeroIfNull(Integer val) {
        if (val == null) {
            return 0;
        }
        return val;
    }

    protected BigDecimal zeroIfNull(BigDecimal val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        return val;
    }

    /**
     * Adds two Integers
     *
     * @param thiz an Integer, may be null
     * @param that an Integer, may be null
     * @return an Integer sum of thiz and that, may be 0 but never null
     */
    private static Integer addIntegers(Integer thiz, Integer that) {
        if (thiz == null) {
            thiz = 0;
        }
        if (that == null) {
            that = 0;
        }
        return thiz + that;
    }

    /**
     * Adds two Integers
     *
     * @param thiz an Integer, may be null
     * @param that an Integer, may be null
     * @return an Integer sum of thiz and that, may be zero, never null
     */
    private static BigDecimal addBigDecimal(BigDecimal thiz, BigDecimal that) {
        if (thiz == null) {
            thiz = BigDecimal.ZERO;
        }
        if (that == null) {
            that = BigDecimal.ZERO;
        }
        return thiz.add(that);
    }

    @Override
    public int compareTo(ResultCount that) {
        if (that == null) {
            return 1;
        }
        return chainCompare(
                errors.compareTo(that.errors),
                failures.compareTo(that.failures),
                skipped.compareTo(that.skipped),
                successes.compareTo(that.successes),
                tests.compareTo(that.tests),
                time.compareTo(that.time)
        );
    }
}