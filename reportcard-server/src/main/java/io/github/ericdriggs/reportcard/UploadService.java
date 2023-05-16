package io.github.ericdriggs.reportcard;

import io.github.ericdriggs.reportcard.gen.db.tables.daos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.records.*;

import io.github.ericdriggs.reportcard.model.TestCase;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.TestSuite;
import io.github.ericdriggs.reportcard.model.*;
import org.jooq.DSLContext;
import org.jooq.Record;

import org.jooq.SelectConditionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class UploadService extends AbstractReportCardService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    final OrgDao orgDao;
    final RepoDao repoDao;
    final BranchDao branchDao;
    final JobDao jobDao;
    final RunDao runDao;
    final StageDao stageDao;
    final TestResultDao testResultDao;
    final TestSuiteDao testSuiteDao;
    final TestCaseDao testCaseDao;

    @Autowired
    public UploadService(DSLContext dsl) {
        super(dsl);

        orgDao = new OrgDao(dsl.configuration());
        repoDao = new RepoDao(dsl.configuration());
        branchDao = new BranchDao(dsl.configuration());
        jobDao = new JobDao(dsl.configuration());
        runDao = new RunDao(dsl.configuration());
        stageDao = new StageDao(dsl.configuration());

        testResultDao = new TestResultDao(dsl.configuration());
        testSuiteDao = new TestSuiteDao(dsl.configuration());
        testCaseDao = new TestCaseDao(dsl.configuration());
    }

    public StagePath getStagePath(ReportMetaData request) {

        Map<String, String> metadataFilters = request.getJobInfo();
        SelectConditionStep<Record> selectConditionStep = dsl.
                select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(request.getOrg()))
                        .and(REPO.REPO_NAME.eq(request.getRepo()))
                )
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(request.getBranch())))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(request.getSha()))
                        .and(RUN.RUN_REFERENCE.eq(request.getRunReference())))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                        .and(STAGE.STAGE_NAME.eq(request.getStage())))
                .where(ORG.ORG_NAME.eq(request.getOrg()));

        Record record = selectConditionStep
                .fetchOne();

        StagePath stagePath = new StagePath();
        if (record == null) {
            return stagePath;
        }

        {
            Org _org = null;
            Repo _repo = null;
            Branch _branch = null;
            Job _context = null;
            Run _run = null;
            Stage _stage = null;

            if (record.get(ORG.ORG_ID.getName()) != null) {
                _org = record.into(OrgRecord.class).into(Org.class);
            }
            if (record.get(REPO.REPO_ID.getName()) != null) {
                _repo = record.into(RepoRecord.class).into(Repo.class);
            }
            if (record.get(BRANCH.BRANCH_ID.getName()) != null) {
                _branch = record.into(BranchRecord.class).into(Branch.class);
            }

            if (record.get(JOB.JOB_ID.getName()) != null) {
                _context = record.into(JobRecord.class).into(Job.class);
            }
            if (record.get(RUN.RUN_ID.getName()) != null) {
                _run = record.into(RunRecord.class).into(Run.class);
            }
            if (record.get(STAGE.STAGE_ID.getName()) != null) {
                _stage = record.into(StageRecord.class).into(Stage.class);
            }

            stagePath.setOrg(_org);
            stagePath.setRepo(_repo);
            stagePath.setBranch(_branch);
            stagePath.setJob(_context);
            stagePath.setRun(_run);
            stagePath.setStage(_stage);

        }
        return stagePath;
    }

    /**
     * @param request a BuildStagePathRequest with the fields to match on
     * @return the RunStagePath for the provided report metadata.
     */
    public StagePath getOrInsertStagePath(ReportMetaData request) {
        return getOrInsertStagePath(request, null);
    }

    /*
     * TODO: add test to simulate race condition on insert where buildstage path is missing data from db to ensure that
     *     1) insert failure is ignored/skipped and
     *     2) the existing data is returned
     */

    /**
     * prefer public method. The ability to inject an stagePath is for testing purposes.
     *
     * @param request      a ReportMetaData
     * @param stagePath normally null, only values passed for testing
     * @return the RunStagePath for the provided report metadata.
     */
    public StagePath getOrInsertStagePath(ReportMetaData request, StagePath stagePath) {

        request.validateAndSetDefaults();

        if (stagePath == null) {
            stagePath = getStagePath(request);
        }

        if (stagePath.getOrg() == null) {
            Org org = new Org()
                    .setOrgName(request.getOrg());
            orgDao.insert(org);
            stagePath.setOrg(org);
        }

        if (stagePath.getRepo() == null) {
            Repo repo = new Repo()
                    .setRepoName(request.getRepo())
                    .setOrgFk(stagePath.getOrg().getOrgId());
            repoDao.insert(repo);
            stagePath.setRepo(repo);
        }

        if (stagePath.getBranch() == null) {
            Branch branch = new Branch()
                    .setBranchName(request.getBranch())
                    .setRepoFk(stagePath.getRepo().getRepoId());
            branchDao.insert(branch);
            stagePath.setBranch(branch);
        }

        if (stagePath.getJob() == null) {
            Job job = new Job()
                    .setJobInfo(request.getJobInfoJson())
                    .setBranchFk(stagePath.getBranch().getBranchId());
            //jobDao.insert(job);
            //user insert since DAO/POJO would incorrectly attempt to insert generated column job_info_str
            Job insertedJob = dsl.insertInto(JOB, JOB.BRANCH_FK, JOB.JOB_INFO)
                    .values(stagePath.getBranch().getBranchId(), request.getJobInfoJson())
                    .returningResult(JOB.JOB_ID, JOB.JOB_INFO, JOB.BRANCH_FK, JOB.JOB_INFO_STR)
                    .fetchOne().into(Job.class);

            stagePath.setJob(insertedJob);
        }

        if (stagePath.getRun() == null) {
            Run run = new Run()
                    .setRunReference(request.getRunReference())
                    .setSha(request.getSha())
                    .setJobFk(stagePath.getJob().getJobId());
            runDao.insert(run);
            stagePath.setRun(run);
        }

        if (stagePath.getStage() == null) {
            Stage stage = new Stage()
                    .setStageName(request.getStage())
                    .setRunFk(stagePath.getRun().getRunId());
            stageDao.insert(stage);
            stagePath.setStage(stage);
        }
        return stagePath;
    }

    public TestResult getTestResult(Long testResultId) {

        TestResult testResult = dsl.
                select(TEST_RESULT.fields())
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId))
                .fetchOne().into(TestResult.class);

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
        return testResult;
    }

    public Set<TestResult> getTestResults(Long stageId) {

        Set<TestResult> testResults = new TreeSet<>(ModelComparators.TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER);
        testResults.addAll(
                dsl.
                        select(TEST_RESULT.fields())
                        .from(STAGE
                                .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                        ).where(STAGE.STAGE_ID.eq(stageId))
                        .fetchInto(TestResult.class));

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

        if (testResult.getStageFk() == null) {
            throw new NullPointerException("testResult.getStageFk()");
        }

        if (testResult.getTestResultId() == null) {
            List<TestSuite> testSuites = testResult.getTestSuites();
            TestResultRecord testResultRecord = dsl.newRecord(TEST_RESULT);
            testResultRecord.setStageFk(testResult.getStageFk())
                    .setError(testResult.getError())
                    .setFailure(testResult.getFailure())
                    .setSkipped(testResult.getSkipped())
                    .setTests(testResult.getTests())
                    .setTime(testResult.getTime())
                    .setExternalLinks(testResult.getExternalLinks())
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
                        .setName(testSuite.getName())
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
                            .setTestCaseId(testCase.getTestCaseId())
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

    public Map<Byte, String> getTestStatusMap() {
        List<TestStatusRecord> testStatusRecords = dsl.
                select(TEST_STATUS.fields())
                .from(TEST_STATUS)
                .fetchInto(TestStatusRecord.class);

        Map<Byte, String> testStatusMap = new TreeMap<>();
        for (TestStatusRecord testStatusRecord : testStatusRecords) {
            testStatusMap.put(testStatusRecord.getTestStatusId(), testStatusRecord.getTestStatusName());
        }

        return testStatusMap;
    }

}