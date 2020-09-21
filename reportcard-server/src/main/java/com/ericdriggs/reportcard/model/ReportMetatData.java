package com.ericdriggs.reportcard.model;

import lombok.Data;
import org.jooq.tools.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

@Data
public class ReportMetatData {
    private String org;
    private String repo;
    private String app;
    private String branch;
    private String buildIdentifier;
    private String stage;

    public void validateAndSetDefaults() {
        Map<String,String> errors = new TreeMap<>();
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
