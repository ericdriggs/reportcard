package io.github.ericdriggs.reportcard.controller;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import io.github.ericdriggs.reportcard.ReportCardService;
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
    public ResponseEntity<Map<Org, Set<Repo>>> getOrgs() {
        return new ResponseEntity<>(reportCardService.getOrgsRepos(), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}", produces = "application/json")
    public ResponseEntity<Map<Org,Set<Repo>>> getOrg(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getOrgRepos(org), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo", produces = "application/json")
    public ResponseEntity<Map<Repo,Set<Branch>>> getRepos(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getReposBranches(org), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}", produces = "application/json")
    public ResponseEntity<Map<Repo,Set<Branch>>> getRepo(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getRepoBranches(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch", produces = "application/json")
    public ResponseEntity<Set<Branch>> getBranches(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getBranches(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}", produces = "application/json")
    public ResponseEntity<Branch> getBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getBranch(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job", produces = "application/json")
    public ResponseEntity<Set<Job>> getJobs(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) Map<String,String> jobInfoFilters) {
        return new ResponseEntity<>(reportCardService.getJobs(org, repo, branch, jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}", produces = "application/json")
    public ResponseEntity<Job> getJob(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        return new ResponseEntity<>(reportCardService.getJob(org, repo, branch, jobId), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution", produces = "application/json")
    public ResponseEntity<Set<Execution>> getExecutions(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @RequestParam(required = false) Map<String,String> jobInfoFilters) {
        return new ResponseEntity<>(reportCardService.getExecutions(org, repo, branch, jobId,  jobInfoFilters), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{exectionId}", produces = "application/json")
    public ResponseEntity<Execution> getExecution(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long executionId) {
        return new ResponseEntity<>(reportCardService.getExecution(org, repo, branch, jobId, executionId), HttpStatus.OK);
    }

    @GetMapping(path = "org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/execution/{exectionId}/stage", produces = "application/json")
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