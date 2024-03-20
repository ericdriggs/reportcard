package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.cache.model.util.TestResultConverterUtil;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StageTestResult;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCasePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuitePojo;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//TODO: add reports endpoint after stages
@RestController
@RequestMapping("")
@SuppressWarnings("unused")
public class BrowseUIController {

    private final BrowseService browseService;

    @Autowired
    public BrowseUIController(BrowseService browseService) {
        this.browseService = browseService;
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
        return new ResponseEntity<>(BrowseHtmlHelper.getBranchHtml(company, org, repo, branch, branchStageViewResponse), HttpStatus.OK);
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


    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}"}, produces = "text/html")
    public ResponseEntity<String> getTestResult(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId,
            @PathVariable String stage) {
        Map<StageTestResult, Map<TestSuitePojo, Map<TestCasePojo, List<TestCaseFaultPojo>>>> stageTestResultMap = browseService.getStageTestResultMap(company, org, repo, branch , jobId, runId, stage);
        TestResult testResult = TestResultConverterUtil.fromStageTestResultMap(stageTestResultMap);
        return new ResponseEntity<>(TestResultHtmlHelper.getTestResult(testResult), HttpStatus.OK);
    }

//
//    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}", produces = "text/html")
//    public ResponseEntity<Map<Stage, Map<TestResult, Set<TestSuite>>>> getStageTestResultsTestSuites(
//            @PathVariable String company,
//            @PathVariable String org,
//            @PathVariable String repo,
//            @PathVariable String branch,
//            @PathVariable Long jobId,
//            @PathVariable Long runId,
//            @PathVariable String stage) {
//        return new ResponseEntity<>(browseService.getStageTestResultsTestSuites(company, org, repo, branch, jobId, runId, stage), HttpStatus.OK);
//    }
//
//    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run", produces = "text/html")
//    public ResponseEntity<Map<Branch, Map<Job, Set<Run>>>> getRuns(
//            @PathVariable String company,
//            @PathVariable String org,
//            @PathVariable String repo,
//            @PathVariable String branch,
//            @PathVariable String sha,
//            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
//        //TODO: filters
//        return new ResponseEntity<>(browseService.getBranchJobsRunsForSha(company, org, repo, branch, sha), HttpStatus.OK);
//    }
//
//    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}", produces = "text/html")
//    public ResponseEntity<Run> getRunForReference(
//            @PathVariable String company,
//            @PathVariable String org,
//            @PathVariable String repo,
//            @PathVariable String branch,
//            @PathVariable String sha,
//            @PathVariable String runReference,
//            @RequestParam(required = false) Map<String, String> metadataFilters) {
//        return new ResponseEntity<>(browseService.getRunFromReference(company, org, repo, branch, sha, runReference), HttpStatus.OK);
//    }
//
//    @GetMapping(path = "company/{company}/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stages/{stage}", produces = "text/html")
//    public ResponseEntity<Map<Stage, Map<TestResult, Set<TestSuite>>>> getStage(
//            @PathVariable String company,
//            @PathVariable String org,
//            @PathVariable String repo,
//            @PathVariable String branch,
//            @PathVariable Long jobId,
//            @PathVariable Long runId,
//            @PathVariable String stage,
//            @RequestParam(required = false) Map<String, String> metadataFilters) {
//        return new ResponseEntity<>(browseService.getStageTestResultsTestSuites(company, org, repo, branch, jobId, runId, stage), HttpStatus.OK);
//    }

//    @GetMapping(path = "{org}/repo/{repo}/branch/{branch}/sha/{sha}/run/{runReference}/stages", produces = "text/html")
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