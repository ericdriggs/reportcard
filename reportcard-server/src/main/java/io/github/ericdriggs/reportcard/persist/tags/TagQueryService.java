package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Service for tag-based test result queries.
 *
 * <p>Coordinates parsing of tag expressions and execution of database queries.
 * Uses {@link TagExpressionParser} to parse boolean expressions and
 * {@link TagQueryBuilder} to generate efficient SQL queries.
 *
 * <p>OR queries use UNION for index usage (see {@link TagQueryBuilder}).
 */
@Service
@Slf4j
public class TagQueryService {

    private final DSLContext dsl;
    private final TagQueryBuilder queryBuilder;

    /**
     * Creates a TagQueryService with the given DSLContext.
     *
     * @param dsl the JOOQ DSLContext for database operations
     */
    @Autowired
    public TagQueryService(DSLContext dsl) {
        this.dsl = dsl;
        this.queryBuilder = new TagQueryBuilder(dsl);
    }

    /**
     * Find test results matching a tag expression.
     *
     * <p>Parses the expression and executes an efficient query using
     * UNION for OR expressions and single WHERE for AND expressions.
     *
     * @param expression Boolean tag expression (e.g., "smoke AND env=prod")
     * @return List of matching test result records
     * @throws ParseException if the expression is invalid
     */
    public List<TestResultRecord> findByTagExpression(String expression) {
        // Parse expression to AST
        TagExpr expr = TagExpressionParser.parse(expression);

        // Build and execute query
        Select<TestResultRecord> query = queryBuilder.buildQuery(expr);
        Result<TestResultRecord> results = query.fetch();

        return new ArrayList<>(results);
    }

