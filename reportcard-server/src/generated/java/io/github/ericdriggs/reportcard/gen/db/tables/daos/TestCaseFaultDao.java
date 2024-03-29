/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.TestCaseFaultTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestCaseFaultRecord;

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
public class TestCaseFaultDao extends DAOImpl<TestCaseFaultRecord, TestCaseFaultPojo, Long> {

    /**
     * Create a new TestCaseFaultDao without any configuration
     */
    public TestCaseFaultDao() {
        super(TestCaseFaultTable.TEST_CASE_FAULT, TestCaseFaultPojo.class);
    }

    /**
     * Create a new TestCaseFaultDao with an attached configuration
     */
    public TestCaseFaultDao(Configuration configuration) {
        super(TestCaseFaultTable.TEST_CASE_FAULT, TestCaseFaultPojo.class, configuration);
    }

    @Override
    public Long getId(TestCaseFaultPojo object) {
        return object.getTestCaseFaultId();
    }

    /**
     * Fetch records that have <code>test_case_fault_id BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<TestCaseFaultPojo> fetchRangeOfTestCaseFaultIdTable(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FAULT_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_case_fault_id IN (values)</code>
     */
    public List<TestCaseFaultPojo> fetchByTestCaseFaultIdTable(Long... values) {
        return fetch(TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FAULT_ID, values);
    }

    /**
     * Fetch a unique record that has <code>test_case_fault_id = value</code>
     */
    public TestCaseFaultPojo fetchOneByTestCaseFaultIdTable(Long value) {
        return fetchOne(TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FAULT_ID, value);
    }

    /**
     * Fetch a unique record that has <code>test_case_fault_id = value</code>
     */
    public Optional<TestCaseFaultPojo> fetchOptionalByTestCaseFaultIdTable(Long value) {
        return fetchOptional(TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FAULT_ID, value);
    }

    /**
     * Fetch records that have <code>test_case_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestCaseFaultPojo> fetchRangeOfTestCaseFkTable(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>test_case_fk IN (values)</code>
     */
    public List<TestCaseFaultPojo> fetchByTestCaseFkTable(Long... values) {
        return fetch(TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FK, values);
    }

    /**
     * Fetch records that have <code>fault_context_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestCaseFaultPojo> fetchRangeOfFaultContextFkTable(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(TestCaseFaultTable.TEST_CASE_FAULT.FAULT_CONTEXT_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>fault_context_fk IN (values)</code>
     */
    public List<TestCaseFaultPojo> fetchByFaultContextFkTable(Byte... values) {
        return fetch(TestCaseFaultTable.TEST_CASE_FAULT.FAULT_CONTEXT_FK, values);
    }

    /**
     * Fetch records that have <code>type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestCaseFaultPojo> fetchRangeOfTypeTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestCaseFaultTable.TEST_CASE_FAULT.TYPE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>type IN (values)</code>
     */
    public List<TestCaseFaultPojo> fetchByTypeTable(String... values) {
        return fetch(TestCaseFaultTable.TEST_CASE_FAULT.TYPE, values);
    }

    /**
     * Fetch records that have <code>message BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestCaseFaultPojo> fetchRangeOfMessageTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestCaseFaultTable.TEST_CASE_FAULT.MESSAGE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>message IN (values)</code>
     */
    public List<TestCaseFaultPojo> fetchByMessageTable(String... values) {
        return fetch(TestCaseFaultTable.TEST_CASE_FAULT.MESSAGE, values);
    }

    /**
     * Fetch records that have <code>value BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<TestCaseFaultPojo> fetchRangeOfValueTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(TestCaseFaultTable.TEST_CASE_FAULT.VALUE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>value IN (values)</code>
     */
    public List<TestCaseFaultPojo> fetchByValueTable(String... values) {
        return fetch(TestCaseFaultTable.TEST_CASE_FAULT.VALUE, values);
    }
}
