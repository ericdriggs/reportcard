/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Indexes;
import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestSuiteRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row12;
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
public class TestSuite extends TableImpl<TestSuiteRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>reportcard.test_suite</code>
     */
    public static final TestSuite TEST_SUITE = new TestSuite();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TestSuiteRecord> getRecordType() {
        return TestSuiteRecord.class;
    }

    /**
     * The column <code>reportcard.test_suite.test_suite_id</code>.
     */
    public final TableField<TestSuiteRecord, Long> TEST_SUITE_ID = createField(DSL.name("test_suite_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.test_suite.test_result_fk</code>.
     */
    public final TableField<TestSuiteRecord, Long> TEST_RESULT_FK = createField(DSL.name("test_result_fk"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_suite.tests</code>.
     */
    public final TableField<TestSuiteRecord, Integer> TESTS = createField(DSL.name("tests"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_suite.skipped</code>.
     */
    public final TableField<TestSuiteRecord, Integer> SKIPPED = createField(DSL.name("skipped"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_suite.error</code>.
     */
    public final TableField<TestSuiteRecord, Integer> ERROR = createField(DSL.name("error"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_suite.failure</code>.
     */
    public final TableField<TestSuiteRecord, Integer> FAILURE = createField(DSL.name("failure"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.test_suite.time</code>.
     */
    public final TableField<TestSuiteRecord, BigDecimal> TIME = createField(DSL.name("time"), SQLDataType.DECIMAL(9, 3).nullable(false), this, "");

    /**
     * The column <code>reportcard.test_suite.package</code>.
     */
    public final TableField<TestSuiteRecord, String> PACKAGE = createField(DSL.name("package"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>reportcard.test_suite.group</code>.
     */
    public final TableField<TestSuiteRecord, String> GROUP = createField(DSL.name("group"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>reportcard.test_suite.properties</code>.
     */
    public final TableField<TestSuiteRecord, String> PROPERTIES = createField(DSL.name("properties"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>reportcard.test_suite.is_success</code>.
     */
    public final TableField<TestSuiteRecord, Boolean> IS_SUCCESS = createField(DSL.name("is_success"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>reportcard.test_suite.has_skip</code>.
     */
    public final TableField<TestSuiteRecord, Boolean> HAS_SKIP = createField(DSL.name("has_skip"), SQLDataType.BOOLEAN, this, "");

    private TestSuite(Name alias, Table<TestSuiteRecord> aliased) {
        this(alias, aliased, null);
    }

    private TestSuite(Name alias, Table<TestSuiteRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.test_suite</code> table reference
     */
    public TestSuite(String alias) {
        this(DSL.name(alias), TEST_SUITE);
    }

    /**
     * Create an aliased <code>reportcard.test_suite</code> table reference
     */
    public TestSuite(Name alias) {
        this(alias, TEST_SUITE);
    }

    /**
     * Create a <code>reportcard.test_suite</code> table reference
     */
    public TestSuite() {
        this(DSL.name("test_suite"), null);
    }

    public <O extends Record> TestSuite(Table<O> child, ForeignKey<O, TestSuiteRecord> key) {
        super(child, key, TEST_SUITE);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.TEST_SUITE_TEST_RESULT_FK_IDX);
    }

    @Override
    public Identity<TestSuiteRecord, Long> getIdentity() {
        return (Identity<TestSuiteRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<TestSuiteRecord> getPrimaryKey() {
        return Keys.KEY_TEST_SUITE_PRIMARY;
    }

    @Override
    public List<UniqueKey<TestSuiteRecord>> getKeys() {
        return Arrays.<UniqueKey<TestSuiteRecord>>asList(Keys.KEY_TEST_SUITE_PRIMARY);
    }

    @Override
    public List<ForeignKey<TestSuiteRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<TestSuiteRecord, ?>>asList(Keys.TEST_RESULT_FK);
    }

    private transient TestResult _testResult;

    public TestResult testResult() {
        if (_testResult == null)
            _testResult = new TestResult(this, Keys.TEST_RESULT_FK);

        return _testResult;
    }

    @Override
    public TestSuite as(String alias) {
        return new TestSuite(DSL.name(alias), this);
    }

    @Override
    public TestSuite as(Name alias) {
        return new TestSuite(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TestSuite rename(String name) {
        return new TestSuite(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestSuite rename(Name name) {
        return new TestSuite(name, null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, Integer, Integer, Integer, Integer, BigDecimal, String, String, String, Boolean, Boolean> fieldsRow() {
        return (Row12) super.fieldsRow();
    }
}
