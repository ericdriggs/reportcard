/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.records;


import io.github.ericdriggs.reportcard.gen.db.tables.Company;

import lombok.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CompanyRecord extends UpdatableRecordImpl<CompanyRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = -251987140;

    /**
     * Setter for <code>reportcard.company.company_id</code>.
     */
    public CompanyRecord setCompanyId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.company.company_id</code>.
     */
    public Integer getCompanyId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reportcard.company.company_name</code>.
     */
    public CompanyRecord setCompanyName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>reportcard.company.company_name</code>.
     */
    public String getCompanyName() {
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
        return Company.COMPANY.COMPANY_ID;
    }

    @Override
    public Field<String> field2() {
        return Company.COMPANY.COMPANY_NAME;
    }

    @Override
    public Integer component1() {
        return getCompanyId();
    }

    @Override
    public String component2() {
        return getCompanyName();
    }

    @Override
    public Integer value1() {
        return getCompanyId();
    }

    @Override
    public String value2() {
        return getCompanyName();
    }

    @Override
    public CompanyRecord value1(Integer value) {
        setCompanyId(value);
        return this;
    }

    @Override
    public CompanyRecord value2(String value) {
        setCompanyName(value);
        return this;
    }

    @Override
    public CompanyRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CompanyRecord
     */
    public CompanyRecord() {
        super(Company.COMPANY);
    }

    /**
     * Create a detached, initialised CompanyRecord
     */
    public CompanyRecord(Integer companyId, String companyName) {
        super(Company.COMPANY);

        setCompanyId(companyId);
        setCompanyName(companyName);
    }

    /**
     * Create a detached, initialised CompanyRecord
     */
    public CompanyRecord(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company value) {
        super(Company.COMPANY);

        if (value != null) {
            setCompanyId(value.getCompanyId());
            setCompanyName(value.getCompanyName());
        }
    }
}
