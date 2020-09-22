package com.ericdriggs.reportcard.reportcardscanner;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class ReportMetaData {
    private String org;
    private String repo;
    private String app;
    private String branch;
    private String buildIdentifier;
    private String stage;

    public ReportMetaData() {

    }

    public ReportMetaData(Map<ReportMetaDataVariable, String> varMap) {

        if (varMap.get(ReportMetaDataVariable.GIT_ORG) != null) {
            this.org = varMap.get(ReportMetaDataVariable.GIT_ORG);
        }
        if (varMap.get(ReportMetaDataVariable.GIT_REPO) != null) {
            this.repo = varMap.get(ReportMetaDataVariable.GIT_REPO);
        }
        if (varMap.get(ReportMetaDataVariable.GIT_BRANCH) != null) {
            this.branch = varMap.get(ReportMetaDataVariable.GIT_BRANCH);
        }
        if (varMap.get(ReportMetaDataVariable.BUILD_APP) != null) {
            this.app = varMap.get(ReportMetaDataVariable.BUILD_APP);
        }
        if (varMap.get(ReportMetaDataVariable.BUILD_IDENTIFIER) != null) {
            this.buildIdentifier = varMap.get(ReportMetaDataVariable.BUILD_IDENTIFIER);
        }
        if (varMap.get(ReportMetaDataVariable.BUILD_STAGE) != null) {
            this.stage = varMap.get(ReportMetaDataVariable.BUILD_STAGE);
        }

    }

//    public ReportMetaData(List<ArgumentValue> argumentValues) {
//
//        for (ArgumentValue argumentValue : argumentValues) {
//            if (argumentValue.getArgument() == ReportMetaDataVariable.GIT_ORG) {
//                this.org = argumentValue.getValue();
//            } else if (argumentValue.getArgument() == ReportMetaDataVariable.GIT_REPO) {
//                this.repo = argumentValue.getValue();
//            } else if (argumentValue.getArgument() == ReportMetaDataVariable.GIT_BRANCH) {
//                this.branch = argumentValue.getValue();
//            } else if (argumentValue.getArgument() == ReportMetaDataVariable.BUILD_APP) {
//                this.app = argumentValue.getValue();
//            } else if (argumentValue.getArgument() == ReportMetaDataVariable.BUILD_IDENTIFIER) {
//                this.buildIdentifier = argumentValue.getValue();
//            } else if (argumentValue.getArgument() == ReportMetaDataVariable.BUILD_STAGE) {
//                this.stage = argumentValue.getValue();
//            }
//        }
//
//    }

    public void validateAndSetDefaults() {
        Map<String, String> errors = new TreeMap<>();
        if (StringUtils.isEmpty(org)) {
            errors.put("org", "missing required field");
        }
        if (StringUtils.isEmpty(repo)) {
            errors.put("repo", "missing required field");
        }
        if (StringUtils.isEmpty(app)) {
            app = repo;
        }
        if (StringUtils.isEmpty(branch)) {
            errors.put("branch", "missing required field");
        }
        if (StringUtils.isEmpty(buildIdentifier)) {
            errors.put("buildIdentifier", "missing required field");
        }
        if (StringUtils.isEmpty(stage)) {
            errors.put("stage", "missing required field");
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "errors - " + Arrays.toString(errors.entrySet().toArray()));
        }
    }

}
