package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.cache.model.BranchStageViewResponse;
import io.github.ericdriggs.reportcard.cache.model.CompanyOrgRepoBranch;
import io.github.ericdriggs.reportcard.cache.model.JobRun;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.model.StageTestResultPojo;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import io.github.ericdriggs.reportcard.util.db.SqlJsonUtil;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

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

    public Set<CompanyPojo> getCompanies() {
        Set<CompanyPojo> companies = new TreeSet<>(PojoComparators.COMPANY_CASE_INSENSITIVE_ORDER);
        companies.addAll(dsl.select().from(COMPANY).fetch().into(CompanyPojo.class));
        return companies;
    }

    public Map<CompanyPojo, Set<OrgPojo>> getCompanyOrgs() {
        Result<Record> recordResult = dsl.select().from(COMPANY).leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)).fetch();

        Map<CompanyPojo, Set<OrgPojo>> companyOrgMap = new TreeMap<>(PojoComparators.COMPANY_CASE_INSENSITIVE_ORDER);
        for (Record record : recordResult) {
            CompanyPojo company = record.into(CompanyPojo.class);
            OrgPojo org = record.into(OrgPojo.class);

            if (!companyOrgMap.containsKey(company)) {
                companyOrgMap.put(company, new TreeSet<>(PojoComparators.ORG_CASE_INSENSITIVE_ORDER));
            }
            companyOrgMap.get(company).add(org);
        }
        return companyOrgMap;
    }

    public Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> getCompanyOrgsRepos(String companyName) {
        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<OrgPojo, Set<RepoPojo>> orgRepoMap = new TreeMap<>(PojoComparators.ORG_CASE_INSENSITIVE_ORDER);
        CompanyPojo company = null;
        for (Record record : recordResult) {
            if (company == null || company.getCompanyId() == null) {
                company = record.into(CompanyPojo.class);
            }
            OrgPojo org = record.into(OrgPojo.class);
            RepoPojo repo = record.into(RepoPojo.class);

            if (!orgRepoMap.containsKey(org)) {
                orgRepoMap.put(org, new TreeSet<>(PojoComparators.REPO_CASE_INSENSITIVE_ORDER));
            }
            orgRepoMap.get(org).add(repo);
        }
        return Collections.singletonMap(company, orgRepoMap);
    }

    public Map<OrgPojo, Set<RepoPojo>> getOrgsRepos(String companyName) {

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<OrgPojo, Set<RepoPojo>> orgRepoMap = new TreeMap<>(PojoComparators.ORG_CASE_INSENSITIVE_ORDER);
        for (Record record : recordResult) {
            OrgPojo org = record.into(OrgPojo.class);
            RepoPojo repo = record.into(RepoPojo.class);

            if (!orgRepoMap.containsKey(org)) {
                orgRepoMap.put(org, new TreeSet<>(PojoComparators.REPO_CASE_INSENSITIVE_ORDER));
            }
            orgRepoMap.get(org).add(repo);
        }
        return orgRepoMap;
    }

    public CompanyPojo getCompany(String companyName) {
        Record record = dsl.select(COMPANY.fields()).from(COMPANY).where(COMPANY.COMPANY_NAME.eq(companyName)).fetchOne();
        if (record == null) {
            throwNotFound("companyName:" + companyName);
        }
        return record.into(CompanyPojo.class);
    }

    public OrgPojo getOrg(String companyName, String orgName) {
        try {
            return dsl.
                    select(ORG.fields())
                    .from(COMPANY)
                    .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                                                .and(ORG.ORG_NAME.eq(orgName)))
                    .where(COMPANY.COMPANY_NAME.eq(companyName))
                    .fetchSingle()
                    .into(OrgPojo.class);
        } catch (NoDataFoundException ex) {
            throwNotFound("company: " + companyName, "org: " + orgName);
        }
        throw new IllegalStateException("unreachable code");
    }

    public Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> getOrgReposBranches(String companyName, String orgName) {

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<RepoPojo, Set<BranchPojo>> repoBranchMap = new TreeMap<>(PojoComparators.REPO_CASE_INSENSITIVE_ORDER);
        OrgPojo org = null;
        for (Record record : recordResult) {
            if (org == null || org.getOrgId() == null) {
                org = record.into(OrgPojo.class);
            }
            RepoPojo repo = record.into(RepoPojo.class);
            BranchPojo branch = record.into(BranchPojo.class);

            if (!repoBranchMap.containsKey(repo)) {
                repoBranchMap.put(repo, new TreeSet<>(PojoComparators.BRANCH_CASE_INSENSITIVE_ORDER));
            }
            repoBranchMap.get(repo).add(branch);
        }
        return Collections.singletonMap(org, repoBranchMap);
    }

    public RepoPojo getRepo(String companyName, String orgName, String repoName) {

        try {
            return dsl.
                    select(REPO.fields())
                    .from(COMPANY)
                    .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                            .and(ORG.ORG_NAME.eq(orgName)))
                    .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                            .and(REPO.REPO_NAME.eq(repoName)))
                    .where(COMPANY.COMPANY_NAME.eq(companyName))
                    .fetchSingle()
                    .into(RepoPojo.class);
        } catch (NoDataFoundException ex) {
            throwNotFound("company: " + companyName + ", org: " + orgName, "repo: " + repoName);
        }
        throw new IllegalStateException("unreachable code");
    }

    public Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>> getRepoBranchesJobs(String companyName, String orgName, String repoName) {

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<BranchPojo, Set<JobPojo>> branchesJobsMap = new TreeMap<>(PojoComparators.BRANCH_CASE_INSENSITIVE_ORDER);
        RepoPojo repo = null;
        for (Record record : recordResult) {
            if (repo == null || repo.getRepoId() == null) {
                repo = record.into(RepoPojo.class);
            }
            BranchPojo branch = record.into(BranchPojo.class);
            JobPojo job = record.into(JobPojo.class);

            if (!branchesJobsMap.containsKey(branch)) {
                branchesJobsMap.put(branch, new TreeSet<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER));
            }
            branchesJobsMap.get(branch).add(job);
        }
        return Collections.singletonMap(repo, branchesJobsMap);
    }

    public BranchPojo getBranch(String companyName, String orgName, String repoName, String branchName) {
        BranchPojo ret = dsl.select(BRANCH.fields())
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .where(COMPANY.COMPANY_NAME.eq( companyName))
                .fetchOne()
                .into(BranchPojo.class);
        if (ret == null) {
            throwNotFound("company: " + companyName + ", org: " + orgName, "repo: " + repoName, "branch: " + branchName);
        }
        return ret;
    }

    public Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> getBranchJobsRuns(String companyName, String orgName, String repoName, String branchName, Map<String, String> expectedJobFilters) {

        if (expectedJobFilters == null) {
            expectedJobFilters = Collections.emptyMap();
        }

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        //TODO: filter jobs
        Map<JobPojo, Set<RunPojo>> jobRunMap = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);
        BranchPojo branch = null;
        for (Record record : recordResult) {
            if (branch == null || branch.getBranchId() == null) {
                branch = record.into(BranchPojo.class);
            }
            JobPojo job = record.into(JobPojo.class);
            if (!JsonCompare.containsMap(expectedJobFilters, job.getJobInfo())) {
                continue;
            }
            RunPojo run = record.into(RunPojo.class);

            if (!jobRunMap.containsKey(job)) {
                jobRunMap.put(job, new TreeSet<>(PojoComparators.RUN_CASE_INSENSITIVE_ORDER));
            }
            jobRunMap.get(job).add(run);
        }
        return Collections.singletonMap(branch, jobRunMap);
    }

    public JobPojo getJob(String companyName, String orgName, String repoName, String branchName, Map<String, String> jobInfo) {
        Set<JobPojo> jobs = new TreeSet<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);

        Result<Record> jobsResult = dsl.select(JOB.fields())
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        for (Record record : jobsResult) {
            JobPojo job = record.into(JobPojo.class);
            if (JsonCompare.equalsMap(job.getJobInfo(), jobInfo)) {
                return job;
            }
        }
        throwNotFound("company: " + companyName + ", org: " + orgName, "repo: " + repoName, "branch: " + branchName, "jobInfo: " + jobInfo);
        throw new IllegalStateException("unreachable code");
    }

    public Map<JobPojo, Map<RunPojo, Set<StagePojo>>> getJobRunsStages(String companyName, String orgName, String repoName, String branchName, Long jobId) {

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId))
                )
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<RunPojo, Set<StagePojo>> runStageMap = new ConcurrentSkipListMap<>(PojoComparators.RUN_DESCENDING);
        JobPojo job = null;
        for (Record record : recordResult) {
            if (job == null || job.getJobId() == null) {
                job = record.into(JobPojo.class);
            }
            RunPojo run = record.into(RunPojo.class);
            StagePojo stage = record.into(StagePojo.class);

            if (!runStageMap.containsKey(run)) {
                runStageMap.put(run, new TreeSet<>(PojoComparators.STAGE_CASE_INSENSITIVE_ORDER));
            }
            runStageMap.get(run).add(stage);
        }
        return Collections.singletonMap(job, runStageMap);
    }

    public Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>> getRunStagesTestResults(String companyName, String orgName, String repoName, String branchName, Long jobId, Long runId) {

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
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
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<StagePojo, Set<TestResultPojo>> stageTestResultMap = new TreeMap<>(PojoComparators.STAGE_CASE_INSENSITIVE_ORDER);
        RunPojo run = null;
        for (Record record : recordResult) {
            if (run == null || run.getRunId() == null) {
                run = record.into(RunPojo.class);
            }
            StagePojo stage = record.into(StagePojo.class);
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo testResult = record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo.class);

            if (!stageTestResultMap.containsKey(stage)) {
                stageTestResultMap.put(stage, new TreeSet<>(PojoComparators.TEST_RESULT_CASE_INSENSITIVE_ORDER));
            }
            stageTestResultMap.get(stage).add(testResult);
        }
        return Collections.singletonMap(run, stageTestResultMap);
    }

    public BranchStageViewResponse getStageViewForBranch(String companyName, String orgName, String repoName, String branchName, int runs) {

        Long[] topRunIds = dsl.select(RUN.RUN_ID)
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID))
                        .and(BRANCH.BRANCH_NAME.eq(branchName))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .orderBy(RUN.RUN_ID.desc())
                .limit(runs)
                .fetchArray(RUN.RUN_ID, Long.class);

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID).and(RUN.RUN_ID.in(topRunIds)))
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .leftJoin(STORAGE).on(STORAGE.STAGE_FK.eq(STAGE.STAGE_ID))
                .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        return doGetStageViewForBranch(recordResult);
    }

    public BranchStageViewResponse getStageViewForJob(String companyName, String orgName, String repoName, String branchName, Long jobId, int runs) {

        Long[] topRunIds = dsl.selectDistinct(RUN.RUN_ID)
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .orderBy(RUN.RUN_ID.desc())
                .limit(runs)
                .fetchArray(RUN.RUN_ID, Long.class);


        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.RUN_ID.in(topRunIds)))
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .leftJoin(STORAGE).on(STORAGE.STAGE_FK.eq(STAGE.STAGE_ID))
                .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .orderBy(RUN.RUN_ID.desc())
                .fetch();

        return doGetStageViewForBranch(recordResult);
    }

    public BranchStageViewResponse getStageViewForJobInfo(String companyName, String orgName, String repoName, String branchName, Map<String,String> jobInfo) {

        //final String jobInfoJson = StringMapUtil.toJson(jobInfo);
        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(SqlJsonUtil.jobInfoEqualsJson(jobInfo)))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID))
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .leftJoin(STORAGE).on(STORAGE.STAGE_FK.eq(STAGE.STAGE_ID))
                .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        return doGetStageViewForBranch(recordResult);
    }

    public BranchStageViewResponse doGetStageViewForBranch(Result<Record> recordResult) {

        BranchStageViewResponse.BranchStageViewResponseBuilder rBuilder = BranchStageViewResponse.builder();

        CompanyOrgRepoBranch companyOrgRepoBranch = null;
        Map<JobRun, Map<StageTestResultPojo, Set<StoragePojo>>> jobRunStagesMap = new TreeMap<>(PojoComparators.JOB_RUN_DATE_DESCENDING_ORDER);

        for (Record record : recordResult) {
            if (companyOrgRepoBranch == null) {
                CompanyOrgRepoBranch.CompanyOrgRepoBranchBuilder cBuilder = CompanyOrgRepoBranch.builder();
                cBuilder.company(record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo.class));

                cBuilder.org(record.into(OrgPojo.class));
                cBuilder.repo(record.into(RepoPojo.class));
                cBuilder.branch(record.into(BranchPojo.class));
                companyOrgRepoBranch = cBuilder.build();
            }

            JobPojo job = record.into(JobPojo.class);
            RunPojo run = record.into(RunPojo.class);

            if (job.getJobId() != null || run.getRunId() != null) {
                JobRun jobRun = JobRun.builder().job(job).run(run).build();

                jobRunStagesMap.computeIfAbsent(jobRun, k -> new TreeMap<>(PojoComparators.STAGE_TEST_RESULT_POJO_DATE_DESCENDING));

                Map<StageTestResultPojo, Set<StoragePojo>> stageTestResult_StorageMap = jobRunStagesMap.get(jobRun);

                StagePojo stage = record.into(StagePojo.class);
                TestResultPojo testResult = null;
                try {
                    testResult = record.into(TestResultPojo.class);
                } catch (Exception ex) {
                    //NO-OP. allowed to be null. Would prefer more elegant handling of null
                }
                StageTestResultPojo stageTestResult = StageTestResultPojo.builder().stage(stage).testResultPojo(testResult).build();
                stageTestResult_StorageMap.putIfAbsent(stageTestResult, new TreeSet<>(PojoComparators.STORAGE_CASE_INSENSITIVE_ORDER));

                StoragePojo storage = null;
                try {
                    storage = record.into(StoragePojo.class);
                    if (storage.getStorageId() != null) {
                        stageTestResult_StorageMap.get(stageTestResult).add(storage);
                    }
                } catch (Exception ex) {
                    //NO-OP. allowed to be null. Would prefer more elegant handling of null
                }
            }
        }
        return BranchStageViewResponse.builder().companyOrgRepoBranch(companyOrgRepoBranch).jobRun_StageTestResult_StoragesMap(jobRunStagesMap).build();
    }

    public Map<RunPojo, Map<StagePojo, Set<StoragePojo>>> getRunStagesStorages(String companyName, String orgName, String repoName, String branchName, Long jobId, Long runId) {

        Result<Record> recordResult = dsl.select()
                                         .from(COMPANY)
                                         .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                                                                         .and(ORG.ORG_NAME.eq(orgName)))
                                         .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                                                                       .and(REPO.REPO_NAME.eq(repoName)))
                                         .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                                                                            .and(BRANCH.BRANCH_NAME.eq(branchName)))
                                         .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                                                                        .and(JOB.JOB_ID.eq(jobId)))
                                         .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                                                                     .and(RUN.RUN_ID.eq(runId)))
                                         .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                                         .leftJoin(STORAGE).on(STORAGE.STAGE_FK.eq(STORAGE.STORAGE_ID))
                                         .where(COMPANY.COMPANY_NAME.eq(companyName))
                                         .fetch();

        Map<StagePojo, Set<StoragePojo>> stageStorageMap = new TreeMap<>(PojoComparators.STAGE_CASE_INSENSITIVE_ORDER);
        RunPojo run = null;
        for (Record record : recordResult) {
            if (run == null || run.getRunId() == null) {
                run = record.into(RunPojo.class);
            }
            StagePojo stage = record.into(StagePojo.class);
            io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo testResult = record.into(io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo.class);

            if (!stageStorageMap.containsKey(stage)) {
                stageStorageMap.put(stage, new TreeSet<>(PojoComparators.STORAGE_CASE_INSENSITIVE_ORDER));
            }
            stageStorageMap.get(stage).add(testResult);
        }
        return Collections.singletonMap(run, stageStorageMap);
    }

    public RunPojo getRun(String companyName, String orgName, String repoName, String branchName, Long jobId, Long runId) {

        RunPojo ret = dsl.select(RUN.fields())
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                        .and(JOB.JOB_ID.eq(jobId)))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.RUN_ID.eq(runId)))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetchOne()
                .into(RunPojo.class);

        if (ret == null) {
            throwNotFound("company: " + companyName + ", org: " + orgName, "repo: " + repoName, "branch: " + branchName, "jobId: " + Long.toString(jobId), "runId: " + Long.toString(runId));
        }
        return ret;
    }

    public Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> getBranchJobsRunsForSha(String companyName, String orgName, String repoName, String branchName, String sha) {

        Result<Record> recordResult = dsl.select()
                .from(COMPANY)
                .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(sha)))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetch();

        Map<JobPojo, Set<RunPojo>> jobRunMap = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);

        BranchPojo branch = null;
        for (Record record : recordResult) {
            if (branch == null || branch.getBranchId() == null) {
                branch = record.into(BranchPojo.class);
            }
            JobPojo job = record.into(JobPojo.class);
            RunPojo run = record.into(RunPojo.class);

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

    public RunPojo getRunFromReference(String companyName, String orgName, String repoName, String branchName, String sha, UUID runReference) {

        RunPojo ret = dsl.select(RUN.fields())
                .from(COMPANY)
                .join(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                        .and(ORG.ORG_NAME.eq(orgName)))
                .join(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                        .and(REPO.REPO_NAME.eq(repoName)))
                .join(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                        .and(BRANCH.BRANCH_NAME.eq(branchName)))
                .join(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID))
                .join(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                        .and(RUN.SHA.eq(sha))
                        .and(RUN.RUN_REFERENCE.eq(runReference.toString())))
                .where(COMPANY.COMPANY_NAME.eq(companyName))
                .fetchOne()
                .into(RunPojo.class);

        if (ret == null) {
            throwNotFound("company: " + companyName + ", org: " + orgName, "repo: " + repoName, "branch: " + branchName, "sha:" + sha, "runReference: " + runReference);
        }
        return ret;
    }

    public StageTestResultModel getStageTestResultMap(String companyName, String orgName, String repoName, String branchName, Long jobId, Long runId, String stageName) {

        Result<Record> recordResult =
                dsl.select()
                   .from(COMPANY)
                   .leftJoin(ORG).on(ORG.COMPANY_FK.eq(COMPANY.COMPANY_ID)
                                                   .and(ORG.ORG_NAME.eq(orgName)))
                   .leftJoin(REPO).on(REPO.ORG_FK.eq(ORG.ORG_ID)
                                                 .and(REPO.REPO_NAME.eq(repoName)))
                   .leftJoin(BRANCH).on(BRANCH.REPO_FK.eq(REPO.REPO_ID)
                                                      .and(BRANCH.BRANCH_NAME.eq(branchName)))
                   .leftJoin(JOB).on(JOB.BRANCH_FK.eq(BRANCH.BRANCH_ID)
                                                  .and(JOB.JOB_ID.eq(jobId)))
                   .leftJoin(RUN).on(RUN.JOB_FK.eq(JOB.JOB_ID)
                                               .and(RUN.RUN_ID.eq(runId)))
                   .leftJoin(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID)
                                                   .and(STAGE.STAGE_NAME.eq(stageName)))
                   .leftJoin(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                   .where(COMPANY.COMPANY_NAME.eq(companyName))
                   .fetch();

        //

        StagePojo stage = null;
        //TestResultPojo testResult = null;

        TestResultModel testResult = TestResultPersistService.testResultFromRecords(recordResult);

        //Map<TestSuitePojo, Map<TestCasePojo, List<TestCaseFaultPojo>>> testSuiteTestCaseMap = new LinkedHashMap<>();
        for (Record record : recordResult) {
            if (stage == null) {
                stage = record.into(StagePojo.class);
            }
            break;
        }

        return StageTestResultModel.builder().stage(stage).testResult(testResult).build();
    }



