package io.github.ericdriggs.reportcard.controller;

import java.util.Map;
import java.util.Set;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: add reports endpoint after stages
@RestController
@RequestMapping("/v1/api")
@Hidden
@SuppressWarnings("unused")
public class BrowseJsonController {

    private final BrowseService browseService;

    @Autowired
    public BrowseJsonController(BrowseService browseService) {
        this.browseService = browseService;
    }

    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> getCompanyOrgs() {
        return new ResponseEntity<>(CompanyOrgsCache.INSTANCE.getCache(), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "application/json")
    public ResponseEntity<Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>> getCompanyOrgsRepos(@PathVariable String company) {
        return new ResponseEntity<>(CompanyOrgsReposCacheMap.INSTANCE.getValue(new CompanyDTO(company)), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>>> getOrgReposBranches(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesCacheMap.INSTANCE.getValue(CompanyOrgDTO.builder().company(company).org(org).build()), HttpStatus.OK);
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
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        return new ResponseEntity<>(BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchDTO(company, org, repo, branch)), HttpStatus.OK);
        //TODO: use jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "application/json")
    public ResponseEntity<Map<JobPojo, Map<RunPojo, Set<StagePojo>>>> getJobRunsStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        return new ResponseEntity<>(JobRunsStagesCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobDTO(company, org, repo, branch, jobId)), HttpStatus.OK);
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
            @PathVariable String runReference,
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

//    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}/stages", produces = "application/json")
//    public ResponseEntity<Map<StagePojo,Set<TestResultPojo>>> getStages (
//            @PathVariable String org,
//            @PathVariable String repo,
//            @PathVariable String branch,
//            @PathVariable String sha,
//            @PathVariable String runReference,
//            @RequestParam(required = false) Map<String,String> metadataFilters) {
//        return new ResponseEntity<>(reportCardService.getStageTestResults(org, repo, branch, sha, runReference), HttpStatus.OK);
//    }

}