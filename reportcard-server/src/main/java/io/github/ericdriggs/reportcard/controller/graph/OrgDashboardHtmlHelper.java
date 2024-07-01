package io.github.ericdriggs.reportcard.controller.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import io.github.ericdriggs.reportcard.util.badge.*;
import io.github.ericdriggs.reportcard.util.badge.dto.RunBadgeDTO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper.getPrefixUrl;
import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

public class OrgDashboardHtmlHelper extends BrowseHtmlHelper {

    public static String renderOrgDashboardHtml(OrgDashboard orgDashboard) {
        final String main = getOrgDashboardMainDiv(orgDashboard);
        return getPage(main, getOrgDashboardBreadCrumb(orgDashboard.getCompanyPojo(), orgDashboard.getOrgPojo()))
                .replace("<body>", "<body onload=\"applyTestFilters()\">")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/dashboard.css\">" + ls);

    }

    private static String getOrgDashboardMainDiv(OrgDashboard orgDashboard) {

        final String companyName = orgDashboard.getCompanyPojo().getCompanyName();
        final String orgName = orgDashboard.getOrgPojo().getOrgName();
        final CompanyOrgRepoBranchJobRunStageDTO companyPath = CompanyOrgRepoBranchJobRunStageDTO.builder().company(companyName).build();
        final CompanyOrgRepoBranchJobRunStageDTO orgPath = CompanyOrgRepoBranchJobRunStageDTO.builder().company(companyName).org(orgName).build();
        final String companyUrl = getUrl(companyPath);
        final String orgUrl = getUrl(orgPath);
        final String companyOrgLegendTable = getCompanyOrgLegendTable(companyName, companyUrl, orgName, orgUrl);

        StringBuilder str = new StringBuilder();
        for (RepoGraph repoGraph : emptyIfNull(orgDashboard.getRepoGraphs())) {
            final CompanyOrgRepoBranchJobRunStageDTO repoPath = orgPath.toBuilder().repo(repoGraph.repoName()).build();
            final String repoUrl = getUrl(repoPath);
            str.append("<fieldset class=\"fieldset-group\">").append(ls);
            str.append("  <legend>repo: <a href=\"{repoUrl}\">{repoName}</a></legend>"
                    .replace("{repoUrl}", repoUrl)
                    .replace("{repoName}", repoGraph.repoName())
            ).append(ls);

            for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                final CompanyOrgRepoBranchJobRunStageDTO branchPath = repoPath.toBuilder().branch(branchGraph.branchName()).build();
                final String branchUrl = getUrl(branchPath);
                str.append("<fieldset class=\"fieldset-group\">").append(ls);
                str.append("  <legend>branch: <a href=\"{branchUrl}\">{branchName}</a></legend>"
                        .replace("{branchUrl}", branchUrl)
                        .replace("{branchName}", branchGraph.branchName())
                ).append(ls);

                for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                    final CompanyOrgRepoBranchJobRunStageDTO jobPath = branchPath.toBuilder().jobId(jobGraph.jobId()).build();
                    final String jobUrl = getUrl(jobPath);
                    str.append("<fieldset class=\"fieldset-group\">").append(ls);
                    str.append("  <legend>job: <a href=\"{jobUrl}\">{jobInfo}</a></legend>"
                            .replace("{jobUrl}", jobUrl)
                            .replace("{jobInfo}", StringMapUtil.valuesOnlyColonSeparated(jobGraph.jobInfo()))
                    ).append(ls);

                    RunGraph lastSuccess = null;
                    RunGraph runGraph = null;
                    {

                        List<RunGraph> runGraphs = emptyIfNull(jobGraph.runs());
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
                            lastSuccess = runGraphs.get(1);
                        }
                        if (!runGraphs.isEmpty()) {
                            //should already be in descending order by runId, but if not, use TreeSet
                            runGraph = runGraphs.get(0);
                        }
                    }
                    if (lastSuccess != null) {
                        final CompanyOrgRepoBranchJobRunStageDTO runPath = jobPath.toBuilder().runId(lastSuccess.runId()).build();
                        final RunIdIDateShaUri rdsu = RunIdIDateShaUri.fromRunGraph(lastSuccess, runPath);
                        str.append("<div class=\"last-success\">last success</div>").append(ls);
                        str.append(BadgeSvgHelper.lastSuccessDateSha(rdsu)).append(ls);
                    }
                    if (runGraph != null) {
                        final CompanyOrgRepoBranchJobRunStageDTO runPath = jobPath.toBuilder().runId(runGraph.runId()).build();
                        final RunBadgeDTO badgeStatusDateShaUri = RunBadgeDTO.fromRunGraph(runGraph, runPath);


                        final String runUrl = getUrl(runPath);

                        str.append("<fieldset class=\"fieldset-group\">").append(ls);
                        str.append("  <legend>run: <a href=\"{runUrl}\">{runCount}</a></legend>"
                                .replace("{runUrl}", runUrl)
                                .replace("{runCount}", NumberStringUtil.toString(runGraph.jobRunCount()))
                        ).append(ls);

                        str.append(BadgeSvgHelper.statusDateSha(badgeStatusDateShaUri)).append(ls);

                        TreeSet<StageBadgesDTO> stageBadgesDTOS = new TreeSet<>();
                        for (StageGraph stageGraph : emptyIfNull(runGraph.stages())) {
                            final CompanyOrgRepoBranchJobRunStageDTO stagePath = runPath.toBuilder().stageName(stageGraph.stageName()).build();

                            TreeSet<StorageUri> storageUris = new TreeSet<>();
                            for (StorageGraph storageGraph : emptyIfNull(stageGraph.storages())) {
                                final StoragePath storagePath = new StoragePath(stagePath, runGraph.jobRunCount(), storageGraph.label());
                                final String storageUri = getPrefixUrl(storagePath.getPrefix());
                                storageUris.add(StorageUri.builder().uri(storageUri).label(storageGraph.label()).build());
                            }
                            stageBadgesDTOS.add(StageBadgesDTO.fromStageGraph(stageGraph, stagePath, storageUris));
                        }
                        str.append(getStages(stageBadgesDTOS));

                        str.append("</fieldset><!--end-run-fieldset-->").append(ls);
                    }
                    str.append("</fieldset><!--end-job-fieldset-->").append(ls);
                }
                str.append("</fieldset><!--end-branch-fieldset-->").append(ls);
            }
            str.append("</fieldset><!--end-repo-fieldset-->").append(ls);
        }
        return companyOrgLegendTable + str;
    }

    static String getStages(TreeSet<StageBadgesDTO> stageBadgesDTO) {
        final String tableBase =
                """
                <table><tbody>
                  <!--stages-->
                </tbody></table>
                """;

        StringBuilder builder = new StringBuilder();

        return tableBase.replace("<!--stages-->", builder.toString());
    }

    static String getStage(StageBadgesDTO stageBadgesDTO) {
        final String stageBase =
                """
                <tr>
                  <th class="job-info"><a href="{stageUri}">{stageName}</a></th>
                  <!--statusBadge-->
                  <!--trendBadge-->
                  <!--htmlLinks-->
                </tr>
                """;

        final String statusBadge = BadgeSvgHelper.status(stageBadgesDTO.toBadgeStatusUri());
        final String trendBadge = BadgeSvgHelper.trend(stageBadgesDTO.getTrendUri());
        final StringBuilder htmlLinks = new StringBuilder();
        for (StorageUri storageUri : stageBadgesDTO.getStorageUris()) {
            htmlLinks.append(BadgeSvgHelper.htmlAnchor(storageUri));
        }

        return stageBase
                .replace("{stageUri}", stageBadgesDTO.getStageUri().toString())
                .replace("{stageName}", stageBadgesDTO.getStageName())
                .replace("<!--statusBadge-->", statusBadge)
                .replace("<!--trendBadge-->", trendBadge)
                .replace("<!--htmlLinks-->", htmlLinks.toString());
    }

    static String getCompanyOrgLegendTable(String companyName, String companyUrl, String orgName, String orgUrl) {
        return
                """
                <div class="table-wrapper org-legend" >
                    <table>
                        <tr>
                            <th>company</th>
                            <td><a href="companyUrl">companyName</a></td>
                        </tr>
                        <tr>
                            <th>org</th>
                            <td><a href="orgUrl">companyUrl</a></td>
                        </tr>
                    </table>
                </div>
                """.replace("companyName", companyName)
                   .replace("companyUrl", companyUrl)
                   .replace("orgName", orgName)
                   .replace("orgUrl", orgUrl);

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