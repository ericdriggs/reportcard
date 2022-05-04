package io.github.ericdriggs.reportcard.controller;

import java.util.Map;
import java.util.Set;

import io.github.ericdriggs.reportcard.ReportCardService;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.HostApplicationPipeline;
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

    @GetMapping(path = "{org}", produces = "application/json")
    public ResponseEntity<Map<Org,Set<Repo>>> getOrg(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getOrgRepos(org), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos", produces = "application/json")
    public ResponseEntity<Map<Repo,Set<Branch>>> getRepos(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getReposBranches(org), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}", produces = "application/json")
    public ResponseEntity<Map<Repo,Set<Branch>>> getRepo(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getRepoBranches(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches", produces = "application/json")
    public ResponseEntity<Map<Branch,Set<Sha>>> getBranches(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getBranchesShas(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}", produces = "application/json")
    public ResponseEntity<Map<Branch,Set<Sha>>> getBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getBranchShas(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas", produces = "application/json")
    public ResponseEntity<Map<Sha, Set<Context>>> getShas(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getShasContexts(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}", produces = "application/json")
    public ResponseEntity<Map<Sha, Set<Context>>> getSha(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch, @PathVariable String sha) {
        return new ResponseEntity<>(reportCardService.getShaContexts(org, repo, branch, sha), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts", produces = "application/json")
    public ResponseEntity<Map<Context,Set<Execution>>> getContexts(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch, @PathVariable String sha) {
        return new ResponseEntity<>(reportCardService.getContextsExecutions(org, repo, branch, sha), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}", produces = "application/json")
    public ResponseEntity<Map<Context,Set<Execution>>> getContext (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getContextExecutions(org, repo, branch, sha, hostApplicationPipeline), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions", produces = "application/json")
    public ResponseEntity<Map<Execution,Set<Stage>>> getExecutions (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getExecutionsStages(org, repo, branch, sha, hostApplicationPipeline), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions/{externalExecutionId}", produces = "application/json")
    public ResponseEntity<Map<Execution,Set<Stage>>> getExecution (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @PathVariable String externalExecutionId,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getExecutionStages(org, repo, branch, sha, hostApplicationPipeline, externalExecutionId), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions/{executionName}/stages", produces = "application/json")
    public ResponseEntity<Map<Stage,Set<TestResult>>> getStages (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @PathVariable String executionName,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getStagesTestResults(org, repo, branch, sha, hostApplicationPipeline, executionName), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions/{executionName}/stages/{stage}", produces = "application/json")
    public ResponseEntity<Map<Stage,Set<TestResult>>> getStage (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @PathVariable String executionName,
            @PathVariable String stage,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getStageTestResults(org, repo, branch, sha, hostApplicationPipeline, executionName, stage), HttpStatus.OK);
    }

}