/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.ragnarok.db.tables.daos;


import com.ericdriggs.ragnarok.db.tables.Repo;
import com.ericdriggs.ragnarok.db.tables.records.RepoRecord;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RepoDao extends DAOImpl<RepoRecord, com.ericdriggs.ragnarok.db.tables.pojos.Repo, Integer> {

    /**
     * Create a new RepoDao without any configuration
     */
    public RepoDao() {
        super(Repo.REPO, com.ericdriggs.ragnarok.db.tables.pojos.Repo.class);
    }

    /**
     * Create a new RepoDao with an attached configuration
     */
    public RepoDao(Configuration configuration) {
        super(Repo.REPO, com.ericdriggs.ragnarok.db.tables.pojos.Repo.class, configuration);
    }

    @Override
    public Integer getId(com.ericdriggs.ragnarok.db.tables.pojos.Repo object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.Repo> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Repo.REPO.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.Repo> fetchById(Integer... values) {
        return fetch(Repo.REPO.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.ericdriggs.ragnarok.db.tables.pojos.Repo fetchOneById(Integer value) {
        return fetchOne(Repo.REPO.ID, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.Repo> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(Repo.REPO.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.Repo> fetchByName(String... values) {
        return fetch(Repo.REPO.NAME, values);
    }

    /**
     * Fetch a unique record that has <code>name = value</code>
     */
    public com.ericdriggs.ragnarok.db.tables.pojos.Repo fetchOneByName(String value) {
        return fetchOne(Repo.REPO.NAME, value);
    }

    /**
     * Fetch records that have <code>org_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.Repo> fetchRangeOfOrgFk(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Repo.REPO.ORG_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>org_fk IN (values)</code>
     */
    public List<com.ericdriggs.ragnarok.db.tables.pojos.Repo> fetchByOrgFk(Integer... values) {
        return fetch(Repo.REPO.ORG_FK, values);
    }
}