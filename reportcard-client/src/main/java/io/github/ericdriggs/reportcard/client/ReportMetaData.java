package io.github.ericdriggs.reportcard.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

//TODO: rename to TestPublishRequest
@Data
public class ReportMetaData {

    @JsonIgnore
    private static ObjectMapper objectMapper = new ObjectMapper();

    private String company;
    private String org;
    private String repo;
    private String branch;
    private String sha;
    private Map<String,String> jobInfo = new HashMap<>();
    private String runReference;
    private String stage;
    private Map<String, String> externalLinks = new HashMap<>();
    private String testReportPath;
    private String testReportRegex;
    private String karateJsonFile;


    public ReportMetaData() {
        this(Collections.EMPTY_MAP);
    }

    @SneakyThrows(JsonProcessingException.class)
    public ReportMetaData(Map<ClientArg, String> argMap) {
        final String company = argMap.get(ClientArg.SCM_COMPANY);
        final String org = argMap.get(ClientArg.SCM_ORG);
        final String repo = argMap.get(ClientArg.SCM_REPO);
        final String branch = argMap.get(ClientArg.SCM_BRANCH);
        final String sha = argMap.get(ClientArg.SCM_SHA);
        final String metadataJson = argMap.get(ClientArg.METADATA);
        final String stage = argMap.get(ClientArg.STAGE);
        final String testReportPath = argMap.get(ClientArg.TEST_REPORT_PATH);
        final String testReportRegex = argMap.get(ClientArg.TEST_REPORT_REGEX);
        final String karateJsonFile = argMap.get(ClientArg.KARATE_REPORT_PATH);
        final String runReference = argMap.get(ClientArg.RUN_REFERENCE);
        final String externalLinks = argMap.get(ClientArg.EXTERNAL_LINKS);

        this.company = company;
        this.org = org;
        this.repo = repo;
        this.branch = branch;
        this.sha = sha;

        if (!StringUtils.isEmpty(metadataJson)) {
            this.jobInfo.putAll(objectMapper.readValue(metadataJson, Map.class));
        }

        if (!StringUtils.isEmpty(externalLinks)) {
            this.externalLinks.putAll(objectMapper.readValue(externalLinks, Map.class));
        }

        this.stage = stage;
        this.testReportPath = testReportPath;
        this.testReportRegex = testReportRegex;
        this.karateJsonFile = karateJsonFile;
        this.runReference = runReference;
    }

    public Map<String,String> validate() {

        //Prepare errors
        Map<String, String> validationErrors = new ConcurrentSkipListMap<>();
        if (ObjectUtils.isEmpty(company)) {
            validationErrors.put("company", "missing required field");
        }
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

        return validationErrors;
    }
}
