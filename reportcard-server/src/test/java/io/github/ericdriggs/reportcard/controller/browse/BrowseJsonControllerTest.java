package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BrowseJsonController hierarchy endpoints.
 * Tests validate JSON serialization and HTTP responses for company, org, repo, and branch level endpoints.
 */
public class BrowseJsonControllerTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerTest(BrowseService browseService, BrowseJsonController browseJsonController) {
        super(browseService);
        this.controller = browseJsonController;
    }

    @Test
    void getCompanyOrgsJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> response = controller.getCompanyOrgs();

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<CompanyPojo, Set<OrgPojo>> companyOrgs = response.getBody();
        assertNotNull(companyOrgs);
        assertFalse(companyOrgs.isEmpty());

        // Verify test data appears in response
        boolean companyWasFound = false;
        for (Map.Entry<CompanyPojo, Set<OrgPojo>> entry : companyOrgs.entrySet()) {
            final CompanyPojo company = entry.getKey();
            final Set<OrgPojo> orgs = entry.getValue();
            assertNotNull(orgs);
            assertFalse(orgs.isEmpty());

            if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
                companyWasFound = true;
                // Verify expected org exists
                boolean orgFound = false;
                for (OrgPojo org : orgs) {
                    if (org.getOrgName().equals(TestData.org)) {
                        orgFound = true;
                        break;
                    }
                }
                assertTrue(orgFound, "Expected org '" + TestData.org + "' not found in company '" + TestData.company + "'");
            }
        }
        assertTrue(companyWasFound, "Expected company '" + TestData.company + "' not found in response");
    }

    @Test
    void getCompanyOrgsReposJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>> response =
            controller.getCompanyOrgsRepos(TestData.company);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> companyOrgsRepos = response.getBody();
        assertNotNull(companyOrgsRepos);
        assertFalse(companyOrgsRepos.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> companyEntry : companyOrgsRepos.entrySet()) {
            final CompanyPojo company = companyEntry.getKey();
            final Map<OrgPojo, Set<RepoPojo>> orgRepos = companyEntry.getValue();
            assertNotNull(orgRepos);
            assertFalse(orgRepos.isEmpty());

            if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
                for (Map.Entry<OrgPojo, Set<RepoPojo>> orgEntry : orgRepos.entrySet()) {
                    final OrgPojo org = orgEntry.getKey();
                    final Set<RepoPojo> repos = orgEntry.getValue();
                    assertNotNull(repos);
                    assertFalse(repos.isEmpty());

                    if (org.getOrgName().equals(TestData.org)) {
                        // Verify expected repo exists
                        for (RepoPojo repo : repos) {
                            if (repo.getRepoName().equals(TestData.repo)) {
                                testDataFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (company: " + TestData.company +
            ", org: " + TestData.org + ", repo: " + TestData.repo + ") not found in response");
    }

    @Test
    void getOrgReposBranchesJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>>> response =
            controller.getOrgReposBranches(TestData.company, TestData.org);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgReposBranches = response.getBody();
        assertNotNull(orgReposBranches);
        assertFalse(orgReposBranches.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgEntry : orgReposBranches.entrySet()) {
            final OrgPojo org = orgEntry.getKey();
            final Map<RepoPojo, Set<BranchPojo>> repoBranches = orgEntry.getValue();
            assertNotNull(repoBranches);
            assertFalse(repoBranches.isEmpty());

            if (org.getOrgName().equals(TestData.org)) {
                for (Map.Entry<RepoPojo, Set<BranchPojo>> repoEntry : repoBranches.entrySet()) {
                    final RepoPojo repo = repoEntry.getKey();
                    final Set<BranchPojo> branches = repoEntry.getValue();
                    assertNotNull(branches);
                    assertFalse(branches.isEmpty());

                    if (repo.getRepoName().equals(TestData.repo)) {
                        // Verify expected branch exists
                        for (BranchPojo branch : branches) {
                            if (branch.getBranchName().equals(TestData.branch)) {
                                testDataFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (org: " + TestData.org +
            ", repo: " + TestData.repo + ", branch: " + TestData.branch + ") not found in response");
    }

    @Test
    void getRepoBranchesJobsJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>>> response =
            controller.getRepoBranchesJobs(TestData.company, TestData.org, TestData.repo);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoBranchesJobs = response.getBody();
        assertNotNull(repoBranchesJobs);
        assertFalse(repoBranchesJobs.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoEntry : repoBranchesJobs.entrySet()) {
            final RepoPojo repo = repoEntry.getKey();
            final Map<BranchPojo, Set<JobPojo>> branchJobs = repoEntry.getValue();
            assertNotNull(branchJobs);
            assertFalse(branchJobs.isEmpty());

            if (repo.getRepoName().equals(TestData.repo)) {
                for (Map.Entry<BranchPojo, Set<JobPojo>> branchEntry : branchJobs.entrySet()) {
                    final BranchPojo branch = branchEntry.getKey();
                    final Set<JobPojo> jobs = branchEntry.getValue();
                    assertNotNull(jobs);
                    assertFalse(jobs.isEmpty());

                    if (branch.getBranchName().equals(TestData.branch)) {
                        // Verify expected job exists (matching jobInfo)
                        for (JobPojo job : jobs) {
                            if (JsonCompare.equalsMap(job.getJobInfo(), TestData.jobInfo)) {
                                testDataFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (repo: " + TestData.repo +
            ", branch: " + TestData.branch + ", jobInfo: " + TestData.jobInfo + ") not found in response");
    }

    @Test
    void getBranchJobsRunsJsonSuccessTest() {
        // Call controller endpoint (jobInfoFilters not implemented yet, pass null)
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo, TestData.branch, null);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchJobsRuns = response.getBody();
        assertNotNull(branchJobsRuns);
        assertFalse(branchJobsRuns.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchEntry : branchJobsRuns.entrySet()) {
            final BranchPojo branch = branchEntry.getKey();
            final Map<JobPojo, Set<RunPojo>> jobRuns = branchEntry.getValue();
            assertNotNull(jobRuns);
            assertFalse(jobRuns.isEmpty());

            if (branch.getBranchName().equals(TestData.branch)) {
                for (Map.Entry<JobPojo, Set<RunPojo>> jobEntry : jobRuns.entrySet()) {
                    final JobPojo job = jobEntry.getKey();
                    final Set<RunPojo> runs = jobEntry.getValue();
                    assertNotNull(runs);
                    assertFalse(runs.isEmpty());

                    // Verify jobId matches TestData.jobId
                    if (job.getJobId().equals(TestData.jobId)) {
                        testDataFound = true;
                        // Verify at least one run exists
                        assertTrue(runs.size() > 0, "Expected at least one run for jobId: " + TestData.jobId);
                        break;
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (branch: " + TestData.branch +
            ", jobId: " + TestData.jobId + ") not found in response");
    }

    @Test
    void getJobRunsStagesJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> response =
            controller.getJobRunsStages(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> jobRunsStages = response.getBody();
        assertNotNull(jobRunsStages);
        assertFalse(jobRunsStages.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<JobPojo, Map<RunPojo, Set<StagePojo>>> jobEntry : jobRunsStages.entrySet()) {
            final JobPojo job = jobEntry.getKey();
            final Map<RunPojo, Set<StagePojo>> runStages = jobEntry.getValue();
            assertNotNull(runStages);
            assertFalse(runStages.isEmpty());

            // Verify job matches TestData.jobId
            if (job.getJobId().equals(TestData.jobId)) {
                // Verify at least one run and stage exist
                assertTrue(runStages.size() > 0, "Expected at least one run for jobId: " + TestData.jobId);

                for (Map.Entry<RunPojo, Set<StagePojo>> runEntry : runStages.entrySet()) {
                    final Set<StagePojo> stages = runEntry.getValue();
                    assertNotNull(stages);
                    assertFalse(stages.isEmpty());

                    // Verify stage name matches TestData.stage
                    for (StagePojo stage : stages) {
                        if (stage.getStageName().equals(TestData.stage)) {
                            testDataFound = true;
                            break;
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (jobId: " + TestData.jobId +
            ", stage: " + TestData.stage + ") not found in response");
    }

    @Test
    void getStagesByIdsJsonSuccessTest() {
        // Use runId 1L as mentioned in plan
        Long runId = 1L;

        // Call controller endpoint
        ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> response =
            controller.getStagesByIds(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId, runId);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>> runStagesTestResults = response.getBody();
        assertNotNull(runStagesTestResults);
        assertFalse(runStagesTestResults.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<RunPojo, Map<StagePojo, Set<TestResultPojo>>> runEntry : runStagesTestResults.entrySet()) {
            final RunPojo run = runEntry.getKey();
            final Map<StagePojo, Set<TestResultPojo>> stageTestResults = runEntry.getValue();
            assertNotNull(stageTestResults);
            assertFalse(stageTestResults.isEmpty());

            // Verify run exists
            assertNotNull(run.getRunId(), "Run ID should not be null");

            // Verify stages contain TestData.stage
            for (Map.Entry<StagePojo, Set<TestResultPojo>> stageEntry : stageTestResults.entrySet()) {
                final StagePojo stage = stageEntry.getKey();
                final Set<TestResultPojo> testResults = stageEntry.getValue();

                if (stage.getStageName().equals(TestData.stage)) {
                    testDataFound = true;
                    // Verify test results are present
                    assertNotNull(testResults);
                    assertFalse(testResults.isEmpty(), "Test results should not be empty for stage: " + TestData.stage);
                    break;
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (stage: " + TestData.stage +
            ") not found in response");
    }

    @Test
    void getStageTestResultsTestSuitesJsonSuccessTest() {
        // Use runId 1L
        Long runId = 1L;

        // Call controller endpoint
        ResponseEntity<StageTestResultModel> response =
            controller.getStageTestResultsTestSuites(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, runId, TestData.stage);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        StageTestResultModel stageTestResultModel = response.getBody();
        assertNotNull(stageTestResultModel, "StageTestResultModel should not be null");

        // Verify stage details contain test suites
        assertNotNull(stageTestResultModel.getStage(), "Stage should not be null");
        assertEquals(TestData.stage, stageTestResultModel.getStage().getStageName(),
            "Stage name should match TestData.stage");

        assertNotNull(stageTestResultModel.getTestResult(), "TestResult should not be null");

        // Verify test suites contain test cases
        assertNotNull(stageTestResultModel.getTestResult().getTestSuites(),
            "Test suites should not be null");
        assertFalse(stageTestResultModel.getTestResult().getTestSuites().isEmpty(),
            "Test suites should not be empty");

        // Verify test cases exist in at least one test suite
        boolean hasTestCases = stageTestResultModel.getTestResult().getTestSuites().stream()
            .anyMatch(suite -> suite.getTestCases() != null && !suite.getTestCases().isEmpty());
        assertTrue(hasTestCases, "At least one test suite should contain test cases");
    }
}
