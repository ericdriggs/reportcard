package io.github.ericdriggs.reportcard.client;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public class ReportMetaData {

    private String org;
    private String repo;
    private String branch;
    private String sha;
    private HostApplicationPipeline hostApplicationPipeline = new HostApplicationPipeline();
    private String externalExecutionId;
    private String stage;
    private Map<String, String> externalLinks;
    private String testReportPath;
    private String testReportRegex;


    public ReportMetaData() {
        this(Collections.EMPTY_MAP);
    }
    public ReportMetaData(Map<ClientArg, String> argMap) {
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
            this.getHostApplicationPipeline().setHost(argMap.get(ClientArg.CONTEXT_HOST));
        }
        if (argMap.get(ClientArg.CONTEXT_APPLICATION) != null) {
            this.getHostApplicationPipeline().setApplication(argMap.get(ClientArg.CONTEXT_APPLICATION));

        }
        if (argMap.get(ClientArg.CONTEXT_PIPELINE) != null) {
            this.getHostApplicationPipeline().setPipeline(argMap.get(ClientArg.CONTEXT_PIPELINE));
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

        this.externalExecutionId = argMap.get(ClientArg.EXECUTION_EXTERNAL_ID);
        if (ObjectUtils.isEmpty(this.getExternalExecutionId())) {
            this.externalExecutionId = UUID.randomUUID().toString();
        }

    }

    public Map<String,String> validate() {

        //Prepare errors
        Map<String, String> validationErrors = new ConcurrentSkipListMap<>();
        if (ObjectUtils.isEmpty(org)) {
            validationErrors.put("org", "missing required field");
        }
        if (ObjectUtils.isEmpty(repo)) {
            validationErrors.put("repo", "missing required field");
        }
        if (ObjectUtils.isEmpty(branch)) {
            validationErrors.put("branch", "missing required field");
        }

        if (ObjectUtils.isEmpty(sha)) {
            validationErrors.put("sha", "missing required field");
        }

        if (ObjectUtils.isEmpty(stage)) {
            validationErrors.put("stage", "missing required field");
        }

        if (ObjectUtils.isEmpty(hostApplicationPipeline)) {
            validationErrors.put("hostApplicationPipeline", "missing required hostApplicationPipeline");
        }
        else if (ObjectUtils.isEmpty(hostApplicationPipeline.getHost())) {
            validationErrors.put("hostApplicationPipeline.getHost()", "missing required hostApplicationPipeline.getHost()");
        }

        return validationErrors;
    }
}
