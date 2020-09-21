package com.ericdriggs.reportcard.controller;

import java.util.List;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.db.tables.pojos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JsonController {

    private final ReportCardService reportCardService;

    @Autowired
    public JsonController(ReportCardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    @GetMapping(path = "/orgs", produces = "application/json")
    public ResponseEntity<List<Org>> getOrgs(@PathVariable String org) {
        return new ResponseEntity<List<Org>>(reportCardService.getOrgs(), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}", produces = "application/json")
    public ResponseEntity<Org> getOrg(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getOrg(org), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}/repos", produces = "application/json")
    public ResponseEntity<List<Repo>> getRepos(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getRepos(org), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}/repos/{repo}", produces = "application/json")
    public ResponseEntity<Repo> getRepo(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getRepo(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}/repos/{repo}/apps", produces = "application/json")
    public ResponseEntity<List<App>> getApps(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getApps(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}/repos/{repo}/apps/{app}", produces = "application/json")
    public ResponseEntity<App> getApp(
            @PathVariable String org, @PathVariable String repo, @PathVariable String app) {
        return new ResponseEntity<>(reportCardService.getApp(org, repo, app), HttpStatus.OK);
    }


    @GetMapping(path = "/orgs/{org}/repos/{repo}/branches", produces = "application/json")
    public ResponseEntity<List<Branch>> getBranches(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getBranches(org, repo), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}/repos/{repo}/branches/{branch}", produces = "application/json")
    public ResponseEntity<Branch> getBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getBranch(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(
            path = {"/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}",
                    "/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}"},
            produces = "application/json")
    public ResponseEntity<AppBranch> getAppBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String app, @PathVariable String branch) {
        return new ResponseEntity<AppBranch>(reportCardService.getAppBranch(org, repo, app, branch), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds", produces = "application/json")
    public ResponseEntity<List<Build>> getBuilds(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String app,
            @PathVariable String branch
    ) {
        return new ResponseEntity<>(reportCardService.getBuilds(org, repo, app, branch), HttpStatus.OK);
    }

    //http://localhost:8080/api/orgs/default/repos/default/apps/app1/branches/master/builds/1/
    @GetMapping(path = {"/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal}",
            "/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal}"},
            produces = "application/json")
    public ResponseEntity<Build> getBuild(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String app,
            @PathVariable String branch,
            @PathVariable String buildUniqueString

    ) {
        return new ResponseEntity<>(reportCardService.getBuild(org, repo, app, branch, buildUniqueString), HttpStatus.OK);
    }

    @GetMapping(path = {"/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages",
            "/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages"},
            produces = "application/json")
    public ResponseEntity<List<Stage>> getStages(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String app,
            @PathVariable String branch
    ) {
        return new ResponseEntity<>(reportCardService.getStages(org, repo, app, branch), HttpStatus.OK);
    }

    //http://localhost:8080/api/orgs/default/repos/default/apps/app1/branches/master/builds/1/
    @GetMapping(path = {"/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage}",
            "/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage}"},
            produces = "application/json")
    public ResponseEntity<Stage> getStage(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String app,
            @PathVariable String branch,
            @PathVariable String stage

    ) {
        return new ResponseEntity<>(reportCardService.getStage(org, repo, app, branch, stage), HttpStatus.OK);
    }

    //http://localhost:8080/api/orgs/default/repos/default/apps/app1/branches/master/builds/1/
    @GetMapping(path = {
            "/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal}/stages/{stage}",
            "/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal}/stages/{stage}",
            "/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage}/builds/{buildOrdinal}",
            "/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage}/builds/{buildOrdinal}"
    }, produces = "application/json")
    public ResponseEntity<Stage> getStage(
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String app,
            @PathVariable String branch,
            @PathVariable String buildUniqueString,
            @PathVariable String stage

    ) {
        return new ResponseEntity<>(reportCardService.getBuildStage(org, repo, app, branch, buildUniqueString, stage), HttpStatus.OK);
    }

}