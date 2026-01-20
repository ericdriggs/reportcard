package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalRequest;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCount;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardRequest;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.GraphService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@RestController
@RequestMapping("/v1/api")
@SuppressWarnings("unused")
public class GraphJsonController {

    private final GraphService graphService;

    @Autowired
    public GraphJsonController(GraphService graphService) {

        this.graphService = graphService;

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
            @RequestParam(required = false) Instant end
    ) {
        return new ResponseEntity<>(graphService.getJobStageTestTrend(company, org, repo, branch, jobId, stage, start, end, 30), HttpStatus.OK);
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
            @RequestParam(required = false, defaultValue = "30") Integer intervalDays,
            @RequestParam(required = false, defaultValue = "2") Integer intervalCount
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
                intervalDays,
                intervalCount
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
        // Parse jobInfo params into Map
        Map<String, String> jobInfoMap = new HashMap<>();
        if (jobInfo != null) {
            for (String info : jobInfo) {
                String[] parts = info.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (value.contains("*")) {
                        value = value.replace("*", "%");
                    }
                    jobInfoMap.put(key, value);
                }
            }
        }
        
        JobDashboardRequest request = JobDashboardRequest.builder()
                .company(company)
                .org(org)
                .jobInfos(jobInfoMap)
                .days(days)
                .build();
        return new ResponseEntity<>(graphService.getPipelineDashboard(request), HttpStatus.OK);
    }

}