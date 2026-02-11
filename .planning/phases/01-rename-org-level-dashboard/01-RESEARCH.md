# Phase 1: Rename Org-Level Dashboard - Research

**Researched:** 2026-02-11
**Domain:** Spring Boot 2.6.15 REST API URL/UI renaming
**Confidence:** HIGH

## Summary

This phase involves renaming the org-level "pipelines" dashboard to "jobs" terminology in URLs and UI. The technical approach is straightforward using Spring MVC's standard @GetMapping annotations and HTTP 301 redirects.

**Current state:**
- Endpoint: `GET /company/{company}/org/{org}/pipelines` (line 210 in GraphUIController)
- HTML title: "Org Pipelines" (line 25 in PipelineDashboardHtmlHelper)
- Link text: "Pipelines ⏲" (line 130 in BrowseHtmlHelper)

**Standard approach:**
1. Add new `/jobs` endpoint with identical logic
2. Add redirect from old `/pipelines` endpoint to `/jobs`
3. Update HTML generation (title and breadcrumb link)
4. No internal code rename (DB/models stay as-is per user decision)

**Primary recommendation:** Use Spring MVC's `RedirectView` for backwards-compatible URL forwarding with HTTP 301 status code to preserve SEO and bookmarks.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 2.6.15 | Web framework | Project's base framework, well-documented |
| Spring Web MVC | 5.3.x (via Boot) | HTTP routing | Native to Spring Boot, handles redirects |
| Lombok | (existing) | Reduce boilerplate | Already used throughout codebase |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| JUnit 5 | (existing) | Unit testing | Test redirect behavior |
| Testcontainers | (existing) | Integration testing | Already used in codebase tests |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| RedirectView | Manual redirect string "redirect:/..." | RedirectView is cleaner, explicit control over status code |
| HTTP 301 | HTTP 302 | 301 is permanent (better for SEO), 302 is temporary |

**Installation:**
No new dependencies required - all capabilities exist in Spring Boot 2.6.15.

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/
├── controller/
│   └── graph/
│       ├── GraphUIController.java           # Add new endpoint, redirect
│       └── PipelineDashboardHtmlHelper.java # Update title text
└── controller/browse/
    └── BrowseHtmlHelper.java                # Update link text
```

### Pattern 1: URL Renaming with Backwards Compatibility
**What:** Create new endpoint, redirect old endpoint
**When to use:** URL schema changes where old URLs must continue working
**Example:**
```java
// Source: Spring MVC 5.3 documentation patterns
// New endpoint (primary)
@GetMapping(path = "company/{company}/org/{org}/jobs", produces = "text/html;charset=UTF-8")
public ResponseEntity<String> getJobDashboard(
        @PathVariable String company,
        @PathVariable String org,
        @RequestParam(required = false) List<String> jobInfo,
        @RequestParam(required = false, defaultValue = "90") Integer days) {
    // Original logic here
}

// Old endpoint (redirect)
@GetMapping(path = "company/{company}/org/{org}/pipelines")
public RedirectView redirectPipelinesToJobs(
        @PathVariable String company,
        @PathVariable String org,
        @RequestParam(required = false) List<String> jobInfo,
        @RequestParam(required = false, defaultValue = "90") Integer days,
        HttpServletRequest request) {

    String queryString = request.getQueryString();
    String redirectUrl = String.format("/company/%s/org/%s/jobs", company, org);
    if (queryString != null && !queryString.isEmpty()) {
        redirectUrl += "?" + queryString;
    }

    RedirectView redirectView = new RedirectView(redirectUrl);
    redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // HTTP 301
    return redirectView;
}
```

### Pattern 2: String-Based HTML Generation
**What:** This codebase uses string concatenation for HTML (not templates)
**When to use:** Matches existing codebase patterns
**Example:**
```java
// Source: PipelineDashboardHtmlHelper.java lines 22-26
private static String renderPipelineDashboardMain(List<JobDashboardMetrics> metrics, String pipeline, Integer days) {
    StringBuilder sb = new StringBuilder();
    String title = pipeline != null && !pipeline.trim().isEmpty() ? pipeline : "All Jobs"; // CHANGED
    sb.append("<h1>Org Jobs - ").append(title).append("</h1>").append(ls); // CHANGED
    // ...
}
```

### Pattern 3: Navigation Link Updates
**What:** Update hardcoded link text in BrowseHtmlHelper
**When to use:** UI terminology changes
**Example:**
```java
// Source: BrowseHtmlHelper.java line 130
// BEFORE:
.replace("{jobDashboardLink}", "<a href='" + orgPath.toUrlPath() + "/pipelines?days=90' style='text-decoration: none;'>" + org + " Pipelines ⏲</a>")

