/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;

import lombok.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StorageType implements Serializable {

    private static final long serialVersionUID = -1900520082;

    private Byte   storageTypeId;
    private String storageTypeName;

    public StorageType() {}

    public StorageType(StorageType value) {
        this.storageTypeId = value.storageTypeId;
        this.storageTypeName = value.storageTypeName;
    }

    public StorageType(
        Byte   storageTypeId,
        String storageTypeName
    ) {
        this.storageTypeId = storageTypeId;
        this.storageTypeName = storageTypeName;
    }

    /**
     * Getter for <code>reportcard.storage_type.storage_type_id</code>.
     */
    public Byte getStorageTypeId() {
        return this.storageTypeId;
    }

    /**
     * Setter for <code>reportcard.storage_type.storage_type_id</code>.
     */
    public StorageType setStorageTypeId(Byte storageTypeId) {
        this.storageTypeId = storageTypeId;
        return this;
    }

    /**
     * Getter for <code>reportcard.storage_type.storage_type_name</code>.
     */
    public String getStorageTypeName() {
        return this.storageTypeName;
    }

    /**
     * Setter for <code>reportcard.storage_type.storage_type_name</code>.
     */
    public StorageType setStorageTypeName(String storageTypeName) {
        this.storageTypeName = storageTypeName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("StorageType (");

        sb.append(storageTypeId);
        sb.append(", ").append(storageTypeName);

        sb.append(")");
        return sb.toString();
    }
}