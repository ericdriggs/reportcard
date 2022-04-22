package com.ericdriggs.reportcard.xml;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ResultCount {

    private Integer errors;
    private Integer failed;
    private Integer passed;
    private Integer skipped;
    private Integer tests;
    private BigDecimal time;

    /**
     * Sums the fields of a resultCount
     * @param that a ResultCount
     * @return a new ResultCount sum of this and that
     */
    public ResultCount add(ResultCount that) {
        ResultCount resultCount = new ResultCount();
        resultCount.setErrors(addIntegers(this.getErrors(), that.getErrors()));
        resultCount.setFailed(addIntegers(this.getFailed(), that.getFailed()));
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

    public BigDecimal getPercentPassed() {

        final Integer errorCount = zeroIfNull(errors);
        final Integer failedCount = zeroIfNull(failed);
        final Integer passedCount = zeroIfNull(passed);

        final Integer failureTotal = failedCount + errorCount;

        return BigDecimal.valueOf(passedCount / (passedCount + failureTotal));
    }

    protected Integer zeroIfNull(Integer integer) {
        if (integer == null) {
            return 0;
        }
        return integer;
    }


}