// AFTER:
.replace("{jobDashboardLink}", "<a href='" + orgPath.toUrlPath() + "/jobs?days=90' style='text-decoration: none;'>" + org + " Jobs ⏲</a>")
```

### Anti-Patterns to Avoid
- **Removing old endpoint:** Old URLs must redirect, not 404
- **Breaking query parameters:** Redirect must preserve `?jobInfo=...&days=...` parameters
- **Renaming internal code:** User decided DB and internal models stay as "pipeline" (avoids churn)

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| HTTP redirects | Manual response.setHeader() | Spring RedirectView | Handles edge cases, status codes, URL encoding |
| Query parameter forwarding | String parsing/rebuilding | HttpServletRequest.getQueryString() | Already parsed, encoded correctly |
| Path variable encoding | Manual URL encoding | Spring's @PathVariable | Auto-decodes/encodes special chars |

**Key insight:** Spring MVC already handles all URL manipulation concerns. Use its built-in classes rather than string manipulation.

## Common Pitfalls

### Pitfall 1: Query Parameter Loss in Redirect
**What goes wrong:** User clicks old `/pipelines?jobInfo=app:foo&days=60` link, redirect drops parameters
**Why it happens:** Forgetting to preserve query string when building redirect URL
**How to avoid:** Use `HttpServletRequest.getQueryString()` and append to redirect URL
**Warning signs:** Tests with query parameters fail, users report lost filter settings

### Pitfall 2: Wrong HTTP Status Code
**What goes wrong:** Using HTTP 302 (temporary) instead of 301 (permanent)
**Why it happens:** RedirectView defaults to 302 in some Spring versions
**How to avoid:** Explicitly set `redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY)`
**Warning signs:** Search engines don't update indexed URLs, browser doesn't cache redirect

### Pitfall 3: Incomplete UI Updates
**What goes wrong:** URL works but old terminology appears in UI
**Why it happens:** Multiple locations reference "Pipelines" text
**How to avoid:** Search entire codebase for "Pipelines" and "pipelines" strings
**Warning signs:** Inconsistent terminology across pages

**Verified locations needing changes:**
- GraphUIController.java line 210: URL path
- PipelineDashboardHtmlHelper.java line 25: page title "Org Pipelines"
- BrowseHtmlHelper.java line 130: navigation link text "Pipelines ⏲"

## Code Examples

Verified patterns from Spring Boot 2.6 and existing codebase:

### RedirectView with Query Parameters
```java
// Source: Spring MVC 5.3 RedirectView documentation
@GetMapping(path = "company/{company}/org/{org}/pipelines")
public RedirectView redirectPipelinesToJobs(
        @PathVariable String company,
        @PathVariable String org,
        HttpServletRequest request) {

    // Build redirect URL preserving path variables
    String redirectUrl = String.format("/company/%s/org/%s/jobs", company, org);

    // Preserve query parameters
    String queryString = request.getQueryString();
    if (queryString != null && !queryString.isEmpty()) {
        redirectUrl += "?" + queryString;
    }

    // Create permanent redirect
    RedirectView redirectView = new RedirectView(redirectUrl);
    redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // HTTP 301
    return redirectView;
}
```

### Endpoint Duplication Pattern
```java
// Source: GraphUIController.java lines 210-227
// BEFORE (single endpoint):
@GetMapping(path = "company/{company}/org/{org}/pipelines", produces = "text/html;charset=UTF-8")
public ResponseEntity<String> getJobDashboard(...) {
    // logic
}

// AFTER (two endpoints - new primary, old redirect):
@GetMapping(path = "company/{company}/org/{org}/jobs", produces = "text/html;charset=UTF-8")
public ResponseEntity<String> getJobDashboard(...) {
    // same logic as before
}

@GetMapping(path = "company/{company}/org/{org}/pipelines")
public RedirectView redirectPipelinesToJobs(...) {
    // redirect to /jobs
}
```

### HTML Title Update
```java
// Source: PipelineDashboardHtmlHelper.java line 24-25
// BEFORE:
String title = pipeline != null && !pipeline.trim().isEmpty() ? pipeline : "All Pipelines";
sb.append("<h1>Org Pipelines - ").append(title).append("</h1>").append(ls);

// AFTER:
String title = pipeline != null && !pipeline.trim().isEmpty() ? pipeline : "All Jobs";
sb.append("<h1>Org Jobs - ").append(title).append("</h1>").append(ls);
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| HTTP 302 temporary redirects | HTTP 301 permanent redirects | Spring MVC 3.x+ | Search engines update indexes, browsers cache |
| Manual redirect strings | RedirectView class | Spring 3.0+ | Cleaner code, better status control |
| Template engines (JSP/Thymeleaf) | String-based HTML | This codebase | Matches existing pattern |

**Deprecated/outdated:**
- Manual response headers: Spring provides RedirectView
- Removing old URLs: Modern practice is permanent redirects

## Open Questions

No significant open questions. The approach is well-defined:

1. **Domain/technology:** Spring Boot 2.6.15 with Spring MVC 5.3.x - stable, mature, well-documented
2. **Pattern:** URL redirect with RedirectView - standard Spring MVC pattern since version 3.0
3. **Impact:** Low-risk change (no DB, no internal refactoring per user decision)

## Sources

### Primary (HIGH confidence)
- Spring Boot 2.6.15 project configuration (gradle.properties)
- GraphUIController.java (existing endpoint at line 210)
- PipelineDashboardHtmlHelper.java (existing HTML generation)
- BrowseHtmlHelper.java (existing navigation links at line 130)

### Secondary (MEDIUM confidence)
- Spring Framework 5.3 documentation - RedirectView API (current for Spring Boot 2.6.x)
- Spring MVC best practices - URL versioning and redirects

### Tertiary (LOW confidence)
- None - all findings verified with codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - All capabilities exist in current Spring Boot 2.6.15
- Architecture: HIGH - Verified existing code patterns in GraphUIController and helper classes
- Pitfalls: HIGH - Based on direct codebase inspection and Spring MVC documentation

**Research date:** 2026-02-11
**Valid until:** 2026-03-13 (30 days - Spring Boot 2.6.x is stable/maintenance mode)
