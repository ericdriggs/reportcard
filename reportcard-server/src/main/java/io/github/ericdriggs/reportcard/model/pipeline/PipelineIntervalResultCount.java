package io.github.ericdriggs.reportcard.model.pipeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

@Builder
@Jacksonized
@Value
public class PipelineIntervalResultCount implements Comparable<PipelineIntervalResultCount> {
    InstantRange range;
    TreeMap<CompanyOrgDTO, PipelineResultCount> orgResultCounts;
    TreeMap<CompanyOrgRepoDTO, PipelineResultCount> repoResultCounts;
    TreeMap<CompanyOrgRepoBranchDTO, PipelineResultCount> branchResultCounts;
    TreeMap<CompanyOrgRepoBranchJobInfoDTO, PipelineResultCount> jobResultCounts;

    @Override
    public int compareTo(@NonNull PipelineIntervalResultCount that) {
        return CompareUtil.chainCompare(
                range.compareTo(that.range),
                CompareUtil.compareComparableMap(orgResultCounts, that.orgResultCounts),
                CompareUtil.compareComparableMap(repoResultCounts, that.repoResultCounts),
                CompareUtil.compareComparableMap(branchResultCounts, that.branchResultCounts),
                CompareUtil.compareComparableMap(jobResultCounts, that.jobResultCounts)
        );
    }

    @JsonIgnore
    public static PipelineIntervalResultCount fromCompanyGraphs(List<CompanyGraph> companyGraphs, TreeMap<String, TreeSet<String>> excluded, TreeMap<String, TreeSet<String>> required, InstantRange range) {

        TreeMap<CompanyOrgRepoBranchJobInfoDTO, PipelineResultCount.Accumulator> jobAccumulators = new TreeMap<>();
        TreeMap<CompanyOrgRepoBranchDTO, PipelineResultCount.Accumulator> branchAccumulators = new TreeMap<>();
        TreeMap<CompanyOrgRepoDTO, PipelineResultCount.Accumulator> repoAccumulators = new TreeMap<>();
        TreeMap<CompanyOrgDTO, PipelineResultCount.Accumulator> orgAccumulators = new TreeMap<>();

        for (CompanyGraph companyGraph : emptyIfNull(companyGraphs)) {
            for (OrgGraph orgGraph : emptyIfNull(companyGraph.orgs())) {
                final CompanyOrgDTO companyOrgDTO = CompanyOrgDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).build();
                PipelineResultCount.Accumulator orgAcc = orgAccumulators.computeIfAbsent(companyOrgDTO, k -> new PipelineResultCount.Accumulator());

                for (RepoGraph repoGraph : emptyIfNull(orgGraph.repos())) {
                    final CompanyOrgRepoDTO companyOrgRepoDTO = CompanyOrgRepoDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).repo(repoGraph.repoName()).build();
                    PipelineResultCount.Accumulator repoAcc = repoAccumulators.computeIfAbsent(companyOrgRepoDTO, k -> new PipelineResultCount.Accumulator());

                    for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                        final CompanyOrgRepoBranchDTO companyOrgRepoBranchDTO = CompanyOrgRepoBranchDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).repo(repoGraph.repoName()).branch(branchGraph.branchName()).build();
                        PipelineResultCount.Accumulator branchAcc = branchAccumulators.computeIfAbsent(companyOrgRepoBranchDTO, k -> new PipelineResultCount.Accumulator());

                        for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                            if (!matchExcludedRequired(jobGraph, excluded, required)) {
                                continue;
                            }
                            final CompanyOrgRepoBranchJobInfoDTO companyOrgRepoBranchJobInfoDTO = CompanyOrgRepoBranchJobInfoDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).repo(repoGraph.repoName()).branch(branchGraph.branchName()).jobInfo(jobGraph.jobInfo()).build();
                            PipelineResultCount.Accumulator jobAcc = jobAccumulators.computeIfAbsent(companyOrgRepoBranchJobInfoDTO, k -> new PipelineResultCount.Accumulator());

                            for (RunGraph runGraph : emptyIfNull(jobGraph.runs())) {
                                for (StageGraph stageGraph : emptyIfNull(runGraph.stages())) {
                                    for (TestResultGraph testResultGraph : emptyIfNull(stageGraph.testResults())) {
                                        orgAcc.add(testResultGraph, runGraph);
                                        repoAcc.add(testResultGraph, runGraph);
                                        branchAcc.add(testResultGraph, runGraph);
                                        jobAcc.add(testResultGraph, runGraph);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Convert accumulators to immutable results
        TreeMap<CompanyOrgRepoBranchJobInfoDTO, PipelineResultCount> jobResultCounts = new TreeMap<>();
        jobAccumulators.forEach((k, v) -> jobResultCounts.put(k, v.build()));
        
        TreeMap<CompanyOrgRepoBranchDTO, PipelineResultCount> branchResultCounts = new TreeMap<>();
        branchAccumulators.forEach((k, v) -> branchResultCounts.put(k, v.build()));
        
        TreeMap<CompanyOrgRepoDTO, PipelineResultCount> repoResultCounts = new TreeMap<>();
        repoAccumulators.forEach((k, v) -> repoResultCounts.put(k, v.build()));
        
        TreeMap<CompanyOrgDTO, PipelineResultCount> orgResultCounts = new TreeMap<>();
        orgAccumulators.forEach((k, v) -> orgResultCounts.put(k, v.build()));
        return PipelineIntervalResultCount.builder()
                .orgResultCounts(orgResultCounts)
                .repoResultCounts(repoResultCounts)
                .branchResultCounts(branchResultCounts)
                .jobResultCounts(jobResultCounts)
                .range(range)
                .build();
    }

    @JsonIgnore
    public static boolean matchExcludedRequired(JobGraph jobGraph, TreeMap<String, TreeSet<String>> excluded, TreeMap<String, TreeSet<String>> required) {
        TreeMap<String, String> jobInfo = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        jobInfo.putAll(jobGraph.jobInfo());
        
        for (Map.Entry<String, TreeSet<String>> entry : excluded.entrySet()) {
            for (String value : entry.getValue()) {
                if (value.trim().equals(jobInfo.get(entry.getKey()))) {
                    return false;
                }
            }
        }

        for (Map.Entry<String, TreeSet<String>> entry : required.entrySet()) {
            boolean foundRequired = false;
            for (String value : entry.getValue()) {
                if (value.trim().equalsIgnoreCase(jobInfo.get(entry.getKey()))) {
                    foundRequired = true;
                }
            }
            if (!foundRequired) {
                return false;
            }
        }
        return true;
    }
}