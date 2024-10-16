package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalRequest;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCount;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCountMaps;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.TreeSet;

@RestController
@RequestMapping("")
@SuppressWarnings("unused")
@Slf4j
public class GraphUIController {

    private final GraphService graphService;

    @Autowired
    public GraphUIController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping(path = "company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/stage/{stage}/trend", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getJobStageTestTrend(
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
        Exception e = null;
        String trendHtml = null;
        //temporary workaround until bump max_allowed_packet
        for (int i = runs; i > 0; i = i / 2) {
            try {
                final JobStageTestTrend jobTestTrend = graphService.getJobStageTestTrend(company, org, repo, branch, jobId, stage, start, end, i);
                e = null;
                trendHtml = TrendHtmlHelper.renderTrendHtml(jobTestTrend);
                break;
            } catch (Exception ex) {
                e = ex;
                log.warn("failed run for jobId: {}, i: {}", jobId, i, ex);
            }
        }
        if (e != null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.join("\n", ExceptionUtils.getStackFrames(e)));
        }
        //TODO: add cache headers * browser side cache using header, e.g. Cache-Control: max-age=600 //10 mins
        return new ResponseEntity<>(trendHtml, HttpStatus.OK);
    }

    @GetMapping(path = "metrics/all", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getMetricsAll(
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
        return postCompanyDashboard(metricsIntervalRequest);
    }

    @GetMapping(path = "metrics/company/{company}", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getCompanyMetrics(
            @PathVariable String company,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> orgs,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> repos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> branches,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> jobInfos,

            @RequestParam(required = false, defaultValue = "") TreeSet<String> notOrgs,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notRepos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notBranches,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notJobInfos,
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @RequestParam(required = false, defaultValue = "30") Integer intervalDays,
            @RequestParam(required = false, defaultValue = "2") Integer intervalCount
    ) {
        TreeSet<String> companies = new TreeSet<>();
        companies.add(company);
        TreeSet<String> notCompanies = new TreeSet<>();

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
        return postCompanyDashboard(metricsIntervalRequest);
    }

    @GetMapping(path = "metrics/company/{company}/org/{org}", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getOrgMetrics(
            @PathVariable String company,
            @PathVariable String org,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> repos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> branches,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> jobInfos,

            @RequestParam(required = false, defaultValue = "") TreeSet<String> notRepos,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notBranches,
            @RequestParam(required = false, defaultValue = "") TreeSet<String> notJobInfos,
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @RequestParam(required = false, defaultValue = "30") Integer intervalDays,
            @RequestParam(required = false, defaultValue = "2") Integer intervalCount
    ) {
        TreeSet<String> companies = new TreeSet<>();
        companies.add(company);
        TreeSet<String> notCompanies = new TreeSet<>();

        TreeSet<String> orgs = new TreeSet<>();
        orgs.add(org);
        TreeSet<String> notOrgs = new TreeSet<>();

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
        return postCompanyDashboard(metricsIntervalRequest);
    }

    @PostMapping(path = "metrics", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> postCompanyDashboard(
            @RequestBody MetricsIntervalRequest metricsIntervalRequest
    ) {
        TreeSet<MetricsIntervalResultCount> metricsIntervalResultCounts = graphService.getCompanyDashboardIntervalResultCount(metricsIntervalRequest);
        MetricsIntervalResultCountMaps metricsIntervalResultCountMaps = MetricsIntervalResultCountMaps.fromMetricsIntervalResultCount(metricsIntervalResultCounts);
        final String response = MetricsHtmlHelper.renderMetricsIntervalResultCountMaps(metricsIntervalResultCountMaps);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}