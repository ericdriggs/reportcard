/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.BranchTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.records.BranchRecord;

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
public class BranchDao extends DAOImpl<BranchRecord, BranchPojo, Integer> {

    /**
     * Create a new BranchDao without any configuration
     */
    public BranchDao() {
        super(BranchTable.BRANCH, BranchPojo.class);
    }

    /**
     * Create a new BranchDao with an attached configuration
     */
    public BranchDao(Configuration configuration) {
        super(BranchTable.BRANCH, BranchPojo.class, configuration);
    }

    @Override
    public Integer getId(BranchPojo object) {
        return object.getBranchId();
    }

    /**
     * Fetch records that have <code>branch_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<BranchPojo> fetchRangeOfBranchIdTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(BranchTable.BRANCH.BRANCH_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>branch_id IN (values)</code>
     */
    public List<BranchPojo> fetchByBranchIdTable(Integer... values) {
        return fetch(BranchTable.BRANCH.BRANCH_ID, values);
    }

    /**
     * Fetch a unique record that has <code>branch_id = value</code>
     */
    public BranchPojo fetchOneByBranchIdTable(Integer value) {
        return fetchOne(BranchTable.BRANCH.BRANCH_ID, value);
    }

    /**
     * Fetch a unique record that has <code>branch_id = value</code>
     */
    public Optional<BranchPojo> fetchOptionalByBranchIdTable(Integer value) {
        return fetchOptional(BranchTable.BRANCH.BRANCH_ID, value);
    }

    /**
     * Fetch records that have <code>branch_name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<BranchPojo> fetchRangeOfBranchNameTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(BranchTable.BRANCH.BRANCH_NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>branch_name IN (values)</code>
     */
    public List<BranchPojo> fetchByBranchNameTable(String... values) {
        return fetch(BranchTable.BRANCH.BRANCH_NAME, values);
    }

    /**
     * Fetch records that have <code>repo_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<BranchPojo> fetchRangeOfRepoFkTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(BranchTable.BRANCH.REPO_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>repo_fk IN (values)</code>
     */
    public List<BranchPojo> fetchByRepoFkTable(Integer... values) {
        return fetch(BranchTable.BRANCH.REPO_FK, values);
    }

    /**
     * Fetch records that have <code>last_run BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<BranchPojo> fetchRangeOfLastRunTable(Instant lowerInclusive, Instant upperInclusive) {
        return fetchRange(BranchTable.BRANCH.LAST_RUN, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_run IN (values)</code>
     */
    public List<BranchPojo> fetchByLastRunTable(Instant... values) {
        return fetch(BranchTable.BRANCH.LAST_RUN, values);
    }
}
