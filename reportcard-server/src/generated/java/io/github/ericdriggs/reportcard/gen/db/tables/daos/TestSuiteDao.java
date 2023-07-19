/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.TestSuite;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestSuiteRecord;

import java.math.BigDecimal;
import java.util.List;

import lombok.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestSuiteDao extends DAOImpl<TestSuiteRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite, Long> {

    /**
     * Create a new TestSuiteDao without any configuration
     */
    public TestSuiteDao() {
        super(TestSuite.TEST_SUITE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite.class);
    }

    /**
     * Create a new TestSuiteDao with an attached configuration
     */
    public TestSuiteDao(Configuration configuration) {
        super(TestSuite.TEST_SUITE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite object) {
        return object.getTestSuiteId();
    }

    /**
     * Fetch records that have <code>test_suite_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfTestSuiteId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TEST_SUITE_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_suite_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByTestSuiteId(Long... values) {
        return fetch(TestSuite.TEST_SUITE.TEST_SUITE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>test_suite_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite fetchOneByTestSuiteId(Long value) {
        return fetchOne(TestSuite.TEST_SUITE.TEST_SUITE_ID, value);
    }

    /**
     * Fetch records that have <code>test_result_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfTestResultFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TEST_RESULT_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByTestResultFk(Long... values) {
        return fetch(TestSuite.TEST_SUITE.TEST_RESULT_FK, values);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByName(String... values) {
        return fetch(TestSuite.TEST_SUITE.NAME, values);
    }

    /**
     * Fetch records that have <code>tests BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfTests(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TESTS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tests IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByTests(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.TESTS, values);
    }

    /**
     * Fetch records that have <code>skipped BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfSkipped(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.SKIPPED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>skipped IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchBySkipped(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.SKIPPED, values);
    }

    /**
     * Fetch records that have <code>error BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfError(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.ERROR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>error IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByError(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.ERROR, values);
    }

    /**
     * Fetch records that have <code>failure BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfFailure(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.FAILURE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failure IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByFailure(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.FAILURE, values);
    }

    /**
     * Fetch records that have <code>time BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfTime(BigDecimal lowerInclusive, BigDecimal upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TIME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>time IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByTime(BigDecimal... values) {
        return fetch(TestSuite.TEST_SUITE.TIME, values);
    }

    /**
     * Fetch records that have <code>package BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfPackage(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.PACKAGE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>package IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByPackage(String... values) {
        return fetch(TestSuite.TEST_SUITE.PACKAGE, values);
    }

    /**
     * Fetch records that have <code>group BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfGroup(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.GROUP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>group IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByGroup(String... values) {
        return fetch(TestSuite.TEST_SUITE.GROUP, values);
    }

    /**
     * Fetch records that have <code>properties BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfProperties(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.PROPERTIES, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>properties IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByProperties(String... values) {
        return fetch(TestSuite.TEST_SUITE.PROPERTIES, values);
    }

    /**
     * Fetch records that have <code>is_success BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfIsSuccess(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.IS_SUCCESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>is_success IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByIsSuccess(Boolean... values) {
        return fetch(TestSuite.TEST_SUITE.IS_SUCCESS, values);
    }

    /**
     * Fetch records that have <code>has_skip BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchRangeOfHasSkip(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.HAS_SKIP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>has_skip IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> fetchByHasSkip(Boolean... values) {
        return fetch(TestSuite.TEST_SUITE.HAS_SKIP, values);
    }
}
