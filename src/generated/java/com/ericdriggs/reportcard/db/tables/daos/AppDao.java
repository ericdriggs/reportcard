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
        return object.getAppId();
    }

    /**
     * Fetch records that have <code>app_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchRangeOfAppId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(App.APP.APP_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>app_id IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchByAppId(Integer... values) {
        return fetch(App.APP.APP_ID, values);
    }

    /**
     * Fetch a unique record that has <code>app_id = value</code>
     */
    public com.ericdriggs.reportcard.db.tables.pojos.App fetchOneByAppId(Integer value) {
        return fetchOne(App.APP.APP_ID, value);
    }

    /**
     * Fetch records that have <code>app_name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchRangeOfAppName(String lowerInclusive, String upperInclusive) {
        return fetchRange(App.APP.APP_NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>app_name IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchByAppName(String... values) {
        return fetch(App.APP.APP_NAME, values);
    }

    /**
     * Fetch a unique record that has <code>app_name = value</code>
     */
    public com.ericdriggs.reportcard.db.tables.pojos.App fetchOneByAppName(String value) {
        return fetchOne(App.APP.APP_NAME, value);
    }

    /**
     * Fetch records that have <code>repo_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchRangeOfRepoFk(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(App.APP.REPO_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>repo_fk IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.App> fetchByRepoFk(Integer... values) {
        return fetch(App.APP.REPO_FK, values);
    }
}
