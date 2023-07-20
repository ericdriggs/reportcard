package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;

import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.NoDataFoundException;
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
public class BrowseService extends AbstractPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BrowseService(DSLContext dsl) {
        super(dsl);

    }

    public Set<Org> getOrgs() {
        Set<Org> orgs = new TreeSet<>(PojoComparators.ORG_CASE_INSENSITIVE_ORDER);
        orgs.addAll(dsl.select().from(ORG).fetch().into(Org.class));
        return orgs;
    }

    public Map<Org, Set<Repo>> getOrgsRepos() {

        Result<Record> recordResult = dsl.select().from(ORG).leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)).fetch();

        Map<Org, Set<Repo>> orgRepoMap = new TreeMap<>(PojoComparators.ORG_CASE_INSENSITIVE_ORDER);
        for (Record record : recordResult) {
            Org org = record.into(Org.class);
            Repo repo = record.into(Repo.class);

            if (!orgRepoMap.containsKey(org)) {
                orgRepoMap.put(org, new TreeSet<>(PojoComparators.REPO_CASE_INSENSITIVE_ORDER));
            }
            orgRepoMap.get(org).add(repo);
        }
        return orgRepoMap;
    }

    public Org getOrg(String orgName) {
        Record record = dsl.select(ORG.fields()).from(ORG).where(ORG.ORG_NAME.eq(orgName)).fetchOne();
        if (record == null) {
            throwNotFound("org:" + orgName);
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

        Map<Repo, Set<Branch>> repoBranchMap = new TreeMap<>(PojoComparators.REPO_CASE_INSENSITIVE_ORDER);
        Org org = null;
        for (Record record : recordResult) {
            if (org == null) {
                org = record.into(Org.class);
            }
            Repo repo = record.into(Repo.class);
            Branch branch = record.into(Branch.class);

            if (!repoBranchMap.containsKey(repo)) {
                repoBranchMap.put(repo, new TreeSet<>(PojoComparators.BRANCH_CASE_INSENSITIVE_ORDER));
            }
            repoBranchMap.get(repo).add(branch);
        }
        return Collections.singletonMap(org, repoBranchMap);
    }

    public Repo getRepo(String orgName, String repoName) {

        try {
            return dsl.
                    select(REPO.fields())
                    .from(ORG)
                    .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                            .and(REPO.REPO_NAME.eq(repoName)))
                    .where(ORG.ORG_NAME.eq(orgName))
                    .fetchSingle()
                    .into(Repo.class);
        } catch (NoDataFoundException ex) {
            throwNotFound("org: " + orgName, "repo: " + repoName);
        }
        throw new IllegalStateException("unreachable code");
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

        Map<Branch, Set<Job>> branchesJobsMap = new TreeMap<>(PojoComparators.BRANCH_CASE_INSENSITIVE_ORDER);
        Repo repo = null;
        for (Record record : recordResult) {
            if (repo == null) {
                repo = record.into(Repo.class);
            }
            Branch branch = record.into(Branch.class);
            Job job = record.into(Job.class);

            if (!branchesJobsMap.containsKey(branch)) {
                branchesJobsMap.put(branch, new TreeSet<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER));
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
            throwNotFound("org: " + orgName, "repo: " + repoName, "branch: " + branchName);
        }
        return ret;
    }

    public Map<Branch, Map<Job, Set<Run>>> getBranchJobsRuns(String orgName, String repoName, String branchName, Map<String, String> expectedJobFilters) {

        if (expectedJobFilters == null) {
            expectedJobFilters = Collections.emptyMap();
        }

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        //TODO: filter jobs
        Map<Job, Set<Run>> jobRunMap = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);
        Branch branch = null;
        for (Record record : recordResult) {
            if (branch == null) {
                branch = record.into(Branch.class);
            }
            Job job = record.into(Job.class);
            if (!JsonCompare.containsMap(expectedJobFilters, job.getJobInfo())){
                continue;
            }
            Run run = record.into(Run.class);

            if (!jobRunMap.containsKey(job)) {
                jobRunMap.put(job, new TreeSet<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER));
            }
            jobRunMap.get(job).add(run);
        }
        return Collections.singletonMap(branch, jobRunMap);
    }

    public Job getJob(String orgName, String repoName, String branchName, Map<String, String> jobInfo) {
        Set<Job> jobs = new TreeSet<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);

        Result<Record> jobsResult = dsl.select(JOB.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .fetch();

        for (Record record: jobsResult) {
            Job job = record.into(Job.class);
            if (JsonCompare.equalsMap(job.getJobInfo(), jobInfo)) {
                return job;
            }
        }
        throwNotFound("org: " + orgName, "repo: " + repoName, "branch: " + branchName, "jobInfo: " + jobInfo);
        throw new IllegalStateException("unreachable code");
    }

    public Map<Job, Map<Run, Set<Stage>>> getJobRunsStages(String orgName, String repoName, String branchName, Long jobId) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId))
                )
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        Map<Run, Set<Stage>> runStageMap = new ConcurrentSkipListMap<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER);
        Job job = null;
        for (Record record : recordResult) {
            if (job == null) {
                job = record.into(Job.class);
            }
            Run run = record.into(Run.class);
            Stage stage = record.into(Stage.class);

            if (!runStageMap.containsKey(run)) {
                runStageMap.put(run, new TreeSet<>(PojoComparators.STAGE_CASE_INSENSITIVE_ORDER));
            }
            runStageMap.get(run).add(stage);
        }
        return Collections.singletonMap(job, runStageMap);
    }

    public Map<Run, Map<Stage, Set<TestResult>>> getRunStagesTestResults(String orgName, String repoName, String branchName, Long jobId, Long runId) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.RUN_ID.eq(runId)))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                .where(ORG.ORG_NAME.eq(orgName))
                .fetch();

        Map<Stage, Set<TestResult>> stageTestResultMap = new TreeMap<>(PojoComparators.STAGE_CASE_INSENSITIVE_ORDER);
        Run run = null;
        for (Record record : recordResult) {
            if (run == null) {
                run = record.into(Run.class);
            }
            Stage stage = record.into(Stage.class);
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult testResult = record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult.class);

            if (!stageTestResultMap.containsKey(stage)) {
                stageTestResultMap.put(stage, new TreeSet<>(PojoComparators.TEST_RESULT_CASE_INSENSITIVE_ORDER));
            }
            stageTestResultMap.get(stage).add(testResult);
        }
        return Collections.singletonMap(run, stageTestResultMap);
    }

    public Run getRun(String orgName, String repoName, String branchName, Long jobId, Long runId) {

        Run ret = dsl.select(RUN.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.RUN_ID.eq(runId)))
                .fetchOne()
                .into(Run.class);

        if (ret == null) {
            throwNotFound("org: " + orgName, "repo: " + repoName, "branch: " + branchName, "jobId: " + Long.toString(jobId), "runId: " + Long.toString(runId));
        }
        return ret;
    }

    public Map<Branch, Map<Job, Set<Run>>> getBranchJobsRunsForSha(String orgName, String repoName, String branchName, String sha) {

        Result<Record> recordResult = dsl.select()
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(sha)))
                .fetch();

        Map<Job, Set<Run>> jobRunMap = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);

        Branch branch = null;
        for (Record record : recordResult) {
            if (branch == null) {
                branch = record.into(Branch.class);
            }
            Job job = record.into(Job.class);
            Run run = record.into(Run.class);

            if (!jobRunMap.containsKey(job)) {
                synchronized (job.getJobInfoStr() + ":" + run.getRunId()) {
                    if (!jobRunMap.containsKey(job)) {
                        jobRunMap.put(job, new TreeSet<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER));
                    }
                }
            }
            jobRunMap.get(job).add(run);
        }
        return Collections.singletonMap(branch, jobRunMap);
    }

    public Run getRunFromReference(String orgName, String repoName, String branchName, String sha, String runReference) {

        Run ret = dsl.select(RUN.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(sha))
                        .and(RUN.RUN_REFERENCE.eq(runReference)))
                .fetchOne()
                .into(Run.class);

        if (ret == null) {
            throwNotFound("org: " + orgName, "repo: " + repoName, "branch: " + branchName, "sha:" + sha, "runReference: " + runReference);
        }
        return ret;
    }

    public Map<Stage, Map<TestResult, Set<TestSuite>>> getStageTestResultsTestSuites(String orgName, String repoName, String branchName, Long jobId, Long runId, String stageName) {

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
                        .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                                .and(RUN.RUN_ID.eq(runId)))
                        .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                                .and(STAGE.STAGE_NAME.eq(stageName)))
                        //TODO: left join?
                        .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                        .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                        .fetch();

        Map<TestResult, Set<TestSuite>> testResultSuiteMap = new TreeMap<>(PojoComparators.TEST_RESULT_CASE_INSENSITIVE_ORDER);

        Stage stage = null;
        for (Record record : recordResult) {
            if (stage == null) {
                stage = record.into(Stage.class);
            }
            TestResult testResult = getTestResultFromRecord(record);
            TestSuite testSuite = getTestSuiteFromRecord(record);

            if (!testResultSuiteMap.containsKey(testResult)) {

                testResultSuiteMap.put(testResult, new TreeSet<>(PojoComparators.TEST_SUITE_CASE_INSENSITIVE_ORDER));

            }
            testResultSuiteMap.get(testResult).add(testSuite);
        }
        return Collections.singletonMap(stage, testResultSuiteMap);

    }

    /**
     * Needed since ambiguous column names can be overwritten by another table if using record into
     *
     * @param record a record containing TestResult fields
     * @return TestResult
     */
    protected static TestResult getTestResultFromRecord(Record record) {
        TestResult testResult = record.into(TestResult.class);
        testResult.setTests(record.get(TEST_RESULT.TESTS));
        testResult.setSkipped(record.get(TEST_RESULT.SKIPPED));
        testResult.setError(record.get(TEST_RESULT.ERROR));
        testResult.setFailure(record.get(TEST_RESULT.FAILURE));
        testResult.setTime(record.get(TEST_RESULT.TIME));
        testResult.setIsSuccess(record.get(TEST_RESULT.IS_SUCCESS));
        testResult.setIsSuccess(record.get(TEST_RESULT.HAS_SKIP));
        return testResult;
    }

    /**
     * Needed since ambiguous column names can be overwritten by another table if using record into
     *
     * @param record a record containing TestResult fields
     * @return TestResult
     */
    protected static TestSuite getTestSuiteFromRecord(Record record) {
        TestSuite testSuite = record.into(TestSuite.class);
        testSuite.setTests(record.get(TEST_SUITE.TESTS));
        testSuite.setSkipped(record.get(TEST_SUITE.SKIPPED));
        testSuite.setError(record.get(TEST_SUITE.ERROR));
        testSuite.setFailure(record.get(TEST_SUITE.FAILURE));
        testSuite.setTime(record.get(TEST_SUITE.TIME));
        testSuite.setIsSuccess(record.get(TEST_SUITE.IS_SUCCESS));
        testSuite.setIsSuccess(record.get(TEST_SUITE.HAS_SKIP));
        return testSuite;
    }

    public Stage getStage(String orgName, String repoName, String branchName, String sha, String runReference, String stageName) {
        Stage ret = dsl.select(STAGE.fields())
                .from(ORG)
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(ORG.ORG_NAME.eq(orgName))
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(sha))
                        .and(RUN.RUN_REFERENCE.eq(runReference)))
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                        .and(STAGE.STAGE_NAME.eq(stageName)))
                .fetchOne()
                .into(Stage.class);
        if (ret == null) {
            throwNotFound("org: " + orgName, "repo: " + repoName, "branch: " + branchName, "sha: " + sha, "runReference: " + runReference, "stage: " + stageName);
        }
        return ret;
    }

    protected void throwNotFound(String... args) throws ResponseStatusException {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, getNotFoundMessage(args));
    }

    //TODO: refactor to take tuple of variable name and value
    protected static String getNotFoundMessage(String... args) {
        return "Unable to find " + String.join(" <- ", args);
    }

}