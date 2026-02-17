# Phase 10: Tag Query HTML Interface - Research

**Researched:** 2026-02-17
**Domain:** Spring Boot HTML controllers with form handling and nested data rendering
**Confidence:** HIGH

## Summary

Phase 10 adds an HTML interface for tag-based test queries, mirroring the existing TagQueryController REST API (Phase 9). The implementation follows established patterns in the Reportcard codebase: HTML helpers extend BrowseHtmlHelper for shared utilities, controllers use `@RequestMapping` with `produces = "text/html;charset=UTF-8"`, and services are injected for business logic.

The key challenge is rendering the nested TagQueryResponse structure (branch -> sha -> job -> tests) as HTML with clickable navigation links back to browse pages. The codebase already has patterns for this in OrgDashboardHtmlHelper (nested fieldsets) and PipelineDashboardHtmlHelper (HTML forms with query parameters).

**Primary recommendation:** Extend BrowseHtmlHelper, use nested fieldsets for hierarchy rendering, delegate form submission to JavaScript for query parameter construction, and handle empty tags parameter with a search form.

## Standard Stack

The codebase uses standard Java/Spring Boot HTML generation with server-side string templating.

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 2.6.15 | Web framework | Project's base framework |
| Spring Web MVC | (included) | Controller layer | Standard Spring web |
| JOOQ | (project) | Database access | Type-safe queries |
| Lombok | (project) | Boilerplate reduction | Builder patterns |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Jackson | (Spring Boot) | JSON parsing | TagQueryResponse serialization |
| Testcontainers | (test) | Integration tests | Service layer tests |
| MockMvc | (Spring Test) | Controller tests | Unit tests without service |

### HTML Generation Approach
| Pattern | Used in Codebase | Purpose |
|---------|------------------|---------|
| String templates (Java text blocks) | BrowseHtmlHelper.java lines 726-760 | Base page structure |
| StringBuilder concatenation | OrgDashboardHtmlHelper.java | Dynamic content |
| String.replace() placeholders | BrowseHtmlHelper.java line 661 | Template variable substitution |
| Inline JavaScript | PipelineDashboardHtmlHelper.java lines 64-105 | Form handling, query params |

**Installation:**
N/A - Uses existing project dependencies

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/io/github/ericdriggs/reportcard/
├── controller/
│   ├── TagQueryUIController.java          # NEW: HTML endpoints
│   ├── TagQueryController.java            # Existing: JSON API
│   ├── browse/
│   │   ├── BrowseUIController.java        # Pattern: HTML controller
│   │   └── BrowseHtmlHelper.java          # Pattern: HTML generation
│   ├── graph/
│   │   ├── OrgDashboardHtmlHelper.java    # Pattern: nested fieldsets
│   │   └── PipelineDashboardHtmlHelper.java # Pattern: forms
│   └── html/
│       └── TagQueryHtmlHelper.java         # NEW: extends BrowseHtmlHelper
```

### Pattern 1: HTML Controller Structure
**What:** HTML controllers use `@RequestMapping("")` (no `/api/v1` prefix) and produce `text/html;charset=UTF-8`

**When to use:** For all HTML endpoints that mirror JSON APIs

**Example:**
```java
// Source: BrowseUIController.java lines 18-40
@RestController
@RequestMapping("")
public class BrowseUIController {

    private final BrowseService browseService;

    @Autowired
    public BrowseUIController(BrowseService browseService) {
        this.browseService = browseService;
    }

    @GetMapping(path = {"company/{company}/org/{org}"}, produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getOrgRepos(
            @PathVariable String company,
            @PathVariable String org) {
        return new ResponseEntity<>(BrowseHtmlHelper.getOrgHtml(company, org), HttpStatus.OK);
    }
}
```

**Key points:**
- No `/api/v1` prefix (HTML at root paths, JSON at `/api/v1`)
- `produces = "text/html;charset=UTF-8"` for content negotiation
- Return `ResponseEntity<String>` with HTML body
- Delegate to helper class for HTML generation

### Pattern 2: HTML Helper Extension
**What:** HTML helpers extend BrowseHtmlHelper to inherit utility methods (`getPage()`, `getUrl()`, `getLink()`, `getBreadCrumb()`)

**When to use:** For all HTML generation that needs consistent page structure and navigation

**Example:**
```java
// Source: OrgDashboardHtmlHelper.java lines 30-38
public class OrgDashboardHtmlHelper extends BrowseHtmlHelper {

