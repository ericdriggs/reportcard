/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.Storage;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StorageRecord;

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
public class StorageDao extends DAOImpl<StorageRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage, Long> {

    /**
     * Create a new StorageDao without any configuration
     */
    public StorageDao() {
        super(Storage.STORAGE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage.class);
    }

    /**
     * Create a new StorageDao with an attached configuration
     */
    public StorageDao(Configuration configuration) {
        super(Storage.STORAGE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage object) {
        return object.getStorageId();
    }

    /**
     * Fetch records that have <code>storage_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchRangeOfStorageId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Storage.STORAGE.STORAGE_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>storage_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchByStorageId(Long... values) {
        return fetch(Storage.STORAGE.STORAGE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>storage_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage fetchOneByStorageId(Long value) {
        return fetchOne(Storage.STORAGE.STORAGE_ID, value);
    }

    /**
     * Fetch a unique record that has <code>storage_id = value</code>
     */
    public Optional<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchOptionalByStorageId(Long value) {
        return fetchOptional(Storage.STORAGE.STORAGE_ID, value);
    }

    /**
     * Fetch records that have <code>stage_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchRangeOfStageFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Storage.STORAGE.STAGE_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>stage_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchByStageFk(Long... values) {
        return fetch(Storage.STORAGE.STAGE_FK, values);
    }

    /**
     * Fetch records that have <code>label BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchRangeOfLabel(String lowerInclusive, String upperInclusive) {
        return fetchRange(Storage.STORAGE.LABEL, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>label IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchByLabel(String... values) {
        return fetch(Storage.STORAGE.LABEL, values);
    }

    /**
     * Fetch records that have <code>prefix BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchRangeOfPrefix(String lowerInclusive, String upperInclusive) {
        return fetchRange(Storage.STORAGE.PREFIX, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>prefix IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchByPrefix(String... values) {
        return fetch(Storage.STORAGE.PREFIX, values);
    }

    /**
     * Fetch records that have <code>index_file BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchRangeOfIndexFile(String lowerInclusive, String upperInclusive) {
        return fetchRange(Storage.STORAGE.INDEX_FILE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>index_file IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchByIndexFile(String... values) {
        return fetch(Storage.STORAGE.INDEX_FILE, values);
    }

    /**
     * Fetch records that have <code>storage_type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchRangeOfStorageType(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Storage.STORAGE.STORAGE_TYPE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>storage_type IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage> fetchByStorageType(Integer... values) {
        return fetch(Storage.STORAGE.STORAGE_TYPE, values);
    }
}
