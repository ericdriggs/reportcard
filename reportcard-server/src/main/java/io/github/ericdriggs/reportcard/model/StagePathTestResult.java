package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collections;
import java.util.Map;

@Builder
@Jacksonized
@Value
public class StagePathTestResult {
    StagePath stagePath;
    TestResultModel testResult;

    @JsonIgnore
    public Map<String,String> getUrls() {
        if (stagePath == null) {
            return Collections.emptyMap();
        }
        return stagePath.getUrlMaps();
    }
}
