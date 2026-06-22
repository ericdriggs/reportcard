package io.github.ericdriggs.reportcard.model.failures;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Jacksonized
@Value
public class TestCaseFailureSummary {
    String packageName;
    String suiteName;
    String caseName;
    BigDecimal successPercent;
    Instant lastPassedAt;
    int totalRuns;
    int successCount;
    int failureCount;
    String repo;
    String branch;
    String jobInfo;
}
