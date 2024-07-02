package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultPojo;
import io.github.ericdriggs.reportcard.model.graph.*;
import lombok.Builder;
import lombok.Data;

import java.util.*;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.assertSize1;
import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

@Data
@Builder(toBuilder = true)
public class BranchStageViewResponse {
    CompanyOrgRepoBranch companyOrgRepoBranch;
    Map<JobRun, Map<StageTestResultPojo, Set<StoragePojo>>> jobRun_StageTestResult_StoragesMap;

    public static BranchStageViewResponse fromCompanyGraphs(List<CompanyGraph> companyGraphs) {

        assertSize1(companyGraphs, "companyGraphs");
        final CompanyGraph companyGraph = companyGraphs.get(0);

        assertSize1(companyGraph.orgs(), "orgs");
        final OrgGraph orgGraph = companyGraph.orgs().get(0);

        assertSize1(orgGraph.repos(), "repos");
        final RepoGraph repoGraph = orgGraph.repos().get(0);

        assertSize1(repoGraph.branches(), "branches");
        final BranchGraph branchGraph = repoGraph.branches().get(0);

        Map<JobRun, Map<StageTestResultPojo, Set<StoragePojo>>> jobRun_StageTestResult_StoragesMap = new TreeMap<>(PojoComparators.JOB_RUN_DATE_DESCENDING_ORDER);
        for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
            final JobPojo jobPojo = jobGraph.asJobPojo();

            for (RunGraph runGraph : emptyIfNull(jobGraph.runs())) {
                final RunPojo runPojo = runGraph.asRunPojo();
                Map<StageTestResultPojo, Set<StoragePojo>> stageResultStoragesMap = new TreeMap<>(PojoComparators.STAGE_TEST_RESULT_POJO_DATE_DESCENDING);
                for (StageGraph stageGraph : emptyIfNull(runGraph.stages())) {
                    final StagePojo stagePojo = stageGraph.asStagePojo();

                    TestResultPojo testResultPojo = null;
                    for (TestResultGraph testResultGraph : emptyIfNull(stageGraph.testResults())) {
                        testResultPojo = testResultGraph.asTestResultPojo();
                        break;
                    }
                    StageTestResultPojo stageTestResultPojo = StageTestResultPojo.builder().stage(stagePojo).testResultPojo(testResultPojo).build();
                    if (stageResultStoragesMap.get(stageTestResultPojo) != null) {
                        throw new IllegalStateException("duplicate stage when expected singleton. stageResultStoragesMap.get(stageTestResultPojo): " + stageResultStoragesMap.get(stageTestResultPojo) );
                    }
                    TreeSet<StoragePojo> storagePojos = new TreeSet<>(PojoComparators.STORAGE_CASE_INSENSITIVE_ORDER);
                    for (StorageGraph storageGraph : emptyIfNull(stageGraph.storages())) {
                        storagePojos.add(storageGraph.asStoragePojo());
                    }

                    stageResultStoragesMap.put(stageTestResultPojo, storagePojos);

                }
                final JobRun jobRun = JobRun.builder().job(jobPojo).run(runPojo).build();
                jobRun_StageTestResult_StoragesMap.put(jobRun, stageResultStoragesMap);
            }
        }

        final CompanyOrgRepoBranch companyOrgRepoBranch =
                CompanyOrgRepoBranch
                        .builder()
                        .company(companyGraph.asCompanyPojo())
                        .org(orgGraph.asOrgPojo())
                        .repo(repoGraph.asRepoPojo())
                        .branch(branchGraph.asBranchPojo())
                        .build();
        return BranchStageViewResponse
                .builder()
                .companyOrgRepoBranch(companyOrgRepoBranch)
                .jobRun_StageTestResult_StoragesMap(jobRun_StageTestResult_StoragesMap)
                .build();
    }
}
