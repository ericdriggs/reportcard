/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.records.JobRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JobDao extends DAOImpl<JobRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job, Long> {

    /**
     * Create a new JobDao without any configuration
     */
    public JobDao() {
        super(Job.JOB, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job.class);
    }

    /**
     * Create a new JobDao with an attached configuration
     */
    public JobDao(Configuration configuration) {
        super(Job.JOB, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job object) {
        return object.getJobId();
    }

    /**
     * Fetch records that have <code>job_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchRangeOfJobId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Job.JOB.JOB_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>job_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchByJobId(Long... values) {
        return fetch(Job.JOB.JOB_ID, values);
    }

    /**
     * Fetch a unique record that has <code>job_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job fetchOneByJobId(Long value) {
        return fetchOne(Job.JOB.JOB_ID, value);
    }

    /**
     * Fetch a unique record that has <code>job_id = value</code>
     */
    public Optional<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchOptionalByJobId(Long value) {
        return fetchOptional(Job.JOB.JOB_ID, value);
    }

    /**
     * Fetch records that have <code>job_info BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchRangeOfJobInfo(String lowerInclusive, String upperInclusive) {
        return fetchRange(Job.JOB.JOB_INFO, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>job_info IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchByJobInfo(String... values) {
        return fetch(Job.JOB.JOB_INFO, values);
    }

    /**
     * Fetch records that have <code>branch_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchRangeOfBranchFk(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Job.JOB.BRANCH_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>branch_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchByBranchFk(Integer... values) {
        return fetch(Job.JOB.BRANCH_FK, values);
    }

    /**
     * Fetch records that have <code>job_info_str BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchRangeOfJobInfoStr(String lowerInclusive, String upperInclusive) {
        return fetchRange(Job.JOB.JOB_INFO_STR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>job_info_str IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchByJobInfoStr(String... values) {
        return fetch(Job.JOB.JOB_INFO_STR, values);
    }

    /**
     * Fetch records that have <code>last_run BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchRangeOfLastRun(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(Job.JOB.LAST_RUN, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_run IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> fetchByLastRun(LocalDateTime... values) {
        return fetch(Job.JOB.LAST_RUN, values);
    }
}
