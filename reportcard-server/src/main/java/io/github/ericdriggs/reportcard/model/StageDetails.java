package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

//TODO: rename to StageDetails
@Data
public class StageDetails {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String org;
    private String repo;
    private String branch;
    private String sha;
    private Map<String,String> jobInfo;
    private String runReference;
    private String stage;
    private Map<String,String> externalLinks;

    public void validateAndSetDefaults() {
        Map<String,String> errors = new LinkedHashMap<>();
        addErrorIfMissing(errors, org, "org");
        addErrorIfMissing(errors, repo, "repo");
        addErrorIfMissing(errors, branch, "branch");
        addErrorIfMissing(errors, sha, "sha");
        addErrorIfMissing(errors, runReference, "runReference");
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

    @SneakyThrows(JsonProcessingException.class)
    public String getJobInfoJson() {
        return mapper.writeValueAsString(jobInfo);
    }
}
