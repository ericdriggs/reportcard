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

    @GetMapping(path ="/orgs", produces = "application/json")
    public ResponseEntity<List<Org>> getOrgs(@PathVariable String org) {
        return new ResponseEntity<List<Org>>(reportCardService.getOrgs(), HttpStatus.OK);
    }

    @GetMapping(path = "/orgs/{org}", produces = "application/json")
    public ResponseEntity<Org> getOrg(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getOrg(org), HttpStatus.OK);
    }

    @GetMapping(path ="/orgs/{org}/repos", produces = "application/json")
    public ResponseEntity<List<Repo>> getRepos(@PathVariable String org) {
        return new ResponseEntity<>(reportCardService.getRepos(org), HttpStatus.OK);
    }

    @GetMapping(path ="/orgs/{org}/repos/{repo}", produces = "application/json")
    public ResponseEntity<Repo> getRepo(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getRepo(org, repo), HttpStatus.OK);
    }

    @GetMapping(path ="/orgs/{org}/repos/{repo}/apps", produces = "application/json")
    public ResponseEntity<List<App>> getApps(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getApps(org, repo), HttpStatus.OK);
    }

    @GetMapping(path ="/orgs/{org}/repos/{repo}/apps/{app}", produces = "application/json")
    public ResponseEntity<App> getApp(
            @PathVariable String org, @PathVariable String repo, @PathVariable String app) {
        return new ResponseEntity<>(reportCardService.getApp(org, repo, app), HttpStatus.OK);
    }


    @GetMapping(path ="/orgs/{org}/repos/{repo}/branches", produces = "application/json")
    public ResponseEntity<List<Branch>> getBranches(@PathVariable String org, @PathVariable String repo) {
        return new ResponseEntity<>(reportCardService.getBranches(org, repo), HttpStatus.OK);
    }

    @GetMapping(path ="/orgs/{org}/repos/{repo}/branches/{branch}", produces = "application/json")
    public ResponseEntity<Branch> getBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String branch) {
        return new ResponseEntity<>(reportCardService.getBranch(org, repo, branch), HttpStatus.OK);
    }

    @GetMapping(path ="/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}", produces = "application/json")
    public ResponseEntity<AppBranch> getAppBranch(
            @PathVariable String org, @PathVariable String repo, @PathVariable String app, @PathVariable String branch) {
        return new ResponseEntity<AppBranch>(reportCardService.getAppBranch(org, repo, app, branch), HttpStatus.OK);
    }

}