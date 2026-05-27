package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.cache.model.BranchStageViewResponse;
import io.github.ericdriggs.reportcard.controller.browse.response.*;
import io.github.ericdriggs.reportcard.controller.graph.trend.TestTrendTable;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.controller.browse.response.OrgDashboardFlattener;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.ericdriggs.reportcard.util.StringMapUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO: add reports endpoint after stages
@Slf4j
@RestController
@RequestMapping("/json")
@SuppressWarnings("unused")
public class BrowseJsonController {

    private final BrowseService browseService;
    private final GraphService graphService;

    @Autowired
    public BrowseJsonController(BrowseService browseService, GraphService graphService) {
        this.browseService = browseService;
        this.graphService = graphService;
    }

    @Operation(summary = "List all companies and their orgs")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<CompanyOrgsResponse> getCompanyOrgs() {
        return new ResponseEntity<>(CompanyOrgsResponse.fromMap(browseService.getCompanyOrgs()), HttpStatus.OK);
    }

    @Operation(summary = "List orgs and repos for a company")
    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
    public ResponseEntity<CompanyOrgsReposResponse> getCompanyOrgsRepos(@PathVariable String company) {
        return new ResponseEntity<>(CompanyOrgsReposResponse.fromMap(browseService.getCompanyOrgsRepos(company)), HttpStatus.OK);
    }

