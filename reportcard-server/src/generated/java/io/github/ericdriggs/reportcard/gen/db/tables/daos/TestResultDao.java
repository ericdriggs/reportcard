/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.TestResult;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;

import java.math.BigDecimal;
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
public class TestResultDao extends DAOImpl<TestResultRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Long> {

    /**
     * Create a new TestResultDao without any configuration
     */
    public TestResultDao() {
        super(TestResult.TEST_RESULT, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult.class);
    }

    /**
     * Create a new TestResultDao with an attached configuration
     */
    public TestResultDao(Configuration configuration) {
        super(TestResult.TEST_RESULT, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult object) {
        return object.getTestResultId();
    }

    /**
     * Fetch records that have <code>test_result_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfTestResultId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TEST_RESULT_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByTestResultId(Long... values) {
        return fetch(TestResult.TEST_RESULT.TEST_RESULT_ID, values);
    }

    /**
     * Fetch a unique record that has <code>test_result_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult fetchOneByTestResultId(Long value) {
        return fetchOne(TestResult.TEST_RESULT.TEST_RESULT_ID, value);
    }

    /**
     * Fetch a unique record that has <code>test_result_id = value</code>
     */
    public Optional<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchOptionalByTestResultId(Long value) {
        return fetchOptional(TestResult.TEST_RESULT.TEST_RESULT_ID, value);
    }

    /**
     * Fetch records that have <code>stage_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfStageFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.STAGE_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>stage_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByStageFk(Long... values) {
        return fetch(TestResult.TEST_RESULT.STAGE_FK, values);
    }

    /**
     * Fetch records that have <code>tests BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfTests(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TESTS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tests IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByTests(Integer... values) {
        return fetch(TestResult.TEST_RESULT.TESTS, values);
    }

    /**
     * Fetch records that have <code>skipped BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfSkipped(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.SKIPPED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>skipped IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchBySkipped(Integer... values) {
        return fetch(TestResult.TEST_RESULT.SKIPPED, values);
    }

    /**
     * Fetch records that have <code>error BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfError(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.ERROR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>error IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByError(Integer... values) {
        return fetch(TestResult.TEST_RESULT.ERROR, values);
    }

    /**
     * Fetch records that have <code>failure BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfFailure(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.FAILURE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failure IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByFailure(Integer... values) {
        return fetch(TestResult.TEST_RESULT.FAILURE, values);
    }

    /**
     * Fetch records that have <code>time BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfTime(BigDecimal lowerInclusive, BigDecimal upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TIME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>time IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByTime(BigDecimal... values) {
        return fetch(TestResult.TEST_RESULT.TIME, values);
    }

    /**
     * Fetch records that have <code>test_result_created BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfTestResultCreated(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.TEST_RESULT_CREATED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_created IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByTestResultCreated(LocalDateTime... values) {
        return fetch(TestResult.TEST_RESULT.TEST_RESULT_CREATED, values);
    }

    /**
     * Fetch records that have <code>external_links BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfExternalLinks(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.EXTERNAL_LINKS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>external_links IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByExternalLinks(String... values) {
        return fetch(TestResult.TEST_RESULT.EXTERNAL_LINKS, values);
    }

    /**
     * Fetch records that have <code>is_success BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfIsSuccess(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.IS_SUCCESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>is_success IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByIsSuccess(Boolean... values) {
        return fetch(TestResult.TEST_RESULT.IS_SUCCESS, values);
    }

    /**
     * Fetch records that have <code>has_skip BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchRangeOfHasSkip(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(TestResult.TEST_RESULT.HAS_SKIP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>has_skip IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> fetchByHasSkip(Boolean... values) {
        return fetch(TestResult.TEST_RESULT.HAS_SKIP, values);
    }
}
