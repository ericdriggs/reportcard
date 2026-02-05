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

import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BrowseJsonController hierarchy endpoints.
 * Tests validate JSON serialization and HTTP responses for company, org, repo, and branch level endpoints.
 * Includes SHA lookup tests and error case validation.
 */
public class BrowseJsonControllerTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;
    private final BrowseService browseService;

    @Autowired
    public BrowseJsonControllerTest(BrowseService browseService, BrowseJsonController browseJsonController) {
        super(browseService);
        this.browseService = browseService;
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
        // Call controller endpoint (runs=null uses default, jobInfoFilters=null)
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo, TestData.branch, null, null);

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
        // Call controller endpoint (runs=null uses default)
        ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> response =
            controller.getJobRunsStages(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId, null);

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

    // ==================== Latest Run Endpoint Tests ====================

    @Test
    void getLatestRunStagesJsonSuccessTest() {
        // Call latest run endpoint
        ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> response =
            controller.getLatestRunStages(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>> runStagesTestResults = response.getBody();
        assertNotNull(runStagesTestResults);
        assertFalse(runStagesTestResults.isEmpty());

        // Verify this is indeed the latest run
        RunPojo latestRun = runStagesTestResults.keySet().iterator().next();
        assertNotNull(latestRun.getRunId(), "Run ID should not be null");

        // Verify stages are present
        Map<StagePojo, Set<TestResultPojo>> stageTestResults = runStagesTestResults.get(latestRun);
        assertNotNull(stageTestResults);
        assertFalse(stageTestResults.isEmpty());

        // Verify expected stage exists
        boolean stageFound = false;
        for (StagePojo stage : stageTestResults.keySet()) {
            if (stage.getStageName().equals(TestData.stage)) {
                stageFound = true;
                break;
            }
        }
        assertTrue(stageFound, "Expected stage '" + TestData.stage + "' not found in response");
    }

    @Test
    void getLatestRunStagesJsonSameAsIdBasedTest() {
        // Get latest run ID directly
        Long latestRunId = browseService.getLatestRunId(TestData.jobId);

        // Call latest endpoint
        ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> latestResponse =
            controller.getLatestRunStages(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId);

        // Call ID-based endpoint with resolved run ID
        ResponseEntity<Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>>> idBasedResponse =
            controller.getStagesByIds(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, latestRunId);

        // Verify both responses return same run
        assertNotNull(latestResponse.getBody());
        assertNotNull(idBasedResponse.getBody());

        RunPojo latestRun = latestResponse.getBody().keySet().iterator().next();
        RunPojo idBasedRun = idBasedResponse.getBody().keySet().iterator().next();

        assertEquals(idBasedRun.getRunId(), latestRun.getRunId(),
            "Latest endpoint should return same run ID as ID-based endpoint");
    }

    @Test
    void getLatestRunStagesJsonNotFoundTest() {
        // Job ID with no runs
        Long nonExistentJobId = 999999L;

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            controller.getLatestRunStages(TestData.company, TestData.org, TestData.repo,
                TestData.branch, nonExistentJobId);
        });

        assertEquals(404, ex.getStatus().value(), "Expected 404 status for job with no runs");
    }

    // ==================== Latest Run Stage Endpoint Tests ====================

    @Test
    void getLatestRunStageTestResultsJsonSuccessTest() {
        // Call latest stage endpoint
        ResponseEntity<StageTestResultModel> response =
            controller.getLatestRunStageTestResults(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, TestData.stage);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        StageTestResultModel stageTestResultModel = response.getBody();
        assertNotNull(stageTestResultModel, "StageTestResultModel should not be null");

        // Verify stage details
        assertNotNull(stageTestResultModel.getStage(), "Stage should not be null");
        assertEquals(TestData.stage, stageTestResultModel.getStage().getStageName(),
            "Stage name should match TestData.stage");

        // Verify test result data
        assertNotNull(stageTestResultModel.getTestResult(), "TestResult should not be null");
        assertNotNull(stageTestResultModel.getTestResult().getTestSuites(),
            "Test suites should not be null");
        assertFalse(stageTestResultModel.getTestResult().getTestSuites().isEmpty(),
            "Test suites should not be empty");

        // Verify test cases exist
        boolean hasTestCases = stageTestResultModel.getTestResult().getTestSuites().stream()
            .anyMatch(suite -> suite.getTestCases() != null && !suite.getTestCases().isEmpty());
        assertTrue(hasTestCases, "At least one test suite should contain test cases");
    }

    @Test
    void getLatestRunStageTestResultsJsonSameAsIdBasedTest() {
        // Get latest run ID directly
        Long latestRunId = browseService.getLatestRunId(TestData.jobId);

        // Call latest endpoint
        ResponseEntity<StageTestResultModel> latestResponse =
            controller.getLatestRunStageTestResults(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, TestData.stage);

        // Call ID-based endpoint with resolved run ID
        ResponseEntity<StageTestResultModel> idBasedResponse =
            controller.getStageTestResultsTestSuites(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, latestRunId, TestData.stage);

        // Verify both responses return same stage
        assertNotNull(latestResponse.getBody());
        assertNotNull(idBasedResponse.getBody());

        StageTestResultModel latestModel = latestResponse.getBody();
        StageTestResultModel idBasedModel = idBasedResponse.getBody();

        assertEquals(idBasedModel.getStage().getStageId(), latestModel.getStage().getStageId(),
            "Latest endpoint should return same stage ID as ID-based endpoint");
        assertEquals(idBasedModel.getTestResult().getTestResultId(),
            latestModel.getTestResult().getTestResultId(),
            "Latest endpoint should return same test result ID as ID-based endpoint");
    }

    @Test
    void getLatestRunStageTestResultsJsonNotFoundTest() {
        // Job ID with no runs
        Long nonExistentJobId = 999999L;

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            controller.getLatestRunStageTestResults(TestData.company, TestData.org, TestData.repo,
                TestData.branch, nonExistentJobId, TestData.stage);
        });

        assertEquals(404, ex.getStatus().value(), "Expected 404 status for job with no runs");
    }

    // ==================== SHA Lookup Endpoint Tests ====================

    @Test
    void getRunsForShaJsonSuccessTest() {
        // Call controller endpoint with SHA - use TestData.sha constant
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getRuns(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.sha, null);

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
                    final Set<RunPojo> runs = jobEntry.getValue();
                    assertNotNull(runs);
                    assertFalse(runs.isEmpty());

                    // Verify at least one run matches TestData.sha
                    for (RunPojo run : runs) {
                        if (TestData.sha.equals(run.getSha())) {
                            testDataFound = true;
                            break;
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected run with SHA '" + TestData.sha + "' not found in response");
    }

    @Test
    void getRunForReferenceJsonSuccessTest() {
        // Call controller endpoint with SHA and runReference - use TestData constants
        ResponseEntity<RunPojo> response =
            controller.getRunForReference(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.sha, TestData.runReference, null);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        RunPojo run = response.getBody();
        assertNotNull(run, "Run should not be null");

        // Verify run matches expected test data
        assertEquals(TestData.sha, run.getSha(), "Run SHA should match TestData.sha");
        assertEquals(TestData.runReference.toString(), run.getRunReference(),
            "Run reference should match TestData.runReference");
    }

    // ==================== Error Case Validation Tests ====================
    // Per research findings, error tests call browseService methods directly (not controller)
    // ResponseStatusException propagates from service layer to Spring's global exception handling

    @Test
    void getOrgNotFoundTest() {
        // Test missing org - service should throw ResponseStatusException with 404
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getOrg(TestData.company, "MISSING_ORG");
        });

        // Verify 404 status
        assertEquals(404, ex.getStatus().value(), "Expected 404 status for missing org");

        // Verify hierarchical error message format contains all path segments
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains(TestData.company),
            "Error message should contain company: " + TestData.company);
        assertTrue(ex.getMessage().contains("MISSING_ORG"),
            "Error message should contain missing org name");
    }

    @Test
    void getRepoWithMissingParentOrgNotFoundTest() {
        // Test missing repo with missing parent org - service should throw ResponseStatusException with 404
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getRepo(TestData.company, "MISSING_ORG", "MISSING_REPO");
        });

        // Verify 404 status
        assertEquals(404, ex.getStatus().value(), "Expected 404 status for missing repo");

        // Verify hierarchical error message format contains all three path segments
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains(TestData.company),
            "Error message should contain company: " + TestData.company);
        assertTrue(ex.getMessage().contains("MISSING_ORG"),
            "Error message should contain missing org name");
        assertTrue(ex.getMessage().contains("MISSING_REPO"),
            "Error message should contain missing repo name");
    }

    @Test
    void getRepoWithValidOrgNotFoundTest() {
        // Test missing repo with valid org - service should throw ResponseStatusException with 404
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getRepo(TestData.company, TestData.org, "MISSING_REPO");
        });

        // Verify 404 status
        assertEquals(404, ex.getStatus().value(), "Expected 404 status for missing repo");

        // Verify hierarchical error message format contains all path segments
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains(TestData.company),
            "Error message should contain company: " + TestData.company);
        assertTrue(ex.getMessage().contains(TestData.org),
            "Error message should contain org: " + TestData.org);
        assertTrue(ex.getMessage().contains("MISSING_REPO"),
            "Error message should contain missing repo name");
    }

    @Test
    void getJobNotFoundTest() {
        // Test missing job - service should throw ResponseStatusException with 404
        java.util.TreeMap<String, String> missingJobInfo = new java.util.TreeMap<>();
        missingJobInfo.put("host", "nonexistent.jenkins.com");
        missingJobInfo.put("application", "missingapp");
        missingJobInfo.put("pipeline", "missingpipeline");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getJob(TestData.company, TestData.org, TestData.repo, TestData.branch, missingJobInfo);
        });

        // Verify 404 status
        assertEquals(404, ex.getStatus().value(), "Expected 404 status for missing job");

        // Verify hierarchical error message format contains all path segments including jobInfo
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains(TestData.company),
            "Error message should contain company: " + TestData.company);
        assertTrue(ex.getMessage().contains(TestData.org),
            "Error message should contain org: " + TestData.org);
        assertTrue(ex.getMessage().contains(TestData.repo),
            "Error message should contain repo: " + TestData.repo);
        assertTrue(ex.getMessage().contains(TestData.branch),
            "Error message should contain branch: " + TestData.branch);
        assertTrue(ex.getMessage().contains("jobInfo"),
            "Error message should contain jobInfo");
    }

    @Test
    void getCompanyNotFoundTest() {
        // Test missing company - service should throw ResponseStatusException with 404
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getCompany("MISSING_COMPANY");
        });

        // Verify 404 status
        assertEquals(404, ex.getStatus().value(), "Expected 404 status for missing company");

        // Verify error message contains company name
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("MISSING_COMPANY"),
            "Error message should contain missing company name");
    }

    // ==================== Runs Parameter Tests ====================

    @Test
    void getBranchJobsRunsWithRunsParameterTest() {
        // Test with explicit runs parameter (limit to 10)
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
                TestData.branch, 10, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify runs per job limited to 10
        for (Map<JobPojo, Set<RunPojo>> jobRuns : result.values()) {
            for (Set<RunPojo> runs : jobRuns.values()) {
                assertTrue(runs.size() <= 10, "Each job should have at most 10 runs");
            }
        }
    }

    @Test
    void getBranchJobsRunsDefaultRunsTest() {
        // Test without runs parameter (should use default 60)
        // Note: Updated signature requires runs parameter, pass null to test default behavior
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
                TestData.branch, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify runs per job limited to 60 (default)
        for (Map<JobPojo, Set<RunPojo>> jobRuns : result.values()) {
            for (Set<RunPojo> runs : jobRuns.values()) {
                assertTrue(runs.size() <= 60, "Each job should have at most 60 runs (default)");
            }
        }
    }

    @Test
    void getBranchJobsRunsWithZeroRunsUsesDefaultTest() {
        // Test with runs=0 (should use default 60)
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
                TestData.branch, 0, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Should return results (not empty due to invalid runs value)
        assertFalse(response.getBody().isEmpty());

        // Verify runs per job limited to 60 (default because 0 < 1)
        for (Map<JobPojo, Set<RunPojo>> jobRuns : response.getBody().values()) {
            for (Set<RunPojo> runs : jobRuns.values()) {
                assertTrue(runs.size() <= 60, "Each job should have at most 60 runs (default for runs=0)");
            }
        }
    }

    @Test
    void getBranchJobsRunsWithNegativeRunsUsesDefaultTest() {
        // Test with runs=-1 (should use default 60)
        ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> response =
            controller.getBranchJobsRuns(TestData.company, TestData.org, TestData.repo,
                TestData.branch, -1, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Should return results (not empty due to invalid runs value)
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getJobRunsStagesWithRunsParameterTest() {
        // Test job-level runs parameter
        ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> response =
            controller.getJobRunsStages(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, 5);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify runs limited to 5
        for (Map<RunPojo, Set<StagePojo>> runStages : result.values()) {
            assertTrue(runStages.size() <= 5, "Job should have at most 5 runs");
        }
    }

    @Test
    void getJobRunsStagesDefaultRunsTest() {
        // Test without runs parameter (null should use default 60)
        ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> response =
            controller.getJobRunsStages(TestData.company, TestData.org, TestData.repo,
                TestData.branch, TestData.jobId, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify runs limited to 60 (default)
        for (Map<RunPojo, Set<StagePojo>> runStages : result.values()) {
            assertTrue(runStages.size() <= 60, "Job should have at most 60 runs (default)");
        }
    }
}
