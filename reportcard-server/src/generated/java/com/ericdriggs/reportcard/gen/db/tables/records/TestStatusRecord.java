/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.records;


import com.ericdriggs.reportcard.gen.db.tables.TestStatus;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestStatusRecord extends UpdatableRecordImpl<TestStatusRecord> implements Record2<Byte, String> {

    private static final long serialVersionUID = -242422172;

    /**
     * Setter for <code>reportcard.test_status.test_status_id</code>.
     */
    public TestStatusRecord setTestStatusId(Byte value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_status.test_status_id</code>.
     */
    public Byte getTestStatusId() {
        return (Byte) get(0);
    }

    /**
     * Setter for <code>reportcard.test_status.test_status_name</code>.
     */
    public TestStatusRecord setTestStatusName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.test_status.test_status_name</code>.
     */
    public String getTestStatusName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Byte> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Byte, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Byte, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Byte> field1() {
        return TestStatus.TEST_STATUS.TEST_STATUS_ID;
    }

    @Override
    public Field<String> field2() {
        return TestStatus.TEST_STATUS.TEST_STATUS_NAME;
    }

    @Override
    public Byte component1() {
        return getTestStatusId();
    }

    @Override
    public String component2() {
        return getTestStatusName();
    }

    @Override
    public Byte value1() {
        return getTestStatusId();
    }

    @Override
    public String value2() {
        return getTestStatusName();
    }

    @Override
    public TestStatusRecord value1(Byte value) {
        setTestStatusId(value);
        return this;
    }

    @Override
    public TestStatusRecord value2(String value) {
        setTestStatusName(value);
        return this;
    }

    @Override
    public TestStatusRecord values(Byte value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TestStatusRecord
     */
    public TestStatusRecord() {
        super(TestStatus.TEST_STATUS);
    }

    /**
     * Create a detached, initialised TestStatusRecord
     */
    public TestStatusRecord(Byte testStatusId, String testStatusName) {
        super(TestStatus.TEST_STATUS);

        set(0, testStatusId);
        set(1, testStatusName);
    }
}
