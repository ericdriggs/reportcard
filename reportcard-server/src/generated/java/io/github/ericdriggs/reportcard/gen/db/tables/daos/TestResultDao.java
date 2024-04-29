/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.TestResultTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;

import java.math.BigDecimal;
import java.time.Instant;
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
public class TestResultDao extends DAOImpl<TestResultRecord, TestResultPojo, Long> {

    /**
     * Create a new TestResultDao without any configuration
     */
    public TestResultDao() {
        super(TestResultTable.TEST_RESULT, TestResultPojo.class);
    }

    /**
     * Create a new TestResultDao with an attached configuration
     */
    public TestResultDao(Configuration configuration) {
        super(TestResultTable.TEST_RESULT, TestResultPojo.class, configuration);
    }

    @Override
    public Long getId(TestResultPojo object) {
        return object.getTestResultId();
    }

    /**
     * Fetch records that have <code>test_result_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfTestResultIdTable(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.TEST_RESULT_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_id IN (values)</code>
     */
    public List<TestResultPojo> fetchByTestResultIdTable(Long... values) {
        return fetch(TestResultTable.TEST_RESULT.TEST_RESULT_ID, values);
    }

    /**
     * Fetch a unique record that has <code>test_result_id = value</code>
     */
    public TestResultPojo fetchOneByTestResultIdTable(Long value) {
        return fetchOne(TestResultTable.TEST_RESULT.TEST_RESULT_ID, value);
    }

    /**
     * Fetch a unique record that has <code>test_result_id = value</code>
     */
    public Optional<TestResultPojo> fetchOptionalByTestResultIdTable(Long value) {
        return fetchOptional(TestResultTable.TEST_RESULT.TEST_RESULT_ID, value);
    }

    /**
     * Fetch records that have <code>stage_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfStageFkTable(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.STAGE_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>stage_fk IN (values)</code>
     */
    public List<TestResultPojo> fetchByStageFkTable(Long... values) {
        return fetch(TestResultTable.TEST_RESULT.STAGE_FK, values);
    }

    /**
     * Fetch records that have <code>tests BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfTestsTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.TESTS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tests IN (values)</code>
     */
    public List<TestResultPojo> fetchByTestsTable(Integer... values) {
        return fetch(TestResultTable.TEST_RESULT.TESTS, values);
    }

    /**
     * Fetch records that have <code>skipped BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfSkippedTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.SKIPPED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>skipped IN (values)</code>
     */
    public List<TestResultPojo> fetchBySkippedTable(Integer... values) {
        return fetch(TestResultTable.TEST_RESULT.SKIPPED, values);
    }

    /**
     * Fetch records that have <code>error BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfErrorTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.ERROR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>error IN (values)</code>
     */
    public List<TestResultPojo> fetchByErrorTable(Integer... values) {
        return fetch(TestResultTable.TEST_RESULT.ERROR, values);
    }

    /**
     * Fetch records that have <code>failure BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfFailureTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.FAILURE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failure IN (values)</code>
     */
    public List<TestResultPojo> fetchByFailureTable(Integer... values) {
        return fetch(TestResultTable.TEST_RESULT.FAILURE, values);
    }

    /**
     * Fetch records that have <code>time BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfTimeTable(BigDecimal lowerInclusive, BigDecimal upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.TIME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>time IN (values)</code>
     */
    public List<TestResultPojo> fetchByTimeTable(BigDecimal... values) {
        return fetch(TestResultTable.TEST_RESULT.TIME, values);
    }

    /**
     * Fetch records that have <code>test_result_created BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfTestResultCreatedTable(Instant lowerInclusive, Instant upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.TEST_RESULT_CREATED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_created IN (values)</code>
     */
    public List<TestResultPojo> fetchByTestResultCreatedTable(Instant... values) {
        return fetch(TestResultTable.TEST_RESULT.TEST_RESULT_CREATED, values);
    }

    /**
     * Fetch records that have <code>external_links BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfExternalLinksTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.EXTERNAL_LINKS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>external_links IN (values)</code>
     */
    public List<TestResultPojo> fetchByExternalLinksTable(String... values) {
        return fetch(TestResultTable.TEST_RESULT.EXTERNAL_LINKS, values);
    }

    /**
     * Fetch records that have <code>is_success BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfIsSuccessTable(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.IS_SUCCESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>is_success IN (values)</code>
     */
    public List<TestResultPojo> fetchByIsSuccessTable(Boolean... values) {
        return fetch(TestResultTable.TEST_RESULT.IS_SUCCESS, values);
    }

    /**
     * Fetch records that have <code>has_skip BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestResultPojo> fetchRangeOfHasSkipTable(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(TestResultTable.TEST_RESULT.HAS_SKIP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>has_skip IN (values)</code>
     */
    public List<TestResultPojo> fetchByHasSkipTable(Boolean... values) {
        return fetch(TestResultTable.TEST_RESULT.HAS_SKIP, values);
    }
}
