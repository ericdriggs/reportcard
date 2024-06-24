package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.GraphService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;


@RestController
@RequestMapping("/v1/api")
@Hidden
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

}