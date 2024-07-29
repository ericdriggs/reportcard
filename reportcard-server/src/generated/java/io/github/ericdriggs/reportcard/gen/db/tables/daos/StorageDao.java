/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.StorageTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
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
public class StorageDao extends DAOImpl<StorageRecord, StoragePojo, Long> {

    /**
     * Create a new StorageDao without any configuration
     */
    public StorageDao() {
        super(StorageTable.STORAGE, StoragePojo.class);
    }

    /**
     * Create a new StorageDao with an attached configuration
     */
    public StorageDao(Configuration configuration) {
        super(StorageTable.STORAGE, StoragePojo.class, configuration);
    }

    @Override
    public Long getId(StoragePojo object) {
        return object.getStorageId();
    }

    /**
     * Fetch records that have <code>storage_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfStorageIdTable(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(StorageTable.STORAGE.STORAGE_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>storage_id IN (values)</code>
     */
    public List<StoragePojo> fetchByStorageIdTable(Long... values) {
        return fetch(StorageTable.STORAGE.STORAGE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>storage_id = value</code>
     */
    public StoragePojo fetchOneByStorageIdTable(Long value) {
        return fetchOne(StorageTable.STORAGE.STORAGE_ID, value);
    }

    /**
     * Fetch a unique record that has <code>storage_id = value</code>
     */
    public Optional<StoragePojo> fetchOptionalByStorageIdTable(Long value) {
        return fetchOptional(StorageTable.STORAGE.STORAGE_ID, value);
    }

    /**
     * Fetch records that have <code>stage_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfStageFkTable(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(StorageTable.STORAGE.STAGE_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>stage_fk IN (values)</code>
     */
    public List<StoragePojo> fetchByStageFkTable(Long... values) {
        return fetch(StorageTable.STORAGE.STAGE_FK, values);
    }

    /**
     * Fetch records that have <code>label BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfLabelTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(StorageTable.STORAGE.LABEL, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>label IN (values)</code>
     */
    public List<StoragePojo> fetchByLabelTable(String... values) {
        return fetch(StorageTable.STORAGE.LABEL, values);
    }

    /**
     * Fetch records that have <code>prefix BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfPrefixTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(StorageTable.STORAGE.PREFIX, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>prefix IN (values)</code>
     */
    public List<StoragePojo> fetchByPrefixTable(String... values) {
        return fetch(StorageTable.STORAGE.PREFIX, values);
    }

    /**
     * Fetch records that have <code>index_file BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfIndexFileTable(String lowerInclusive, String upperInclusive) {
        return fetchRange(StorageTable.STORAGE.INDEX_FILE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>index_file IN (values)</code>
     */
    public List<StoragePojo> fetchByIndexFileTable(String... values) {
        return fetch(StorageTable.STORAGE.INDEX_FILE, values);
    }

    /**
     * Fetch records that have <code>storage_type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfStorageTypeTable(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(StorageTable.STORAGE.STORAGE_TYPE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>storage_type IN (values)</code>
     */
    public List<StoragePojo> fetchByStorageTypeTable(Integer... values) {
        return fetch(StorageTable.STORAGE.STORAGE_TYPE, values);
    }

    /**
     * Fetch records that have <code>is_upload_complete BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<StoragePojo> fetchRangeOfIsUploadCompleteTable(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(StorageTable.STORAGE.IS_UPLOAD_COMPLETE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>is_upload_complete IN (values)</code>
     */
    public List<StoragePojo> fetchByIsUploadCompleteTable(Boolean... values) {
        return fetch(StorageTable.STORAGE.IS_UPLOAD_COMPLETE, values);
    }
}
