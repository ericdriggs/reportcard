/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.records;


import com.ericdriggs.reportcard.db.tables.App;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.13.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppRecord extends UpdatableRecordImpl<AppRecord> implements Record3<Integer, String, Integer> {

    private static final long serialVersionUID = -456779023;

    /**
     * Setter for <code>reportcard.app.app_id</code>.
     */
    public AppRecord setAppId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.app.app_id</code>.
     */
    public Integer getAppId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.app.app_name</code>.
     */
    public AppRecord setAppName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.app.app_name</code>.
     */
    public String getAppName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>reportcard.app.repo_fk</code>.
     */
    public AppRecord setRepoFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.app.repo_fk</code>.
     */
    public Integer getRepoFk() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, String, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return App.APP.APP_ID;
    }

    @Override
    public Field<String> field2() {
        return App.APP.APP_NAME;
    }

    @Override
    public Field<Integer> field3() {
        return App.APP.REPO_FK;
    }

    @Override
    public Integer component1() {
        return getAppId();
    }

    @Override
    public String component2() {
        return getAppName();
    }

    @Override
    public Integer component3() {
        return getRepoFk();
    }

    @Override
    public Integer value1() {
        return getAppId();
    }

    @Override
    public String value2() {
        return getAppName();
    }

    @Override
    public Integer value3() {
        return getRepoFk();
    }

    @Override
    public AppRecord value1(Integer value) {
        setAppId(value);
        return this;
    }

    @Override
    public AppRecord value2(String value) {
        setAppName(value);
        return this;
    }

    @Override
    public AppRecord value3(Integer value) {
        setRepoFk(value);
        return this;
    }

    @Override
    public AppRecord values(Integer value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppRecord
     */
    public AppRecord() {
        super(App.APP);
    }

    /**
     * Create a detached, initialised AppRecord
     */
    public AppRecord(Integer appId, String appName, Integer repoFk) {
        super(App.APP);

        set(0, appId);
        set(1, appName);
        set(2, repoFk);
    }
}
