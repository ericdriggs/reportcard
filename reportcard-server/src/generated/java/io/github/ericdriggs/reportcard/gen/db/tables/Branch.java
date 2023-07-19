/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Indexes;
import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.BranchRecord;

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
import org.jooq.Row4;
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
public class Branch extends TableImpl<BranchRecord> {

    private static final long serialVersionUID = 1737046642;

    /**
     * The reference instance of <code>reportcard.branch</code>
     */
    public static final Branch BRANCH = new Branch();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BranchRecord> getRecordType() {
        return BranchRecord.class;
    }

    /**
     * The column <code>reportcard.branch.branch_id</code>.
     */
    public final TableField<BranchRecord, Integer> BRANCH_ID = createField(DSL.name("branch_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.branch.branch_name</code>.
     */
    public final TableField<BranchRecord, String> BRANCH_NAME = createField(DSL.name("branch_name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>reportcard.branch.repo_fk</code>.
     */
    public final TableField<BranchRecord, Integer> REPO_FK = createField(DSL.name("repo_fk"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>reportcard.branch.last_run</code>.
     */
    public final TableField<BranchRecord, LocalDateTime> LAST_RUN = createField(DSL.name("last_run"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.inline("utc_timestamp()", SQLDataType.LOCALDATETIME)), this, "");

    private Branch(Name alias, Table<BranchRecord> aliased) {
        this(alias, aliased, null);
    }

    private Branch(Name alias, Table<BranchRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.branch</code> table reference
     */
    public Branch(String alias) {
        this(DSL.name(alias), BRANCH);
    }

    /**
     * Create an aliased <code>reportcard.branch</code> table reference
     */
    public Branch(Name alias) {
        this(alias, BRANCH);
    }

    /**
     * Create a <code>reportcard.branch</code> table reference
     */
    public Branch() {
        this(DSL.name("branch"), null);
    }

    public <O extends Record> Branch(Table<O> child, ForeignKey<O, BranchRecord> key) {
        super(child, key, BRANCH);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BRANCH_BRANCH_REPO_IDX);
    }

    @Override
    public Identity<BranchRecord, Integer> getIdentity() {
        return (Identity<BranchRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<BranchRecord> getPrimaryKey() {
        return Keys.KEY_BRANCH_PRIMARY;
    }

    @Override
    public List<UniqueKey<BranchRecord>> getKeys() {
        return Arrays.<UniqueKey<BranchRecord>>asList(Keys.KEY_BRANCH_PRIMARY);
    }

    @Override
    public List<ForeignKey<BranchRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BranchRecord, ?>>asList(Keys.BRANCH_REPO_FK);
    }

    private transient Repo _repo;

    public Repo repo() {
        if (_repo == null)
            _repo = new Repo(this, Keys.BRANCH_REPO_FK);

        return _repo;
    }

    @Override
    public Branch as(String alias) {
        return new Branch(DSL.name(alias), this);
    }

    @Override
    public Branch as(Name alias) {
        return new Branch(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Branch rename(String name) {
        return new Branch(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Branch rename(Name name) {
        return new Branch(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, Integer, LocalDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
