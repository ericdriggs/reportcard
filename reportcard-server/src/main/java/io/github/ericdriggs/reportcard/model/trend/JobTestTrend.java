package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.PojoComparators;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.xml.testng.suite.Test;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

@Builder
@Jacksonized
@Value
public class JobTestTrend {

    CompanyOrgRepoBranchJob companyOrgRepoBranchJob;
    StageName stageName;
    TreeMap<TestSuiteNameTestCaseName, TreeMap<RunPojo, TestCaseModel>> testCaseTrends;
    InstantRange range;
    Instant generated;

    public static JobTestTrend fromCompanyGraphs(List<CompanyGraph> companyGraphs) {
        if (companyGraphs == null) {
            throw new NullPointerException("companyGraphs");
        }
        if (companyGraphs.size() != 1) {
            throw new IllegalArgumentException("companyGraphs size must be only 1");
        }
        return fromCompanyGraph(companyGraphs.get(0));
    }

    private static JobTestTrend fromCompanyGraph(CompanyGraph companyGraph) {
        Pair<CompanyOrgRepoBranchJob, JobGraph> graphPair = getCompanyOrgRepoBranchJob(companyGraph);
        CompanyOrgRepoBranchJob companyOrgRepoBranchJob = graphPair.getLeft();
        JobGraph jobGraph = graphPair.getRight();
        Instant now = Instant.now();
        InstantRange instantRange = new InstantRange();
        TreeMap<TestSuiteNameTestCaseName, TreeMap<RunPojo, TestCaseModel>> testCaseTrends = new TreeMap<>();
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
                                            final String testSuiteName = testSuiteGraph.name();
                                            List<TestCaseGraph> testCaseGraphs = testSuiteGraph.testCases();
                                            if (!CollectionUtils.isEmpty(testCaseGraphs)) {
                                                for (TestCaseGraph testCaseGraph : testCaseGraphs) {
                                                    final TestSuiteNameTestCaseName testSuiteNameTestCaseName = new TestSuiteNameTestCaseName(testSuiteName, testCaseGraph.name());
                                                    final TestCaseModel testCaseModel = testCaseGraph.asTestCaseModel();

                                                    testCaseTrends.computeIfAbsent(testSuiteNameTestCaseName, k -> new TreeMap<>(PojoComparators::compareRun));
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

        return JobTestTrend
                .builder()
                .companyOrgRepoBranchJob(companyOrgRepoBranchJob)
                .stageName(stageName)
                .testCaseTrends(testCaseTrends)
                .range(instantRange)
                .generated(now)
                .build();
    }

    private static Pair<CompanyOrgRepoBranchJob, JobGraph> getCompanyOrgRepoBranchJob(CompanyGraph companyGraph) {

        assertSize1(companyGraph.orgs(), "orgs");
        final OrgGraph orgGraph = companyGraph.orgs().get(0);

        assertSize1(orgGraph.repos(), "repos");
        final RepoGraph repoGraph = orgGraph.repos().get(0);

        assertSize1(repoGraph.branches(), "branches");
        final BranchGraph branchGraph = repoGraph.branches().get(0);

        assertSize1(branchGraph.jobs(), "jobs");
        final JobGraph jobGraph = branchGraph.jobs().get(0);

        CompanyOrgRepoBranchJob companyOrgRepoBranchJob = CompanyOrgRepoBranchJob
                .builder()
                .companyPojo(companyGraph.asCompanyPojo())
                .orgPojo(orgGraph.asOrgPojo())
                .repoPojo(repoGraph.asRepoPojo())
                .branchPojo(branchGraph.asBranchPojo())
                .jobPojo(jobGraph.asJobPojo())
                .build();
        return Pair.of(companyOrgRepoBranchJob, jobGraph);
    }

    static void assertSize1(List<?> col, String name) {
        if (CollectionUtils.isEmpty(col)) {
            throw new NullPointerException(name);
        }

        if (col.size() != 1) {
            throw new IllegalArgumentException("expected size 1. actual " + name + ".size(): " + col.size());
        }
    }

}
