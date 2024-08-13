package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

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
        for (int i = runs; i > 0; i = i/2) {
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

    @GetMapping(path = "company/{company}/org/{org}/dashboard", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getOrgDashbarod(
            @PathVariable String company,
            @PathVariable String org,
            @RequestParam(required = false) List<String> repos,
            @RequestParam(required = false, defaultValue = "") List<String> branches,
            @RequestParam(required = false, defaultValue = "true") boolean shouldIncludeDefaultBranches,
            @RequestParam(required = false) Integer days
    ) {
        final OrgDashboard orgDashboard = graphService.getOrgDashboard(company, org, repos, branches, shouldIncludeDefaultBranches, days);
        final String dashboardHtml = OrgDashboardHtmlHelper.renderOrgDashboardHtml(orgDashboard);
        //TODO: add cache headers * browser side cache using header, e.g. Cache-Control: max-age=600 //10 mins
        return new ResponseEntity<>(dashboardHtml, HttpStatus.OK);
    }

}