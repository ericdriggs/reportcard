/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.records;


import com.ericdriggs.reportcard.db.tables.Stage;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StageRecord extends UpdatableRecordImpl<StageRecord> implements Record3<Integer, String, Integer> {

    private static final long serialVersionUID = -1166025207;

    /**
     * Setter for <code>reportcard.stage.stage_id</code>.
     */
    public StageRecord setStageId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.stage_id</code>.
     */
    public Integer getStageId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.stage.stage_name</code>.
     */
    public StageRecord setStageName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.stage_name</code>.
     */
    public String getStageName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>reportcard.stage.app_branch_fk</code>.
     */
    public StageRecord setAppBranchFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.app_branch_fk</code>.
     */
    public Integer getAppBranchFk() {
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
        return Stage.STAGE.STAGE_ID;
    }

    @Override
    public Field<String> field2() {
        return Stage.STAGE.STAGE_NAME;
    }

    @Override
    public Field<Integer> field3() {
        return Stage.STAGE.APP_BRANCH_FK;
    }

    @Override
    public Integer component1() {
        return getStageId();
    }

    @Override
    public String component2() {
        return getStageName();
    }

    @Override
    public Integer component3() {
        return getAppBranchFk();
    }

    @Override
    public Integer value1() {
        return getStageId();
    }

    @Override
    public String value2() {
        return getStageName();
    }

    @Override
    public Integer value3() {
        return getAppBranchFk();
    }

    @Override
    public StageRecord value1(Integer value) {
        setStageId(value);
        return this;
    }

    @Override
    public StageRecord value2(String value) {
        setStageName(value);
        return this;
    }

    @Override
    public StageRecord value3(Integer value) {
        setAppBranchFk(value);
        return this;
    }

    @Override
    public StageRecord values(Integer value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached StageRecord
     */
    public StageRecord() {
        super(Stage.STAGE);
    }

    /**
     * Create a detached, initialised StageRecord
     */
    public StageRecord(Integer stageId, String stageName, Integer appBranchFk) {
        super(Stage.STAGE);

        set(0, stageId);
        set(1, stageName);
        set(2, appBranchFk);
    }
}