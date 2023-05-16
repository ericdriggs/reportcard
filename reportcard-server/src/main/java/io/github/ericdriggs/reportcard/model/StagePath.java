package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public class StagePath {
    private Org org;
    private Repo repo;
    private Branch branch;
    private Job job;
    private Run run;
    private Stage stage;

    public boolean isEmpty() {
        return org == null;
    }

    public boolean isComplete() {
        return validate().isEmpty();
    }

    public Map<String, String> validate() {

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

        if (ObjectUtils.isEmpty(job)) {
            validationErrors.put("job", "missing required field");
        }

        if (ObjectUtils.isEmpty(run)) {
            validationErrors.put("run", "missing required field");
        }

        if (ObjectUtils.isEmpty(stage)) {
            validationErrors.put("stage", "missing required field");
        }

        return validationErrors;
    }
}