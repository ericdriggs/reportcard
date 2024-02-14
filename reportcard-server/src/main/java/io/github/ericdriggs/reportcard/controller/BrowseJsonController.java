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
@RequestMapping("/v1/api")
@SuppressWarnings("unused")
public class BrowseJsonController {

    private final BrowseService browseService;

    @Autowired
    public BrowseJsonController(BrowseService browseService) {
        this.browseService = browseService;
    }

    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Map<Company, Set<Org>>> getCompanyOrgs() {
        return new ResponseEntity<>(CompanyOrgsCache.INSTANCE.getCache(), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
    public ResponseEntity<Map<Company, Map<Org, Set<Repo>>>> getCompanyOrgsRepos(@PathVariable String company) {
        return new ResponseEntity<>(CompanyOrgsReposCacheMap.INSTANCE.getValue(new CompanyName(company)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<Map<Org, Map<Repo, Set<Branch>>>> getOrgReposBranches(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesCacheMap.INSTANCE.getValue(CompanyOrg.builder().company(company).org(org).build()), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
    public ResponseEntity<Map<Repo, Map<Branch, Set<Job>>>> getRepoBranchesJobs(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo) {
        return new ResponseEntity<>(RepoBranchesJobsCacheMap.INSTANCE.getValue(CompanyOrgRepo.builder().company(company).org(org).repo(repo).build()), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
    public ResponseEntity<Map<Branch, Map<Job, Set<Run>>>> getBranchJobsRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        return new ResponseEntity<>(BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranch(company, org, repo, branch)), HttpStatus.OK);
        //TODO: use jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "application/json")
    public ResponseEntity<Map<Job, Map<Run, Set<Stage>>>> getJobRunsStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        return new ResponseEntity<>(JobRunsStagesCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJob(company, org, repo, branch, jobId)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage"}, produces = "application/json")
    public ResponseEntity<Map<Run, Map<Stage, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult>>>> getStagesByIds(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId) {
        return new ResponseEntity<>(RunStagesTestResultsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobRun(company, org, repo, branch, jobId, runId)), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}", produces = "application/json")
    public ResponseEntity<Map<Stage, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>>>> getStageTestResultsTestSuites(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage) {
        return new ResponseEntity<>(browseService.getStageTestResultsTestSuites(company, org, repo, branch, jobId, runId, stage), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run", produces = "application/json")
    public ResponseEntity<Map<Branch, Map<Job, Set<Run>>>> getRuns(
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
    public ResponseEntity<Run> getRunForReference(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String runReference,
            @RequestParam(required = false) Map<String, String> metadataFilters) {
        return new ResponseEntity<>(browseService.getRunFromReference(company, org, repo, branch, sha, runReference), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stages/{stage}", produces = "application/json")
    public ResponseEntity<Map<Stage, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>>>> getStage(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage,
            @RequestParam(required = false) Map<String, String> metadataFilters) {
        return new ResponseEntity<>(browseService.getStageTestResultsTestSuites(company, org, repo, branch, jobId, runId, stage), HttpStatus.OK);
    }

//    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}/stages", produces = "application/json")
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