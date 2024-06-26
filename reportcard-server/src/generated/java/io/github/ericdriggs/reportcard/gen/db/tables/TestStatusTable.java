/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.ReportcardTable;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestStatusRecord;

import java.util.function.Function;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
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
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestStatusTable extends TableImpl<TestStatusRecord> {

    private static final long serialVersionUID = 947296408;

    /**
     * The reference instance of <code>reportcard.test_status</code>
     */
    public static final TestStatusTable TEST_STATUS = new TestStatusTable();

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

    private TestStatusTable(Name alias, Table<TestStatusRecord> aliased) {
        this(alias, aliased, null);
    }

    private TestStatusTable(Name alias, Table<TestStatusRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.test_status</code> table reference
     */
    public TestStatusTable(String alias) {
        this(DSL.name(alias), TEST_STATUS);
    }

    /**
     * Create an aliased <code>reportcard.test_status</code> table reference
     */
    public TestStatusTable(Name alias) {
        this(alias, TEST_STATUS);
    }

    /**
     * Create a <code>reportcard.test_status</code> table reference
     */
    public TestStatusTable() {
        this(DSL.name("test_status"), null);
    }

    public <O extends Record> TestStatusTable(Table<O> child, ForeignKey<O, TestStatusRecord> key) {
        super(child, key, TEST_STATUS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ReportcardTable.REPORTCARD;
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
    public TestStatusTable as(String alias) {
        return new TestStatusTable(DSL.name(alias), this);
    }

    @Override
    public TestStatusTable as(Name alias) {
        return new TestStatusTable(alias, this);
    }

    @Override
    public TestStatusTable as(Table<?> alias) {
        return new TestStatusTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TestStatusTable rename(String name) {
        return new TestStatusTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestStatusTable rename(Name name) {
        return new TestStatusTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestStatusTable rename(Table<?> name) {
        return new TestStatusTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Byte, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Byte, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Byte, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
