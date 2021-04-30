package com.ericdriggs.reportcard.controller;

import java.util.List;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.gen.db.tables.pojos.*;
import com.ericdriggs.reportcard.model.HostApplicationPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Org>> getOrgs(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getOrgs(), HttpStatus.OK);
    }

    @GetMapping(path = "{org}", produces = "application/json")
    public ResponseEntity<Org> getOrg(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getOrg(org), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos", produces = "application/json")
    public ResponseEntity<List<Repo>> getRepos(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getRepos(org), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}", produces = "application/json")
    public ResponseEntity<Repo> getRepo(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getRepo(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches", produces = "application/json")
    public ResponseEntity<List<Branch>> getBranches(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getBranches(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}", produces = "application/json")
    public ResponseEntity<Branch> getBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getBranch(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas", produces = "application/json")
    public ResponseEntity<List<Sha>> getShas(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getShas(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}", produces = "application/json")
    public ResponseEntity<Sha> getSha(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch, @PathVariable String sha) {
        return new ResponseEntity<>(reportCardService.getSha(org, repo, branch, sha), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts", produces = "application/json")
    public ResponseEntity<List<Context>> getContexts(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch, @PathVariable String sha) {
        return new ResponseEntity<>(reportCardService.getContexts(org, repo, branch, sha), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}", produces = "application/json")
    public ResponseEntity<Context> getContext (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getContext(org, repo, branch, sha, hostApplicationPipeline), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions", produces = "application/json")
    public ResponseEntity<List<Execution>> getExecutions (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getExecutions(org, repo, branch, sha, hostApplicationPipeline), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/execution/{executionName}", produces = "application/json")
    public ResponseEntity<Execution> getExecution (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @PathVariable String executionName,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getExecution(org, repo, branch, sha, hostApplicationPipeline, executionName), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/execution/{executionName}/stages", produces = "application/json")
    public ResponseEntity<List<Stage>> getStages (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @PathVariable String executionName,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getStages(org, repo, branch, sha, hostApplicationPipeline, executionName), HttpStatus.OK);
    }

    @GetMapping(path = "{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/execution/{executionName}/stages/{stage}", produces = "application/json")
    public ResponseEntity<Stage> getStage (
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @PathVariable String host,
            @PathVariable String executionName,
            @PathVariable String stage,
            @RequestParam String application,  @RequestParam String pipeline) {
        HostApplicationPipeline hostApplicationPipeline  = new HostApplicationPipeline(host, application,  pipeline);
        return new ResponseEntity<>(reportCardService.getStage(org, repo, branch, sha, hostApplicationPipeline, executionName, stage), HttpStatus.OK);
    }


}