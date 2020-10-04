package com.ericdriggs.reportcard.scanner;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static com.ericdriggs.reportcard.scanner.ScannerArg.getToken;

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
    private String externalLinks;

    public ScannerPostRequest() {
    }

    public ScannerPostRequest(Map<ScannerArg, String> argMap) {

        if (argMap.get(ScannerArg.REPORTCARD_HOST) != null) {
            this.host = argMap.get(ScannerArg.REPORTCARD_HOST);
        }
        if (argMap.get(ScannerArg.REPORTCARD_USER) != null) {
            this.user = argMap.get(ScannerArg.REPORTCARD_USER);
        }
        if (argMap.get(ScannerArg.REPORTCARD_PASS) != null) {
            this.pass = argMap.get(ScannerArg.REPORTCARD_PASS);
        }

        if (argMap.get(ScannerArg.SCM_ORG) != null) {
            this.org = argMap.get(ScannerArg.SCM_ORG);
        }
        if (argMap.get(ScannerArg.SCM_REPO) != null) {
            this.repo = argMap.get(ScannerArg.SCM_REPO);
        }
        if (argMap.get(ScannerArg.SCM_BRANCH) != null) {
            this.branch = argMap.get(ScannerArg.SCM_BRANCH);
        }


        if (argMap.get(ScannerArg.BUILD_APP) != null) {
            this.app = argMap.get(ScannerArg.BUILD_APP);
        }
        if (argMap.get(ScannerArg.BUILD_IDENTIFIER) != null) {
            this.buildIdentifier = argMap.get(ScannerArg.BUILD_IDENTIFIER);
        }
        if (argMap.get(ScannerArg.BUILD_STAGE) != null) {
            this.stage = argMap.get(ScannerArg.BUILD_STAGE);
        }


        if (argMap.get(ScannerArg.TEST_REPORT_PATH) != null) {
            this.testReportPath = argMap.get(ScannerArg.TEST_REPORT_PATH);
        }
        if (argMap.get(ScannerArg.TEST_REPORT_REGEX) != null) {
            this.testReportRegex = argMap.get(ScannerArg.TEST_REPORT_REGEX);
        }


        if (argMap.get(ScannerArg.EXTERNAL_LINKS) != null) {
            String externalLinksWithTokens = argMap.get(ScannerArg.EXTERNAL_LINKS);
            this.externalLinks = replaceTokens(externalLinksWithTokens, argMap);
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

    protected String replaceTokens(String externalLinks, Map<ScannerArg, String> argMap) {
        for (ScannerArg scannerArg : ScannerArg.values()) {
            if (scannerArg == ScannerArg.EXTERNAL_LINKS) {
                continue;
            }

            final String token = getToken(scannerArg);
            if (externalLinks.contains(token)) {
                externalLinks = externalLinks.replace(token, argMap.get(scannerArg));
            }
        }
        return externalLinks;
    }


}
