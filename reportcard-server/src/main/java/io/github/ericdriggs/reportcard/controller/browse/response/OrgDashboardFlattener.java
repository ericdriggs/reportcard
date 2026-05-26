package io.github.ericdriggs.reportcard.controller.browse.response;

import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.graph.comparator.GraphComparators;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

public class OrgDashboardFlattener {

    private OrgDashboardFlattener() {}

    public static List<FlatDashboardEntry> flatten(List<OrgDashboard> dashboards) {
        List<FlatDashboardEntry> entries = new ArrayList<>();
        for (OrgDashboard dashboard : emptyIfNull(dashboards)) {
            if (dashboard.getCompanyPojo() == null || dashboard.getOrgPojo() == null) {
                continue;
            }
            String company = dashboard.getCompanyPojo().getCompanyName();
            String org = dashboard.getOrgPojo().getOrgName();

            for (RepoGraph repoGraph : emptyIfNull(dashboard.getRepoGraphs())) {
                for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                    for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                        RunGraph latestRun = getLatestRun(jobGraph.runs());
                        if (latestRun == null) {
                            continue;
                        }
                        String url = String.format("/company/%s/org/%s/repo/%s/branch/%s/job/%d/run/%d",
                                company, org, repoGraph.repoName(), branchGraph.branchName(),
                                jobGraph.jobId(), latestRun.runId());
                        for (StageGraph stage : emptyIfNull(latestRun.stages())) {
                            Map<String, String> storageUrls = buildStorageUrls(stage.storages());
                            entries.add(FlatDashboardEntry.builder()
                                    .company(company)
                                    .org(org)
                                    .repo(repoGraph.repoName())
                                    .branch(branchGraph.branchName())
                                    .jobId(jobGraph.jobId())
                                    .jobInfo(jobGraph.jobInfo())
                                    .runId(latestRun.runId())
                                    .jobRunCount(latestRun.jobRunCount())
                                    .sha(latestRun.sha())
                                    .runDate(latestRun.runDate())
                                    .isSuccess(latestRun.isSuccess())
                                    .url(url)
                                    .stageName(stage.stageName())
                                    .storageUrls(storageUrls)
                                    .build());
                        }
                    }
                }
            }
        }
        return entries;
    }

    private static Map<String, String> buildStorageUrls(List<StorageGraph> storages) {
        Map<String, String> urls = new LinkedHashMap<>();
        for (StorageGraph storage : emptyIfNull(storages)) {
            if (storage.label() == null || storage.prefix() == null) {
                continue;
            }
            String storageUrl = storage.indexFile() != null
                    ? "/v1/api/storage/key/" + storage.prefix() + "/" + storage.indexFile()
                    : "/v1/api/storage/key/" + storage.prefix();
            urls.put(storage.label(), storageUrl);
        }
        return urls;
    }

    private static RunGraph getLatestRun(List<RunGraph> runs) {
        if (runs == null || runs.isEmpty()) {
            return null;
        }
        TreeSet<RunGraph> sorted = new TreeSet<>(GraphComparators.RUN_GRAPH_DESC);
        sorted.addAll(runs);
        return sorted.first();
    }
}
