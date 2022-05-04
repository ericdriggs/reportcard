/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.Org;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
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
public class OrgRecord extends UpdatableRecordImpl<OrgRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>reportcard.org.org_id</code>.
     */
    public OrgRecord setOrgId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.org.org_id</code>.
     */
    public Integer getOrgId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.org.org_name</code>.
     */
    public OrgRecord setOrgName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.org.org_name</code>.
     */
    public String getOrgName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Org.ORG.ORG_ID;
    }

    @Override
    public Field<String> field2() {
        return Org.ORG.ORG_NAME;
    }

    @Override
    public Integer component1() {
        return getOrgId();
    }

    @Override
    public String component2() {
        return getOrgName();
    }

    @Override
    public Integer value1() {
        return getOrgId();
    }

    @Override
    public String value2() {
        return getOrgName();
    }

    @Override
    public OrgRecord value1(Integer value) {
        setOrgId(value);
        return this;
    }

    @Override
    public OrgRecord value2(String value) {
        setOrgName(value);
        return this;
    }

    @Override
    public OrgRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OrgRecord
     */
    public OrgRecord() {
        super(Org.ORG);
    }

    /**
     * Create a detached, initialised OrgRecord
     */
    public OrgRecord(Integer orgId, String orgName) {
        super(Org.ORG);

        setOrgId(orgId);
        setOrgName(orgName);
    }
}
