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
public class Company implements Serializable {

    private static final long serialVersionUID = 498708604;

    private Integer companyId;
    private String  companyName;

    public Company() {}

    public Company(Company value) {
        this.companyId = value.companyId;
        this.companyName = value.companyName;
    }

    public Company(
        Integer companyId,
        String  companyName
    ) {
        this.companyId = companyId;
        this.companyName = companyName;
    }

    /**
     * Getter for <code>reportcard.company.company_id</code>.
     */
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * Setter for <code>reportcard.company.company_id</code>.
     */
    public Company setCompanyId(Integer companyId) {
        this.companyId = companyId;
        return this;
    }

    /**
     * Getter for <code>reportcard.company.company_name</code>.
     */
    public String getCompanyName() {
        return this.companyName;
    }

    /**
     * Setter for <code>reportcard.company.company_name</code>.
     */
    public Company setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Company (");

        sb.append(companyId);
        sb.append(", ").append(companyName);

        sb.append(")");
        return sb.toString();
    }
}
