package io.github.ericdriggs.reportcard.persist.browse;

import io.github.ericdriggs.reportcard.cache.EmptyUtil;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseServiceTest extends AbstractBrowseServiceTest {
    @Autowired
    public BrowseServiceTest(BrowseService browseService) {
        super(browseService);
    }

    @Test
    void getCompanyOrgsSuccessTest() {
        Map<CompanyPojo, Set<OrgPojo>> companyOrgs = browseService.getCompanyOrgs();
        assertNotNull(companyOrgs);
        assertFalse(companyOrgs.isEmpty());

        boolean companyWasFound = false;
        for (Map.Entry<CompanyPojo, Set<OrgPojo>> entry : companyOrgs.entrySet()) {
            final CompanyPojo company = entry.getKey();
            final Set<OrgPojo> orgs = entry.getValue();
            assertNotNull(orgs);
            assertFalse(orgs.isEmpty());
            if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
                Set<String> orgNames = orgs.stream().map(OrgPojo::getOrgName).collect(Collectors.toSet());
                assertTrue(orgNames.contains(TestData.org));
                companyWasFound = true;
            }
        }
        assertTrue(companyWasFound);

    }

    @Test
    void getOrgSuccessTest() {
        OrgPojo org = browseService.getOrg(TestData.company, TestData.org);
        validateTestOrg(org);
    }

    @Test
    void getOrgNotFoundTest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getOrg(TestData.company, "MISSING_ORG");
        });
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains(TestData.company));
        assertTrue(ex.getMessage().contains("MISSING_ORG"));
        assertEquals(404, ex.getStatus().value());

    }

    @Test
    void getOrgReposSuccessTest() {
        final Map<OrgPojo, Set<RepoPojo>> orgRepos = browseService.getOrgsRepos(TestData.company);
        final OrgPojo org = getTestOrg(orgRepos.keySet(), TestData.org);
        validateTestOrg(org);
        final RepoPojo repo = getTestRepo(orgRepos.get(org), TestData.repo);
        validateTestRepo(repo);
    }

    @Test
    void getOrgReposBranchesTest() {
        Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgReposBranches = browseService.getOrgReposBranches(TestData.company, TestData.org);
        final OrgPojo org = getTestOrg(orgReposBranches.keySet(), TestData.org);
        validateTestOrg(org);
        final Map<RepoPojo, Set<BranchPojo>> repoBranches = orgReposBranches.get(org);
        final RepoPojo repo = getTestRepo(repoBranches.keySet(), TestData.repo);
        validateTestRepo(repo);
        final BranchPojo branch = getTestBranch(repoBranches.get(repo), TestData.branch);
        validateTestBranch(branch);
    }

    @Test
    void getRepoSuccessTest() {
        RepoPojo repo = browseService.getRepo(TestData.company, TestData.org, TestData.repo);
        validateTestRepo(repo);
    }

    @Test
    void getRepoNotFoundTest() {
        {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                browseService.getRepo(TestData.company, "MISSING_ORG", "MISSING_REPO");
            });
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains(TestData.company), ex.getMessage());
            assertTrue(ex.getMessage().contains("MISSING_ORG"), ex.getMessage());
            assertTrue(ex.getMessage().contains("MISSING_REPO"), ex.getMessage());
            assertEquals(404, ex.getStatus().value(), ex.getMessage());
        }
        {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                browseService.getRepo(TestData.company, TestData.org, "MISSING_REPO");
            });
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains(TestData.company), ex.getMessage());
            assertTrue(ex.getMessage().contains(TestData.org), ex.getMessage());
            assertTrue(ex.getMessage().contains("MISSING_REPO"), ex.getMessage());
            assertEquals(404, ex.getStatus().value(), ex.getMessage());
        }
    }

    @Test
    void getRepoBranchesJobsTest() {
        Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoBranchesJobs = browseService.getRepoBranchesJobs(TestData.company, TestData.org, TestData.repo);
        final RepoPojo repo = getTestRepo(repoBranchesJobs.keySet(), TestData.repo);
        final Map<BranchPojo, Set<JobPojo>> branchJobs = repoBranchesJobs.get(repo);
        final BranchPojo branch = getTestBranch(branchJobs.keySet(), TestData.branch);
        final JobPojo job = getTestJob(nestedSet(branchJobs.values()), TestData.jobInfo);
        validateTestRepo(repo);
        validateTestBranch(branch);
        validateTestJob(job);
    }

    @Test
    void getBranchTest() {
        BranchPojo branch = browseService.getBranch(TestData.company, TestData.org, TestData.repo, TestData.branch);
        validateTestBranch(branch);
    }

    @Test
    void getBranchJobsRunsTest() {
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchJobRuns = browseService.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo, TestData.branch, null);
        final BranchPojo branch = getTestBranch(branchJobRuns.keySet(), TestData.branch);
        final Map<JobPojo, Set<RunPojo>> jobRuns = branchJobRuns.get(branch);
        final JobPojo job = getTestJob(jobRuns.keySet(), TestData.jobInfo);
        final RunPojo run = getTestRun(nestedSet(jobRuns.values()), TestData.runReference);
        validateTestBranch(branch);
        validateTestJob(job);
        validateTestRun(run);
    }

    @Test
    void getBranchJobsRunsForShaTest() {
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchJobRuns = browseService.getBranchJobsRunsForSha(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.sha);
        final BranchPojo branch = getTestBranch(branchJobRuns.keySet(), TestData.branch);
        final Map<JobPojo, Set<RunPojo>> jobRuns = branchJobRuns.get(branch);
        final JobPojo job = getTestJob(jobRuns.keySet(), TestData.jobInfo);
        final RunPojo run = getTestRun(nestedSet(jobRuns.values()), TestData.runReference);
        validateTestBranch(branch);
        validateTestJob(job);
        validateTestRun(run);
    }

    @Test
    void getJobTest() {
        JobPojo job = browseService.getJob(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobInfo);
        validateTestJob(job);
    }

    @Test
    void getJobRunsStagesTest() {
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> jobRunsStages = browseService.getJobRunsStages(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l);
        final JobPojo job = getTestJob(jobRunsStages.keySet(), TestData.jobInfo);
        final Map<RunPojo, Set<StagePojo>> runStages = jobRunsStages.get(job);
        final RunPojo run = getTestRun(runStages.keySet(), TestData.runReference);
        final StagePojo stage = getTestStage(nestedSet(runStages.values()), TestData.stage);
        validateTestJob(job);
        validateTestRun(run);
        validateTestStage(stage);
    }


    @Test
    void getRunTest() {
        RunPojo run = browseService.getRun(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l, 1l);
        validateTestRun(run);
    }

    @Test
    void getRunFromReferenceTest() {
        RunPojo run = browseService.getRunFromReference(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.sha, TestData.runReference);
        validateTestRun(run);
    }

    @Test
    void getRunStagesTestResultsTest() {
        Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>> runStageResults = browseService.getRunStagesTestResults(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l, 1l);
        final RunPojo run = getTestRun(runStageResults.keySet(), TestData.runReference);
        final Map<StagePojo, Set<TestResultPojo>> stageTestResults = runStageResults.get(run);
        final StagePojo stage = getTestStage(stageTestResults.keySet(), TestData.stage);
        final TestResultPojo testResult = getTestResult(nestedSet(stageTestResults.values()), TestData.testResultId);

        validateTestRun(run);
        validateTestStage(stage);
        validateTestResultPojo(testResult);
    }

    @Test
    void getStageTestResultsTestSuitesTest() {
        //Map<StagePojo, Map<TestResultPojo, Set<TestSuitePojo>>> stageResultSuites =
        StageTestResultModel stageTestResultModel = browseService.getStageTestResultMap(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l, 1l, TestData.stage);

        final StagePojo stage = stageTestResultModel.getStage();
        final TestResultModel testResultModel = stageTestResultModel.getTestResult();

        validateTestStage(stage);
        validateTestResultModel(testResultModel);
        validateTestSuites(testResultModel.getTestSuites());
        validateTestCases(testResultModel.getTestSuites().get(0).getTestCases());
        validateTestCaseFaults(testResultModel.getTestSuites().get(0).getTestCases());
    }

    static OrgPojo getTestOrg(Collection<OrgPojo> orgs, final String expectedOrg) {

        OrgPojo org = null;
        for (OrgPojo o : orgs) {
            if (o.getOrgName().equals(expectedOrg)) {
                org = o;
                break;
            }
        }
        assertNotNull(org);
        return org;
    }

    static RepoPojo getTestRepo(Collection<RepoPojo> repos, String expectedRepo) {

        RepoPojo repo = null;
        for (RepoPojo r : repos) {
            if (r.getRepoName().equals(expectedRepo)) {
                repo = r;
                break;
            }
        }
        assertNotNull(repo);
        return repo;
    }

    static BranchPojo getTestBranch(Collection<BranchPojo> branches, String expectedBranchName) {
        BranchPojo branch = null;
        for (BranchPojo b : branches) {
            if (b.getBranchName().equals(expectedBranchName)) {
                branch = b;
                break;
            }
        }
        assertNotNull(branch);
        return branch;
    }

    private static <T> List<T> nestedSet(Collection<Set<T>> t) {
        return t.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    static JobPojo getTestJob(Collection<JobPojo> jobs, final Map<String, String> expectedJobInfo) {

        JobPojo job = null;
        for (JobPojo j : jobs) {
            if (JsonCompare.equalsMap(j.getJobInfo(), expectedJobInfo)) {
                job = j;
                break;
            }
        }
        assertNotNull(job);
        return job;
    }

    static RunPojo getTestRun(Collection<RunPojo> runs, UUID expectedRunReference) {

        RunPojo run = null;
        for (RunPojo r : runs) {
            if (expectedRunReference.toString().equals(r.getRunReference())) {
                run = r;
                break;
            }
        }
        assertNotNull(run);
        return run;
    }

    static TestResultPojo getTestResult(Collection<TestResultPojo> testResults, Long expectedTestResultId) {
        TestResultPojo testResult = null;
        for (TestResultPojo t : testResults) {
            if (t.getTestResultId().equals(expectedTestResultId)) {
                testResult = t;
                break;
            }
        }
        assertNotNull(testResult);
        return testResult;
    }

    static TestSuitePojo getTestSuite(Collection<TestSuitePojo> testSuites, Long expectedTestSuiteId) {
        TestSuitePojo testSuite = null;
        for (TestSuitePojo t : testSuites) {
            if (t.getTestSuiteId().equals(expectedTestSuiteId)) {
                testSuite = t;
                break;
            }
        }
        assertNotNull(testSuite);
        return testSuite;
    }

    static StagePojo getTestStage(Collection<StagePojo> stages, final String expectedStageName) {
        StagePojo stage = null;
        for (StagePojo s : stages) {
            if (s.getStageName().equals(expectedStageName)) {
                stage = s;
                break;
            }
        }
        assertNotNull(stage);
        return stage;
    }

    private void validateTestOrg(OrgPojo org) {
        assertNotNull(org);
        assertEquals(TestData.org, org.getOrgName());
        assertEquals(1, org.getOrgId());
    }

    private void validateTestRepo(RepoPojo repo) {
        assertEquals(TestData.repo, repo.getRepoName());
        assertEquals(1, repo.getRepoId());
        assertEquals(1, repo.getOrgFk());
    }

    private void validateTestBranch(BranchPojo branch) {
        assertEquals(TestData.branch, branch.getBranchName());
        assertEquals(1, branch.getBranchId());
        assertEquals(1, branch.getRepoFk());
        assertNotNull(branch.getLastRun());
    }

    private void validateTestJob(JobPojo job) {
        assertTrue(JsonCompare.equalsMap(job.getJobInfo(), TestData.jobInfo));
        assertEquals(1, job.getBranchFk());
        assertEquals(1, job.getJobId());
        assertNotNull(job.getLastRun());
    }

    private void validateTestRun(RunPojo run) {
        assertEquals(TestData.runReference.toString(), run.getRunReference());
        assertEquals(1, run.getRunId());
        assertEquals(1, run.getJobFk());
        assertEquals(1, run.getJobRunCount());
        assertEquals(TestData.sha, run.getSha());
        assertNotNull(run.getRunDate());

    }

    private void validateTestStage(StagePojo stage) {
        assertEquals(TestData.stage, stage.getStageName());
        assertEquals(1, stage.getStageId());
        assertEquals(1, stage.getRunFk());
    }

    private void validateTestResultModel(TestResultModel testResult) {
        assertEquals(1, testResult.getTestResultId());
        assertEquals(1, testResult.getStageFk());
        assertEquals(TestData.expectedTestCaseCount, testResult.getTests());
    }

    private void validateTestResultPojo(TestResultPojo testResult) {
        assertEquals(1, testResult.getTestResultId());
        assertEquals(1, testResult.getStageFk());
        assertEquals(TestData.testResultTestPojoCount, testResult.getTests());
    }

    private void validateTestSuites(List<TestSuiteModel> testSuites) {
        assertEquals(1, testSuites.size());
        assertEquals(TestData.testSuiteTestCount, testSuites.get(0).getTests());
    }

    private void validateTestCases(List<TestCaseModel> testCases) {
        assertEquals(2, testCases.size());
    }

    private void validateTestCaseFaults(List<TestCaseModel> testCases) {
        int faultCount = 0;
        for (TestCaseModel t : testCases) {
            for (TestCaseFaultModel f : EmptyUtil.nullListToEmpty(t.getTestCaseFaults())) {
                faultCount++;
            }
        }
        assertEquals(1, faultCount);
    }
}
