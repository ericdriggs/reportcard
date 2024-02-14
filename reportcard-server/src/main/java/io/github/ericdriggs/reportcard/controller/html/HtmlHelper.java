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
        final String main = baseCompaniesMain.replace("<!--companies-->", getCompaniesItems());
        return getPage(main, getBreadCrumb(null));
    }

    private static String getCompaniesItems() {

        final Map<Company, Set<Org>> companyOrgs = CompanyOrgsCache.INSTANCE.getCache();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Company, Set<Org>> entry : companyOrgs.entrySet()) {
            final Company company = entry.getKey();
            final Set<Org> orgs = entry.getValue();

            CompanyOrgRepoBranchJobRun path = CompanyOrgRepoBranchJobRun.builder().company(company.getCompanyName()).build();
            sb.append(getItemRow(path, company.getCompanyName(), orgs.size(), null));
        }
        return sb.toString();
    }

    private final static String baseCompaniesMain =
            """
            <div class="flex-row" role="main" id="main">
                <fieldset style="display: inline-block">
                    <legend>Companies</legend>
                    <table style="border:1px" class="sortable">
                        <thead>
                        <tr class="heading">
                            <th class="heading">Name</th>
                            <th class="heading">Organizations</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!--companies-->
                        </tbody>
                    </table>
                </fieldset>
            </div>
            """;

    //******************** company ********************//
    public static String getCompanyHtml(String company) {
        final CompanyOrgRepoBranchJobRun path = CompanyOrgRepoBranchJobRun.builder().company(company).build();
        Map<Company, Map<Org, Set<Repo>>> companyOrgReposMap = CompanyOrgsReposCacheMap.INSTANCE.getValue(new CompanyName(company));

        if (companyOrgReposMap == null || companyOrgReposMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (companyOrgReposMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Org, Set<Repo>> orgRepos = companyOrgReposMap.values().stream().findFirst().get();

        final String main = baseCompanyMain.replace("<!--companyOrgs-->", getCompanyOrgs(path, orgRepos));
        return getPage(main, getBreadCrumb(path));

    }

    private static String getCompanyOrgs(CompanyOrgRepoBranchJobRun path, Map<Org, Set<Repo>> orgRepos) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Org, Set<Repo>> entry : orgRepos.entrySet()) {
            final Org org = entry.getKey();
            final Set<Repo> repos = entry.getValue();
            sb.append("<tr>").append(ls)
              .append("  <td><a href=\"" + getUrl(path.toBuilder().org(org.getOrgName()).build()) + "\">" + org.getOrgName() + "</a></td>").append(ls)
              .append("  <td class=\"count\">" + repos.size() + "</td>").append(ls)
              .append("</tr").append(ls);
        }
        return sb.toString();
    }

    private final static String baseCompanyMain =
            """
            <div class="flex-row" role="main" id="main">
                <fieldset style="display: inline-block">
                    <legend>Orgs</legend>
                    <table style="border:1px" class="sortable">
                        <thead>
                        <tr class="heading">
                            <th>Name</th>
                            <th>Repos</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!--companyOrgs-->
                        </tbody>
                    </table>
                </fieldset>
            </div>
            """;
    //******************** org ********************//

    public static String getOrgHtml(String company, String org) {

        final CompanyOrgRepoBranchJobRun path = CompanyOrgRepoBranchJobRun.builder().company(company).org(org).build();
        Map<Org, Map<Repo, Set<Branch>>> orgRepoBranchMap = OrgReposBranchesCacheMap.INSTANCE.getValue(new CompanyOrg(company, org));

        if (orgRepoBranchMap == null || orgRepoBranchMap.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found: " + path.toUrlPath());
        }
        if (orgRepoBranchMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one entry found: " + path.toUrlPath());
        }

        Map<Repo, Set<Branch>> repoBranchMap = orgRepoBranchMap.values().stream().findFirst().orElseThrow();
        final String main = baseOrgHtml.replace("<!--orgRepos-->", getOrgRepos(path, repoBranchMap));
        return getPage(main, getBreadCrumb(path));
    }

    private static String getOrgRepos(CompanyOrgRepoBranchJobRun path, Map<Repo, Set<Branch>> repoBranchMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Repo, Set<Branch>> entry : repoBranchMap.entrySet()) {
            final Repo repo = entry.getKey();
            final Set<Branch> branches = entry.getValue();
            final CompanyOrgRepoBranchJobRun itemPath = path.toBuilder().repo(repo.getRepoName()).build();
            final Instant lastRun = mostRecent(branches.stream().map(Branch::getLastRun).collect(Collectors.toSet()));
            sb.append(getItemRow(itemPath, repo.getRepoName(), branches.size(), lastRun));
        }
        return sb.toString();
    }

    private final static String baseOrgHtml =
            """
            <div class="flex-row" role="main" id="main">
                <fieldset style="display: inline-block">
                    <legend>Repos</legend>
                    <table style="border:1px" class="sortable">
                        <thead>
                        <tr class="heading">
                            <th>Name</th>
                            <th>Branches</th>
                            <th>Last Updated</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!--orgRepos-->
                        </tbody>
                    </table>
                </fieldset>
            </div>
            """;

    //******************** repo ********************//

    public static String getRepoHtml(String company, String org, String repo) {

        final CompanyOrgRepoBranchJobRun path = CompanyOrgRepoBranchJobRun
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
        final String main = baseRepoHtml.replace("<!--repoBranches-->", getRepoBranches(path, branchJobMap));
        return getPage(main, getBreadCrumb(path));
    }

    private static String getRepoBranches(CompanyOrgRepoBranchJobRun path, Map<Branch, Set<Job>> branchJobMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Branch, Set<Job>> entry : branchJobMap.entrySet()) {
            final Branch branch = entry.getKey();
            final Set<Job> jobs = entry.getValue();
            final Instant lastRun = mostRecent(jobs.stream().map(Job::getLastRun).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRun itemPath = path.toBuilder().branch(branch.getBranchName()).build();
            sb.append(getItemRow(itemPath, branch.getBranchName(), jobs.size(), lastRun));
        }
        return sb.toString();
    }

    private final static String baseRepoHtml =
            """
            <div class="flex-row" role="main" id="main">
                <fieldset style="display: inline-block">
                    <legend>Branches</legend>
                    <table style="border:1px" class="sortable">
                        <thead>
                            <tr class="heading">
                                <th>Name</th>
                                <th>Jobs</th>
                                <th>Last Updated</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!--repoBranches-->
                        </tbody>
                    </table>
                </fieldset>
                </div>
            """;
    //******************** branch ********************//

    public static String getBranchHtml(String company, String org, String repo, String branch) {

        final CompanyOrgRepoBranchJobRun path = CompanyOrgRepoBranchJobRun
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
        final String main = baseBranchHtml.replace("<!--jobRuns-->", getJobRuns(path, jobRunMap));
        return getPage(main, getBreadCrumb(path));
    }

    private static String getJobRuns(CompanyOrgRepoBranchJobRun path, Map<Job, Set<Run>> jobRunMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Job, Set<Run>> entry : jobRunMap.entrySet()) {
            final Job job = entry.getKey();
            final Set<Run> runs = entry.getValue();
            final Instant lastRun = mostRecent(runs.stream().map(Run::getCreated).collect(Collectors.toSet()));
            final CompanyOrgRepoBranchJobRun itemPath = path.toBuilder().jobId(job.getJobId()).build();
            sb.append(getItemRow(itemPath, Long.toString(job.getJobId()), runs.size(), lastRun));

        }
        return sb.toString();
    }

    private final static String baseBranchHtml =
            """
            <div class="flex-row" role="main" id="main">
                <fieldset style="display: inline-block">
                    <legend>Jobs</legend>
                    <table style="border:1px" class="sortable">
                        <thead>
                            <tr class="heading">
                                <th>Name</th>
                                <th>Runs</th>
                                <th>Last Updated</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!--jobRuns-->
                        </tbody>
                    </table>
                </fieldset>
                </div>
            """;

    //******************** job ********************//

    public static String getJobHtml(String company, String org, String repo, String branch, Long jobId) {

        final CompanyOrgRepoBranchJobRun path = CompanyOrgRepoBranchJobRun
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
        final String main = baseJobHtml.replace("<!--runStages-->", getRunStages(path, runStageMap));
        return getPage(main, getBreadCrumb(path));
    }

    private static String getRunStages(CompanyOrgRepoBranchJobRun path, Map<Run, Set<Stage>> runStageMap) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Run, Set<Stage>> entry : runStageMap.entrySet()) {
            final Run run = entry.getKey();
            final Set<Stage> stages = entry.getValue();

            final Instant lastRun = mostRecent(Set.of(run.getCreated()));
            final CompanyOrgRepoBranchJobRun itemPath = path.toBuilder().runId(run.getRunId()).build();
            sb.append(getItemRow(itemPath, Long.toString(run.getRunId()), stages.size(), lastRun));
        }
        return sb.toString();
    }

    private final static String baseJobHtml =
            """
            <div class="flex-row" role="main" id="main">
                <fieldset style="display: inline-block">
                    <legend>Runs</legend>
                    <table style="border:1px" class="sortable">
                        <thead>
                            <tr class="heading">
                                <th>Name</th>
                                <th>Runs</th>
                                <th>Last Updated</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!--runStages-->
                        </tbody>
                    </table>
                </fieldset>
                </div>
            """;
    //******************** run ********************//

