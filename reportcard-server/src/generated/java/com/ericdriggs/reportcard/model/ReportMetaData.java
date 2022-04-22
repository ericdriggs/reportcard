package com.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ReportMetaData {
    private String org;
    private String repo;
    private String branch;
    private String sha;
    private HostApplicationPipeline hostApplicationPipeline;
    private String externalExecutionId;
    private String stage;
    private Map<String,String> externalLinks = Collections.emptyMap();

    public void validateAndSetDefaults() {
        Map<String,String> errors = new LinkedHashMap<>();
        addErrorIfMissing(errors, org, "org");
        addErrorIfMissing(errors, repo, "repo");
        addErrorIfMissing(errors, branch, "branch");
        addErrorIfMissing(errors, sha, "sha");
        if (hostApplicationPipeline == null)  {
            errors.put("hostApplicationPipeline", "missing required field");
        }

        if (hostApplicationPipeline.hasErrors()) {
            errors.put("hostApplicationPipeline", String.join(", ", hostApplicationPipeline.getValidationErrors()));
        }
        addErrorIfMissing(errors, externalExecutionId, "externalExecutionId");
        addErrorIfMissing(errors, stage, "stage");
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "errors - " + Arrays.toString(errors.entrySet().toArray()));
        }
    }

    protected void addErrorIfMissing(Map<String,String> errors,  String val, String variableName) {
        if (!StringUtils.hasText(val)) {
            errors.put(variableName, "missing required field");
        }
    }

    final static ObjectMapper mapper = new ObjectMapper();

    @JsonIgnore
    public String getExternalLinksJson() {
        if (externalLinks == null)  {
            return null;
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(externalLinks);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
