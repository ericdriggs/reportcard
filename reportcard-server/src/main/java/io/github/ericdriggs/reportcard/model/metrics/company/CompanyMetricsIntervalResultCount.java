package io.github.ericdriggs.reportcard.model.metrics.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

@Builder
@Jacksonized
@Value
@Slf4j
public class CompanyMetricsIntervalResultCount implements Comparable<CompanyMetricsIntervalResultCount> {
    InstantRange range;
    TreeMap<CompanyOrgDTO, RunResultCount> orgResultCounts;
    TreeMap<CompanyOrgRepoDTO, RunResultCount> repoResultCounts;
    TreeMap<CompanyOrgRepoBranchDTO, RunResultCount> branchResultCounts;
    TreeMap<CompanyOrgRepoBranchJobDTO, RunResultCount> jobResultCounts;
    @Override
    public int compareTo(@NonNull CompanyMetricsIntervalResultCount that) {
        return CompareUtil.chainCompare(
                ObjectUtils.compare(range, that.range),
                CompareUtil.compareComparableMap(orgResultCounts, that.orgResultCounts),
                CompareUtil.compareComparableMap(repoResultCounts, that.repoResultCounts),
                CompareUtil.compareComparableMap(branchResultCounts, that.branchResultCounts),
                CompareUtil.compareComparableMap(jobResultCounts, that.jobResultCounts)
        );
    }

    @JsonIgnore
    public static CompanyMetricsIntervalResultCount fromCompanyGraphs(List<CompanyGraph> companyGraphs, TreeMap<String, TreeSet<String>> excluded, TreeMap<String, TreeSet<String>> required, InstantRange range) {

        TreeMap<CompanyOrgRepoBranchJobDTO, RunResultCount> jobResultCounts = new TreeMap<>();
        TreeMap<CompanyOrgRepoBranchDTO, RunResultCount> branchResultCounts = new TreeMap<>();
        TreeMap<CompanyOrgRepoDTO, RunResultCount> repoResultCounts = new TreeMap<>();
        TreeMap<CompanyOrgDTO, RunResultCount> orgResultCounts = new TreeMap<>();

        for (CompanyGraph companyGraph : emptyIfNull(companyGraphs)) {
            final List<OrgGraph> orgGraphs = companyGraph.orgs();

            for (OrgGraph orgGraph : emptyIfNull(orgGraphs)) {
                final CompanyOrgDTO companyOrgDTO = CompanyOrgDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).build();
                RunResultCount orgResultCount = RunResultCount.builder().build();
                final List<RepoGraph> repoGraphs = orgGraph.repos();

                for (RepoGraph repoGraph : emptyIfNull(repoGraphs)) {
                    final CompanyOrgRepoDTO companyOrgRepoDTO = CompanyOrgRepoDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).repo(repoGraph.repoName()).build();
                    RunResultCount repoResultCount = RunResultCount.builder().build();
                    final List<BranchGraph> branchGraphs = repoGraph.branches();

                    for (BranchGraph branchGraph : emptyIfNull(branchGraphs)) {
                        final CompanyOrgRepoBranchDTO companyOrgRepoBranchDTO = CompanyOrgRepoBranchDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).repo(repoGraph.repoName()).branch(branchGraph.branchName()).build();
                        RunResultCount branchResultCount = RunResultCount.builder().build();
                        final List<JobGraph> jobGraphs = branchGraph.jobs();

                        for (JobGraph jobGraph : emptyIfNull(jobGraphs)) {
                            if (!matchExcludedRequired(jobGraph, excluded, required)) {
                                continue;
                            }
                            final CompanyOrgRepoBranchJobDTO companyOrgRepoBranchJobDTO = CompanyOrgRepoBranchJobDTO.builder().company(companyGraph.companyName()).org(orgGraph.orgName()).repo(repoGraph.repoName()).branch(branchGraph.branchName()).jobId(jobGraph.jobId()).build();
                            RunResultCount jobResultCount = RunResultCount.builder().build();
                            final List<RunGraph> runGraphs = jobGraph.runs();

                            for (RunGraph runGraph : emptyIfNull(runGraphs)) {
                                final List<StageGraph> stageGraphs = runGraph.stages();

                                for (StageGraph stageGraph : emptyIfNull(stageGraphs)) {
                                    final List<TestResultGraph> testResultGraphs = stageGraph.testResults();

                                    for (TestResultGraph testResultGraph : emptyIfNull(testResultGraphs)) {
                                        orgResultCount.add(testResultGraph);
                                        repoResultCount.add(testResultGraph);
                                        branchResultCount.add(testResultGraph);
                                        jobResultCount.add(testResultGraph);
                                    }
                                }
                            }
                            jobResultCounts.put(companyOrgRepoBranchJobDTO, jobResultCount);
                        }
                        branchResultCounts.put(companyOrgRepoBranchDTO, branchResultCount);
                    }
                    repoResultCounts.put(companyOrgRepoDTO, repoResultCount);
                }
                orgResultCounts.put(companyOrgDTO, orgResultCount);
            }
        }
        return CompanyMetricsIntervalResultCount.builder()
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
                if (jobInfo.get(entry.getKey()).equalsIgnoreCase(value)) {
                    log.trace("excluded key: {}, value: {} was found in jobInfo: {}", entry.getKey(), entry.getValue(), jobInfo);
                    return false;
                }
            }
        }

        for (Map.Entry<String, TreeSet<String>> entry : required.entrySet()) {
            boolean foundRequired = false;
            for (String value : entry.getValue()) {
                if (jobInfo.get(entry.getKey()).equalsIgnoreCase(value)) {
                    foundRequired = true;
                }
            }
            if (!foundRequired) {
                log.trace("expected required - key: {}, value: {}, not found in jobInfo: {}", entry.getKey(), entry.getValue(), jobInfo);
                return false;
            }
        }
        return true;
    }
}