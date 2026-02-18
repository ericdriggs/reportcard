package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.html.TagQueryHtmlHelper;
import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import io.github.ericdriggs.reportcard.persist.tags.ParseException;
import io.github.ericdriggs.reportcard.persist.tags.TagQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HTML UI controller for tag-based test queries.
 * Provides search form and results rendering at all hierarchy levels.
 */
@RestController
@RequestMapping("")
@SuppressWarnings("unused")
public class TagQueryUIController {

    private final TagQueryService tagQueryService;

    @Autowired
    public TagQueryUIController(TagQueryService tagQueryService) {
        this.tagQueryService = tagQueryService;
    }

    /**
     * Tag search at company scope.
     */
    @GetMapping(path = "/company/{company}/tags/tests", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> searchByTagsCompany(
            @PathVariable String company,
            @RequestParam(required = false) String tags) {

        CompanyOrgRepoBranchJobRunStageDTO scopePath = CompanyOrgRepoBranchJobRunStageDTO.builder()
            .company(company)
            .build();

        return handleTagQuery(tags, scopePath, company, null, null, null, null);
    }

    /**
     * Tag search at org scope.
     */
    @GetMapping(path = "/company/{company}/org/{org}/tags/tests", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> searchByTagsOrg(
            @PathVariable String company,
            @PathVariable String org,
            @RequestParam(required = false) String tags) {

        CompanyOrgRepoBranchJobRunStageDTO scopePath = CompanyOrgRepoBranchJobRunStageDTO.builder()
            .company(company)
            .org(org)
            .build();

        return handleTagQuery(tags, scopePath, company, org, null, null, null);
    }

    /**
     * Tag search at repo scope.
     */
    @GetMapping(path = "/company/{company}/org/{org}/repo/{repo}/tags/tests", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> searchByTagsRepo(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @RequestParam(required = false) String tags) {

        CompanyOrgRepoBranchJobRunStageDTO scopePath = CompanyOrgRepoBranchJobRunStageDTO.builder()
            .company(company)
            .org(org)
            .repo(repo)
            .build();

        return handleTagQuery(tags, scopePath, company, org, repo, null, null);
    }

    /**
     * Tag search at branch scope.
     */
    @GetMapping(path = "/company/{company}/org/{org}/repo/{repo}/branch/{branch}/tags/tests", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> searchByTagsBranch(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) String tags) {

        CompanyOrgRepoBranchJobRunStageDTO scopePath = CompanyOrgRepoBranchJobRunStageDTO.builder()
            .company(company)
            .org(org)
            .repo(repo)
            .branch(branch)
            .build();

        return handleTagQuery(tags, scopePath, company, org, repo, branch, null);
    }

    /**
     * Tag search at sha scope.
     */
    @GetMapping(path = "/company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/tags/tests", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> searchByTagsSha(
            @PathVariable String company,
            @PathVariable String org,
            @PathVariable String repo,
            @PathVariable String branch,
            @PathVariable String sha,
            @RequestParam(required = false) String tags) {

        CompanyOrgRepoBranchJobRunStageDTO scopePath = CompanyOrgRepoBranchJobRunStageDTO.builder()
            .company(company)
            .org(org)
            .repo(repo)
            .branch(branch)
            .build();

        return handleTagQuery(tags, scopePath, company, org, repo, branch, sha);
    }

    /**
     * Common handler for all tag query endpoints.
     * Shows form if no tags parameter, executes query otherwise.
     */
    private ResponseEntity<String> handleTagQuery(
            String tags,
            CompanyOrgRepoBranchJobRunStageDTO scopePath,
            String company,
            String org,
            String repo,
            String branch,
            String sha) {

        // If no tags parameter, show search form
        if (tags == null || tags.isBlank()) {
            String html = TagQueryHtmlHelper.renderTagQueryPage(null, scopePath);
            return ResponseEntity.ok(html);
        }

        // Execute query
        try {
            TagQueryResponse response = tagQueryService.findByTagExpressionByPath(
                tags, company, org, repo, branch, sha);

            String html = TagQueryHtmlHelper.renderTagQueryPage(response, scopePath);
            return ResponseEntity.ok(html);

        } catch (ParseException e) {
            String errorHtml = TagQueryHtmlHelper.renderErrorPage(
                e.getMessage(), tags, scopePath);
            return ResponseEntity.badRequest().body(errorHtml);
        }
    }
}
