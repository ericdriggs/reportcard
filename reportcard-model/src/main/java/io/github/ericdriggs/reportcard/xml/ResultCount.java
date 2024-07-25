package io.github.ericdriggs.reportcard.xml;

import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

        if (tests == null || tests == 0) {
            return BigDecimal.ZERO;
        }

        final int skippedCount = skipped == null ? 0 : skipped;
        if (tests - skippedCount == 0) {
            return BigDecimal.ZERO;
        }
        final int successCount = successes == null ? 0 : successes;

        final int errorCount = errors == null ? 0 : errors;
        final int failureCount = failures == null ? 0 : failures;


        //@SuppressWarnings("WrapperTypeMayBePrimitive")
        final int errorFailureCount = errorCount + failureCount;

        try {
            return BigDecimal.valueOf(
                    100 * (double) successCount /
                    (double) (tests - skippedCount)
            ).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
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
                //round time for comparison since precision may change after persist/read
                CompareUtil.compareBigDecimalAsBigInteger(this.time, that.time)
        );
    }

    public static List<String> diff(ResultCount o1, ResultCount o2) {
        if (o1 == null && o2 == null) {
            return Collections.emptyList();
        } else if (o1 == null) {
            return Collections.singletonList("o1 is NULL, o2 is not NULL");
        } else if (o2 == null) {
            return Collections.singletonList("o1 is not NULL, o2 is NULL");
        }

        List<String> diffs = new ArrayList<>();
        if (!Objects.equals(o1.errors, o2.errors)) {
            diffs.add("o1.errors: " + o1.errors + " != o2.errors: " + o2.errors);
        }

        if (!Objects.equals(o1.failures, o2.failures)) {
            diffs.add("o1.errors: " + o1.failures + " != o2.failures: " + o2.failures);
        }

        if (!Objects.equals(o1.skipped, o2.skipped)) {
            diffs.add("o1.skipped: " + o1.skipped + " != o2.skipped: " + o2.skipped);
        }

        if (!Objects.equals(o1.tests, o2.tests)) {
            diffs.add("o1.tests: " + o1.tests + " != o2.tests: " + o2.tests);
        }

        if (!Objects.equals(o1.tests, o2.tests)) {
            diffs.add("o1.tests: " + o1.tests + " != o2.tests: " + o2.tests);
        }

        //round time for comparison since precision may change after persist/read
        if (CompareUtil.compareBigDecimalAsBigInteger(o1.time, o2.time) != 0) {
            diffs.add("o1.time: " + o1.time + " != o2.time: " + o2.time);
        }
        return diffs;
    }


}