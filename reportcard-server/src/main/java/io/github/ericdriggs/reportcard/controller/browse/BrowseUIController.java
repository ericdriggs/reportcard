package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.controller.html.TestResultHtmlHelper;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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

    @GetMapping(path = {"", "company"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getCompanies() {
        return new ResponseEntity<>(BrowseHtmlHelper.getCompaniesHtml(), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}", "company/{company}/org"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getCompanyOrgs(@PathVariable String company) {
        return new ResponseEntity<>(BrowseHtmlHelper.getCompanyHtml(company), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}", "company/org/{org}/repo"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getOrgRepos(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(BrowseHtmlHelper.getOrgHtml(company, org), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}", "company/org/{org}/repo/{repo}/branch"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getRepoBranches(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo)
    {
        return new ResponseEntity<>(BrowseHtmlHelper.getRepoHtml(company, org, repo), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getBranchJobRuns(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false, defaultValue = "60") Integer runs,
            @RequestParam(required = false) Map<String, String> jobInfoFilters) {
        runs = validateRuns(runs);
        BranchStageViewResponse branchStageViewResponse  = browseService.getStageViewForBranch(company, org, repo, branch, runs);
        BranchJobLatestRunMap branchJobLatestRunMap = graphService.getBranchJobLatestRunMap(company, org, repo, branch);
        return new ResponseEntity<>(BrowseHtmlHelper.getBranchHtml(company, org, repo, branch, branchStageViewResponse, branchJobLatestRunMap), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getRunStagesFromJobId(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @RequestParam(required = false, defaultValue = "60") Integer runs
            ) {
        runs = validateRuns(runs);
        BranchStageViewResponse branchStageViewResponse  = browseService.getStageViewForJob(company, org, repo, branch, jobId, runs);
        BranchJobLatestRunMap branchJobLatestRunMap = graphService.getBranchJobLatestRunMap(company, org, repo, branch, jobId);
        return new ResponseEntity<>(BrowseHtmlHelper.getJobHtml(company, org, repo, branch, jobId, branchStageViewResponse, branchJobLatestRunMap), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}",
            "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/run"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getRunStagesFromJobInfo(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String jobInfo) {
        Map<String,String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        BranchStageViewResponse branchStageViewResponse  = browseService.getStageViewForJobInfo(company, org, repo, branch, jobInfoMap);
        final Set<Long> jobIds = branchStageViewResponse.getJobIds();
        if (jobIds.isEmpty()) {
            throw new IllegalArgumentException("no job found matching: " + jobInfo);
        }
        if (jobIds.size() > 1) {
            throw new IllegalArgumentException("multiple jobIds: " + jobIds + "  found matching: " + jobInfo);
        }
        final Long jobId = jobIds.iterator().next();
        BranchJobLatestRunMap branchJobLatestRunMap = graphService.getBranchJobLatestRunMap(company, org, repo, branch, jobId);
        return new ResponseEntity<>(BrowseHtmlHelper.getJobHtml(company, org, repo, branch, jobId, branchStageViewResponse, branchJobLatestRunMap), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}",
                        "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getStageTestResults(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable Long runId) {
        BranchStageViewResponse branchStageViewResponse = graphService.getRunBranchStageViewResponse(company, org, repo, branch, jobId, runId);
        BranchJobLatestRunMap branchJobLatestRunMap = graphService.getBranchJobLatestRunMap(company, org, repo, branch, jobId);
        return new ResponseEntity<>(BrowseHtmlHelper.getJobHtml(company, org, repo, branch, jobId, branchStageViewResponse, branchJobLatestRunMap), HttpStatus.OK);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/runcount/{runCount}"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getStageTestResultsNaturalKeys(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String jobInfo,
            @PathVariable Integer runCount) {
        TreeMap<String,String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        BranchStageViewResponse branchStageViewResponse = graphService.getRunBranchStageViewResponse(company, org, repo, branch, jobInfoMap, runCount);
        final TreeSet<Long> jobIds = branchStageViewResponse.getJobIds();
        if (jobIds == null || jobIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No job matches parameters requested");
        }
        if (jobIds.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Multiple jobIds match parameters requested: " + jobIds);
        }
        BranchJobLatestRunMap branchJobLatestRunMap = graphService.getBranchJobLatestRunMap(company, org, repo, branch, jobIds.first());
        return new ResponseEntity<>(BrowseHtmlHelper.getJobHtml(company, org, repo, branch, jobIds.first(), branchStageViewResponse, branchJobLatestRunMap), HttpStatus.OK);
    }


    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getLatestRunStages(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId) {
        Long latestRunId = browseService.getLatestRunId(jobId);
        return getStageTestResults(company, org, repo, branch, jobId, latestRunId);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId}/stage/{stage}"}, produces = "text/html;charset=UTF-8")
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

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/latest/stage/{stage}"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getLatestRunStageTestResult(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable String stage) {
        Long latestRunId = browseService.getLatestRunId(jobId);
        return getTestResult(company, org, repo, branch, jobId, latestRunId, stage);
    }

    @GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/runcount/{runCount}/stage/{stage}"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getTestResultNaturalKeys(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String jobInfo,
            @PathVariable Integer runCount,
            @PathVariable String stage) {

        TreeMap<String,String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        StageTestResultModel stageTestResultModel = browseService.getStageTestResultMap(company, org, repo, branch , jobInfoMap, runCount, stage);
        return new ResponseEntity<>(TestResultHtmlHelper.getTestResult(stageTestResultModel.getTestResult()), HttpStatus.OK);
    }

    Integer validateRuns(int runs) {
        if (runs < 1) {
            return 60;
        }
        return runs;
    }
}