package com.ericdriggs.reportcard.controller;

import java.util.List;

import com.ericdriggs.reportcard.ReportcardService;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import com.ericdriggs.reportcard.db.tables.pojos.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JsonController {

    private final ReportcardService reportCardService;

    @Autowired
    public JsonController(ReportcardService reportCardService) {
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
    public ResponseEntity<Repo> getRepo(@RequestParam String org, @RequestParam String repo) {
        return new ResponseEntity<>(reportCardService.getRepo(org, repo), HttpStatus.OK);
    }
}