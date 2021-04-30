/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.daos;


import com.ericdriggs.reportcard.gen.db.tables.Execution;
import com.ericdriggs.reportcard.gen.db.tables.records.ExecutionRecord;

import java.util.List;

import javax.annotation.Generated;

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
public class ExecutionDao extends DAOImpl<ExecutionRecord, com.ericdriggs.reportcard.gen.db.tables.pojos.Execution, Long> {

    /**
     * Create a new ExecutionDao without any configuration
     */
    public ExecutionDao() {
        super(Execution.EXECUTION, com.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class);
    }

    /**
     * Create a new ExecutionDao with an attached configuration
     */
    public ExecutionDao(Configuration configuration) {
        super(Execution.EXECUTION, com.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class, configuration);
    }

    @Override
    public Long getId(com.ericdriggs.reportcard.gen.db.tables.pojos.Execution object) {
        return object.getExecutionId();
    }

    /**
     * Fetch records that have <code>execution_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.gen.db.tables.pojos.Execution> fetchRangeOfExecutionId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Execution.EXECUTION.EXECUTION_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>execution_id IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.gen.db.tables.pojos.Execution> fetchByExecutionId(Long... values) {
        return fetch(Execution.EXECUTION.EXECUTION_ID, values);
    }

    /**
     * Fetch a unique record that has <code>execution_id = value</code>
     */
    public com.ericdriggs.reportcard.gen.db.tables.pojos.Execution fetchOneByExecutionId(Long value) {
        return fetchOne(Execution.EXECUTION.EXECUTION_ID, value);
    }

    /**
     * Fetch records that have <code>execution_external_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.gen.db.tables.pojos.Execution> fetchRangeOfExecutionExternalId(String lowerInclusive, String upperInclusive) {
        return fetchRange(Execution.EXECUTION.EXECUTION_EXTERNAL_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>execution_external_id IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.gen.db.tables.pojos.Execution> fetchByExecutionExternalId(String... values) {
        return fetch(Execution.EXECUTION.EXECUTION_EXTERNAL_ID, values);
    }

    /**
     * Fetch records that have <code>context_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.gen.db.tables.pojos.Execution> fetchRangeOfContextFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Execution.EXECUTION.CONTEXT_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>context_fk IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.gen.db.tables.pojos.Execution> fetchByContextFk(Long... values) {
        return fetch(Execution.EXECUTION.CONTEXT_FK, values);
    }
}
