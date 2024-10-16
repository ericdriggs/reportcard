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
        CompanyOrgRepoBranchJobStageName companyOrgRepoBranchJobStageNameStageName = graphPair.getLeft();
        JobGraph jobGraph = graphPair.getRight();
        Instant now = Instant.now();
        InstantRange instantRange = InstantRange.builder().build();
        TreeMap<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testCaseTrends = new TreeMap<>();
        StageName stageName = null;

        if (jobGraph != null) {
            List<RunGraph> runGraphs = jobGraph.runs();
            if (!CollectionUtils.isEmpty(runGraphs)) {
                for (RunGraph runGraph : runGraphs) {
                    final RunPojo runPojo = runGraph.asRunPojo();
                    instantRange.updateRange(runPojo.getRunDate());
                    List<StageGraph> stageGraphs = runGraph.stages();
                    if (!CollectionUtils.isEmpty(stageGraphs)) {
                        for (StageGraph stageGraph : stageGraphs) {
                            final String stageGraphName = stageGraph.stageName();
                            if (stageName == null) {
                                stageName = new StageName(stageGraphName);
                            } else {
                                if (!stageName.getStageName().equals(stageGraphName)) {
                                    throw new IllegalArgumentException("can only run trend analysis on same stage. cannot compare stage: " + stageName.getStageName() + " to stage: " + stageGraphName);
                                }
                            }
                            List<TestResultGraph> testResultGraphs = stageGraph.testResults();
                            if (!CollectionUtils.isEmpty(testResultGraphs)) {
                                for (TestResultGraph testResultGraph : testResultGraphs) {
                                    List<TestSuiteGraph> testSuiteGraphs = testResultGraph.testSuites();
                                    if (!CollectionUtils.isEmpty(testSuiteGraphs)) {
                                        for (TestSuiteGraph testSuiteGraph : testSuiteGraphs) {
                                            final String testPackage = testSuiteGraph.packageName();
                                            final String testSuiteName = testSuiteGraph.name();
                                            List<TestCaseGraph> testCaseGraphs = testSuiteGraph.testCases();
                                            if (!CollectionUtils.isEmpty(testCaseGraphs)) {
                                                for (TestCaseGraph testCaseGraph : testCaseGraphs) {
                                                    final TestPackageSuiteCase testSuiteNameTestCaseName = new TestPackageSuiteCase(testPackage, testSuiteName, testCaseGraph.name());
                                                    final TestCaseModel testCaseModel = testCaseGraph.asTestCaseModel();

                                                    testCaseTrends.computeIfAbsent(testSuiteNameTestCaseName, k -> new TreeMap<>(PojoComparators::compareRunDescending));
                                                    TreeMap<RunPojo, TestCaseModel> runTrend = testCaseTrends.get(testSuiteNameTestCaseName);
                                                    runTrend.put(runPojo, testCaseModel);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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

        assertSize1(companyGraph.orgs(), "orgs");
        final OrgGraph orgGraph = companyGraph.orgs().get(0);

        assertSize1(orgGraph.repos(), "repos");
        final RepoGraph repoGraph = orgGraph.repos().get(0);

        assertSize1(repoGraph.branches(), "branches");
        final BranchGraph branchGraph = repoGraph.branches().get(0);

        assertSize1(branchGraph.jobs(), "jobs");
        final JobGraph jobGraph = branchGraph.jobs().get(0);

        String stageName = null;
        if (!CollectionUtils.isEmpty(jobGraph.runs())) {
            for (RunGraph run : jobGraph.runs()) {
                for (StageGraph stage : run.stages()) {
                    if (stage.stageName() != null) {
                        if (stageName == null) {
                            stageName = stage.stageName();
                        }

                        if (!stageName.equals(stage.stageName())) {
                            throw new IllegalStateException("different stage detected. stageName: " + stageName + " != stage.getStageMame: " + stage.stageName());
                        }
                    }
                }
            }
        }


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


}
