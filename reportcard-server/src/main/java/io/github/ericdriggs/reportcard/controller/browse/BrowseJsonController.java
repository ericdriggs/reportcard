package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.controller.browse.response.CompanyOrgsReposResponse;
import io.github.ericdriggs.reportcard.controller.browse.response.CompanyOrgsResponse;
import io.github.ericdriggs.reportcard.controller.browse.response.OrgReposBranchesResponse;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.persist.BrowseService;
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
@RequestMapping("/v1/api")
@SuppressWarnings("unused")
public class BrowseJsonController {

    private final BrowseService browseService;

    @Autowired
    public BrowseJsonController(BrowseService browseService) {
        this.browseService = browseService;
    }

    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<CompanyOrgsResponse> getCompanyOrgs() {
        return new ResponseEntity<>(CompanyOrgsResponse.fromMap(CompanyOrgsCache.INSTANCE.getCache()), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
    public ResponseEntity<CompanyOrgsReposResponse> getCompanyOrgsRepos(@PathVariable String company) {
        return new ResponseEntity<>(CompanyOrgsReposResponse.fromMap(CompanyOrgsReposCacheMap.INSTANCE.getValue(new CompanyDTO(company))), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<OrgReposBranchesResponse> getOrgReposBranches(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesResponse.fromMap(OrgReposBranchesCacheMap.INSTANCE.getValue(CompanyOrgDTO.builder().company(company).org(org).build())), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
    public ResponseEntity<Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>>> getRepoBranchesJobs(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo) {
        return new ResponseEntity<>(RepoBranchesJobsCacheMap.INSTANCE.getValue(CompanyOrgRepoDTO.builder().company(company).org(org).repo(repo).build()), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
    public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getBranchJobsRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false, defaultValue = "60") Integer runs,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        runs = validateRuns(runs);
        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> fullResult =
            BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchDTO(company, org, repo, branch));
        return new ResponseEntity<>(limitRunsPerJob(fullResult, runs), HttpStatus.OK);
        //TODO: use jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "application/json")
    public ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> getJobRunsStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @RequestParam(required = false, defaultValue = "60") Integer runs) {
        runs = validateRuns(runs);
        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> fullResult =
            JobRunsStagesCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobDTO(company, org, repo, branch, jobId));
        return new ResponseEntity<>(limitRunsInJob(fullResult, runs), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage"}, produces = "application/json")
    public ResponseEntity<Map<RunPojo, Map<StagePojo, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo>>>> getStagesByIds(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId) {
        return new ResponseEntity<>(RunStagesTestResultsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobRunDTO(company, org, repo, branch, jobId, runId)), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest",
                produces = "application/json")
    public ResponseEntity<Map<RunPojo, Map<StagePojo, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo>>>> getLatestRunStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        Long latestRunId = browseService.getLatestRunId(jobId);
        return getStagesByIds(company, org, repo, branch, jobId, latestRunId);
    }

    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run", produces = "application/json")
    public ResponseEntity<Map<BranchPojo, Map<JobPojo, Set<RunPojo>>>> getRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        //TODO: filters
        return new ResponseEntity<>(browseService.getBranchJobsRunsForSha(company, org, repo, branch, sha), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}", produces = "application/json")
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