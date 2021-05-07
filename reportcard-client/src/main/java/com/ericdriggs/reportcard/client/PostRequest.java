package com.ericdriggs.reportcard.client;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.ericdriggs.reportcard.client.ClientArg.getToken;

@Data
public class PostRequest {

    private String reportCardHost;
    private String reportCardUser;
    private String reportCardPass;
    private String org;
    private String repo;
    private String branch;
    private String sha;

    private String contextHost;
    private String contextApplication;
    private String contextPipeline;

    private String executionExternalId;
    private String stage;

    private String testReportPath;
    private String testReportRegex;
    private Map<String, String> externalLinks;

    public PostRequest() {
    }

    public PostRequest(Map<ClientArg, String> argMap) {

        if (argMap.get(ClientArg.REPORTCARD_HOST) != null) {
            this.reportCardHost = argMap.get(ClientArg.REPORTCARD_HOST);
        }
        if (argMap.get(ClientArg.REPORTCARD_USER) != null) {
            this.reportCardUser = argMap.get(ClientArg.REPORTCARD_USER);
        }
        if (argMap.get(ClientArg.REPORTCARD_PASS) != null) {
            this.reportCardPass = argMap.get(ClientArg.REPORTCARD_PASS);
        }

        if (argMap.get(ClientArg.SCM_ORG) != null) {
            this.org = argMap.get(ClientArg.SCM_ORG);
        }
        if (argMap.get(ClientArg.SCM_REPO) != null) {
            this.repo = argMap.get(ClientArg.SCM_REPO);
        }
        if (argMap.get(ClientArg.SCM_BRANCH) != null) {
            this.branch = argMap.get(ClientArg.SCM_BRANCH);
        }
        if (argMap.get(ClientArg.SCM_SHA) != null) {
            this.sha = argMap.get(ClientArg.SCM_SHA);
        }


        if (argMap.get(ClientArg.CONTEXT_HOST) != null) {
            this.contextHost = argMap.get(ClientArg.CONTEXT_HOST);
        }
        if (argMap.get(ClientArg.CONTEXT_APPLICATION) != null) {
            this.contextApplication = argMap.get(ClientArg.CONTEXT_APPLICATION);
        }
        if (argMap.get(ClientArg.CONTEXT_PIPELINE) != null) {
            this.contextPipeline = argMap.get(ClientArg.CONTEXT_PIPELINE);
        }

        if (argMap.get(ClientArg.EXECUTION_EXTERNAL_ID) != null) {
            this.executionExternalId = argMap.get(ClientArg.EXECUTION_EXTERNAL_ID);
        }
        if (argMap.get(ClientArg.STAGE) != null) {
            this.stage = argMap.get(ClientArg.STAGE);
        }

        if (argMap.get(ClientArg.TEST_REPORT_PATH) != null) {
            this.testReportPath = argMap.get(ClientArg.TEST_REPORT_PATH);
        }
        if (argMap.get(ClientArg.TEST_REPORT_REGEX) != null) {
            this.testReportRegex = argMap.get(ClientArg.TEST_REPORT_REGEX);
        }

        this.externalLinks = buildExternalLinkMap(argMap.get(ClientArg.EXTERNAL_LINKS), argMap);

    }

    public void prepare() {

        if (StringUtils.isEmpty(executionExternalId)) {
            executionExternalId = UUID.randomUUID().toString();
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

    protected Map<String, String> buildExternalLinkMap(String externalLinksArg, Map<ClientArg, String> argMap) {
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

    protected String replaceTokens(String externalLinksString, Map<ClientArg, String> argMap) {
        for (ClientArg scannerArg : ClientArg.values()) {
            if (scannerArg == ClientArg.EXTERNAL_LINKS) {
                continue;
            }

            final String token = getToken(scannerArg);
            if (externalLinksString.contains(token)) {
                externalLinksString = externalLinksString.replace(token, argMap.get(scannerArg));
            }
        }
        return externalLinksString;
    }


}
