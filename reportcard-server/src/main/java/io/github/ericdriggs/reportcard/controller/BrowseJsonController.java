package io.github.ericdriggs.reportcard.controller;

import java.util.Map;
import java.util.Set;

import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: add reports endpoint after stages
@RestController
@RequestMapping("/api/v1/orgs")
@SuppressWarnings("unused")
public class BrowseJsonController {

    private final BrowseService browseService;

    @Autowired
    public BrowseJsonController(BrowseService browseService) {
        this.browseService = browseService;
    }

    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Map<Org, Set<Repo>>> getOrgsRepos() {
        return new ResponseEntity<>(OrgsReposCache.INSTANCE.getCache(), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<Map<Org, Map<Repo, Set<Branch>>>> getOrgReposBranches(@PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesCacheMap.INSTANCE.getValue(new OrgName(org)), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
    public ResponseEntity<Map<Repo, Map<Branch, Set<Job>>>> getRepoBranchesJobs(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(RepoBranchesJobsCacheMap.INSTANCE.getValue(new OrgRepo(org, repo)), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}/branch/{branch}",
            "org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
    public ResponseEntity<Map<Branch, Map<Job, Set<Run>>>> getBranchJobsRuns(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        return new ResponseEntity<>(BranchJobsRunsCacheMap.INSTANCE.getValue(new OrgRepoBranch(org, repo, branch)), HttpStatus.OK);
        //TODO: use jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution"}, produces = "application/json")
    public ResponseEntity<Map<Job, Map<Run, Set<Stage>>>> getJobRunsStages(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        return new ResponseEntity<>(JobRunsStagesCacheMap.INSTANCE.getValue(new OrgRepoBranchJob(org, repo, branch, jobId)), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{runId}",
            "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{runId}/stage"}, produces = "application/json")
    public ResponseEntity<Map<Run, Map<Stage, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult>>>> getStagesByIds(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId) {
        return new ResponseEntity<>(RunStagesTestResultsCacheMap.INSTANCE.getValue(new OrgRepoBranchJobRun(org, repo, branch, jobId, runId)), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{runId}/stage/{stage}", produces = "application/json")
    public ResponseEntity<Map<Stage, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>>>> getStageTestResultsTestSuites(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage) {
        return new ResponseEntity<>(browseService.getStageTestResultsTestSuites(org, repo, branch, jobId, runId, stage), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/executions", produces = "application/json")
    public ResponseEntity<Map<Branch, Map<Job, Set<Run>>>> getRuns(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        //TODO: filters
        return new ResponseEntity<>(browseService.getBranchJobsRunsForSha(org, repo, branch, sha), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/contexts/executions/{runReference}", produces = "application/json")
    public ResponseEntity<Run> getRunForReference(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String runReference,
            @RequestParam(required = false) Map<String, String> metadataFilters) {
        return new ResponseEntity<>(browseService.getRunFromReference(org, repo, branch, sha, runReference), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{runId}/stages/{stage}", produces = "application/json")
    public ResponseEntity<Map<Stage, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>>>> getStage(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage,
            @RequestParam(required = false) Map<String, String> metadataFilters) {
        return new ResponseEntity<>(browseService.getStageTestResultsTestSuites(org, repo, branch, jobId, runId, stage), HttpStatus.OK);
    }

//    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/contexts/executions/{runReference}/stages", produces = "application/json")
//    public ResponseEntity<Map<Stage,Set<TestResult>>> getStages (
//            @PathVariable String org,
//            @PathVariable String repo,
//            @PathVariable String branch,
//            @PathVariable String sha,
//            @PathVariable String runReference,
//            @RequestParam(required = false) Map<String,String> metadataFilters) {
//        return new ResponseEntity<>(reportCardService.getStageTestResults(org, repo, branch, sha, runReference), HttpStatus.OK);
//    }

}