package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.util.db.SqlJsonUtil;
import lombok.*;

import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static io.github.ericdriggs.reportcard.util.StringMapUtil.lower;

//TODO: refactor to Value
@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
@AllArgsConstructor
public class StageDetails {

    @JsonIgnore
    final static ObjectMapper mapper = new ObjectMapper();

    String company;
    String org;
    String repo;
    String branch;
    String sha;
    TreeMap<String, String> jobInfo;
    UUID runReference;
    String stage;
    Map<String, String> externalLinks;

    public static class StageDetailsBuilder {
        String company;
        String org;
        String repo;
        String branch;
        String sha;
        TreeMap<String, String> jobInfo;
        UUID runReference;
        String stage;
        Map<String, String> externalLinks;

        public StageDetails build() {
            validateAndSetDefaults();
            return new StageDetails(company, org, repo, branch, sha, jobInfo, runReference, stage, externalLinks);
        }

        public void validateAndSetDefaults() {
            if (runReference == null) {
                runReference = UUID.randomUUID();
            }
            Map<String, String> errors = new LinkedHashMap<>();
            addErrorIfMissing(errors, company, "company");
            addErrorIfMissing(errors, org, "org");
            addErrorIfMissing(errors, repo, "repo");
            addErrorIfMissing(errors, branch, "branch");
            addErrorIfMissing(errors, sha, "sha");
            addErrorIfMissing(errors, stage, "stage");
            jobInfo = lower(jobInfo);
            branch = branch.replace("/", "_");
            if (!errors.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "errors - " + Arrays.toString(errors.entrySet().toArray()));
            }
        }

        @JsonIgnore
        private void addErrorIfMissing(Map<String, String> errors, String val, String variableName) {
            if (StringUtils.isEmpty(val)) {
                errors.put(variableName, "missing required field");
            }
        }
    }

    @JsonIgnore
    public String getExternalLinksJson() {
        if (externalLinks == null) {
            return null;
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(externalLinks);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public String getJobInfoSqlClause() {
        return SqlJsonUtil.jobInfoEqualsJson(getJobInfoJson());
    }

    @JsonIgnore
    @SneakyThrows(JsonProcessingException.class)
    public String getJobInfoJson() {
        return mapper.writeValueAsString(jobInfo);
    }

    @JsonIgnore
    public String getRunReferenceString() {
        if (runReference == null) {
            return null;
        }
        return runReference.toString();
    }

}