    @Operation(summary = "List repos and branches for an org")
    @GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<OrgReposBranchesResponse> getOrgReposBranches(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesResponse.fromMap(browseService.getOrgReposBranches(company, org)), HttpStatus.OK);
    }

    @Operation(summary = "Get repo dashboard with latest stage results per branch/job")
    @GetMapping(path = "repo/{repoName}/dashboard", produces = "application/json")
    public ResponseEntity<List<OrgDashboard>> getRepoDashboardJson(
            @PathVariable String repoName,
            @Parameter(description = "Filter to specific branches. Empty means all branches.")
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @Parameter(description = "Include default branches (main, master, develop) even if not in branches list. Default: true")
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @Parameter(description = "Only include runs from the last N days. Null means no time limit.")
            @RequestParam(required = false) Integer days
    ) {
        List<OrgDashboard> repoDashboards = graphService.getRepoDashboard(repoName, branches, shouldIncludeDefaultBranches, validateDays(days));
        return new ResponseEntity<>(repoDashboards, HttpStatus.OK);
    }

    @Operation(summary = "Get repo dashboard filtered by job info key-value pairs")
    @GetMapping(path = "repo/{repoName}/jobinfo/{jobInfo}/dashboard", produces = "application/json")
    public ResponseEntity<List<OrgDashboard>> getRepoDashboardWithJobInfoJson(
            @PathVariable String repoName,
            @Parameter(description = "Comma-separated key=value pairs to filter jobs (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @Parameter(description = "Filter to specific branches. Empty means all branches.")
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @Parameter(description = "Include default branches (main, master, develop) even if not in branches list. Default: true")
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @Parameter(description = "Only include runs from the last N days. Null means no time limit.")
            @RequestParam(required = false) Integer days
    ) {
        Map<String, String> jobInfoFilter = validateJobInfo(jobInfo);
        List<OrgDashboard> repoDashboards = graphService.getRepoDashboard(repoName, branches, shouldIncludeDefaultBranches, validateDays(days), jobInfoFilter);
        return new ResponseEntity<>(repoDashboards, HttpStatus.OK);
    }

    @Operation(summary = "Get repo dashboard filtered by job info, flattened to one row per stage")
    @GetMapping(path = "repo/{repoName}/jobinfo/{jobInfo}/dashboard/flat", produces = "application/json")
    public ResponseEntity<List<FlatDashboardEntry>> getRepoDashboardFlatWithJobInfoJson(
            @PathVariable String repoName,
            @Parameter(description = "Comma-separated key=value pairs to filter jobs (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @Parameter(description = "Filter to specific branches. Empty means all branches.")
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @Parameter(description = "Include default branches (main, master, develop) even if not in branches list. Default: true")
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @Parameter(description = "Only include runs from the last N days. Null means no time limit.")
            @RequestParam(required = false) Integer days
    ) {
        Map<String, String> jobInfoFilter = validateJobInfo(jobInfo);
        List<OrgDashboard> repoDashboards = graphService.getRepoDashboard(repoName, branches, shouldIncludeDefaultBranches, validateDays(days), jobInfoFilter);
        return new ResponseEntity<>(OrgDashboardFlattener.flatten(repoDashboards), HttpStatus.OK);
    }

    @Operation(summary = "Get repo dashboard flattened to one row per stage")
    @GetMapping(path = "repo/{repoName}/dashboard/flat", produces = "application/json")
    public ResponseEntity<List<FlatDashboardEntry>> getRepoDashboardFlatJson(
            @PathVariable String repoName,
            @Parameter(description = "Filter to specific branches. Empty means all branches.")
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @Parameter(description = "Include default branches (main, master, develop) even if not in branches list. Default: true")
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @Parameter(description = "Only include runs from the last N days. Null means no time limit.")
            @RequestParam(required = false) Integer days
    ) {
        List<OrgDashboard> repoDashboards = graphService.getRepoDashboard(repoName, branches, shouldIncludeDefaultBranches, validateDays(days));
        return new ResponseEntity<>(OrgDashboardFlattener.flatten(repoDashboards), HttpStatus.OK);
    }

    @Operation(summary = "Get org dashboard with latest stage results per repo/branch/job")
    @GetMapping(path = "company/{company}/org/{org}/dashboard", produces = "application/json")
    public ResponseEntity<OrgDashboard> getOrgDashboardJson(
            @PathVariable String company,
            @PathVariable String org,
            @Parameter(description = "Filter to specific repos. Null means all repos in the org.")
            @RequestParam(required = false) List<String> repos,
            @Parameter(description = "Filter to specific branches. Empty means all branches.")
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @Parameter(description = "Include default branches (main, master, develop) even if not in branches list. Default: true")
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @Parameter(description = "Only include runs from the last N days. Null means no time limit.")
            @RequestParam(required = false) Integer days
    ) {
        OrgDashboard orgDashboard = graphService.getOrgDashboard(company, org, repos, branches, shouldIncludeDefaultBranches, validateDays(days));
        return new ResponseEntity<>(orgDashboard, HttpStatus.OK);
    }

    @Operation(summary = "Get org dashboard flattened to one row per stage")
    @GetMapping(path = "company/{company}/org/{org}/dashboard/flat", produces = "application/json")
    public ResponseEntity<List<FlatDashboardEntry>> getOrgDashboardFlatJson(
            @PathVariable String company,
            @PathVariable String org,
            @Parameter(description = "Filter to specific repos. Null means all repos in the org.")
            @RequestParam(required = false) List<String> repos,
            @Parameter(description = "Filter to specific branches. Empty means all branches.")
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @Parameter(description = "Include default branches (main, master, develop) even if not in branches list. Default: true")
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @Parameter(description = "Only include runs from the last N days. Null means no time limit.")
            @RequestParam(required = false) Integer days
    ) {
        OrgDashboard orgDashboard = graphService.getOrgDashboard(company, org, repos, branches, shouldIncludeDefaultBranches, validateDays(days));
        return new ResponseEntity<>(OrgDashboardFlattener.flatten(List.of(orgDashboard)), HttpStatus.OK);
    }

    @Operation(summary = "List branches and jobs for a repo")
    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
    public ResponseEntity<RepoBranchesJobsResponse> getRepoBranchesJobs(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo) {
        return new ResponseEntity<>(RepoBranchesJobsResponse.fromMap(browseService.getRepoBranchesJobs(company, org, repo)), HttpStatus.OK);
    }

    @Operation(summary = "List jobs and recent runs for a branch")
    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
    public ResponseEntity<BranchJobsRunsResponse> getBranchJobsRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Maximum number of recent runs to return per job. Default: 60")
            @RequestParam(required = false, defaultValue = "60") Integer runs,
            @Parameter(description = "Filter jobs by info key-value pairs passed as query params (e.g. ?application=myapp&env=prod)")
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        runs = validateRuns(runs);
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult =
            browseService.getBranchJobsRuns(company, org, repo, branch, jobInfoFilters);
        return new ResponseEntity<>(BranchJobsRunsResponse.fromMap(limitRunsPerJob(fullResult, runs)), HttpStatus.OK);
    }

    @Operation(summary = "List recent runs and their stages for a job")
    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "application/json")
    public ResponseEntity<JobRunsStagesResponse> getJobRunsStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @Parameter(description = "Maximum number of recent runs to return. Default: 60")
            @RequestParam(required = false, defaultValue = "60") Integer runs) {
        runs = validateRuns(runs);
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> fullResult =
            browseService.getJobRunsStages(company, org, repo, branch, jobId);
        return new ResponseEntity<>(JobRunsStagesResponse.fromMap(limitRunsInJob(fullResult, runs)), HttpStatus.OK);
    }

    @Operation(summary = "List recent runs and their stages for a job (by job info)")
    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/run"}, produces = "application/json")
    public ResponseEntity<JobRunsStagesResponse> getJobRunsStagesFromJobInfo(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @Parameter(description = "Maximum number of recent runs to return. Default: 60")
            @RequestParam(required = false, defaultValue = "60") Integer runs) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        JobPojo job = browseService.getJob(company, org, repo, branch, jobInfoMap);
        return getJobRunsStages(company, org, repo, branch, job.getJobId(), runs);
    }

    @Operation(summary = "Get stages and test results for a specific run")
    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage"}, produces = "application/json")
    public ResponseEntity<RunStagesTestResultsResponse> getStagesByIds(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId) {
        BranchStageViewResponse result = graphService.getRunBranchStageViewResponse(company, org, repo, branch, jobId, runId);
        return new ResponseEntity<>(RunStagesTestResultsResponse.fromBranchStageViewResponse(result), HttpStatus.OK);
    }

    @Operation(summary = "Get stages and test results for a run by job info and run count")
    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/runcount/{runCount}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/runcount/{runCount}/stage"}, produces = "application/json")
    public ResponseEntity<RunStagesTestResultsResponse> getStagesByJobInfoAndRunCount(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @Parameter(description = "The jobRunCount (build number) identifying the specific run")
            @PathVariable Integer runCount) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        BranchStageViewResponse result = graphService.getRunBranchStageViewResponse(company, org, repo, branch, jobInfoMap, runCount);
        return new ResponseEntity<>(RunStagesTestResultsResponse.fromBranchStageViewResponse(result), HttpStatus.OK);
    }

    @Operation(summary = "Get stages and test results for the latest run of a job")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest",
                produces = "application/json")
    public ResponseEntity<RunStagesTestResultsResponse> getLatestRunStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        Long latestRunId = browseService.getLatestRunId(jobId);
        return getStagesByIds(company, org, repo, branch, jobId, latestRunId);
    }

    @Operation(summary = "Get stages and test results for the latest run (by job info)")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/run/latest",
                produces = "application/json")
    public ResponseEntity<RunStagesTestResultsResponse> getLatestRunStagesFromJobInfo(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        JobPojo job = browseService.getJob(company, org, repo, branch, jobInfoMap);
        Long latestRunId = browseService.getLatestRunId(job.getJobId());
        return getStagesByIds(company, org, repo, branch, job.getJobId(), latestRunId);
    }

    @Operation(summary = "List runs for a specific SHA")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run", produces = "application/json")
    public ResponseEntity<BranchJobsRunsResponse> getRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @Parameter(description = "Filter jobs by info key-value pairs passed as query params (e.g. ?application=myapp&env=prod)")
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        //TODO: filters
        return new ResponseEntity<>(BranchJobsRunsResponse.fromMap(browseService.getBranchJobsRunsForSha(company, org, repo, branch, sha)), HttpStatus.OK);
    }

    @Operation(summary = "Get a specific run by its UUID reference")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}", produces = "application/json")
    public ResponseEntity<RunPojo> getRunForReference(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @Parameter(description = "UUID reference identifying the run")
            @PathVariable UUID runReference,
            @Parameter(description = "Filter by metadata key-value pairs passed as query params")
            @RequestParam(required = false) Map<String, String> metadataFilters) {
        return new ResponseEntity<>(browseService.getRunFromReference(company, org, repo, branch, sha, runReference), HttpStatus.OK);
    }

    @Operation(summary = "Get full test results (suites, cases) for a stage")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}", produces = "application/json")
    public ResponseEntity<StageTestResultModel> getStageTestResultsTestSuites(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage) {
        return new ResponseEntity<>(browseService.getStageTestResultMap(company, org, repo, branch, jobId, runId, stage), HttpStatus.OK);
    }

    @Operation(summary = "Get full test results for a stage by job info and run count")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/runcount/{runCount}/stage/{stage}",
                produces = "application/json")
    public ResponseEntity<StageTestResultModel> getStageTestResultsByJobInfoAndRunCount(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @Parameter(description = "The jobRunCount (build number) identifying the specific run")
            @PathVariable Integer runCount,
            @PathVariable String stage) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        return new ResponseEntity<>(browseService.getStageTestResultMap(company, org, repo, branch, jobInfoMap, runCount, stage), HttpStatus.OK);
    }

    @Operation(summary = "Get full test results for the latest run of a stage")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest/stage/{stage}",
                produces = "application/json")
    public ResponseEntity<StageTestResultModel> getLatestRunStageTestResults(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable String stage) {
        Long latestRunId = browseService.getLatestRunId(jobId);
        return getStageTestResultsTestSuites(company, org, repo, branch, jobId, latestRunId, stage);
    }

    @Operation(summary = "Get full test results for the latest run of a stage (by job info)")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/run/latest/stage/{stage}",
                produces = "application/json")
    public ResponseEntity<StageTestResultModel> getLatestRunStageTestResultsFromJobInfo(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @PathVariable String stage) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        JobPojo job = browseService.getJob(company, org, repo, branch, jobInfoMap);
        Long latestRunId = browseService.getLatestRunId(job.getJobId());
        return getStageTestResultsTestSuites(company, org, repo, branch, job.getJobId(), latestRunId, stage);
    }

    // ==================== Trend Endpoints ====================

    @Operation(summary = "Get test case trend data for a job stage",
               description = "Returns a sparse matrix of test case results across recent runs. "
                       + "The 'runs' map is keyed by jobRunCount (build number) with run metadata. "
                       + "Each testCase entry contains runStates (state -> list of build numbers) and failureMessages (message -> list of build numbers). "
                       + "Use detail=summary to omit per-run breakdown for a compact overview. "
                       + "Use onlyShowFailures=true to filter to tests with successPercent < 100%.")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/stage/{stage}/trend",
                produces = "application/json")
    public ResponseEntity<TestTrendResponse> getJobStageTestTrendJson(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable String stage,
            @Parameter(description = "Start of time range filter (ISO 8601, e.g. 2025-05-01T00:00:00Z). Only runs after this time are included.")
            @RequestParam(required = false) Instant start,
            @Parameter(description = "End of time range filter (ISO 8601, e.g. 2025-05-31T00:00:00Z). Only runs before this time are included.")
            @RequestParam(required = false) Instant end,
            @Parameter(description = "Maximum number of recent runs to include. Halves automatically on packet-too-large errors. Default: 30")
            @RequestParam(required = false, defaultValue = "30") Integer runs,
            @Parameter(description = "Response detail level. 'full' includes runStates and failureMessages per test case. 'summary' omits them for a compact overview. Default: full")
            @RequestParam(required = false, defaultValue = "full") TrendDetail detail,
            @Parameter(description = "When true, only returns test cases with successPercent < 100%. Reduces response size for failure analysis. Default: false")
            @RequestParam(required = false, defaultValue = "false") Boolean onlyShowFailures) {

        JobStageTestTrend jobTestTrend = null;
        for (int i = runs; i > 0; i = i / 2) {
            try {
                jobTestTrend = graphService.getJobStageTestTrend(company, org, repo, branch, jobId, stage, start, end, i);
                break;
            } catch (Exception ex) {
                log.warn("failed trend for jobId: {}, i: {}", jobId, i, ex);
            }
        }

        if (jobTestTrend == null) {
            return ResponseEntity.ok(TestTrendResponse.builder()
                    .runs(Map.of())
                    .testCases(List.of())
                    .summary(TestTrendResponse.TestTrendSummary.builder()
                            .totalTests(0).successCount(0).failCount(0).build())
                    .build());
        }

        TestTrendTable testTrendTable = TestTrendTable.fromJob(jobTestTrend);
        TestTrendResponse response = TestTrendResponse.fromTestTrendTable(testTrendTable);
        return ResponseEntity.ok(response.withFilters(detail, onlyShowFailures));
    }

    @Operation(summary = "Get test case trend data for a job stage (by job info)",
               description = "Same as the /job/{jobId}/stage/{stage}/trend endpoint but resolves the job by its info key-value pairs "
                       + "(e.g. 'application=myapp,pipeline=nightly') instead of numeric jobId. "
                       + "See the jobId variant for full response format documentation.")
    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/stage/{stage}/trend",
                produces = "application/json")
    public ResponseEntity<TestTrendResponse> getJobStageTestTrendFromJobInfoJson(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @Parameter(description = "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')")
            @PathVariable String jobInfo,
            @PathVariable String stage,
            @Parameter(description = "Start of time range filter (ISO 8601, e.g. 2025-05-01T00:00:00Z). Only runs after this time are included.")
            @RequestParam(required = false) Instant start,
            @Parameter(description = "End of time range filter (ISO 8601, e.g. 2025-05-31T00:00:00Z). Only runs before this time are included.")
            @RequestParam(required = false) Instant end,
            @Parameter(description = "Maximum number of recent runs to include. Halves automatically on packet-too-large errors. Default: 30")
            @RequestParam(required = false, defaultValue = "30") Integer runs,
            @Parameter(description = "Response detail level. 'full' includes runStates and failureMessages per test case. 'summary' omits them for a compact overview. Default: full")
            @RequestParam(required = false, defaultValue = "full") TrendDetail detail,
            @Parameter(description = "When true, only returns test cases with successPercent < 100%. Reduces response size for failure analysis. Default: false")
            @RequestParam(required = false, defaultValue = "false") Boolean onlyShowFailures) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        JobPojo job = browseService.getJob(company, org, repo, branch, jobInfoMap);
        return getJobStageTestTrendJson(company, org, repo, branch, job.getJobId(), stage, start, end, runs, detail, onlyShowFailures);
    }

    // ==================== Helper Methods ====================

    Map<String, String> validateJobInfo(String jobInfo) {
        Map<String, String> parsed = StringMapUtil.stringToMap(jobInfo);
        if (parsed.isEmpty() && jobInfo != null && !jobInfo.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid jobInfo format. Expected comma-separated key=value pairs, e.g. 'application=myapp,env=prod'. Got: " + jobInfo);
        }
        return parsed;
    }

    Integer validateDays(Integer days) {
        if (days == null) {
            return null;
        }
        return Math.max(days, 1);
    }

    /**
     * Validates runs parameter. Returns 60 as default for null or invalid values.
     */
    Integer validateRuns(Integer runs) {
        if (runs == null || runs < 1) {
            return 60;
        }
        return runs;
    }

    /**
     * Limits the number of runs per job for branch-level endpoints.
     * Preserves original Map structure while applying run limit.
     */
    private <K> Map<K, Map<JobPojo, Set<RunPojo>>> limitRunsPerJob(
            Map<K, Map<JobPojo, Set<RunPojo>>> input, int maxRuns) {
        Map<K, Map<JobPojo, Set<RunPojo>>> result = new LinkedHashMap<>();
        for (Map.Entry<K, Map<JobPojo, Set<RunPojo>>> outerEntry : input.entrySet()) {
            Map<JobPojo, Set<RunPojo>> limitedJobs = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);
            for (Map.Entry<JobPojo, Set<RunPojo>> jobEntry : outerEntry.getValue().entrySet()) {
                Set<RunPojo> limitedRuns = jobEntry.getValue().stream()
                    .sorted(PojoComparators.RUN_DESCENDING)
                    .limit(maxRuns)
                    .collect(Collectors.toCollection(() ->
                        new TreeSet<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER)));
                limitedJobs.put(jobEntry.getKey(), limitedRuns);
            }
            result.put(outerEntry.getKey(), limitedJobs);
        }
        return result;
    }

    /**
     * Limits the number of runs for job-level endpoints.
     * Preserves original Map structure while applying run limit.
     */
    private Map<JobPojo, Map<RunPojo, Set<StagePojo>>> limitRunsInJob(
            Map<JobPojo, Map<RunPojo, Set<StagePojo>>> input, int maxRuns) {
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> result = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);
        for (Map.Entry<JobPojo, Map<RunPojo, Set<StagePojo>>> jobEntry : input.entrySet()) {
            Map<RunPojo, Set<StagePojo>> limitedRuns = jobEntry.getValue().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(PojoComparators.RUN_DESCENDING))
                .limit(maxRuns)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    () -> new TreeMap<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER)));
            result.put(jobEntry.getKey(), limitedRuns);
        }
        return result;
    }
}