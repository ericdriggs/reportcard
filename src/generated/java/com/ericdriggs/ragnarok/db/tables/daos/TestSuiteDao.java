/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.ragnarok.db.tables.daos;


import com.ericdriggs.ragnarok.db.tables.TestSuite;
import com.ericdriggs.ragnarok.db.tables.records.TestSuiteRecord;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.jooq.types.ULong;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestSuiteDao extends DAOImpl<TestSuiteRecord, com.ericdriggs.ragnarok.db.tables.pojos.TestSuite, ULong> {

    /**
     * Create a new TestSuiteDao without any configuration
     */
    public TestSuiteDao() {
        super(TestSuite.TEST_SUITE, com.ericdriggs.ragnarok.db.tables.pojos.TestSuite.class);
    }

    /**
     * Create a new TestSuiteDao with an attached configuration
     */
    public TestSuiteDao(Configuration configuration) {
        super(TestSuite.TEST_SUITE, com.ericdriggs.ragnarok.db.tables.pojos.TestSuite.class, configuration);
    }

    @Override
    public ULong getId(com.ericdriggs.ragnarok.db.tables.pojos.TestSuite object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfId(ULong lowerInclusive, ULong upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchById(ULong... values) {
        return fetch(TestSuite.TEST_SUITE.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.ericdriggs.ragnarok.db.tables.pojos.TestSuite fetchOneById(ULong value) {
        return fetchOne(TestSuite.TEST_SUITE.ID, value);
    }

    /**
     * Fetch records that have <code>test_result_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfTestResultFk(ULong lowerInclusive, ULong upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TEST_RESULT_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_result_fk IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByTestResultFk(ULong... values) {
        return fetch(TestSuite.TEST_SUITE.TEST_RESULT_FK, values);
    }

    /**
     * Fetch records that have <code>package BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfPackage(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.PACKAGE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>package IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByPackage(String... values) {
        return fetch(TestSuite.TEST_SUITE.PACKAGE, values);
    }

    /**
     * Fetch records that have <code>tests BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfTests(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TESTS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tests IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByTests(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.TESTS, values);
    }

    /**
     * Fetch records that have <code>skipped BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfSkipped(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.SKIPPED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>skipped IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchBySkipped(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.SKIPPED, values);
    }

    /**
     * Fetch records that have <code>error BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfError(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.ERROR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>error IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByError(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.ERROR, values);
    }

    /**
     * Fetch records that have <code>failure BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfFailure(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.FAILURE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failure IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByFailure(Integer... values) {
        return fetch(TestSuite.TEST_SUITE.FAILURE, values);
    }

    /**
     * Fetch records that have <code>time BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfTime(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.TIME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>time IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByTime(Long... values) {
        return fetch(TestSuite.TEST_SUITE.TIME, values);
    }

    /**
     * Fetch records that have <code>is_success BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfIsSuccess(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.IS_SUCCESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>is_success IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByIsSuccess(Byte... values) {
        return fetch(TestSuite.TEST_SUITE.IS_SUCCESS, values);
    }

    /**
     * Fetch records that have <code>has_skip BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchRangeOfHasSkip(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(TestSuite.TEST_SUITE.HAS_SKIP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>has_skip IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.TestSuite> fetchByHasSkip(Byte... values) {
        return fetch(TestSuite.TEST_SUITE.HAS_SKIP, values);
    }
}
