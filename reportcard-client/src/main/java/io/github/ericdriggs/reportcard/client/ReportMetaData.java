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
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

//TODO: rename to ExecutionDetails
@Data
public class ReportMetaData {

    @JsonIgnore
    private static ObjectMapper objectMapper = new ObjectMapper();

    private String org;
    private String repo;
    private String branch;
    private String sha;
    private Map<String,String> metadata = new HashMap<>();
    private String externalExecutionId;
    private String stage;
    private Map<String, String> externalLinks;
    private String testReportPath;
    private String testReportRegex;


    public ReportMetaData() {
        this(Collections.EMPTY_MAP);
    }

    @SneakyThrows(JsonProcessingException.class)
    public ReportMetaData(Map<ClientArg, String> argMap) {
        final String org = argMap.get(ClientArg.SCM_ORG);
        final String repo = argMap.get(ClientArg.SCM_REPO);
        final String branch = argMap.get(ClientArg.SCM_BRANCH);
        final String sha = argMap.get(ClientArg.SCM_SHA);
        final String metadataJson = argMap.get(ClientArg.METADATA);
        final String stage = argMap.get(ClientArg.STAGE);
        final String testReportPath = argMap.get(ClientArg.TEST_REPORT_PATH);
        final String testReportRegex = argMap.get(ClientArg.TEST_REPORT_REGEX);
        final String externalExecutionId = argMap.get(ClientArg.EXECUTION_EXTERNAL_ID);

        this.org = org;
        this.repo = repo;
        this.branch = branch;
        this.sha = sha;

        if (!StringUtils.isEmpty(metadataJson)) {
            this.metadata.putAll(objectMapper.readValue(metadataJson, Map.class));
        }

        this.stage = stage;
        this.testReportPath = testReportPath;
        this.testReportRegex = testReportRegex;
        this.externalExecutionId = externalExecutionId;
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

        return validationErrors;
    }
}
