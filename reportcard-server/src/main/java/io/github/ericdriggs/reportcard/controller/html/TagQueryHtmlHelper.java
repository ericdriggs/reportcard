package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

/**
 * HTML rendering for tag query interface.
 * Extends BrowseHtmlHelper for consistent page structure and navigation.
 */
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class TagQueryHtmlHelper extends BrowseHtmlHelper {

    private static final String ls = System.lineSeparator();

    /**
     * Main entry point for rendering tag query pages.
     * Delegates to form or results based on response being null.
     *
     * @param response Tag query response (null shows search form)
     * @param scopePath Hierarchy path for scoping the query
     * @return HTML page content
     */
    public static String renderTagQueryPage(TagQueryResponse response, CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        if (response == null) {
            return getSearchForm(scopePath);
        } else {
            return getResultsHtml(response, scopePath);
        }
    }

    /**
     * Renders search form for tag queries.
     *
     * @param scopePath Hierarchy path for form action
     * @return HTML page with search form
     */
    public static String getSearchForm(CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        final String formAction = scopePath.toUrlPath() + "/tags/tests";

        final String main =
            """
            <div>
              <fieldset>
                <legend>Tag Search</legend>
                <form method="get" action="{formAction}">
                  <label for="tags">Tag Expression:</label><br>
                  <input type="text" id="tags" name="tags" style="width: 400px" placeholder="smoke AND env=prod"><br><br>
                  <button type="submit">Search</button>
                </form>
                <br>
                <div class="info">
                  <strong>Syntax:</strong>
                  <ul>
                    <li>Single tag: <code>smoke</code></li>
                    <li>AND expression: <code>smoke AND regression</code></li>
                    <li>OR expression: <code>smoke OR regression</code></li>
                    <li>Key=value tags: <code>env=prod</code></li>
                    <li>Complex: <code>(smoke AND env=prod) OR (regression AND env=staging)</code></li>
                  </ul>
                  <strong>Note:</strong> Operators (AND, OR) are case-sensitive and must be uppercase.
                </div>
              </fieldset>
            </div>
            """
            .replace("{formAction}", formAction);

        return getPage(main, getBreadCrumb(scopePath, "tags/tests"));
    }

    /**
     * Renders query results grouped by branch -> sha -> job.
     *
     * @param response Tag query response with results
     * @param scopePath Hierarchy path for navigation links
     * @return HTML page with results
     */
    public static String getResultsHtml(TagQueryResponse response, CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        StringBuilder main = new StringBuilder();

        // Query info section
        main.append("<div>").append(ls);
        main.append("  <fieldset>").append(ls);
        main.append("    <legend>Query Info</legend>").append(ls);
        main.append("    <strong>Scope:</strong> " + response.getQuery().getScope() + "<br>").append(ls);
        main.append("    <strong>Tag Expression:</strong> <code>" + HtmlUtils.htmlEscape(response.getQuery().getTags()) + "</code>").append(ls);
        main.append("  </fieldset>").append(ls);
        main.append("</div>").append(ls);
        main.append("<br>").append(ls);

        // Results section
        Map<String, Map<String, Map<String, TagQueryResponse.JobResult>>> results = response.getResults();

        if (results == null || results.isEmpty()) {
            main.append("<div class=\"info\">No results found for this query.</div>").append(ls);
        } else {
            main.append("<div>").append(ls);
            main.append("  <fieldset>").append(ls);
            main.append("    <legend>Results</legend>").append(ls);

            // Iterate branches
            for (Map.Entry<String, Map<String, Map<String, TagQueryResponse.JobResult>>> branchEntry : results.entrySet()) {
                String branchName = branchEntry.getKey();
                Map<String, Map<String, TagQueryResponse.JobResult>> shaMap = branchEntry.getValue();

                // Build branch path for link
                CompanyOrgRepoBranchJobRunStageDTO branchPath = scopePath.toBuilder()
                    .branch(branchName)
                    .build();
                String branchUrl = getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateBranch(branchPath));

                main.append("    <fieldset class=\"branch-fieldset\">").append(ls);
                main.append("      <legend><a href=\"" + branchUrl + "\">" + HtmlUtils.htmlEscape(branchName) + "</a></legend>").append(ls);

                // Iterate SHAs
                for (Map.Entry<String, Map<String, TagQueryResponse.JobResult>> shaEntry : shaMap.entrySet()) {
                    String sha = shaEntry.getKey();
                    Map<String, TagQueryResponse.JobResult> jobMap = shaEntry.getValue();

                    main.append("      <fieldset class=\"sha-fieldset\">").append(ls);
                    main.append("        <legend>SHA: " + HtmlUtils.htmlEscape(sha) + "</legend>").append(ls);

                    // Iterate jobs
                    for (Map.Entry<String, TagQueryResponse.JobResult> jobEntry : jobMap.entrySet()) {
                        String jobInfo = jobEntry.getKey();
                        TagQueryResponse.JobResult jobResult = jobEntry.getValue();

                        main.append("        <div class=\"job-result\">").append(ls);
                        main.append("          <strong>Job:</strong> " + HtmlUtils.htmlEscape(jobInfo) + "<br>").append(ls);
                        main.append("          <strong>Run Date:</strong> " + jobResult.getRunDate() + "<br>").append(ls);
                        main.append("          <strong>Matching Tests:</strong>").append(ls);
                        main.append("          <ul>").append(ls);

                        List<String> tests = jobResult.getTests();
                        if (tests != null) {
                            for (String test : tests) {
                                main.append("            <li>" + HtmlUtils.htmlEscape(test) + "</li>").append(ls);
                            }
                        }

                        main.append("          </ul>").append(ls);
                        main.append("        </div>").append(ls);
                    }

                    main.append("      </fieldset><!--end-sha-fieldset-->").append(ls);
                }

                main.append("    </fieldset><!--end-branch-fieldset-->").append(ls);
            }

            main.append("  </fieldset>").append(ls);
            main.append("</div>").append(ls);
        }

        return getPage(main.toString(), getBreadCrumb(scopePath, "tags/tests"));
    }

    /**
     * Renders error page with form pre-populated with invalid query.
     *
     * @param errorMessage Error message to display
     * @param originalQuery The invalid query that caused the error
     * @param scopePath Hierarchy path for form action
     * @return HTML page with error and form
     */
    public static String renderErrorPage(String errorMessage, String originalQuery, CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        final String formAction = scopePath.toUrlPath() + "/tags/tests";
        final String escapedError = HtmlUtils.htmlEscape(errorMessage);
        final String escapedQuery = HtmlUtils.htmlEscape(originalQuery);

        final String main =
            """
            <div>
              <div class="error" style="color: red; font-weight: bold; margin-bottom: 20px;">
                Error: {errorMessage}
              </div>
              <fieldset>
                <legend>Tag Search</legend>
                <form method="get" action="{formAction}">
                  <label for="tags">Tag Expression:</label><br>
                  <input type="text" id="tags" name="tags" style="width: 400px" value="{originalQuery}" placeholder="smoke AND env=prod"><br><br>
                  <button type="submit">Search</button>
                </form>
                <br>
                <div class="info">
                  <strong>Syntax:</strong>
                  <ul>
                    <li>Single tag: <code>smoke</code></li>
                    <li>AND expression: <code>smoke AND regression</code></li>
                    <li>OR expression: <code>smoke OR regression</code></li>
                    <li>Key=value tags: <code>env=prod</code></li>
                    <li>Complex: <code>(smoke AND env=prod) OR (regression AND env=staging)</code></li>
                  </ul>
                  <strong>Note:</strong> Operators (AND, OR) are case-sensitive and must be uppercase.
                </div>
              </fieldset>
            </div>
            """
            .replace("{errorMessage}", escapedError)
            .replace("{formAction}", formAction)
            .replace("{originalQuery}", escapedQuery);

        return getPage(main, getBreadCrumb(scopePath, "tags/tests"));
    }
}
