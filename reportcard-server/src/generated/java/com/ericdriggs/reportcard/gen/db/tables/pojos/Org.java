/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;

import lombok.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Org implements Serializable {

    private static final long serialVersionUID = 440401688;

    private Integer orgId;
    private String  orgName;

    public Org() {}

    public Org(Org value) {
        this.orgId = value.orgId;
        this.orgName = value.orgName;
    }

    public Org(
        Integer orgId,
        String  orgName
    ) {
        this.orgId = orgId;
        this.orgName = orgName;
    }

    public Integer getOrgId() {
        return this.orgId;
    }

    public Org setOrgId(Integer orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public Org setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Org (");

        sb.append(orgId);
        sb.append(", ").append(orgName);

        sb.append(")");
        return sb.toString();
    }
}
