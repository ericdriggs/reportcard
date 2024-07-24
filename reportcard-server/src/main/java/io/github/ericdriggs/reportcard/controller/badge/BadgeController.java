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
        final String statusBadge = BadgeSvgHelper.status(BadgeStatus.fromText(badgeStatus));
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "trend", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> trend() {
        final String statusBadge = BadgeSvgHelper.trend();
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "html/{label}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> html(
            @PathVariable String label
    ) {
        final String statusBadge = BadgeSvgHelper.htmlBadge(label);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "lastsuccess/{runCount}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> statusDateSha(
            @PathVariable int runCount,
            @RequestParam Instant date,
            @RequestParam String sha
    ) {
        final String statusBadge = BadgeSvgHelper.lastSuccessDateSha(runCount, date, sha);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "xml/{label}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> xml(
            @PathVariable String label
    ) {
        final String statusBadge = BadgeSvgHelper.xmlBadge(label);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

    @GetMapping(path = "statusdatesha/{badgeStatus}", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> statusDateSha(
            @PathVariable BadgeStatus badgeStatus,
            @RequestParam Instant date,
            @RequestParam String sha
    ) {
        final String statusBadge = BadgeSvgHelper.statusDateSha(badgeStatus, date, sha);
        return new ResponseEntity<>(statusBadge, HttpStatus.OK);
    }

}