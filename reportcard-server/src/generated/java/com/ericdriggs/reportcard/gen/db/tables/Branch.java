/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables;


import com.ericdriggs.reportcard.gen.db.Indexes;
import com.ericdriggs.reportcard.gen.db.Keys;
import com.ericdriggs.reportcard.gen.db.Reportcard;
import com.ericdriggs.reportcard.gen.db.tables.records.BranchRecord;

import java.util.Arrays;
import java.util.List;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Branch extends TableImpl<BranchRecord> {

    private static final long serialVersionUID = 1596064478;

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
    public final TableField<BranchRecord, Integer> BRANCH_ID = createField(DSL.name("branch_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.branch.branch_name</code>.
     */
    public final TableField<BranchRecord, String> BRANCH_NAME = createField(DSL.name("branch_name"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>reportcard.branch.repo_fk</code>.
     */
    public final TableField<BranchRecord, Integer> REPO_FK = createField(DSL.name("repo_fk"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>reportcard.branch</code> table reference
     */
    public Branch() {
        this(DSL.name("branch"), null);
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

    private Branch(Name alias, Table<BranchRecord> aliased) {
        this(alias, aliased, null);
    }

    private Branch(Name alias, Table<BranchRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
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
        return Keys.IDENTITY_BRANCH;
    }

    @Override
    public UniqueKey<BranchRecord> getPrimaryKey() {
        return Keys.KEY_BRANCH_PRIMARY;
    }

    @Override
    public List<UniqueKey<BranchRecord>> getKeys() {
        return Arrays.<UniqueKey<BranchRecord>>asList(Keys.KEY_BRANCH_PRIMARY, Keys.KEY_BRANCH_REPO_BRANCH_NAME_IDX);
    }

    @Override
    public List<ForeignKey<BranchRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BranchRecord, ?>>asList(Keys.BRANCH_REPO_FK);
    }

    public Repo repo() {
        return new Repo(this, Keys.BRANCH_REPO_FK);
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
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
