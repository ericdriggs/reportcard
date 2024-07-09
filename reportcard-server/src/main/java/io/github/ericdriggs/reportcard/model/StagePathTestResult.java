package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collections;
import java.util.List;

@Builder
@Jacksonized
@Value
public class StagePathTestResult {
    StagePath stagePath;
    TestResultModel testResult;

    @JsonIgnore
    public List<String> getUrls() {
        if (stagePath == null) {
            Collections.emptyList();
        }
        return List.of(stagePath.getUrl());
    }
}
