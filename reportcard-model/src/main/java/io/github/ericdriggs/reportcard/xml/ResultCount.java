package io.github.ericdriggs.reportcard.xml;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class ResultCount {
    /**
     * tests="3" skipped="1" failures="0" errors="0"
     */

    private Integer errors = 0;
    private Integer failures = 0;
    private Integer skipped = 0;
    private Integer successes = 0;
    private Integer tests = 0;
    private BigDecimal time = BigDecimal.ZERO;

    public ResultCount setErrors(Integer val) {
        errors = zeroIfNull(val);
        return this;
    }

    public ResultCount setFailures(Integer val) {
        failures = zeroIfNull(val);
        return this;
    }

    public ResultCount setSkipped(Integer val) {
        this.skipped = zeroIfNull(val);
        return this;
    }

    public ResultCount setSuccesses(Integer val) {
        this.successes = zeroIfNull(val);
        return this;
    }

    public ResultCount setTests(Integer val) {
        this.tests = zeroIfNull(val);
        return this;
    }

    public ResultCount setTime(BigDecimal val) {
        this.time = zeroIfNull(val);
        return this;
    }

    /**
     * Sums the fields of a resultCount
     *
     * @param that a ResultCount
     * @return a new ResultCount sum of this and that
     */
    public ResultCount add(ResultCount that) {
        ResultCount resultCount = new ResultCount();
        resultCount.setErrors(addIntegers(this.getErrors(), that.getErrors()));
        resultCount.setFailures(addIntegers(this.getFailures(), that.getFailures()));
        resultCount.setSkipped(addIntegers(this.getSkipped(), that.getSkipped()));
        resultCount.setSuccesses(addIntegers(this.getSuccesses(), that.getSuccesses()));
        resultCount.setTests(addIntegers(this.getTests(), that.getTests()));
        resultCount.setTime(addBigDecimal(this.getTime(), that.getTime()));
        return resultCount;
    }

    public static ResultCount aggregate(List<ResultCount> resultCounts) {
        ResultCount resultCount = new ResultCount();
        for (ResultCount r : resultCounts) {
            resultCount = resultCount.add(r);
        }
        return resultCount;
    }

    /**
     * @return percent of tests which passed (skipped tests are excluded from total)
     */
    public BigDecimal getPassedPercent() {

        final Integer passedCount = getSuccesses();
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
        }if (that == null) {
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


}
