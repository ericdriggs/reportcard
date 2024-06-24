package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.model.graph.CompanyGraph;
import io.github.ericdriggs.reportcard.model.graph.CompanyGraphBuilder;
import io.github.ericdriggs.reportcard.model.graph.condition.TableConditionMap;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import lombok.SneakyThrows;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.jooq.impl.DSL.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class GraphService extends AbstractPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        if (start == null) {
            start = Instant.now().atZone(ZoneOffset.UTC).minusDays(30).toInstant();
        }
        List<CompanyGraph> companyGraphs = getJobTrendCompanyGraphs(companyName, orgName, repoName, branchName, jobId, stageName, start, end, maxRuns);
        return JobStageTestTrend.fromCompanyGraphs(companyGraphs, 30);
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

        Condition runCondition = RUN.JOB_FK.eq(JOB.JOB_ID);
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
        return getJobTrendCompanyGraphs(tableConditionMap);

    }

    public BranchJobLatestRunMap getBranchJobLatestRunMap(String companyName,
                                                          String orgName,
                                                          String repoName,
                                                          String branchName) {
        List<CompanyGraph> companyGraphs = getLatestRunForBranchJobStages(companyName, orgName, repoName, branchName);
        return BranchJobLatestRunMap.fromCompanyGraphs(companyGraphs);
    }

    List<CompanyGraph> getLatestRunForBranchJobStages(String companyName,
                                                      String orgName,
                                                      String repoName,
                                                      String branchName) {

        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(companyName));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(orgName));
        tableConditionMap.put(REPO, REPO.REPO_NAME.eq(repoName));
        tableConditionMap.put(BRANCH, BRANCH.BRANCH_NAME.eq(branchName));
        //don't want the full test graph for this view
        tableConditionMap.put(TEST_RESULT, TEST_RESULT.TEST_RESULT_ID.isNull());

        Condition runCondition =
                RUN.RUN_ID.in(
                        select(max(RUN.RUN_ID))
                                .from(COMPANY)
                                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)).and(ORG.ORG_NAME.eq(orgName)).and(COMPANY.COMPANY_NAME.eq(companyName))
                                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID).and(REPO.REPO_NAME.eq(repoName)))
                                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(BRANCH.BRANCH_NAME.eq(branchName)))
                                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                                .groupBy(JOB.JOB_ID, STAGE.STAGE_NAME)
                );

        tableConditionMap.put(RUN, runCondition);
        return getJobTrendCompanyGraphs(tableConditionMap);

    }

    @SneakyThrows(JsonProcessingException.class)
    @SuppressWarnings("rawtypes")
    protected List<CompanyGraph> getJobTrendCompanyGraphs(TableConditionMap tableConditionMap) {
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
                                                                                  key("testSuites").value(dsl.select(jsonArrayAgg(jsonObject(
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
                                                                                                  ))).from(TEST_CASE_FAULT).where(TEST_CASE_FAULT.TEST_CASE_FK.eq(TEST_CASE.TEST_CASE_ID).and(tableConditionMap.getCondition(TEST_CASE_FAULT))))
                                                                                          ))).from(TEST_CASE).where(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID).and(tableConditionMap.getCondition(TEST_CASE))))
                                                                                  ))).from(TEST_SUITE).where(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID).and(tableConditionMap.getCondition(TEST_SUITE))))
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

}