//    import io.github.ericdriggs.reportcard.model.TestSuitePojo;
//import io.github.ericdriggs.reportcard.model.TestResultPojo;
//import io.github.ericdriggs.reportcard.model.TestCasePojo;


    /**
     * Needed since ambiguous column names can be overwritten by another table if using record into
     *
     * @param record a record containing TestResultPojo fields
     * @return TestResultPojo
     */
    protected static TestResultPojo getTestResultFromRecord(Record record) {
        TestResultPojo testResult = record.into(TestResultPojo.class);
        testResult.setTests(record.get(TEST_RESULT.TESTS));
        testResult.setSkipped(record.get(TEST_RESULT.SKIPPED));
        testResult.setError(record.get(TEST_RESULT.ERROR));
        testResult.setFailure(record.get(TEST_RESULT.FAILURE));
        testResult.setTime(record.get(TEST_RESULT.TIME));
        testResult.setIsSuccess(record.get(TEST_RESULT.IS_SUCCESS));
        testResult.setHasSkip(record.get(TEST_RESULT.HAS_SKIP));
        testResult.setTestSuitesJson(record.get(TEST_RESULT.TEST_SUITES_JSON));
        return testResult;
    }

    public List<TestStatusPojo> getAllTestStatuses() {
        List<TestStatusPojo> ret = dsl.select(TEST_STATUS.fields())
                .from(TEST_STATUS)
                .fetch()
                .into(TestStatusPojo.class);
        return ret;
    }

    protected void throwNotFound(String... args) throws ResponseStatusException {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, getNotFoundMessage(args));
    }

    //TODO: refactor to take tuple of variable name and value (or map<String,String>
    protected static String getNotFoundMessage(String... args) {
        return "Unable to find " + String.join(" <- ", args);
    }

}