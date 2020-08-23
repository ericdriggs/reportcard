/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.daos;


import com.ericdriggs.reportcard.db.tables.TestResult;
import com.ericdriggs.reportcard.db.tables.records.TestResultRecord;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestResultDao extends DAOImpl<TestResultRecord, com.ericdriggs.reportcard.db.tables.pojos.TestResult, Long> {

    /**
     * Create a new TestResultDao without any configuration
     */
    public TestResultDao() {
        super(TestResult.TEST_RESULT, com.ericdriggs.reportcard.db.tables.pojos.TestResult.class);
    }

    /**
     * Create a new TestResultDao with an attached configuration
     */
    public TestResultDao(Configuration configuration) {
        super(TestResult.TEST_RESULT, com.ericdriggs.reportcard.db.tables.pojos.TestResult.class, configuration);
    }

    @Override
    public Long getId(com.ericdriggs.reportcard.db.tables.pojos.TestResult object) {
        return object.getTestResultId();
    }

    /**
     * Fetch records that have <code>test_result_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfTestResultId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TEST_RESULT_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_id IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByTestResultId(Long... values) {
        return fetch(TestResult.TEST_RESULT.TEST_RESULT_ID, values);
    }

    /**
     * Fetch a unique record that has <code>test_result_id = value</code>
     */
    public com.ericdriggs.reportcard.db.tables.pojos.TestResult fetchOneByTestResultId(Long value) {
        return fetchOne(TestResult.TEST_RESULT.TEST_RESULT_ID, value);
    }

    /**
     * Fetch records that have <code>build_stage_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfBuildStageFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.BUILD_STAGE_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>build_stage_fk IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByBuildStageFk(Long... values) {
        return fetch(TestResult.TEST_RESULT.BUILD_STAGE_FK, values);
    }

    /**
     * Fetch records that have <code>tests BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfTests(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TESTS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tests IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByTests(Integer... values) {
        return fetch(TestResult.TEST_RESULT.TESTS, values);
    }

    /**
     * Fetch records that have <code>skipped BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfSkipped(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.SKIPPED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>skipped IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchBySkipped(Integer... values) {
        return fetch(TestResult.TEST_RESULT.SKIPPED, values);
    }

    /**
     * Fetch records that have <code>error BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfError(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.ERROR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>error IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByError(Integer... values) {
        return fetch(TestResult.TEST_RESULT.ERROR, values);
    }

    /**
     * Fetch records that have <code>failure BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfFailure(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.FAILURE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failure IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByFailure(Integer... values) {
        return fetch(TestResult.TEST_RESULT.FAILURE, values);
    }

    /**
     * Fetch records that have <code>time BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfTime(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TIME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>time IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByTime(Long... values) {
        return fetch(TestResult.TEST_RESULT.TIME, values);
    }

    /**
     * Fetch records that have <code>is_success BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfIsSuccess(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.IS_SUCCESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>is_success IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByIsSuccess(Byte... values) {
        return fetch(TestResult.TEST_RESULT.IS_SUCCESS, values);
    }

    /**
     * Fetch records that have <code>has_skip BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchRangeOfHasSkip(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.HAS_SKIP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>has_skip IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.TestResult> fetchByHasSkip(Byte... values) {
        return fetch(TestResult.TEST_RESULT.HAS_SKIP, values);
    }
}
