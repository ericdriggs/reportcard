/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Build implements Serializable {

    private static final long serialVersionUID = 429504732;

    private final Long          buildId;
    private final Integer       appBranchFk;
    private final Integer       appBranchBuildOrdinal;
    private final LocalDateTime buildCreated;

    public Build(Build value) {
        this.buildId = value.buildId;
        this.appBranchFk = value.appBranchFk;
        this.appBranchBuildOrdinal = value.appBranchBuildOrdinal;
        this.buildCreated = value.buildCreated;
    }

    public Build(
        Long          buildId,
        Integer       appBranchFk,
        Integer       appBranchBuildOrdinal,
        LocalDateTime buildCreated
    ) {
        this.buildId = buildId;
        this.appBranchFk = appBranchFk;
        this.appBranchBuildOrdinal = appBranchBuildOrdinal;
        this.buildCreated = buildCreated;
    }

    public Long getBuildId() {
        return this.buildId;
    }

    public Integer getAppBranchFk() {
        return this.appBranchFk;
    }

    public Integer getAppBranchBuildOrdinal() {
        return this.appBranchBuildOrdinal;
    }

    public LocalDateTime getBuildCreated() {
        return this.buildCreated;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Build (");

        sb.append(buildId);
        sb.append(", ").append(appBranchFk);
        sb.append(", ").append(appBranchBuildOrdinal);
        sb.append(", ").append(buildCreated);

        sb.append(")");
        return sb.toString();
    }
}
