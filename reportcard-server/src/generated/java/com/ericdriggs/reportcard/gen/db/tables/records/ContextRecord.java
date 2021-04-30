/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.records;


import com.ericdriggs.reportcard.gen.db.tables.Context;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


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
public class ContextRecord extends UpdatableRecordImpl<ContextRecord> implements Record5<Long, Long, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>reportcard.context.context_id</code>.
     */
    public ContextRecord setContextId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.context.context_id</code>.
     */
    public Long getContextId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>reportcard.context.sha_fk</code>.
     */
    public ContextRecord setShaFk(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.context.sha_fk</code>.
     */
    public Long getShaFk() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>reportcard.context.host</code>.
     */
    public ContextRecord setHost(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.context.host</code>.
     */
    public String getHost() {
        return (String) get(2);
    }

    /**
     * Setter for <code>reportcard.context.application</code>.
     */
    public ContextRecord setApplication(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.context.application</code>.
     */
    public String getApplication() {
        return (String) get(3);
    }

    /**
     * Setter for <code>reportcard.context.pipeline</code>.
     */
    public ContextRecord setPipeline(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.context.pipeline</code>.
     */
    public String getPipeline() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, Long, String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Context.CONTEXT.CONTEXT_ID;
    }

    @Override
    public Field<Long> field2() {
        return Context.CONTEXT.SHA_FK;
    }

    @Override
    public Field<String> field3() {
        return Context.CONTEXT.HOST;
    }

    @Override
    public Field<String> field4() {
        return Context.CONTEXT.APPLICATION;
    }

    @Override
    public Field<String> field5() {
        return Context.CONTEXT.PIPELINE;
    }

    @Override
    public Long component1() {
        return getContextId();
    }

    @Override
    public Long component2() {
        return getShaFk();
    }

    @Override
    public String component3() {
        return getHost();
    }

    @Override
    public String component4() {
        return getApplication();
    }

    @Override
    public String component5() {
        return getPipeline();
    }

    @Override
    public Long value1() {
        return getContextId();
    }

    @Override
    public Long value2() {
        return getShaFk();
    }

    @Override
    public String value3() {
        return getHost();
    }

    @Override
    public String value4() {
        return getApplication();
    }

    @Override
    public String value5() {
        return getPipeline();
    }

    @Override
    public ContextRecord value1(Long value) {
        setContextId(value);
        return this;
    }

    @Override
    public ContextRecord value2(Long value) {
        setShaFk(value);
        return this;
    }

    @Override
    public ContextRecord value3(String value) {
        setHost(value);
        return this;
    }

    @Override
    public ContextRecord value4(String value) {
        setApplication(value);
        return this;
    }

    @Override
    public ContextRecord value5(String value) {
        setPipeline(value);
        return this;
    }

    @Override
    public ContextRecord values(Long value1, Long value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ContextRecord
     */
    public ContextRecord() {
        super(Context.CONTEXT);
    }

    /**
     * Create a detached, initialised ContextRecord
     */
    public ContextRecord(Long contextId, Long shaFk, String host, String application, String pipeline) {
        super(Context.CONTEXT);

        setContextId(contextId);
        setShaFk(shaFk);
        setHost(host);
        setApplication(application);
        setPipeline(pipeline);
    }
}
