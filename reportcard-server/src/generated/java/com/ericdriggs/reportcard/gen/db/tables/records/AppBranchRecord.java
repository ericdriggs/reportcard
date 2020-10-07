/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.records;


import com.ericdriggs.reportcard.gen.db.tables.AppBranch;

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
public class AppBranchRecord extends UpdatableRecordImpl<AppBranchRecord> implements Record3<Integer, Integer, Integer> {

    private static final long serialVersionUID = -788970278;

    /**
     * Setter for <code>reportcard.app_branch.app_branch_id</code>.
     */
    public AppBranchRecord setAppBranchId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.app_branch.app_branch_id</code>.
     */
    public Integer getAppBranchId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.app_branch.app_fk</code>.
     */
    public AppBranchRecord setAppFk(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.app_branch.app_fk</code>.
     */
    public Integer getAppFk() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>reportcard.app_branch.branch_fk</code>.
     */
    public AppBranchRecord setBranchFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.app_branch.branch_fk</code>.
     */
    public Integer getBranchFk() {
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
    public Row3<Integer, Integer, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, Integer, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return AppBranch.APP_BRANCH.APP_BRANCH_ID;
    }

    @Override
    public Field<Integer> field2() {
        return AppBranch.APP_BRANCH.APP_FK;
    }

    @Override
    public Field<Integer> field3() {
        return AppBranch.APP_BRANCH.BRANCH_FK;
    }

    @Override
    public Integer component1() {
        return getAppBranchId();
    }

    @Override
    public Integer component2() {
        return getAppFk();
    }

    @Override
    public Integer component3() {
        return getBranchFk();
    }

    @Override
    public Integer value1() {
        return getAppBranchId();
    }

    @Override
    public Integer value2() {
        return getAppFk();
    }

    @Override
    public Integer value3() {
        return getBranchFk();
    }

    @Override
    public AppBranchRecord value1(Integer value) {
        setAppBranchId(value);
        return this;
    }

    @Override
    public AppBranchRecord value2(Integer value) {
        setAppFk(value);
        return this;
    }

    @Override
    public AppBranchRecord value3(Integer value) {
        setBranchFk(value);
        return this;
    }

    @Override
    public AppBranchRecord values(Integer value1, Integer value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppBranchRecord
     */
    public AppBranchRecord() {
        super(AppBranch.APP_BRANCH);
    }

    /**
     * Create a detached, initialised AppBranchRecord
     */
    public AppBranchRecord(Integer appBranchId, Integer appFk, Integer branchFk) {
        super(AppBranch.APP_BRANCH);

        set(0, appBranchId);
        set(1, appFk);
        set(2, branchFk);
    }
}
