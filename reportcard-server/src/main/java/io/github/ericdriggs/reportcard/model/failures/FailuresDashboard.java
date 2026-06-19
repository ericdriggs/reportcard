package io.github.ericdriggs.reportcard.model.failures;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Jacksonized
@Value
public class FailuresDashboard {
    FailuresDashboardRequest request;
    List<TestCaseFailureSummary> failingTests;
    List<DailyTestAggregation> dailyAggregations;
    Map<String, List<DailyTestAggregation>> dailyAggregationsByOrg;
    Instant generated;
}
