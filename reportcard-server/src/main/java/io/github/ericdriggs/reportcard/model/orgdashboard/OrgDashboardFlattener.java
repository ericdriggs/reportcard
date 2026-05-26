package io.github.ericdriggs.reportcard.model.orgdashboard;

import io.github.ericdriggs.reportcard.controller.browse.response.FlatDashboardEntry;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.graph.comparator.GraphComparators;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

public class OrgDashboardFlattener {

    private OrgDashboardFlattener() {}

    public static List<FlatDashboardEntry> flatten(List<OrgDashboard> dashboards) {
        List<FlatDashboardEntry> entries = new ArrayList<>();
        for (OrgDashboard dashboard : emptyIfNull(dashboards)) {
            String company = dashboard.getCompanyPojo().getCompanyName();
            String org = dashboard.getOrgPojo().getOrgName();

            for (RepoGraph repoGraph : emptyIfNull(dashboard.getRepoGraphs())) {
                for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                    for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                        RunGraph latestRun = getLatestRun(jobGraph.runs());
                        if (latestRun == null) {
                            continue;
                        }
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
                                .build());
                    }
                }
            }
        }
        return entries;
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
