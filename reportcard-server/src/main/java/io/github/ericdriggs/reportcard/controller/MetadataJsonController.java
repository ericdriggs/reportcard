package io.github.ericdriggs.reportcard.controller;

import java.util.Map;
import java.util.Set;

import io.github.ericdriggs.reportcard.ReportCardService;
import io.github.ericdriggs.reportcard.cache.dto.OrgName;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepo;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranch;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJob;
import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: add reports endpoint after stages
//TODO: return not only current level but level below in all results (e.g if ask for org, list all of its repos)
@RestController
@RequestMapping("/api/v1/orgs")
@SuppressWarnings("unused")
public class MetadataJsonController {

    private final ReportCardService reportCardService;

    @Autowired
    public MetadataJsonController(ReportCardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Map<Org, Set<Repo>>> getOrgsRepos() {
        return new ResponseEntity<>(OrgsReposCache.INSTANCE.getCache(), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}", "org/{org}/repo"}, produces = "application/json")
    public ResponseEntity<Map<Org,Map<Repo,Set<Branch>>>> getOrgReposBranches(@PathVariable String org) {
        return new ResponseEntity<>(OrgReposBranchesCacheMap.INSTANCE.getValue(new OrgName(org)), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "application/json")
    public ResponseEntity<Map<Repo,Map<Branch,Set<Job>>>> getRepoBranchesJobs(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(RepoBranchesJobsCacheMap.INSTANCE.getValue(new OrgRepo(org, repo)), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}/branch/{branch}", "org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "application/json")
    public ResponseEntity<Map<Branch,Map<Job,Set<Execution>>>> getBranchJobsExecutions(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) Map<String,String> jobInfoFilters) {
        return new ResponseEntity<>(BranchJobsExecutionsCacheMap.INSTANCE.getValue(new OrgRepoBranch(org, repo, branch)), HttpStatus.OK);
        //TODO: use jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = {"org/{org}/repo/{repo}/branch/{branch}/job/{jobId}", "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution"}, produces = "application/json")
    public ResponseEntity<Map<Job,Map<Execution,Set<Stage>>>> getJobExecutionsStages(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        return new ResponseEntity<>(JobExecutionsStagesCacheMap.INSTANCE.getValue(new OrgRepoBranchJob(org, repo, branch, jobId)), HttpStatus.OK);
    }


    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{executionId}/stage", produces = "application/json")
    public ResponseEntity<Set<Stage>> getStagesByIds(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long executionId) {
        return new ResponseEntity<>(reportCardService.getStagesFromIds(org, repo, branch, jobId, executionId), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{exectionId}/stage/{stage}", produces = "application/json")
    public ResponseEntity<Stage> getStageByIds(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long executionId,
            @PathVariable String stage) {
        return new ResponseEntity<>(reportCardService.getStageFromIds(org, repo, branch, jobId, executionId, stage), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/executions", produces = "application/json")
    public ResponseEntity<Set<Execution>> getExecutions (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @RequestParam(required = false) Map<String,String> jobInfoFilters) {
        return new ResponseEntity<>(reportCardService.getExecutionsForSha(org, repo, branch, sha, jobInfoFilters), HttpStatus.OK);
    }


    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/contexts/executions/{executionReference}", produces = "application/json")
    public ResponseEntity<Execution> getExecutionForReference (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String executionReference,
            @RequestParam(required = false) Map<String,String> metadataFilters) {
        return new ResponseEntity<>(reportCardService.getExecutionFromReference(org, repo, branch, sha, executionReference), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/contexts/executions/{executionReference}/stages", produces = "application/json")
    public ResponseEntity<Map<Stage,Set<TestResult>>> getStages (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String executionReference,
            @RequestParam(required = false) Map<String,String> metadataFilters) {
        return new ResponseEntity<>(reportCardService.getStagesTestResults(org, repo, branch, sha, executionReference), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/execution/{executionReference}/stages/{stage}", produces = "application/json")
    public ResponseEntity<Map<Stage,Set<TestResult>>> getStage (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String executionReference,
            @PathVariable String stage,
            @RequestParam(required = false) Map<String,String> metadataFilters) {
        return new ResponseEntity<>(reportCardService.getStageTestResults(org, repo, branch, sha, executionReference, stage), HttpStatus.OK);
    }

    //TODO: getStagesFor

}