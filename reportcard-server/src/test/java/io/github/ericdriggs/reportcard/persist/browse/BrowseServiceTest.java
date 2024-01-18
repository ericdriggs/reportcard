package io.github.ericdriggs.reportcard.persist.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.persist.BrowseService;;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseServiceTest extends AbstractBrowseServiceTest {
    @Autowired
    public BrowseServiceTest(BrowseService browseService) {
        super(browseService);
    }

    @Test
    void getCompanyOrgsSuccessTest() {
        Map<Company, Set<Org>> companyOrgs = browseService.getCompanyOrgs();
        assertNotNull(companyOrgs);
        assertFalse(companyOrgs.isEmpty());

        boolean companyWasFound = false;
        for (Map.Entry<Company, Set<Org>> entry: companyOrgs.entrySet()){
            final Company company = entry.getKey();
            final Set<Org> orgs = entry.getValue();
            assertNotNull(orgs);
            assertFalse(orgs.isEmpty());
            if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
                Set<String> orgNames = orgs.stream().map(Org::getOrgName).collect(Collectors.toSet());
                assertTrue(orgNames.contains(TestData.org));
                companyWasFound = true;
            }
        }
        assertTrue(companyWasFound);

    }

    @Test
    void getOrgSuccessTest() {
        Org org = browseService.getOrg(TestData.company, TestData.org);
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
        final Map<Org, Set<Repo>> orgRepos = browseService.getOrgsRepos(TestData.company);
        final Org org = getTestOrg(orgRepos.keySet(), TestData.org);
        validateTestOrg(org);
        final Repo repo = getTestRepo(orgRepos.get(org), TestData.repo);
        validateTestRepo(repo);
    }

    @Test
    void getOrgReposBranchesTest() {
        Map<Org, Map<Repo, Set<Branch>>> orgReposBranches = browseService.getOrgReposBranches(TestData.company, TestData.org);
        final Org org = getTestOrg(orgReposBranches.keySet(), TestData.org);
        validateTestOrg(org);
        final Map<Repo, Set<Branch>> repoBranches = orgReposBranches.get(org);
        final Repo repo = getTestRepo(repoBranches.keySet(), TestData.repo);
        validateTestRepo(repo);
        final Branch branch = getTestBranch(repoBranches.get(repo), TestData.branch);
        validateTestBranch(branch);
    }

    @Test
    void getRepoSuccessTest() {
        Repo repo = browseService.getRepo(TestData.company, TestData.org, TestData.repo);
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
        Map<Repo, Map<Branch, Set<Job>>> repoBranchesJobs = browseService.getRepoBranchesJobs(TestData.company, TestData.org, TestData.repo);
        final Repo repo = getTestRepo(repoBranchesJobs.keySet(), TestData.repo);
        final Map<Branch, Set<Job>> branchJobs = repoBranchesJobs.get(repo);
        final Branch branch = getTestBranch(branchJobs.keySet(), TestData.branch);
        final Job job = getTestJob(nestedSet(branchJobs.values()), TestData.jobInfo);
        validateTestRepo(repo);
        validateTestBranch(branch);
        validateTestJob(job);
    }

    @Test
    void getBranchTest() {
        Branch branch = browseService.getBranch(TestData.company, TestData.org, TestData.repo, TestData.branch);
        validateTestBranch(branch);
    }

    @Test
    void getBranchJobsRunsTest() {
        Map<Branch, Map<Job, Set<Run>>> branchJobRuns = browseService.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo, TestData.branch, null);
        final Branch branch = getTestBranch(branchJobRuns.keySet(), TestData.branch);
        final Map<Job, Set<Run>> jobRuns = branchJobRuns.get(branch);
        final Job job = getTestJob(jobRuns.keySet(), TestData.jobInfo);
        final Run run = getTestRun(nestedSet(jobRuns.values()), TestData.runReference);
        validateTestBranch(branch);
        validateTestJob(job);
        validateTestRun(run);
    }

    @Test
    void getBranchJobsRunsForShaTest() {
        Map<Branch, Map<Job, Set<Run>>> branchJobRuns = browseService.getBranchJobsRunsForSha(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.sha);
        final Branch branch = getTestBranch(branchJobRuns.keySet(), TestData.branch);
        final Map<Job, Set<Run>> jobRuns = branchJobRuns.get(branch);
        final Job job = getTestJob(jobRuns.keySet(), TestData.jobInfo);
        final Run run = getTestRun(nestedSet(jobRuns.values()), TestData.runReference);
        validateTestBranch(branch);
        validateTestJob(job);
        validateTestRun(run);
    }

    @Test
    void getJobTest() {
        Job job = browseService.getJob(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobInfo);
        validateTestJob(job);
    }

    @Test
    void getJobRunsStagesTest() {
        Map<Job, Map<Run, Set<Stage>>> jobRunsStages = browseService.getJobRunsStages(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l);
        final Job job = getTestJob(jobRunsStages.keySet(), TestData.jobInfo);
        final Map<Run, Set<Stage>> runStages = jobRunsStages.get(job);
        final Run run = getTestRun(runStages.keySet(), TestData.runReference);
        final Stage stage = getTestStage(nestedSet(runStages.values()), TestData.stage);
        validateTestJob(job);
        validateTestRun(run);
        validateTestStage(stage);
    }

    @Test
    void getStageTest() {
        Stage stage = browseService.getStage(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.sha, TestData.runReference, TestData.stage);
        validateTestStage(stage);
    }

    @Test
    void getRunTest() {
        Run run = browseService.getRun(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l, 1l);
        validateTestRun(run);
    }

    @Test
    void getRunFromReferenceTest() {
        Run run = browseService.getRunFromReference(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.sha, TestData.runReference);
        validateTestRun(run);
    }



    @Test
    void getRunStagesTestResultsTest() {
        Map<Run, Map<Stage, Set<TestResult>>> runStageResults = browseService.getRunStagesTestResults(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l, 1l);
        final Run run = getTestRun(runStageResults.keySet(), TestData.runReference);
        final Map<Stage, Set<TestResult>> stageTestResults = runStageResults.get(run);
        final Stage stage = getTestStage(stageTestResults.keySet(), TestData.stage);
        final TestResult testResult = getTestResult(nestedSet(stageTestResults.values()), TestData.testResultId);

        validateTestRun(run);
        validateTestStage(stage);
        validateTestResult(testResult);
    }

    @Test
    void getStageTestResultsTestSuitesTest() {
        Map<Stage, Map<TestResult, Set<TestSuite>>> stageResultSuites = browseService.getStageTestResultsTestSuites(TestData.company, TestData.org, TestData.repo, TestData.branch, 1l, 1l, TestData.stage);
        final Stage stage = getTestStage(stageResultSuites.keySet(), TestData.stage);
        final Map<TestResult, Set<TestSuite>> resultSuites = stageResultSuites.get(stage);
        final TestResult testResult = getTestResult(resultSuites.keySet(), TestData.testResultId);
        final TestSuite testSuite = getTestSuite(nestedSet(resultSuites.values()), TestData.testSuiteId);
        validateTestStage(stage);
        validateTestResult(testResult);
        validateTestSuite(testSuite);
    }

    static Org getTestOrg(Collection<Org> orgs, final String expectedOrg) {

        Org org = null;
        for (Org o : orgs) {
            if (o.getOrgName().equals(expectedOrg)) {
                org = o;
                break;
            }
        }
        assertNotNull(org);
        return org;
    }

    static Repo getTestRepo(Collection<Repo> repos, String expectedRepo) {

        Repo repo = null;
        for (Repo r : repos) {
            if (r.getRepoName().equals(expectedRepo)) {
                repo = r;
                break;
            }
        }
        assertNotNull(repo);
        return repo;
    }

    static Branch getTestBranch(Collection<Branch> branches, String expectedBranchName) {
        Branch branch = null;
        for (Branch b : branches) {
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

    static Job getTestJob(Collection<Job> jobs, final Map<String,String> expectedJobInfo) {

        Job job = null;
        for (Job j : jobs) {
            if (JsonCompare.equalsMap(j.getJobInfo(), expectedJobInfo)) {
                job = j;
                break;
            }
        }
        assertNotNull(job);
        return job;
    }

    static Run getTestRun(Collection<Run> runs, String expectedRunReference) {

        Run run = null;
        for (Run r : runs) {
            if (r.getRunReference().equals(expectedRunReference)) {
                run = r;
                break;
            }
        }
        assertNotNull(run);
        return run;
    }

    static TestResult getTestResult(Collection<TestResult> testResults, Long expectedTestResultId) {
        TestResult testResult = null;
        for (TestResult t : testResults) {
            if (t.getTestResultId().equals(expectedTestResultId)) {
                testResult = t;
                break;
            }
        }
        assertNotNull(testResult);
        return testResult;
    }

    static TestSuite getTestSuite(Collection<TestSuite> testSuites, Long expectedTestSuiteId) {
        TestSuite testSuite = null;
        for (TestSuite t : testSuites) {
            if (t.getTestSuiteId().equals(expectedTestSuiteId)) {
                testSuite = t;
                break;
            }
        }
        assertNotNull(testSuite);
        return testSuite;
    }

    static Stage getTestStage(Collection<Stage> stages, final String expectedStageName) {
        Stage stage = null;
        for (Stage s : stages) {
            if (s.getStageName().equals(expectedStageName)) {
                stage = s;
                break;
            }
        }
        assertNotNull(stage);
        return stage;
    }

    private void validateTestOrg(Org org) {
        assertNotNull(org);
        assertEquals(TestData.org, org.getOrgName());
        assertEquals(1, org.getOrgId());
    }

    private void validateTestRepo(Repo repo) {
        assertEquals(TestData.repo, repo.getRepoName());
        assertEquals(1, repo.getRepoId());
        assertEquals(1, repo.getOrgFk());
    }

    private void validateTestBranch(Branch branch) {
        assertEquals(TestData.branch, branch.getBranchName());
        assertEquals(1, branch.getBranchId());
        assertEquals(1, branch.getRepoFk());
        assertNotNull(branch.getLastRun());
    }

    private void validateTestJob(Job job) {
        assertTrue(JsonCompare.equalsMap(job.getJobInfo(), TestData.jobInfo));
        assertEquals(1, job.getBranchFk());
        assertEquals(1, job.getJobId());
        assertNotNull(job.getLastRun());
    }

    private void validateTestRun(Run run) {
        assertEquals(TestData.runReference, run.getRunReference());
        assertEquals(1, run.getRunId());
        assertEquals(1, run.getJobFk());
        assertEquals(1, run.getJobRunCount());
        assertEquals(TestData.sha, run.getSha());
        assertNotNull(run.getCreated());
        assertNotNull(run.getCreated());
    }

    private void validateTestStage(Stage stage) {
        assertEquals(TestData.stage, stage.getStageName());
        assertEquals(1, stage.getStageId());
        assertEquals(1, stage.getRunFk());
    }

    private void validateTestResult(TestResult testResult) {
        assertEquals(1, testResult.getTestResultId());
        assertEquals(1, testResult.getStageFk());
        assertEquals(TestData.testResultTestCount, testResult.getTests());
    }

    private void validateTestSuite(TestSuite testSuite) {
        assertEquals(1, testSuite.getTestSuiteId());
        assertEquals(1, testSuite.getTestResultFk());
        assertEquals(TestData.testSuiteTestCount, testSuite.getTests());
    }
}
