/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables;


import com.ericdriggs.reportcard.gen.db.Keys;
import com.ericdriggs.reportcard.gen.db.Reportcard;
import com.ericdriggs.reportcard.gen.db.tables.records.TestStatusRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


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
public class TestStatus extends TableImpl<TestStatusRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>reportcard.test_status</code>
     */
    public static final TestStatus TEST_STATUS = new TestStatus();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TestStatusRecord> getRecordType() {
        return TestStatusRecord.class;
    }

    /**
     * The column <code>reportcard.test_status.test_status_id</code>.
     */
    public final TableField<TestStatusRecord, Byte> TEST_STATUS_ID = createField(DSL.name("test_status_id"), SQLDataType.TINYINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.test_status.test_status_name</code>.
     */
    public final TableField<TestStatusRecord, String> TEST_STATUS_NAME = createField(DSL.name("test_status_name"), SQLDataType.VARCHAR(64).nullable(false), this, "");

    private TestStatus(Name alias, Table<TestStatusRecord> aliased) {
        this(alias, aliased, null);
    }

    private TestStatus(Name alias, Table<TestStatusRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.test_status</code> table reference
     */
    public TestStatus(String alias) {
        this(DSL.name(alias), TEST_STATUS);
    }

    /**
     * Create an aliased <code>reportcard.test_status</code> table reference
     */
    public TestStatus(Name alias) {
        this(alias, TEST_STATUS);
    }

    /**
     * Create a <code>reportcard.test_status</code> table reference
     */
    public TestStatus() {
        this(DSL.name("test_status"), null);
    }

    public <O extends Record> TestStatus(Table<O> child, ForeignKey<O, TestStatusRecord> key) {
        super(child, key, TEST_STATUS);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public Identity<TestStatusRecord, Byte> getIdentity() {
        return (Identity<TestStatusRecord, Byte>) super.getIdentity();
    }

    @Override
    public UniqueKey<TestStatusRecord> getPrimaryKey() {
        return Keys.KEY_TEST_STATUS_PRIMARY;
    }

    @Override
    public List<UniqueKey<TestStatusRecord>> getKeys() {
        return Arrays.<UniqueKey<TestStatusRecord>>asList(Keys.KEY_TEST_STATUS_PRIMARY);
    }

    @Override
    public TestStatus as(String alias) {
        return new TestStatus(DSL.name(alias), this);
    }

    @Override
    public TestStatus as(Name alias) {
        return new TestStatus(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TestStatus rename(String name) {
        return new TestStatus(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestStatus rename(Name name) {
        return new TestStatus(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Byte, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
