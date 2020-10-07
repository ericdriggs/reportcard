/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.records;


import com.ericdriggs.reportcard.gen.db.tables.Branch;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BranchRecord extends UpdatableRecordImpl<BranchRecord> implements Record3<Integer, String, Integer> {

    private static final long serialVersionUID = 1277432308;

    /**
     * Setter for <code>reportcard.branch.branch_id</code>.
     */
    public BranchRecord setBranchId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.branch.branch_id</code>.
     */
    public Integer getBranchId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.branch.branch_name</code>.
     */
    public BranchRecord setBranchName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.branch.branch_name</code>.
     */
    public String getBranchName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>reportcard.branch.repo_fk</code>.
     */
    public BranchRecord setRepoFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.branch.repo_fk</code>.
     */
    public Integer getRepoFk() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, String, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Branch.BRANCH.BRANCH_ID;
    }

    @Override
    public Field<String> field2() {
        return Branch.BRANCH.BRANCH_NAME;
    }

    @Override
    public Field<Integer> field3() {
        return Branch.BRANCH.REPO_FK;
    }

    @Override
    public Integer component1() {
        return getBranchId();
    }

    @Override
    public String component2() {
        return getBranchName();
    }

    @Override
    public Integer component3() {
        return getRepoFk();
    }

    @Override
    public Integer value1() {
        return getBranchId();
    }

    @Override
    public String value2() {
        return getBranchName();
    }

    @Override
    public Integer value3() {
        return getRepoFk();
    }

    @Override
    public BranchRecord value1(Integer value) {
        setBranchId(value);
        return this;
    }

    @Override
    public BranchRecord value2(String value) {
        setBranchName(value);
        return this;
    }

    @Override
    public BranchRecord value3(Integer value) {
        setRepoFk(value);
        return this;
    }

    @Override
    public BranchRecord values(Integer value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BranchRecord
     */
    public BranchRecord() {
        super(Branch.BRANCH);
    }

    /**
     * Create a detached, initialised BranchRecord
     */
    public BranchRecord(Integer branchId, String branchName, Integer repoFk) {
        super(Branch.BRANCH);

        set(0, branchId);
        set(1, branchName);
        set(2, repoFk);
    }
}
