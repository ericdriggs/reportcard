# Phase 2: Add Company-Level Dashboard - Research

**Researched:** 2026-02-11
**Domain:** Spring Boot 2.6.15 REST API - Multi-org data aggregation
**Confidence:** HIGH

## Summary

This phase adds a company-level jobs dashboard that aggregates data across all orgs within a company. The technical approach mirrors the existing org-level pattern (Phase 1) but removes the org-specific filter from the service layer.

**Current state:**
- Org-level endpoint exists at `/company/{company}/org/{org}/jobs` (line 213, GraphUIController)
- Service method `getPipelineDashboard(request)` accepts org parameter (line 588, GraphService)
- Database hierarchy: company → org → repo → branch → job (FK relationships in schema)
- HTML table already includes company and org columns (lines 120-121, PipelineDashboardHtmlHelper)

**Standard approach:**
1. Create new endpoint `/company/{company}/jobs` (no org parameter)
2. Modify `JobDashboardRequest` to make org optional
3. Update `GraphService.getPipelineDashboardCompanyGraphs()` to conditionally apply org filter
4. Add company-level link to `BrowseHtmlHelper.getCompanyLinks()` (line 58)
5. Reuse existing HTML table (already has org column for grouping)

**Primary recommendation:** Minimal code changes - make org parameter optional in existing service method rather than creating separate company-level method. The HTML table already displays org column, providing natural grouping.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 2.6.15 | Web framework | Project's base framework |
| JOOQ | (via Boot) | Type-safe SQL | Project's database access layer |
| Lombok | (existing) | Reduce boilerplate | Already used throughout |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| JUnit 5 | (existing) | Unit testing | Test new endpoint |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Optional org parameter | Separate company-level service method | Optional parameter reuses logic, separate method duplicates code |
| Reuse HTML helper | New company-specific HTML helper | Existing table already has org column, no need to duplicate |

**Installation:**
No new dependencies required - all capabilities exist in current stack.

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/java/
├── controller/
│   └── graph/
│       ├── GraphUIController.java           # Add company-level endpoint
│       └── PipelineDashboardHtmlHelper.java # No changes needed (already has org column)
├── model/pipeline/
│   └── JobDashboardRequest.java             # Make org field optional
├── persist/
│   └── GraphService.java                    # Conditionally apply org filter
└── controller/browse/
    └── BrowseHtmlHelper.java                # Add company jobs link
