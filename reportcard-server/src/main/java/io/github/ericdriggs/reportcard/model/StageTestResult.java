package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StageTestResult {
    Stage stage;
    TestResult testResult;
}
