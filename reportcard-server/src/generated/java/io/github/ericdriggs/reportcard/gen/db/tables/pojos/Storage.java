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

    private static final long serialVersionUID = -1067741147;

    private Long   storageId;
    private Long   stageFk;
    private String label;
    private String prefix;
    private String indexfile;

    public Storage() {}

    public Storage(Storage value) {
        this.storageId = value.storageId;
        this.stageFk = value.stageFk;
        this.label = value.label;
        this.prefix = value.prefix;
        this.indexfile = value.indexfile;
    }

    public Storage(
        Long   storageId,
        Long   stageFk,
        String label,
        String prefix,
        String indexfile
    ) {
        this.storageId = storageId;
        this.stageFk = stageFk;
        this.label = label;
        this.prefix = prefix;
        this.indexfile = indexfile;
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
     * Getter for <code>reportcard.storage.indexFile</code>.
     */
    public String getIndexfile() {
        return this.indexfile;
    }

    /**
     * Setter for <code>reportcard.storage.indexFile</code>.
     */
    public Storage setIndexfile(String indexfile) {
        this.indexfile = indexfile;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Storage (");

        sb.append(storageId);
        sb.append(", ").append(stageFk);
        sb.append(", ").append(label);
        sb.append(", ").append(prefix);
        sb.append(", ").append(indexfile);

        sb.append(")");
        return sb.toString();
    }
}
