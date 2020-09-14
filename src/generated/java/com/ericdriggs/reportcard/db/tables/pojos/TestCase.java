/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestCase implements Serializable {

    private static final long serialVersionUID = -327093051;

    private Long       testCaseId;
    private Long       testSuiteFk;
    private String     testCaseName;
    private String     className;
    private BigDecimal time;
    private Byte       testStatusFk;

    public TestCase() {}

    public TestCase(TestCase value) {
        this.testCaseId = value.testCaseId;
        this.testSuiteFk = value.testSuiteFk;
        this.testCaseName = value.testCaseName;
        this.className = value.className;
        this.time = value.time;
        this.testStatusFk = value.testStatusFk;
    }

    public TestCase(
        Long       testCaseId,
        Long       testSuiteFk,
        String     testCaseName,
        String     className,
        BigDecimal time,
        Byte       testStatusFk
    ) {
        this.testCaseId = testCaseId;
        this.testSuiteFk = testSuiteFk;
        this.testCaseName = testCaseName;
        this.className = className;
        this.time = time;
        this.testStatusFk = testStatusFk;
    }

    public Long getTestCaseId() {
        return this.testCaseId;
    }

    public TestCase setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
        return this;
    }

    public Long getTestSuiteFk() {
        return this.testSuiteFk;
    }

    public TestCase setTestSuiteFk(Long testSuiteFk) {
        this.testSuiteFk = testSuiteFk;
        return this;
    }

    public String getTestCaseName() {
        return this.testCaseName;
    }

    public TestCase setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public TestCase setClassName(String className) {
        this.className = className;
        return this;
    }

    public BigDecimal getTime() {
        return this.time;
    }

    public TestCase setTime(BigDecimal time) {
        this.time = time;
        return this;
    }

    public Byte getTestStatusFk() {
        return this.testStatusFk;
    }

    public TestCase setTestStatusFk(Byte testStatusFk) {
        this.testStatusFk = testStatusFk;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestCase (");

        sb.append(testCaseId);
        sb.append(", ").append(testSuiteFk);
        sb.append(", ").append(testCaseName);
        sb.append(", ").append(className);
        sb.append(", ").append(time);
        sb.append(", ").append(testStatusFk);

        sb.append(")");
        return sb.toString();
    }
}
