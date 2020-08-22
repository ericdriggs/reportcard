/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.daos;


import com.ericdriggs.reportcard.db.tables.StorageType;
import com.ericdriggs.reportcard.db.tables.records.StorageTypeRecord;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StorageTypeDao extends DAOImpl<StorageTypeRecord, com.ericdriggs.reportcard.db.tables.pojos.StorageType, Byte> {

    /**
     * Create a new StorageTypeDao without any configuration
     */
    public StorageTypeDao() {
        super(StorageType.STORAGE_TYPE, com.ericdriggs.reportcard.db.tables.pojos.StorageType.class);
    }

    /**
     * Create a new StorageTypeDao with an attached configuration
     */
    public StorageTypeDao(Configuration configuration) {
        super(StorageType.STORAGE_TYPE, com.ericdriggs.reportcard.db.tables.pojos.StorageType.class, configuration);
    }

    @Override
    public Byte getId(com.ericdriggs.reportcard.db.tables.pojos.StorageType object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.StorageType> fetchRangeOfId(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(StorageType.STORAGE_TYPE.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.StorageType> fetchById(Byte... values) {
        return fetch(StorageType.STORAGE_TYPE.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.ericdriggs.reportcard.db.tables.pojos.StorageType fetchOneById(Byte value) {
        return fetchOne(StorageType.STORAGE_TYPE.ID, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.StorageType> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(StorageType.STORAGE_TYPE.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<com.ericdriggs.reportcard.db.tables.pojos.StorageType> fetchByName(String... values) {
        return fetch(StorageType.STORAGE_TYPE.NAME, values);
    }
}
