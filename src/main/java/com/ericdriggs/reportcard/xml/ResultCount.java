package com.ericdriggs.reportcard.xml;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ResultCount {
    private Integer tests;
    private Integer failures;
    private Integer errors;
    private BigDecimal time;

//    public static ResultCount aggregate(List<ResultCount> resultCounts) {
//        ResultCount resultCount = new ResultCount();
//        resultCount.setErrors(
//                resultCounts
//                .stream()
//                .map(ResultCount::getErrors)
//                .reduce(0,(a, b) -> a + b));
//    }

    /**
     * Sums the fields of a resultCount
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


//    public ResultCount getResultCount() {
//        for (Testsuite t : testsuite) {
//
//            t.skipped.;
//                    t.time;
//                    t.failures;
//                    t.errors;
//        }
//    }

}
