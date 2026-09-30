package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.model.publication.FailedPublicationsResponse;
import io.github.ericdriggs.reportcard.persist.FailedPublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/v1/api")
@SuppressWarnings("unused")
public class FailedPublicationJsonController {

    private final FailedPublicationService failedPublicationService;

    @Autowired
    public FailedPublicationJsonController(FailedPublicationService failedPublicationService) {
        this.failedPublicationService = failedPublicationService;
    }

    @Operation(summary = "Global list of stages with storage and no test result (suspected failed publications), newest stage first")
    @GetMapping(path = "failed-publishes", produces = "application/json")
    public ResponseEntity<FailedPublicationsResponse> getFailedPublications(
            @Parameter(description = "Run date window in days, 1-30. Default: 1")
            @RequestParam(value = "days", required = false) Integer days,
            @Parameter(description = "Excludes runs newer than this many minutes, at least 1. Default: 10")
            @RequestParam(value = "olderThanMinutes", required = false) Integer olderThanMinutes,
            @Parameter(description = "Maximum stages, 1-200. Default: 50")
            @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.ok(failedPublicationService.getFailedPublications(days, olderThanMinutes, limit, Instant.now()));
    }
}
