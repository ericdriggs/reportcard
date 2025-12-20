package io.github.ericdriggs.reportcard.model.pipeline;

import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import java.util.TreeSet;

@Builder
@Jacksonized
@Value
public class PipelineIntervalRequest {
    TreeSet<String> companies;
    TreeSet<String> orgs;
    TreeSet<String> repos;
    TreeSet<String> branches;
    TreeSet<String> jobInfos;
    TreeSet<String> notCompanies;
    TreeSet<String> notOrgs;
    TreeSet<String> notRepos;
    TreeSet<String> notBranches;
    TreeSet<String> notJobInfos;
    boolean shouldIncludeDefaultBranches;
    Integer intervalDays;
    Integer intervalCount;

    public static PipelineIntervalRequest fromQueryParams(
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
            Integer intervalCount
    ) {
        return PipelineIntervalRequest.builder()
                .companies(companies)
                .orgs(orgs)
                .repos(repos)
                .branches(branches)
                .jobInfos(jobInfos)
                .notCompanies(notCompanies)
                .notOrgs(notOrgs)
                .notRepos(notRepos)
                .notBranches(notBranches)
                .notJobInfos(notJobInfos)
                .shouldIncludeDefaultBranches(shouldIncludeDefaultBranches)
                .intervalDays(intervalDays)
                .intervalCount(intervalCount)
                .build();
    }

    public TreeSet<PipelineRequest> toPipelineRequests() {
        TreeSet<PipelineRequest> requests = new TreeSet<>();
        
        Instant now = Instant.now();
        for (int i = 0; i < intervalCount; i++) {
            Instant end = now.minus(i * intervalDays, ChronoUnit.DAYS);
            Instant start = end.minus(intervalDays, ChronoUnit.DAYS);
            
            InstantRange range = InstantRange.builder()
                    .start(start)
                    .end(end)
                    .build();
                    
            requests.add(PipelineRequest.builder()
                    .companies(companies)
                    .orgs(orgs)
                    .repos(repos)
                    .branches(branches)
                    .jobInfos(jobInfos)
                    .notCompanies(notCompanies)
                    .notOrgs(notOrgs)
                    .notRepos(notRepos)
                    .notBranches(notBranches)
                    .notJobInfos(notJobInfos)
                    .shouldIncludeDefaultBranches(shouldIncludeDefaultBranches)
                    .range(range)
                    .build());
        }
        
        return requests;
    }
}