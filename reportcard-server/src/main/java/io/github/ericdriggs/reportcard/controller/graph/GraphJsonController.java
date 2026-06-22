package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.model.failures.FailuresDashboard;
import io.github.ericdriggs.reportcard.model.failures.FailuresDashboardRequest;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalRequest;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCount;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardRequest;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.GraphService;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@RestController
@RequestMapping("/v1/api")
@SuppressWarnings("unused")
public class GraphJsonController {

    private final GraphService graphService;
    private final BrowseService browseService;

    @Autowired
    public GraphJsonController(GraphService graphService, BrowseService browseService) {
        this.graphService = graphService;
        this.browseService = browseService;
    }

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/stage/{stage}/trend", produces = "application/json")
    public ResponseEntity<JobStageTestTrend> getJobStageTestTrend(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable Long jobId,
            @PathVariable String stage,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end,
            @RequestParam(required = false, defaultValue = "30") Integer runs
    ) {
        return new ResponseEntity<>(graphService.getJobStageTestTrend(company, org, repo, branch, jobId, stage, start, end, runs), HttpStatus.OK);
    }

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/jobinfo/{jobInfo}/stage/{stage}/trend", produces = "application/json")
    public ResponseEntity<JobStageTestTrend> getJobStageTestTrendFromJobInfo(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String jobInfo,
            @PathVariable String stage,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end,
            @RequestParam(required = false, defaultValue = "30") Integer runs
    ) {
        Map<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);
        JobPojo job = browseService.getJob(company, org, repo, branch, jobInfoMap);
        return getJobStageTestTrend(company, org, repo, branch, job.getJobId(), stage, start, end, runs);
    }

    @GetMapping(path = "metrics/all", produces = "application/json")
    @Operation(summary = "Get metrics using query parameters",
            description = "supports filtering and exclusion using lists e.g. (companies or notCompanies). jobInfo and notJobInfo expected colon separated values, e.g. application:foo,application:bar",
            operationId = "getMetricsJson"
    )
    public ResponseEntity<TreeSet<MetricsIntervalResultCount>> getMetricsJson(
            @RequestParam(required = false, defaultValue = "") TreeSet<String> companies,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> orgs,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> repos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> branches,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> jobInfos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notCompanies,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notOrgs,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notRepos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notBranches,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notJobInfos,
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @RequestParam(required = false, defaultValue = "30") Integer intervalDays
    ) {
        MetricsIntervalRequest metricsIntervalRequest = MetricsIntervalRequest.fromQueryParams(
                companies,
                orgs,
                repos,
                branches,
                jobInfos,
                notCompanies,
                notOrgs,
                notRepos,
                notBranches,
                notJobInfos,
                shouldIncludeDefaultBranches,
                intervalDays
        );

        return postMetrics(metricsIntervalRequest);
    }

    @PostMapping(path = "metrics", produces = "application/json;charset=UTF-8")
    public ResponseEntity<TreeSet<MetricsIntervalResultCount>> postMetrics(
            @RequestBody MetricsIntervalRequest metricsIntervalRequest
    ) {
        TreeSet<MetricsIntervalResultCount> metricsIntervalResultCounts = graphService.getCompanyDashboardIntervalResultCount(metricsIntervalRequest);
        return new ResponseEntity<>(metricsIntervalResultCounts, HttpStatus.OK);
    }

    @GetMapping(path = "job_dashboard/company/{company}/org/{org}", produces = "application/json")
    @Operation(summary = "Get job dashboard metrics",
            description = "Individual job metrics with days since passing, job pass %, test pass %",
            operationId = "getJobDashboardJson"
    )
    public ResponseEntity<List<JobDashboardMetrics>> getJobDashboardJson(
            @PathVariable String company,
            @PathVariable String org,
            @RequestParam(required = false) List<String> jobInfo,
            @RequestParam(required = false, defaultValue = "90") Integer days
    ) {
        Map<String, String> jobInfoMap = JobInfoParser.parseJobInfoParams(jobInfo);
        
        JobDashboardRequest request = JobDashboardRequest.builder()
                .company(company)
                .org(org)
                .jobInfos(jobInfoMap)
                .days(days)
                .build();
        return new ResponseEntity<>(graphService.getPipelineDashboard(request), HttpStatus.OK);
    }

    @Operation(summary = "Get failures dashboard for company")
    @GetMapping(path = "company/{company}/failures", produces = "application/json")
    public ResponseEntity<FailuresDashboard> getFailuresDashboardForCompany(
            @PathVariable String company,
            @RequestParam(required = false, defaultValue = "7") Integer days,
            @RequestParam(required = false, defaultValue = "50") Integer failureThreshold,
            @RequestParam(required = false) List<String> repos,
            @RequestParam(required = false) List<String> jobInfo,
            @RequestParam(required = false) Integer limit
    ) {
        return buildFailuresDashboardResponse(company, null, days, failureThreshold, repos, jobInfo, limit);
    }

    @Operation(summary = "Get failures dashboard for org")
    @GetMapping(path = "company/{company}/org/{org}/failures", produces = "application/json")
    public ResponseEntity<FailuresDashboard> getFailuresDashboardForOrg(
            @PathVariable String company,
            @PathVariable String org,
            @RequestParam(required = false, defaultValue = "7") Integer days,
            @RequestParam(required = false, defaultValue = "50") Integer failureThreshold,
            @RequestParam(required = false) List<String> repos,
            @RequestParam(required = false) List<String> jobInfo,
            @RequestParam(required = false) Integer limit
    ) {
        return buildFailuresDashboardResponse(company, org, days, failureThreshold, repos, jobInfo, limit);
    }

    private ResponseEntity<FailuresDashboard> buildFailuresDashboardResponse(
            String company, String org, Integer days, Integer failureThreshold,
            List<String> repos, List<String> jobInfo, Integer limit) {
        FailuresDashboardRequest.FailuresDashboardRequestBuilder builder = FailuresDashboardRequest.builder()
                .company(company)
                .org(org)
                .days(days)
                .failureThreshold(failureThreshold)
                .repos(repos)
                .limit(limit);

        if (jobInfo != null && !jobInfo.isEmpty()) {
            String[] parts = jobInfo.get(0).split(":", 2);
            if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                builder.jobInfoKey(parts[0])
                        .jobInfoValues(List.of(parts[1].split(",")));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "jobInfo must be in format 'key:value1,value2'. Got: " + jobInfo.get(0));
            }
        }

        return new ResponseEntity<>(graphService.getFailuresDashboard(builder.build()), HttpStatus.OK);
    }

}
