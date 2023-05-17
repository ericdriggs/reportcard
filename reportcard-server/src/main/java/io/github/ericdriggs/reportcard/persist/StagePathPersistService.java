package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.daos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.records.*;

import io.github.ericdriggs.reportcard.model.TestCase;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.TestSuite;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.xml.XmlUtil;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import org.jooq.DSLContext;
import org.jooq.Record;

import org.jooq.SelectConditionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class StagePathPersistService extends AbstractPersistService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    final protected OrgDao orgDao;
    final protected RepoDao repoDao;
    final protected BranchDao branchDao;
    final protected JobDao jobDao;
    final protected RunDao runDao;
    final protected StageDao stageDao;
    final protected TestResultDao testResultDao;
    final protected TestSuiteDao testSuiteDao;
    final protected TestCaseDao testCaseDao;

    @Autowired
    public StagePathPersistService(DSLContext dsl) {
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


    public StagePath getStagePath(Long runId, String stageName) {

        SelectConditionStep<Record> selectConditionStep = dsl.
                select()
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                        .and(STAGE.STAGE_NAME.eq(stageName)))
                .where(RUN.RUN_ID.eq(runId));
        return doGetStagePath(selectConditionStep);
    }

    public StagePath getStagePath(Long stageId) {

        SelectConditionStep<Record> selectConditionStep = dsl.
                select()
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .where(STAGE.STAGE_ID.eq(stageId));
        return doGetStagePath(selectConditionStep);
    }

    public StagePath getStagePath(StageDetails request) {

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
        return doGetStagePath(selectConditionStep);
    }

    protected StagePath doGetStagePath(SelectConditionStep<Record> selectConditionStep) {

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
    public StagePath getOrInsertStagePath(StageDetails request) {
        return getOrInsertStagePath(request, null);
    }

    /*
     * TODO: add test to simulate race condition on insert where buildstage path is missing data from db to ensure that
     *     1) insert failure is ignored/skipped and
     *     2) the existing data is returned
     */

    public StagePath getOrInsertStage(Long runId, String stageName) {
        StagePath stagePath = getStagePath(runId, stageName);
        if (stagePath.getStage() == null) {
            if (stagePath.getStage() == null) {
                Stage stage = new Stage()
                        .setStageName(stageName)
                        .setRunFk(stagePath.getRun().getRunId());
                stageDao.insert(stage);
                stagePath.setStage(stage);
            }
        }
        return stagePath;
    }

    /**
     * prefer public method. The ability to inject an stagePath is for testing purposes.
     *
     * @param request   a ReportMetaData
     * @param stagePath normally null, only values passed for testing
     * @return the RunStagePath for the provided report metadata.
     */
    public StagePath getOrInsertStagePath(StageDetails request, StagePath stagePath) {

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