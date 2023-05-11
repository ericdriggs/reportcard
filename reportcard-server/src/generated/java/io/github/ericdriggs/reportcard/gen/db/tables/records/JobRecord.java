/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.Job;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
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
public class JobRecord extends UpdatableRecordImpl<JobRecord> implements Record4<Long, String, Integer, String> {

    private static final long serialVersionUID = 363237857;

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

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, Integer, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, String, Integer, String> valuesRow() {
        return (Row4) super.valuesRow();
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
    public JobRecord values(Long value1, String value2, Integer value3, String value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
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
    public JobRecord(Long jobId, String jobInfo, Integer branchFk, String jobInfoStr) {
        super(Job.JOB);

        setJobId(jobId);
        setJobInfo(jobInfo);
        setBranchFk(branchFk);
        setJobInfoStr(jobInfoStr);
    }
}
