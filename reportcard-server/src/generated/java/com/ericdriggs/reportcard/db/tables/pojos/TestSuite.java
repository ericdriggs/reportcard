/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.13.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestSuite implements Serializable {

    private static final long serialVersionUID = -1949112380;

    private Long       testSuiteId;
    private Long       testResultFk;
    private Integer    tests;
    private Integer    skipped;
    private Integer    error;
    private Integer    failure;
    private BigDecimal time;
    private String     package_;
    private String     group;
    private String     properties;
    private Boolean    isSuccess;
    private Boolean    hasSkip;

    public TestSuite() {}

    public TestSuite(TestSuite value) {
        this.testSuiteId = value.testSuiteId;
        this.testResultFk = value.testResultFk;
        this.tests = value.tests;
        this.skipped = value.skipped;
        this.error = value.error;
        this.failure = value.failure;
        this.time = value.time;
        this.package_ = value.package_;
        this.group = value.group;
        this.properties = value.properties;
        this.isSuccess = value.isSuccess;
        this.hasSkip = value.hasSkip;
    }

    public TestSuite(
        Long       testSuiteId,
        Long       testResultFk,
        Integer    tests,
        Integer    skipped,
        Integer    error,
        Integer    failure,
        BigDecimal time,
        String     package_,
        String     group,
        String     properties,
        Boolean    isSuccess,
        Boolean    hasSkip
    ) {
        this.testSuiteId = testSuiteId;
        this.testResultFk = testResultFk;
        this.tests = tests;
        this.skipped = skipped;
        this.error = error;
        this.failure = failure;
        this.time = time;
        this.package_ = package_;
        this.group = group;
        this.properties = properties;
        this.isSuccess = isSuccess;
        this.hasSkip = hasSkip;
    }

    public Long getTestSuiteId() {
        return this.testSuiteId;
    }

    public TestSuite setTestSuiteId(Long testSuiteId) {
        this.testSuiteId = testSuiteId;
        return this;
    }

    public Long getTestResultFk() {
        return this.testResultFk;
    }

    public TestSuite setTestResultFk(Long testResultFk) {
        this.testResultFk = testResultFk;
        return this;
    }

    public Integer getTests() {
        return this.tests;
    }

    public TestSuite setTests(Integer tests) {
        this.tests = tests;
        return this;
    }

    public Integer getSkipped() {
        return this.skipped;
    }

    public TestSuite setSkipped(Integer skipped) {
        this.skipped = skipped;
        return this;
    }

    public Integer getError() {
        return this.error;
    }

    public TestSuite setError(Integer error) {
        this.error = error;
        return this;
    }

    public Integer getFailure() {
        return this.failure;
    }

    public TestSuite setFailure(Integer failure) {
        this.failure = failure;
        return this;
    }

    public BigDecimal getTime() {
        return this.time;
    }

    public TestSuite setTime(BigDecimal time) {
        this.time = time;
        return this;
    }

    public String getPackage() {
        return this.package_;
    }

    public TestSuite setPackage(String package_) {
        this.package_ = package_;
        return this;
    }

    public String getGroup() {
        return this.group;
    }

    public TestSuite setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getProperties() {
        return this.properties;
    }

    public TestSuite setProperties(String properties) {
        this.properties = properties;
        return this;
    }

    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    public TestSuite setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
        return this;
    }

    public Boolean getHasSkip() {
        return this.hasSkip;
    }

    public TestSuite setHasSkip(Boolean hasSkip) {
        this.hasSkip = hasSkip;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestSuite (");

        sb.append(testSuiteId);
        sb.append(", ").append(testResultFk);
        sb.append(", ").append(tests);
        sb.append(", ").append(skipped);
        sb.append(", ").append(error);
        sb.append(", ").append(failure);
        sb.append(", ").append(time);
        sb.append(", ").append(package_);
        sb.append(", ").append(group);
        sb.append(", ").append(properties);
        sb.append(", ").append(isSuccess);
        sb.append(", ").append(hasSkip);

        sb.append(")");
        return sb.toString();
    }
}
