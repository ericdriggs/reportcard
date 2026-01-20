package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.cache.model.BranchStageViewResponse;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalRequest;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCount;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsRequest;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardRequest;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;

import io.github.ericdriggs.reportcard.model.graph.CompanyGraph;
import io.github.ericdriggs.reportcard.model.graph.TestSuiteGraph;
import io.github.ericdriggs.reportcard.model.graph.TestSuiteGraphBuilder;
import io.github.ericdriggs.reportcard.model.graph.condition.TableConditionMap;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.util.db.SqlJsonUtil;
import lombok.SneakyThrows;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

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
        tableConditionMap.put(TEST_RESULT, SqlJsonUtil.jsonNotEqualsCondition(TEST_RESULT.TEST_SUITES_JSON, "[]"));

        Condition runCondition = trueCondition();
        if (start != null) {
            runCondition = runCondition.and(RUN.RUN_DATE.ge(start));
        }
        if (end != null) {
            runCondition = runCondition.and(RUN.RUN_DATE.le(end));
        }
        Condition stageCondition = trueCondition();

        if (maxRuns != null) {
            Long[] runIds =
                    dsl.select().from(
                                    dsl.selectDistinct(RUN.RUN_ID.as("RUN_IDS"))
                                            .from(COMPANY)
                                            .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)).and(COMPANY.COMPANY_NAME.eq(companyName).and(ORG.ORG_NAME.eq(orgName)))
                                            .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID).and(REPO.REPO_NAME.eq(repoName)))
                                            .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(BRANCH.BRANCH_NAME.eq(branchName)))
                                            .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID).and(JOB.JOB_ID.eq(jobId)))
                                            .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)).and(runCondition)
                                            .innerJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID).and(STAGE.STAGE_NAME.eq(stageName)))
                                            .innerJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID)).and(SqlJsonUtil.jsonNotEqualsCondition(TEST_RESULT.TEST_SUITES_JSON, "[]"))
                                            .orderBy(RUN.RUN_ID.desc())
                            )
                            .limit(maxRuns)
                            .fetchArray("RUN_IDS", Long.class);

            runCondition = runCondition.and(
                    RUN.RUN_ID.in(runIds)
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
                                                                 Map<String,String> jobInfo,
                                                                 Integer runCount) {

        List<CompanyGraph> companyGraphs = getRunCompanyGraphs(companyName, orgName, repoName, branchName, jobInfo, runCount);
        return BranchStageViewResponse.fromCompanyGraphs(companyGraphs);
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
                                           Map<String,String> jobInfo,
                                           Integer runCount) {

        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(companyName));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(orgName));
        tableConditionMap.put(REPO, REPO.REPO_NAME.eq(repoName));
        tableConditionMap.put(BRANCH, BRANCH.BRANCH_NAME.eq(branchName));
        tableConditionMap.put(JOB, trueCondition().and(SqlJsonUtil.jobInfoEqualsJson(jobInfo)));
        tableConditionMap.put(RUN, RUN.JOB_RUN_COUNT.eq(runCount));
        return getCompanyGraphs(tableConditionMap);
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

    public TreeSet<MetricsIntervalResultCount> getCompanyDashboardIntervalResultCount(MetricsIntervalRequest metricsIntervalRequest) {
        TreeSet<MetricsRequest> metricsRequests = metricsIntervalRequest.toCompanyDashboardRequests();
        Set<MetricsIntervalResultCount> results = new ConcurrentSkipListSet<>();

        //for (MetricsRequest metricsRequest : metricsRequests) {
        metricsRequests.parallelStream().forEach(metricsRequest -> {
            results.add(getCompanyDashboardIntervalResultCount(metricsRequest));
        });
        return new TreeSet<>(results);
    }

    MetricsIntervalResultCount getCompanyDashboardIntervalResultCount(MetricsRequest req) {
        List<CompanyGraph> companyGraphs = getCompanyDashboardCompanyGraphs(req);
        return MetricsIntervalResultCount.fromCompanyGraphs(companyGraphs, req.getExcluded().getJobInfos(), req.getRequired().getJobInfos(), req.getRange());
    }

    List<CompanyGraph> getCompanyDashboardCompanyGraphs(MetricsRequest req) {

        TableConditionMap tableConditionMap = new TableConditionMap();

        //company
        if (!req.getRequired().getCompanies().isEmpty() || !req.getExcluded().getCompanies().isEmpty()) {
            Condition companyCondition = trueCondition();
            if (!req.getRequired().getCompanies().isEmpty()) {
                companyCondition = companyCondition.and(COMPANY.COMPANY_NAME.in(req.getRequired().getCompanies()));
            }
            if (!req.getExcluded().getCompanies().isEmpty()) {
                companyCondition = companyCondition.and(COMPANY.COMPANY_NAME.notIn(req.getExcluded().getCompanies()));
            }
            tableConditionMap.put(COMPANY, companyCondition);
        }

        //org
        if (!req.getRequired().getOrgs().isEmpty() || !req.getExcluded().getOrgs().isEmpty()) {//org
            Condition orgCondition = trueCondition();
            if (!req.getRequired().getOrgs().isEmpty()) {
                orgCondition = orgCondition.and(ORG.ORG_NAME.in(req.getRequired().getOrgs()));
            }
            if (!req.getExcluded().getOrgs().isEmpty()) {
                orgCondition = orgCondition.and(ORG.ORG_NAME.notIn(req.getExcluded().getOrgs()));
            }
            tableConditionMap.put(ORG, orgCondition);
        }

        //repo
        if (!req.getRequired().getRepos().isEmpty() || !req.getExcluded().getRepos().isEmpty()) {
            Condition repoCondition = trueCondition();
            if (!req.getRequired().getRepos().isEmpty()) {
                repoCondition = repoCondition.and(REPO.REPO_NAME.in(req.getRequired().getRepos()));
            }
            if (!req.getExcluded().getRepos().isEmpty()) {
                repoCondition = repoCondition.and(REPO.REPO_NAME.notIn(req.getExcluded().getRepos()));
            }
            tableConditionMap.put(REPO, repoCondition);
        }

        {//branch
            List<String> branches = new ArrayList<>(req.getRequired().getBranches());
            List<String> notBranches = new ArrayList<>(req.getExcluded().getBranches());

            if (req.isShouldIncludeDefaultBranches()) {
                branches.addAll(defaultBranchNames);
            }

            if (!branches.isEmpty() || !notBranches.isEmpty()) {
                Condition branchCondition = trueCondition();
                if (!branches.isEmpty()) {
                    branchCondition = branchCondition.and(BRANCH.BRANCH_NAME.in(branches));
                }
                if (!notBranches.isEmpty()) {
                    branchCondition = branchCondition.and(BRANCH.BRANCH_NAME.notIn(notBranches));
                }

                tableConditionMap.put(BRANCH, branchCondition);
            }
        }

        //TOMAYBE: support jobInfo required and excluded (currently filtered at later stage).
        //phrasing this in SQL may be difficult and require rethinking indexes to avoid scans
        //{//jobInfo }

        { //Run
            final InstantRange range = req.getRange();
            Condition runCondition = RUN.RUN_DATE.ge(range.getStart());
            runCondition = runCondition.and(RUN.RUN_DATE.le(range.getEnd()));
            tableConditionMap.put(RUN, runCondition);
        }

        //Don't show runs without test data. Maybe should filter on test_suites_json but this for now
        tableConditionMap.put(TEST_RESULT, TEST_RESULT.TEST_RESULT_ID.isNotNull().and(TEST_RESULT.TESTS.greaterThan(0)));

        Long[] runIds = dsl.selectDistinct(RUN.RUN_ID.as("RUN_IDS"))
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                .where(tableConditionMap.getCondition(COMPANY)
                        .and(tableConditionMap.getCondition(ORG))
                        .and(tableConditionMap.getCondition(REPO))
                        .and(tableConditionMap.getCondition(BRANCH))
                        .and(tableConditionMap.getCondition(JOB))
                        .and(tableConditionMap.getCondition(RUN))
                        .and(tableConditionMap.getCondition(TEST_RESULT))
                )
                .fetchArray("RUN_IDS", Long.class);

        tableConditionMap.put(RUN, RUN.RUN_ID.in(runIds));
        return getCompanyGraphs(tableConditionMap, false);

    }

    protected List<CompanyGraph> getCompanyGraphs(TableConditionMap tableConditionMap) {
        return getCompanyGraphs(tableConditionMap, true);
    }

    @SneakyThrows(JsonProcessingException.class)
    @SuppressWarnings("rawtypes")
    protected List<CompanyGraph> getCompanyGraphs(TableConditionMap tableConditionMap, boolean shouldIncludeTestJson) {
        Result result = getFullTestGraph(tableConditionMap, shouldIncludeTestJson);
        if (!result.isEmpty() && result.get(0) instanceof Record1 record1) {
            String json = record1.formatJSON();
            log.info("getCompanyGraph json: " + json);
            return Arrays.asList(mapper.readValue(json, CompanyGraph[].class));
        }
        return Collections.emptyList();
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
    protected Result getFullTestGraph(TableConditionMap tableConditionMap, boolean shouldIncludeTestJson) {

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
                                                                        key("testResults").value(dsl.select(getTestResultSelect(shouldIncludeTestJson))
                                                                                .from(TEST_RESULT).where(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID).and(tableConditionMap.getCondition(TEST_RESULT))))
                                                                ))).from(STAGE).where(STAGE.RUN_FK.eq(RUN.RUN_ID).and(tableConditionMap.getCondition(STAGE))))
                                                        ))).from(RUN).where(RUN.JOB_FK.eq(JOB.JOB_ID).and(tableConditionMap.getCondition(RUN))))
                                                ))).from(JOB).where(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID).and(tableConditionMap.getCondition(JOB))))
                                        ))).from(BRANCH).where(BRANCH.REPO_FK.eq(REPO.REPO_ID).and(tableConditionMap.getCondition(BRANCH))))
                                ))).from(REPO).where(REPO.ORG_FK.eq(ORG.ORG_ID).and(tableConditionMap.getCondition(REPO))))
                        ))).from(ORG).where(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID).and(tableConditionMap.getCondition(ORG))))
                )).from(COMPANY).where(tableConditionMap.getCondition(COMPANY))
                .fetch();
    }

    JSONArrayAggOrderByStep<JSON> getTestResultSelect(boolean shouldIncludeTestResultJson) {

        final JSONArrayAggOrderByStep<JSON> testResultSelect;

        if (shouldIncludeTestResultJson) {
            testResultSelect = jsonArrayAgg(jsonObject(
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
            ));
        } else {
            testResultSelect = jsonArrayAgg(jsonObject(
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
                    key("hasSkip").value(TEST_RESULT.HAS_SKIP)
            ));
        }
        return testResultSelect;
    }

    public TreeSet<Long> getTestResultsWithoutSuiteJson(int maxCount) {
        Long[] ids = dsl.select(TEST_RESULT.TEST_RESULT_ID)
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_SUITES_JSON.isNull())
                .orderBy(TEST_RESULT.TEST_RESULT_ID.desc())
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
            String json = record1.get(0).toString();
            log.info("getTestSuitesGraph json: " + json);
            return Arrays.asList(mapper.readValue(json, TestSuiteGraph[].class));
        }
        return List.of(TestSuiteGraphBuilder.builder().build());
    }

    @SuppressWarnings("rawtypes")
    public String getTestSuitesGraphJson(Long testResultId) {
        Result result = getTestSuitesGraphResult(testResultId);
        if (!result.isEmpty() && result.get(0) instanceof Record1 record1) {
            if (record1.size() == 1 && record1.get(0) != null) {
                //convert from nested array to array
                String json = record1.get(0).toString();
                log.info("getTestSuitesGraphJson json: " + json);
                if (json != null && !"[null]".equals(json)) {
                    return json;
                }
            } else if (record1.size() > 1) {
                throw new IllegalStateException("Expected nested array with single element but got: " + record1.size() + " for record: " + record1);
            }
        }
        return "[]";
    }

    public void populateTestSuitesJson(Long testResultId) {
        final String testResultJson = getTestSuitesGraphJson(testResultId);
        updateTestResultJson(testResultId, testResultJson);
    }

    public List<JobDashboardMetrics> getPipelineDashboard(JobDashboardRequest request) {
        List<CompanyGraph> companyGraphs = getPipelineDashboardCompanyGraphs(request);
        return JobDashboardMetrics.fromCompanyGraphs(companyGraphs, request);
    }
    
    List<CompanyGraph> getPipelineDashboardCompanyGraphs(JobDashboardRequest request) {
        TableConditionMap tableConditionMap = new TableConditionMap();
        tableConditionMap.put(COMPANY, COMPANY.COMPANY_NAME.eq(request.getCompany()));
        tableConditionMap.put(ORG, ORG.ORG_NAME.eq(request.getOrg()));
        
        // Build conditions for lightweight runIds query
        Condition companyCondition = COMPANY.COMPANY_NAME.eq(request.getCompany());
        Condition orgCondition = ORG.ORG_NAME.eq(request.getOrg());
        Condition jobCondition = trueCondition();
        Condition runCondition = trueCondition();
        
        // Filter by jobInfos (only if provided)
        if (request.getJobInfos() != null && !request.getJobInfos().isEmpty()) {
            for (Map.Entry<String, String> entry : request.getJobInfos().entrySet()) {
                Condition jobInfoCondition = SqlJsonUtil.jobInfoContainsKeyValue(entry.getKey(), entry.getValue());
                log.info("Setting JOB condition: key='{}', value='{}', condition={}", entry.getKey(), entry.getValue(), jobInfoCondition);
                jobCondition = jobCondition.and(jobInfoCondition);
            }
            tableConditionMap.put(JOB, jobCondition);
        }
        
        // Filter by days
        if (request.getDays() != null) {
            Instant cutoff = Instant.now().minus(request.getDays(), ChronoUnit.DAYS);
            runCondition = runCondition.and(RUN.RUN_DATE.ge(cutoff));
        }
        
        // Step 1: Get runIds with lightweight query (prevents LIKE from propagating to STAGE/TEST_RESULT)
        // Optimized: Skip REPO/BRANCH joins since we don't filter on them
        Long[] runIds = dsl.selectDistinct(RUN.RUN_ID.as("RUN_IDS"))
                .from(RUN)
                .innerJoin(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                .innerJoin(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                .innerJoin(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                .innerJoin(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                .innerJoin(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
                .where(companyCondition.and(orgCondition).and(jobCondition).and(runCondition))
                .orderBy(RUN.RUN_ID.desc())
                .limit(10000)  // Safety limit to prevent excessive memory usage
                .fetchArray("RUN_IDS", Long.class);
        
        log.info("Found {} runIds matching filter", runIds.length);
        
        // Step 2: Use runIds to limit expensive graph query
        tableConditionMap.put(RUN, RUN.RUN_ID.in(runIds));
        
        // Include test data - but exclude testSuitesJson (only need aggregate counts)
        tableConditionMap.put(TEST_RESULT, TEST_RESULT.TEST_RESULT_ID.isNotNull());
        
        // Pass false to exclude testSuitesJson - massive performance improvement
        return getCompanyGraphs(tableConditionMap, false);
    }



    @SuppressWarnings("rawtypes")
    protected Result getTestSuitesGraphResult(Long testResultId) {

        return dsl.select(jsonArrayAgg(jsonObject(
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
                ))).from(TEST_SUITE).where(TEST_SUITE.TEST_RESULT_FK.eq(testResultId))
                .fetch();
    }

}