    public static String renderOrgDashboardHtml(OrgDashboard orgDashboard) {
        final String main = getOrgDashboardMainDiv(orgDashboard);
        final List<Pair<String, String>> breadCrumbs = getOrgDashboardBreadCrumb(
            orgDashboard.getCompanyPojo(), orgDashboard.getOrgPojo());
        return getPage(main, breadCrumbs, "dashboard-columns")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/dashboard.css\">" + ls);
    }
}
```

**Key points:**
- Static methods only (no instance state)
- `getPage(main, breadCrumbs)` for consistent page structure
- `getUrl(path)` for navigation links
- `getBreadCrumb(path)` for breadcrumb navigation

### Pattern 3: Nested Fieldsets for Hierarchy
**What:** Use nested HTML fieldsets to represent hierarchical data (branch contains sha contains job)

**When to use:** When rendering multi-level hierarchical data with navigation links

**Example:**
```java
// Source: OrgDashboardHtmlHelper.java lines 50-68
str.append("<fieldset class=\"repo-fieldset fieldset-group\">").append(ls);
str.append("  <legend class='repo-legend'>repo: <a href=\"{repoUrl}\">{repoName}</a></legend>")
    .replace("{repoUrl}", repoUrl)
    .replace("{repoName}", repoGraph.repoName());

