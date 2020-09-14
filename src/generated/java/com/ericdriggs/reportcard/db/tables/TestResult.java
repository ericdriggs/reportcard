/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables;


import com.ericdriggs.reportcard.db.Indexes;
import com.ericdriggs.reportcard.db.Keys;
import com.ericdriggs.reportcard.db.Reportcard;
import com.ericdriggs.reportcard.db.tables.records.TestResultRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row10;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestResult extends TableImpl<TestResultRecord> {

    private static final long serialVersionUID = 1580938508;

    /**
     * The reference instance of <code>reportcard.test_result</code>
     */
    public static final TestResult TEST_RESULT = new TestResult();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TestResultRecord> getRecordType() {
        return TestResultRecord.class;
    }

    /**
     * The column <code>reportcard.test_result.test_result_id</code>.
     */
    public final TableField<TestResultRecord, Long> TEST_RESULT_ID = createField(DSL.name("test_result_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.test_result.build_stage_fk</code>.
     */
    public final TableField<TestResultRecord, Long> BUILD_STAGE_FK = createField(DSL.name("build_stage_fk"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_result.tests</code>.
     */
    public final TableField<TestResultRecord, Integer> TESTS = createField(DSL.name("tests"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_result.skipped</code>.
     */
    public final TableField<TestResultRecord, Integer> SKIPPED = createField(DSL.name("skipped"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_result.error</code>.
     */
    public final TableField<TestResultRecord, Integer> ERROR = createField(DSL.name("error"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_result.failure</code>.
     */
    public final TableField<TestResultRecord, Integer> FAILURE = createField(DSL.name("failure"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_result.time</code>.
     */
    public final TableField<TestResultRecord, BigDecimal> TIME = createField(DSL.name("time"), org.jooq.impl.SQLDataType.DECIMAL(9, 3).nullable(false), this, "");

    /**
     * The column <code>reportcard.test_result.test_result_created</code>.
     */
    public final TableField<TestResultRecord, LocalDateTime> TEST_RESULT_CREATED = createField(DSL.name("test_result_created"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>reportcard.test_result.is_success</code>.
     */
    public final TableField<TestResultRecord, Byte> IS_SUCCESS = createField(DSL.name("is_success"), org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>reportcard.test_result.has_skip</code>.
     */
    public final TableField<TestResultRecord, Byte> HAS_SKIP = createField(DSL.name("has_skip"), org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * Create a <code>reportcard.test_result</code> table reference
     */
    public TestResult() {
        this(DSL.name("test_result"), null);
    }

    /**
     * Create an aliased <code>reportcard.test_result</code> table reference
     */
    public TestResult(String alias) {
        this(DSL.name(alias), TEST_RESULT);
    }

    /**
     * Create an aliased <code>reportcard.test_result</code> table reference
     */
    public TestResult(Name alias) {
        this(alias, TEST_RESULT);
    }

    private TestResult(Name alias, Table<TestResultRecord> aliased) {
        this(alias, aliased, null);
    }

    private TestResult(Name alias, Table<TestResultRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> TestResult(Table<O> child, ForeignKey<O, TestResultRecord> key) {
        super(child, key, TEST_RESULT);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.TEST_RESULT_TEST_RESULT_FK_BUILD_STAGE_IDX);
    }

    @Override
    public Identity<TestResultRecord, Long> getIdentity() {
        return Keys.IDENTITY_TEST_RESULT;
    }

    @Override
    public UniqueKey<TestResultRecord> getPrimaryKey() {
        return Keys.KEY_TEST_RESULT_PRIMARY;
    }

    @Override
    public List<UniqueKey<TestResultRecord>> getKeys() {
        return Arrays.<UniqueKey<TestResultRecord>>asList(Keys.KEY_TEST_RESULT_PRIMARY);
    }

    @Override
    public List<ForeignKey<TestResultRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<TestResultRecord, ?>>asList(Keys.FK_TEST_RESULT_BUILD_STAGE);
    }

    public BuildStage buildStage() {
        return new BuildStage(this, Keys.FK_TEST_RESULT_BUILD_STAGE);
    }

    @Override
    public TestResult as(String alias) {
        return new TestResult(DSL.name(alias), this);
    }

    @Override
    public TestResult as(Name alias) {
        return new TestResult(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TestResult rename(String name) {
        return new TestResult(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestResult rename(Name name) {
        return new TestResult(name, null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, LocalDateTime, Byte, Byte> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
