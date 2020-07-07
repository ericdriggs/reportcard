/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.ragnarok.db.tables;


import com.ericdriggs.ragnarok.db.Keys;
import com.ericdriggs.ragnarok.db.Ragnarok;
import com.ericdriggs.ragnarok.db.tables.records.OrgRecord;

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
public class Org extends TableImpl<OrgRecord> {

    private static final long serialVersionUID = -2129442296;

    /**
     * The reference instance of <code>ragnarok.org</code>
     */
    public static final Org ORG = new Org();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrgRecord> getRecordType() {
        return OrgRecord.class;
    }

    /**
     * The column <code>ragnarok.org.id</code>.
     */
    public final TableField<OrgRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>ragnarok.org.name</code>.
     */
    public final TableField<OrgRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * Create a <code>ragnarok.org</code> table reference
     */
    public Org() {
        this(DSL.name("org"), null);
    }

    /**
     * Create an aliased <code>ragnarok.org</code> table reference
     */
    public Org(String alias) {
        this(DSL.name(alias), ORG);
    }

    /**
     * Create an aliased <code>ragnarok.org</code> table reference
     */
    public Org(Name alias) {
        this(alias, ORG);
    }

    private Org(Name alias, Table<OrgRecord> aliased) {
        this(alias, aliased, null);
    }

    private Org(Name alias, Table<OrgRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Org(Table<O> child, ForeignKey<O, OrgRecord> key) {
        super(child, key, ORG);
    }

    @Override
    public Schema getSchema() {
        return Ragnarok.RAGNAROK;
    }

    @Override
    public Identity<OrgRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ORG;
    }

    @Override
    public UniqueKey<OrgRecord> getPrimaryKey() {
        return Keys.KEY_ORG_PRIMARY;
    }

    @Override
    public List<UniqueKey<OrgRecord>> getKeys() {
        return Arrays.<UniqueKey<OrgRecord>>asList(Keys.KEY_ORG_PRIMARY, Keys.KEY_ORG_IDX_ORG_NAME);
    }

    @Override
    public Org as(String alias) {
        return new Org(DSL.name(alias), this);
    }

    @Override
    public Org as(Name alias) {
        return new Org(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Org rename(String name) {
        return new Org(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Org rename(Name name) {
        return new Org(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}