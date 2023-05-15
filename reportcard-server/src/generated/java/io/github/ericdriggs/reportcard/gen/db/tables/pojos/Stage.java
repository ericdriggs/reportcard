/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;

import javax.annotation.processing.Generated;


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
public class Stage implements Serializable {

    private static final long serialVersionUID = 938175161;

    private Long   stageId;
    private String stageName;
    private Long   runFk;

    public Stage() {}

    public Stage(Stage value) {
        this.stageId = value.stageId;
        this.stageName = value.stageName;
        this.runFk = value.runFk;
    }

    public Stage(
        Long   stageId,
        String stageName,
        Long   runFk
    ) {
        this.stageId = stageId;
        this.stageName = stageName;
        this.runFk = runFk;
    }

    /**
     * Getter for <code>reportcard.stage.stage_id</code>.
     */
    public Long getStageId() {
        return this.stageId;
    }

    /**
     * Setter for <code>reportcard.stage.stage_id</code>.
     */
    public Stage setStageId(Long stageId) {
        this.stageId = stageId;
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.stage_name</code>.
     */
    public String getStageName() {
        return this.stageName;
    }

    /**
     * Setter for <code>reportcard.stage.stage_name</code>.
     */
    public Stage setStageName(String stageName) {
        this.stageName = stageName;
        return this;
    }

    /**
     * Getter for <code>reportcard.stage.run_fk</code>.
     */
    public Long getRunFk() {
        return this.runFk;
    }

    /**
     * Setter for <code>reportcard.stage.run_fk</code>.
     */
    public Stage setRunFk(Long runFk) {
        this.runFk = runFk;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Stage (");

        sb.append(stageId);
        sb.append(", ").append(stageName);
        sb.append(", ").append(runFk);

        sb.append(")");
        return sb.toString();
    }
}
