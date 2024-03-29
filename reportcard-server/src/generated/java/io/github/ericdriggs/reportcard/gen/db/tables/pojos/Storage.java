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
public class Storage implements Serializable {

    private static final long serialVersionUID = -741296756;

    private Long    storageId;
    private Long    stageFk;
    private String  label;
    private String  prefix;
    private String  indexFile;
    private Integer storageType;

    public Storage() {}

    public Storage(Storage value) {
        this.storageId = value.storageId;
        this.stageFk = value.stageFk;
        this.label = value.label;
        this.prefix = value.prefix;
        this.indexFile = value.indexFile;
        this.storageType = value.storageType;
    }

    public Storage(
        Long    storageId,
        Long    stageFk,
        String  label,
        String  prefix,
        String  indexFile,
        Integer storageType
    ) {
        this.storageId = storageId;
        this.stageFk = stageFk;
        this.label = label;
        this.prefix = prefix;
        this.indexFile = indexFile;
        this.storageType = storageType;
    }

    /**
     * Getter for <code>reportcard.storage.storage_id</code>.
     */
    public Long getStorageId() {
        return this.storageId;
    }

    /**
     * Setter for <code>reportcard.storage.storage_id</code>.
     */
    public Storage setStorageId(Long storageId) {
        this.storageId = storageId;
        return this;
    }

    /**
     * Getter for <code>reportcard.storage.stage_fk</code>.
     */
    public Long getStageFk() {
        return this.stageFk;
    }

    /**
     * Setter for <code>reportcard.storage.stage_fk</code>.
     */
    public Storage setStageFk(Long stageFk) {
        this.stageFk = stageFk;
        return this;
    }

    /**
     * Getter for <code>reportcard.storage.label</code>.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Setter for <code>reportcard.storage.label</code>.
     */
    public Storage setLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * Getter for <code>reportcard.storage.prefix</code>.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Setter for <code>reportcard.storage.prefix</code>.
     */
    public Storage setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Getter for <code>reportcard.storage.index_file</code>.
     */
    public String getIndexFile() {
        return this.indexFile;
    }

    /**
     * Setter for <code>reportcard.storage.index_file</code>.
     */
    public Storage setIndexFile(String indexFile) {
        this.indexFile = indexFile;
        return this;
    }

    /**
     * Getter for <code>reportcard.storage.storage_type</code>.
     */
    public Integer getStorageType() {
        return this.storageType;
    }

    /**
     * Setter for <code>reportcard.storage.storage_type</code>.
     */
    public Storage setStorageType(Integer storageType) {
        this.storageType = storageType;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Storage (");

        sb.append(storageId);
        sb.append(", ").append(stageFk);
        sb.append(", ").append(label);
        sb.append(", ").append(prefix);
        sb.append(", ").append(indexFile);
        sb.append(", ").append(storageType);

        sb.append(")");
        return sb.toString();
    }
}
