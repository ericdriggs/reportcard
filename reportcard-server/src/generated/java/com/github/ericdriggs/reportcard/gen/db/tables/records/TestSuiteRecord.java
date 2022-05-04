/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.TestSuite;

import java.math.BigDecimal;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


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
public class TestSuiteRecord extends UpdatableRecordImpl<TestSuiteRecord> implements Record12<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> {

    private static final long serialVersionUID = 1L;

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
     * Setter for <code>reportcard.test_suite.tests</code>.
     */
    public TestSuiteRecord setTests(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.tests</code>.
     */
    public Integer getTests() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>reportcard.test_suite.skipped</code>.
     */
    public TestSuiteRecord setSkipped(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.skipped</code>.
     */
    public Integer getSkipped() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>reportcard.test_suite.error</code>.
     */
    public TestSuiteRecord setError(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.error</code>.
     */
    public Integer getError() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>reportcard.test_suite.failure</code>.
     */
    public TestSuiteRecord setFailure(Integer value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.failure</code>.
     */
    public Integer getFailure() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>reportcard.test_suite.time</code>.
     */
    public TestSuiteRecord setTime(BigDecimal value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.time</code>.
     */
    public BigDecimal getTime() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>reportcard.test_suite.package</code>.
     */
    public TestSuiteRecord setPackage(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.package</code>.
     */
    public String getPackage() {
        return (String) get(7);
    }

    /**
     * Setter for <code>reportcard.test_suite.group</code>.
     */
    public TestSuiteRecord setGroup(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.group</code>.
     */
    public String getGroup() {
        return (String) get(8);
    }

    /**
     * Setter for <code>reportcard.test_suite.properties</code>.
     */
    public TestSuiteRecord setProperties(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.properties</code>.
     */
    public String getProperties() {
        return (String) get(9);
    }

    /**
     * Setter for <code>reportcard.test_suite.is_success</code>.
     */
    public TestSuiteRecord setIsSuccess(Boolean value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.is_success</code>.
     */
    public Boolean getIsSuccess() {
        return (Boolean) get(10);
    }

    /**
     * Setter for <code>reportcard.test_suite.has_skip</code>.
     */
    public TestSuiteRecord setHasSkip(Boolean value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_suite.has_skip</code>.
     */
    public Boolean getHasSkip() {
        return (Boolean) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TestSuite.TEST_SUITE.TEST_SUITE_ID;
    }

    @Override
    public Field<Long> field2() {
        return TestSuite.TEST_SUITE.TEST_RESULT_FK;
    }

    @Override
    public Field<Integer> field3() {
        return TestSuite.TEST_SUITE.TESTS;
    }

    @Override
    public Field<Integer> field4() {
        return TestSuite.TEST_SUITE.SKIPPED;
    }

    @Override
    public Field<Integer> field5() {
        return TestSuite.TEST_SUITE.ERROR;
    }

    @Override
    public Field<Integer> field6() {
        return TestSuite.TEST_SUITE.FAILURE;
    }

    @Override
    public Field<BigDecimal> field7() {
        return TestSuite.TEST_SUITE.TIME;
    }

    @Override
    public Field<String> field8() {
        return TestSuite.TEST_SUITE.PACKAGE;
    }

    @Override
    public Field<String> field9() {
        return TestSuite.TEST_SUITE.GROUP;
    }

    @Override
    public Field<String> field10() {
        return TestSuite.TEST_SUITE.PROPERTIES;
    }

    @Override
    public Field<Boolean> field11() {
        return TestSuite.TEST_SUITE.IS_SUCCESS;
    }

    @Override
    public Field<Boolean> field12() {
        return TestSuite.TEST_SUITE.HAS_SKIP;
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
    public String component8() {
        return getPackage();
    }

    @Override
    public String component9() {
        return getGroup();
    }

    @Override
    public String component10() {
        return getProperties();
    }

    @Override
    public Boolean component11() {
        return getIsSuccess();
    }

    @Override
    public Boolean component12() {
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
    public String value8() {
        return getPackage();
    }

    @Override
    public String value9() {
        return getGroup();
    }

    @Override
    public String value10() {
        return getProperties();
    }

    @Override
    public Boolean value11() {
        return getIsSuccess();
    }

    @Override
    public Boolean value12() {
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
    public TestSuiteRecord value3(Integer value) {
        setTests(value);
        return this;
    }

    @Override
    public TestSuiteRecord value4(Integer value) {
        setSkipped(value);
        return this;
    }

    @Override
    public TestSuiteRecord value5(Integer value) {
        setError(value);
        return this;
    }

    @Override
    public TestSuiteRecord value6(Integer value) {
        setFailure(value);
        return this;
    }

    @Override
    public TestSuiteRecord value7(BigDecimal value) {
        setTime(value);
        return this;
    }

    @Override
    public TestSuiteRecord value8(String value) {
        setPackage(value);
        return this;
    }

    @Override
    public TestSuiteRecord value9(String value) {
        setGroup(value);
        return this;
    }

    @Override
    public TestSuiteRecord value10(String value) {
        setProperties(value);
        return this;
    }

    @Override
    public TestSuiteRecord value11(Boolean value) {
        setIsSuccess(value);
        return this;
    }

    @Override
    public TestSuiteRecord value12(Boolean value) {
        setHasSkip(value);
        return this;
    }

    @Override
    public TestSuiteRecord values(Long value1, Long value2, Integer value3, Integer value4, Integer value5, Integer value6, BigDecimal value7, String value8, String value9, String value10, Boolean value11, Boolean value12) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TestSuiteRecord
     */
    public TestSuiteRecord() {
        super(TestSuite.TEST_SUITE);
    }

    /**
     * Create a detached, initialised TestSuiteRecord
     */
    public TestSuiteRecord(Long testSuiteId, Long testResultFk, Integer tests, Integer skipped, Integer error, Integer failure, BigDecimal time, String package_, String group, String properties, Boolean isSuccess, Boolean hasSkip) {
        super(TestSuite.TEST_SUITE);

        setTestSuiteId(testSuiteId);
        setTestResultFk(testResultFk);
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
}
