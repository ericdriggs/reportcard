package io.github.ericdriggs.reportcard.persist.tags;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import io.github.ericdriggs.reportcard.model.TagQueryResponse.*;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.trueCondition;

/**
 * Service for tag-based test result queries.
 */
@Service
@Slf4j
public class TagQueryService {

    private final DSLContext dsl;
    private final TagQueryBuilder queryBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TagQueryService(DSLContext dsl) {
        this.dsl = dsl;
        this.queryBuilder = new TagQueryBuilder(dsl);
    }

    /**
     * Find test results matching a tag expression.
     */
    public List<TestResultRecord> findByTagExpression(String expression) {
        TagExpr expr = TagExpressionParser.parse(expression);
        Select<TestResultRecord> query = queryBuilder.buildQuery(expr);
        Result<TestResultRecord> results = query.fetch();
        return new ArrayList<>(results);
    }

    /**
     * Find test results matching a tag expression within a scope.
     */
    public List<TestResultRecord> findByTagExpression(
            String expression,
            Integer companyId,
            Integer orgId,
            Integer repoId,
            Integer branchId) {

        TagExpr expr = TagExpressionParser.parse(expression);
        Select<TestResultRecord> baseQuery = queryBuilder.buildQuery(expr);

        if (companyId == null && orgId == null && repoId == null && branchId == null) {
            return new ArrayList<>(baseQuery.fetch());
        }

        var scopedQuery = dsl.selectFrom(TEST_RESULT)
            .where(TEST_RESULT.TEST_RESULT_ID.in(
                dsl.select(TEST_RESULT.TEST_RESULT_ID)
                    .from(baseQuery.asTable("tag_filtered"))
            ));

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
        } else {
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
     * Find test results by path and return properly structured hierarchy.
     * Uses the multi-value JSON index on test_result.tags for efficient filtering.
     *
     * @param expression Tag expression
     * @param company Company name (required)
     * @param org Org name (nullable - determines scope)
     * @param repo Repo name (nullable)
     * @param branch Branch name (nullable)
     * @param sha SHA value (nullable)
     * @return TagQueryResponse with properly nested hierarchy
     */
    public TagQueryResponse findByTagExpressionByPath(
            String expression,
            String company,
            String org,
            String repo,
            String branch,
            String sha) {

        // Parse tag expression
        TagExpr tagExpr = TagExpressionParser.parse(expression);

        // For OR expressions, use subquery with UNION to maintain index usage
        // For AND/simple expressions, use condition directly
        Condition tagCondition;
        if (tagExpr instanceof OrExpr) {
            // OR requires UNION for index usage - use subquery
            Select<TestResultRecord> tagQuery = queryBuilder.buildQuery(tagExpr);
            tagCondition = TEST_RESULT.TEST_RESULT_ID.in(
                dsl.select(TEST_RESULT.TEST_RESULT_ID).from(tagQuery.asTable("tag_matches"))
            );
        } else {
            // AND/simple can use condition directly with index
            tagCondition = queryBuilder.buildCondition(tagExpr);
        }

        // Build scope conditions
        Condition scopeCondition = COMPANY.COMPANY_NAME.eq(company);
        if (org != null) {
            scopeCondition = scopeCondition.and(ORG.ORG_NAME.eq(org));
        }
        if (repo != null) {
            scopeCondition = scopeCondition.and(REPO.REPO_NAME.eq(repo));
        }
        if (branch != null) {
            scopeCondition = scopeCondition.and(BRANCH.BRANCH_NAME.eq(branch));
        }
        if (sha != null) {
            scopeCondition = scopeCondition.and(RUN.SHA.eq(sha));
        }

        // Subquery to get latest run per job that has test data (scoped to same filters)
        var latestRunPerJob = dsl.select(RUN.JOB_FK, max(RUN.RUN_DATE).as("max_run_date"))
            .from(RUN)
            .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
            .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
            .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
            .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
            .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
            .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
            .join(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
            .where(TEST_RESULT.TEST_SUITES_JSON.isNotNull())
            .and(scopeCondition)
            .groupBy(RUN.JOB_FK)
            .asTable("latest_runs");

        // Single query: join hierarchy, filter to latest run with data, then filter by tags
        var fullData = dsl.select(
                ORG.ORG_ID, ORG.ORG_NAME,
                REPO.REPO_ID, REPO.REPO_NAME,
                BRANCH.BRANCH_ID, BRANCH.BRANCH_NAME,
                JOB.JOB_ID, JOB.JOB_INFO,
                RUN.RUN_ID, RUN.SHA, RUN.RUN_DATE,
                STAGE.STAGE_ID, STAGE.STAGE_NAME,
                TEST_RESULT.TEST_RESULT_ID, TEST_RESULT.TEST_SUITES_JSON
            )
            .from(TEST_RESULT)
            .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
            .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
            .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
            .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
            .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
            .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
            .join(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
            .join(latestRunPerJob).on(
                JOB.JOB_ID.eq(latestRunPerJob.field(RUN.JOB_FK))
                .and(RUN.RUN_DATE.eq(latestRunPerJob.field("max_run_date", RUN.RUN_DATE.getDataType())))
            )
            .where(tagCondition)
            .and(scopeCondition)
            .orderBy(ORG.ORG_NAME, REPO.REPO_NAME, BRANCH.BRANCH_NAME, JOB.JOB_ID, RUN.RUN_DATE.desc(), STAGE.STAGE_NAME)
            .fetch();

        if (fullData.isEmpty()) {
            return buildEmptyResponse(company, org, repo, branch, sha, expression);
        }

        // Build hierarchy based on scope
        return buildHierarchyResponse(fullData, company, org, repo, branch, sha, expression);
    }

    private TagQueryResponse buildEmptyResponse(String company, String org, String repo, String branch, String sha, String expression) {
        String scope = buildScopeString(company, org, repo, branch, sha);
        return TagQueryResponse.builder()
            .query(QueryInfo.builder().scope(scope).tags(expression).build())
            .build();
    }

    private String buildScopeString(String company, String org, String repo, String branch, String sha) {
        StringBuilder sb = new StringBuilder("/company/").append(company);
        if (org != null) sb.append("/org/").append(org);
        if (repo != null) sb.append("/repo/").append(repo);
        if (branch != null) sb.append("/branch/").append(branch);
        if (sha != null) sb.append("/sha/").append(sha);
        return sb.toString();
    }

    private TagQueryResponse buildHierarchyResponse(
            Result<? extends Record> data,
            String company, String org, String repo, String branch, String sha,
            String expression) {

        String scope = buildScopeString(company, org, repo, branch, sha);
        TagQueryResponse.TagQueryResponseBuilder responseBuilder = TagQueryResponse.builder()
            .query(QueryInfo.builder().scope(scope).tags(expression).build());

        // Parse tag expression for filtering tests
        TagExpr tagExpr = TagExpressionParser.parse(expression);

        // Group data into hierarchy maps
        Map<Integer, OrgData> orgMap = new LinkedHashMap<>();

        for (Record row : data) {
            Integer orgIdVal = row.get(ORG.ORG_ID);
            String orgNameVal = row.get(ORG.ORG_NAME);
            Integer repoIdVal = row.get(REPO.REPO_ID);
            String repoNameVal = row.get(REPO.REPO_NAME);
            Integer branchIdVal = row.get(BRANCH.BRANCH_ID);
            String branchNameVal = row.get(BRANCH.BRANCH_NAME);
            Long jobIdVal = row.get(JOB.JOB_ID);
            String jobInfoStr = row.get(JOB.JOB_INFO);
            Long runIdVal = row.get(RUN.RUN_ID);
            String shaVal = row.get(RUN.SHA);
            Instant runDateVal = row.get(RUN.RUN_DATE);
            Long stageIdVal = row.get(STAGE.STAGE_ID);
            String stageNameVal = row.get(STAGE.STAGE_NAME);
            String testSuitesJson = row.get(TEST_RESULT.TEST_SUITES_JSON);

            // Build nested hierarchy
            OrgData orgData = orgMap.computeIfAbsent(orgIdVal,
                k -> new OrgData(orgIdVal, orgNameVal));
            RepoData repoData = orgData.repos.computeIfAbsent(repoIdVal,
                k -> new RepoData(repoIdVal, repoNameVal));
            BranchData branchData = repoData.branches.computeIfAbsent(branchIdVal,
                k -> new BranchData(branchIdVal, branchNameVal));
            JobData jobData = branchData.jobs.computeIfAbsent(jobIdVal,
                k -> new JobData(jobIdVal, parseJobInfo(jobInfoStr)));
            RunData runData = jobData.runs.computeIfAbsent(runIdVal,
                k -> new RunData(runIdVal, shaVal, runDateVal));
            StageData stageData = runData.stages.computeIfAbsent(stageIdVal,
                k -> new StageData(stageIdVal, stageNameVal));

            // Add only tests that match the tag expression
            stageData.tests.addAll(extractMatchingTestInfos(testSuitesJson, tagExpr));
        }

        // Convert to response objects based on scope
        if (org == null) {
            // Company scope - return orgs
            responseBuilder.orgs(buildOrgResults(orgMap));
        } else if (repo == null) {
            // Org scope - return repos
            OrgData orgData = orgMap.values().iterator().next();
            responseBuilder.repos(buildRepoResults(orgData.repos));
        } else if (branch == null) {
            // Repo scope - return branches
            OrgData orgData = orgMap.values().iterator().next();
            RepoData repoData = orgData.repos.values().iterator().next();
            responseBuilder.branches(buildBranchResults(repoData.branches));
        } else {
            // Branch or SHA scope - return jobs
            OrgData orgData = orgMap.values().iterator().next();
            RepoData repoData = orgData.repos.values().iterator().next();
            BranchData branchData = repoData.branches.values().iterator().next();
            responseBuilder.jobs(buildJobResults(branchData.jobs));
        }

        return responseBuilder.build();
    }

    // Internal data classes for building hierarchy
    private static class OrgData {
        final Integer orgId;
        final String orgName;
        final Map<Integer, RepoData> repos = new LinkedHashMap<>();
        OrgData(Integer orgId, String orgName) { this.orgId = orgId; this.orgName = orgName; }
    }

    private static class RepoData {
        final Integer repoId;
        final String repoName;
        final Map<Integer, BranchData> branches = new LinkedHashMap<>();
        RepoData(Integer repoId, String repoName) { this.repoId = repoId; this.repoName = repoName; }
    }

    private static class BranchData {
        final Integer branchId;
        final String branchName;
        final Map<Long, JobData> jobs = new LinkedHashMap<>();
        BranchData(Integer branchId, String branchName) { this.branchId = branchId; this.branchName = branchName; }
    }

    private static class JobData {
        final Long jobId;
        final TreeMap<String, String> jobInfo;
        final Map<Long, RunData> runs = new LinkedHashMap<>();
        JobData(Long jobId, TreeMap<String, String> jobInfo) { this.jobId = jobId; this.jobInfo = jobInfo; }
    }

    private static class RunData {
        final Long runId;
        final String sha;
        final Instant runDate;
        final Map<Long, StageData> stages = new LinkedHashMap<>();
        RunData(Long runId, String sha, Instant runDate) { this.runId = runId; this.sha = sha; this.runDate = runDate; }
    }

    private static class StageData {
        final Long stageId;
        final String stageName;
        final List<TestInfo> tests = new ArrayList<>();
        StageData(Long stageId, String stageName) { this.stageId = stageId; this.stageName = stageName; }
    }

    // Conversion methods - filter out empty containers at each level
    private List<OrgResult> buildOrgResults(Map<Integer, OrgData> orgMap) {
        return orgMap.values().stream()
            .map(o -> {
                List<RepoResult> repos = buildRepoResults(o.repos);
                if (repos.isEmpty()) return null;
                return OrgResult.builder()
                    .orgId(o.orgId)
                    .orgName(o.orgName)
                    .repos(repos)
                    .build();
            })
            .filter(o -> o != null)
            .toList();
    }

    private List<RepoResult> buildRepoResults(Map<Integer, RepoData> repoMap) {
        return repoMap.values().stream()
            .map(r -> {
                List<BranchResult> branches = buildBranchResults(r.branches);
                if (branches.isEmpty()) return null;
                return RepoResult.builder()
                    .repoId(r.repoId)
                    .repoName(r.repoName)
                    .branches(branches)
                    .build();
            })
            .filter(r -> r != null)
            .toList();
    }

    private List<BranchResult> buildBranchResults(Map<Integer, BranchData> branchMap) {
        return branchMap.values().stream()
            .map(b -> {
                List<JobResult> jobs = buildJobResults(b.jobs);
                if (jobs.isEmpty()) return null;
                return BranchResult.builder()
                    .branchId(b.branchId)
                    .branchName(b.branchName)
                    .jobs(jobs)
                    .build();
            })
            .filter(b -> b != null)
            .toList();
    }

    private List<JobResult> buildJobResults(Map<Long, JobData> jobMap) {
        return jobMap.values().stream()
            .map(j -> {
                List<RunResult> runs = buildRunResults(j.runs);
                if (runs.isEmpty()) return null;
                return JobResult.builder()
                    .jobId(j.jobId)
                    .jobInfo(j.jobInfo)
                    .runs(runs)
                    .build();
            })
            .filter(j -> j != null)
            .toList();
    }

    private List<RunResult> buildRunResults(Map<Long, RunData> runMap) {
        return runMap.values().stream()
            .map(r -> {
                List<StageResult> stages = buildStageResults(r.stages);
                if (stages.isEmpty()) return null;
                return RunResult.builder()
                    .runId(r.runId)
                    .sha(r.sha)
                    .runDate(r.runDate)
                    .stages(stages)
                    .build();
            })
            .filter(r -> r != null)
            .toList();
    }

    private List<StageResult> buildStageResults(Map<Long, StageData> stageMap) {
        return stageMap.values().stream()
            .filter(s -> !s.tests.isEmpty())  // Only include stages with matching tests
            .map(s -> StageResult.builder()
                .stageId(s.stageId)
                .stageName(s.stageName)
                .tests(s.tests)
                .build())
            .toList();
    }

    private TreeMap<String, String> parseJobInfo(String jobInfoStr) {
        if (jobInfoStr == null || jobInfoStr.isBlank()) {
            return new TreeMap<>();
        }
        try {
            @SuppressWarnings("unchecked")
            TreeMap<String, String> result = objectMapper.readValue(jobInfoStr, TreeMap.class);
            return result;
        } catch (Exception e) {
            log.warn("Failed to parse jobInfo: {}", jobInfoStr);
            return new TreeMap<>();
        }
    }

    /**
     * Extract test infos that match the tag expression.
     * Tags from suite and test case are combined before evaluation,
     * so "suiteTag AND testTag" matches when suite has "suiteTag" and test has "testTag".
     *
     * If no tags exist in the JSON (tags only at test_result level), returns ALL tests
     * since the test_result was already matched by the database query.
     */
    private List<TestInfo> extractMatchingTestInfos(String testSuitesJson, TagExpr tagExpr) {
        if (testSuitesJson == null || testSuitesJson.isBlank()) {
            return Collections.emptyList();
        }

        List<TestInfo> testInfos = new ArrayList<>();
        boolean jsonHasAnyTags = false;

        try {
            JsonNode root = objectMapper.readTree(testSuitesJson);
            if (root.isArray()) {
                // First pass: check if JSON has any tags and collect all test infos
                List<TestInfoWithTags> allTests = new ArrayList<>();

                for (JsonNode suite : root) {
                    String className = suite.has("name") ? suite.get("name").asText() : null;
                    List<String> suiteTags = extractTags(suite);
                    if (!suiteTags.isEmpty()) {
                        jsonHasAnyTags = true;
                    }

                    JsonNode testCases = suite.get("testCases");
                    if (testCases != null && testCases.isArray()) {
                        for (JsonNode testCase : testCases) {
                            String testName = testCase.has("name") ? testCase.get("name").asText() : null;
                            String status = testCase.has("testStatus") ? testCase.get("testStatus").asText() : null;
                            List<String> testTags = extractTags(testCase);
                            if (!testTags.isEmpty()) {
                                jsonHasAnyTags = true;
                            }

                            if (testName != null) {
                                Set<String> combinedTags = new HashSet<>(suiteTags);
                                combinedTags.addAll(testTags);
                                allTests.add(new TestInfoWithTags(testName, className, status, combinedTags));
                            }
                        }
                    }
                }

                // If JSON has no tags, return all tests (tag match was at test_result level)
                // If JSON has tags, filter by expression
                for (TestInfoWithTags test : allTests) {
                    boolean include = !jsonHasAnyTags || TagExprEvaluator.matches(tagExpr, test.combinedTags);
                    if (include) {
                        testInfos.add(TestInfo.builder()
                            .testName(test.testName)
                            .className(test.className)
                            .status(test.status)
                            .build());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract test infos from testSuitesJson", e);
        }
        return testInfos;
    }

    private record TestInfoWithTags(String testName, String className, String status, Set<String> combinedTags) {}

    private List<String> extractTags(JsonNode node) {
        List<String> tags = new ArrayList<>();
        JsonNode tagsNode = node.get("tags");
        if (tagsNode != null && tagsNode.isArray()) {
            for (JsonNode tag : tagsNode) {
                if (tag.isTextual()) {
                    tags.add(tag.asText());
                }
            }
        }
        return tags;
    }

    // ID resolution methods
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

    public TagQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }
}
