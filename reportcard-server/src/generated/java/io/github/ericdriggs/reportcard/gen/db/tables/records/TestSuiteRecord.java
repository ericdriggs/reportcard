/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.TestSuiteTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuitePojo;

import java.math.BigDecimal;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestSuiteRecord extends UpdatableRecordImpl<TestSuiteRecord> implements Record13<Long, Long, String, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> {

    private static final long serialVersionUID = -1116678144;

    /**
     * Setter for <code>reportcard.test_suite.test_suite_id</code>.
     */
    public TestSuiteRecord setTestSuiteId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.test_suite_id</code>.
     */
    public Long getTestSuiteId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.test_suite.test_result_fk</code>.
     */
    public TestSuiteRecord setTestResultFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.test_result_fk</code>.
     */
    public Long getTestResultFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>reportcard.test_suite.name</code>.
     */
    public TestSuiteRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>reportcard.test_suite.tests</code>.
     */
    public TestSuiteRecord setTests(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.tests</code>.
     */
    public Integer getTests() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>reportcard.test_suite.skipped</code>.
     */
    public TestSuiteRecord setSkipped(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.skipped</code>.
     */
    public Integer getSkipped() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>reportcard.test_suite.error</code>.
     */
    public TestSuiteRecord setError(Integer value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.error</code>.
     */
    public Integer getError() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>reportcard.test_suite.failure</code>.
     */
    public TestSuiteRecord setFailure(Integer value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.failure</code>.
     */
    public Integer getFailure() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>reportcard.test_suite.time</code>.
     */
    public TestSuiteRecord setTime(BigDecimal value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.time</code>.
     */
    public BigDecimal getTime() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>reportcard.test_suite.package</code>.
     */
    public TestSuiteRecord setPackage(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.package</code>.
     */
    public String getPackage() {
        return (String) get(8);
    }

    /**
     * Setter for <code>reportcard.test_suite.group</code>.
     */
    public TestSuiteRecord setGroup(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.group</code>.
     */
    public String getGroup() {
        return (String) get(9);
    }

    /**
     * Setter for <code>reportcard.test_suite.properties</code>.
     */
    public TestSuiteRecord setProperties(String value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.properties</code>.
     */
    public String getProperties() {
        return (String) get(10);
    }

    /**
     * Setter for <code>reportcard.test_suite.is_success</code>.
     */
    public TestSuiteRecord setIsSuccess(Boolean value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.is_success</code>.
     */
    public Boolean getIsSuccess() {
        return (Boolean) get(11);
    }

    /**
     * Setter for <code>reportcard.test_suite.has_skip</code>.
     */
    public TestSuiteRecord setHasSkip(Boolean value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.has_skip</code>.
     */
    public Boolean getHasSkip() {
        return (Boolean) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, Long, String, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<Long, Long, String, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TestSuiteTable.TEST_SUITE.TEST_SUITE_ID;
    }

    @Override
    public Field<Long> field2() {
        return TestSuiteTable.TEST_SUITE.TEST_RESULT_FK;
    }

    @Override
    public Field<String> field3() {
        return TestSuiteTable.TEST_SUITE.NAME;
    }

    @Override
    public Field<Integer> field4() {
        return TestSuiteTable.TEST_SUITE.TESTS;
    }

    @Override
    public Field<Integer> field5() {
        return TestSuiteTable.TEST_SUITE.SKIPPED;
    }

    @Override
    public Field<Integer> field6() {
        return TestSuiteTable.TEST_SUITE.ERROR;
    }

    @Override
    public Field<Integer> field7() {
        return TestSuiteTable.TEST_SUITE.FAILURE;
    }

    @Override
    public Field<BigDecimal> field8() {
        return TestSuiteTable.TEST_SUITE.TIME;
    }

    @Override
    public Field<String> field9() {
        return TestSuiteTable.TEST_SUITE.PACKAGE;
    }

    @Override
    public Field<String> field10() {
        return TestSuiteTable.TEST_SUITE.GROUP;
    }

    @Override
    public Field<String> field11() {
        return TestSuiteTable.TEST_SUITE.PROPERTIES;
    }

    @Override
    public Field<Boolean> field12() {
        return TestSuiteTable.TEST_SUITE.IS_SUCCESS;
    }

    @Override
    public Field<Boolean> field13() {
        return TestSuiteTable.TEST_SUITE.HAS_SKIP;
    }

    @Override
    public Long component1() {
        return getTestSuiteId();
    }

    @Override
    public Long component2() {
        return getTestResultFk();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Integer component4() {
        return getTests();
    }

    @Override
    public Integer component5() {
        return getSkipped();
    }

    @Override
    public Integer component6() {
        return getError();
    }

    @Override
    public Integer component7() {
        return getFailure();
    }

    @Override
    public BigDecimal component8() {
        return getTime();
    }

    @Override
    public String component9() {
        return getPackage();
    }

    @Override
    public String component10() {
        return getGroup();
    }

    @Override
    public String component11() {
        return getProperties();
    }

    @Override
    public Boolean component12() {
        return getIsSuccess();
    }

    @Override
    public Boolean component13() {
        return getHasSkip();
    }

    @Override
    public Long value1() {
        return getTestSuiteId();
    }

    @Override
    public Long value2() {
        return getTestResultFk();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public Integer value4() {
        return getTests();
    }

    @Override
    public Integer value5() {
        return getSkipped();
    }

    @Override
    public Integer value6() {
        return getError();
    }

    @Override
    public Integer value7() {
        return getFailure();
    }

    @Override
    public BigDecimal value8() {
        return getTime();
    }

    @Override
    public String value9() {
        return getPackage();
    }

    @Override
    public String value10() {
        return getGroup();
    }

    @Override
    public String value11() {
        return getProperties();
    }

    @Override
    public Boolean value12() {
        return getIsSuccess();
    }

    @Override
    public Boolean value13() {
        return getHasSkip();
    }

    @Override
    public TestSuiteRecord value1(Long value) {
        setTestSuiteId(value);
        return this;
    }

    @Override
    public TestSuiteRecord value2(Long value) {
        setTestResultFk(value);
        return this;
    }

    @Override
    public TestSuiteRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public TestSuiteRecord value4(Integer value) {
        setTests(value);
        return this;
    }

    @Override
    public TestSuiteRecord value5(Integer value) {
        setSkipped(value);
        return this;
    }

    @Override
    public TestSuiteRecord value6(Integer value) {
        setError(value);
        return this;
    }

    @Override
    public TestSuiteRecord value7(Integer value) {
        setFailure(value);
        return this;
    }

    @Override
    public TestSuiteRecord value8(BigDecimal value) {
        setTime(value);
        return this;
    }

    @Override
    public TestSuiteRecord value9(String value) {
        setPackage(value);
        return this;
    }

    @Override
    public TestSuiteRecord value10(String value) {
        setGroup(value);
        return this;
    }

    @Override
    public TestSuiteRecord value11(String value) {
        setProperties(value);
        return this;
    }

    @Override
    public TestSuiteRecord value12(Boolean value) {
        setIsSuccess(value);
        return this;
    }

    @Override
    public TestSuiteRecord value13(Boolean value) {
        setHasSkip(value);
        return this;
    }

    @Override
    public TestSuiteRecord values(Long value1, Long value2, String value3, Integer value4, Integer value5, Integer value6, Integer value7, BigDecimal value8, String value9, String value10, String value11, Boolean value12, Boolean value13) {
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
        value12(value12);
        value13(value13);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TestSuiteRecord
     */
    public TestSuiteRecord() {
        super(TestSuiteTable.TEST_SUITE);
    }

    /**
     * Create a detached, initialised TestSuiteRecord
     */
    public TestSuiteRecord(Long testSuiteId, Long testResultFk, String name, Integer tests, Integer skipped, Integer error, Integer failure, BigDecimal time, String package_, String group, String properties, Boolean isSuccess, Boolean hasSkip) {
        super(TestSuiteTable.TEST_SUITE);

        setTestSuiteId(testSuiteId);
        setTestResultFk(testResultFk);
        setName(name);
        setTests(tests);
        setSkipped(skipped);
        setError(error);
        setFailure(failure);
        setTime(time);
        setPackage(package_);
        setGroup(group);
        setProperties(properties);
        setIsSuccess(isSuccess);
        setHasSkip(hasSkip);
    }

    /**
     * Create a detached, initialised TestSuiteRecord
     */
    public TestSuiteRecord(TestSuitePojo value) {
        super(TestSuiteTable.TEST_SUITE);

        if (value != null) {
            setTestSuiteId(value.getTestSuiteId());
            setTestResultFk(value.getTestResultFk());
            setName(value.getName());
            setTests(value.getTests());
            setSkipped(value.getSkipped());
            setError(value.getError());
            setFailure(value.getFailure());
            setTime(value.getTime());
            setPackage(value.getPackage_());
            setGroup(value.getGroup());
            setProperties(value.getProperties());
            setIsSuccess(value.getIsSuccess());
            setHasSkip(value.getHasSkip());
        }
    }
}
