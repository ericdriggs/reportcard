package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.math.BigDecimal;

public enum TestStatusType {
    SUCCESS(ResultCount.builder().tests(1).successes(1).build()),
    SKIPPED(ResultCount.builder().tests(1).skipped(1).build()),
    FAILURE(ResultCount.builder().tests(1).failures(1).build()),
    ERROR(ResultCount.builder().tests(1).errors(1).build());

    private ResultCount resultCount;

    TestStatusType(ResultCount resultCount) {
        this.resultCount = resultCount;
    }

    public ResultCount getResultCount(BigDecimal time) {
        return resultCount.toBuilder().time(time).build();
    }
}
