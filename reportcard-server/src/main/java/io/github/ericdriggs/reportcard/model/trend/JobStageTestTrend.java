package io.github.ericdriggs.reportcard.model.trend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.PojoComparators;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.graph.*;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.assertSize1;

@Builder
@Jacksonized
@Value
public class JobStageTestTrend {

    CompanyOrgRepoBranchJobStageName companyOrgRepoBranchJobStageName;

    TreeMap<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testCaseTrends;
    InstantRange range;
    Instant generated;
    Integer maxRuns;

    public static JobStageTestTrend fromCompanyGraphs(List<CompanyGraph> companyGraphs, int maxRuns) {
        if (companyGraphs == null) {
            throw new NullPointerException("companyGraphs");
        }
        if (companyGraphs.size() != 1) {
            throw new IllegalArgumentException("companyGraphs size must be only 1");
        }
        return fromCompanyGraph(companyGraphs.get(0), maxRuns);
    }

    private static JobStageTestTrend fromCompanyGraph(CompanyGraph companyGraph, int maxRuns) {
        Pair<CompanyOrgRepoBranchJobStageName, JobGraph> graphPair = getCompanyOrgRepoBranchJob(companyGraph);
        if (graphPair == null) {
            return null;
        }
        CompanyOrgRepoBranchJobStageName companyOrgRepoBranchJobStageNameStageName = graphPair.getLeft();
        JobGraph jobGraph = graphPair.getRight();
        Instant now = Instant.now();
        InstantRange instantRange = InstantRange.builder().build();
        TreeMap<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testCaseTrends = new TreeMap<>();
        StageName stageName = null;

        if (jobGraph == null || CollectionUtils.isEmpty(jobGraph.runs())) {
            return JobStageTestTrend.builder()
                    .companyOrgRepoBranchJobStageName(companyOrgRepoBranchJobStageNameStageName)
                    .testCaseTrends(testCaseTrends)
                    .range(instantRange)
                    .generated(now)
                    .maxRuns(maxRuns)
                    .build();
        }

        for (RunGraph runGraph : jobGraph.runs()) {
            final RunPojo runPojo = runGraph.asRunPojo();
            instantRange.updateRange(runPojo.getRunDate());

            if (CollectionUtils.isEmpty(runGraph.stages())) {
                continue;
            }

            for (StageGraph stageGraph : runGraph.stages()) {
                final String stageGraphName = stageGraph.stageName();
                if (stageName == null) {
                    stageName = new StageName(stageGraphName);
                } else if (!stageName.getStageName().equals(stageGraphName)) {
                    throw new IllegalArgumentException("can only run trend analysis on same stage. cannot compare stage: " + stageName.getStageName() + " to stage: " + stageGraphName);
                }

                if (CollectionUtils.isEmpty(stageGraph.testResults())) {
                    continue;
                }

                for (TestResultGraph testResultGraph : stageGraph.testResults()) {
                    if (CollectionUtils.isEmpty(testResultGraph.testSuites())) {
                        continue;
                    }

                    for (TestSuiteGraph testSuiteGraph : testResultGraph.testSuites()) {
                        final String testPackage = testSuiteGraph.packageName();
                        final String testSuiteName = testSuiteGraph.name();

                        if (CollectionUtils.isEmpty(testSuiteGraph.testCases())) {
                            continue;
                        }

                        for (TestCaseGraph testCaseGraph : testSuiteGraph.testCases()) {
                            final TestPackageSuiteCase testSuiteNameTestCaseName = new TestPackageSuiteCase(testPackage, testSuiteName, testCaseGraph.name());
                            final TestCaseModel testCaseModel = testCaseGraph.asTestCaseModel();

                            testCaseTrends.computeIfAbsent(testSuiteNameTestCaseName, k -> new TreeMap<>(PojoComparators::compareRunDescending));
                            testCaseTrends.get(testSuiteNameTestCaseName).put(runPojo, testCaseModel);
                        }
                    }
                }
            }
        }

        return JobStageTestTrend
                .builder()
                .companyOrgRepoBranchJobStageName(companyOrgRepoBranchJobStageNameStageName)
                .testCaseTrends(testCaseTrends)
                .range(instantRange)
                .generated(now)
                .maxRuns(maxRuns)
                .build();
    }

    @JsonIgnore
    public CompanyOrgRepoBranchJobRunStageDTO toCompanyOrgRepoBranchJobRunStageDTO() {
        return CompanyOrgRepoBranchJobRunStageDTO
                .builder()
                .company(companyOrgRepoBranchJobStageName.getCompanyPojo().getCompanyName())
                .org(companyOrgRepoBranchJobStageName.getOrgPojo().getOrgName())
                .repo(companyOrgRepoBranchJobStageName.getRepoPojo().getRepoName())
                .branch(companyOrgRepoBranchJobStageName.getBranchPojo().getBranchName())
                .jobId(companyOrgRepoBranchJobStageName.getJobPojo().getJobId())
                .stageName(companyOrgRepoBranchJobStageName.getStageName())
                .build();
    }

    private static Pair<CompanyOrgRepoBranchJobStageName, JobGraph> getCompanyOrgRepoBranchJob(CompanyGraph companyGraph) {
        final OrgGraph orgGraph = getSingleOrNull(companyGraph.orgs(), "orgs");
        if (orgGraph == null) {
            return null;
        }

        final RepoGraph repoGraph = getSingleOrNull(orgGraph.repos(), "repos");
        if (repoGraph == null) {
            return null;
        }

        final BranchGraph branchGraph = getSingleOrNull(repoGraph.branches(), "branches");
        if (branchGraph == null) {
            return null;
        }

        final JobGraph jobGraph = getSingleOrNull(branchGraph.jobs(), "jobs");
        if (jobGraph == null) {
            return null;
        }

        String stageName = findStageName(jobGraph);

        CompanyOrgRepoBranchJobStageName companyOrgRepoBranchJobStageName = CompanyOrgRepoBranchJobStageName
                .builder()
                .companyPojo(companyGraph.asCompanyPojo())
                .orgPojo(orgGraph.asOrgPojo())
                .repoPojo(repoGraph.asRepoPojo())
                .branchPojo(branchGraph.asBranchPojo())
                .jobPojo(jobGraph.asJobPojo())
                .stageName(stageName)
                .build();
        return Pair.of(companyOrgRepoBranchJobStageName, jobGraph);
    }

    /**
     * Returns the single element from a list, or null if empty.
     * Throws if list has more than one element.
     */
    private static <T> T getSingleOrNull(List<T> list, String name) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        assertSize1(list, name);
        return list.get(0);
    }

    /**
     * Finds the stage name from job runs, validating all stages have the same name.
     */
    private static String findStageName(JobGraph jobGraph) {
        if (CollectionUtils.isEmpty(jobGraph.runs())) {
            return null;
        }

        String stageName = null;
        for (RunGraph run : jobGraph.runs()) {
            for (StageGraph stage : run.stages()) {
                if (stage.stageName() == null) {
                    continue;
                }
                if (stageName == null) {
                    stageName = stage.stageName();
                } else if (!stageName.equals(stage.stageName())) {
                    throw new IllegalStateException("different stage detected. stageName: " + stageName + " != stage.getStageMame: " + stage.stageName());
                }
            }
        }
        return stageName;
    }


}
