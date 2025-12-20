package io.github.ericdriggs.reportcard.model.pipeline;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
@Jacksonized
@Value
public class PipelineDashboardRequest {
    String company;
    String org;
    String jobInfo; // e.g., "pipeline:build_acceptance" or "application:commons-utils"
    @Builder.Default
    Integer days = 90;
    @Builder.Default
    Instant endDate = Instant.now();
    
    public Instant getStartDate() {
        return endDate.minus(days, ChronoUnit.DAYS);
    }
}