/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.pojos;


import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long          testResultId;
    private Long          stageFk;
    private Integer       tests;
    private Integer       skipped;
    private Integer       error;
    private Integer       failure;
    private BigDecimal    time;
    private LocalDateTime testResultCreated;
    private String        externalLinks;
    private Boolean       isSuccess;
    private Boolean       hasSkip;

    public TestResult() {}

    public TestResult(TestResult value) {
        this.testResultId = value.testResultId;
        this.stageFk = value.stageFk;
        this.tests = value.tests;
        this.skipped = value.skipped;
        this.error = value.error;
        this.failure = value.failure;
        this.time = value.time;
        this.testResultCreated = value.testResultCreated;
        this.externalLinks = value.externalLinks;
        this.isSuccess = value.isSuccess;
        this.hasSkip = value.hasSkip;
    }

    public TestResult(
        Long          testResultId,
        Long          stageFk,
        Integer       tests,
        Integer       skipped,
        Integer       error,
        Integer       failure,
        BigDecimal    time,
        LocalDateTime testResultCreated,
        String        externalLinks,
        Boolean       isSuccess,
        Boolean       hasSkip
    ) {
        this.testResultId = testResultId;
        this.stageFk = stageFk;
        this.tests = tests;
        this.skipped = skipped;
        this.error = error;
        this.failure = failure;
        this.time = time;
        this.testResultCreated = testResultCreated;
        this.externalLinks = externalLinks;
        this.isSuccess = isSuccess;
        this.hasSkip = hasSkip;
    }

    /**
     * Getter for <code>reportcard.test_result.test_result_id</code>.
     */
    public Long getTestResultId() {
        return this.testResultId;
    }

    /**
     * Setter for <code>reportcard.test_result.test_result_id</code>.
     */
    public TestResult setTestResultId(Long testResultId) {
        this.testResultId = testResultId;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.stage_fk</code>.
     */
    public Long getStageFk() {
        return this.stageFk;
    }

    /**
     * Setter for <code>reportcard.test_result.stage_fk</code>.
     */
    public TestResult setStageFk(Long stageFk) {
        this.stageFk = stageFk;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.tests</code>.
     */
    public Integer getTests() {
        return this.tests;
    }

    /**
     * Setter for <code>reportcard.test_result.tests</code>.
     */
    public TestResult setTests(Integer tests) {
        this.tests = tests;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.skipped</code>.
     */
    public Integer getSkipped() {
        return this.skipped;
    }

    /**
     * Setter for <code>reportcard.test_result.skipped</code>.
     */
    public TestResult setSkipped(Integer skipped) {
        this.skipped = skipped;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.error</code>.
     */
    public Integer getError() {
        return this.error;
    }

    /**
     * Setter for <code>reportcard.test_result.error</code>.
     */
    public TestResult setError(Integer error) {
        this.error = error;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.failure</code>.
     */
    public Integer getFailure() {
        return this.failure;
    }

    /**
     * Setter for <code>reportcard.test_result.failure</code>.
     */
    public TestResult setFailure(Integer failure) {
        this.failure = failure;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.time</code>.
     */
    public BigDecimal getTime() {
        return this.time;
    }

    /**
     * Setter for <code>reportcard.test_result.time</code>.
     */
    public TestResult setTime(BigDecimal time) {
        this.time = time;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.test_result_created</code>.
     */
    public LocalDateTime getTestResultCreated() {
        return this.testResultCreated;
    }

    /**
     * Setter for <code>reportcard.test_result.test_result_created</code>.
     */
    public TestResult setTestResultCreated(LocalDateTime testResultCreated) {
        this.testResultCreated = testResultCreated;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.external_links</code>.
     */
    public String getExternalLinks() {
        return this.externalLinks;
    }

    /**
     * Setter for <code>reportcard.test_result.external_links</code>.
     */
    public TestResult setExternalLinks(String externalLinks) {
        this.externalLinks = externalLinks;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.is_success</code>.
     */
    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    /**
     * Setter for <code>reportcard.test_result.is_success</code>.
     */
    public TestResult setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.has_skip</code>.
     */
    public Boolean getHasSkip() {
        return this.hasSkip;
    }

    /**
     * Setter for <code>reportcard.test_result.has_skip</code>.
     */
    public TestResult setHasSkip(Boolean hasSkip) {
        this.hasSkip = hasSkip;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestResult (");

        sb.append(testResultId);
        sb.append(", ").append(stageFk);
        sb.append(", ").append(tests);
        sb.append(", ").append(skipped);
        sb.append(", ").append(error);
        sb.append(", ").append(failure);
        sb.append(", ").append(time);
        sb.append(", ").append(testResultCreated);
        sb.append(", ").append(externalLinks);
        sb.append(", ").append(isSuccess);
        sb.append(", ").append(hasSkip);

        sb.append(")");
        return sb.toString();
    }
}
