package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.controller.graph.trend.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.model.TestCaseModel;

import io.github.ericdriggs.reportcard.model.trend.CompanyOrgRepoBranchJobStageName;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.model.trend.TestPackageSuiteCase;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;
import io.github.ericdriggs.reportcard.util.StringMapUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.*;

import static io.github.ericdriggs.reportcard.util.NumberStringUtil.zeroPaddedSecond;

public class TrendHtmlHelper extends BrowseHtmlHelper {

    public static String renderTrendHtml(JobStageTestTrend jobStageTestTrend) {
        final String main = getTrendMainDiv(jobStageTestTrend);
        return getPage(main, getTrendBreadCrumb(jobStageTestTrend.toCompanyOrgRepoBranchJobRunStageDTO()))
                .replace("<body>", "<body onload=\"applyTestFilters()\">")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/trend.css\">" + ls + "<script src=\"/js/trend.js\"></script>" + ls);

    }

    public static String getTrendMainDiv(JobStageTestTrend jobStageTestTrend) {
        final CompanyOrgRepoBranchJobStageName companyOrgRepoBranchJobStageName = jobStageTestTrend.getCompanyOrgRepoBranchJobStageName();
        final TreeMap<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testCaseTrends = jobStageTestTrend.getTestCaseTrends();

        TestTrendTable testTrendTable = TestTrendTable.fromJob(jobStageTestTrend);
        return trendMainDiv.replace("<!--companyName-->", companyOrgRepoBranchJobStageName.getCompanyPojo().getCompanyName())
                .replace("<!--orgName-->", companyOrgRepoBranchJobStageName.getOrgPojo().getOrgName())
                .replace("<!--repoName-->", companyOrgRepoBranchJobStageName.getRepoPojo().getRepoName())
                .replace("<!--branchName-->", companyOrgRepoBranchJobStageName.getBranchPojo().getBranchName())
                .replace("<!--jobInfo-->", renderJobInfo(companyOrgRepoBranchJobStageName.getJobPojo().getJobInfo()))
                .replace("<!--stageName-->", companyOrgRepoBranchJobStageName.getStageName())
                .replace("<!--jobRunHeaders-->", renderJobRunHeaders(testTrendTable.getTestRunHeaders()))
                .replace("<!--jobRunTestRows-->", renderJobRunTestRows(testTrendTable.getTestCaseTrendRows()))
                ;
    }

    static String renderJobInfo(String jobInfo) {
        TreeMap<String, String> jobInfoMap = StringMapUtil.jsonToMap(jobInfo);

        boolean isFirst = true;
        StringBuilder builder = new StringBuilder();
        if (!CollectionUtils.isEmpty(jobInfoMap)) {
            for (Map.Entry<String, String> entry : jobInfoMap.entrySet()) {
                if (isFirst) {
                    builder.append("<span class=\"job-info\"><span class=\"job-info-term\">" + entry.getKey() + ":&nbsp;</span>&nbsp; " + entry.getValue() + " </span>").append(ls);
                    isFirst = false;
                } else {
                    builder.append("<div class=\"job-info\"><span class=\"job-info-term\">" + entry.getKey() + " :&nbsp;</span>" + entry.getValue() + "</div>").append(ls);
                }
            }
        }
        return builder.toString();
    }

    static String renderJobRunHeaders(Collection<TestRunHeader> testRunHeaders) {
        StringBuilder builder = new StringBuilder();

        for (TestRunHeader testRunHeader : testRunHeaders) {
            builder.append("<th title=\"" + testRunHeader.getRunDate().toString() + "\"><a class=\"run-link\" target=\"_blank\" href=\"" + testRunHeader.getRunUri() + "\">" + testRunHeader.getJobRunCount() + "</a></th>");
        }
        return builder.toString();
    }

    static String renderJobRunTestRows(List<TestCaseTrendRow> testCaseTrendRows) {

        Set<TestPackageSuiteCase> testsWithFailures = new TreeSet<>();
        Set<TestPackageSuiteCase> testWithSkip = new TreeSet<>();
        Set<Long> runIdsWithFailures = new TreeSet<>();
        Set<Long> runIdsWithSkip = new TreeSet<>();

        for (TestCaseTrendRow row : testCaseTrendRows) {
            for (TestCaseRunGroupedState col : row.getTestRunGroupedStates()) {
                if (col.getTestCaseRunState().isFail()) {
                    testsWithFailures.add(row.getTestPackageSuiteCase());
                    runIdsWithFailures.add(col.getRunId());
                }
            }
        }

        for (TestCaseTrendRow row : testCaseTrendRows) {
            if (row.isHasSkip()) {
                testWithSkip.add(row.getTestPackageSuiteCase());
                for (TestCaseRunGroupedState col : row.getTestRunGroupedStates()) {
                    if (col.getTestCaseRunState().isSkipped()) {
                        runIdsWithSkip.add(col.getRunId());
                    }
                }
            }
        }

        final String baseTestRow =
                """
                <tr class="test-row test-case-class">
                    <td><!--testPackageName--></td>
                    <td><!--testSuiteName--></td>
                    <td><!--testCaseName--></td>
                    <td class="fail-messages"><!--failMessages--></td>
                    <td class="fail-since"><!--failSince--></td>
                    <td><!--averageDuration--></td>
                    <!--passPercent-->
                    <!--runStates-->
                </tr>
                """;
        StringBuilder builder = new StringBuilder();

        for (TestCaseTrendRow testCaseTrendRow : testCaseTrendRows) {
            StringBuilder runStatesBuilder = new StringBuilder();

            for (TestCaseRunGroupedState testCaseRunGroupedState : testCaseTrendRow.getTestRunGroupedStates()) {
                final boolean runHasFail = runIdsWithFailures.contains(testCaseRunGroupedState.getRunId());
                final boolean runHasSkip = runIdsWithSkip.contains(testCaseRunGroupedState.getRunId());
                runStatesBuilder.append(renderTestRunState(testCaseRunGroupedState, runHasFail, runHasSkip));
            }

            final boolean hasFail = testsWithFailures.contains(testCaseTrendRow.getTestPackageSuiteCase());
            final boolean hasSkip = testWithSkip.contains(testCaseTrendRow.getTestPackageSuiteCase());

            builder.append(
                    baseTestRow
                            .replace("<!--testPackageName-->", TestPackageSuiteCase.getPackageName(testCaseTrendRow.getTestPackageSuiteCase()))
                            .replace("<!--testSuiteName-->", TestPackageSuiteCase.getTestSuiteName(testCaseTrendRow.getTestPackageSuiteCase()))
                            .replace("<!--testCaseName-->", TestPackageSuiteCase.getTestCaseName(testCaseTrendRow.getTestPackageSuiteCase()))
                            .replace("<!--failMessages-->", renderFailMessages(testCaseTrendRow.getFailureMessageIndexMap()))
                            .replace("<!--failSince-->", renderFailSince(testCaseTrendRow.getFailSince()))
                            .replace("<!--passPercent-->", renderSuccessPercent(testCaseTrendRow.getSuccessPercent()))
                            .replace("<!--averageDuration-->", zeroPaddedSecond(testCaseTrendRow.getAverageDurationSeconds()))
                            .replace("<!--runStates-->", runStatesBuilder.toString())
                            .replace("test-case-class", getTestCaseClass(hasFail, hasSkip))
            );
        }
        return builder.toString();
    }

    static String getTestCaseClass(boolean hasFailure, boolean hasSkip) {
        List<String> classes = new ArrayList<>();

        if (hasFailure) {
            classes.add("test-case-fail");
        } else {
            classes.add("test-case-success");
        }

        if (hasSkip) {
            classes.add("test-case-skip");
        }
        return String.join(" ", classes);
    }

    static String renderTestRunState(TestCaseRunGroupedState testCaseRunGroupedState, boolean runHasFail, boolean runHasSkip) {
        final TestCaseRunState runState = testCaseRunGroupedState.getTestCaseRunState();
        String ret = runState.name();
        switch (runState) {
            case SUCCESS -> ret = "<td class=\"stage-pass run-class\">S</td>" + ls;
            case SKIPPED -> ret = "<td class=\"stage-skip run-class\">-</td>" + ls;
            case FAIL ->
                    ret = "<td class=\"stage-fail run-class\">F<sub>" + testCaseRunGroupedState.getTestCaseRunStateGroup() + "</sub></td>" + ls;
        }
        return ret.replace("run-class", getRunClass(runHasFail, runHasSkip));
    }

    static String getRunClass(boolean hasFailure, boolean hasSkip) {
        List<String> classes = new ArrayList<>();

        if (hasFailure) {
            classes.add("run-fail");
        } else {
            classes.add("run-success");
        }

        if (hasSkip) {
            classes.add("run-skip");
        }
        return String.join(" ", classes);
    }

    static String renderSuccessPercent(BigDecimal successPercent) {
        if (SuccessAverage.isSuccess(successPercent)) {
            return "<td class=\"pass-percent stage-success\">" + successPercent.toPlainString() + "</td>";
        }
        return "<td class=\"pass-percent stage-fail\">" + successPercent.toPlainString() + "</td>";
    }

    static String renderFailSince(Instant failSince) {
        //use a hidden value so when sort failSince in ascending order the empty values will be at the bottom
        if (failSince == null) {
            return "<span style=\"opacity: 0\">z</span>";
        }
        return failSince.truncatedTo(ChronoUnit.SECONDS).toString();
    }

    static String renderFailMessages(FailureMessageIndexMap failureMessageIndexMap) {
        if (failureMessageIndexMap == null || CollectionUtils.isEmpty(failureMessageIndexMap.getIndexFailureMessageMap())) {
            return "-";
        }
        final String baseFailMessages =
                """
                <details >
                    <summary class="fail-messages">messages</summary>
                    <dl>
                         <!--failureMessageDefinitions-->
                    </dl>
                </details>
                """;

        StringBuilder builder = new StringBuilder();
        TreeMap<Integer, String> failureMessagesMap = failureMessageIndexMap.getIndexFailureMessageMap();
        for (Map.Entry<Integer, String> entry : failureMessagesMap.entrySet()) {
            builder.append("<dt>" + entry.getKey() + "</dt>").append(ls);
            builder.append("<dd>" + entry.getValue() + "</d>").append(ls);
        }
        return baseFailMessages.replace("<!--failureMessageDefinitions-->", builder.toString());
    }


    /*

     */

    static String trendMainDiv =
            """
            <div id="main">
                <details id="test-page-context" open="open" class="test-page-context">
                    <summary id="page-context-summary" class="page-context-summary ">Test trend context</summary>
                    <dl class="page-context">
                        <dt class="dt-page-context">company</dt>
                        <dd class="dd-page-context"><!--companyName--></dd>
                        <dt class="dt-page-context">org</dt>
                        <dd class="dd-page-context"><!--orgName--></dd>
                        <dt class="dt-page-context">repo</dt>
                        <dd class="dd-page-context"><!--repoName--></dd>
                        <dt class="dt-page-context">branch</dt>
                        <dd class="dd-page-context"><!--branchName--></dd>
                        <dt class="dt-page-context">jobInfo</dt>
                        <dd>
                            <!--jobInfo-->
                        </dd>
                        <dt class="dt-page-context">stage</dt>
                        <dd><!--stageName--></dd>
                    </dl>
                    <br>
                </details>

                <!--todo: implement js filter functions and display-->
                <fieldset style="display:inline-block;width=500px;" id="test-filters" display:none>
                    <legend>Test Filters</legend>

                    <label for="test-case-status-filter">Test Cases</label>
                    <select name="test-case-status-filter" id="test-case-status-filter" onchange="applyTestFilters()"   >
                        <option value="all">All</option>
                        <option value="fail" selected>Fail</option>
                        <option value="skip">Skip</option>
                        <option value="success">Success</option>
                    </select><br>

                    <label for="test-run-filter" style="display:none">Test Runs</label>
                    <select name="test-run-filter" id="test-run-filter" style="display:none">
                        <option value="all" selected>All</option>
                        <option value="fail">Fail</option>
                        <option value="skip">Skip</option>
                        <option value="success">Success</option>
                    </select>
                </fieldset>
                <br>

                <table class="sortable" style="vertical-align: top" id="test-case-trends">
                    <thead>
                    <tr class="test-row-header">
                        <th>Test Package</th>
                        <th>Test Suite</th>
                        <th style="min-width:300px">Test Case</th>
                        <th>Fail Messages</th>
                        <th style="min-width:131px">Fail Since</th>
                        <th>Avg Duration</th>
                        <th>Pass %</th>
                        <!--jobRunHeaders-->
                    </tr>
                    </thead>
                    <tbody>
                        <!--jobRunTestRows-->
                    </tbody>
                </table>
            </div>
            """;

    protected static List<Pair<String, String>> getTrendBreadCrumb(CompanyOrgRepoBranchJobRunStageDTO path) {
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
                            breadCrumbs.add(Pair.of(path.getBranch(),
                                    getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateBranch(path))));

                            if (path.getJobId() != null) {
                                final String jobUrl = getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateJob(path));
                                breadCrumbs.add(Pair.of("jobId: " + path.getJobId(), jobUrl));
                                if (path.getStageName() != null) {
                                    String stageUrl = jobUrl + "/stage/" + path.getStageName();
                                    //TODO: replace with stage view when controller at that path
                                    breadCrumbs.add(Pair.of("stage: " + path.getStageName(), "#"));
                                    breadCrumbs.add(Pair.of("trend", stageUrl + "/trend"));
                                }
                            }
                        }
                    }
                }
            }
        }
        return breadCrumbs;
    }

    public static URI getTrendURI(CompanyOrgRepoBranchJobRunStageDTO path) {
        if (path.getJobId() == null) {
            throw new NullPointerException("path.getJobId()");
        }
        if (path.getStageName() == null) {
            throw new NullPointerException("path.getStageName()");
        }

        final String jobUrl = getUrl(CompanyOrgRepoBranchJobRunStageDTO.truncateJob(path));
        final String stageUrl = jobUrl + "/stage/" + path.getStageName();
        return URI.create(stageUrl + "/trend");
    }
}