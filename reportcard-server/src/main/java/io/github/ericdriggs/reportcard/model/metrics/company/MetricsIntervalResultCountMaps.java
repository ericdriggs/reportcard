package io.github.ericdriggs.reportcard.model.metrics.company;

import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Builder
@Jacksonized
@Value
public class MetricsIntervalResultCountMaps {

    TreeMap<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgResultCounts;
    TreeMap<CompanyOrgRepoDTO, TreeMap<InstantRange, RunResultCount>> repoResultCounts;
    TreeMap<CompanyOrgRepoBranchDTO, TreeMap<InstantRange, RunResultCount>> branchResultCounts;
    TreeMap<CompanyOrgRepoBranchJobInfoDTO, TreeMap<InstantRange, RunResultCount>> jobResultCounts;


    public static MetricsIntervalResultCountMaps fromMetricsIntervalResultCount(Collection<MetricsIntervalResultCount> metricsIntervalResultCounts) {
        TreeMap<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgResultCounts = new TreeMap<>();
        TreeMap<CompanyOrgRepoDTO, TreeMap<InstantRange, RunResultCount>> repoResultCounts = new TreeMap<>();
        TreeMap<CompanyOrgRepoBranchDTO, TreeMap<InstantRange, RunResultCount>> branchResultCounts = new TreeMap<>();
        TreeMap<CompanyOrgRepoBranchJobInfoDTO, TreeMap<InstantRange, RunResultCount>> jobResultCounts = new TreeMap<>();

        for (MetricsIntervalResultCount metricsIntervalResultCount : metricsIntervalResultCounts) {
            final InstantRange range = metricsIntervalResultCount.getRange();

            //org
            for (Map.Entry<CompanyOrgDTO, RunResultCount> entry : metricsIntervalResultCount.getOrgResultCounts().entrySet()) {
                final CompanyOrgDTO companyOrgDTO = entry.getKey();
                final RunResultCount runResultCount = entry.getValue();

                orgResultCounts.computeIfAbsent(companyOrgDTO, k -> new TreeMap<>(InstantRange.DESCENDING));
                orgResultCounts.get(companyOrgDTO).put(range, runResultCount);
            }

            //repo
            for (Map.Entry<CompanyOrgRepoDTO, RunResultCount> entry : metricsIntervalResultCount.getRepoResultCounts().entrySet()) {
                final CompanyOrgRepoDTO companyOrgRepoDTO = entry.getKey();
                final RunResultCount runResultCount = entry.getValue();

                repoResultCounts.computeIfAbsent(companyOrgRepoDTO, k -> new TreeMap<>(InstantRange.DESCENDING));
                repoResultCounts.get(companyOrgRepoDTO).put(range, runResultCount);
            }

            //branch
            for (Map.Entry<CompanyOrgRepoBranchDTO, RunResultCount> entry : metricsIntervalResultCount.getBranchResultCounts().entrySet()) {
                final CompanyOrgRepoBranchDTO companyOrgRepoBranchDTO = entry.getKey();
                final RunResultCount runResultCount = entry.getValue();

                branchResultCounts.computeIfAbsent(companyOrgRepoBranchDTO, k -> new TreeMap<>(InstantRange.DESCENDING));
                branchResultCounts.get(companyOrgRepoBranchDTO).put(range, runResultCount);
            }

            //job
            for (Map.Entry<CompanyOrgRepoBranchJobInfoDTO, RunResultCount> entry : metricsIntervalResultCount.getJobResultCounts().entrySet()) {
                final CompanyOrgRepoBranchJobInfoDTO companyOrgRepoBranchJobInfoDTO = entry.getKey();
                final RunResultCount runResultCount = entry.getValue();

                jobResultCounts.computeIfAbsent(companyOrgRepoBranchJobInfoDTO, k -> new TreeMap<>(InstantRange.DESCENDING));
                jobResultCounts.get(companyOrgRepoBranchJobInfoDTO).put(range, runResultCount);
            }
        }

        return MetricsIntervalResultCountMaps.builder()
                .orgResultCounts(orgResultCounts)
                .repoResultCounts(repoResultCounts)
                .branchResultCounts(branchResultCounts)
                .jobResultCounts(jobResultCounts)
                .build();
    }
}
