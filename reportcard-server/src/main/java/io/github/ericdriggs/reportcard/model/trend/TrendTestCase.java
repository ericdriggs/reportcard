package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class TrendTestCase {

    TestSuiteName testSuiteName;
    TestCaseModel testCaseModel;
}
