package io.github.ericdriggs.reportcard.model.trend;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class TestCaseName {
    String testCaseName;
}