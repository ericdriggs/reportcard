package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.controller.StorageController;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultPojo;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.model.branch.RunStorageTestResult;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;
import io.github.ericdriggs.reportcard.util.PrettyPrintUtil;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.ericdriggs.reportcard.util.badge.BadgeSvgHelper.truncatedSha;

@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class BrowseHtmlHelper {

    ;//static methods only

    //******************** companies ********************//

    public static String getCompaniesHtml() {
        final String main = baseFieldsetTable.replace(LEGEND, "Companies")
                .replace(TABLE_HEADERS, nameCountHeaders)
                .replace(TABLE_ROWS, getCompaniesItems());

        return getPage(main, getBreadCrumb(null));
    }

    protected static String getCompaniesItems() {

        final Map<CompanyPojo, Set<OrgPojo>> companyOrgs = CompanyOrgsCache.INSTANCE.getCache();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<CompanyPojo, Set<OrgPojo>> entry : companyOrgs.entrySet()) {
            final CompanyPojo company = entry.getKey();
            final Set<OrgPojo> orgs = entry.getValue();

            CompanyOrgRepoBranchJobRunStageDTO path = CompanyOrgRepoBranchJobRunStageDTO.builder().company(company.getCompanyName()).build();
            sb.append(getItemRow(path, company.getCompanyName(), orgs.size(), null));
        }
        return sb.toString();
    }

    //******************** company ********************//


    public static String getCompanyLinks(String company) {

        return
                """
                <fieldset>
                <legend>{companyName} links</legend>
                    {metricsLink}
                </fieldset>
                """
                        .replace("{companyName}", company)
                        .replace("{metricsLink}", getLink(company + " Metrics 🔢", "/metrics/company/" + company ))
                ;
    }

    public static String getCompanyHtml(String company) {
        final CompanyOrgRepoBranchJobRunStageDTO path = CompanyOrgRepoBranchJobRunStageDTO.builder().company(company).build();
        Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> companyOrgReposMap = CompanyOrgsReposCacheMap.INSTANCE.getValue(new CompanyDTO(company));

        if (companyOrgReposMap == null || companyOrgReposMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (companyOrgReposMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<OrgPojo, Set<RepoPojo>> orgRepos = companyOrgReposMap.values().stream().findFirst().get();

        final String companyLinks = getCompanyLinks(company);

        final String orgs = baseFieldsetTable.replace(LEGEND, "Orgs")
                .replace(TABLE_HEADERS, nameCountLastUpdatedHeaders)
                .replace(TABLE_ROWS, getCompanyOrgs(path, orgRepos));

        final String main =
                """
                <div>
                  {orgLinks}
                  {orgs}
                </div>
                """.replace("{orgLinks}", companyLinks)
                        .replace("{orgs}", orgs);
        return getPage(main, getBreadCrumb(path));
    }

    protected static String getCompanyOrgs(CompanyOrgRepoBranchJobRunStageDTO path, Map<OrgPojo, Set<RepoPojo>> orgRepos) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<OrgPojo, Set<RepoPojo>> entry : orgRepos.entrySet()) {
            final OrgPojo org = entry.getKey();
            final Set<RepoPojo> repos = entry.getValue();
            sb.append("                  <tr>").append(ls)
                    .append("  <td><a href=\"" + getUrl(path.toBuilder().org(org.getOrgName()).build()) + "\">" + org.getOrgName() + "</a></td>").append(ls)
                    .append("  <td class=\"count\">" + repos.size() + "</td>").append(ls)
                    .append("</tr").append(ls);
        }
        return sb.toString();
    }

    //******************** org ********************//

    public static String getOrgLinks(String org, CompanyOrgRepoBranchJobRunStageDTO path) {
        final CompanyOrgRepoBranchJobRunStageDTO orgPath = CompanyOrgRepoBranchJobRunStageDTO.truncateOrg(path);
        return
                """
                <fieldset>
                <legend>{orgName} links</legend>
                    {dashboardLink}<br>
                    {jobDashboardLink}<br>
                    {metricsLink}
                </fieldset>
                """
                        .replace("{orgName}", org)
                        .replace("{dashboardLink}", getLink(org + " Dashboard 📊", orgPath.toUrlPath() + "/dashboard?days=30"))
                        .replace("{jobDashboardLink}", "<a href='" + orgPath.toUrlPath() + "/pipelines?days=90' style='text-decoration: none;'>" + org + " Pipelines ⏲</a>" + System.lineSeparator())
                        .replace("{metricsLink}", getLink(org + " Metrics 🔢", "/metrics" + orgPath.toUrlPath() ))
                ;
    }

    public static String getOrgHtml(String company, String org) {

        final CompanyOrgRepoBranchJobRunStageDTO path = CompanyOrgRepoBranchJobRunStageDTO.builder().company(company).org(org).build();
        Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgRepoBranchMap = OrgReposBranchesCacheMap.INSTANCE.getValue(new CompanyOrgDTO(company, org));

        if (orgRepoBranchMap == null || orgRepoBranchMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (orgRepoBranchMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<RepoPojo, Set<BranchPojo>> repoBranchMap = orgRepoBranchMap.values().stream().findFirst().orElseThrow();

        final String orgLinks = getOrgLinks(org, path);

        final String repos = baseFieldsetTable.replace(LEGEND, "Repos")
                .replace(TABLE_HEADERS, nameCountLastUpdatedHeaders)
                .replace(TABLE_ROWS, getOrgRepos(path, repoBranchMap));

        final String main =
                """
                <div>
                  {orgLinks}
                  {repos}
                </div>
                """.replace("{orgLinks}", orgLinks)
                        .replace("{repos}", repos);
        return getPage(main, getBreadCrumb(path));
    }

    protected static String getOrgRepos(CompanyOrgRepoBranchJobRunStageDTO path, Map<RepoPojo, Set<BranchPojo>> repoBranchMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<RepoPojo, Set<BranchPojo>> entry : repoBranchMap.entrySet()) {
            final RepoPojo repo = entry.getKey();
            final Set<BranchPojo> branches = entry.getValue();
            final CompanyOrgRepoBranchJobRunStageDTO itemPath = path.toBuilder().repo(repo.getRepoName()).build();
            final Instant lastRun = mostRecent(branches.stream().map(BranchPojo::getLastRun).collect(Collectors.toSet()));
            sb.append(getItemRow(itemPath, repo.getRepoName(), branches.size(), lastRun));
        }
        return sb.toString();
    }

    //******************** repo ********************//

    public static String getRepoHtml(String company, String org, String repo) {

        final CompanyOrgRepoBranchJobRunStageDTO path = CompanyOrgRepoBranchJobRunStageDTO
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .build();

        Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoBranchJobMap = RepoBranchesJobsCacheMap.INSTANCE.getValue(new CompanyOrgRepoDTO(company, org, repo));

        if (repoBranchJobMap == null || repoBranchJobMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (repoBranchJobMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<BranchPojo, Set<JobPojo>> branchJobMap = repoBranchJobMap.values().stream().findFirst().orElseThrow();
        final String main = baseFieldsetTable.replace(LEGEND, "Branches")
                .replace(TABLE_HEADERS, nameCountLastUpdatedHeaders)
                .replace(TABLE_ROWS, getRepoBranches(path, branchJobMap));

        return getPage(main, getBreadCrumb(path));
    }

    protected static String getRepoBranches(CompanyOrgRepoBranchJobRunStageDTO path, Map<BranchPojo, Set<JobPojo>> branchJobMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<BranchPojo, Set<JobPojo>> entry : branchJobMap.entrySet()) {
            final BranchPojo branch = entry.getKey();
            final Set<JobPojo> jobs = entry.getValue();
            final Instant lastRun = mostRecent(jobs.stream().map(JobPojo::getLastRun).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRunStageDTO itemPath = path.toBuilder().branch(branch.getBranchName()).build();
            sb.append(getItemRow(itemPath, branch.getBranchName(), jobs.size(), lastRun, null, Map.of("runs", "60")));
        }
        return sb.toString();
    }

    //******************** branch ********************//

    public static String getBranchHtml(String company, String org, String repo, String branch, BranchStageViewResponse branchStageViewResponse, BranchJobLatestRunMap branchJobLatestRunMap) {

        final CompanyOrgRepoBranchDTO dto = CompanyOrgRepoBranchDTO
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .build();

        final CompanyOrgRepoBranchJobRunStageDTO path = dto.toCompanyOrgRepoBranchJobRunStageDTO();

        Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchJobRunMap = BranchJobsRunsCacheMap.INSTANCE.getValue(dto);

        if (branchJobRunMap == null || branchJobRunMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (branchJobRunMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        final String jobMain = baseFieldsetTable.replace(LEGEND, "Jobs")
                .replace(TABLE_HEADERS, branchHeaders)
                .replace(TABLE_ROWS, getJobRuns(path, branchStageViewResponse.getJobRun_StageTestResult_StoragesMap().keySet()));

        final String jobStagesDiv = getJobStages(branchJobLatestRunMap);

        final String stageHistory = getBranchStageView(branchStageViewResponse);

        return getPage("<div>" + jobMain + "<br>" + jobStagesDiv + "</div>" + stageHistory, getBreadCrumb(path));
    }

    protected static String getJobRuns(CompanyOrgRepoBranchJobRunStageDTO path, Set<JobRun> jobRuns) {

        StringBuilder sb = new StringBuilder();

        Map<JobPojo, Set<RunPojo>> jobRunMap = new LinkedHashMap<>();

        for (JobRun jobRun : jobRuns) {
            final JobPojo jobPojo = jobRun.getJob();
            jobRunMap.computeIfAbsent(jobPojo, k -> new LinkedHashSet<>());
            final RunPojo runPojo = jobRun.getRun();
            jobRunMap.get(jobPojo).add(runPojo);
        }

        for (Map.Entry<JobPojo, Set<RunPojo>> entry : jobRunMap.entrySet()) {
            final JobPojo job = entry.getKey();
            final Set<RunPojo> runs = entry.getValue();
            Integer maxRunCount = 0;
            for (RunPojo runPojo : runs) {
                maxRunCount = Integer.max(maxRunCount, runPojo.getJobRunCount());
            }
            final Instant lastRun = mostRecent(runs.stream().map(RunPojo::getRunDate).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRunStageDTO itemPath = path.toBuilder().jobId(job.getJobId()).build();
            final String jobInfo = "<pre>" + PrettyPrintUtil.sortedPrettyPrint(job.getJobInfo()) + "\n</pre>";
            sb.append(getItemRow(itemPath, Long.toString(job.getJobId()), maxRunCount, lastRun, jobInfo, Map.of("runs", "60")));

        }
        return sb.toString();
    }
    //******************** job ********************//

    public static String getJobHtml(String company, String org, String repo, String branch, Long jobId, BranchStageViewResponse branchStageViewResponse, BranchJobLatestRunMap branchJobLatestRunMap) {

        final CompanyOrgRepoBranchJobRunStageDTO path = CompanyOrgRepoBranchJobRunStageDTO
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .jobId(jobId)
                .build();

        final String jobStagesDiv = getJobStages(branchJobLatestRunMap);

        Map<JobPojo, Map<RunPojo, Set<StagePojo>>> jobRunStageMap = JobRunsStagesCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobDTO(company, org, repo, branch, jobId));

        if (jobRunStageMap == null || jobRunStageMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (jobRunStageMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        final String stagesMain = getBranchStageView(branchStageViewResponse);
        final String orgLinks = getOrgLinks(org, path);

        return getPage("<div>" + orgLinks + "<br>" + jobStagesDiv + "</div>" + stagesMain, getBreadCrumb(path));
    }

    protected final static String branchHeaders =
            """
            <th>JobId</th>
            <th>Runs</th>
            <th>Job Info</th>
            <th>Last Run</th>
            """;

    public static String getBranchStageView(BranchStageViewResponse branchStageViewResponse) {

        if (branchStageViewResponse == null || branchStageViewResponse.getJobRun_StageTestResult_StoragesMap() == null || branchStageViewResponse.getJobRun_StageTestResult_StoragesMap().isEmpty()) {
            return "";
        }

        final CompanyOrgRepoBranchJobRunStageDTO branchPath;
        {
            final CompanyOrgRepoBranch c = branchStageViewResponse.getCompanyOrgRepoBranch();
            branchPath = CompanyOrgRepoBranchJobRunStageDTO
                    .builder()
                    .company(c.getCompany() == null ? null : c.getCompany().getCompanyName())
                    .org(c.getOrg() == null ? null : c.getOrg().getOrgName())
                    .repo(c.getRepo() == null ? null : c.getRepo().getRepoName())
                    .branch(c.getBranch() == null ? null : c.getBranch().getBranchName())
                    .build();
        }

        //JobRun row
        StringBuilder runRowsHtml = new StringBuilder();
        for (Map.Entry<JobRun, Map<StageTestResultPojo, Set<StoragePojo>>> jobRunEntry : branchStageViewResponse.getJobRun_StageTestResult_StoragesMap().entrySet()) {
            final JobRun jobRun = jobRunEntry.getKey();
            Map<StageTestResultPojo, Set<StoragePojo>> stageTestResult_StorageMap = jobRunEntry.getValue();
            final JobPojo job = jobRun.getJob();
            final RunPojo run = jobRun.getRun();

            if (job == null || run == null || job.getJobId() == null || run.getRunId() == null) {
                continue;
            }

            final CompanyOrgRepoBranchJobRunStageDTO jobPath = branchPath.toBuilder().jobId(job.getJobId()).build();
            final CompanyOrgRepoBranchJobRunStageDTO runPath = jobPath.toBuilder().runId(run.getRunId()).build();

            StringBuilder stagesHtml = new StringBuilder();
            for (Map.Entry<StageTestResultPojo, Set<StoragePojo>> stageTestResultEntry : stageTestResult_StorageMap.entrySet()) {
                final StageTestResultPojo stageTestResult = stageTestResultEntry.getKey();
                final StagePojo stage = stageTestResult.getStage();
                final CompanyOrgRepoBranchJobRunStageDTO stagePath = runPath.toBuilder().stageName(stage.getStageName()).build();
                final Set<StoragePojo> storages = stageTestResultEntry.getValue();

                final String stageHtml = stageItemHtmlBase
                        .replace("{stageClass}", getStageClass(stageTestResult))
                        .replace("{stageName}", stage.getStageName())
                        .replace("{stageId}", Long.toString(stage.getStageId()))
                        .replace("{stageTime}", stageTestResult.getDurationString())
                        .replace("{stageUrl}", getUrl(stagePath))
                        .replace("<!--reportLinks-->", getReportLinks(storages));
                stagesHtml.append(stageHtml);
            }
            final String runRowHtml = runRowHtmlBase
                    .replace("{runRowId", "run_" + jobRun.getRun().getRunId())
                    .replace("{dotClass}", jobRun.isSuccess() ? "dot-pass" : "dot-fail")
                    .replace("{jobId}", Long.toString(job.getJobId()))
                    .replace("{jobInfo}", job.getJobInfo())
                    .replace("{jobUrl}", getUrl(jobPath))
                    .replace("{runCount}", NumberStringUtil.toString(run.getJobRunCount()))
                    .replace("{runDate}", run.getRunDate().toString())
                    .replace("{sha}", truncatedSha(run.getSha()))
                    .replace("{runId}", Long.toString(run.getRunId()))
                    .replace("{runUrl}", getUrl(runPath))
                    .replace("<!--stages-->", stagesHtml);

            runRowsHtml.append(runRowHtml);
        }
        return stageViewMain.replace("<!--runRows-->", runRowsHtml.toString());
    }

    protected static String getStageClass(StageTestResultPojo stageTestResultPojo) {
        final int testCount = stageTestResultPojo.getTestCount();
        if (!stageTestResultPojo.isSuccess() && testCount > 0) {
            return "stage-fail";
        } else {
            TestResultPojo testResultPojo = stageTestResultPojo.getTestResultPojo();
            if (testCount == 0 || (testResultPojo.getSkipped().equals(testCount))) {
                return "stage-skip";
            } else {
                return "stage-pass";
            }
        }
    }

    protected static String getReportLinks(Set<StoragePojo> storages) {
        if (storages == null || storages.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (StoragePojo storage : storages) {
            final String reportLink = reportLinkBase
                    .replace("{reportName}", storage.getLabel())
                    .replace("{reportUrl}", getStorageKey(storage));
            sb.append(reportLink + ls);
        }
        return sb.toString();
    }

    protected final static String stageViewMain =
            """
            <div id="stage-view">
              <fieldset>
                <legend>Run stage history</legend>
                <table class="sortable" id="run-stage-history-table">
                  <tbody>
                    <!--runRows-->
                  </tbody>
                </table> <!-- end run-stage-history-table -->
              </fieldset>
            </div><!-- end stage view -->
            """;

    protected final static String runRowHtmlBase =
            """
            <tr id='{runRowId}'>
              <td>
                <fieldset class="stage">
                  <legend id="run-{runId}-job-{jobId}">
                    <span class="dot {dotClass}"></span>
                    <a class="info" title='{jobInfo}' href="{jobUrl}">jobId: {jobId}</a>
                    <a href="{runUrl}">run #{runCount}
                      <span class="tiny_info">{sha}</span>
                      <span class="tiny_info">{runDate}</span>
                    </a>
                  </legend>
                  <div class="flex-row" style="text-align:left">
                    <!--stages-->
                  </div>
                </fieldset>
              </td>
            </tr>
            """;

    protected final static String stageItemHtmlBase =
            """
            <fieldset class="stage {stageClass}">
              <legend class="stage-legend">
                <a href="{stageUrl}">{stageName}</a>
                <a class="info" title='{jobInfo}' href="{stageUrl}">stageId: {stageId}</a>
                <br><span class="info">({stageTime})</span></legend>
              <!--reportLinks-->
            </fieldset>
            """;

    protected final static String reportLinkBase = "<a class=\"info report-link\" href=\"{reportUrl}\">" +
                                                   "<img alt=\"{reportName}\" class=\"report-img\" src=\"/image/report-simple.svg\">" +
                                                   "{reportName}" +
                                                   "</a>";

    //******************** job stages ********************//

    public static String getJobStages(BranchJobLatestRunMap latest) {

        StringBuilder jobStageRows = new StringBuilder();
        CompanyOrgRepoBranchDTO companyOrgRepoBranchDTO = CompanyOrgRepoBranchDTO
                .builder()
                .company(latest.getCompanyPojo().getCompanyName())
                .org(latest.getOrgPojo().getOrgName())
                .repo(latest.getRepoPojo().getRepoName())
                .branch(latest.getBranchPojo().getBranchName())
                .build();

        for (Map.Entry<JobPojo, TreeMap<String, RunStorageTestResult>> jobLatestEntry : latest.getJobStageLatestMap().entrySet()) {

            final JobPojo jobPojo = jobLatestEntry.getKey();
            final Long jobId = jobPojo.getJobId();
            final CompanyOrgRepoBranchJobRunStageDTO jobPath = companyOrgRepoBranchDTO.toCompanyOrgRepoBranchJobRunStageDTO(jobId, null, null);
            final TreeMap<String, RunStorageTestResult> stageReports = jobLatestEntry.getValue();
            for (Map.Entry<String, RunStorageTestResult> stageEntry : stageReports.entrySet()) {

                final String stageName = stageEntry.getKey();
                final RunStorageTestResult runStorageTestResult = stageEntry.getValue();
                final RunPojo runPojo = runStorageTestResult.getRunPojo();
                final Long runId = runPojo.getRunId();
                final CompanyOrgRepoBranchJobRunStageDTO stagePath = companyOrgRepoBranchDTO.toCompanyOrgRepoBranchJobRunStageDTO(jobId, runId, stageName);
                final URI trendReportURI = getTrendReportURI(companyOrgRepoBranchDTO.toCompanyOrgRepoBranchJobRunStageDTO(jobId, runId, stageName), stageName);
                jobStageRows.append(getJobRunRow(jobPath, jobPojo.getJobInfo(), stageName, runStorageTestResult.getStoragePojos(), trendReportURI));

            }
        }

        final String jobStageHeaders =
                """
                <th>Job Id</th>
                <th>Job Info</th>
                <th>Stage Name</th>
                <th>Latest reports</th>
                <th>Trend reports</th>
                """;

        return baseFieldsetTable.replace(LEGEND, "Job Stages")
                .replace(TABLE_HEADERS, jobStageHeaders)
                .replace(TABLE_ROWS, jobStageRows);
    }

    private static URI getTrendReportURI(CompanyOrgRepoBranchJobRunStageDTO c, String stageName) {
        return URI.create("/company/" + c.getCompany() + "/org/" + c.getOrg() + "/repo/" + c.getRepo() + "/branch/" + c.getBranch() + "/job/" + c.getJobId() + "/stage/" + stageName + "/trend?runs=30");
    }

    private static String getJobRunRow(CompanyOrgRepoBranchJobRunStageDTO path, String jobInfo, String stageName, TreeSet<StoragePojo> storagePojos, URI trendReportURI) {

        TreeMap<String, StoragePojo> latestStoragePojos = new TreeMap<>();
        for (StoragePojo storagePojo : storagePojos) {
            latestStoragePojos.putIfAbsent(storagePojo.getLabel(), storagePojo);
        }
        List<String> latestReports = new ArrayList<>();
        for (Map.Entry<String, StoragePojo> entry : latestStoragePojos.entrySet()) {
            final StoragePojo storagePojo = entry.getValue();
            latestReports.add(getReportLinks(Set.of(storagePojo)));
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<tr>").append(ls)
                .append("  <td><a href=\"" + getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateJob(path)) + "\">" + path.getJobId() + "</a></td>").append(ls)
                .append("  <td class=\"info\">" + StringMapUtil.jsonToDefinitionList(jobInfo) + "</td>").append(ls)
                .append("  <td><a href=\"" + getUrl(path.toBuilder().stageName(stageName).build()) + "\">" + stageName + "</a></td>").append(ls)
                .append("  <td>" + String.join(", ", latestReports + "</td>")).append(ls)
                .append("  <td><a href=\"" + trendReportURI.toString() + "\">trend link</a></td>").append(ls);
        sb.append("</tr>").append(ls);
        return sb.toString();
    }

    protected static String getStageTestResult(CompanyOrgRepoBranchJobRunStageDTO path, Map<StagePojo, Set<TestResultPojo>> runStageMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<StagePojo, Set<TestResultPojo>> entry : runStageMap.entrySet()) {
            final StagePojo stage = entry.getKey();
            final Set<TestResultPojo> testResults = entry.getValue();

            final Instant lastRun = mostRecent(testResults.stream().map(TestResultPojo::getTestResultCreated).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRunStageDTO itemPath = path.toBuilder().stageName(stage.getStageName()).build();
            sb.append(getItemRow(itemPath, stage.getStageName(), testResults.size(), lastRun));
        }
        return sb.toString();
    }
    //******************** stage ********************//

    //******************** util ********************//

    protected static List<Pair<String, String>> getBreadCrumb(CompanyOrgRepoBranchJobRunStageDTO path) {
        return getBreadCrumb(path, null);
    }

    protected static List<Pair<String, String>> getBreadCrumb(CompanyOrgRepoBranchJobRunStageDTO path, String suffix) {
        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("home", getUrl(null)));

        if (path != null) {
            if (!StringUtils.isEmpty(path.getCompany())) {
                breadCrumbs.add(Pair.of(path.getCompany(),
                        getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateCompany(path))));

                if (!StringUtils.isEmpty(path.getOrg())) {
                    breadCrumbs.add(Pair.of(path.getOrg(),
                            getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateOrg(path))));

                    if (!StringUtils.isEmpty(path.getRepo())) {
                        breadCrumbs.add(Pair.of(path.getRepo(),
                                getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateRepo(path))));

                        if (!StringUtils.isEmpty(path.getBranch())) {
                            final Map<String,String> branchQueryParams = path.getJobId() != null ? Collections.emptyMap() : Map.of("runs", "60");
                            breadCrumbs.add(Pair.of(path.getBranch(),
                                    getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateBranch(path), branchQueryParams)));

                            if (path.getJobId() != null) {
                                final Map<String,String> runQueryParams = path.getRunId() != null ? Collections.emptyMap() : Map.of("runs", "60");
                                breadCrumbs.add(Pair.of(("jobId: " + path.getJobId()),
                                        getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateJob(path), runQueryParams)));

                                if (path.getRunId() != null) {
                                    breadCrumbs.add(Pair.of(("runId: " + path.getRunId()),
                                            getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateRun(path))));

                                    if (path.getStageName() != null) {
                                        breadCrumbs.add(Pair.of(path.getStageName(), getUrl(path)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (suffix != null) {
            Pair<String, String> lastEntry = breadCrumbs.get(breadCrumbs.size() - 1);
            breadCrumbs.add(Pair.of(suffix, lastEntry.getValue() + "/" + suffix));
        }
        return breadCrumbs;
    }

    protected static String getItemRow(CompanyOrgRepoBranchJobRunStageDTO path, String name, int count, Instant date) {
        return getItemRow(path, name, count, date, null, Collections.emptyMap());
    }

    protected static String getItemRow(CompanyOrgRepoBranchJobRunStageDTO path, String name, int count, Instant date, String info, Map<String, String> queryParams) {
        StringBuilder sb = new StringBuilder();

        sb.append("<tr>").append(ls)
                .append("  <td><a href=\"" + getUrl(path, queryParams) + "\">" + name + "</a></td>").append(ls)
                .append("  <td class=\"count\">" + count + "</td>").append(ls);
        if (info != null) {
            sb.append("  <td class=\"info\">" + info + "</td>").append(ls);
        }
        if (date != null) {
            sb.append("  <td class=\"info\">" + date + "</td>").append(ls);
        }
        sb.append("</tr>").append(ls);
        return sb.toString();
    }

    protected static String getLink(String text, String url) {
        return getLink(text, url, null);
    }

    protected static String getLink(String text, String url, String cssClass) {
        return "<a href=\"{url}\" class=\"{cssClass}\">{text}</a>"
                       .replace("{url}", url)
                       .replace("{text}", text)
                       .replace("{cssClass}", cssClass == null ? "" : cssClass) + ls;
    }

    protected static String getUrl(CompanyOrgRepoBranchJobRunStageDTO path) {
        return getUrl(path, Collections.emptyMap());
    }

    protected static String getUrl(CompanyOrgRepoBranchJobRunStageDTO path, Map<String, String> queryParams) {
        if (path == null) {
            return "/";
        } else {
            return path.toUrlPath() + StringMapUtil.toQueryParams(queryParams);
        }
    }

    protected final static String ls = System.lineSeparator();

    public static String getPage(String main, List<Pair<String, String>> breadCrumbs) {
        return getPage(main, breadCrumbs, "flex-row");
    }

    public static String getPage(String main, List<Pair<String, String>> breadCrumbs, String mainClass) {
        return basePage
                .replace("main-class", mainClass)
                .replace("<!--bread-crumb-items-->", getBreadCrumbItems(breadCrumbs))
                .replace("<!--main-->", main);
    }

    protected static String getBreadCrumbItems(List<Pair<String, String>> breadCrumbs) {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, String> breadCrumb : breadCrumbs) {
            sb.append("<li><a href=\"").append(breadCrumb.getValue()).append("\">")
                    .append(breadCrumb.getKey())
                    .append("</a></li>").append(ls);
        }
        return sb.toString();
    }

    public static Instant mostRecent(Set<Instant> dateTimes) {
        if (dateTimes == null) {
            return null;
        }
        Instant lastRun = null;
        for (Instant localDateTime : dateTimes) {
            if (lastRun == null || localDateTime.isAfter(lastRun)) {
                lastRun = localDateTime;
            }
        }

        if (lastRun == null) {
            return null;
        }
        return lastRun.atZone(ZoneOffset.UTC).toInstant();
    }

    protected final static String nameCountHeaders =
            """
            <th>Name</th>
            <th>Count</th>
            """;

    protected final static String nameCountLastUpdatedHeaders =
            """
            <th>Name</th>
            <th>Count</th>
            <th>Last Updated</th>
            """;

    protected final static String LEGEND = "<!--legend-->";
    protected final static String TABLE_HEADERS = "<!--tableHeaders-->";
    protected final static String TABLE_ROWS = "<!--tableRows-->";

    protected final static String baseFieldsetTable =
            """
            <div name="base-fieldset-div" id="base-fieldset-div">
              <fieldset style="display: inline-block">
                <legend><!--legend--></legend>
                <table style="border:1px" class="sortable">
                  <thead>
                    <!--tableHeaders-->
                    </thead>
                    <tbody>
                      <!--tableRows-->
                    </tbody>
                </table>
              </fieldset>
            </div>
            """;

    protected final static String basePage =
            """
            <!DOCTYPE html>
            <html lang="en">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <head>
              <link rel="stylesheet" href="/css/shared.css">
              <link rel="stylesheet" href="/css/sortable.min.css"/>
              <!--additionalLinks-->
              <script src="/js/sortable.min.js"></script>
              <title>ReportCard</title>
            </head>
            <body>
            <header>
              <img alt="reportcard logo" src="/image/clipboard-check.svg" width="40" height="40" style="vertical-align: middle"> <span class="logo-text">ReportCard</span>
              <span>
                &nbsp;&nbsp;&nbsp;
                <a href="/swagger-ui/index.html">swagger</a>&nbsp;&nbsp;&nbsp;
                <a href="https://github.com/ericdriggs/reportcard">source</a>&nbsp;&nbsp;&nbsp;
                <span style="color:white">ver: 0.1.22</span>
              </span>
            </header>
            <nav aria-label="breadcrumb">
              <ul class="breadcrumb">
                <!--bread-crumb-items-->
              </ul>
            </nav>
            <br/>
            <!--pre-main-->
            <div class="main-class" role="main" id="main">
            <!--main-->
            </div><!-- end main -->
            </body>
            </html>
            """;

    public static String getStorageKey(StoragePojo storage) {
        if (storage == null) {
            return StorageController.storageKeyPath;
        }
        final String indexFile = storage.getIndexFile() == null ? "" : "/" + storage.getIndexFile();
        return StorageController.storageKeyPath + "/" + storage.getPrefix() + indexFile;
    }

}


