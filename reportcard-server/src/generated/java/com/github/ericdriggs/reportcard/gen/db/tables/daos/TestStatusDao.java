/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.records.TestStatusRecord;

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
public class TestStatusDao extends DAOImpl<TestStatusRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus, Byte> {

    /**
     * Create a new TestStatusDao without any configuration
     */
    public TestStatusDao() {
        super(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus.class);
    }

    /**
     * Create a new TestStatusDao with an attached configuration
     */
    public TestStatusDao(Configuration configuration) {
        super(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus.class, configuration);
    }

    @Override
    public Byte getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus object) {
        return object.getTestStatusId();
    }

    /**
     * Fetch records that have <code>test_status_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus> fetchRangeOfTestStatusId(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS.TEST_STATUS_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_status_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus> fetchByTestStatusId(Byte... values) {
        return fetch(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS.TEST_STATUS_ID, values);
    }

    /**
     * Fetch a unique record that has <code>test_status_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus fetchOneByTestStatusId(Byte value) {
        return fetchOne(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS.TEST_STATUS_ID, value);
    }

    /**
     * Fetch records that have <code>test_status_name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus> fetchRangeOfTestStatusName(String lowerInclusive, String upperInclusive) {
        return fetchRange(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS.TEST_STATUS_NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_status_name IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus> fetchByTestStatusName(String... values) {
        return fetch(io.github.ericdriggs.reportcard.gen.db.tables.TestStatus.TEST_STATUS.TEST_STATUS_NAME, values);
    }
}
