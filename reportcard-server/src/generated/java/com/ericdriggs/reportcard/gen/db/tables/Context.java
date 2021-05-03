/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables;


import com.ericdriggs.reportcard.gen.db.Keys;
import com.ericdriggs.reportcard.gen.db.Reportcard;
import com.ericdriggs.reportcard.gen.db.tables.records.ContextRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
public class Context extends TableImpl<ContextRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>reportcard.context</code>
     */
    public static final Context CONTEXT = new Context();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContextRecord> getRecordType() {
        return ContextRecord.class;
    }

    /**
     * The column <code>reportcard.context.context_id</code>.
     */
    public final TableField<ContextRecord, Long> CONTEXT_ID = createField(DSL.name("context_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.context.sha_fk</code>.
     */
    public final TableField<ContextRecord, Long> SHA_FK = createField(DSL.name("sha_fk"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>reportcard.context.host</code>.
     */
    public final TableField<ContextRecord, String> HOST = createField(DSL.name("host"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>reportcard.context.application</code>.
     */
    public final TableField<ContextRecord, String> APPLICATION = createField(DSL.name("application"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>reportcard.context.pipeline</code>.
     */
    public final TableField<ContextRecord, String> PIPELINE = createField(DSL.name("pipeline"), SQLDataType.VARCHAR(255), this, "");

    private Context(Name alias, Table<ContextRecord> aliased) {
        this(alias, aliased, null);
    }

    private Context(Name alias, Table<ContextRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.context</code> table reference
     */
    public Context(String alias) {
        this(DSL.name(alias), CONTEXT);
    }

    /**
     * Create an aliased <code>reportcard.context</code> table reference
     */
    public Context(Name alias) {
        this(alias, CONTEXT);
    }

    /**
     * Create a <code>reportcard.context</code> table reference
     */
    public Context() {
        this(DSL.name("context"), null);
    }

    public <O extends Record> Context(Table<O> child, ForeignKey<O, ContextRecord> key) {
        super(child, key, CONTEXT);
    }

    @Override
    public Schema getSchema() {
        return Reportcard.REPORTCARD;
    }

    @Override
    public Identity<ContextRecord, Long> getIdentity() {
        return (Identity<ContextRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ContextRecord> getPrimaryKey() {
        return Keys.KEY_CONTEXT_PRIMARY;
    }

    @Override
    public List<UniqueKey<ContextRecord>> getKeys() {
        return Arrays.<UniqueKey<ContextRecord>>asList(Keys.KEY_CONTEXT_PRIMARY);
    }

    @Override
    public List<ForeignKey<ContextRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ContextRecord, ?>>asList(Keys.CONTEXT_SHA_FK);
    }

    private transient Sha _sha;

    public Sha sha() {
        if (_sha == null)
            _sha = new Sha(this, Keys.CONTEXT_SHA_FK);

        return _sha;
    }

    @Override
    public Context as(String alias) {
        return new Context(DSL.name(alias), this);
    }

    @Override
    public Context as(Name alias) {
        return new Context(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Context rename(String name) {
        return new Context(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Context rename(Name name) {
        return new Context(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
