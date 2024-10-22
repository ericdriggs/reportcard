package io.github.ericdriggs.reportcard.model.branch;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.graph.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.assertSize1;
import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

@Builder
@Jacksonized
@Value
public class BranchJobLatestRunMap {
    CompanyPojo companyPojo;
    OrgPojo orgPojo;
    RepoPojo repoPojo;
    BranchPojo branchPojo;

    @Builder.Default
    TreeMap<JobPojo, TreeMap<String, RunStorageTestResult>> jobStageLatestMap = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);

    public static BranchJobLatestRunMap fromCompanyGraphs(List<CompanyGraph> companyGraphs) {

        assertSize1(companyGraphs, "companyGraphs");
        final CompanyGraph companyGraph = companyGraphs.get(0);

        assertSize1(companyGraph.orgs(), "orgs");
        final OrgGraph orgGraph = companyGraph.orgs().get(0);

        assertSize1(orgGraph.repos(), "repos");
        final RepoGraph repoGraph = orgGraph.repos().get(0);

        assertSize1(repoGraph.branches(), "branches");
        final BranchGraph branchGraph = repoGraph.branches().get(0);

        TreeMap<JobPojo, TreeMap<String, RunStorageTestResult>> jobStageLatestMap = new TreeMap<>(PojoComparators.JOB_CASE_INSENSITIVE_ORDER);
        for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
            final JobPojo jobPojo = jobGraph.asJobPojo();
            TreeMap<String, RunStorageTestResult> stageRunStorageTestResultMap = new TreeMap<>();
            for (RunGraph runGraph : emptyIfNull(jobGraph.runs())) {
                final RunPojo runPojo = runGraph.asRunPojo();
                for (StageGraph stageGraph : emptyIfNull(runGraph.stages())) {
                    final StagePojo stagePojo = stageGraph.asStagePojo();
                    if (stageRunStorageTestResultMap.get(stagePojo.getStageName()) != null) {
                        final RunStorageTestResult existingResult = stageRunStorageTestResultMap.get(stagePojo.getStageName());
                        //query may have duplicates for multi-stage so use comparison to get latest
                        if (existingResult.getRunPojo().getRunId() > runPojo.getRunId()) {
                            continue;
                        }
                    }
                    TestResultPojo testResultPojo = null;
                    for (TestResultGraph testResultGraph : emptyIfNull(stageGraph.testResults())) {
                        testResultPojo = testResultGraph.asTestResultPojo();
                        break;
                    }
                    TreeSet<StoragePojo> storagePojos = new TreeSet<>(PojoComparators.STORAGE_CASE_INSENSITIVE_ORDER);
                    for (StorageGraph storageGraph : emptyIfNull(stageGraph.storages())) {
                        storagePojos.add(storageGraph.asStoragePojo());
                    }

                    RunStorageTestResult runStorageTestResult = RunStorageTestResult.builder().runPojo(runPojo).testResultPojo(testResultPojo).storagePojos(storagePojos).build();
                    stageRunStorageTestResultMap.put(stagePojo.getStageName(), runStorageTestResult);

                }
            }
            jobStageLatestMap.put(jobPojo, stageRunStorageTestResultMap);
        }
        return BranchJobLatestRunMap
                .builder()
                .companyPojo(companyGraph.asCompanyPojo())
                .orgPojo(orgGraph.asOrgPojo())
                .repoPojo(repoGraph.asRepoPojo())
                .branchPojo(branchGraph.asBranchPojo())
                .jobStageLatestMap(jobStageLatestMap)
                .build();
    }


}
