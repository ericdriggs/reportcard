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

import java.math.BigInteger;
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

    public Set<Repo> getRepos(String orgName) {
        Set<Repo> repos = new TreeSet<>(Comparators.REPO_CASE_INSENSITIVE_ORDER);
        repos.addAll(dsl.
                select(REPO.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .fetch()
                .into(Repo.class));
        return repos;
    }

    public Map<Org, Set<Repo>> getOrgsRepos() {

        Map<Org, Set<Repo>> orgRepoMap = new ConcurrentSkipListMap<>(Comparators.ORG_CASE_INSENSITIVE_ORDER);
        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .fetch();

        for (Record record : recordResult) {
            Org org = record.into(Org.class);
            Repo repo = record.into(Repo.class);

            if (!orgRepoMap.containsKey(org)) {
                synchronized (org.getOrgName() + ":" + repo.getRepoName()) {
                    if (!orgRepoMap.containsKey(org)) {
                        orgRepoMap.put(org, new TreeSet<>(Comparators.REPO_CASE_INSENSITIVE_ORDER));
                    }
                }
            }
            orgRepoMap.get(org).add(repo);
        }
        return orgRepoMap;
    }

    public Map<Org, Set<Repo>> getOrgRepos(String orgName) {
        Org org = getOrg(orgName);
        Set<Repo> repos = getRepos(orgName);
        return Collections.singletonMap(org, repos);
    }

    public Repo getRepo(String orgName, String repoName) {
        Repo ret = dsl.
                select(REPO.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .fetchOne()
                .into(Repo.class);
        if (ret == null) {
            throwNotFound(orgName, repoName);
        }
        return ret;
    }

    //TODO: refactor to use single select
    public Map<Repo, Set<Branch>> getReposBranches(String orgName) {
        Set<Repo> repos = getRepos(orgName);
        Map<Repo, Set<Branch>> repoBranchMap = new ConcurrentSkipListMap<>(Comparators.REPO_CASE_INSENSITIVE_ORDER);
        repos.parallelStream().forEach(repo -> {
            repoBranchMap.put(repo, getBranches(orgName, repo.getRepoName()));
        });
        return repoBranchMap;
    }

    public Map<Repo, Set<Branch>> getRepoBranches(String orgName, String repoName) {
        Repo repo = getRepo(orgName, repoName);
        return Collections.singletonMap(repo, getBranches(orgName, repoName));
    }

    public Set<Branch> getBranches(String orgName, String repoName) {
        Set<Branch> branches = new TreeSet<>(Comparators.BRANCH_CASE_INSENSITIVE_ORDER);
        branches.addAll(dsl.
                select(BRANCH.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .fetch()
                .into(Branch.class));
        return branches;
    }

    public Branch getBranch(String orgName, String repoName, String branchName) {
        Branch ret = getBranchSelect(orgName, repoName, branchName, BRANCH.fields())
                .fetchOne()
                .into(Branch.class);
        if (ret == null) {
            throwNotFound(orgName, repoName, branchName);
        }
        return ret;
    }

    protected SelectOnConditionStep<Record> getBranchSelect(String orgName, String repoName, String branchName, SelectFieldOrAsterisk... fields) {
        return  dsl.select(fields)
                .from(BRANCH)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)));
    }

    protected SelectOnConditionStep<Record> getJobsSelect(String orgName, String repoName, String branchName, SelectFieldOrAsterisk... fields) {
        return dsl.select(fields)
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID));
    }

    public Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> getJobs(String orgName, String repoName, String branchName, Map<String,String> jobInfoFilters) {
        Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> jobs = new TreeSet<>(Comparators.JOB_CASE_INSENSITIVE_ORDER);
        jobs.addAll(
                getJobsSelect(orgName, repoName, branchName, JOB.fields()).fetch()
                        .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job.class));

        //TODO: filter json based on metadata
        return jobs;
    }

    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job getJob(String orgName, String repoName, String branchName, Long jobId) {
        Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job> jobs = new TreeSet<>(Comparators.JOB_CASE_INSENSITIVE_ORDER);

        io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job ret = dsl.select(JOB.fields())
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

    public Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution> getExecutions(String orgName, String repoName, String branchName, Long jobId, Map<String,String> jobInfoFilters) {
        Set<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution> executions = new TreeSet<>(Comparators.EXECUTION_CASE_INSENSITIVE_ORDER);
        //TODO: use filters
        executions.addAll(
                dsl.select(JOB.fields())
                        .from(ORG)
                        .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                                .and(ORG.ORG_NAME.eq(orgName))
                                .and(REPO.REPO_NAME.eq(repoName)))
                        .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                                .and(BRANCH.BRANCH_NAME.eq(branchName)))
                        .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                                .and(JOB.JOB_ID.eq(jobId)))
                        .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID))
                        .fetch()
                        .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class)
        );
        return executions;
    }

    public Set<Execution> getExecutionsForSha(String orgName, String repoName, String branchName, String sha, Map<String,String> jobInfoFilters) {
        Set<Execution> executions = new TreeSet<>(Comparators.EXECUTION_CASE_INSENSITIVE_ORDER);
        //TODO: use filters
        executions.addAll(
                dsl.select(JOB.fields())
                        .from(ORG)
                        .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                                .and(ORG.ORG_NAME.eq(orgName))
                                .and(REPO.REPO_NAME.eq(repoName)))
                        .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                                .and(BRANCH.BRANCH_NAME.eq(branchName)))
                        .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                                .and(EXECUTION.SHA.eq(sha)))
                        .fetch()
                        .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class)
        );
        return executions;
    }

    public Execution getExecutionFromReference(String orgName, String repoName, String branchName, String sha, String executionReference) {

        Execution ret = dsl.select(JOB.fields())
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

    public Execution getExecution(String orgName, String repoName, String branchName, Long jobId, Long executionId) {

        Execution ret = dsl.select(JOB.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and (JOB.JOB_ID.eq(jobId)))
                .join(EXECUTION).on(EXECUTION.JOB_FK.eq(JOB.JOB_ID)
                        .and(EXECUTION.EXECUTION_ID.eq(executionId)))
                .fetchOne()
                .into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution.class);

        if (ret == null) {
            throwNotFound(orgName, repoName, branchName, Long.toString(jobId), Long.toString(executionId));
        }
        return ret;
    }

