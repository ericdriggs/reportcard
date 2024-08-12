package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.cache.model.BranchStageViewResponse;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.model.graph.CompanyGraph;
import io.github.ericdriggs.reportcard.model.graph.CompanyGraphBuilder;
import io.github.ericdriggs.reportcard.model.graph.TestSuiteGraph;
import io.github.ericdriggs.reportcard.model.graph.TestSuiteGraphBuilder;
import io.github.ericdriggs.reportcard.model.graph.condition.TableConditionMap;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.util.db.SqlJsonUtil;
import lombok.SneakyThrows;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.jooq.impl.DSL.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions", "DuplicatedCode"})
public class GraphService extends AbstractPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static List<String> defaultBranchNames = List.of("dev", "develop", "qa", "staging", "main", "master", "staging", "test");

    @Autowired
    public GraphService(DSLContext dsl) {
        super(dsl);

    }

    public JobStageTestTrend getJobStageTestTrend(String companyName,
                                                  String orgName,
                                                  String repoName,
                                                  String branchName,
                                                  Long jobId,
                                                  String stageName,
                                                  Instant start,
                                                  Instant end,
                                                  Integer maxRuns) {
        List<CompanyGraph> companyGraphs = getJobTrendCompanyGraphs(companyName, orgName, repoName, branchName, jobId, stageName, start, end, maxRuns);
        return JobStageTestTrend.fromCompanyGraphs(companyGraphs, maxRuns);
    }

    List<CompanyGraph> getJobTrendCompanyGraphs(String companyName,
                                                String orgName,
                                                String repoName,
                                                String branchName,
                                                Long jobId,
                                                String stageName,
                                                Instant start,
                                                Instant end,
                                                Integer maxRuns) {

        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(companyName));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(orgName));
        tableConditionMap.put(REPO, REPO.REPO_NAME.eq(repoName));
        tableConditionMap.put(BRANCH, BRANCH.BRANCH_NAME.eq(branchName));
        tableConditionMap.put(JOB, JOB.JOB_ID.eq(jobId));
        tableConditionMap.put(TEST_RESULT, condition(SqlJsonUtil.fieldNotEqualsJson(TEST_RESULT.TEST_SUITES_JSON.getName(), "[]")));

        Condition runCondition = trueCondition();
        if (start != null) {
            runCondition = runCondition.and(RUN.RUN_DATE.ge(start));
        }
        if (end != null) {
            runCondition = runCondition.and(RUN.RUN_DATE.le(end));
        }
        if (maxRuns != null) {
            runCondition = runCondition.and(
                    RUN.RUN_ID.in(
                            select(RUN.RUN_ID)
                                    .from(RUN)
                                    .where(RUN.JOB_FK.eq(jobId))
                                    .orderBy(RUN.RUN_ID.desc())
                                    .limit(maxRuns)
                    )
            );
        }
        tableConditionMap.put(RUN, runCondition);
        tableConditionMap.put(STAGE, STAGE.STAGE_NAME.eq(stageName));
        return getCompanyGraphs(tableConditionMap);

    }

    public BranchStageViewResponse getRunBranchStageViewResponse(String companyName,
                                                           String orgName,
                                                           String repoName,
                                                           String branchName,
                                                           Long jobId,
                                                           Long runId) {

        List<CompanyGraph> companyGraphs = getRunCompanyGraphs(companyName, orgName, repoName, branchName, jobId, runId);
        return BranchStageViewResponse.fromCompanyGraphs(companyGraphs);
    }

    List<CompanyGraph> getRunCompanyGraphs(String companyName,
                                                String orgName,
                                                String repoName,
                                                String branchName,
                                                Long jobId,
                                                Long runId) {

        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(companyName));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(orgName));
        tableConditionMap.put(REPO, REPO.REPO_NAME.eq(repoName));
        tableConditionMap.put(BRANCH, BRANCH.BRANCH_NAME.eq(branchName));
        tableConditionMap.put(JOB, JOB.JOB_ID.eq(jobId));
        tableConditionMap.put(RUN, RUN.RUN_ID.eq(runId));
        return getCompanyGraphs(tableConditionMap);
    }

    public BranchJobLatestRunMap getBranchJobLatestRunMap(String companyName,
                                                          String orgName,
                                                          String repoName,
                                                          String branchName) {
        return getBranchJobLatestRunMap(companyName, orgName, repoName, branchName, null);
    }

    public BranchJobLatestRunMap getBranchJobLatestRunMap(String companyName,
                                                          String orgName,
                                                          String repoName,
                                                          String branchName,
                                                          Long jobId) {
        List<CompanyGraph> companyGraphs = getLatestRunForBranchJobStages(companyName, orgName, repoName, branchName, jobId);
        return BranchJobLatestRunMap.fromCompanyGraphs(companyGraphs);
    }

    List<CompanyGraph> getLatestRunForBranchJobStages(String companyName,
                                                      String orgName,
                                                      String repoName,
                                                      String branchName,
                                                      Long jobId) {

        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(companyName));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(orgName));
        tableConditionMap.put(REPO, REPO.REPO_NAME.eq(repoName));
        tableConditionMap.put(BRANCH, BRANCH.BRANCH_NAME.eq(branchName));

        if (jobId != null) {
            tableConditionMap.put(JOB, JOB.JOB_ID.eq(jobId));
        }
        //don't want the full test graph for this view
        tableConditionMap.put(TEST_RESULT, TEST_RESULT.TEST_RESULT_ID.isNull());

        Long[] runIds = dsl.select(max(RUN.RUN_ID).as("MAX_RUN_ID"))
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)).and(ORG.ORG_NAME.eq(orgName)).and(COMPANY.COMPANY_NAME.eq(companyName))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID).and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .groupBy(JOB.JOB_ID, STAGE.STAGE_NAME)
                .fetchArray("MAX_RUN_ID", Long.class);

        tableConditionMap.put(RUN, RUN.RUN_ID.in(runIds));
        return getCompanyGraphs(tableConditionMap);

    }

    public OrgDashboard getOrgDashboard(String companyName, String orgName, List<String> repoNames, List<String> branchNames, boolean shouldIncludeDefaultBranches, Integer days) {
        List<CompanyGraph> companyGraphs = getOrgDashboardCompanyGraphs(companyName, orgName, repoNames, branchNames, shouldIncludeDefaultBranches, days);
        return OrgDashboard.fromCompanyGraphs(companyGraphs);
    }

    List<CompanyGraph> getOrgDashboardCompanyGraphs(String companyName,
                                                    String orgName,
                                                    List<String> repoNames,
                                                    List<String> branchNames,
                                                    boolean shouldIncludeDefaultBranches,
                                                    Integer days) {

        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(companyName));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(orgName));
        //Don't show runs without test data. Maybe should filter on test_suites_json but this for now
        tableConditionMap.put(TEST_RESULT, TEST_RESULT.TEST_RESULT_ID.isNotNull());

        Condition repoCondition = trueCondition();
        if (!CollectionUtils.isEmpty(repoNames)) {
            repoCondition = REPO.REPO_NAME.likeRegex(String.join("|", repoNames));
            tableConditionMap.put(REPO, repoCondition);
        }

        if (shouldIncludeDefaultBranches) {
            branchNames.addAll(defaultBranchNames);
        }
        tableConditionMap.put(BRANCH, BRANCH.BRANCH_NAME.in(branchNames));

        if (days != null) {
            Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);
            tableConditionMap.put(JOB, JOB.LAST_RUN.ge(cutoff));
        }

        Long[] runIds = dsl.select(max(RUN.RUN_ID).as("MAX_RUN_ID"))
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)).and(ORG.ORG_NAME.eq(orgName)).and(COMPANY.COMPANY_NAME.eq(companyName))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)).and(repoCondition)
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(BRANCH.BRANCH_NAME.in(branchNames)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))

                .groupBy(JOB.JOB_ID, STAGE.STAGE_NAME)
                .union(
                        //latest successful run
                        select(max(RUN.RUN_ID))
                                .from(COMPANY)
                                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)).and(ORG.ORG_NAME.eq(orgName)).and(COMPANY.COMPANY_NAME.eq(companyName))
                                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)).and(repoCondition)
                                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(BRANCH.BRANCH_NAME.in(branchNames)))
                                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID).and(RUN.IS_SUCCESS))
                                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                                .groupBy(JOB.JOB_ID, STAGE.STAGE_NAME)
                ).fetchArray("MAX_RUN_ID", Long.class);

        tableConditionMap.put(RUN, RUN.RUN_ID.in(runIds));
        return getCompanyGraphs(tableConditionMap);

    }

    @SneakyThrows(JsonProcessingException.class)
    @SuppressWarnings("rawtypes")
    protected List<CompanyGraph> getCompanyGraphs(TableConditionMap tableConditionMap) {
        Result result = getFullTestGraph(tableConditionMap);
        if (!result.isEmpty() && result.get(0) instanceof Record1 record1) {
            String json = record1.formatJSON();
            log.info("getCompanyGraph json: " + json);
            return Arrays.asList(mapper.readValue(json, CompanyGraph[].class));
        }
        return List.of(CompanyGraphBuilder.builder().build());
    }

    @SuppressWarnings("rawtypes")
    public static Field<String> isoDateFormat(Field field) {
        return dateFormat(field, "%Y-%m-%dT%T.000Z");
    }

    @SuppressWarnings("rawtypes")
    public static Field<String> dateFormat(Field field, String format) {
        return DSL.field("date_format({0}, {1})", SQLDataType.VARCHAR,
                field, DSL.inline(format));
    }

    @SuppressWarnings("rawtypes")
    protected Result getFullTestGraph(TableConditionMap tableConditionMap) {

        return dsl.select(jsonObject(
                        key("companyId").value(COMPANY.COMPANY_ID),
                        key("companyName").value(COMPANY.COMPANY_NAME),
                        key("orgs").value(dsl.select(jsonArrayAgg(jsonObject(
                                key("orgId").value(ORG.ORG_ID),
                                key("orgName").value(ORG.ORG_NAME),
                                key("companyFk").value(ORG.COMPANY_FK),
                                key("repos").value(dsl.select(jsonArrayAgg(jsonObject(
                                        key("repoId").value(REPO.REPO_ID),
                                        key("repoName").value(REPO.REPO_NAME),
                                        key("orgFk").value(REPO.ORG_FK),
                                        key("branches").value(dsl.select(jsonArrayAgg(jsonObject(
                                                key("branchId").value(BRANCH.BRANCH_ID),
                                                key("branchName").value(BRANCH.BRANCH_NAME),
                                                key("repoFk").value(BRANCH.REPO_FK),
                                                key("lastRun").value(isoDateFormat(BRANCH.LAST_RUN)),
                                                key("jobs").value(dsl.select(jsonArrayAgg(jsonObject(
                                                        key("jobId").value(JOB.JOB_ID),
                                                        key("jobInfo").value(JOB.JOB_INFO),
                                                        key("jobInfoStr").value(JOB.JOB_INFO_STR),
                                                        key("branchFk").value(JOB.BRANCH_FK),
                                                        key("lastRun").value(isoDateFormat(JOB.LAST_RUN)),
                                                        key("runs").value(dsl.select(jsonArrayAgg(jsonObject(
                                                                key("runId").value(RUN.RUN_ID),
                                                                key("runReference").value(RUN.RUN_REFERENCE),
                                                                key("jobFk").value(RUN.JOB_FK),
                                                                key("jobRunCount").value(RUN.JOB_RUN_COUNT),
                                                                key("sha").value(RUN.SHA),
                                                                key("runDate").value(isoDateFormat(RUN.RUN_DATE)),
                                                                key("isSuccess").value(RUN.IS_SUCCESS),
                                                                key("stages").value(dsl.select(jsonArrayAgg(jsonObject(
                                                                        key("stageId").value(STAGE.STAGE_ID),
                                                                        key("stageName").value(STAGE.STAGE_NAME),
                                                                        key("runFk").value(STAGE.RUN_FK),
                                                                        key("storages").value(dsl.select(jsonArrayAgg(jsonObject(
                                                                                key("storageId").value(STORAGE.STORAGE_ID),
                                                                                key("stageFk").value(STORAGE.STAGE_FK),
                                                                                key("label").value(STORAGE.LABEL),
                                                                                key("prefix").value(STORAGE.PREFIX),
                                                                                key("indexFile").value(STORAGE.INDEX_FILE),
                                                                                key("storageType").value(STORAGE.STORAGE_TYPE)
                                                                        ))).from(STORAGE).where(STORAGE.STAGE_FK.eq(STAGE.STAGE_ID).and(tableConditionMap.getCondition(STORAGE)))),
                                                                        key("testResults").value(dsl.select(jsonArrayAgg(jsonObject(
                                                                                key("testResultId").value(TEST_RESULT.TEST_RESULT_ID),
                                                                                key("stageFk").value(TEST_RESULT.STAGE_FK),
                                                                                key("tests").value(TEST_RESULT.TESTS),
                                                                                key("skipped").value(TEST_RESULT.SKIPPED),
                                                                                key("error").value(TEST_RESULT.ERROR),
                                                                                key("failure").value(TEST_RESULT.FAILURE),
                                                                                key("time").value(TEST_RESULT.TIME),
                                                                                key("testResultCreated").value(isoDateFormat(TEST_RESULT.TEST_RESULT_CREATED)),
                                                                                key("externalLinks").value(TEST_RESULT.EXTERNAL_LINKS),
                                                                                key("isSuccess").value(TEST_RESULT.IS_SUCCESS),
                                                                                key("hasSkip").value(TEST_RESULT.HAS_SKIP),
                                                                                key("testSuitesJson").value(TEST_RESULT.TEST_SUITES_JSON)
                                                                        ))).from(TEST_RESULT).where(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID).and(tableConditionMap.getCondition(TEST_RESULT))))
                                                                ))).from(STAGE).where(STAGE.RUN_FK.eq(RUN.RUN_ID).and(tableConditionMap.getCondition(STAGE))))
                                                        ))).from(RUN).where(RUN.JOB_FK.eq(JOB.JOB_ID).and(tableConditionMap.getCondition(RUN))))
                                                ))).from(JOB).where(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID).and(tableConditionMap.getCondition(JOB))))
                                        ))).from(BRANCH).where(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(tableConditionMap.getCondition(BRANCH))))
                                ))).from(REPO).where(REPO.ORG_FK.eq(ORG.ORG_ID).and(tableConditionMap.getCondition(REPO))))
                        ))).from(ORG).where(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID).and(tableConditionMap.getCondition(ORG))))
                )).from(COMPANY).where(tableConditionMap.getCondition(COMPANY))
                .fetch();
    }

    public TreeSet<Long> getTestResultsWithoutSuiteJson(int maxCount) {
        Long[] ids = dsl.selectDistinct(TEST_RESULT.TEST_RESULT_ID)
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_SUITES_JSON.isNull())
                .orderBy(TEST_RESULT.TEST_RESULT_ID.asc())
                .limit(maxCount)
                .fetchArray(TEST_RESULT.TEST_RESULT_ID, Long.class);
        return new TreeSet<Long>(Arrays.stream(ids).toList());
    }

    private void updateTestResultJson(Long testResultId, String testSuiteJson) {
        log.info("updating test result json for test result: {}, testSuiteJson: {}", testResultId, testSuiteJson);
        int result = dsl.update(TEST_RESULT)
                .set(TEST_RESULT.TEST_SUITES_JSON, testSuiteJson)
                .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId).and(TEST_RESULT.TEST_SUITES_JSON.isNull()))
                .execute();
        if (result == 1) {
            log.info("success updating testResultId: {}, testResultJson: {}", testResultId, testSuiteJson);
        } else {
            log.error("failed to update TEST_RESULT. update returned: {} for testResultId: {}", result, testResultId);
        }
    }

    @SneakyThrows(JsonProcessingException.class)
    @SuppressWarnings("rawtypes")
    public List<TestSuiteGraph> getTestSuitesGraph(Long testResultId) {
        Result result = getTestSuitesGraphResult(testResultId);
        if (!result.isEmpty() && result.get(0) instanceof Record1 record1) {
            String json = record1.formatJSON();
            log.info("getTestSuitesGraph json: " + json);
            return Arrays.asList(mapper.readValue(json, TestSuiteGraph[].class));
        }
        return List.of(TestSuiteGraphBuilder.builder().build());
    }

    @SuppressWarnings("rawtypes")
    public String getTestSuitesGraphJson(Long testResultId) {
        Result result = getTestSuitesGraphResult(testResultId);
        if (!result.isEmpty() && result.get(0) instanceof Record1 record1) {
            String json = record1.formatJSON();
            log.info("getTestSuitesGraphJson json: " + json);
            return json;
        }
        return "[]";
    }

    public void populateTestSuitesJson(Long testResultId) {
        final String testResultJson = getTestSuitesGraphJson(testResultId);
        updateTestResultJson(testResultId, testResultJson);
    }

    @SuppressWarnings("rawtypes")
    protected Result getTestSuitesGraphResult(Long testResultId) {

        return dsl.select(jsonObject(
                        key("testSuiteId").value(TEST_SUITE.TEST_SUITE_ID),
                        key("testResultFk").value(TEST_SUITE.TEST_RESULT_FK),
                        key("name").value(TEST_SUITE.NAME),
                        key("tests").value(TEST_SUITE.TESTS),
                        key("skipped").value(TEST_SUITE.SKIPPED),
                        key("error").value(TEST_SUITE.ERROR),
                        key("failure").value(TEST_SUITE.FAILURE),
                        key("time").value(TEST_SUITE.TIME),
                        key("packageName").value(TEST_SUITE.PACKAGE_NAME),
                        key("group").value(TEST_SUITE.GROUP),
                        key("properties").value(TEST_SUITE.PROPERTIES),
                        key("isSuccess").value(TEST_SUITE.IS_SUCCESS),
                        key("hasSkip").value(TEST_SUITE.HAS_SKIP),
                        key("testCases").value(dsl.select(jsonArrayAgg(jsonObject(
                                key("testCaseId").value(TEST_CASE.TEST_CASE_ID),
                                key("testSuiteFk").value(TEST_CASE.TEST_SUITE_FK),
                                key("testStatusFk").value(TEST_CASE.TEST_STATUS_FK),
                                key("name").value(TEST_CASE.NAME),
                                key("className").value(TEST_CASE.CLASS_NAME),
                                key("time").value(TEST_CASE.TIME),
                                key("systemErr").value(TEST_CASE.SYSTEM_ERR),
                                key("systemOut").value(TEST_CASE.SYSTEM_OUT),
                                key("assertions").value(TEST_CASE.ASSERTIONS),
                                key("testCaseFaults").value(dsl.select(jsonArrayAgg(jsonObject(
                                        key("testCaseFaultId").value(TEST_CASE_FAULT.TEST_CASE_FAULT_ID),
                                        key("testCaseFk").value(TEST_CASE_FAULT.TEST_CASE_FK),
                                        key("faultContextFk").value(TEST_CASE_FAULT.FAULT_CONTEXT_FK),
                                        key("type").value(TEST_CASE_FAULT.TYPE),
                                        key("message").value(TEST_CASE_FAULT.MESSAGE)
                                ))).from(TEST_CASE_FAULT).where(TEST_CASE_FAULT.TEST_CASE_FK.eq(TEST_CASE.TEST_CASE_ID)))
                        ))).from(TEST_CASE).where(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID)))
                )).from(TEST_SUITE).where(TEST_SUITE.TEST_RESULT_FK.eq(testResultId))
                .fetch();
    }

}