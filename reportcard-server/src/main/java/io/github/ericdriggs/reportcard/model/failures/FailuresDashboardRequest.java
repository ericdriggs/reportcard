package io.github.ericdriggs.reportcard.model.failures;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Value
public class FailuresDashboardRequest {
    String company;
    String org;
    String jobInfoKey;
    List<String> jobInfoValues;
    @Builder.Default
    int days = 7;
    @Builder.Default
    int minFailurePercent = 50;
    List<String> repos;
    Integer limit;
}