//    private final static String baseRunHtml =
//            """
//            <div class="flex-row" role="main" id="main">
//                <fieldset style="display: inline-block">
//                    <legend>Runs</legend>
//                    <table style="border:1px" class="sortable">
//                        <thead>
//                            <tr class="heading">
//                                <th>Name</th>
//                                <th>Stages</th>
//                            </tr>
//                        </thead>
//                        <tbody>
//                            <!--stageRuns-->
//                        </tbody>
//                    </table>
//                </fieldset>
//                </div>
//            """;

    //******************** stage ********************//

    //******************** util ********************//

    private static List<Pair<String, String>> getBreadCrumb(CompanyOrgRepoBranchJobRun path) {
        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("home", getUrl(null)));

        if (path != null) {
            if (!StringUtils.isEmpty(path.getCompany())) {
                breadCrumbs.add(Pair.of(path.getCompany(),
                        getUrl(CompanyOrgRepoBranchJobRun.truncateCompany(path))));

                if (!StringUtils.isEmpty(path.getOrg())) {
                    breadCrumbs.add(Pair.of(path.getOrg(),
                            getUrl(CompanyOrgRepoBranchJobRun.truncateOrg(path))));

                    if (!StringUtils.isEmpty(path.getRepo())) {
                        breadCrumbs.add(Pair.of(path.getRepo(),
                                getUrl(CompanyOrgRepoBranchJobRun.truncateRepo(path))));

                        if (!StringUtils.isEmpty(path.getBranch())) {
                            breadCrumbs.add(Pair.of(path.getBranch(),
                                    getUrl(CompanyOrgRepoBranchJobRun.truncateBranch(path))));

                            if (path.getJobId() != null) {
                                breadCrumbs.add(Pair.of(String.valueOf(path.getJobId()),
                                        getUrl(CompanyOrgRepoBranchJobRun.truncateJob(path))));

                                if (path.getRunId() != null) {
                                    breadCrumbs.add(Pair.of(String.valueOf(path.getRunId()),
                                            getUrl(path)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return breadCrumbs;
    }

    private static String getItemRow(CompanyOrgRepoBranchJobRun path, String name, int size, Instant date) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>").append(ls)
          .append("  <td><a href=\"" + getUrl(path) + "\">" + name + "</a></td>").append(ls)
          .append("  <td class=\"count\">" + size + "</td>").append(ls);
        if (date != null) {
            sb.append("  <td>" + date.toString() + "</td>").append(ls);
        }
        sb.append("</tr>").append(ls);
        return sb.toString();
    }

    private static String getUrl(CompanyOrgRepoBranchJobRun path) {
        StringBuilder sb = new StringBuilder();
        sb.append("/v1/ui");

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
            <!--main-->

            <footer>
                <span>Reportcard </span>&nbsp;&nbsp;&nbsp;
                <a href="https://github.com/ericdriggs/reportcard">source</a>&nbsp;&nbsp;&nbsp;
                <span>version: 0.0.1</span>
            </footer>
            </body>
            </html>
            """;

}


