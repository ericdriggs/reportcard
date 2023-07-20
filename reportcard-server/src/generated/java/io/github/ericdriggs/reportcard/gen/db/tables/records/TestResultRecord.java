/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.TestResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestResultRecord extends UpdatableRecordImpl<TestResultRecord> implements Record11<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, LocalDateTime, String, Boolean, Boolean> {

    private static final long serialVersionUID = -1855125137;

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
     * Setter for <code>reportcard.test_result.stage_fk</code>.
     */
    public TestResultRecord setStageFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.stage_fk</code>.
     */
    public Long getStageFk() {
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
    public TestResultRecord setTime(BigDecimal value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.time</code>.
     */
    public BigDecimal getTime() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>reportcard.test_result.test_result_created</code>.
     */
    public TestResultRecord setTestResultCreated(LocalDateTime value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.test_result_created</code>.
     */
    public LocalDateTime getTestResultCreated() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>reportcard.test_result.external_links</code>.
     */
    public TestResultRecord setExternalLinks(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.external_links</code>.
     */
    public String getExternalLinks() {
        return (String) get(8);
    }

    /**
     * Setter for <code>reportcard.test_result.is_success</code>.
     */
    public TestResultRecord setIsSuccess(Boolean value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.is_success</code>.
     */
    public Boolean getIsSuccess() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>reportcard.test_result.has_skip</code>.
     */
    public TestResultRecord setHasSkip(Boolean value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_result.has_skip</code>.
     */
    public Boolean getHasSkip() {
        return (Boolean) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, LocalDateTime, String, Boolean, Boolean> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, LocalDateTime, String, Boolean, Boolean> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TestResult.TEST_RESULT.TEST_RESULT_ID;
    }

    @Override
    public Field<Long> field2() {
        return TestResult.TEST_RESULT.STAGE_FK;
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
    public Field<BigDecimal> field7() {
        return TestResult.TEST_RESULT.TIME;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return TestResult.TEST_RESULT.TEST_RESULT_CREATED;
    }

    @Override
    public Field<String> field9() {
        return TestResult.TEST_RESULT.EXTERNAL_LINKS;
    }

    @Override
    public Field<Boolean> field10() {
        return TestResult.TEST_RESULT.IS_SUCCESS;
    }

    @Override
    public Field<Boolean> field11() {
        return TestResult.TEST_RESULT.HAS_SKIP;
    }

    @Override
    public Long component1() {
        return getTestResultId();
    }

    @Override
    public Long component2() {
        return getStageFk();
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
    public BigDecimal component7() {
        return getTime();
    }

    @Override
    public LocalDateTime component8() {
        return getTestResultCreated();
    }

    @Override
    public String component9() {
        return getExternalLinks();
    }

    @Override
    public Boolean component10() {
        return getIsSuccess();
    }

    @Override
    public Boolean component11() {
        return getHasSkip();
    }

    @Override
    public Long value1() {
        return getTestResultId();
    }

    @Override
    public Long value2() {
        return getStageFk();
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
    public BigDecimal value7() {
        return getTime();
    }

    @Override
    public LocalDateTime value8() {
        return getTestResultCreated();
    }

    @Override
    public String value9() {
        return getExternalLinks();
    }

    @Override
    public Boolean value10() {
        return getIsSuccess();
    }

    @Override
    public Boolean value11() {
        return getHasSkip();
    }

    @Override
    public TestResultRecord value1(Long value) {
        setTestResultId(value);
        return this;
    }

    @Override
    public TestResultRecord value2(Long value) {
        setStageFk(value);
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
    public TestResultRecord value7(BigDecimal value) {
        setTime(value);
        return this;
    }

    @Override
    public TestResultRecord value8(LocalDateTime value) {
        setTestResultCreated(value);
        return this;
    }

    @Override
    public TestResultRecord value9(String value) {
        setExternalLinks(value);
        return this;
    }

    @Override
    public TestResultRecord value10(Boolean value) {
        setIsSuccess(value);
        return this;
    }

    @Override
    public TestResultRecord value11(Boolean value) {
        setHasSkip(value);
        return this;
    }

    @Override
    public TestResultRecord values(Long value1, Long value2, Integer value3, Integer value4, Integer value5, Integer value6, BigDecimal value7, LocalDateTime value8, String value9, Boolean value10, Boolean value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
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
    public TestResultRecord(Long testResultId, Long stageFk, Integer tests, Integer skipped, Integer error, Integer failure, BigDecimal time, LocalDateTime testResultCreated, String externalLinks, Boolean isSuccess, Boolean hasSkip) {
        super(TestResult.TEST_RESULT);

        setTestResultId(testResultId);
        setStageFk(stageFk);
        setTests(tests);
        setSkipped(skipped);
        setError(error);
        setFailure(failure);
        setTime(time);
        setTestResultCreated(testResultCreated);
        setExternalLinks(externalLinks);
        setIsSuccess(isSuccess);
        setHasSkip(hasSkip);
    }

    /**
     * Create a detached, initialised TestResultRecord
     */
    public TestResultRecord(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult value) {
        super(TestResult.TEST_RESULT);

        if (value != null) {
            setTestResultId(value.getTestResultId());
            setStageFk(value.getStageFk());
            setTests(value.getTests());
            setSkipped(value.getSkipped());
            setError(value.getError());
            setFailure(value.getFailure());
            setTime(value.getTime());
            setTestResultCreated(value.getTestResultCreated());
            setExternalLinks(value.getExternalLinks());
            setIsSuccess(value.getIsSuccess());
            setHasSkip(value.getHasSkip());
        }
    }
}
