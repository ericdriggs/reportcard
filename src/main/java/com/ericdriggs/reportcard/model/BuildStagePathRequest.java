package com.ericdriggs.reportcard.model;

import lombok.Data;

//TOMAYBE: will likely have to change from buildOrdinal to buildSha
@Data
public class BuildStagePathRequest {
    private String orgName;
    private String repoName;
    private String appName;
    private String branchName;
    private String buildUniqueString;
    private String stageName;

}