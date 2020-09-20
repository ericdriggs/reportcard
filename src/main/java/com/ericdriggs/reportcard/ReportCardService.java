package com.ericdriggs.reportcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ericdriggs.reportcard.db.tables.daos.*;
import com.ericdriggs.reportcard.db.tables.pojos.*;
import com.ericdriggs.reportcard.db.tables.records.*;
import com.ericdriggs.reportcard.model.*;
import com.ericdriggs.reportcard.model.TestCase;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.model.TestSuite;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import static com.ericdriggs.reportcard.db.Tables.*;

@Service
@SuppressWarnings("unused")
/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */
public class ReportCardService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DSLContext dsl;

    @Autowired
    private ModelMapper mapper;

    final OrgDao orgDao;
    final RepoDao repoDao;
    final AppDao appDao;
    final BranchDao branchDao;
    final AppBranchDao appBranchDao;
    //    final BuildDao buildDao;
    final StageDao stageDao;
    final BuildStageDao buildStageDao;

    final TestResultDao testResultDao;
    final TestSuiteDao testSuiteDao;
    final TestCaseDao testCaseDao;


    private ReportCardService() {
        throw new RuntimeException("needs dsl in constructor");
    }

    @Autowired
    public ReportCardService(DSLContext dsl, ModelMapper mapper) {

        this.dsl = dsl;
        this.mapper = mapper;
        orgDao = new OrgDao(dsl.configuration());
        repoDao = new RepoDao(dsl.configuration());
        appDao = new AppDao(dsl.configuration());
        branchDao = new BranchDao(dsl.configuration());
        appBranchDao = new AppBranchDao(dsl.configuration());
//        buildDao = new BuildDao(dsl.configuration());
        stageDao = new StageDao(dsl.configuration());
        buildStageDao = new BuildStageDao(dsl.configuration());
        testResultDao = new TestResultDao(dsl.configuration());
        testSuiteDao = new TestSuiteDao(dsl.configuration());
        testCaseDao = new TestCaseDao(dsl.configuration());
    }


    public List<Org> getOrgs() {
        return dsl.select().from(ORG)
                .fetch()
                .into(Org.class);
    }

    public Org getOrg(String org) {
        Org ret = dsl.select().from(ORG)
                .where(ORG.ORG_NAME.eq(org))
                .fetchOne()
                .into(Org.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org);
        }
        return ret;

    }

    public List<Repo> getRepos(String org) {
        return dsl.
                select(REPO.fields())
                .from(REPO.join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .fetch()
                .into(Repo.class);
    }

    public Repo getRepo(String org, String repo) {
        Repo ret = dsl.
                select(REPO.fields()).from(REPO).join(ORG)
                .on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetchOne()
                .into(Repo.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo);
        }
        return ret;
    }

    public List<App> getApps(String org, String repo) {
        return dsl.
                select(APP.fields())
                .from(APP.join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(App.class);
    }

    public App getApp(String org, String repo, String app) {
        App ret = dsl.
                select(APP.fields())
                .from(APP.join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .fetchOne()
                .into(App.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo + ", app: " + app);
        }
        return ret;
    }

    public List<Branch> getBranches(String org, String repo) {
        return dsl.
                select(BRANCH.fields())
                .from(BRANCH.join(REPO)
                        .on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(Branch.class);
    }

    public Branch getBranch(String org, String repo, String branch) {
        Branch ret = dsl.
                select(BRANCH.fields())
                .from(BRANCH.join(REPO)
                        .on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(Branch.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo + ", branch: " + branch);
        }
        return ret;
    }

    public AppBranch getAppBranch(String org, String repo, String app, String branch) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");
        AppBranch ret = dsl.
                select(APP_BRANCH.fields())
                .from(APP_BRANCH
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(REPO)
                        .on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO2)
                        .on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(AppBranch.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch);
        }
        return ret;
    }

    public List<Build> getBuilds(String org, String repo, String app, String branch) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        List<Build> ret = dsl.
                select(BUILD.fields())
                .from(BUILD
                        .join(APP_BRANCH).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetch()
                .into(Build.class);
        return ret;
    }


    public Build getBuild(String org, String repo, String app, String branch, String buildUniqueString) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        Build ret = dsl.
                select(BUILD.fields())
                .from(BUILD
                        .join(APP_BRANCH).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(BUILD.BUILD_UNIQUE_STRING.eq(buildUniqueString))
                .fetchOne()
                .into(Build.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", buildUniqueString: " + buildUniqueString);
        }
        return ret;
    }

    public List<Stage> getStages(String org, String repo, String app, String branch) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        List<Stage> ret = dsl.
                select(STAGE.fields())
                .from(STAGE
                        .join(APP_BRANCH).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetch()
                .into(Stage.class);
        return ret;
    }


    public Stage getStage(String org, String repo, String app, String branch, String stage) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        Stage ret = dsl.
                select(STAGE.fields())
                .from(STAGE
                        .join(APP_BRANCH).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(STAGE.STAGE_NAME.eq(stage))
                .fetchOne()
                .into(Stage.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", stage: " + stage);
        }
        return ret;
    }

    public Stage getBuildStage(String org, String repo, String app, String branch, String buildUniqueString, String stage) {
        final com.ericdriggs.reportcard.db.tables.Repo REPO2 = REPO.as("REPO2");

        Stage ret = dsl.
                select(BUILD_STAGE.fields())
                .from(BUILD_STAGE
                        .join(BUILD).on(BUILD_STAGE.BUILD_FK.eq(BUILD.BUILD_ID))
                        .join(STAGE).on(BUILD_STAGE.STAGE_FK.eq(STAGE.STAGE_ID))
                        .join(APP_BRANCH).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID))
                        .join(APP).on(APP_BRANCH.APP_FK.eq(APP.APP_ID))
                        .join(BRANCH).on(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(APP.REPO_FK.eq(REPO.REPO_ID))
                        .join(REPO2).on(BRANCH.REPO_FK.eq(REPO2.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                )
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(APP.APP_NAME.eq(app))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(BUILD.BUILD_UNIQUE_STRING.eq(buildUniqueString))
                .and(STAGE.STAGE_NAME.eq(stage))
                .fetchOne()
                .into(Stage.class);
        if (ret == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unable to find org:" + org + ", repo: " + repo +
                            ", app: " + app + ", branch: " + branch +
                            ", stage: " + stage);
        }
        return ret;
    }

    public BuildStagePath getBuildStagePath(ReportMetatData request) {

        //String org, String repo, String app, String branch, Integer buildOrdinal, String stage

        Record record = dsl.
                select()
                .from(ORG
                        .leftJoin(REPO).on(ORG.ORG_ID.eq(REPO.ORG_FK)).and(REPO.REPO_NAME.eq(request.getRepo()))
                        .leftJoin(APP).on(APP.REPO_FK.eq(REPO.REPO_ID)).and(APP.APP_NAME.eq(request.getApp()))
                        .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)).and(BRANCH.BRANCH_NAME.eq(request.getBranch()))
                        .leftJoin(APP_BRANCH).on(APP_BRANCH.APP_FK.eq(APP.APP_ID).and(APP_BRANCH.BRANCH_FK.eq(BRANCH.BRANCH_ID)))
                        .leftJoin(BUILD).on(BUILD.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID)).and(BUILD.BUILD_UNIQUE_STRING.eq(request.getBuildIdentifier()))
                        .leftJoin(STAGE).on(STAGE.APP_BRANCH_FK.eq(APP_BRANCH.APP_BRANCH_ID)).and(STAGE.STAGE_NAME.eq(request.getStage()))
                        .leftJoin(BUILD_STAGE).on(BUILD_STAGE.BUILD_FK.eq(BUILD.BUILD_ID).and(BUILD_STAGE.STAGE_FK.eq(STAGE.STAGE_ID)))
                ).where(ORG.ORG_NAME.eq(request.getOrg()))
                .fetchOne();

        BuildStagePath buildStagePath = new BuildStagePath();
        if (record == null) {
            return buildStagePath;
        }

        {
            Org _org = null;
            Repo _repo = null;
            Branch _branch = null;
            App _app = null;
            AppBranch _appBranch = null;
            Build _build = null;
            Stage _stage = null;
            BuildStage _buildStage = null;

            if (record.get(ORG.ORG_ID.getName()) != null) {
                _org = record.into(OrgRecord.class).into(Org.class);
            }
            if (record.get(REPO.REPO_ID.getName()) != null) {
                _repo = record.into(RepoRecord.class).into(Repo.class);
            }
            if (record.get(APP.APP_ID.getName()) != null) {
                _app = record.into(AppRecord.class).into(App.class);
            }
            if (record.get(BRANCH.BRANCH_ID.getName()) != null) {
                _branch = record.into(BranchRecord.class).into(Branch.class);
            }
            if (record.get(APP_BRANCH.APP_BRANCH_ID.getName()) != null) {
                _appBranch = record.into(AppBranchRecord.class).into(AppBranch.class);
            }
            if (record.get(BUILD.BUILD_ID.getName()) != null) {
                _build = record.into(BuildRecord.class).into(Build.class);
            }
            if (record.get(STAGE.STAGE_ID.getName()) != null) {
                _stage = record.into(StageRecord.class).into(Stage.class);
            }
            if (record.get(BUILD_STAGE.BUILD_STAGE_ID.getName()) != null) {
                _buildStage = record.into(BuildStageRecord.class).into(BuildStage.class);
            }

            buildStagePath.setOrg(_org);
            buildStagePath.setRepo(_repo);
            buildStagePath.setApp(_app);
            buildStagePath.setBranch(_branch);
            buildStagePath.setAppBranch(_appBranch);
            buildStagePath.setBuild(_build);
            buildStagePath.setStage(_stage);
            buildStagePath.setBuildStage(_buildStage);

        }
        return buildStagePath;
    }

    /**
     * @param request a BuildStagePathRequest with the fields to match on
     * @return
     */
    public BuildStagePath getOrInsertBuildStagePath(ReportMetatData request) {
        return getOrInsertBuildStagePath(request, null);
    }

    /**
     * TODO: add test to simulate race condition on insert where buildstage path is missing data from db to ensure that
     *     1) insert failure is ignored/skipped and
     *     2) the existing data is returned
     */


    /**
     * prefer public method -- t
     *
     * @param request        a BuildStagePathRequest
     * @param buildStagePath normally null, only values passed for testing
     * @return
     */
    BuildStagePath getOrInsertBuildStagePath(ReportMetatData request, BuildStagePath buildStagePath) {

        request.validateAndSetDefaults();

        if (buildStagePath == null) {
            buildStagePath = getBuildStagePath(request);
        }

        if (buildStagePath.getOrg() == null) {
            Org org = new Org()
                    .setOrgName(request.getOrg());
            orgDao.insert(org);
            buildStagePath.setOrg(org);
        }

        if (buildStagePath.getRepo() == null) {
            Repo repo = new Repo()
                    .setRepoName(request.getRepo())
                    .setOrgFk(buildStagePath.getOrg().getOrgId());
            repoDao.insert(repo);
            buildStagePath.setRepo(repo);
        }

        if (buildStagePath.getApp() == null) {
            App app = new App()
                    .setAppName(request.getApp())
                    .setRepoFk(buildStagePath.getRepo().getRepoId());
            appDao.insert(app);
            buildStagePath.setApp(app);
        }

        if (buildStagePath.getBranch() == null) {
            Branch branch = new Branch()
                    .setBranchName(request.getBranch())
                    .setRepoFk(buildStagePath.getRepo().getRepoId());
            branchDao.insert(branch);
            buildStagePath.setBranch(branch);
        }

        if (buildStagePath.getAppBranch() == null) {
            AppBranch appBranch = new AppBranch()
                    .setAppFk(buildStagePath.getApp().getAppId())
                    .setBranchFk(buildStagePath.getBranch().getBranchId());
            appBranchDao.insert(appBranch);
            buildStagePath.setAppBranch(appBranch);
        }

        //Can't use dao since won't handle null columns correctly
        if (buildStagePath.getBuild() == null) {
            Record record = dsl
                    .insertInto(BUILD, BUILD.APP_BRANCH_FK, BUILD.BUILD_UNIQUE_STRING)
                    .values(buildStagePath.getAppBranch().getAppBranchId(), request.getBuildIdentifier())
                    .onConflictDoNothing()
                    .returning()
                    .fetchOne();

            Build build = record.into(BuildRecord.class).into(Build.class);
            buildStagePath.setBuild(build);
        }

        if (buildStagePath.getStage() == null) {
            Stage stage = new Stage()
                    .setStageName(request.getStage())
                    .setAppBranchFk(buildStagePath.getAppBranch().getAppBranchId());
            stageDao.insert(stage);
            buildStagePath.setStage(stage);
        }
        if (buildStagePath.getBuildStage() == null) {
            BuildStage buildStage = new BuildStage()
                    .setBuildFk(buildStagePath.getBuild().getBuildId())
                    .setStageFk(buildStagePath.getStage().getStageId());
            buildStageDao.insert(buildStage);
            buildStagePath.setBuildStage(buildStage);
        }
        return buildStagePath;
    }

    public List<TestResult> getTestResults(Long buildStageId) {

        List<TestResult> testResults = dsl.
                select(TEST_RESULT.fields())
                .from(BUILD_STAGE
                        .join(TEST_RESULT).on(TEST_RESULT.BUILD_STAGE_FK.eq(BUILD_STAGE.BUILD_STAGE_ID))
                ).where(BUILD_STAGE.BUILD_STAGE_ID.eq(buildStageId))
                .fetchInto(TestResult.class);

        for (TestResult testResult : testResults) {

            List<TestSuite> testSuites = dsl.
                    select(TEST_SUITE.fields())
                    .from(TEST_RESULT
                            .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                    ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchInto(TestSuite.class);

            testResult.setTestSuites(testSuites);

            for (TestSuite testSuite : testResult.getTestSuites()) {
                List<TestCase> testCases = dsl.
                        select(TEST_CASE.fields())
                        .from(TEST_CASE
                                .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                        ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                        .fetchInto(TestCase.class);

                testSuite.setTestCases(testCases);
            }

        }
        return testResults;
    }

    public TestResult insertTestResult(TestResult testResult) {

        if (testResult.getBuildStageFk() == null) {
            throw new NullPointerException("testResult.getBuildStageFk()");
        }

        if (testResult.getTestResultId() == null) {
            List<TestSuite> testSuites = testResult.getTestSuites();
            TestResultRecord testResultRecord = dsl.newRecord(TEST_RESULT);
            testResultRecord.setBuildStageFk(testResult.getBuildStageFk())
                    .setError(testResult.getError())
                    .setFailure(testResult.getFailure())
                    .setSkipped(testResult.getSkipped())
                    .setTests(testResult.getTests())
                    .setTime(testResult.getTime())
                    .store();

            testResult = testResultRecord.into(TestResult.class);

            //need select for generated values
            testResult = dsl.select().from(TEST_RESULT)
                    .where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchOne()
                    .into(TestResultRecord.class).into(TestResult.class);
            testResult.setTestSuites(testSuites);
        }

        if (testResult.getTestSuites().isEmpty()) {
            log.warn("testSuites.isEmpty()");
        }

        List<TestSuite> testSuites = new ArrayList<>();
        for (TestSuite testSuite : testResult.getTestSuites()) {
            if (testSuite.getTestSuiteId() == null) {
                List<TestCase> testCases = testSuite.getTestCases();
                TestSuiteRecord testSuiteRecord = dsl.newRecord(TEST_SUITE);
                testSuiteRecord.setTestResultFk(testResult.getTestResultId())
                        .setError(testSuite.getError())
                        .setFailure(testSuite.getFailure())
                        .setSkipped(testSuite.getSkipped())
                        .setTests(testSuite.getTests())
                        .setTime(testSuite.getTime())
                        .setPackage(testSuite.getPackage())
                        .store();

                //need select for generated values
                testSuite = dsl.select().from(TEST_SUITE)
                        .where(TEST_SUITE.TEST_SUITE_ID.eq(testSuiteRecord.getTestSuiteId()))
                        .fetchOne()
                        .into(TestSuiteRecord.class).into(TestSuite.class);

                testSuite.setTestCases(testCases);
                testSuites.add(testSuite);
            }

            if (testSuite.getTestCases().isEmpty()) {
                log.warn("testCases.isEmpty()");
            }

            final List<TestCase> testCases = new ArrayList<>();
            for (TestCase testCase : testSuite.getTestCases()) {
                if (testCase.getTestCaseId() == null) {

                    TestCaseRecord testCaseRecord = dsl.newRecord(TEST_CASE);
                    testCaseRecord.setTestSuiteFk(testSuite.getTestSuiteId())
                            .setTestStatusFk(testCase.getTestStatusFk())
                            .setClassName(testCase.getClassName())
                            .setName(testCase.getName())
                            .setTime(testCase.getTime())
                            .store();

                    testCase = testCaseRecord.into(TestCase.class);
                    testCases.add(testCase);
                }
            }
            testSuite.setTestCases(testCases);
        }
        testResult.setTestSuites(testSuites);

        return testResult;
    }

    public Map<Integer, String> getTestStatusMap() {
        List<TestStatusRecord> testStatusRecords = dsl.
                select(TEST_STATUS.fields())
                .from(TEST_STATUS)
                .fetchInto(TestStatusRecord.class);

        Map<Integer, String> testStatusMap = new TreeMap<>();
        for (TestStatusRecord testStatusRecord : testStatusRecords) {
            testStatusMap.put(testStatusRecord.getTestStatusId().intValue(), testStatusRecord.getTestStatusName());
        }

        return testStatusMap;
    }

}