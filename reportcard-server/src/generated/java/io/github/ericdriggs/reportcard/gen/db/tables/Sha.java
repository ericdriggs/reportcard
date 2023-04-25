/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Indexes;
import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.ShaRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class Sha extends TableImpl<ShaRecord> {

    private static final long serialVersionUID = -1321306048;

    /**
     * The reference instance of <code>reportcard.sha</code>
     */
    public static final Sha SHA = new Sha();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ShaRecord> getRecordType() {
        return ShaRecord.class;
    }

    /**
     * The column <code>reportcard.sha.sha_id</code>.
     */
    public final TableField<ShaRecord, Long> SHA_ID = createField(DSL.name("sha_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.sha.sha</code>.
     */
    public final TableField<ShaRecord, String> SHA_ = createField(DSL.name("sha"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>reportcard.sha.sha_created</code>.
     */
    public final TableField<ShaRecord, LocalDateTime> SHA_CREATED = createField(DSL.name("sha_created"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>reportcard.sha.repo_fk</code>.
     */
    public final TableField<ShaRecord, Integer> REPO_FK = createField(DSL.name("repo_fk"), SQLDataType.INTEGER.nullable(false), this, "");

    private Sha(Name alias, Table<ShaRecord> aliased) {
        this(alias, aliased, null);
    }

    private Sha(Name alias, Table<ShaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.sha</code> table reference
     */
    public Sha(String alias) {
        this(DSL.name(alias), SHA);
    }

    /**
     * Create an aliased <code>reportcard.sha</code> table reference
     */
    public Sha(Name alias) {
        this(alias, SHA);
    }

    /**
     * Create a <code>reportcard.sha</code> table reference
     */
    public Sha() {
        this(DSL.name("sha"), null);
    }

    public <O extends Record> Sha(Table<O> child, ForeignKey<O, ShaRecord> key) {
        super(child, key, SHA);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SHA_BUILD_CREATED, Indexes.SHA_REPO_FK_IDX);
    }

    @Override
    public Identity<ShaRecord, Long> getIdentity() {
        return (Identity<ShaRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ShaRecord> getPrimaryKey() {
        return Keys.KEY_SHA_PRIMARY;
    }

    @Override
    public List<UniqueKey<ShaRecord>> getKeys() {
        return Arrays.<UniqueKey<ShaRecord>>asList(Keys.KEY_SHA_PRIMARY);
    }

    @Override
    public List<ForeignKey<ShaRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ShaRecord, ?>>asList(Keys.REPO_FK);
    }

    private transient Repo _repo;

    public Repo repo() {
        if (_repo == null)
            _repo = new Repo(this, Keys.REPO_FK);

        return _repo;
    }

    @Override
    public Sha as(String alias) {
        return new Sha(DSL.name(alias), this);
    }

    @Override
    public Sha as(Name alias) {
        return new Sha(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Sha rename(String name) {
        return new Sha(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Sha rename(Name name) {
        return new Sha(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, LocalDateTime, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