    /**
     * Find test results matching a tag expression within a scope.
     *
     * <p>Filters results by optional hierarchy constraints (company, org, repo, branch).
     * Null values for scope filters indicate no filtering at that level.
     *
     * @param expression Boolean tag expression (e.g., "smoke AND env=prod")
     * @param companyId Scope filter (nullable for all companies)
     * @param orgId Scope filter (nullable)
     * @param repoId Scope filter (nullable)
     * @param branchId Scope filter (nullable)
     * @return List of matching test result records within scope
     * @throws ParseException if the expression is invalid
     */
    public List<TestResultRecord> findByTagExpression(
            String expression,
            Integer companyId,
            Integer orgId,
            Integer repoId,
            Integer branchId) {

        // Parse expression to AST
        TagExpr expr = TagExpressionParser.parse(expression);

        // Build base query from AST
        Select<TestResultRecord> baseQuery = queryBuilder.buildQuery(expr);

        // If no scope filters, execute directly
        if (companyId == null && orgId == null && repoId == null && branchId == null) {
            return new ArrayList<>(baseQuery.fetch());
        }

        // Build scoped query with joins
        // This wraps the tag-filtered query with hierarchy filtering
        var scopedQuery = dsl.selectFrom(TEST_RESULT)
            .where(TEST_RESULT.TEST_RESULT_ID.in(
                dsl.select(TEST_RESULT.TEST_RESULT_ID)
                    .from(baseQuery.asTable("tag_filtered"))
            ));

        // Add scope conditions via joins
        if (branchId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .where(BRANCH.BRANCH_ID.eq(branchId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        } else if (repoId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                        .where(REPO.REPO_ID.eq(repoId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        } else if (orgId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                        .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                        .where(ORG.ORG_ID.eq(orgId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        } else if (companyId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                        .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                        .join(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
                        .where(COMPANY.COMPANY_ID.eq(companyId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        }

        return new ArrayList<>(scopedQuery.fetch());
    }

    /**
     * Find test results matching a tag expression within a path-based scope.
     *
     * <p>Resolves path names to IDs and executes a scoped query.
     * Returns results grouped by remaining hierarchy levels.
     *
     * @param expression Boolean tag expression (e.g., "smoke AND env=prod")
     * @param company Company name (required)
     * @param org Org name (nullable)
     * @param repo Repo name (nullable)
     * @param branch Branch name (nullable)
     * @param sha SHA value (nullable) - filters by run.sha column
     * @return Results grouped by hierarchy: branch -> sha -> job -> JobResult
     * @throws ParseException if the expression is invalid
     */
    public Map<String, Map<String, Map<String, io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult>>> findByTagExpressionByPath(
            String expression,
            String company,
            String org,
            String repo,
            String branch,
            String sha) {

        // Resolve path to IDs
        Integer companyId = resolveCompanyId(company);
        if (companyId == null) {
            log.debug("Company not found: {}", company);
            return Collections.emptyMap();
        }
        Integer orgId = (org != null) ? resolveOrgId(companyId, org) : null;
        if (org != null && orgId == null) {
            log.debug("Org not found: {} in company {}", org, company);
            return Collections.emptyMap();
        }
        Integer repoId = (repo != null && orgId != null) ? resolveRepoId(orgId, repo) : null;
        if (repo != null && repoId == null) {
            log.debug("Repo not found: {} in org {}", repo, org);
            return Collections.emptyMap();
        }
        Integer branchId = (branch != null && repoId != null) ? resolveBranchId(repoId, branch) : null;
        if (branch != null && branchId == null) {
            log.debug("Branch not found: {} in repo {}", branch, repo);
            return Collections.emptyMap();
        }

        // Execute tag query with scope
        List<TestResultRecord> results = findByTagExpression(expression, companyId, orgId, repoId, branchId);

        // Filter by SHA if specified (sha is a column on run table)
        if (sha != null && !sha.isBlank()) {
            results = filterBySha(results, sha);
        }

        // Group results by hierarchy
        return groupResultsByHierarchy(results);
    }

    private Integer resolveCompanyId(String company) {
        return dsl.select(COMPANY.COMPANY_ID)
            .from(COMPANY)
            .where(COMPANY.COMPANY_NAME.eq(company))
            .fetchOne(COMPANY.COMPANY_ID);
    }

    private Integer resolveOrgId(Integer companyId, String org) {
        if (companyId == null) return null;
        return dsl.select(ORG.ORG_ID)
            .from(ORG)
            .where(ORG.COMPANY_FK.eq(companyId))
            .and(ORG.ORG_NAME.eq(org))
            .fetchOne(ORG.ORG_ID);
    }

    private Integer resolveRepoId(Integer orgId, String repo) {
        if (orgId == null) return null;
        return dsl.select(REPO.REPO_ID)
            .from(REPO)
            .where(REPO.ORG_FK.eq(orgId))
            .and(REPO.REPO_NAME.eq(repo))
            .fetchOne(REPO.REPO_ID);
    }

    private Integer resolveBranchId(Integer repoId, String branch) {
        if (repoId == null) return null;
        return dsl.select(BRANCH.BRANCH_ID)
            .from(BRANCH)
            .where(BRANCH.REPO_FK.eq(repoId))
            .and(BRANCH.BRANCH_NAME.eq(branch))
            .fetchOne(BRANCH.BRANCH_ID);
    }

    private List<TestResultRecord> filterBySha(List<TestResultRecord> results, String sha) {
        if (sha == null || results.isEmpty()) return results;

        // Get test_result_ids for runs with matching sha
        Set<Long> testResultIdsInSha = new HashSet<>(
            dsl.select(TEST_RESULT.TEST_RESULT_ID)
                .from(TEST_RESULT)
                .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                .where(RUN.SHA.eq(sha))
                .fetch(TEST_RESULT.TEST_RESULT_ID)
        );

        List<TestResultRecord> filtered = new ArrayList<>();
        for (TestResultRecord r : results) {
            if (testResultIdsInSha.contains(r.getTestResultId())) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    private Map<String, Map<String, Map<String, io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult>>> groupResultsByHierarchy(
            List<TestResultRecord> results) {

        // Result structure: branch -> sha -> jobInfo -> JobResult
        Map<String, Map<String, Map<String, io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult>>> grouped = new LinkedHashMap<>();

        // For each result, get its hierarchy info and group
        for (TestResultRecord tr : results) {
            // Query hierarchy for this test result
            var hierarchyInfo = dsl.select(
                    BRANCH.BRANCH_NAME,
                    RUN.SHA,
                    JOB.JOB_INFO,
                    RUN.RUN_DATE,
                    TEST_RESULT.TEST_SUITES_JSON
                )
                .from(TEST_RESULT)
                .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                .where(TEST_RESULT.TEST_RESULT_ID.eq(tr.getTestResultId()))
                .fetchOne();

            if (hierarchyInfo == null) continue;

            String branchName = hierarchyInfo.get(BRANCH.BRANCH_NAME);
            String shaValue = hierarchyInfo.get(RUN.SHA);
            String jobInfo = hierarchyInfo.get(JOB.JOB_INFO);
            java.time.Instant runDate = hierarchyInfo.get(RUN.RUN_DATE);
            String testSuitesJson = hierarchyInfo.get(TEST_RESULT.TEST_SUITES_JSON);

            // Use "default" for null jobInfo or sha
            String jobKey = (jobInfo != null && !jobInfo.isBlank()) ? jobInfo : "default";
            String shaKey = (shaValue != null && !shaValue.isBlank()) ? shaValue : "unknown";

            // Extract test names from test_suites_json
            List<String> tests = extractTestNames(testSuitesJson);

            // Group into structure
            grouped
                .computeIfAbsent(branchName, k -> new LinkedHashMap<>())
                .computeIfAbsent(shaKey, k -> new LinkedHashMap<>())
                .merge(jobKey,
                    io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult.builder()
                        .runDate(runDate)
                        .tests(tests)
                        .build(),
                    (existing, newResult) -> {
                        // Merge tests, keep latest runDate
                        List<String> mergedTests = new ArrayList<>(existing.getTests());
                        mergedTests.addAll(newResult.getTests());
                        return io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult.builder()
                            .runDate(existing.getRunDate() != null && newResult.getRunDate() != null &&
                                    existing.getRunDate().isAfter(newResult.getRunDate())
                                    ? existing.getRunDate() : newResult.getRunDate())
                            .tests(mergedTests)
                            .build();
                    });
        }

        return grouped;
    }

    private List<String> extractTestNames(String testSuitesJson) {
        if (testSuitesJson == null || testSuitesJson.isBlank()) return Collections.emptyList();

        List<String> testNames = new ArrayList<>();
        try {
            // Parse JSON to extract test names
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(testSuitesJson);

            // Navigate: testSuites array -> testCases array -> name
            if (root.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode suite : root) {
                    com.fasterxml.jackson.databind.JsonNode testCases = suite.get("testCases");
                    if (testCases != null && testCases.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode testCase : testCases) {
                            com.fasterxml.jackson.databind.JsonNode name = testCase.get("name");
                            if (name != null && !name.isNull()) {
                                testNames.add(name.asText());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract test names from testSuitesJson: {}",
                    testSuitesJson != null ? testSuitesJson.substring(0, Math.min(100, testSuitesJson.length())) : "null",
                    e);
        }
        return testNames;
    }

    /**
     * Get the query builder for advanced usage.
     *
     * @return the TagQueryBuilder instance
     */
    public TagQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }
}
