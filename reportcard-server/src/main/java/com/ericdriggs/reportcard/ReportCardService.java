package com.ericdriggs.reportcard;

import com.ericdriggs.reportcard.gen.db.tables.daos.*;
import com.ericdriggs.reportcard.gen.db.tables.pojos.*;
import com.ericdriggs.reportcard.gen.db.tables.records.*;
import com.ericdriggs.reportcard.model.TestCase;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.model.TestSuite;
import com.ericdriggs.reportcard.model.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.ericdriggs.reportcard.gen.db.Tables.*;

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
    final ShaDao shaDao;
    final ContextDao contextDao;
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
        shaDao = new ShaDao(dsl.configuration());
        contextDao = new ContextDao(dsl.configuration());
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

    public Org getOrg(String org) {
        Record record = dsl.select().from(ORG)
                .where(ORG.ORG_NAME.eq(org))
                .fetchOne();
        if (record == null) {
            throwNotFound(org);
        }
        return record.into(Org.class);
    }

    public Set<Repo> getRepos(String org) {
        Set<Repo> repos= new TreeSet<>(Comparators.REPO_CASE_INSENSITIVE_ORDER);
        repos.addAll( dsl.
                select(REPO.fields())
                .from(REPO.join(ORG)
                        .on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .fetch()
                .into(com.ericdriggs.reportcard.gen.db.tables.pojos.Repo.class));
        return repos;
    }

    public Map<Org, Set<Repo>> getOrgsRepos() {

        Map<Org, Set<Repo>> orgRepoMap = new ConcurrentSkipListMap<>(Comparators.ORG_CASE_INSENSITIVE_ORDER);
        Set<Org> orgs = getOrgs();
        orgs.stream().forEach(org -> {
            orgRepoMap.put(org, getRepos(org.getOrgName()));
        });
        return orgRepoMap;
    }

    public Map<Org, Set<Repo>> getOrgRepos(String orgName) {
        Org org = getOrg(orgName);
        Set<Repo> repos = getRepos(orgName);
        return Collections.singletonMap(org, repos);
    }

    public com.ericdriggs.reportcard.gen.db.tables.pojos.Repo getRepo(String org, String repo) {
        com.ericdriggs.reportcard.gen.db.tables.pojos.Repo ret = dsl.
                select(REPO.fields()).from(REPO)
                .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetchOne()
                .into(com.ericdriggs.reportcard.gen.db.tables.pojos.Repo.class);
        if (ret == null) {
            throwNotFound(org, repo);
        }
        return ret;
    }

    public Map<Repo, Set<Branch>> getRepoBranches(String org) {
        Set<Repo> repos = getRepos(org);
        Map<Repo,Set<Branch>> repoBranchMap= new ConcurrentSkipListMap<>(Comparators.REPO_CASE_INSENSITIVE_ORDER);
        repos.stream().forEach( repo-> {
            repoBranchMap.put(repo, getBranches(org, repo.getRepoName()));
        });
        return repoBranchMap;
    }


    public Set<Branch> getBranches(String org, String repo) {
        Set<Branch> branches= new TreeSet<>(Comparators.BRANCH_CASE_INSENSITIVE_ORDER);
        branches.addAll( dsl.
                select(BRANCH.fields())
                .from(BRANCH
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .fetch()
                .into(Branch.class));
        return branches;
    }

    public Branch getBranch(String org, String repo, String branch) {
        Branch ret = dsl.
                select(BRANCH.fields())
                .from(BRANCH
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetchOne()
                .into(Branch.class);
        if (ret == null) {
            throwNotFound(org, repo, branch);
        }
        return ret;
    }

    public Map<Branch,Set<Sha>> getBranchShas(String org, String repo) {
        Set<Branch> branches = getBranches(org, repo);
        Map<Branch,Set<Sha>> branchShaMap = new ConcurrentSkipListMap<>(Comparators.BRANCH_CASE_INSENSITIVE_ORDER);
        branches.stream().forEach(branch ->
                        branchShaMap.put(branch, getShas(org, repo, branch.getBranchName()))
                );
        return branchShaMap;
    }

    public Set<Sha> getShas(String org, String repo, String branch) {
        Set<Sha> shas = new TreeSet<>(Comparators.SHA);
        shas.addAll(dsl.
                select(SHA.fields())
                .from(SHA
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .fetch()
                .into(Sha.class));
        return shas;
    }

    public Sha getSha(String org, String repo, String branch, String sha) {
        Sha ret = dsl.
                select(SHA.fields())
                .from(SHA
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(SHA.SHA_.eq(sha))
                .fetchOne()
                .into(Sha.class);
        if (ret == null) {
            throwNotFound(org, repo, branch, sha);
        }
        return ret;
    }

    public List<Context> getContexts(String org, String repo, String branch, String sha) {
        return dsl.
                select(CONTEXT.fields())
                .from(CONTEXT
                        .join(SHA).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(SHA.SHA_.eq(sha))
                .fetch()
                .into(Context.class);
    }

    protected static void addContextConditions(SelectConditionStep<Record> selectConditionStep, HostApplicationPipeline hostApplicatiionPipeline) {

        selectConditionStep.and(CONTEXT.HOST.eq(hostApplicatiionPipeline.getHost()));
        if (hostApplicatiionPipeline.getApplication() == null) {
            selectConditionStep.and(CONTEXT.APPLICATION.isNull());
        } else {
            selectConditionStep.and(CONTEXT.APPLICATION.eq(hostApplicatiionPipeline.getApplication()));
        }

        if (hostApplicatiionPipeline.getPipeline() == null) {
            selectConditionStep.and(CONTEXT.PIPELINE.isNull());
        } else {
            selectConditionStep.and(CONTEXT.PIPELINE.eq(hostApplicatiionPipeline.getPipeline()));
        }
    }

    public Context getContext(String org, String repo, String branch, String sha, HostApplicationPipeline hostApplicatiionPipeline) {
        SelectConditionStep<Record> selectConditionStep = dsl.
                select(CONTEXT.fields())
                .from(CONTEXT
                        .join(SHA).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(SHA.SHA_.eq(sha));

        addContextConditions(selectConditionStep, hostApplicatiionPipeline);

        Context ret =
                selectConditionStep
                .fetchOne()
                .into(Context.class);
        if (ret == null) {
            throwNotFound(org, repo, branch, sha, hostApplicatiionPipeline.toString());
        }
        return ret;
    }


    public List<Execution> getExecutions(String org, String repo, String branch, String sha, HostApplicationPipeline hostApplicatiionPipeline) {
        SelectConditionStep<Record> selectConditionStep =  dsl.
                select(EXECUTION.fields())
                .from(EXECUTION
                        .join(CONTEXT).on(EXECUTION.CONTEXT_FK.eq(CONTEXT.CONTEXT_ID))
                        .join(SHA).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(SHA.SHA_.eq(sha));

        addContextConditions(selectConditionStep, hostApplicatiionPipeline);
        return selectConditionStep
                .fetch()
                .into(Execution.class);
    }

    public Execution getExecution(String org, String repo, String branch, String sha, HostApplicationPipeline hostApplicatiionPipeline, String executionExternalId) {
        SelectConditionStep<Record> selectConditionStep = dsl.
                select(EXECUTION.fields())
                .from(EXECUTION
                        .join(CONTEXT).on(EXECUTION.CONTEXT_FK.eq(CONTEXT.CONTEXT_ID))
                        .join(SHA).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(SHA.SHA_.eq(sha));

        addContextConditions(selectConditionStep, hostApplicatiionPipeline);
        selectConditionStep.and(EXECUTION.EXECUTION_EXTERNAL_ID.eq(executionExternalId));

        Execution ret =
                selectConditionStep
                        .fetchOne()
                        .into(Execution.class);
        if (ret == null) {
            throwNotFound(org, repo, branch, sha, hostApplicatiionPipeline.toString(), executionExternalId);
        }
        return ret;
    }

    public List<Stage> getStages(String org, String repo, String branch, String sha, HostApplicationPipeline hostApplicatiionPipeline, String executionExternalId) {
        SelectConditionStep<Record> selectConditionStep =  dsl.
                select(STAGE.fields())
                .from(STAGE
                        .join(EXECUTION).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID))
                        .join(CONTEXT).on(EXECUTION.CONTEXT_FK.eq(CONTEXT.CONTEXT_ID))
                        .join(SHA).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(SHA.SHA_.eq(sha));

        addContextConditions(selectConditionStep, hostApplicatiionPipeline);
        selectConditionStep.and(EXECUTION.EXECUTION_EXTERNAL_ID.eq(executionExternalId));
        return selectConditionStep
                .fetch()
                .into(Stage.class);
    }

    public Stage getStage(String org, String repo, String branch, String sha, HostApplicationPipeline hostApplicatiionPipeline, String executionExternalId, String stage) {
        SelectConditionStep<Record> selectConditionStep = dsl.
                select(STAGE.fields())
                .from(STAGE
                        .join(EXECUTION).on(STAGE.EXECUTION_FK.eq(EXECUTION.EXECUTION_ID))
                        .join(CONTEXT).on(EXECUTION.CONTEXT_FK.eq(CONTEXT.CONTEXT_ID))
                        .join(SHA).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .join(BRANCH).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                        .join(REPO).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .join(ORG).on(REPO.ORG_FK.eq(ORG.ORG_ID)))
                .where(ORG.ORG_NAME.eq(org))
                .and(REPO.REPO_NAME.eq(repo))
                .and(BRANCH.BRANCH_NAME.eq(branch))
                .and(SHA.SHA_.eq(sha));

        addContextConditions(selectConditionStep, hostApplicatiionPipeline);
        selectConditionStep.and(EXECUTION.EXECUTION_EXTERNAL_ID.eq(executionExternalId))
                .and(STAGE.STAGE_NAME.eq(stage));

        Stage ret =
                selectConditionStep
                        .fetchOne()
                        .into(Stage.class);
        if (ret == null) {
            throwNotFound(org, repo, branch, sha, hostApplicatiionPipeline.toString(), executionExternalId, stage);
        }
        return ret;
    }

    protected void throwNotFound(String... args) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                getNotFoundMessage(args));
    }

    protected static String getNotFoundMessage(String... args) {
        return "Unable to find " + String.join(" <- ", args);
    }



    public ExecutionStagePath getExecutionStagePath(ReportMetaData request) {

        //String org, String repo, String app, String branch, Integer buildOrdinal, String stage

        SelectConditionStep<Record> selectConditionStep = dsl.
                select()
                .from(ORG
                        .leftJoin(REPO).on(ORG.ORG_ID.eq(REPO.ORG_FK)).and(REPO.REPO_NAME.eq(request.getRepo()))
                        .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)).and(BRANCH.BRANCH_NAME.eq(request.getBranch()))
                        .leftJoin(SHA).on(SHA.BRANCH_FK.eq(BRANCH.BRANCH_ID)).and(SHA.SHA_.eq(request.getSha()))
                        .leftJoin(CONTEXT).on(CONTEXT.SHA_FK.eq(SHA.SHA_ID))
                        .leftJoin(EXECUTION).on(EXECUTION.CONTEXT_FK.eq(CONTEXT.CONTEXT_ID)).and(EXECUTION.EXECUTION_EXTERNAL_ID.eq(request.getExternalExecutionId()))
                        .leftJoin(STAGE).on(STAGE.STAGE_NAME.eq(request.getStage())).and(STAGE.STAGE_NAME.eq(request.getStage()))
                ).where(ORG.ORG_NAME.eq(request.getOrg()));
        addContextConditions(selectConditionStep, request.getHostApplicatiionPipeline());
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
            Sha _sha = null;
            Context _context = null;
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
            if (record.get(SHA.SHA_ID.getName()) != null) {
                _sha = record.into(ShaRecord.class).into(Sha.class);
            }
            if (record.get(CONTEXT.CONTEXT_ID.getName()) != null) {
                _context = record.into(ContextRecord.class).into(Context.class);
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
            executionStagePath.setSha(_sha);
            executionStagePath.setContext(_context);
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
     * @param request        a ReportMetaData
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
            com.ericdriggs.reportcard.gen.db.tables.pojos.Repo repo = new com.ericdriggs.reportcard.gen.db.tables.pojos.Repo()
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

        if (executionStagePath.getSha() == null) {
            Sha sha = new Sha()
                    .setSha(request.getSha())
                    .setBranchFk(executionStagePath.getBranch().getBranchId());
            shaDao.insert(sha);
            executionStagePath.setSha(sha);
        }

        if (executionStagePath.getContext() == null) {
            Context context = new Context()
                    .setHost(request.getHostApplicatiionPipeline().getHost())
                    .setApplication(request.getHostApplicatiionPipeline().getApplication())
                    .setPipeline(request.getHostApplicatiionPipeline().getPipeline())
                    .setShaFk(executionStagePath.getSha().getShaId());
            contextDao.insert(context);
            executionStagePath.setContext(context);
        }

        if (executionStagePath.getExecution() == null) {
            Execution execution = new Execution()
                    .setExecutionExternalId(request.getExternalExecutionId())
                    .setContextFk(executionStagePath.getContext().getContextId());
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

    //TODO: refactor to use getTestResult
    public List<TestResult> getTestResults(Long stageId) {

        List<TestResult> testResults = dsl.
                select(TEST_RESULT.fields())
                .from(STAGE
                        .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                ).where(STAGE.STAGE_ID.eq(stageId))
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