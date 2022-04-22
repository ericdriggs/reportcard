package com.ericdriggs.reportcard.xml;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class ResultCount {
    /**
     * tests="3" skipped="1" failures="0" errors="0"
     */

    private Integer errors;
    private Integer failures;
    private Integer skipped;
    private Integer tests;
    private BigDecimal time;

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
        resultCount.setTests(addIntegers(this.getTests(), that.getTests()));
        resultCount.setTime(addBigDecimal(this.getTime(), that.getTime()));
        return resultCount;
    }

    public ResultCount aggregate(List<ResultCount> resultCounts) {
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

        final Integer passedCount = getPassedCount();
        final Integer failureErrorsTotal = getFailuresCount() + getErrorsCount();
        return BigDecimal.valueOf(
                        (100 * passedCount.doubleValue()) /
                                (passedCount.doubleValue() + failureErrorsTotal.doubleValue())
                ).setScale(2, RoundingMode.HALF_UP);
    }

    public Integer getErrorsCount() {
        return zeroIfNull(errors);
    }

    public Integer getFailuresCount() {
        return zeroIfNull(failures);
    }

    public Integer getSkippedCount() {
        return zeroIfNull(skipped);
    }

    public Integer getTestsCount() {
        return zeroIfNull(tests);
    }

    public Integer getPassedCount() {
        return getTestsCount() - getErrorsCount() - getFailuresCount() - getSkippedCount();
    }

    protected Integer zeroIfNull(Integer integer) {
        if (integer == null) {
            return 0;
        }
        return integer;
    }

    /**
     * Adds two Integers
     *
     * @param thiz an Integer, may be null
     * @param that an Integer, may be null
     * @return an Integer sum of thiz and that, only <code>null</code> if both are null.
     */
    private static Integer addIntegers(Integer thiz, Integer that) {
        if (thiz == null) {
            return that;
        } else if (that == null) {
            return null;
        } else {
            return thiz + that;
        }
    }

    /**
     * Adds two Integers
     *
     * @param thiz an Integer, may be null
     * @param that an Integer, may be null
     * @return an Integer sum of thiz and that, only <code>null</code> if both are null.
     */
    private static BigDecimal addBigDecimal(BigDecimal thiz, BigDecimal that) {
        if (thiz == null) {
            return that;
        } else if (that == null) {
            return null;
        } else {
            return thiz.add(that);
        }
    }


}
