package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.tables.daos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.records.*;

import io.github.ericdriggs.reportcard.model.*;
import lombok.SneakyThrows;
import org.jooq.*;

import org.jooq.Record;
import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
public class StagePathPersistService extends AbstractPersistService {

    protected final static ObjectMapper mapper = new ObjectMapper();

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    final protected CompanyDao companyDao;
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

        companyDao = new CompanyDao(dsl.configuration());
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
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                        .and(STAGE.STAGE_NAME.eq(stageName)))
                .where(RUN.RUN_ID.eq(runId));
        return doGetStagePath(selectConditionStep, null);
    }

    public StagePath getStagePath(Long stageId) {

        SelectConditionStep<Record> selectConditionStep = dsl.
                select()
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .where(STAGE.STAGE_ID.eq(stageId));
        return doGetStagePath(selectConditionStep, null);
    }

    public StagePath getStagePath(StageDetails request) {

        Map<String, String> metadataFilters = request.getJobInfo();
        SelectConditionStep<Record> selectConditionStep = dsl.
                select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(request.getOrg())))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(request.getRepo())))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(request.getBranch())))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(request.getSha()))
                        .and(RUN.RUN_REFERENCE.eq(request.getRunReference())))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                        .and(STAGE.STAGE_NAME.eq(request.getStage())))
                .where(COMPANY.COMPANY_NAME.eq(request.getCompany()));
        return doGetStagePath(selectConditionStep, request);
    }

    /**
     * Gets stage path using provided left join query and optional stageDetails
     *
     * @param selectConditionStep the query
     * @param stageDetails        optional stage details (used to match job info map)
     * @return matching stage path where some of the elements may be null
     */
    @SneakyThrows(JsonProcessingException.class)
    protected StagePath doGetStagePath(SelectConditionStep<Record> selectConditionStep, StageDetails stageDetails) {

        Result<Record> records = selectConditionStep
                .fetch();

        /*
         * stagePath may only be partially populated
         */
        StagePath returnStagePath = new StagePath();
        for (Record record : records) {
            StagePath stagePath = new StagePath();
            if (record == null) {
                return stagePath;
            }

            Company _company = null;
            Org _org = null;
            Repo _repo = null;
            Branch _branch = null;
            Job _job = null;
            Run _run = null;
            Stage _stage = null;

            //TODO: explain why go through record to get to pojo
            if (record.get(COMPANY.COMPANY_ID.getName()) != null) {
                _company = record.into(CompanyRecord.class).into(Company.class);
            }
            if (record.get(ORG.ORG_ID.getName()) != null) {
                _org = record.into(OrgRecord.class).into(Org.class);
            }
            if (record.get(REPO.REPO_ID.getName()) != null) {
                _repo = record.into(RepoRecord.class).into(Repo.class);
            }
            if (record.get(BRANCH.BRANCH_ID.getName()) != null) {
                _branch = record.into(BranchRecord.class).into(Branch.class);
            }

            /*
             * It's difficult to compare json strings directly so use TreeMap<String,String> for comparisons
             */
            if (record.get(JOB.JOB_ID.getName()) != null) {
                _job = record.into(JobRecord.class).into(Job.class);
                if (stageDetails != null && !ObjectUtils.isEmpty(stageDetails.getJobInfo()) && !StringUtils.isEmpty(_job.getJobInfo())) {
                    @SuppressWarnings("unchecked")
                    TreeMap<String, String> jobInfo = mapper.readValue(_job.getJobInfo(), TreeMap.class);

                    if (!jobInfo.equals(stageDetails.getJobInfo())) {
                        log.debug("jobInfo: {}  != request.getJobInfo: {}", jobInfo, stageDetails.getJobInfo());
                        //retain stagePath as return candidate in case no other record matches on jobInfo
                        returnStagePath = stagePath;
                        continue;
                    }
                }
            }
            if (record.get(RUN.RUN_ID.getName()) != null) {
                _run = record.into(RunRecord.class).into(Run.class);
            }
            if (record.get(STAGE.STAGE_ID.getName()) != null) {
                _stage = record.into(StageRecord.class).into(Stage.class);
            }

            stagePath.setCompany(_company);
            stagePath.setOrg(_org);
            stagePath.setRepo(_repo);
            stagePath.setBranch(_branch);
            stagePath.setJob(_job);
            stagePath.setRun(_run);
            stagePath.setStage(_stage);
            returnStagePath = stagePath;
        }
        return returnStagePath;

    }

    /**
     * @param request a BuildStagePathRequest with the fields to match on
     * @return the RunStagePath for the provided report metadata.
     */
    public StagePath getUpsertedStagePath(StageDetails request) {
        return getUpsertedStagePath(request, null);
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
     * prefer version which only accepts request. The ability to inject a stagePath is for testing purposes.
     *
     * @param request   a ReportMetaData
     * @param stagePath normally null, only values passed for testing
     * @return the RunStagePath for the provided report metadata.
     */
    @SuppressWarnings("resource")
    public StagePath getUpsertedStagePath(StageDetails request, StagePath stagePath) {

        LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);

        if (stagePath == null) {
            stagePath = getStagePath(request);
        }

        if (stagePath.getCompany() == null) {
            Company company = new Company()
                    .setCompanyName(request.getCompany());
            companyDao.insert(company);
            stagePath.setCompany(company);
        }

        if (stagePath.getOrg() == null) {
            Org org = new Org()
                    .setOrgName(request.getOrg())
                    .setCompanyFk(stagePath.getCompany().getCompanyId());
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
                    .setRepoFk(stagePath.getRepo().getRepoId())
                    .setLastRun(nowUTC);
            branchDao.insert(branch);
            stagePath.setBranch(branch);
        } else {
            Branch branch = stagePath.getBranch();
            //TODO: update lastRun AFTER post data
            //branch.setLastRun(nowUTC);
            branchDao.update(branch);
        }

        if (stagePath.getJob() == null) {
            Job job = new Job()
                    .setJobInfo(request.getJobInfoJson())
                    .setBranchFk(stagePath.getBranch().getBranchId())
                    .setLastRun(nowUTC);
            // insert since DAO/POJO would incorrectly attempt to insert generated column job_info_str
            Job insertedJob = dsl.insertInto(JOB, JOB.BRANCH_FK, JOB.JOB_INFO, JOB.LAST_RUN)
                    .values(stagePath.getBranch().getBranchId(), request.getJobInfoJson(), nowUTC)
                    .returningResult(JOB.JOB_ID, JOB.JOB_INFO, JOB.BRANCH_FK, JOB.JOB_INFO_STR, JOB.LAST_RUN)
                    .fetchOne().into(Job.class);

            stagePath.setJob(insertedJob);
        }

        if (stagePath.getRun() == null) {
            //Result<Record1<Integer>> countRecord = dsl.selectCount().from(RUN).where(RUN.JOB_FK.eq(JOB.JOB_ID)).fetch();
            int runCount = 1 + dsl.fetchCount(selectFrom(RUN).where(RUN.JOB_FK.eq(stagePath.getJob().getJobId())));

            Run run = new Run()
                    .setRunReference(request.getRunReference())
                    .setSha(request.getSha())
                    .setJobFk(stagePath.getJob().getJobId())
                    .setJobRunCount(runCount)
                    .setCreated(nowUTC);
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

    public void updateLastRunToNow(StagePath stagePath) {
        LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        if (stagePath.getBranch() != null) {
            Branch branch = stagePath.getBranch();
            branch.setLastRun(nowUTC);
            branchDao.update(branch);
        }

        if (stagePath.getJob() != null) {
            Job job = stagePath.getJob();
            job.setLastRun(nowUTC);
            // insert since DAO/POJO would incorrectly attempt to insert generated column job_info_str
            dsl.update(JOB)
                    .set(JOB.LAST_RUN, nowUTC)
                    .where(JOB.JOB_ID.eq(job.getJobId())).execute();
        }

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