package io.github.ericdriggs.reportcard;

import io.github.ericdriggs.reportcard.gen.db.tables.daos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.records.*;
import io.github.ericdriggs.reportcard.model.Comparators;
import io.github.ericdriggs.reportcard.model.TestCase;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.TestSuite;
import io.github.ericdriggs.reportcard.model.*;
import org.jooq.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class ReportCardService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DSLContext dsl;

    @Autowired
    private final ModelMapper mapper;

    final OrgDao orgDao;
    final RepoDao repoDao;
    final BranchDao branchDao;
    final JobDao jobDao;
    final ExecutionDao executionDao;
    final StageDao stageDao;
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
        branchDao = new BranchDao(dsl.configuration());
        jobDao = new JobDao(dsl.configuration());
        executionDao = new ExecutionDao(dsl.configuration());
        stageDao = new StageDao(dsl.configuration());

        testResultDao = new TestResultDao(dsl.configuration());
        testSuiteDao = new TestSuiteDao(dsl.configuration());
        testCaseDao = new TestCaseDao(dsl.configuration());
    }

    public Set<Org> getOrgs() {
        Set<Org> orgs = new TreeSet<>(Comparators.ORG_CASE_INSENSITIVE_ORDER);
        orgs.addAll(dsl.select().from(ORG)
                .fetch()
                .into(Org.class));
        return orgs;
    }

    public Map<Org, Set<Repo>> getOrgsRepos() {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .fetch();

        Map<Org, Set<Repo>> orgRepoMap = new TreeMap<>(Comparators.ORG_CASE_INSENSITIVE_ORDER);
        for (Record record : recordResult) {
            Org org = record.into(Org.class);
            Repo repo = record.into(Repo.class);

            if (!orgRepoMap.containsKey(org)) {
                orgRepoMap.put(org, new TreeSet<>(Comparators.REPO_CASE_INSENSITIVE_ORDER));
            }
            orgRepoMap.get(org).add(repo);
        }
        return orgRepoMap;
    }

    public Org getOrg(String orgName) {
        Record record =
                dsl.select(ORG.fields()).from(ORG)
                        .where(ORG.ORG_NAME.eq(orgName))
                        .fetchOne();
        if (record == null) {
            throwNotFound(orgName);
        }
        return record.into(Org.class);
    }

    public Map<Org, Map<Repo, Set<Branch>>> getOrgReposBranches(String orgName) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        Map<Repo, Set<Branch>> repoBranchMap = new TreeMap<>(Comparators.REPO_CASE_INSENSITIVE_ORDER);
        Org org = null;
        for (Record record : recordResult) {
            if (org == null) {
                org = record.into(Org.class);
            }
            Repo repo = record.into(Repo.class);
            Branch branch = record.into(Branch.class);

            if (!repoBranchMap.containsKey(repo)) {
                repoBranchMap.put(repo, new TreeSet<>(Comparators.BRANCH_CASE_INSENSITIVE_ORDER));
            }
            repoBranchMap.get(repo).add(branch);
        }
        return Collections.singletonMap(org, repoBranchMap);
    }

    public Repo getRepo(String orgName, String repoName) {
        Repo ret = dsl.
                select(REPO.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName))
                )
                .where(ORG.ORG_NAME.eq(orgName))
                .fetchOne()
                .into(Repo.class);
        if (ret == null) {
            throwNotFound(orgName, repoName);
        }
        return ret;
    }

    public Map<Repo, Map<Branch, Set<Job>>> getRepoBranchesJobs(String orgName, String repoName) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        Map<Branch, Set<Job>> branchesJobsMap = new TreeMap<>(Comparators.BRANCH_CASE_INSENSITIVE_ORDER);
        Repo repo = null;
        for (Record record : recordResult) {
            if (repo == null) {
                repo = record.into(Repo.class);
            }
            Branch branch = record.into(Branch.class);
            Job job = record.into(Job.class);

            if (!branchesJobsMap.containsKey(branch)) {
                branchesJobsMap.put(branch, new TreeSet<>(Comparators.JOB_CASE_INSENSITIVE_ORDER));
            }
            branchesJobsMap.get(branch).add(job);
        }
        return Collections.singletonMap(repo, branchesJobsMap);
    }

    public Branch getBranch(String orgName, String repoName, String branchName) {
        Branch ret = dsl.select(BRANCH.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetchOne()
                .into(Branch.class);
        if (ret == null) {
            throwNotFound(orgName, repoName, branchName);
        }
        return ret;
    }

    public Map<Branch, Map<Job, Set<Execution>>> getBranchJobsExecutions(String orgName, String repoName, String branchName, Map<String, String> jobInfoFilters) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        //TODO: filter jobs
        Map<Job, Set<Execution>> jobExecutionMap = new TreeMap<>(Comparators.JOB_CASE_INSENSITIVE_ORDER);
        Branch branch = null;
        for (Record record : recordResult) {
            if (branch == null) {
                branch = record.into(Branch.class);
            }
            Job job = record.into(Job.class);
            Execution execution = record.into(Execution.class);

            if (!jobExecutionMap.containsKey(job)) {
                jobExecutionMap.put(job, new TreeSet<>(Comparators.EXECUTION_CASE_INSENSITIVE_ORDER));
            }
            jobExecutionMap.get(job).add(execution);
        }
        return Collections.singletonMap(branch, jobExecutionMap);
    }

    public Job getJob(String orgName, String repoName, String branchName, Long jobId) {
        Set<Job> jobs = new TreeSet<>(Comparators.JOB_CASE_INSENSITIVE_ORDER);

        Job ret = dsl.select(JOB.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .fetchOne()
                .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job.class);

        if (ret == null) {
            throwNotFound(orgName, repoName, branchName, Long.toString(jobId));
        }
        return ret;
    }

    public Map<Job, Map<Execution, Set<Stage>>> getJobExecutionsStages(String orgName, String repoName, String branchName, Long jobId) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .leftJoin(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        Map<Execution, Set<Stage>> executionStageMap = new ConcurrentSkipListMap<>(Comparators.EXECUTION_CASE_INSENSITIVE_ORDER);
        Job job = null;
        for (Record record : recordResult) {
            if (job == null) {
                job = record.into(Job.class);
            }
            Execution execution = record.into(Execution.class);
            Stage stage = record.into(Stage.class);

            if (!executionStageMap.containsKey(execution)) {
                synchronized (job.getJobInfoStr() + ":" + execution.getExecutionId()) {
                    if (!executionStageMap.containsKey(job)) {
                        executionStageMap.put(execution, new ConcurrentSkipListSet<>(Comparators.STAGE_CASE_INSENSITIVE_ORDER));
                    }
                }
            }
            executionStageMap.get(job).add(stage);
        }
        return Collections.singletonMap(job, executionStageMap);
    }

    public Map<Execution, Map<Stage, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult>>> getExecutionStagesTestResults(String orgName, String repoName, String branchName, Long jobId, Long executionId) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .leftJoin(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.EXECUTION_ID.eq(executionId)))
                .leftJoin(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID))
                .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        Map<Stage, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult>> stageTestResultMap = new TreeMap<>(Comparators.STAGE_CASE_INSENSITIVE_ORDER);
        Execution execution = null;
        for (Record record : recordResult) {
            if (execution == null) {
                execution = record.into(Execution.class);
            }
            Stage stage = record.into(Stage.class);
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult testResult = record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult.class);

            if (!stageTestResultMap.containsKey(stage)) {
                stageTestResultMap.put(stage, new TreeSet<>(Comparators.TEST_RESULT_CASE_INSENSITIVE_ORDER));
            }
            stageTestResultMap.get(stage).add(testResult);
        }
        return Collections.singletonMap(execution, stageTestResultMap);
    }

    public Execution getExecution(String orgName, String repoName, String branchName, Long jobId, Long executionId) {

        Execution ret = dsl.select(JOB.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.EXECUTION_ID.eq(executionId)))
                .fetchOne()
                .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class);

        if (ret == null) {
            throwNotFound(orgName, repoName, branchName, Long.toString(jobId), Long.toString(executionId));
        }
        return ret;
    }

    public Map<Branch, Map<Job, Set<Execution>>> getBranchJobsExecutionsForSha(String orgName, String repoName, String branchName, String sha) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.SHA.eq(sha)))
                .fetch();

        Map<Job, Set<Execution>> jobExecutionMap = new TreeMap<>(Comparators.JOB_CASE_INSENSITIVE_ORDER);

        Branch branch = null;
        for (Record record : recordResult) {
            if (branch == null) {
                branch = record.into(Branch.class);
            }
            Job job = record.into(Job.class);
            Execution execution = record.into(Execution.class);

            if (!jobExecutionMap.containsKey(job)) {
                synchronized (job.getJobInfoStr() + ":" + execution.getExecutionId()) {
                    if (!jobExecutionMap.containsKey(job)) {
                        jobExecutionMap.put(job, new TreeSet<>(Comparators.EXECUTION_CASE_INSENSITIVE_ORDER));
                    }
                }
            }
            jobExecutionMap.get(job).add(execution);
        }
        return Collections.singletonMap(branch, jobExecutionMap);
    }

    public Execution getExecutionFromReference(String orgName, String repoName, String branchName, String sha, String executionReference) {

        Execution ret = dsl.select(EXECUTION.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.SHA.eq(sha))
                        .and(EXECUTION.EXECUTION_REFERENCE.eq(executionReference)))
                .fetchOne()
                .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class);

        if (ret == null) {
            throwNotFound(orgName, repoName, branchName, sha, executionReference);
        }
        return ret;
    }

    public Map<Stage, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>>>
    getStageTestResultsTestSuites(String orgName, String repoName, String branchName, Long jobId, Long executionId, String stageName) {

        Result<Record> recordResult =
                dsl.select()
                        .from(ORG)
                        .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                                .and(ORG.ORG_NAME.eq(orgName))
                                .and(REPO.REPO_NAME.eq(repoName)))
                        .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                                .and(BRANCH.BRANCH_NAME.eq(branchName)))
                        .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                                .and(JOB.JOB_ID.eq(jobId)))
                        .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                                .and(EXECUTION.EXECUTION_ID.eq(executionId)))
                        .join(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID)
                                .and(STAGE.STAGE_NAME.eq(stageName)))
                        .fetch();

        Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>> testResultSuiteMap =
                new TreeMap<>(Comparators.TEST_RESULT_CASE_INSENSITIVE_ORDER);

        Stage stage = null;
        for (Record record : recordResult) {
            if (stage == null) {
                stage = record.into(Stage.class);
            }
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult testResult = record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult.class);
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite testSuite = record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite.class);

            if (!testResultSuiteMap.containsKey(stage)) {

                testResultSuiteMap.put(testResult, new TreeSet<>(Comparators.TEST_SUITE_CASE_INSENSITIVE_ORDER));

            }
            testResultSuiteMap.get(testResult).add(testSuite);
        }
        return Collections.singletonMap(stage, testResultSuiteMap);

    }

    public Stage getStage(String orgName, String repoName, String branchName, String sha, String executionReference, String stageName) {
        Stage ret = dsl.select(STAGE.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.SHA.eq(sha))
                        .and(EXECUTION.EXECUTION_REFERENCE.eq(executionReference)))
                .join(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID)
                        .and(STAGE.STAGE_NAME.eq(stageName)))
                .fetchOne()
                .into(Stage.class);
        if (ret == null) {
            throwNotFound(orgName, repoName, branchName, sha, executionReference, stageName);
        }
        return ret;
    }

