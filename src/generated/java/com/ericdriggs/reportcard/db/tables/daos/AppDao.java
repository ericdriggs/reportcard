/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.daos;


import com.ericdriggs.reportcard.db.tables.App;
import com.ericdriggs.reportcard.db.tables.records.AppRecord;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppDao extends DAOImpl<AppRecord, com.ericdriggs.reportcard.db.tables.pojos.App, Integer> {

    /**
     * Create a new AppDao without any configuration
     */
    public AppDao() {
        super(App.APP, com.ericdriggs.reportcard.db.tables.pojos.App.class);
    }

    /**
     * Create a new AppDao with an attached configuration
     */
    public AppDao(Configuration configuration) {
        super(App.APP, com.ericdriggs.reportcard.db.tables.pojos.App.class, configuration);
    }

    @Override
    public Integer getId(com.ericdriggs.reportcard.db.tables.pojos.App object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(App.APP.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchById(Integer... values) {
        return fetch(App.APP.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.ericdriggs.reportcard.db.tables.pojos.App fetchOneById(Integer value) {
        return fetchOne(App.APP.ID, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(App.APP.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchByName(String... values) {
        return fetch(App.APP.NAME, values);
    }

    /**
     * Fetch a unique record that has <code>name = value</code>
     */
    public com.ericdriggs.reportcard.db.tables.pojos.App fetchOneByName(String value) {
        return fetchOne(App.APP.NAME, value);
    }

    /**
     * Fetch records that have <code>branch_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchRangeOfBranchFk(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(App.APP.BRANCH_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>branch_fk IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchByBranchFk(Integer... values) {
        return fetch(App.APP.BRANCH_FK, values);
    }
}
