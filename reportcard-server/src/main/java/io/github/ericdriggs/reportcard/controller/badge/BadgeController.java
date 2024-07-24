package io.github.ericdriggs.reportcard.controller.badge;

import io.github.ericdriggs.reportcard.util.badge.BadgeStatus;
import io.github.ericdriggs.reportcard.util.badge.BadgeSvgHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/badge")
@SuppressWarnings("unused")
public class BadgeController {


    @GetMapping(path = "status/{badgeStatus}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> status(
            @PathVariable String badgeStatus
    ) {
        final String statusBadge = BadgeSvgHelper.badgeStatus(BadgeStatus.fromText(badgeStatus));
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "trend", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> trend() {
        final String statusBadge = BadgeSvgHelper.badgeTrend();
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "html/{label}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> html(
            @PathVariable String label
    ) {
        final String statusBadge = BadgeSvgHelper.badgeHtml(label);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "lastsuccess/{runCount}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> statusDateSha(
            @PathVariable int runCount,
            @RequestParam Instant date,
            @RequestParam String sha
    ) {
        final String statusBadge = BadgeSvgHelper.badgeLastSuccessDateSha(runCount, date, sha);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "xml/{label}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> xml(
            @PathVariable String label
    ) {
        final String statusBadge = BadgeSvgHelper.badgeXml(label);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "statusdatesha/{badgeStatus}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> statusDateSha(
            @PathVariable BadgeStatus badgeStatus,
            @RequestParam Instant date,
            @RequestParam String sha
    ) {
        final String statusBadge = BadgeSvgHelper.badgeStatusDateSha(badgeStatus, date, sha);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

}