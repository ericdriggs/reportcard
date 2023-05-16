/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Run implements Serializable {

    private static final long serialVersionUID = 102869492;

    private Long          runId;
    private String        runReference;
    private Long          jobFk;
    private Integer       jobRunCount;
    private String        sha;
    private LocalDateTime created;

    public Run() {}

    public Run(Run value) {
        this.runId = value.runId;
        this.runReference = value.runReference;
        this.jobFk = value.jobFk;
        this.jobRunCount = value.jobRunCount;
        this.sha = value.sha;
        this.created = value.created;
    }

    public Run(
        Long          runId,
        String        runReference,
        Long          jobFk,
        Integer       jobRunCount,
        String        sha,
        LocalDateTime created
    ) {
        this.runId = runId;
        this.runReference = runReference;
        this.jobFk = jobFk;
        this.jobRunCount = jobRunCount;
        this.sha = sha;
        this.created = created;
    }

    /**
     * Getter for <code>reportcard.run.run_id</code>.
     */
    public Long getRunId() {
        return this.runId;
    }

    /**
     * Setter for <code>reportcard.run.run_id</code>.
     */
    public Run setRunId(Long runId) {
        this.runId = runId;
        return this;
    }

    /**
     * Getter for <code>reportcard.run.run_reference</code>.
     */
    public String getRunReference() {
        return this.runReference;
    }

    /**
     * Setter for <code>reportcard.run.run_reference</code>.
     */
    public Run setRunReference(String runReference) {
        this.runReference = runReference;
        return this;
    }

    /**
     * Getter for <code>reportcard.run.job_fk</code>.
     */
    public Long getJobFk() {
        return this.jobFk;
    }

    /**
     * Setter for <code>reportcard.run.job_fk</code>.
     */
    public Run setJobFk(Long jobFk) {
        this.jobFk = jobFk;
        return this;
    }

    /**
     * Getter for <code>reportcard.run.job_run_count</code>.
     */
    public Integer getJobRunCount() {
        return this.jobRunCount;
    }

    /**
     * Setter for <code>reportcard.run.job_run_count</code>.
     */
    public Run setJobRunCount(Integer jobRunCount) {
        this.jobRunCount = jobRunCount;
        return this;
    }

    /**
     * Getter for <code>reportcard.run.sha</code>.
     */
    public String getSha() {
        return this.sha;
    }

    /**
     * Setter for <code>reportcard.run.sha</code>.
     */
    public Run setSha(String sha) {
        this.sha = sha;
        return this;
    }

    /**
     * Getter for <code>reportcard.run.created</code>.
     */
    public LocalDateTime getCreated() {
        return this.created;
    }

    /**
     * Setter for <code>reportcard.run.created</code>.
     */
    public Run setCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Run (");

        sb.append(runId);
        sb.append(", ").append(runReference);
        sb.append(", ").append(jobFk);
        sb.append(", ").append(jobRunCount);
        sb.append(", ").append(sha);
        sb.append(", ").append(created);

        sb.append(")");
        return sb.toString();
    }
}