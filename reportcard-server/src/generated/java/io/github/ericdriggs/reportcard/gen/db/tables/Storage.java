/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Indexes;
import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StorageRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


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
public class Storage extends TableImpl<StorageRecord> {

    private static final long serialVersionUID = 1343527043;

    /**
     * The reference instance of <code>reportcard.storage</code>
     */
    public static final Storage STORAGE = new Storage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StorageRecord> getRecordType() {
        return StorageRecord.class;
    }

    /**
     * The column <code>reportcard.storage.storage_id</code>.
     */
    public final TableField<StorageRecord, Long> STORAGE_ID = createField(DSL.name("storage_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.storage.stage_fk</code>.
     */
    public final TableField<StorageRecord, Long> STAGE_FK = createField(DSL.name("stage_fk"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.storage.path</code>.
     */
    public final TableField<StorageRecord, String> PATH = createField(DSL.name("path"), SQLDataType.VARCHAR(1024).nullable(false), this, "");

    /**
     * The column <code>reportcard.storage.indexFile</code>.
     */
    public final TableField<StorageRecord, String> INDEXFILE = createField(DSL.name("indexFile"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>reportcard.storage.type</code>.
     */
    public final TableField<StorageRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(64).nullable(false), this, "");

    private Storage(Name alias, Table<StorageRecord> aliased) {
        this(alias, aliased, null);
    }

    private Storage(Name alias, Table<StorageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.storage</code> table reference
     */
    public Storage(String alias) {
        this(DSL.name(alias), STORAGE);
    }

    /**
     * Create an aliased <code>reportcard.storage</code> table reference
     */
    public Storage(Name alias) {
        this(alias, STORAGE);
    }

    /**
     * Create a <code>reportcard.storage</code> table reference
     */
    public Storage() {
        this(DSL.name("storage"), null);
    }

    public <O extends Record> Storage(Table<O> child, ForeignKey<O, StorageRecord> key) {
        super(child, key, STORAGE);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.STORAGE_STAGE_FK_IDX);
    }

    @Override
    public UniqueKey<StorageRecord> getPrimaryKey() {
        return Keys.KEY_STORAGE_PRIMARY;
    }

    @Override
    public List<UniqueKey<StorageRecord>> getKeys() {
        return Arrays.<UniqueKey<StorageRecord>>asList(Keys.KEY_STORAGE_PRIMARY);
    }

    @Override
    public List<ForeignKey<StorageRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<StorageRecord, ?>>asList(Keys.STAGE_FK);
    }

    private transient Stage _stage;

    public Stage stage() {
        if (_stage == null)
            _stage = new Stage(this, Keys.STAGE_FK);

        return _stage;
    }

    @Override
    public Storage as(String alias) {
        return new Storage(DSL.name(alias), this);
    }

    @Override
    public Storage as(Name alias) {
        return new Storage(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Storage rename(String name) {
        return new Storage(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Storage rename(Name name) {
        return new Storage(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