//    public Map<Execution, Set<Stage>> getExecutionsStages(String orgName, String repoName, String branchName, String shaString, Map<String, String> metadataFilters) {
//        Set<Execution> contexts = getExecutions(orgName, repoName, branchName, shaString);
//        Map<Execution, Set<Stage>> stageExecutionMap = new ConcurrentSkipListMap<>(Comparators.EXECUTION_CASE_INSENSITIVE_ORDER);
//        contexts.parallelStream().forEach(execution -> {
//            stageExecutionMap.put(execution, getStages(orgName, repoName, branchName, shaString, execution.getExecutionReference()));
//        });
//        return stageExecutionMap;
//    }
//
//    public Map<Execution, Set<Stage>> getExecutionStages(String orgName, String repoName, String branchName, String shaString, String executionExternalId) {
//        Execution execution = getExecution(orgName, repoName, branchName, shaString, executionExternalId);
//        return Collections.singletonMap(execution, getStages(orgName, repoName, branchName, shaString, executionExternalId));
//    }

    public Set<Stage> getStages(String orgName, String repoName, String branchName, String sha, String executionReference) {

        Set<Stage> stages = new TreeSet<>(Comparators.STAGE_CASE_INSENSITIVE_ORDER);
        stages.addAll(
                dsl.select(STAGE.fields())
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
                        .join(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID))
                        .fetch()
                        .into(Stage.class)
        );

        return stages;
    }

    public Set<Stage> getStagesFromIds(String orgName, String repoName, String branchName, Long jobId, Long executionId) {

        Set<Stage> stages = new TreeSet<>(Comparators.STAGE_CASE_INSENSITIVE_ORDER);
        stages.addAll(
                dsl.select(STAGE.fields())
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
                        .join(STAGE).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID))
                        .fetch()
                        .into(Stage.class)
        );
        return stages;
    }

    public Stage getStageFromIds(String orgName, String repoName, String branchName, Long jobId, Long executionId, String stageName) {

        Stage ret =
                dsl.select(STAGE.fields())
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
                        .fetchOne()
                        .into(Stage.class);

        if (ret == null) {
            throwNotFound(orgName, repoName, branchName, Long.toString(jobId), Long.toString(executionId), stageName);
        }
        return ret;
    }

    public Stage getStage(String orgName, String repoName, String branchName, String sha, String executionReference, String stageName) {
        Stage ret =  dsl.select(STAGE.fields())
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

    public Map<Stage, Set<TestResult>> getStagesTestResults(String orgName, String repoName, String branchName, String shaString, String executionReference) {
        Set<Stage> stages = getStages(orgName, repoName, branchName, shaString, executionReference);
        Map<Stage, Set<TestResult>> stageTestResultsMap = new ConcurrentSkipListMap<>(Comparators.STAGE_CASE_INSENSITIVE_ORDER);
        stages.parallelStream().forEach(stage -> {
            stageTestResultsMap.put(stage, getTestResults(stage.getStageId()));
        });
        return stageTestResultsMap;
    }

    public Map<Stage, Set<TestResult>> getStageTestResults(String orgName, String repoName, String branchName, String shaString, String executionExternalId, String stageName) {
        Stage stage = getStage(orgName, repoName, branchName, shaString, executionExternalId, stageName);
        return Collections.singletonMap(stage, getTestResults(stage.getStageId()));
    }

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

        Set<TestResult> testResults = new TreeSet<>(Comparators.TEST_RESULT_CASE_INSENSITIVE_ORDER);
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