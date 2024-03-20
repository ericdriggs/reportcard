//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StageTestResult {
    StagePojo stage;
    TestResult testResult;
}
