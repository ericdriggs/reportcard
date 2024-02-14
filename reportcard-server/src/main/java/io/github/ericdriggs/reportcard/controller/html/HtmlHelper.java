package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.cache.dto.CompanyName;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrg;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRun;
import io.github.ericdriggs.reportcard.cache.model.CompanyOrgsCache;
import io.github.ericdriggs.reportcard.cache.model.CompanyOrgsReposCacheMap;
import io.github.ericdriggs.reportcard.cache.model.OrgReposBranchesCacheMap;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Set;

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
            sb.append("<tr>").append(ls)
              .append("  <td><a href=\"" + getUrl(CompanyOrgRepoBranchJobRun.builder().company(company.getCompanyName()).build()) + "\">"
                      + company.getCompanyName() + "</a></td>").append(ls)
              .append("  <td class=\"count\">" + orgs.size() + "</td>").append(ls)
              .append("</tr>").append(ls);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found: " + company);
        }
        if (companyOrgReposMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one company found with same name: " + company);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Org not found: " + company + "/" + org);
        }
        if (orgRepoBranchMap.size() != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "More than one org for: " + company + "/" + org);
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
            sb.append("<tr>").append(ls)
              .append("  <td><a href=\"" + path.toBuilder().repo(repo.getRepoName()).build() + "\">" + repo.getRepoName() + "</a></td>").append(ls)
              .append("  <td class=\"count\">" + branches.size() + "</td>").append(ls)
              .append("</tr>").append(ls)
            ;
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

    //******************** branch ********************//

    //******************** job ********************//

    //******************** run ********************//

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

    private static String getUrl(CompanyOrgRepoBranchJobRun path) {
        StringBuilder sb = new StringBuilder();
        sb.append("/v1/ui/");

        if (path != null) {
            if (!StringUtils.isEmpty(path.getCompany())) {
                sb.append("company/" + path.getCompany());

                if (!StringUtils.isEmpty(path.getOrg())) {
                    sb.append("org/" + path.getOrg());

                    if (!StringUtils.isEmpty(path.getRepo())) {
                        sb.append("repo/" + path.getRepo());

                        if (!StringUtils.isEmpty(path.getBranch())) {
                            sb.append("branch/" + path.getBranch());

                            if (path.getJobId() != null) {
                                sb.append("job/" + path.getJobId());

                                if (path.getRunId() != null) {
                                    sb.append("run/" + path.getRunId());
                                }
                            }
                        }
                    }
                }
            }
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

    private final static String basePage =
            """
            <html lang="en">
            <head>
                <link rel="stylesheet" href="css/shared.css">
                <link rel="stylesheet" href="css/sortable.min.css"/>
                <script src="js/sortable.min.js"></script>
                <title>ReportCard</title>
            </head>
            <body>
            <header><img alt="reportcard logo" src="image/clipboard-check.svg" width="40px" height="40px" style="vertical-align: middle"> <span class="logo-text">ReportCard</span></header>
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


