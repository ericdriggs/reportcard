/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.records;


import com.ericdriggs.reportcard.db.tables.BuildStage;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.13.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BuildStageRecord extends UpdatableRecordImpl<BuildStageRecord> implements Record3<Long, Long, Integer> {

    private static final long serialVersionUID = -2014188985;

    /**
     * Setter for <code>reportcard.build_stage.build_stage_id</code>.
     */
    public BuildStageRecord setBuildStageId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.build_stage.build_stage_id</code>.
     */
    public Long getBuildStageId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.build_stage.build_fk</code>.
     */
    public BuildStageRecord setBuildFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.build_stage.build_fk</code>.
     */
    public Long getBuildFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>reportcard.build_stage.stage_fk</code>.
     */
    public BuildStageRecord setStageFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.build_stage.stage_fk</code>.
     */
    public Integer getStageFk() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return BuildStage.BUILD_STAGE.BUILD_STAGE_ID;
    }

    @Override
    public Field<Long> field2() {
        return BuildStage.BUILD_STAGE.BUILD_FK;
    }

    @Override
    public Field<Integer> field3() {
        return BuildStage.BUILD_STAGE.STAGE_FK;
    }

    @Override
    public Long component1() {
        return getBuildStageId();
    }

    @Override
    public Long component2() {
        return getBuildFk();
    }

    @Override
    public Integer component3() {
        return getStageFk();
    }

    @Override
    public Long value1() {
        return getBuildStageId();
    }

    @Override
    public Long value2() {
        return getBuildFk();
    }

    @Override
    public Integer value3() {
        return getStageFk();
    }

    @Override
    public BuildStageRecord value1(Long value) {
        setBuildStageId(value);
        return this;
    }

    @Override
    public BuildStageRecord value2(Long value) {
        setBuildFk(value);
        return this;
    }

    @Override
    public BuildStageRecord value3(Integer value) {
        setStageFk(value);
        return this;
    }

    @Override
    public BuildStageRecord values(Long value1, Long value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BuildStageRecord
     */
    public BuildStageRecord() {
        super(BuildStage.BUILD_STAGE);
    }

    /**
     * Create a detached, initialised BuildStageRecord
     */
    public BuildStageRecord(Long buildStageId, Long buildFk, Integer stageFk) {
        super(BuildStage.BUILD_STAGE);

        set(0, buildStageId);
        set(1, buildFk);
        set(2, stageFk);
    }
}
