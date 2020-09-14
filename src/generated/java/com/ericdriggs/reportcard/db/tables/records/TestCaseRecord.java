/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.records;


import com.ericdriggs.reportcard.db.tables.TestCase;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestCaseRecord extends UpdatableRecordImpl<TestCaseRecord> implements Record6<Long, Long, String, String, BigDecimal, Byte> {

    private static final long serialVersionUID = 853176352;

    /**
     * Setter for <code>reportcard.test_case.test_case_id</code>.
     */
    public TestCaseRecord setTestCaseId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case.test_case_id</code>.
     */
    public Long getTestCaseId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.test_case.test_suite_fk</code>.
     */
    public TestCaseRecord setTestSuiteFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case.test_suite_fk</code>.
     */
    public Long getTestSuiteFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>reportcard.test_case.test_case_name</code>.
     */
    public TestCaseRecord setTestCaseName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case.test_case_name</code>.
     */
    public String getTestCaseName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>reportcard.test_case.class_name</code>.
     */
    public TestCaseRecord setClassName(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case.class_name</code>.
     */
    public String getClassName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>reportcard.test_case.time</code>.
     */
    public TestCaseRecord setTime(BigDecimal value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case.time</code>.
     */
    public BigDecimal getTime() {
        return (BigDecimal) get(4);
    }

    /**
     * Setter for <code>reportcard.test_case.test_status_fk</code>.
     */
    public TestCaseRecord setTestStatusFk(Byte value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case.test_status_fk</code>.
     */
    public Byte getTestStatusFk() {
        return (Byte) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, String, BigDecimal, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, Long, String, String, BigDecimal, Byte> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TestCase.TEST_CASE.TEST_CASE_ID;
    }

    @Override
    public Field<Long> field2() {
        return TestCase.TEST_CASE.TEST_SUITE_FK;
    }

    @Override
    public Field<String> field3() {
        return TestCase.TEST_CASE.TEST_CASE_NAME;
    }

    @Override
    public Field<String> field4() {
        return TestCase.TEST_CASE.CLASS_NAME;
    }

    @Override
    public Field<BigDecimal> field5() {
        return TestCase.TEST_CASE.TIME;
    }

    @Override
    public Field<Byte> field6() {
        return TestCase.TEST_CASE.TEST_STATUS_FK;
    }

    @Override
    public Long component1() {
        return getTestCaseId();
    }

    @Override
    public Long component2() {
        return getTestSuiteFk();
    }

    @Override
    public String component3() {
        return getTestCaseName();
    }

    @Override
    public String component4() {
        return getClassName();
    }

    @Override
    public BigDecimal component5() {
        return getTime();
    }

    @Override
    public Byte component6() {
        return getTestStatusFk();
    }

    @Override
    public Long value1() {
        return getTestCaseId();
    }

    @Override
    public Long value2() {
        return getTestSuiteFk();
    }

    @Override
    public String value3() {
        return getTestCaseName();
    }

    @Override
    public String value4() {
        return getClassName();
    }

    @Override
    public BigDecimal value5() {
        return getTime();
    }

    @Override
    public Byte value6() {
        return getTestStatusFk();
    }

    @Override
    public TestCaseRecord value1(Long value) {
        setTestCaseId(value);
        return this;
    }

    @Override
    public TestCaseRecord value2(Long value) {
        setTestSuiteFk(value);
        return this;
    }

    @Override
    public TestCaseRecord value3(String value) {
        setTestCaseName(value);
        return this;
    }

    @Override
    public TestCaseRecord value4(String value) {
        setClassName(value);
        return this;
    }

    @Override
    public TestCaseRecord value5(BigDecimal value) {
        setTime(value);
        return this;
    }

    @Override
    public TestCaseRecord value6(Byte value) {
        setTestStatusFk(value);
        return this;
    }

    @Override
    public TestCaseRecord values(Long value1, Long value2, String value3, String value4, BigDecimal value5, Byte value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TestCaseRecord
     */
    public TestCaseRecord() {
        super(TestCase.TEST_CASE);
    }

    /**
     * Create a detached, initialised TestCaseRecord
     */
    public TestCaseRecord(Long testCaseId, Long testSuiteFk, String testCaseName, String className, BigDecimal time, Byte testStatusFk) {
        super(TestCase.TEST_CASE);

        set(0, testCaseId);
        set(1, testSuiteFk);
        set(2, testCaseName);
        set(3, className);
        set(4, time);
        set(5, testStatusFk);
    }
}
