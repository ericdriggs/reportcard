package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import io.github.ericdriggs.reportcard.model.TagQueryResponse.*;
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
     * Delegates to form or results based on response content.
     */
    public static String renderTagQueryPage(TagQueryResponse response, CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        if (response == null || !hasResults(response)) {
            if (response != null && response.getQuery() != null && response.getQuery().getTags() != null) {
                // Query was executed but no results
                return getNoResultsHtml(response, scopePath);
            }
            return getSearchForm(scopePath);
        } else {
            return getResultsHtml(response, scopePath);
        }
    }

    private static boolean hasResults(TagQueryResponse response) {
        return (response.getOrgs() != null && !response.getOrgs().isEmpty())
            || (response.getRepos() != null && !response.getRepos().isEmpty())
            || (response.getBranches() != null && !response.getBranches().isEmpty())
            || (response.getJobs() != null && !response.getJobs().isEmpty());
    }

    /**
     * Renders search form for tag queries.
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
     * Renders "no results" page with query info.
     */
    public static String getNoResultsHtml(TagQueryResponse response, CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        StringBuilder main = new StringBuilder();

        // Query info section
        main.append("<div>").append(ls);
        main.append("  <fieldset>").append(ls);
        main.append("    <legend>Query Info</legend>").append(ls);
        main.append("    <strong>Scope:</strong> ").append(response.getQuery().getScope()).append("<br>").append(ls);
        main.append("    <strong>Tag Expression:</strong> <code>").append(HtmlUtils.htmlEscape(response.getQuery().getTags())).append("</code>").append(ls);
        main.append("  </fieldset>").append(ls);
        main.append("</div>").append(ls);
        main.append("<br>").append(ls);
        main.append("<div class=\"info\">No results found for this query.</div>").append(ls);

        return getPage(main.toString(), getBreadCrumb(scopePath, "tags/tests"));
    }

    /**
     * Renders query results with proper hierarchy based on scope.
     */
    public static String getResultsHtml(TagQueryResponse response, CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        StringBuilder main = new StringBuilder();

        // Query info section
        main.append("<div>").append(ls);
        main.append("  <fieldset>").append(ls);
        main.append("    <legend>Query Info</legend>").append(ls);
        main.append("    <strong>Scope:</strong> ").append(response.getQuery().getScope()).append("<br>").append(ls);
        main.append("    <strong>Tag Expression:</strong> <code>").append(HtmlUtils.htmlEscape(response.getQuery().getTags())).append("</code>").append(ls);
        main.append("  </fieldset>").append(ls);
        main.append("</div>").append(ls);
        main.append("<br>").append(ls);

        // Results section - render based on what's populated
        main.append("<div>").append(ls);
        main.append("  <fieldset>").append(ls);
        main.append("    <legend>Results</legend>").append(ls);

        if (response.getOrgs() != null && !response.getOrgs().isEmpty()) {
            renderOrgs(main, response.getOrgs(), scopePath, 4);
        } else if (response.getRepos() != null && !response.getRepos().isEmpty()) {
            renderRepos(main, response.getRepos(), scopePath, 4);
        } else if (response.getBranches() != null && !response.getBranches().isEmpty()) {
            renderBranches(main, response.getBranches(), scopePath, 4);
        } else if (response.getJobs() != null && !response.getJobs().isEmpty()) {
            renderJobs(main, response.getJobs(), scopePath, 4);
        }

        main.append("  </fieldset>").append(ls);
        main.append("</div>").append(ls);

        return getPage(main.toString(), getBreadCrumb(scopePath, "tags/tests"));
    }

    private static void renderOrgs(StringBuilder sb, List<OrgResult> orgs, CompanyOrgRepoBranchJobRunStageDTO scopePath, int indent) {
        String pad = " ".repeat(indent);
        for (OrgResult org : orgs) {
            CompanyOrgRepoBranchJobRunStageDTO orgPath = scopePath.toBuilder().org(org.getOrgName()).build();
            String orgUrl = getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateOrg(orgPath));

            sb.append(pad).append("<fieldset class=\"org-fieldset\">").append(ls);
            sb.append(pad).append("  <legend>Org: <a href=\"").append(orgUrl).append("\">").append(HtmlUtils.htmlEscape(org.getOrgName())).append("</a></legend>").append(ls);

            if (org.getRepos() != null) {
                renderRepos(sb, org.getRepos(), orgPath, indent + 2);
            }

            sb.append(pad).append("</fieldset>").append(ls);
        }
    }

    private static void renderRepos(StringBuilder sb, List<RepoResult> repos, CompanyOrgRepoBranchJobRunStageDTO scopePath, int indent) {
        String pad = " ".repeat(indent);
        for (RepoResult repo : repos) {
            CompanyOrgRepoBranchJobRunStageDTO repoPath = scopePath.toBuilder().repo(repo.getRepoName()).build();
            String repoUrl = getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateRepo(repoPath));

            sb.append(pad).append("<fieldset class=\"repo-fieldset\">").append(ls);
            sb.append(pad).append("  <legend>Repo: <a href=\"").append(repoUrl).append("\">").append(HtmlUtils.htmlEscape(repo.getRepoName())).append("</a></legend>").append(ls);

            if (repo.getBranches() != null) {
                renderBranches(sb, repo.getBranches(), repoPath, indent + 2);
            }

            sb.append(pad).append("</fieldset>").append(ls);
        }
    }

    private static void renderBranches(StringBuilder sb, List<BranchResult> branches, CompanyOrgRepoBranchJobRunStageDTO scopePath, int indent) {
        String pad = " ".repeat(indent);
        for (BranchResult branch : branches) {
            CompanyOrgRepoBranchJobRunStageDTO branchPath = scopePath.toBuilder().branch(branch.getBranchName()).build();
            String branchUrl = getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateBranch(branchPath));

            sb.append(pad).append("<fieldset class=\"branch-fieldset\">").append(ls);
            sb.append(pad).append("  <legend>Branch: <a href=\"").append(branchUrl).append("\">").append(HtmlUtils.htmlEscape(branch.getBranchName())).append("</a></legend>").append(ls);

            if (branch.getJobs() != null) {
                renderJobs(sb, branch.getJobs(), branchPath, indent + 2);
            }

            sb.append(pad).append("</fieldset>").append(ls);
        }
    }

    private static void renderJobs(StringBuilder sb, List<JobResult> jobs, CompanyOrgRepoBranchJobRunStageDTO scopePath, int indent) {
        String pad = " ".repeat(indent);
        for (JobResult job : jobs) {
            String jobInfoDisplay = job.getJobInfo() != null && !job.getJobInfo().isEmpty()
                ? job.getJobInfo().toString()
                : "default";

            sb.append(pad).append("<fieldset class=\"job-fieldset\">").append(ls);
            sb.append(pad).append("  <legend>Job: ").append(HtmlUtils.htmlEscape(jobInfoDisplay)).append("</legend>").append(ls);

            if (job.getRuns() != null) {
                renderRuns(sb, job.getRuns(), scopePath, indent + 2);
            }

            sb.append(pad).append("</fieldset>").append(ls);
        }
    }

    private static void renderRuns(StringBuilder sb, List<RunResult> runs, CompanyOrgRepoBranchJobRunStageDTO scopePath, int indent) {
        String pad = " ".repeat(indent);
        for (RunResult run : runs) {
            sb.append(pad).append("<fieldset class=\"run-fieldset\">").append(ls);
            sb.append(pad).append("  <legend>Run #").append(run.getRunId()).append("</legend>").append(ls);
            sb.append(pad).append("  <strong>SHA:</strong> ").append(HtmlUtils.htmlEscape(run.getSha() != null ? run.getSha() : "unknown")).append("<br>").append(ls);
            sb.append(pad).append("  <strong>Date:</strong> ").append(run.getRunDate()).append("<br>").append(ls);

            if (run.getStages() != null) {
                renderStages(sb, run.getStages(), indent + 2);
            }

            sb.append(pad).append("</fieldset>").append(ls);
        }
    }

    private static void renderStages(StringBuilder sb, List<StageResult> stages, int indent) {
        String pad = " ".repeat(indent);
        for (StageResult stage : stages) {
            sb.append(pad).append("<fieldset class=\"stage-fieldset\">").append(ls);
            sb.append(pad).append("  <legend>Stage: ").append(HtmlUtils.htmlEscape(stage.getStageName())).append("</legend>").append(ls);

            if (stage.getTests() != null && !stage.getTests().isEmpty()) {
                sb.append(pad).append("  <strong>Matching Tests:</strong>").append(ls);
                sb.append(pad).append("  <ul>").append(ls);
                for (TestInfo test : stage.getTests()) {
                    String testDisplay = test.getClassName() != null
                        ? test.getClassName() + "." + test.getTestName()
                        : test.getTestName();
                    String statusBadge = test.getStatus() != null
                        ? " <span class=\"status-" + test.getStatus().toLowerCase() + "\">[" + test.getStatus() + "]</span>"
                        : "";
                    sb.append(pad).append("    <li>").append(HtmlUtils.htmlEscape(testDisplay)).append(statusBadge).append("</li>").append(ls);
                }
                sb.append(pad).append("  </ul>").append(ls);
            }

            sb.append(pad).append("</fieldset>").append(ls);
        }
    }

    /**
     * Renders error page with form pre-populated with invalid query.
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
