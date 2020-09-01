package com.ericdriggs.reportcard.model;

import lombok.Data;

@Data
public class BuildStagePathRequest {
    private String orgName;
    private String repoName;
    private String appName;
    private String branchName;
    private String buildUniqueString;
    private String stageName;

    public boolean isComplete() {
        return orgName != null
                && repoName != null
                && appName != null
                && branchName != null
                && buildUniqueString != null
                && stageName != null;
    }

}