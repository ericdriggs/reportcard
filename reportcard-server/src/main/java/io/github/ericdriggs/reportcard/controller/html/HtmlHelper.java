package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.cache.dto.*;
import io.github.ericdriggs.reportcard.cache.model.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;

public enum HtmlHelper {

    ;//static methods only

    //******************** home ********************//
    private final static String homeMain =
            """
            <div class="flex-row" role="main" id="main">
              <fieldset>
                <legend>resources</legend>
                <ul>
                  <li><a href="/company">companies</a></li>
                </ul>
              </fieldset>
            </div>
            """;

    //******************** companies ********************//

    public static String getCompaniesHtml() {
        final String main = baseMain.replace(LEGEND, "Companies")
                                    .replace(TABLE_HEADERS, nameCountHeaders)
                                    .replace(TABLE_ROWS, getCompaniesItems());

        return getPage(main, getBreadCrumb(null));
    }

    private static String getCompaniesItems() {

        final Map<Company, Set<Org>> companyOrgs = CompanyOrgsCache.INSTANCE.getCache();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Company, Set<Org>> entry : companyOrgs.entrySet()) {
            final Company company = entry.getKey();
            final Set<Org> orgs = entry.getValue();

            CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage.builder().company(company.getCompanyName()).build();
            sb.append(getItemRow(path, company.getCompanyName(), orgs.size(), null));
        }
        return sb.toString();
    }

    //******************** company ********************//
    public static String getCompanyHtml(String company) {
        final CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage.builder().company(company).build();
        Map<Company, Map<Org, Set<Repo>>> companyOrgReposMap = CompanyOrgsReposCacheMap.INSTANCE.getValue(new CompanyName(company));

        if (companyOrgReposMap == null || companyOrgReposMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (companyOrgReposMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Org, Set<Repo>> orgRepos = companyOrgReposMap.values().stream().findFirst().get();

        final String main = baseMain.replace(LEGEND, "Orgs")
                                    .replace(TABLE_HEADERS, nameCountHeaders)
                                    .replace(TABLE_ROWS, getCompanyOrgs(path, orgRepos));
        return getPage(main, getBreadCrumb(path));
    }

    private static String getCompanyOrgs(CompanyOrgRepoBranchJobRunStage path, Map<Org, Set<Repo>> orgRepos) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Org, Set<Repo>> entry : orgRepos.entrySet()) {
            final Org org = entry.getKey();
            final Set<Repo> repos = entry.getValue();
            sb.append("                  <tr>").append(ls)
              .append("  <td><a href=\"" + getUrl(path.toBuilder().org(org.getOrgName()).build()) + "\">" + org.getOrgName() + "</a></td>").append(ls)
              .append("  <td class=\"count\">" + repos.size() + "</td>").append(ls)
              .append("</tr").append(ls);
        }
        return sb.toString();
    }

    //******************** org ********************//

    public static String getOrgHtml(String company, String org) {

        final CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage.builder().company(company).org(org).build();
        Map<Org, Map<Repo, Set<Branch>>> orgRepoBranchMap = OrgReposBranchesCacheMap.INSTANCE.getValue(new CompanyOrg(company, org));

        if (orgRepoBranchMap == null || orgRepoBranchMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (orgRepoBranchMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Repo, Set<Branch>> repoBranchMap = orgRepoBranchMap.values().stream().findFirst().orElseThrow();

        final String main = baseMain.replace(LEGEND, "Repos")
                                    .replace(TABLE_HEADERS, nameCountLastUpdatedHeaders)
                                    .replace(TABLE_ROWS, getOrgRepos(path, repoBranchMap));

        return getPage(main, getBreadCrumb(path));
    }

    private static String getOrgRepos(CompanyOrgRepoBranchJobRunStage path, Map<Repo, Set<Branch>> repoBranchMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Repo, Set<Branch>> entry : repoBranchMap.entrySet()) {
            final Repo repo = entry.getKey();
            final Set<Branch> branches = entry.getValue();
            final CompanyOrgRepoBranchJobRunStage itemPath = path.toBuilder().repo(repo.getRepoName()).build();
            final Instant lastRun = mostRecent(branches.stream().map(Branch::getLastRun).collect(Collectors.toSet()));
            sb.append(getItemRow(itemPath, repo.getRepoName(), branches.size(), lastRun));
        }
        return sb.toString();
    }

    //******************** repo ********************//

    public static String getRepoHtml(String company, String org, String repo) {

        final CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .build();

        Map<Repo, Map<Branch, Set<Job>>> repoBranchJobMap = RepoBranchesJobsCacheMap.INSTANCE.getValue(new CompanyOrgRepo(company, org, repo));

        if (repoBranchJobMap == null || repoBranchJobMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (repoBranchJobMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Branch, Set<Job>> branchJobMap = repoBranchJobMap.values().stream().findFirst().orElseThrow();
        final String main = baseMain.replace(LEGEND, "Branches")
                                    .replace(TABLE_HEADERS, nameCountLastUpdatedHeaders)
                                    .replace(TABLE_ROWS, getRepoBranches(path, branchJobMap));

        return getPage(main, getBreadCrumb(path));
    }

    private static String getRepoBranches(CompanyOrgRepoBranchJobRunStage path, Map<Branch, Set<Job>> branchJobMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Branch, Set<Job>> entry : branchJobMap.entrySet()) {
            final Branch branch = entry.getKey();
            final Set<Job> jobs = entry.getValue();
            final Instant lastRun = mostRecent(jobs.stream().map(Job::getLastRun).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRunStage itemPath = path.toBuilder().branch(branch.getBranchName()).build();
            sb.append(getItemRow(itemPath, branch.getBranchName(), jobs.size(), lastRun));
        }
        return sb.toString();
    }

    //******************** branch ********************//

    public static String getBranchHtml(String company, String org, String repo, String branch) {

        final CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .build();

        Map<Branch, Map<Job, Set<Run>>> branchJobRunMap = BranchJobsRunsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranch(company, org, repo, branch));

        if (branchJobRunMap == null || branchJobRunMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (branchJobRunMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Job, Set<Run>> jobRunMap = branchJobRunMap.values().stream().findFirst().orElseThrow();
        final String main = baseMain.replace(LEGEND, "Jobs")
                                    .replace(TABLE_HEADERS, branchHeaders)
                                    .replace(TABLE_ROWS, getJobRuns(path, jobRunMap));

        return getPage(main, getBreadCrumb(path));
    }

    private static String getJobRuns(CompanyOrgRepoBranchJobRunStage path, Map<Job, Set<Run>> jobRunMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Job, Set<Run>> entry : jobRunMap.entrySet()) {
            final Job job = entry.getKey();
            final Set<Run> runs = entry.getValue();
            final Instant lastRun = mostRecent(runs.stream().map(Run::getCreated).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRunStage itemPath = path.toBuilder().jobId(job.getJobId()).build();
            final String jobInfo = job.getJobInfo();
            sb.append(getItemRow(itemPath, Long.toString(job.getJobId()), runs.size(), jobInfo, lastRun));

        }
        return sb.toString();
    }
    //******************** job ********************//

    public static String getJobHtml(String company, String org, String repo, String branch, Long jobId) {

        final CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .jobId(jobId)
                .build();

        Map<Job, Map<Run, Set<Stage>>> jobRunStageMap = JobRunsStagesCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJob(company, org, repo, branch, jobId));

        if (jobRunStageMap == null || jobRunStageMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (jobRunStageMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Run, Set<Stage>> runStageMap = jobRunStageMap.values().stream().findFirst().orElseThrow();
        final String main = baseMain.replace(LEGEND, "Runs")
                                    .replace(TABLE_HEADERS, jobHeaders)
                                    .replace(TABLE_ROWS, getRunStages(path, runStageMap));

        return getPage(main, getBreadCrumb(path));
    }

    private static String getRunStages(CompanyOrgRepoBranchJobRunStage path, Map<Run, Set<Stage>> runStageMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Run, Set<Stage>> entry : runStageMap.entrySet()) {
            final Run run = entry.getKey();
            final Set<Stage> stages = entry.getValue();

            final Instant lastRun = mostRecent(Set.of(run.getCreated()));
            final CompanyOrgRepoBranchJobRunStage itemPath = path.toBuilder().runId(run.getRunId()).build();
            sb.append(getItemRow(itemPath, Long.toString(run.getRunId()), stages.size(), lastRun));
        }
        return sb.toString();
    }

    private final static String branchHeaders =
            """
            <th>JobId</th>
            <th>Runs</th>
            <th>Job Info</th>
            <th>Last Updated</th>
            """;

    private final static String jobHeaders =
            """
            <th>runId</th>
            <th>stages</th>
            <th>Last Updated</th>
            """;


    public static String getStageView(CompanyOrgRepoBranchJobRunStage path) {

    }

    //{stageName}
    //{stageTime}
    //<!--reportLinks-->
    private final static String stageItem =
            """
            <fieldset class="stage stage-pass">
              <legend class="stage-legend">{stageName}<br><span class="info">({stageTime})</span></legend>
              <!--reportLinks-->
            </fieldset>
            """;

    //{runId}
    //{jobId}
    //{jobInfo}
    //{runDate}
    private final static String stageRow =
            """
            <tr>
              <td>
                <fieldset class="stage">
                  <legend id="run-{runId}-job-{jobId}">
                    <a href="run.html"><span class="dot dot-pass"></span>#2</a>
                    &nbsp;<a class="info" title='{jobInfo}' href="job.html">114232</a>
                    &nbsp;<span class="info">{runDate}</span>
                  </legend>
                  <div class="flex-row" style="text-align:left">
                    <fieldset class="stage stage-pass">
                      <legend class="stage-legend">{stageName}<br><span class="info">({stageTime})</span></legend>
                      <!--reportLinks-->
                    </fieldset>
                    <fieldset class="stage stage-pass">
                      <legend class="stage-legend">api test<br><span class="info">(5min 24s)</span></legend>
                      <a class="info report-link" href="#"><img alt="cucumber html" class="report-img" src="image/report-simple.svg">cucumber html</a><br />
                      <a class="info report-link" href="#"><img alt="junit xml" class="report-img" src="image/report-xml.svg">junit xml</a>
                    </fieldset>
                  </div>
                </fieldset>
              </td>
            </tr>
            """;
    private final static String stageViewMain =
            """
            <div id="stage-view">
              <fieldset>
                <legend>Stage View</legend>
                <table class="sortable" id="stage-table">
                  <tbody>
                    <!--stageViewRows-->
                  </tbody>
                </table> <!-- end stage-table -->
              </fieldset>
            </div><!-- end stage view -->
            """;

    //******************** run ********************//

    public static String getRunHtml(String company, String org, String repo, String branch, Long jobId, Long runId) {

        final CompanyOrgRepoBranchJobRunStage path = CompanyOrgRepoBranchJobRunStage
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .jobId(jobId)
                .runId(runId)
                .build();

        Map<Run, Map<Stage, Set<TestResult>>> runStageTestResultMap = RunStagesTestResultsCacheMap.INSTANCE.getValue(new CompanyOrgRepoBranchJobRun(company, org, repo, branch, jobId, runId));

        if (runStageTestResultMap == null || runStageTestResultMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (runStageTestResultMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Stage, Set<TestResult>> stageTestResultMap = runStageTestResultMap.values().stream().findFirst().orElseThrow();
        final String main = baseMain.replace(LEGEND, "Stages")
                                    .replace(TABLE_HEADERS, nameCountLastUpdatedHeaders)
                                    .replace(TABLE_ROWS, getStageTestResult(path, stageTestResultMap));

        return getPage(main, getBreadCrumb(path));
    }

    private static String getStageTestResult(CompanyOrgRepoBranchJobRunStage path, Map<Stage, Set<TestResult>> runStageMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Stage, Set<TestResult>> entry : runStageMap.entrySet()) {
            final Stage stage = entry.getKey();
            final Set<TestResult> testResults = entry.getValue();

            final Instant lastRun = mostRecent(testResults.stream().map(TestResult::getTestResultCreated).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRunStage itemPath = path.toBuilder().stageName(stage.getStageName()).build();
            sb.append(getItemRow(itemPath, stage.getStageName(), testResults.size(), lastRun));
        }
        return sb.toString();
    }
    //******************** stage ********************//

    //******************** util ********************//

    private static List<Pair<String, String>> getBreadCrumb(CompanyOrgRepoBranchJobRunStage path) {
        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("home", getUrl(null)));

        if (path != null) {
            if (!StringUtils.isEmpty(path.getCompany())) {
                breadCrumbs.add(Pair.of(path.getCompany(),
                        getUrl(CompanyOrgRepoBranchJobRunStage.truncateCompany(path))));

                if (!StringUtils.isEmpty(path.getOrg())) {
                    breadCrumbs.add(Pair.of(path.getOrg(),
                            getUrl(CompanyOrgRepoBranchJobRunStage.truncateOrg(path))));

                    if (!StringUtils.isEmpty(path.getRepo())) {
                        breadCrumbs.add(Pair.of(path.getRepo(),
                                getUrl(CompanyOrgRepoBranchJobRunStage.truncateRepo(path))));

                        if (!StringUtils.isEmpty(path.getBranch())) {
                            breadCrumbs.add(Pair.of(path.getBranch(),
                                    getUrl(CompanyOrgRepoBranchJobRunStage.truncateBranch(path))));

                            if (path.getJobId() != null) {
                                breadCrumbs.add(Pair.of(String.valueOf("jobId: " + path.getJobId()),
                                        getUrl(CompanyOrgRepoBranchJobRunStage.truncateJob(path))));

                                if (path.getRunId() != null) {
                                    breadCrumbs.add(Pair.of(String.valueOf("runId: " + path.getRunId()),
                                            getUrl(CompanyOrgRepoBranchJobRunStage.truncateRun(path))));

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
        return breadCrumbs;
    }

    private static String getItemRow(CompanyOrgRepoBranchJobRunStage path, String name, int count, Instant date) {
        return getItemRow(path, name, count, null, date);
    }

    private static String getItemRow(CompanyOrgRepoBranchJobRunStage path, String name, int count, String info, Instant date) {
        StringBuilder sb = new StringBuilder();

        sb.append("<tr>").append(ls)
          .append("  <td><a href=\"" + getUrl(path) + "\">" + name + "</a></td>").append(ls)
          .append("  <td class=\"count\">" + count + "</td>").append(ls);
        if (info != null) {
            sb.append("  <td class=\"info\">" + info + "</td>").append(ls);
        }
        if (date != null) {
            sb.append("  <td class=\"info\">" + date.toString() + "</td>").append(ls);
        }
        sb.append("</tr>").append(ls);
        return sb.toString();
    }

    private static String getUrl(CompanyOrgRepoBranchJobRunStage path) {
        StringBuilder sb = new StringBuilder();

        if (path != null) {
            sb.append(path.toUrlPath());
        }
        return sb.toString();
    }

    private final static String ls = System.getProperty("line.separator");

    public static String getPage(String main, List<Pair<String, String>> breadCrumbs) {
        return basePage
                .replace("<!--bread-crumb-items-->", getBreadCrumbItems(breadCrumbs))
                .replace("<!--main-->", main);
    }

    private static String getBreadCrumbItems(List<Pair<String, String>> breadCrumbs) {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, String> breadCrumb : breadCrumbs) {
            sb.append("<li><a href=\"").append(breadCrumb.getValue()).append("\">")
              .append(breadCrumb.getKey())
              .append("</a></li>").append(ls);
        }
        return sb.toString();
    }

    public static Instant mostRecent(Set<LocalDateTime> dateTimes) {
        if (dateTimes == null) {
            return null;
        }
        LocalDateTime lastRun = null;
        for (LocalDateTime localDateTime : dateTimes) {
            if (lastRun == null || localDateTime.isAfter(lastRun)) {
                lastRun = localDateTime;
            }
        }

        if (lastRun == null) {
            return null;
        }
        return lastRun.atZone(ZoneOffset.UTC).toInstant();
    }

    private final static String nameCountHeaders =
            """
            <th>Name</th>
            <th>Count</th>
            """;

    private final static String nameCountLastUpdatedHeaders =
            """
            <th>Name</th>
            <th>Count</th>
            <th>Last Updated</th>
            """;

    private final static String LEGEND = "<!--legend-->";
    private final static String TABLE_HEADERS = "<!--tableHeaders-->";
    private final static String TABLE_ROWS = "<!--tableRows-->";

    private final static String baseMain =
            """
            <div id="browse">
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

    private final static String basePage =
            """
            <html lang="en">
            <head>
              <link rel="stylesheet" href="/css/shared.css">
              <link rel="stylesheet" href="/css/sortable.min.css"/>
              <script src="/js/sortable.min.js"></script>
              <title>ReportCard</title>
            </head>
            <body>
            <header><img alt="reportcard logo" src="/image/clipboard-check.svg" width="40px" height="40px" style="vertical-align: middle"> <span class="logo-text">ReportCard</span></header>
            <nav aria-label="breadcrumb">
              <ul class="breadcrumb">
                <!--bread-crumb-items-->
              </ul>
            </nav>
            <br/>
            <div class="flex-row" role="main" id="main">
            <!--main-->
            </div><!-- end main -->
            <footer>
              <span>Reportcard </span>&nbsp;&nbsp;&nbsp;
              <a href="https://github.com/ericdriggs/reportcard">source</a>&nbsp;&nbsp;&nbsp;
              <span>version: 0.0.1</span>
            </footer>
            </body>
            </html>
            """;

}