for (BranchGraph branchGraph : repoGraph.branches()) {
    str.append("<fieldset class=\"branch-fieldset fieldset-group\">").append(ls);
    str.append("  <legend>branch: <a href=\"{branchUrl}\">{branchName}</a></legend>")
        .replace("{branchUrl}", branchUrl)
        .replace("{branchName}", branchGraph.branchName());

    // ... nested content
    str.append("</fieldset><!--end-branch-fieldset-->").append(ls);
}
str.append("</fieldset><!--end-repo-fieldset-->").append(ls);
```

**Key points:**
- Outer fieldset for parent level
- Inner fieldsets for children
- Legend contains navigation link to browse page
- CSS classes for styling (`fieldset-group`, `branch-fieldset`)

### Pattern 4: HTML Forms with Query Parameters
**What:** HTML forms use JavaScript to construct query parameters and submit via GET

**When to use:** For search/filter interfaces that need to preserve query state in URL

**Example:**
```java
// Source: PipelineDashboardHtmlHelper.java lines 35-58
sb.append("<fieldset style='margin: 20px 0; padding: 15px;'>").append(ls);
sb.append("<legend>Filter by Job Info</legend>").append(ls);
sb.append("<form method='get'>").append(ls);
sb.append("<label for='tags'>Tag Expression:</label>").append(ls);
sb.append("<input type='text' id='tags' name='tags' placeholder='smoke AND env=prod'>").append(ls);
sb.append("<button type='submit'>Search</button>").append(ls);
sb.append("</form>").append(ls);
sb.append("</fieldset>").append(ls);
```

**Key points:**
- `method='get'` preserves query in URL (shareable links)
- JavaScript for complex param construction (if needed)
- Populate form from URL params on page load (for back button)

### Pattern 5: Optional Query Parameter Handling
**What:** Controllers use `@RequestParam(required = false)` for optional parameters

**When to use:** When parameter absence triggers different behavior (e.g., show form vs show results)

**Example:**
```java
// Source: BrowseUIController.java lines 60-70
@GetMapping(path = {"company/{company}/org/{org}/repo/{repo}/branch/{branch}"})
public ResponseEntity<String> getBranchJobRuns(
        @PathVariable String company,
        @PathVariable String org,
        @PathVariable String repo,
        @PathVariable String branch,
        @RequestParam(required = false, defaultValue = "60") Integer runs) {
    // ... use runs parameter
}
```

**For TagQueryUIController:**
```java
@GetMapping(path = "company/{company}/tags/tests", produces = "text/html;charset=UTF-8")
public ResponseEntity<String> searchByTagsCompany(
        @PathVariable String company,
        @RequestParam(required = false) String tags) {

    if (tags == null || tags.isBlank()) {
        // Return search form
        return new ResponseEntity<>(
            TagQueryHtmlHelper.renderTagQueryPage(null, scopePath), HttpStatus.OK);
    }

    try {
        // Execute query and return results
        var results = tagQueryService.findByTagExpressionByPath(tags, company, null, null, null, null);
        var response = TagQueryResponse.builder()
            .query(QueryInfo.builder().scope(company).tags(tags).build())
            .results(results)
            .build();
        return new ResponseEntity<>(
            TagQueryHtmlHelper.renderTagQueryPage(response, scopePath), HttpStatus.OK);
    } catch (ParseException e) {
        // Return error with form
        return new ResponseEntity<>(
            TagQueryHtmlHelper.renderErrorPage(e.getMessage(), scopePath), HttpStatus.BAD_REQUEST);
    }
}
```

### Pattern 6: Navigation Link Integration
**What:** Add links to new functionality in existing pages' link fieldsets

**When to use:** For cross-linking related features (browse -> tag search, tag search -> browse)

**Example:**
```java
// Source: BrowseHtmlHelper.java lines 58-70 (getCompanyLinks)
public static String getCompanyLinks(String company) {
    return
        """
        <fieldset>
        <legend>{companyName} links</legend>
            {metricsLink}
        </fieldset>
        """
        .replace("{companyName}", company)
        .replace("{metricsLink}", getLink(company + " Metrics", "/metrics/company/" + company));
}
```

**For Tag Search integration:**
```java
// Add to BrowseHtmlHelper.getCompanyLinks() and getOrgLinks()
.replace("{tagSearchLink}", getLink("Tag Search", path.toUrlPath() + "/tags/tests"))
```

### Anti-Patterns to Avoid
- **Creating separate template files:** Codebase uses inline string templates, not external HTML files
- **Using request parameters for path components:** Use `@PathVariable` for hierarchy (company/org/repo), `@RequestParam` for filters (tags, runs)
- **Throwing exceptions for empty params:** Return HTML form instead (better UX)
- **Hard-coding URLs:** Use `getUrl(path)` helper for consistency

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| URL construction for browse paths | String concatenation | `BrowseHtmlHelper.getUrl(path)` | Consistent format, handles null values |
| Breadcrumb navigation | Manual HTML list | `BrowseHtmlHelper.getBreadCrumb(path)` | Standard format, automatic path truncation |
| Page structure (header/nav/main) | Copy-paste HTML | `BrowseHtmlHelper.getPage(main, breadCrumbs)` | Consistent across all pages, includes CSS/JS |
| Path truncation for hierarchy | String manipulation | `CompanyOrgRepoBranchJobRunStageDTO.truncateX()` | Type-safe, null-safe |
| Link generation | `<a href="...">` strings | `BrowseHtmlHelper.getLink(text, url)` | Consistent styling, escaping |

**Key insight:** BrowseHtmlHelper centralizes HTML generation utilities. Always check if a helper method exists before building custom HTML.

## Common Pitfalls

### Pitfall 1: Forgetting HTML Escaping
**What goes wrong:** User input (tag expressions) rendered directly in HTML causes XSS vulnerabilities or rendering issues

**Why it happens:** String concatenation doesn't escape HTML entities

**How to avoid:**
- For display: Use Apache Commons Lang `StringEscapeUtils.escapeHtml4()`
- For query params: Browser handles URL encoding automatically in form submission
- For error messages: Escape user input before embedding in HTML

**Warning signs:**
- Tag expression `<script>alert('xss')</script>` renders as executable script
- Expression with `&` or `<` breaks HTML structure

**Example:**
```java
// BAD
sb.append("<div>Query: " + tagExpression + "</div>");

