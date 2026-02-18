package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import io.github.ericdriggs.reportcard.persist.tags.ParseException;
import io.github.ericdriggs.reportcard.persist.tags.TagQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for tag-based test queries.
 *
 * <p>Query syntax: ?tags=(smoke AND env=prod) OR (regression AND env=staging)
 *
 * <p>Endpoints scope query to hierarchy level in path.
 * Response structure varies by scope - see {@link TagQueryResponse}.
 */
@RestController
@RequestMapping("/json")
@SuppressWarnings("unused")
public class TagQueryController {

    private final TagQueryService tagQueryService;

    @Autowired
    public TagQueryController(TagQueryService tagQueryService) {
        this.tagQueryService = tagQueryService;
    }

    /**
     * Search all tests by tags (company-wide).
     * Returns results grouped by org -> repo -> branch -> job -> run -> stage -> test.
     */
    @Operation(summary = "Search tests by tags within company scope")
    @GetMapping("/company/{company}/tags/tests")
    public ResponseEntity<TagQueryResponse> searchByTagsCompany(
            @Parameter(description = "Company name")
            @PathVariable String company,
            @Parameter(description = "Tag expression (e.g., 'smoke AND env=prod')")
            @RequestParam String tags) {

        TagQueryResponse response = tagQueryService.findByTagExpressionByPath(
            tags, company, null, null, null, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Search tests by tags within org scope.
     * Returns results grouped by repo -> branch -> job -> run -> stage -> test.
     */
    @Operation(summary = "Search tests by tags within org scope")
    @GetMapping("/company/{company}/org/{org}/tags/tests")
    public ResponseEntity<TagQueryResponse> searchByTagsOrg(
            @Parameter(description = "Company name")
            @PathVariable String company,
            @Parameter(description = "Org name")
            @PathVariable String org,
            @Parameter(description = "Tag expression (e.g., 'smoke AND env=prod')")
            @RequestParam String tags) {

        TagQueryResponse response = tagQueryService.findByTagExpressionByPath(
            tags, company, org, null, null, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Search tests by tags within repo scope.
     * Returns results grouped by branch -> job -> run -> stage -> test.
     */
    @Operation(summary = "Search tests by tags within repo scope")
    @GetMapping("/company/{company}/org/{org}/repo/{repo}/tags/tests")
    public ResponseEntity<TagQueryResponse> searchByTagsRepo(
            @Parameter(description = "Company name")
            @PathVariable String company,
            @Parameter(description = "Org name")
            @PathVariable String org,
            @Parameter(description = "Repo name")
            @PathVariable String repo,
            @Parameter(description = "Tag expression (e.g., 'smoke AND env=prod')")
            @RequestParam String tags) {

        TagQueryResponse response = tagQueryService.findByTagExpressionByPath(
            tags, company, org, repo, null, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Search tests by tags within branch scope.
     * Returns results grouped by job -> run -> stage -> test.
     */
    @Operation(summary = "Search tests by tags within branch scope")
    @GetMapping("/company/{company}/org/{org}/repo/{repo}/branch/{branch}/tags/tests")
    public ResponseEntity<TagQueryResponse> searchByTagsBranch(
            @Parameter(description = "Company name")
            @PathVariable String company,
            @Parameter(description = "Org name")
            @PathVariable String org,
            @Parameter(description = "Repo name")
            @PathVariable String repo,
            @Parameter(description = "Branch name")
            @PathVariable String branch,
            @Parameter(description = "Tag expression (e.g., 'smoke AND env=prod')")
            @RequestParam String tags) {

        TagQueryResponse response = tagQueryService.findByTagExpressionByPath(
            tags, company, org, repo, branch, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Search tests by tags within sha scope.
     * Returns results grouped by job -> run -> stage -> test (filtered to specific SHA).
     */
    @Operation(summary = "Search tests by tags within sha scope")
    @GetMapping("/company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/tags/tests")
    public ResponseEntity<TagQueryResponse> searchByTagsSha(
            @Parameter(description = "Company name")
            @PathVariable String company,
            @Parameter(description = "Org name")
            @PathVariable String org,
            @Parameter(description = "Repo name")
            @PathVariable String repo,
            @Parameter(description = "Branch name")
            @PathVariable String branch,
            @Parameter(description = "SHA value")
            @PathVariable String sha,
            @Parameter(description = "Tag expression (e.g., 'smoke AND env=prod')")
            @RequestParam String tags) {

        TagQueryResponse response = tagQueryService.findByTagExpressionByPath(
            tags, company, org, repo, branch, sha);
        return ResponseEntity.ok(response);
    }

    /**
     * Handle parse errors for invalid tag expressions.
     */
    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ErrorResponse> handleParseException(ParseException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("Invalid tag expression", e.getMessage()));
    }

    /**
     * Error response for invalid tag expressions.
     */
    public record ErrorResponse(String error, String message) {}
}
