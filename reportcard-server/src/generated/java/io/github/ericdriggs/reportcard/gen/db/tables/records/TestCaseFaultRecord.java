/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.TestCaseFaultTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestCaseFaultRecord extends UpdatableRecordImpl<TestCaseFaultRecord> implements Record6<Long, Long, Byte, String, String, String> {

    private static final long serialVersionUID = 968075530;

    /**
     * Setter for <code>reportcard.test_case_fault.test_case_fault_id</code>.
     */
    public TestCaseFaultRecord setTestCaseFaultId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case_fault.test_case_fault_id</code>.
     */
    public Long getTestCaseFaultId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.test_case_fault.test_case_fk</code>.
     */
    public TestCaseFaultRecord setTestCaseFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case_fault.test_case_fk</code>.
     */
    public Long getTestCaseFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>reportcard.test_case_fault.fault_context_fk</code>.
     */
    public TestCaseFaultRecord setFaultContextFk(Byte value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case_fault.fault_context_fk</code>.
     */
    public Byte getFaultContextFk() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>reportcard.test_case_fault.type</code>.
     */
    public TestCaseFaultRecord setType(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case_fault.type</code>.
     */
    public String getType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>reportcard.test_case_fault.message</code>.
     */
    public TestCaseFaultRecord setMessage(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case_fault.message</code>.
     */
    public String getMessage() {
        return (String) get(4);
    }

    /**
     * Setter for <code>reportcard.test_case_fault.value</code>.
     */
    public TestCaseFaultRecord setValue(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_case_fault.value</code>.
     */
    public String getValue() {
        return (String) get(5);
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
    public Row6<Long, Long, Byte, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, Long, Byte, String, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FAULT_ID;
    }

    @Override
    public Field<Long> field2() {
        return TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FK;
    }

    @Override
    public Field<Byte> field3() {
        return TestCaseFaultTable.TEST_CASE_FAULT.FAULT_CONTEXT_FK;
    }

    @Override
    public Field<String> field4() {
        return TestCaseFaultTable.TEST_CASE_FAULT.TYPE;
    }

    @Override
    public Field<String> field5() {
        return TestCaseFaultTable.TEST_CASE_FAULT.MESSAGE;
    }

    @Override
    public Field<String> field6() {
        return TestCaseFaultTable.TEST_CASE_FAULT.VALUE;
    }

    @Override
    public Long component1() {
        return getTestCaseFaultId();
    }

    @Override
    public Long component2() {
        return getTestCaseFk();
    }

    @Override
    public Byte component3() {
        return getFaultContextFk();
    }

    @Override
    public String component4() {
        return getType();
    }

    @Override
    public String component5() {
        return getMessage();
    }

    @Override
    public String component6() {
        return getValue();
    }

    @Override
    public Long value1() {
        return getTestCaseFaultId();
    }

    @Override
    public Long value2() {
        return getTestCaseFk();
    }

    @Override
    public Byte value3() {
        return getFaultContextFk();
    }

    @Override
    public String value4() {
        return getType();
    }

    @Override
    public String value5() {
        return getMessage();
    }

    @Override
    public String value6() {
        return getValue();
    }

    @Override
    public TestCaseFaultRecord value1(Long value) {
        setTestCaseFaultId(value);
        return this;
    }

    @Override
    public TestCaseFaultRecord value2(Long value) {
        setTestCaseFk(value);
        return this;
    }

    @Override
    public TestCaseFaultRecord value3(Byte value) {
        setFaultContextFk(value);
        return this;
    }

    @Override
    public TestCaseFaultRecord value4(String value) {
        setType(value);
        return this;
    }

    @Override
    public TestCaseFaultRecord value5(String value) {
        setMessage(value);
        return this;
    }

    @Override
    public TestCaseFaultRecord value6(String value) {
        setValue(value);
        return this;
    }

    @Override
    public TestCaseFaultRecord values(Long value1, Long value2, Byte value3, String value4, String value5, String value6) {
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
     * Create a detached TestCaseFaultRecord
     */
    public TestCaseFaultRecord() {
        super(TestCaseFaultTable.TEST_CASE_FAULT);
    }

    /**
     * Create a detached, initialised TestCaseFaultRecord
     */
    public TestCaseFaultRecord(Long testCaseFaultId, Long testCaseFk, Byte faultContextFk, String type, String message, String value) {
        super(TestCaseFaultTable.TEST_CASE_FAULT);

        setTestCaseFaultId(testCaseFaultId);
        setTestCaseFk(testCaseFk);
        setFaultContextFk(faultContextFk);
        setType(type);
        setMessage(message);
        setValue(value);
    }

    /**
     * Create a detached, initialised TestCaseFaultRecord
     */
    public TestCaseFaultRecord(TestCaseFaultPojo value) {
        super(TestCaseFaultTable.TEST_CASE_FAULT);

        if (value != null) {
            setTestCaseFaultId(value.getTestCaseFaultId());
            setTestCaseFk(value.getTestCaseFk());
            setFaultContextFk(value.getFaultContextFk());
            setType(value.getType());
            setMessage(value.getMessage());
            setValue(value.getValue());
        }
    }
}