// GOOD
sb.append("<div>Query: " + StringEscapeUtils.escapeHtml4(tagExpression) + "</div>");
```

### Pitfall 2: Missing `required = false` on Optional Parameters
**What goes wrong:** Spring returns 400 Bad Request when `?tags=` is missing, preventing form display

**Why it happens:** By default, `@RequestParam` parameters are required

**How to avoid:** Use `@RequestParam(required = false)` for optional parameters that trigger form display

**Warning signs:**
- Accessing `/company/testco/tags/tests` (no query param) returns 400 instead of showing form
- Browser logs "Missing required parameter: tags"

**Example:**
```java
// BAD - can't show form without tags param
public ResponseEntity<String> searchByTags(@RequestParam String tags) { }

// GOOD - shows form when tags is missing
public ResponseEntity<String> searchByTags(@RequestParam(required = false) String tags) {
    if (tags == null || tags.isBlank()) {
        return showSearchForm();
    }
    return showResults();
}
```

### Pitfall 3: Inconsistent Path Patterns
**What goes wrong:** HTML endpoints use different paths than JSON endpoints, breaking user mental model

**Why it happens:** Forgetting to check TagQueryController paths when creating UI controller

**How to avoid:**
- JSON paths: `/api/v1/company/{company}/tags/tests`
- HTML paths: `/company/{company}/tags/tests` (same without `/api/v1`)
- Keep path structure identical except for prefix

**Warning signs:**
- User replaces `/api/v1` in browser and gets 404
- Swagger docs and HTML paths diverge

### Pitfall 4: Not Handling ParseException
**What goes wrong:** Invalid tag expressions crash the controller or return generic error page

**Why it happens:** ParseException from TagQueryService not caught in HTML controller

**How to avoid:** Wrap service calls in try-catch, render error message with form

**Warning signs:**
- 500 Internal Server Error for invalid tag syntax
- Stack traces in browser
- No way for user to correct their query

**Example:**
```java
// BAD
var results = tagQueryService.findByTagExpressionByPath(tags, company, ...);
return renderResults(results);

// GOOD
try {
    var results = tagQueryService.findByTagExpressionByPath(tags, company, ...);
    return renderResults(results);
} catch (ParseException e) {
    return renderErrorWithForm(e.getMessage(), tags, scopePath);
}
```

### Pitfall 5: Not Linking Back to Browse Pages
**What goes wrong:** Users see tag search results but can't navigate to job/run/stage pages

**Why it happens:** Results rendered as plain text without clickable links

**How to avoid:**
- Use `getUrl(path)` to generate browse page URLs
- Extract hierarchy info from results (branch, sha, jobInfo)
- Construct `CompanyOrgRepoBranchJobRunStageDTO` for each result level
- Wrap names in `<a href="{url}">{name}</a>`

**Warning signs:**
- Results show "main / abc123 / default" as text
- No way to click through to test details
- Users copy-paste paths manually

### Pitfall 6: Form State Loss on Error
**What goes wrong:** Invalid expression clears the form, user must retype query

**Why it happens:** Error page renders empty form instead of pre-populating with failed query

**How to avoid:** Pass original query string to error page renderer, populate form input value

**Example:**
```java
// Error page form
sb.append("<input type='text' id='tags' name='tags' value='" +
    StringEscapeUtils.escapeHtml4(originalQuery) + "'>");
```

## Code Examples

Verified patterns from existing codebase:

### Controller Structure
```java
// Pattern: HTML controller delegates to helper
// Source: BrowseUIController.java lines 18-46
@RestController
@RequestMapping("")
public class TagQueryUIController {

    private final TagQueryService tagQueryService;

    @Autowired
    public TagQueryUIController(TagQueryService tagQueryService) {
        this.tagQueryService = tagQueryService;
    }

