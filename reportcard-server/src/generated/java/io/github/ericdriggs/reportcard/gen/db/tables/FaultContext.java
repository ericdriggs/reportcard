/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables;


import io.github.ericdriggs.reportcard.gen.db.Keys;
import io.github.ericdriggs.reportcard.gen.db.Reportcard;
import io.github.ericdriggs.reportcard.gen.db.tables.records.FaultContextRecord;

import lombok.Generated;

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
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FaultContext extends TableImpl<FaultContextRecord> {

    private static final long serialVersionUID = -46330864;

    /**
     * The reference instance of <code>reportcard.fault_context</code>
     */
    public static final FaultContext FAULT_CONTEXT = new FaultContext();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FaultContextRecord> getRecordType() {
        return FaultContextRecord.class;
    }

    /**
     * The column <code>reportcard.fault_context.fault_context_id</code>.
     */
    public final TableField<FaultContextRecord, Byte> FAULT_CONTEXT_ID = createField(DSL.name("fault_context_id"), SQLDataType.TINYINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>reportcard.fault_context.fault_context_name</code>.
     */
    public final TableField<FaultContextRecord, String> FAULT_CONTEXT_NAME = createField(DSL.name("fault_context_name"), SQLDataType.VARCHAR(64).nullable(false), this, "");

    private FaultContext(Name alias, Table<FaultContextRecord> aliased) {
        this(alias, aliased, null);
    }

    private FaultContext(Name alias, Table<FaultContextRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>reportcard.fault_context</code> table reference
     */
    public FaultContext(String alias) {
        this(DSL.name(alias), FAULT_CONTEXT);
    }

    /**
     * Create an aliased <code>reportcard.fault_context</code> table reference
     */
    public FaultContext(Name alias) {
        this(alias, FAULT_CONTEXT);
    }

    /**
     * Create a <code>reportcard.fault_context</code> table reference
     */
    public FaultContext() {
        this(DSL.name("fault_context"), null);
    }

    public <O extends Record> FaultContext(Table<O> child, ForeignKey<O, FaultContextRecord> key) {
        super(child, key, FAULT_CONTEXT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Reportcard.REPORTCARD;
    }

    @Override
    public Identity<FaultContextRecord, Byte> getIdentity() {
        return (Identity<FaultContextRecord, Byte>) super.getIdentity();
    }

    @Override
    public UniqueKey<FaultContextRecord> getPrimaryKey() {
        return Keys.KEY_FAULT_CONTEXT_PRIMARY;
    }

    @Override
    public FaultContext as(String alias) {
        return new FaultContext(DSL.name(alias), this);
    }

    @Override
    public FaultContext as(Name alias) {
        return new FaultContext(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FaultContext rename(String name) {
        return new FaultContext(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FaultContext rename(Name name) {
        return new FaultContext(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Byte, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