//    public Map<Stage, Set<TestResult>> getStagesTestResults(String orgName, String repoName, String branchName, String shaString, String executionReference) {
//        Set<Stage> stages = getStages(orgName, repoName, branchName, shaString, executionReference);
//        Map<Stage, Set<TestResult>> stageTestResultsMap = new TreeMap<>(Comparators.STAGE_CASE_INSENSITIVE_ORDER);
//        stages.parallelStream().forEach(stage -> {
//            stageTestResultsMap.put(stage, getTestResults(stage.getStageId()));
//        });
//        return stageTestResultsMap;
//    }
//
//    public Map<Stage, Set<TestResult>> getStageTestResults(String orgName, String repoName, String branchName, String shaString, String executionExternalId, String stageName) {
//        Stage stage = getStage(orgName, repoName, branchName, shaString, executionExternalId, stageName);
//        return Collections.singletonMap(stage, getTestResults(stage.getStageId()));
//    }

    protected void throwNotFound(String... args) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                getNotFoundMessage(args));
    }

    //TODO: refactor to take tuple of variable name and value
    protected static String getNotFoundMessage(String... args) {
        return "Unable to find " + String.join(" <- ", args);
    }

    public ExecutionStagePath getExecutionStagePath(ReportMetaData request) {

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
                .leftJoin(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.SHA.eq(request.getSha()))
                        .and(EXECUTION.EXECUTION_REFERENCE.eq(request.getExecutionReference())))
                .leftJoin(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID)
                        .and(STAGE.STAGE_NAME.eq(request.getStage())))
                .where(ORG.ORG_NAME.eq(request.getOrg()));

        Record record = selectConditionStep
                .fetchOne();

        ExecutionStagePath executionStagePath = new ExecutionStagePath();
        if (record == null) {
            return executionStagePath;
        }

        {
            Org _org = null;
            Repo _repo = null;
            Branch _branch = null;
            Job _context = null;
            Execution _execution = null;
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
                _context = record.into(JobRecord.class).into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job.class);
            }
            if (record.get(EXECUTION.EXECUTION_ID.getName()) != null) {
                _execution = record.into(ExecutionRecord.class).into(Execution.class);
            }
            if (record.get(STAGE.STAGE_ID.getName()) != null) {
                _stage = record.into(StageRecord.class).into(Stage.class);
            }

            executionStagePath.setOrg(_org);
            executionStagePath.setRepo(_repo);
            executionStagePath.setBranch(_branch);
            executionStagePath.setJob(_context);
            executionStagePath.setExecution(_execution);
            executionStagePath.setStage(_stage);

        }
        return executionStagePath;
    }

    /**
     * @param request a BuildStagePathRequest with the fields to match on
     * @return the ExecutionStagePath for the provided report metadata.
     */
    public ExecutionStagePath getOrInsertExecutionStagePath(ReportMetaData request) {
        return getOrInsertExecutionStagePath(request, null);
    }

    /*
     * TODO: add test to simulate race condition on insert where buildstage path is missing data from db to ensure that
     *     1) insert failure is ignored/skipped and
     *     2) the existing data is returned
     */

    /**
     * prefer public method. The ability to inject an executionStagePath is for testing purposes.
     *
     * @param request            a ReportMetaData
     * @param executionStagePath normally null, only values passed for testing
     * @return the ExecutionStagePath for the provided report metadata.
     */
    ExecutionStagePath getOrInsertExecutionStagePath(ReportMetaData request, ExecutionStagePath executionStagePath) {

        request.validateAndSetDefaults();

        if (executionStagePath == null) {
            executionStagePath = getExecutionStagePath(request);
        }

        if (executionStagePath.getOrg() == null) {
            Org org = new Org()
                    .setOrgName(request.getOrg());
            orgDao.insert(org);
            executionStagePath.setOrg(org);
        }

        if (executionStagePath.getRepo() == null) {
            Repo repo = new Repo()
                    .setRepoName(request.getRepo())
                    .setOrgFk(executionStagePath.getOrg().getOrgId());
            repoDao.insert(repo);
            executionStagePath.setRepo(repo);
        }

        if (executionStagePath.getBranch() == null) {
            Branch branch = new Branch()
                    .setBranchName(request.getBranch())
                    .setRepoFk(executionStagePath.getRepo().getRepoId());
            branchDao.insert(branch);
            executionStagePath.setBranch(branch);
        }

        if (executionStagePath.getJob() == null) {
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job job = new io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job()
                    .setJobInfo(request.getJobInfoJson())
                    .setBranchFk(executionStagePath.getBranch().getBranchId());
            //jobDao.insert(job);
            //user insert since DAO/POJO would incorrectly attempt to insert generated column job_info_str
            Job insertedJob = dsl.insertInto(JOB, JOB.BRANCH_FK, JOB.JOB_INFO)
                    .values(executionStagePath.getBranch().getBranchId(), request.getJobInfoJson())
                    .returningResult(JOB.JOB_ID, JOB.JOB_INFO, JOB.BRANCH_FK, JOB.JOB_INFO_STR)
                    .fetchOne().into(Job.class);

            executionStagePath.setJob(insertedJob);
        }

        if (executionStagePath.getExecution() == null) {
            Execution execution = new Execution()
                    .setExecutionReference(request.getExecutionReference())
                    .setSha(request.getSha())
                    .setJobFk(executionStagePath.getJob().getJobId());
            executionDao.insert(execution);
            executionStagePath.setExecution(execution);
        }

        if (executionStagePath.getStage() == null) {
            Stage stage = new Stage()
                    .setStageName(request.getStage())
                    .setExecutionFk(executionStagePath.getExecution().getExecutionId());
            stageDao.insert(stage);
            executionStagePath.setStage(stage);
        }
        return executionStagePath;
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

        Set<TestResult> testResults = new TreeSet<>(Comparators.TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER);
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