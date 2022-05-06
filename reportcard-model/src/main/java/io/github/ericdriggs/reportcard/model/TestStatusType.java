package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.math.BigDecimal;

public enum TestStatusType {
    SUCCESS(new ResultCount().setTests(1).setSuccesses(1)),
    SKIPPED(new ResultCount().setTests(1).setSkipped(1)),
    FAILURE(new ResultCount().setTests(1).setFailures(1)),
    ERROR(new ResultCount().setTests(1).setErrors(1));

    private ResultCount resultCount;

    TestStatusType(ResultCount resultCount) {
        this.resultCount = resultCount;
    }

    public ResultCount getResultCount(BigDecimal time) {
        return resultCount.setTime(time);
    }
}
