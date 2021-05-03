package com.ericdriggs.reportcard.model;

import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ReportMetaData {
    private String org;
    private String repo;
    private String branch;
    private String sha;
    private HostApplicationPipeline hostApplicatiionPipeline;
    private String externalExecutionId;
    private String stage;

    public void validateAndSetDefaults() {
        Map<String,String> errors = new LinkedHashMap<>();
        addErrorIfMissing(errors, org, "org");
        addErrorIfMissing(errors, repo, "repo");
        addErrorIfMissing(errors, branch, "branch");
        addErrorIfMissing(errors, sha, "sha");
        if (hostApplicatiionPipeline == null)  {
            errors.put("hostApplicatiionPipeline", "missing required field");
        }

        if (hostApplicatiionPipeline.hasErrors()) {
            errors.put("hostApplicationPipeline", String.join(", ", hostApplicatiionPipeline.getValidationErrors()));
        }
        addErrorIfMissing(errors, externalExecutionId, "externalExecutionId");
        addErrorIfMissing(errors, stage, "stage");
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "errors - " + Arrays.toString(errors.entrySet().toArray()));
        }
    }

    protected void addErrorIfMissing(Map<String,String> errors,  String val, String variableName) {
        if (StringUtils.isEmpty(val)) {
            errors.put(variableName, "missing required field");
        }
    }
}
