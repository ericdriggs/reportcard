package io.github.ericdriggs.reportcard.model.metrics.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeSet;

@SuppressWarnings("SameParameterValue")
@Builder
@Jacksonized
@Value
public class MetricsIntervalRequest {
    @Builder.Default
    TreeSet<InstantRange> ranges = ranges(30, 2);
    @Builder.Default
    MetricsFilter excluded = MetricsFilter.builder().build();
    @Builder.Default
    MetricsFilter required = MetricsFilter.builder().build();
    @Builder.Default
    boolean shouldIncludeDefaultBranches = true;

    @JsonIgnore
    public TreeSet<MetricsRequest> toCompanyDashboardRequests() {
        TreeSet<MetricsRequest> reqs = new TreeSet<>();
        for (InstantRange range : ranges) {
            reqs.add(MetricsRequest.builder()
                    .range(range)
                    .excluded(excluded)
                    .required(required)
                    .shouldIncludeDefaultBranches(shouldIncludeDefaultBranches)
                    .build());
        }
        return reqs;
    }

    public static TreeSet<InstantRange> ranges(int days, int count) {

        TreeSet<InstantRange> ranges = new TreeSet<>();
        Instant end = Instant.now();
        for (int i = 0; i < count; i++) {
            Instant start = end.minus(days, ChronoUnit.DAYS);
            ranges.add(new InstantRange(start, end));
            end = start;
        }
        return ranges;
    }

    public static MetricsIntervalRequest fromQueryParams(
            TreeSet<String> companies,
            TreeSet<String> orgs,
            TreeSet<String> repos,
            TreeSet<String> branches,
            TreeSet<String> jobInfos,
            TreeSet<String> notCompanies,
            TreeSet<String> notOrgs,
            TreeSet<String> notRepos,
            TreeSet<String> notBranches,
            TreeSet<String> notJobInfos,
            boolean shouldIncludeDefaultBranches,
            Integer intervalDays,
            Integer intervalCount) {
        MetricsFilter required = MetricsFilter
                .builder()
                .companies(companies)
                .orgs(orgs)
                .repos(repos)
                .branches(branches)
                .jobInfos(StringMapUtil.fromColonSeparated(jobInfos))
                .build();

        MetricsFilter excluded = MetricsFilter
                .builder()
                .companies(notCompanies)
                .orgs(notOrgs)
                .repos(notRepos)
                .branches(notBranches)
                .jobInfos(StringMapUtil.fromColonSeparated((notJobInfos)))
                .build();

        TreeSet<InstantRange> ranges = MetricsIntervalRequest.ranges(intervalDays, intervalCount);

        return MetricsIntervalRequest
                .builder()
                .required(required)
                .excluded(excluded)
                .ranges(ranges)
                .shouldIncludeDefaultBranches(shouldIncludeDefaultBranches)
                .build();
    }

}