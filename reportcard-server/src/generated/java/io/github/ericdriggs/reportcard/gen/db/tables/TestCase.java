/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Indexes;
import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestCaseRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
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
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestCase extends TableImpl<TestCaseRecord> {

    private static final long serialVersionUID = -1830193229;

    /**
     * The reference instance of <code>reportcard.test_case</code>
     */
    public static final TestCase TEST_CASE = new TestCase();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TestCaseRecord> getRecordType() {
        return TestCaseRecord.class;
    }

    /**
     * The column <code>reportcard.test_case.test_case_id</code>.
     */
    public final TableField<TestCaseRecord, Long> TEST_CASE_ID = createField(DSL.name("test_case_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.test_case.test_suite_fk</code>.
     */
    public final TableField<TestCaseRecord, Long> TEST_SUITE_FK = createField(DSL.name("test_suite_fk"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_case.test_status_fk</code>.
     */
    public final TableField<TestCaseRecord, Byte> TEST_STATUS_FK = createField(DSL.name("test_status_fk"), SQLDataType.TINYINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_case.name</code>.
     */
    public final TableField<TestCaseRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(1024).nullable(false), this, "");

    /**
     * The column <code>reportcard.test_case.class_name</code>.
     */
    public final TableField<TestCaseRecord, String> CLASS_NAME = createField(DSL.name("class_name"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>reportcard.test_case.time</code>.
     */
    public final TableField<TestCaseRecord, BigDecimal> TIME = createField(DSL.name("time"), SQLDataType.DECIMAL(9, 3), this, "");

    /**
     * The column <code>reportcard.test_case.system_out</code>.
     */
    public final TableField<TestCaseRecord, String> SYSTEM_OUT = createField(DSL.name("system_out"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>reportcard.test_case.system_err</code>.
     */
    public final TableField<TestCaseRecord, String> SYSTEM_ERR = createField(DSL.name("system_err"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>reportcard.test_case.assertions</code>.
     */
    public final TableField<TestCaseRecord, String> ASSERTIONS = createField(DSL.name("assertions"), SQLDataType.CLOB, this, "");

    private TestCase(Name alias, Table<TestCaseRecord> aliased) {
        this(alias, aliased, null);
    }

    private TestCase(Name alias, Table<TestCaseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.test_case</code> table reference
     */
    public TestCase(String alias) {
        this(DSL.name(alias), TEST_CASE);
    }

    /**
     * Create an aliased <code>reportcard.test_case</code> table reference
     */
    public TestCase(Name alias) {
        this(alias, TEST_CASE);
    }

    /**
     * Create a <code>reportcard.test_case</code> table reference
     */
    public TestCase() {
        this(DSL.name("test_case"), null);
    }

    public <O extends Record> TestCase(Table<O> child, ForeignKey<O, TestCaseRecord> key) {
        super(child, key, TEST_CASE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.TEST_CASE_FK_TEST_CASE_STATUS_IDX, Indexes.TEST_CASE_FK_TEST_CASE_TEST_SUITE_IDX);
    }

    @Override
    public Identity<TestCaseRecord, Long> getIdentity() {
        return (Identity<TestCaseRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<TestCaseRecord> getPrimaryKey() {
        return Keys.KEY_TEST_CASE_PRIMARY;
    }

    @Override
    public List<ForeignKey<TestCaseRecord, ?>> getReferences() {
        return Arrays.asList(Keys.FK_TEST_CASE_TEST_SUITE, Keys.FK_TEST_CASE_TEST_STATUS);
    }

    private transient TestSuite _testSuite;
    private transient TestStatus _testStatus;

    /**
     * Get the implicit join path to the <code>reportcard.test_suite</code>
     * table.
     */
    public TestSuite testSuite() {
        if (_testSuite == null)
            _testSuite = new TestSuite(this, Keys.FK_TEST_CASE_TEST_SUITE);

        return _testSuite;
    }

    /**
     * Get the implicit join path to the <code>reportcard.test_status</code>
     * table.
     */
    public TestStatus testStatus() {
        if (_testStatus == null)
            _testStatus = new TestStatus(this, Keys.FK_TEST_CASE_TEST_STATUS);

        return _testStatus;
    }

    @Override
    public TestCase as(String alias) {
        return new TestCase(DSL.name(alias), this);
    }

    @Override
    public TestCase as(Name alias) {
        return new TestCase(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TestCase rename(String name) {
        return new TestCase(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestCase rename(Name name) {
        return new TestCase(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, Byte, String, String, BigDecimal, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
