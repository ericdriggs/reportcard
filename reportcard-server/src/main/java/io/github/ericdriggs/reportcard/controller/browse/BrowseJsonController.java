package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.cache.model.BranchStageViewResponse;
import io.github.ericdriggs.reportcard.controller.browse.response.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO: add reports endpoint after stages
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

    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<CompanyOrgsResponse> getCompanyOrgs() {
        return new ResponseEntity<>(CompanyOrgsResponse.fromMap(browseService.getCompanyOrgs()), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
    public ResponseEntity<CompanyOrgsReposResponse> getCompanyOrgsRepos(@PathVariable String company) {
        return new ResponseEntity<>(CompanyOrgsReposResponse.fromMap(browseService.getCompanyOrgsRepos(company)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<OrgReposBranchesResponse> getOrgReposBranches(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesResponse.fromMap(browseService.getOrgReposBranches(company, org)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
    public ResponseEntity<RepoBranchesJobsResponse> getRepoBranchesJobs(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo) {
        return new ResponseEntity<>(RepoBranchesJobsResponse.fromMap(browseService.getRepoBranchesJobs(company, org, repo)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
    public ResponseEntity<BranchJobsRunsResponse> getBranchJobsRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false, defaultValue = "60") Integer runs,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        runs = validateRuns(runs);
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult =
            browseService.getBranchJobsRuns(company, org, repo, branch, jobInfoFilters);
        return new ResponseEntity<>(BranchJobsRunsResponse.fromMap(limitRunsPerJob(fullResult, runs)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "application/json")
    public ResponseEntity<JobRunsStagesResponse> getJobRunsStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @RequestParam(required = false, defaultValue = "60") Integer runs) {
        runs = validateRuns(runs);
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> fullResult =
            browseService.getJobRunsStages(company, org, repo, branch, jobId);
        return new ResponseEntity<>(JobRunsStagesResponse.fromMap(limitRunsInJob(fullResult, runs)), HttpStatus.OK);
    }

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

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run", produces = "application/json")
    public ResponseEntity<BranchJobsRunsResponse> getRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        //TODO: filters
        return new ResponseEntity<>(BranchJobsRunsResponse.fromMap(browseService.getBranchJobsRunsForSha(company, org, repo, branch, sha)), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}", produces = "application/json")
    public ResponseEntity<RunPojo> getRunForReference(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable UUID runReference,
            @RequestParam(required = false) Map<String, String> metadataFilters) {
        return new ResponseEntity<>(browseService.getRunFromReference(company, org, repo, branch, sha, runReference), HttpStatus.OK);
    }

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

    // ==================== Helper Methods ====================

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