```

### Pattern 1: Optional Parameter in Service Layer
**What:** Make org parameter optional to support both org-level and company-level queries
**When to use:** When logic is identical except for single filter condition
**Example:**
```java
// Source: Analyzed from GraphService.getPipelineDashboardCompanyGraphs (line 593-644)
List<CompanyGraph> getPipelineDashboardCompanyGraphs(JobDashboardRequest request) {
    TableConditionMap tableConditionMap = new TableConditionMap();
    tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(request.getCompany()));

    // NEW: Only apply org filter if org is provided
    if (request.getOrg() != null) {
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(request.getOrg()));
    }

    // Build conditions for lightweight runIds query
    Condition companyCondition = COMPANY.COMPANY_NAME.eq(request.getCompany());
    Condition orgCondition = request.getOrg() != null ?
        ORG.ORG_NAME.eq(request.getOrg()) :
        trueCondition(); // No org filter for company-level

    // ... rest of method unchanged
}
```

### Pattern 2: Controller Endpoint with Optional Path Variable
**What:** New endpoint omits org path segment entirely
**When to use:** Company-level view (aggregates across all orgs)
**Example:**
```java
// Source: Spring MVC pattern from existing org-level endpoint (line 213)
@GetMapping(path = "company/{company}/jobs", produces = "text/html;charset=UTF-8")
public ResponseEntity<String> getCompanyJobDashboard(
        @PathVariable String company,
        @RequestParam(required = false) List<String> jobInfo,
        @RequestParam(required = false, defaultValue = "90") Integer days
) {
    Map<String, String> jobInfoMap = JobInfoParser.parseJobInfoParams(jobInfo);

    JobDashboardRequest request = JobDashboardRequest.builder()
            .company(company)
            // org is null for company-level
            .jobInfos(jobInfoMap)
            .days(days)
            .build();
    List<JobDashboardMetrics> metrics = graphService.getPipelineDashboard(request);
    return new ResponseEntity<>(
        PipelineDashboardHtmlHelper.renderPipelineDashboardMetrics(metrics, request),
        HttpStatus.OK
    );
}
```

### Pattern 3: Reuse HTML Table with Existing Org Column
**What:** HTML table already includes company and org columns for natural grouping
**When to use:** Company-level view shows org context
**Example:**
```java
// Source: PipelineDashboardHtmlHelper.renderPipelineTable (lines 114-172)
// NO CHANGES NEEDED - table already renders both columns:
sb.append("<th>Company</th>").append(ls);
sb.append("<th>Org</th>").append(ls);      // Already present!
sb.append("<th>Repo</th>").append(ls);
// ...
sb.append("<td>").append(metric.getCompany()).append("</td>");
sb.append("<td>").append(metric.getOrg()).append("</td>");  // Shows org for grouping
```

### Pattern 4: Add Company Navigation Link
**What:** Update company page to include link to company jobs dashboard
**When to use:** Provide navigation to new feature
**Example:**
```java
// Source: BrowseHtmlHelper.getCompanyLinks (line 58-70)
public static String getCompanyLinks(String company) {
    return
        """
        <fieldset>
        <legend>{companyName} links</legend>
            {jobsLink}<br>
            {metricsLink}
        </fieldset>
        """
                .replace("{companyName}", company)
                .replace("{jobsLink}", "<a href='/company/" + company + "/jobs?days=90' style='text-decoration: none;'>" + company + " Jobs ⏲</a>")
                .replace("{metricsLink}", getLink(company + " Metrics 🔢", "/metrics/company/" + company));
}
```

### Anti-Patterns to Avoid
- **Duplicating service logic:** Don't create separate `getCompanyPipelineDashboard()` method - make org optional in existing method
- **Creating new HTML table:** Existing table already has org column - reuse it
- **Filtering org in controller:** Apply org filter in service layer where other filters live

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Multi-org aggregation | Custom aggregation logic | Remove org filter, let JOOQ join all orgs | Database already has FK relationships, JOOQ handles joins |
| Org column display | New HTML template | Existing table at line 114-172 | Table already renders org column (line 121, 135) |
| Query parameter parsing | Custom parser | Existing `JobInfoParser.parseJobInfoParams()` | Already handles jobInfo syntax (line 220) |
| Conditional JOOQ filters | Manual SQL building | `trueCondition()` for no-op filter | JOOQ pattern for optional filters (line 601) |

**Key insight:** The database schema is designed for hierarchical queries (company → org → repo → branch → job). Removing the org filter naturally aggregates across all orgs.

## Common Pitfalls

### Pitfall 1: Forgetting Org Filter is Optional
**What goes wrong:** Service method crashes when request.getOrg() is null
**Why it happens:** Existing code assumes org is always present
**How to avoid:** Check `if (request.getOrg() != null)` before applying org condition
**Warning signs:** NullPointerException in GraphService, company endpoint returns 500

### Pitfall 2: Performance with Large Companies
**What goes wrong:** Company with 50+ orgs causes slow page load or timeout
**Why it happens:** Query aggregates across all orgs without org-specific index optimization
**How to avoid:**
- Keep 10,000 run limit (line 631) to prevent excessive memory
- Monitor query performance with EXPLAIN
- Consider adding company_fk index to job table if performance degrades
**Warning signs:** Page load >5 seconds, MySQL slow query log entries

### Pitfall 3: HTML Title Confusion
**What goes wrong:** Page title still says "Org Jobs" on company-level page
**Why it happens:** `renderPipelineDashboardMetrics()` builds title from request (line 174-176)
**How to avoid:** Update title logic to check if org is null:
```java
// Line 175 currently: String title = request.getCompany() + "/" + request.getOrg();
// Should be:
String title = request.getOrg() != null ?
    request.getCompany() + "/" + request.getOrg() :
    request.getCompany();
