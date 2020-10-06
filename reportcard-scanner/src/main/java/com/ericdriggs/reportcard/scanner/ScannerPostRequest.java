package com.ericdriggs.reportcard.scanner;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.*;

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
    private Map<String, String> externalLinks;

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

        this.externalLinks = buildExternalLinkMap(argMap.get(ScannerArg.EXTERNAL_LINKS), argMap);

    }

    public void prepare() {

        //Set defaults for optional values
        if (StringUtils.isEmpty(app)) {
            app = repo;
        }
        if (StringUtils.isEmpty(buildIdentifier)) {
            buildIdentifier = UUID.randomUUID().toString();
        }
        if (StringUtils.isEmpty(testReportRegex)) {
            testReportRegex = ".*[.]xml";
        }

        //Prepare errors
        Map<String, String> validationErrors = new TreeMap<>();
        if (StringUtils.isEmpty(org)) {
            validationErrors.put("org", "missing required field");
        }
        if (StringUtils.isEmpty(repo)) {
            validationErrors.put("repo", "missing required field");
        }
        if (StringUtils.isEmpty(branch)) {
            validationErrors.put("branch", "missing required field");
        }

        if (StringUtils.isEmpty(stage)) {
            validationErrors.put("stage", "missing required field");
        }
        if (StringUtils.isEmpty(testReportPath)) {
            validationErrors.put("testReportPath", "missing required field");
        }

        if (!validationErrors.isEmpty()) {
            throw new BadRequestException(validationErrors);
        }
    }

    protected Map<String, String> buildExternalLinkMap(String externalLinksArg, Map<ScannerArg, String> argMap) {
        if (StringUtils.isEmpty(externalLinksArg)) {
            return Collections.emptyMap();
        }

        String externalLinksDetokenized = replaceTokens(externalLinksArg, argMap);

        Map<String, String> linkMap = new HashMap<>();
        String[] links = externalLinksDetokenized.split(",");
        int count = 0;
        for (String link : links) {
            count++;
            String key;
            String value;
            if (!link.contains("|")) {
                key = Integer.toString(count);
                value = link;
            } else {
                int pos = link.indexOf("|");
                key = link.substring(0, pos);
                value = link.substring(pos + 1);
            }

            linkMap.put(key, value);
        }
        return linkMap;

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
