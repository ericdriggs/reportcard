/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.StorageType;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StorageTypeRecord;

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
public class StorageTypeDao extends DAOImpl<StorageTypeRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType, Byte> {

    /**
     * Create a new StorageTypeDao without any configuration
     */
    public StorageTypeDao() {
        super(StorageType.STORAGE_TYPE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType.class);
    }

    /**
     * Create a new StorageTypeDao with an attached configuration
     */
    public StorageTypeDao(Configuration configuration) {
        super(StorageType.STORAGE_TYPE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType.class, configuration);
    }

    @Override
    public Byte getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType object) {
        return object.getStorageTypeId();
    }

    /**
     * Fetch records that have <code>storage_type_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType> fetchRangeOfStorageTypeId(Byte lowerInclusive, Byte upperInclusive) {
        return fetchRange(StorageType.STORAGE_TYPE.STORAGE_TYPE_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>storage_type_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType> fetchByStorageTypeId(Byte... values) {
        return fetch(StorageType.STORAGE_TYPE.STORAGE_TYPE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>storage_type_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType fetchOneByStorageTypeId(Byte value) {
        return fetchOne(StorageType.STORAGE_TYPE.STORAGE_TYPE_ID, value);
    }

    /**
     * Fetch a unique record that has <code>storage_type_id = value</code>
     */
    public Optional<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType> fetchOptionalByStorageTypeId(Byte value) {
        return fetchOptional(StorageType.STORAGE_TYPE.STORAGE_TYPE_ID, value);
    }

    /**
     * Fetch records that have <code>storage_type_name BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType> fetchRangeOfStorageTypeName(String lowerInclusive, String upperInclusive) {
        return fetchRange(StorageType.STORAGE_TYPE.STORAGE_TYPE_NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>storage_type_name IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StorageType> fetchByStorageTypeName(String... values) {
        return fetch(StorageType.STORAGE_TYPE.STORAGE_TYPE_NAME, values);
    }
}