```
**Warning signs:** H1 shows "company/null" or incorrect breadcrumb

### Pitfall 4: Missing Navigation Link
**What goes wrong:** Users can't find company-level dashboard
**Why it happens:** Forgetting to add link to company page
**How to avoid:** Add "Company Jobs" link in `BrowseHtmlHelper.getCompanyLinks()` (line 58)
**Warning signs:** Feature works but users report "where is it?"

## Code Examples

Verified patterns from existing codebase analysis:

### Company-Level Endpoint
```java
// Source: Pattern from GraphUIController.getJobDashboard (lines 213-230)
@GetMapping(path = "company/{company}/jobs", produces = "text/html;charset=UTF-8")
public ResponseEntity<String> getCompanyJobDashboard(
        @PathVariable String company,
        @RequestParam(required = false) List<String> jobInfo,
        @RequestParam(required = false, defaultValue = "90") Integer days
) {
    Map<String, String> jobInfoMap = JobInfoParser.parseJobInfoParams(jobInfo);

    JobDashboardRequest request = JobDashboardRequest.builder()
            .company(company)
            // org is null for company-level view
            .jobInfos(jobInfoMap)
            .days(days)
            .build();
    List<JobDashboardMetrics> metrics = graphService.getPipelineDashboard(request);
    return new ResponseEntity<>(
        PipelineDashboardHtmlHelper.renderPipelineDashboardMetrics(metrics, request),
        HttpStatus.OK
    );
}
```

### Optional Org Filter in Service
```java
// Source: GraphService.getPipelineDashboardCompanyGraphs (lines 593-644)
List<CompanyGraph> getPipelineDashboardCompanyGraphs(JobDashboardRequest request) {
    TableConditionMap tableConditionMap = new TableConditionMap();
    tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(request.getCompany()));

    // Build conditions for lightweight runIds query
    Condition companyCondition = COMPANY.COMPANY_NAME.eq(request.getCompany());

    // NEW: Make org condition optional
    Condition orgCondition = request.getOrg() != null ?
        ORG.ORG_NAME.eq(request.getOrg()) :
        trueCondition();

    // Only add to tableConditionMap if org is specified
    if (request.getOrg() != null) {
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(request.getOrg()));
    }

    Condition jobCondition = trueCondition();
    Condition runCondition = trueCondition();

    // Filter by jobInfos (only if provided)
    if (request.getJobInfos() != null && !request.getJobInfos().isEmpty()) {
        for (Map.Entry<String, String> entry : request.getJobInfos().entrySet()) {
            Condition jobInfoCondition = SqlJsonUtil.jobInfoContainsKeyValue(entry.getKey(), entry.getValue());
            jobCondition = jobCondition.and(jobInfoCondition);
        }
        tableConditionMap.put(JOB, jobCondition);
    }

    // Filter by days
    if (request.getDays() != null) {
        Instant cutoff = Instant.now().minus(request.getDays(), ChronoUnit.DAYS);
        runCondition = runCondition.and(RUN.RUN_DATE.ge(cutoff));
    }

    // Step 1: Get runIds with lightweight query
    Long[] runIds = dsl.selectDistinct(RUN.RUN_ID.as("RUN_IDS"))
            .from(RUN)
            .innerJoin(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
            .innerJoin(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
            .innerJoin(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
            .innerJoin(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
            .innerJoin(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
            .where(companyCondition.and(orgCondition).and(jobCondition).and(runCondition))
            .orderBy(RUN.RUN_ID.desc())
            .limit(10000)
            .fetchArray("RUN_IDS", Long.class);

    tableConditionMap.put(RUN, RUN.RUN_ID.in(runIds));
    tableConditionMap.put(TEST_RESULT, TEST_RESULT.TEST_RESULT_ID.isNotNull());

    return getCompanyGraphs(tableConditionMap, false);
}
```

### HTML Title Update
```java
// Source: PipelineDashboardHtmlHelper.renderPipelineDashboardMetrics (lines 174-177)
public static String renderPipelineDashboardMetrics(
    List<JobDashboardMetrics> metrics,
    JobDashboardRequest request
) {
    // UPDATED: Handle company-level (no org)
    String title = request.getOrg() != null ?
        request.getCompany() + "/" + request.getOrg() :
        request.getCompany();
    return renderPipelineDashboard(metrics, title, request.getDays());
}
```

### Optional Org in Request Model
```java
// Source: JobDashboardRequest.java (lines 12-28)
@Builder
@Jacksonized
@Value
public class JobDashboardRequest {
    String company;
    String org; // ALREADY optional - no @NonNull annotation
    @Singular
    Map<String, String> jobInfos;
    @Builder.Default
    Integer days = 90;
    @Builder.Default
    Instant endDate = Instant.now();

    public Instant getStartDate() {
        return endDate.minus(days, ChronoUnit.DAYS);
    }
}
```

## State of the Art

| Pattern | Current Approach | When Changed | Impact |
|---------|------------------|--------------|--------|
| Multi-level dashboards | Separate endpoints per level | This phase | Company-level aggregates across orgs |
| Optional filters | `trueCondition()` for no-op | JOOQ best practice | Clean conditional filtering |
| HTML table reuse | Single table supports multiple contexts | Existing | Org column naturally groups company data |

**Key insight:** The codebase already supports multi-level queries through optional filters. The org-level endpoint (Phase 1) established the pattern - company-level just removes one filter.

## Open Questions

No significant open questions. The approach is well-defined:

1. **Database schema:** Company → org FK relationship verified in V1.0 schema (lines 40-52)
2. **Service pattern:** Optional filters using `trueCondition()` is established JOOQ pattern (line 601)
3. **HTML display:** Table already has org column (lines 120-121, 134-135) - no template changes needed
4. **Performance:** 10,000 run limit exists (line 631) to prevent memory issues with large result sets

## Sources

### Primary (HIGH confidence)
- GraphUIController.java - Existing org-level endpoint pattern (line 213-230)
- GraphService.getPipelineDashboardCompanyGraphs - Service layer filtering (lines 593-644)
- PipelineDashboardHtmlHelper.java - HTML table structure with org column (lines 114-172)
- JobDashboardRequest.java - Request model structure (lines 12-28)
- JobDashboardMetrics.java - Metrics calculation from CompanyGraphs (lines 37-105)
- BrowseHtmlHelper.getCompanyLinks - Company page links (line 58-70)
- V1.0__reportcard_mysql_ddl.sql - Database schema (company/org tables, lines 25-52)

### Secondary (MEDIUM confidence)
- JOOQ documentation - `trueCondition()` pattern for optional filters
- Spring MVC 5.3 - Path variable patterns

### Tertiary (LOW confidence)
- None - all findings verified with direct codebase inspection

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - No new dependencies, reuses existing Spring Boot + JOOQ patterns
- Architecture: HIGH - Verified by analyzing existing org-level implementation and service layer
- Pitfalls: HIGH - Based on code analysis and understanding of JOOQ query behavior
- Performance: MEDIUM - Existing 10k limit provides safety, but large multi-org queries untested

**Research date:** 2026-02-11
**Valid until:** 2026-03-13 (30 days - stable codebase, unlikely to change)

**Key findings:**
1. **Minimal changes needed** - org parameter already optional in request model
2. **HTML table ready** - org column exists (line 121), provides natural grouping
3. **Service pattern established** - `trueCondition()` handles optional filters cleanly
4. **Performance safeguard** - 10,000 run limit prevents memory issues
5. **Navigation needed** - Must add company jobs link to company page
