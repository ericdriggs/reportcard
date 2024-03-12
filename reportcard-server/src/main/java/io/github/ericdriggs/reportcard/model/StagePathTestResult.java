package io.github.ericdriggs.reportcard.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StagePathTestResult {
    StagePath stagePath;
    TestResult testResult;
}
