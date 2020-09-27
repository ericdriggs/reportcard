package com.ericdriggs.reportcard.scanner;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

@Data
public class ScannerPostRequest {

    private String host;
    private String user;
    private String pass;
    private String org;
    private String repo;
    private String app;
    private String branch;
    private String buildIdentifier;
    private String stage;
    private String testReportPath;
    private String testReportRegex;
//    private List<String> reports;

    public ScannerPostRequest() {

    }

    public ScannerPostRequest(Map<ScannerArgs, String> argMap) {

        if (argMap.get(ScannerArgs.SCM_ORG) != null) {
            this.org = argMap.get(ScannerArgs.SCM_ORG);
        }
        if (argMap.get(ScannerArgs.SCM_REPO) != null) {
            this.repo = argMap.get(ScannerArgs.SCM_REPO);
        }
        if (argMap.get(ScannerArgs.SCM_BRANCH) != null) {
            this.branch = argMap.get(ScannerArgs.SCM_BRANCH);
        }
        if (argMap.get(ScannerArgs.BUILD_APP) != null) {
            this.app = argMap.get(ScannerArgs.BUILD_APP);
        }
        if (argMap.get(ScannerArgs.BUILD_IDENTIFIER) != null) {
            this.buildIdentifier = argMap.get(ScannerArgs.BUILD_IDENTIFIER);
        }
        if (argMap.get(ScannerArgs.BUILD_STAGE) != null) {
            this.stage = argMap.get(ScannerArgs.BUILD_STAGE);
        }
        if (argMap.get(ScannerArgs.TEST_REPORT_PATH) != null) {
            this.testReportPath = argMap.get(ScannerArgs.TEST_REPORT_PATH);
        }
        if (argMap.get(ScannerArgs.TEST_REPORT_REGEX) != null) {
            this.testReportRegex = argMap.get(ScannerArgs.TEST_REPORT_REGEX);
        }

    }

    public void prepare() {
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
        if (StringUtils.isEmpty(testReportPath)) {
            errors.put("testReportPath", "missing required field");
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "errors - " + Arrays.toString(errors.entrySet().toArray()));
        }
    }

}
