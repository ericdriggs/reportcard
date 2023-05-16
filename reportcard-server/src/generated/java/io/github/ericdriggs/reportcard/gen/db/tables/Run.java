/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Indexes;
import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.RunRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class Run extends TableImpl<RunRecord> {

    private static final long serialVersionUID = 704496551;

    /**
     * The reference instance of <code>reportcard.run</code>
     */
    public static final Run RUN = new Run();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RunRecord> getRecordType() {
        return RunRecord.class;
    }

    /**
     * The column <code>reportcard.run.run_id</code>.
     */
    public final TableField<RunRecord, Long> RUN_ID = createField(DSL.name("run_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.run.run_reference</code>.
     */
    public final TableField<RunRecord, String> RUN_REFERENCE = createField(DSL.name("run_reference"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>reportcard.run.job_fk</code>.
     */
    public final TableField<RunRecord, Long> JOB_FK = createField(DSL.name("job_fk"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.run.job_run_count</code>.
     */
    public final TableField<RunRecord, Integer> JOB_RUN_COUNT = createField(DSL.name("job_run_count"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>reportcard.run.sha</code>.
     */
    public final TableField<RunRecord, String> SHA = createField(DSL.name("sha"), SQLDataType.VARCHAR(128), this, "");

    /**
     * The column <code>reportcard.run.created</code>.
     */
    public final TableField<RunRecord, LocalDateTime> CREATED = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    private Run(Name alias, Table<RunRecord> aliased) {
        this(alias, aliased, null);
    }

    private Run(Name alias, Table<RunRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.run</code> table reference
     */
    public Run(String alias) {
        this(DSL.name(alias), RUN);
    }

    /**
     * Create an aliased <code>reportcard.run</code> table reference
     */
    public Run(Name alias) {
        this(alias, RUN);
    }

    /**
     * Create a <code>reportcard.run</code> table reference
     */
    public Run() {
        this(DSL.name("run"), null);
    }

    public <O extends Record> Run(Table<O> child, ForeignKey<O, RunRecord> key) {
        super(child, key, RUN);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.RUN_RUN_JOB_FK_IDX, Indexes.RUN_RUN_JOB_SHA);
    }

    @Override
    public Identity<RunRecord, Long> getIdentity() {
        return (Identity<RunRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<RunRecord> getPrimaryKey() {
        return Keys.KEY_RUN_PRIMARY;
    }

    @Override
    public List<UniqueKey<RunRecord>> getKeys() {
        return Arrays.<UniqueKey<RunRecord>>asList(Keys.KEY_RUN_PRIMARY, Keys.KEY_RUN_RUN_ID_UNIQUE, Keys.KEY_RUN_UQ_RUN_JOB_REFERENCE);
    }

    @Override
    public List<ForeignKey<RunRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<RunRecord, ?>>asList(Keys.RUN_JOB_FK);
    }

    private transient Job _job;

    public Job job() {
        if (_job == null)
            _job = new Job(this, Keys.RUN_JOB_FK);

        return _job;
    }

    @Override
    public Run as(String alias) {
        return new Run(DSL.name(alias), this);
    }

    @Override
    public Run as(Name alias) {
        return new Run(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Run rename(String name) {
        return new Run(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Run rename(Name name) {
        return new Run(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, Long, Integer, String, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}