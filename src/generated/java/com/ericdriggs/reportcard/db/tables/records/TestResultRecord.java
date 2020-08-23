/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.records;


import com.ericdriggs.reportcard.db.tables.TestResult;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestResultRecord extends UpdatableRecordImpl<TestResultRecord> implements Record9<Long, Long, Integer, Integer, Integer, Integer, Long, Byte, Byte> {

    private static final long serialVersionUID = 2109572980;

    /**
     * Setter for <code>reportcard.test_result.test_result_id</code>.
     */
    public TestResultRecord setTestResultId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.test_result_id</code>.
     */
    public Long getTestResultId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.test_result.build_stage_fk</code>.
     */
    public TestResultRecord setBuildStageFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.build_stage_fk</code>.
     */
    public Long getBuildStageFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>reportcard.test_result.tests</code>.
     */
    public TestResultRecord setTests(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.tests</code>.
     */
    public Integer getTests() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>reportcard.test_result.skipped</code>.
     */
    public TestResultRecord setSkipped(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.skipped</code>.
     */
    public Integer getSkipped() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>reportcard.test_result.error</code>.
     */
    public TestResultRecord setError(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.error</code>.
     */
    public Integer getError() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>reportcard.test_result.failure</code>.
     */
    public TestResultRecord setFailure(Integer value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.failure</code>.
     */
    public Integer getFailure() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>reportcard.test_result.time</code>.
     */
    public TestResultRecord setTime(Long value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.time</code>.
     */
    public Long getTime() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>reportcard.test_result.is_success</code>.
     */
    public TestResultRecord setIsSuccess(Byte value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.is_success</code>.
     */
    public Byte getIsSuccess() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>reportcard.test_result.has_skip</code>.
     */
    public TestResultRecord setHasSkip(Byte value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.has_skip</code>.
     */
    public Byte getHasSkip() {
        return (Byte) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, Integer, Integer, Integer, Integer, Long, Byte, Byte> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<Long, Long, Integer, Integer, Integer, Integer, Long, Byte, Byte> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TestResult.TEST_RESULT.TEST_RESULT_ID;
    }

    @Override
    public Field<Long> field2() {
        return TestResult.TEST_RESULT.BUILD_STAGE_FK;
    }

    @Override
    public Field<Integer> field3() {
        return TestResult.TEST_RESULT.TESTS;
    }

    @Override
    public Field<Integer> field4() {
        return TestResult.TEST_RESULT.SKIPPED;
    }

    @Override
    public Field<Integer> field5() {
        return TestResult.TEST_RESULT.ERROR;
    }

    @Override
    public Field<Integer> field6() {
        return TestResult.TEST_RESULT.FAILURE;
    }

    @Override
    public Field<Long> field7() {
        return TestResult.TEST_RESULT.TIME;
    }

    @Override
    public Field<Byte> field8() {
        return TestResult.TEST_RESULT.IS_SUCCESS;
    }

    @Override
    public Field<Byte> field9() {
        return TestResult.TEST_RESULT.HAS_SKIP;
    }

    @Override
    public Long component1() {
        return getTestResultId();
    }

    @Override
    public Long component2() {
        return getBuildStageFk();
    }

    @Override
    public Integer component3() {
        return getTests();
    }

    @Override
    public Integer component4() {
        return getSkipped();
    }

    @Override
    public Integer component5() {
        return getError();
    }

    @Override
    public Integer component6() {
        return getFailure();
    }

    @Override
    public Long component7() {
        return getTime();
    }

    @Override
    public Byte component8() {
        return getIsSuccess();
    }

    @Override
    public Byte component9() {
        return getHasSkip();
    }

    @Override
    public Long value1() {
        return getTestResultId();
    }

    @Override
    public Long value2() {
        return getBuildStageFk();
    }

    @Override
    public Integer value3() {
        return getTests();
    }

    @Override
    public Integer value4() {
        return getSkipped();
    }

    @Override
    public Integer value5() {
        return getError();
    }

    @Override
    public Integer value6() {
        return getFailure();
    }

    @Override
    public Long value7() {
        return getTime();
    }

    @Override
    public Byte value8() {
        return getIsSuccess();
    }

    @Override
    public Byte value9() {
        return getHasSkip();
    }

    @Override
    public TestResultRecord value1(Long value) {
        setTestResultId(value);
        return this;
    }

    @Override
    public TestResultRecord value2(Long value) {
        setBuildStageFk(value);
        return this;
    }

    @Override
    public TestResultRecord value3(Integer value) {
        setTests(value);
        return this;
    }

    @Override
    public TestResultRecord value4(Integer value) {
        setSkipped(value);
        return this;
    }

    @Override
    public TestResultRecord value5(Integer value) {
        setError(value);
        return this;
    }

    @Override
    public TestResultRecord value6(Integer value) {
        setFailure(value);
        return this;
    }

    @Override
    public TestResultRecord value7(Long value) {
        setTime(value);
        return this;
    }

    @Override
    public TestResultRecord value8(Byte value) {
        setIsSuccess(value);
        return this;
    }

    @Override
    public TestResultRecord value9(Byte value) {
        setHasSkip(value);
        return this;
    }

    @Override
    public TestResultRecord values(Long value1, Long value2, Integer value3, Integer value4, Integer value5, Integer value6, Long value7, Byte value8, Byte value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TestResultRecord
     */
    public TestResultRecord() {
        super(TestResult.TEST_RESULT);
    }

    /**
     * Create a detached, initialised TestResultRecord
     */
    public TestResultRecord(Long testResultId, Long buildStageFk, Integer tests, Integer skipped, Integer error, Integer failure, Long time, Byte isSuccess, Byte hasSkip) {
        super(TestResult.TEST_RESULT);

        set(0, testResultId);
        set(1, buildStageFk);
        set(2, tests);
        set(3, skipped);
        set(4, error);
        set(5, failure);
        set(6, time);
        set(7, isSuccess);
        set(8, hasSkip);
    }
}
