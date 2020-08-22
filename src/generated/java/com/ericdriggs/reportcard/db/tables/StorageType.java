/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables;


import com.ericdriggs.reportcard.db.Keys;
import com.ericdriggs.reportcard.db.Reportcard;
import com.ericdriggs.reportcard.db.tables.records.StorageTypeRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StorageType extends TableImpl<StorageTypeRecord> {

    private static final long serialVersionUID = -1871688142;

    /**
     * The reference instance of <code>reportcard.storage_type</code>
     */
    public static final StorageType STORAGE_TYPE = new StorageType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StorageTypeRecord> getRecordType() {
        return StorageTypeRecord.class;
    }

    /**
     * The column <code>reportcard.storage_type.id</code>.
     */
    public final TableField<StorageTypeRecord, Byte> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.storage_type.name</code>.
     */
    public final TableField<StorageTypeRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.CHAR(16), this, "");

    /**
     * Create a <code>reportcard.storage_type</code> table reference
     */
    public StorageType() {
        this(DSL.name("storage_type"), null);
    }

    /**
     * Create an aliased <code>reportcard.storage_type</code> table reference
     */
    public StorageType(String alias) {
        this(DSL.name(alias), STORAGE_TYPE);
    }

    /**
     * Create an aliased <code>reportcard.storage_type</code> table reference
     */
    public StorageType(Name alias) {
        this(alias, STORAGE_TYPE);
    }

    private StorageType(Name alias, Table<StorageTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private StorageType(Name alias, Table<StorageTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> StorageType(Table<O> child, ForeignKey<O, StorageTypeRecord> key) {
        super(child, key, STORAGE_TYPE);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public Identity<StorageTypeRecord, Byte> getIdentity() {
        return Keys.IDENTITY_STORAGE_TYPE;
    }

    @Override
    public UniqueKey<StorageTypeRecord> getPrimaryKey() {
        return Keys.KEY_STORAGE_TYPE_PRIMARY;
    }

    @Override
    public List<UniqueKey<StorageTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<StorageTypeRecord>>asList(Keys.KEY_STORAGE_TYPE_PRIMARY);
    }

    @Override
    public StorageType as(String alias) {
        return new StorageType(DSL.name(alias), this);
    }

    @Override
    public StorageType as(Name alias) {
        return new StorageType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public StorageType rename(String name) {
        return new StorageType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public StorageType rename(Name name) {
        return new StorageType(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Byte, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
