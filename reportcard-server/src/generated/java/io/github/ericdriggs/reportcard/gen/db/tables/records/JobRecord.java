/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.Job;

import java.time.LocalDateTime;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JobRecord extends UpdatableRecordImpl<JobRecord> implements Record5<Long, String, Integer, String, LocalDateTime> {

    private static final long serialVersionUID = 1387697110;

    /**
     * Setter for <code>reportcard.job.job_id</code>.
     */
    public JobRecord setJobId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.job.job_id</code>.
     */
    public Long getJobId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.job.job_info</code>.
     */
    public JobRecord setJobInfo(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.job.job_info</code>.
     */
    public String getJobInfo() {
        return (String) get(1);
    }

    /**
     * Setter for <code>reportcard.job.branch_fk</code>.
     */
    public JobRecord setBranchFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.job.branch_fk</code>.
     */
    public Integer getBranchFk() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>reportcard.job.job_info_str</code>.
     */
    public JobRecord setJobInfoStr(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.job.job_info_str</code>.
     */
    public String getJobInfoStr() {
        return (String) get(3);
    }

    /**
     * Setter for <code>reportcard.job.last_run</code>.
     */
    public JobRecord setLastRun(LocalDateTime value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.job.last_run</code>.
     */
    public LocalDateTime getLastRun() {
        return (LocalDateTime) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, Integer, String, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, String, Integer, String, LocalDateTime> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Job.JOB.JOB_ID;
    }

    @Override
    public Field<String> field2() {
        return Job.JOB.JOB_INFO;
    }

    @Override
    public Field<Integer> field3() {
        return Job.JOB.BRANCH_FK;
    }

    @Override
    public Field<String> field4() {
        return Job.JOB.JOB_INFO_STR;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Job.JOB.LAST_RUN;
    }

    @Override
    public Long component1() {
        return getJobId();
    }

    @Override
    public String component2() {
        return getJobInfo();
    }

    @Override
    public Integer component3() {
        return getBranchFk();
    }

    @Override
    public String component4() {
        return getJobInfoStr();
    }

    @Override
    public LocalDateTime component5() {
        return getLastRun();
    }

    @Override
    public Long value1() {
        return getJobId();
    }

    @Override
    public String value2() {
        return getJobInfo();
    }

    @Override
    public Integer value3() {
        return getBranchFk();
    }

    @Override
    public String value4() {
        return getJobInfoStr();
    }

    @Override
    public LocalDateTime value5() {
        return getLastRun();
    }

    @Override
    public JobRecord value1(Long value) {
        setJobId(value);
        return this;
    }

    @Override
    public JobRecord value2(String value) {
        setJobInfo(value);
        return this;
    }

    @Override
    public JobRecord value3(Integer value) {
        setBranchFk(value);
        return this;
    }

    @Override
    public JobRecord value4(String value) {
        setJobInfoStr(value);
        return this;
    }

    @Override
    public JobRecord value5(LocalDateTime value) {
        setLastRun(value);
        return this;
    }

    @Override
    public JobRecord values(Long value1, String value2, Integer value3, String value4, LocalDateTime value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached JobRecord
     */
    public JobRecord() {
        super(Job.JOB);
    }

    /**
     * Create a detached, initialised JobRecord
     */
    public JobRecord(Long jobId, String jobInfo, Integer branchFk, String jobInfoStr, LocalDateTime lastRun) {
        super(Job.JOB);

        setJobId(jobId);
        setJobInfo(jobInfo);
        setBranchFk(branchFk);
        setJobInfoStr(jobInfoStr);
        setLastRun(lastRun);
    }

    /**
     * Create a detached, initialised JobRecord
     */
    public JobRecord(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job value) {
        super(Job.JOB);

        if (value != null) {
            setJobId(value.getJobId());
            setJobInfo(value.getJobInfo());
            setBranchFk(value.getBranchFk());
            setJobInfoStr(value.getJobInfoStr());
            setLastRun(value.getLastRun());
        }
    }
}