    @GetMapping(path = "company/{company}/tags/tests", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> searchByTagsCompany(
            @PathVariable String company,
            @RequestParam(required = false) String tags) {

        CompanyOrgRepoBranchJobRunStageDTO scopePath =
            CompanyOrgRepoBranchJobRunStageDTO.builder().company(company).build();

        if (tags == null || tags.isBlank()) {
            return new ResponseEntity<>(
                TagQueryHtmlHelper.renderSearchForm(scopePath), HttpStatus.OK);
        }

        try {
            var results = tagQueryService.findByTagExpressionByPath(
                tags, company, null, null, null, null);
            var response = TagQueryResponse.builder()
                .query(TagQueryResponse.QueryInfo.builder()
                    .scope(company).tags(tags).build())
                .results(results)
                .build();
            return new ResponseEntity<>(
                TagQueryHtmlHelper.renderResults(response, scopePath), HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>(
                TagQueryHtmlHelper.renderError(e.getMessage(), tags, scopePath),
                HttpStatus.BAD_REQUEST);
        }
    }
}
```

### Helper Structure
```java
// Pattern: Extend BrowseHtmlHelper for utilities
// Source: OrgDashboardHtmlHelper.java lines 30-38
public class TagQueryHtmlHelper extends BrowseHtmlHelper {

    public static String renderSearchForm(CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        final String form =
            """
            <fieldset>
              <legend>Tag Search</legend>
              <form method='get'>
                <label for='tags'>Tag Expression:</label>
                <input type='text' id='tags' name='tags'
                    placeholder='smoke AND env=prod' style='width: 400px;'>
                <button type='submit'>Search</button>
              </form>
              <div class='info'>
                Syntax: tag1 AND tag2, tag1 OR tag2, key=value, (expr1) OR (expr2)
              </div>
            </fieldset>
            """;
        return getPage(form, getBreadCrumb(scopePath));
    }

    public static String renderResults(TagQueryResponse response,
                                       CompanyOrgRepoBranchJobRunStageDTO scopePath) {
        StringBuilder sb = new StringBuilder();

        // Echo query
        sb.append("<div class='query-info'>").append(ls);
        sb.append("  Scope: ").append(response.getQuery().getScope()).append(ls);
        sb.append("  Tags: ").append(StringEscapeUtils.escapeHtml4(
            response.getQuery().getTags())).append(ls);
        sb.append("</div>").append(ls);

        // Render nested hierarchy
        for (var branchEntry : response.getResults().entrySet()) {
            sb.append(renderBranch(branchEntry, scopePath));
        }

        return getPage(sb.toString(), getBreadCrumb(scopePath));
    }
}
```

### Nested Fieldset Rendering
```java
// Pattern: Nested fieldsets for hierarchy
// Source: OrgDashboardHtmlHelper.java lines 50-78
private static String renderBranch(
        Map.Entry<String, Map<String, Map<String, JobResult>>> branchEntry,
        CompanyOrgRepoBranchJobRunStageDTO scopePath) {

    String branchName = branchEntry.getKey();
    CompanyOrgRepoBranchJobRunStageDTO branchPath =
        scopePath.toBuilder().branch(branchName).build();

    StringBuilder sb = new StringBuilder();
    sb.append("<fieldset class='branch-fieldset'>").append(ls);
    sb.append("  <legend>Branch: <a href='")
        .append(getUrl(branchPath))
        .append("'>")
        .append(branchName)
        .append("</a></legend>").append(ls);

    for (var shaEntry : branchEntry.getValue().entrySet()) {
        sb.append(renderSha(shaEntry, branchPath));
    }

    sb.append("</fieldset>").append(ls);
    return sb.toString();
}

private static String renderSha(
        Map.Entry<String, Map<String, JobResult>> shaEntry,
        CompanyOrgRepoBranchJobRunStageDTO branchPath) {

    String sha = shaEntry.getKey();
    // Note: Can't link to sha directly (no SHA page in browse)
    // Just render as label

    StringBuilder sb = new StringBuilder();
    sb.append("<fieldset class='sha-fieldset'>").append(ls);
    sb.append("  <legend>SHA: ").append(truncatedSha(sha)).append("</legend>").append(ls);

    for (var jobEntry : shaEntry.getValue().entrySet()) {
        sb.append(renderJob(jobEntry, branchPath, sha));
    }

    sb.append("</fieldset>").append(ls);
    return sb.toString();
}

private static String renderJob(
        Map.Entry<String, JobResult> jobEntry,
        CompanyOrgRepoBranchJobRunStageDTO branchPath,
        String sha) {

    String jobInfo = jobEntry.getKey();
    JobResult result = jobEntry.getValue();

    // Note: Can't construct job URL without jobId (jobInfo is not unique key)
    // Just render tests

    StringBuilder sb = new StringBuilder();
    sb.append("<div class='job-result'>").append(ls);
    sb.append("  <strong>Job:</strong> ").append(StringEscapeUtils.escapeHtml4(jobInfo)).append(ls);
    sb.append("  <strong>Run Date:</strong> ").append(result.getRunDate()).append(ls);
    sb.append("  <strong>Tests:</strong> ").append(ls);
    sb.append("  <ul>").append(ls);
    for (String test : result.getTests()) {
        sb.append("    <li>").append(StringEscapeUtils.escapeHtml4(test)).append("</li>").append(ls);
    }
    sb.append("  </ul>").append(ls);
    sb.append("</div>").append(ls);
    return sb.toString();
}
```

### Error Handling
```java
// Pattern: Error page with form pre-populated
public static String renderError(String errorMessage, String originalQuery,
                                 CompanyOrgRepoBranchJobRunStageDTO scopePath) {
    StringBuilder sb = new StringBuilder();

    sb.append("<div class='error' style='color: red; margin: 20px;'>").append(ls);
    sb.append("  <strong>Error:</strong> ").append(StringEscapeUtils.escapeHtml4(errorMessage)).append(ls);
    sb.append("</div>").append(ls);

    // Re-render form with original query
    sb.append("<fieldset>").append(ls);
    sb.append("  <legend>Tag Search</legend>").append(ls);
    sb.append("  <form method='get'>").append(ls);
    sb.append("    <label for='tags'>Tag Expression:</label>").append(ls);
    sb.append("    <input type='text' id='tags' name='tags' value='")
        .append(StringEscapeUtils.escapeHtml4(originalQuery))
        .append("' style='width: 400px;'>").append(ls);
    sb.append("    <button type='submit'>Search</button>").append(ls);
    sb.append("  </form>").append(ls);
    sb.append("</fieldset>").append(ls);

    return getPage(sb.toString(), getBreadCrumb(scopePath));
}
```

### Adding Navigation Links
```java
// Pattern: Add links to existing pages
// Modify BrowseHtmlHelper.getCompanyLinks() around line 58
public static String getCompanyLinks(String company) {
    CompanyOrgRepoBranchJobRunStageDTO path =
        CompanyOrgRepoBranchJobRunStageDTO.builder().company(company).build();

    return
        """
        <fieldset>
        <legend>{companyName} links</legend>
            {metricsLink}<br>
            {tagSearchLink}
        </fieldset>
        """
        .replace("{companyName}", company)
        .replace("{metricsLink}", getLink(company + " Metrics", "/metrics/company/" + company))
        .replace("{tagSearchLink}", getLink("Tag Search", path.toUrlPath() + "/tags/tests"));
}

// Modify BrowseHtmlHelper.getOrgLinks() around line 117
public static String getOrgLinks(String org, CompanyOrgRepoBranchJobRunStageDTO path) {
    final CompanyOrgRepoBranchJobRunStageDTO orgPath =
        CompanyOrgRepoBranchJobRunStageDTO.truncateOrg(path);
    return
        """
        <fieldset>
        <legend>{orgName} links</legend>
            {dashboardLink}<br>
            {jobDashboardLink}<br>
            {metricsLink}<br>
            {tagSearchLink}
        </fieldset>
        """
        .replace("{orgName}", org)
        .replace("{dashboardLink}", getLink(org + " Dashboard", orgPath.toUrlPath() + "/dashboard?days=30"))
        .replace("{jobDashboardLink}", getLink(org + " Pipelines", orgPath.toUrlPath() + "/pipelines?days=90"))
        .replace("{metricsLink}", getLink(org + " Metrics", "/metrics" + orgPath.toUrlPath()))
        .replace("{tagSearchLink}", getLink("Tag Search", orgPath.toUrlPath() + "/tags/tests"));
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| JSP/Thymeleaf templates | String templates (Java text blocks) | N/A (project baseline) | No external template files, all Java |
| Template engines | String concatenation with replace() | N/A (project baseline) | Simple, no magic, easy to debug |
| Client-side routing | Server-side MVC | N/A (project baseline) | Full page loads, shareable URLs |
| Request scoped beans | Stateless service injection | N/A (project baseline) | Thread-safe, simple |

**Current patterns:**
- Java 15+ text blocks for multiline strings (project uses Java 17)
- Lombok `@Builder` for DTOs (reduces boilerplate)
- Spring Boot 2.6.x (older but stable)
- WebMvcTest for controller unit tests (fast, mocked services)

**Deprecated/outdated:**
- N/A - Project patterns are consistent and stable

## Open Questions

Things that couldn't be fully resolved:

1. **Navigation to job/run pages from tag search results**
   - What we know: Results grouped by branch/sha/jobInfo, but jobInfo is not unique identifier (multiple jobs can have same jobInfo)
   - What's unclear: How to construct browse page URL without jobId (requires database lookup)
   - Recommendation: Link to branch page only, or add jobId to TagQueryResponse structure

2. **SHA-level browse pages**
   - What we know: BrowseUIController has no `/branch/{branch}/sha/{sha}` endpoint
   - What's unclear: Should tag search link to SHA-level pages, or skip to run/job pages?
   - Recommendation: Skip SHA links (just render as label), link directly to job pages when possible

3. **HTML escaping library**
   - What we know: Codebase imports Apache Commons Lang (seen in other files)
   - What's unclear: Whether `StringEscapeUtils.escapeHtml4()` is available or if another method should be used
   - Recommendation: Verify in imports, use Spring's `HtmlUtils.htmlEscape()` if Commons not available

4. **CSS styling for tag search pages**
   - What we know: shared.css exists with fieldset/legend styles
   - What's unclear: Whether additional CSS needed for tag search form/results
   - Recommendation: Use existing CSS classes (`fieldset-group`, `branch-fieldset`), add custom if needed via inline styles

## Sources

### Primary (HIGH confidence)
- BrowseHtmlHelper.java - Core HTML generation patterns
- BrowseUIController.java - HTML controller structure
- OrgDashboardHtmlHelper.java - Nested fieldset pattern
- PipelineDashboardHtmlHelper.java - Form handling pattern
- TagQueryController.java - API endpoints to mirror
- TagQueryResponse.java - Data structure to render
- TagQueryService.java - Service methods to call
- TagQueryControllerTest.java - Test patterns (WebMvcTest)

### Secondary (MEDIUM confidence)
- Phase 09 plan files - Design decisions and rationale
- shared.css - Existing styles for fieldsets/legends

### Tertiary (LOW confidence)
- N/A

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Direct inspection of pom.xml equivalent (Gradle) and existing code
- Architecture: HIGH - Multiple similar examples in codebase (BrowseUI, OrgDashboard, PipelineDashboard)
- Pitfalls: MEDIUM - Inferred from code patterns, not documented explicitly
- Navigation linking: MEDIUM - Unclear how to link to jobs without jobId in results

**Research date:** 2026-02-17
**Valid until:** 2026-03-19 (30 days - stable Spring Boot patterns)

**Key risks:**
- TagQueryResponse structure may not include enough data for full navigation links (needs jobId)
- HTML escaping method needs verification (Apache Commons vs Spring)
- Form state preservation on error needs careful implementation
