/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


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
public class Repo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer repoId;
    private String  repoName;
    private Integer orgFk;

    public Repo() {}

    public Repo(Repo value) {
        this.repoId = value.repoId;
        this.repoName = value.repoName;
        this.orgFk = value.orgFk;
    }

    public Repo(
        Integer repoId,
        String  repoName,
        Integer orgFk
    ) {
        this.repoId = repoId;
        this.repoName = repoName;
        this.orgFk = orgFk;
    }

    /**
     * Getter for <code>reportcard.repo.repo_id</code>.
     */
    public Integer getRepoId() {
        return this.repoId;
    }

    /**
     * Setter for <code>reportcard.repo.repo_id</code>.
     */
    public Repo setRepoId(Integer repoId) {
        this.repoId = repoId;
        return this;
    }

    /**
     * Getter for <code>reportcard.repo.repo_name</code>.
     */
    public String getRepoName() {
        return this.repoName;
    }

    /**
     * Setter for <code>reportcard.repo.repo_name</code>.
     */
    public Repo setRepoName(String repoName) {
        this.repoName = repoName;
        return this;
    }

    /**
     * Getter for <code>reportcard.repo.org_fk</code>.
     */
    public Integer getOrgFk() {
        return this.orgFk;
    }

    /**
     * Setter for <code>reportcard.repo.org_fk</code>.
     */
    public Repo setOrgFk(Integer orgFk) {
        this.orgFk = orgFk;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Repo (");

        sb.append(repoId);
        sb.append(", ").append(repoName);
        sb.append(", ").append(orgFk);

        sb.append(")");
        return sb.toString();
    }
}
