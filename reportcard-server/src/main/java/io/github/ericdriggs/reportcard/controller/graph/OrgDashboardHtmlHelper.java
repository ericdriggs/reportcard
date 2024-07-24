package io.github.ericdriggs.reportcard.controller.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;

import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.graph.comparator.GraphComparators;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import io.github.ericdriggs.reportcard.util.badge.*;
import io.github.ericdriggs.reportcard.util.badge.dto.RunBadgeDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.*;

import static io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper.getStorageURI;
import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

public class OrgDashboardHtmlHelper extends BrowseHtmlHelper {

    public static String renderOrgDashboardHtml(OrgDashboard orgDashboard) {
        final String main = getOrgDashboardMainDiv(orgDashboard);
        final List<Pair<String, String>> breadCrumbs = getOrgDashboardBreadCrumb(orgDashboard.getCompanyPojo(), orgDashboard.getOrgPojo());
        return getPage(main, breadCrumbs, "dashboard-columns")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/dashboard.css\">" + ls);
    }

    private static String getOrgDashboardMainDiv(OrgDashboard orgDashboard) {

        final CompanyOrgRepoBranchJobRunStageDTO orgPath = CompanyOrgRepoBranchJobRunStageDTO.builder()
                .company(orgDashboard.getCompanyPojo().getCompanyName())
                .org(orgDashboard.getOrgPojo().getOrgName()).build();
        StringBuilder str = new StringBuilder();
        for (RepoGraph repoGraph : emptyIfNull(orgDashboard.getRepoGraphs())) {
            final CompanyOrgRepoBranchJobRunStageDTO repoPath = orgPath.toBuilder().repo(repoGraph.repoName()).build();
            final String repoUrl = getUrl(repoPath);
            str.append("<fieldset class=\"repo-fieldset fieldset-group\">").append(ls);
            str.append("  <legend class='repo-legend'>repo: <a href=\"{repoUrl}\">{repoName}</a></legend>"
                    .replace("{repoUrl}", repoUrl)
                    .replace("{repoName}", repoGraph.repoName())
            ).append(ls);

            for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                final CompanyOrgRepoBranchJobRunStageDTO branchPath = repoPath.toBuilder().branch(branchGraph.branchName()).build();
                final String branchUrl = getUrl(branchPath);

                //Don't render branches without jobs
                if (CollectionUtils.isEmpty(branchGraph.jobs())) {
                    continue;
                }
                str.append("<fieldset class=\"branch-fieldset fieldset-group\">").append(ls);
                str.append("  <legend>branch: <a href=\"{branchUrl}\">{branchName}</a></legend>"
                        .replace("{branchUrl}", branchUrl)
                        .replace("{branchName}", branchGraph.branchName())
                ).append(ls);

                for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                    final CompanyOrgRepoBranchJobRunStageDTO jobPath = branchPath.toBuilder().jobId(jobGraph.jobId()).build();
                    final String jobUrl = getUrl(jobPath);
                    str.append("<fieldset class=\"job-fieldset fieldset-group\">").append(ls);
                    str.append("  <legend>job: <a href=\"{jobUrl}\">{jobInfo}</a></legend>"
                            .replace("{jobUrl}", jobUrl)
                            .replace("{jobInfo}", StringMapUtil.valuesOnlyColonSeparated(jobGraph.jobInfo()))
                    ).append(ls);

                    RunGraph lastSuccess = null;
                    RunGraph runGraph = null;
                    {

                        TreeSet<RunGraph> runGraphs = new TreeSet<>(GraphComparators.RUN_GRAPH_DESC);
                        runGraphs.addAll(emptyIfNull(jobGraph.runs()));
                        if (runGraphs.size() > 2) {
                            String runGraphsString = null;
                            try {
                                runGraphsString = SharedObjectMappers.ignoreUnknownObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(runGraphs);
                            } catch (JsonProcessingException e) {
                                runGraphsString = runGraphs.toString();
                            }
                            throw new IllegalStateException("runGraphs size cannot exceed 2, runGraphs:\n " + runGraphsString + "\n. Only latest and last success allowed.");
                        }

                        if (runGraphs.size() == 2) {
                            lastSuccess = runGraphs.last();
                        }
                        if (!runGraphs.isEmpty()) {
                            runGraph = runGraphs.first();
                        }
                    }

                    if (runGraph != null) {
                        final CompanyOrgRepoBranchJobRunStageDTO runPath = jobPath.toBuilder().runId(runGraph.runId()).build();
                        final RunBadgeDTO badgeStatusDateShaUri = RunBadgeDTO.fromRunGraph(runGraph, runPath);

                        final String runUrl = getUrl(runPath);

                        str.append("<fieldset class=\"run-fieldset fieldset-group\">").append(ls);
                        str.append("  <legend>run: <a href=\"{runUrl}\">{runCount}</a></legend>"
                                .replace("{runUrl}", runUrl)
                                .replace("{runCount}", NumberStringUtil.toString(runGraph.jobRunCount()))
                        ).append(ls);

                        str.append(BadgeHtmlHelper.statusDateShaBadge(badgeStatusDateShaUri)).append(ls);

                        TreeSet<StageBadgesDTO> stageBadgesDTOS = new TreeSet<>();
                        for (StageGraph stageGraph : emptyIfNull(runGraph.stages())) {
                            final CompanyOrgRepoBranchJobRunStageDTO stagePath = runPath.toBuilder().stageName(stageGraph.stageName()).build();

                            TreeSet<StorageTypeUriLabel> storageUris = new TreeSet<>();
                            for (StorageGraph storageGraph : emptyIfNull(stageGraph.storages())) {
                                final URI storageUri = getStorageURI(storageGraph.asStoragePojo());
                                storageUris.add(StorageTypeUriLabel.builder()
                                        .uri(storageUri)
                                        .label(storageGraph.label())
                                        .storageType(StorageType.fromStorageTypeId(storageGraph.storageType()))
                                        .build());
                            }
                            stageBadgesDTOS.add(StageBadgesDTO.fromStageGraph(stageGraph, stagePath, storageUris));
                        }
                        str.append(getStages(stageBadgesDTOS));

                        str.append("</fieldset><!--end-run-fieldset-->").append(ls);
                    }
                    if (lastSuccess != null) {
                        final CompanyOrgRepoBranchJobRunStageDTO runPath = jobPath.toBuilder().runId(lastSuccess.runId()).build();
                        final RunIdIDateShaUri rdsu = RunIdIDateShaUri.fromRunGraph(lastSuccess, runPath);
                        str.append("<div class=\"last-success\">").append(ls);
                        str.append("<div class=\"last-success-label\">last success</div>").append(ls);
                        str.append(BadgeHtmlHelper.lastSuccess(rdsu)).append(ls);
                        str.append("</div>").append(ls);
                    }
                    str.append("</fieldset><!--end-job-fieldset-->").append(ls);
                }
                str.append("</fieldset><!--end-branch-fieldset-->").append(ls);
            }
            str.append("</fieldset><!--end-repo-fieldset-->").append(ls);
        }
        return str.toString();
    }

    static String getStages(TreeSet<StageBadgesDTO> stageBadgesDTO) {
        final String tableBase =
                """
                        <table><tbody>
                          <!--stages-->
                        </tbody></table>
                        """;

        StringBuilder builder = new StringBuilder();
        for (StageBadgesDTO badgesDTO : stageBadgesDTO) {
            builder.append(getStage(badgesDTO));
        }

        return tableBase.replace("<!--stages-->", builder.toString());
    }

    static String getStage(StageBadgesDTO stageBadgesDTO) {
        final String stageBase =
                """
                        <tr>
                          <th class="job-info"><a href="{stageUri}">{stageName}</a></th>
                          <td><!--statusBadge--></td>
                          <td><!--trendBadge--></td>
                          <td><!--htmlLinks--></td>
                        </tr>
                        """;

        final String statusBadge = BadgeHtmlHelper.status(stageBadgesDTO.toBadgeStatusUri());
        final String trendBadge = BadgeHtmlHelper.trend(stageBadgesDTO.getTrendUri());
        final StringBuilder htmlLinks = new StringBuilder();
        for (StorageTypeUriLabel storageUri : stageBadgesDTO.getStorageUris()) {
            htmlLinks.append(BadgeHtmlHelper.storage(storageUri));
        }

        return stageBase
                .replace("{stageUri}", stageBadgesDTO.getStageUri().toString())
                .replace("{stageName}", stageBadgesDTO.getStageName())
                .replace("<!--statusBadge-->", statusBadge)
                .replace("<!--trendBadge-->", trendBadge)
                .replace("<!--htmlLinks-->", htmlLinks.toString());
    }

    protected static List<Pair<String, String>> getOrgDashboardBreadCrumb(CompanyPojo companyPojo, OrgPojo orgPojo) {
        if (companyPojo == null) {
            throw new NullPointerException("companyPojo");
        }
        if (orgPojo == null) {
            throw new NullPointerException("orgPojo");
        }

        CompanyOrgRepoBranchJobRunStageDTO path = CompanyOrgRepoBranchJobRunStageDTO.builder().company(companyPojo.getCompanyName()).org(orgPojo.getOrgName()).build();
        return getBreadCrumb(path, "dashboard");
    }

}