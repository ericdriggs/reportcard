/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.Stage;

import javax.annotation.processing.Generated;

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
        "jOOQ version:3.14.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StageRecord extends UpdatableRecordImpl<StageRecord> implements Record3<Long, String, Long> {

    private static final long serialVersionUID = -362926469;

    /**
     * Setter for <code>reportcard.stage.stage_id</code>.
     */
    public StageRecord setStageId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.stage_id</code>.
     */
    public Long getStageId() {
        return (Long) get(0);
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
     * Setter for <code>reportcard.stage.run_fk</code>.
     */
    public StageRecord setRunFk(Long value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.run_fk</code>.
     */
    public Long getRunFk() {
        return (Long) get(2);
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
    public Row3<Long, String, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, String, Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Stage.STAGE.STAGE_ID;
    }

    @Override
    public Field<String> field2() {
        return Stage.STAGE.STAGE_NAME;
    }

    @Override
    public Field<Long> field3() {
        return Stage.STAGE.RUN_FK;
    }

    @Override
    public Long component1() {
        return getStageId();
    }

    @Override
    public String component2() {
        return getStageName();
    }

    @Override
    public Long component3() {
        return getRunFk();
    }

    @Override
    public Long value1() {
        return getStageId();
    }

    @Override
    public String value2() {
        return getStageName();
    }

    @Override
    public Long value3() {
        return getRunFk();
    }

    @Override
    public StageRecord value1(Long value) {
        setStageId(value);
        return this;
    }

    @Override
    public StageRecord value2(String value) {
        setStageName(value);
        return this;
    }

    @Override
    public StageRecord value3(Long value) {
        setRunFk(value);
        return this;
    }

    @Override
    public StageRecord values(Long value1, String value2, Long value3) {
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
    public StageRecord(Long stageId, String stageName, Long runFk) {
        super(Stage.STAGE);

        setStageId(stageId);
        setStageName(stageName);
        setRunFk(runFk);
    }
}
