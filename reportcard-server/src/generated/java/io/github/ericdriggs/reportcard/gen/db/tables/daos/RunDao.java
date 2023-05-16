/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.records.RunRecord;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class RunDao extends DAOImpl<RunRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run, Long> {

    /**
     * Create a new RunDao without any configuration
     */
    public RunDao() {
        super(Run.RUN, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run.class);
    }

    /**
     * Create a new RunDao with an attached configuration
     */
    public RunDao(Configuration configuration) {
        super(Run.RUN, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run object) {
        return object.getRunId();
    }

    /**
     * Fetch records that have <code>run_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchRangeOfRunId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Run.RUN.RUN_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>run_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchByRunId(Long... values) {
        return fetch(Run.RUN.RUN_ID, values);
    }

    /**
     * Fetch a unique record that has <code>run_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run fetchOneByRunId(Long value) {
        return fetchOne(Run.RUN.RUN_ID, value);
    }

    /**
     * Fetch records that have <code>run_reference BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchRangeOfRunReference(String lowerInclusive, String upperInclusive) {
        return fetchRange(Run.RUN.RUN_REFERENCE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>run_reference IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchByRunReference(String... values) {
        return fetch(Run.RUN.RUN_REFERENCE, values);
    }

    /**
     * Fetch records that have <code>job_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchRangeOfJobFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Run.RUN.JOB_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>job_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchByJobFk(Long... values) {
        return fetch(Run.RUN.JOB_FK, values);
    }

    /**
     * Fetch records that have <code>job_run_count BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchRangeOfJobRunCount(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Run.RUN.JOB_RUN_COUNT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>job_run_count IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchByJobRunCount(Integer... values) {
        return fetch(Run.RUN.JOB_RUN_COUNT, values);
    }

    /**
     * Fetch records that have <code>sha BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchRangeOfSha(String lowerInclusive, String upperInclusive) {
        return fetchRange(Run.RUN.SHA, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sha IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchBySha(String... values) {
        return fetch(Run.RUN.SHA, values);
    }

    /**
     * Fetch records that have <code>created BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchRangeOfCreated(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(Run.RUN.CREATED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>created IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run> fetchByCreated(LocalDateTime... values) {
        return fetch(Run.RUN.CREATED, values);
    }
}
