package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.html.FailedPublicationHtmlHelper;
import io.github.ericdriggs.reportcard.persist.FailedPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("")
@SuppressWarnings("unused")
public class FailedPublicationUIController {

    private final FailedPublicationService failedPublicationService;

    @Autowired
    public FailedPublicationUIController(FailedPublicationService failedPublicationService) {
        this.failedPublicationService = failedPublicationService;
    }

    @GetMapping(path = "failed-publishes", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getFailedPublications(
            @RequestParam(value = "days", required = false) Integer days,
            @RequestParam(value = "olderThanMinutes", required = false) Integer olderThanMinutes,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.ok(FailedPublicationHtmlHelper.getFailedPublicationsPage(
                failedPublicationService.getFailedPublications(days, olderThanMinutes, limit, Instant.now())));
    }
}
