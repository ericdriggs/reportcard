package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.controller.html.TestResultHtmlHelper;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("")
@SuppressWarnings("unused")
public class BrowseUIController {

    private final BrowseService browseService;
    private final GraphService graphService;

    @Autowired
    public BrowseUIController(BrowseService browseService, GraphService graphService) {
        this.browseService = browseService;
        this.graphService = graphService;
    }

    @GetMapping(path = {"", "company"}, produces = "text/html")
    public ResponseEntity<String> getCompanies() {
        return new ResponseEntity<>(BrowseHtmlHelper.getCompaniesHtml(), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "text/html")
    public ResponseEntity<String> getCompanyOrgs(@PathVariable String company) {
        return new ResponseEntity<>(BrowseHtmlHelper.getCompanyHtml(company), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}", "org/{org}/repo"}, produces = "text/html")
    public ResponseEntity<String> getRepoBranches(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(BrowseHtmlHelper.getOrgHtml(company, org), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "org/{org}/repo/{repo}/branch"}, produces = "text/html")
    public ResponseEntity<String> getBranchJobs(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo) {
        return new ResponseEntity<>(BrowseHtmlHelper.getRepoHtml(company, org, repo), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "text/html")
    public ResponseEntity<String> getJobRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        BranchStageViewResponse branchStageViewResponse  = browseService.getStageViewForBranch(company, org, repo, branch);
        BranchJobLatestRunMap branchJobLatestRunMap = graphService.getBranchJobLatestRunMap(company, org, repo, branch);
        return new ResponseEntity<>(BrowseHtmlHelper.getBranchHtml(company, org, repo, branch, branchStageViewResponse, branchJobLatestRunMap), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "text/html")
    public ResponseEntity<String> getRunStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        BranchStageViewResponse branchStageViewResponse  = browseService.getStageViewForJob(company, org, repo, branch, jobId);
        return new ResponseEntity<>(BrowseHtmlHelper.getJobHtml(company, org, repo, branch, jobId, branchStageViewResponse), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage"}, produces = "text/html")
    public ResponseEntity<String> getStageTestResults(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId) {
        return new ResponseEntity<>(BrowseHtmlHelper.getRunHtml(company, org, repo, branch, jobId, runId), HttpStatus.OK);
    }


    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}"}, produces = "text/html")
    public ResponseEntity<String> getTestResult(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage) {
        StageTestResultModel stageTestResultModel = browseService.getStageTestResultMap(company, org, repo, branch , jobId, runId, stage);
        return new ResponseEntity<>(TestResultHtmlHelper.getTestResult(stageTestResultModel.getTestResult()), HttpStatus.OK);
    }

}