package io.github.ericdriggs.reportcard.model.metrics.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.TreeSet;

@Builder
@Jacksonized
@Value
public class CompanyMetricsIntervalRequest {
    @NonNull
    String companyName;
    @NonNull
    TreeSet<InstantRange> ranges;
    @Builder.Default
    CompanyMetricsFilter exclude = CompanyMetricsFilter.builder().build();
    @Builder.Default
    CompanyMetricsFilter required  = CompanyMetricsFilter.builder().build();
    @Builder.Default
    boolean shouldIncludeDefaultBranches = true;

    @JsonIgnore
    public TreeSet<CompanyMetricsRequest> toCompanyDashboardRequests() {
        TreeSet<CompanyMetricsRequest> reqs = new TreeSet<>();
        for (InstantRange range : ranges) {
            reqs.add(CompanyMetricsRequest.builder()
                    .companyName(companyName)
                    .range(range)
                    .exclude(exclude)
                    .required(required)
                    .shouldIncludeDefaultBranches(shouldIncludeDefaultBranches)
                    .build());
        }
        return reqs;
    }
}