/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Branch implements Serializable {

    private static final long serialVersionUID = -1411784091;

    private Integer       branchId;
    private String        branchName;
    private Integer       repoFk;
    private LocalDateTime lastRun;

    public Branch() {}

    public Branch(Branch value) {
        this.branchId = value.branchId;
        this.branchName = value.branchName;
        this.repoFk = value.repoFk;
        this.lastRun = value.lastRun;
    }

    public Branch(
        Integer       branchId,
        String        branchName,
        Integer       repoFk,
        LocalDateTime lastRun
    ) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.repoFk = repoFk;
        this.lastRun = lastRun;
    }

    /**
     * Getter for <code>reportcard.branch.branch_id</code>.
     */
    public Integer getBranchId() {
        return this.branchId;
    }

    /**
     * Setter for <code>reportcard.branch.branch_id</code>.
     */
    public Branch setBranchId(Integer branchId) {
        this.branchId = branchId;
        return this;
    }

    /**
     * Getter for <code>reportcard.branch.branch_name</code>.
     */
    public String getBranchName() {
        return this.branchName;
    }

    /**
     * Setter for <code>reportcard.branch.branch_name</code>.
     */
    public Branch setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    /**
     * Getter for <code>reportcard.branch.repo_fk</code>.
     */
    public Integer getRepoFk() {
        return this.repoFk;
    }

    /**
     * Setter for <code>reportcard.branch.repo_fk</code>.
     */
    public Branch setRepoFk(Integer repoFk) {
        this.repoFk = repoFk;
        return this;
    }

    /**
     * Getter for <code>reportcard.branch.last_run</code>.
     */
    public LocalDateTime getLastRun() {
        return this.lastRun;
    }

    /**
     * Setter for <code>reportcard.branch.last_run</code>.
     */
    public Branch setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Branch (");

        sb.append(branchId);
        sb.append(", ").append(branchName);
        sb.append(", ").append(repoFk);
        sb.append(", ").append(lastRun);

        sb.append(")");
        return sb.toString();
    }
}
