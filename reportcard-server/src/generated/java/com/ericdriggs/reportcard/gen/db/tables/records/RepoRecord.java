/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.records;


import com.ericdriggs.reportcard.gen.db.tables.Repo;

import javax.annotation.processing.Generated;

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
        "jOOQ version:3.14.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RepoRecord extends UpdatableRecordImpl<RepoRecord> implements Record3<Integer, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>reportcard.repo.repo_id</code>.
     */
    public RepoRecord setRepoId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.repo.repo_id</code>.
     */
    public Integer getRepoId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.repo.repo_name</code>.
     */
    public RepoRecord setRepoName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.repo.repo_name</code>.
     */
    public String getRepoName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>reportcard.repo.org_fk</code>.
     */
    public RepoRecord setOrgFk(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.repo.org_fk</code>.
     */
    public Integer getOrgFk() {
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
        return Repo.REPO.REPO_ID;
    }

    @Override
    public Field<String> field2() {
        return Repo.REPO.REPO_NAME;
    }

    @Override
    public Field<Integer> field3() {
        return Repo.REPO.ORG_FK;
    }

    @Override
    public Integer component1() {
        return getRepoId();
    }

    @Override
    public String component2() {
        return getRepoName();
    }

    @Override
    public Integer component3() {
        return getOrgFk();
    }

    @Override
    public Integer value1() {
        return getRepoId();
    }

    @Override
    public String value2() {
        return getRepoName();
    }

    @Override
    public Integer value3() {
        return getOrgFk();
    }

    @Override
    public RepoRecord value1(Integer value) {
        setRepoId(value);
        return this;
    }

    @Override
    public RepoRecord value2(String value) {
        setRepoName(value);
        return this;
    }

    @Override
    public RepoRecord value3(Integer value) {
        setOrgFk(value);
        return this;
    }

    @Override
    public RepoRecord values(Integer value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RepoRecord
     */
    public RepoRecord() {
        super(Repo.REPO);
    }

    /**
     * Create a detached, initialised RepoRecord
     */
    public RepoRecord(Integer repoId, String repoName, Integer orgFk) {
        super(Repo.REPO);

        setRepoId(repoId);
        setRepoName(repoName);
        setOrgFk(orgFk);
    }
}
