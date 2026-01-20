package io.github.ericdriggs.reportcard.model.pipeline;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Builder
@Jacksonized
@Value
public class JobDashboardRequest {
    String company;
    String org;
    @Singular
    Map<String, String> jobInfos; // e.g., {"pipeline": "build_acceptance", "application": "commons-utils"}
    @Builder.Default
    Integer days = 90;
    @Builder.Default
    Instant endDate = Instant.now();
    
    public Instant getStartDate() {
        return endDate.minus(days, ChronoUnit.DAYS);
    }
}