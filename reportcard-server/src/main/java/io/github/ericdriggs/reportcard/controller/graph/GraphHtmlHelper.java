package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.controller.graph.trend.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.trend.CompanyOrgRepoBranchJob;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.model.trend.TestPackageSuiteCase;
import io.github.ericdriggs.reportcard.util.StringMapUtil;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GraphHtmlHelper extends BrowseHtmlHelper {

    public static String renderHtml(JobStageTestTrend jobStageTestTrend) {
        final String main = getTrendMainDiv(jobStageTestTrend);
        return getPage(main, getBreadCrumb(jobStageTestTrend.toCompanyOrgRepoBranchJobRunStageDTO())).replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/trend.css\">");
    }

    public static String getTrendMainDiv(JobStageTestTrend jobStageTestTrend) {
        final CompanyOrgRepoBranchJob companyOrgRepoBranchJob = jobStageTestTrend.getCompanyOrgRepoBranchJob();
        final TreeMap<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testCaseTrends = jobStageTestTrend.getTestCaseTrends();

        TestTrendTable testTrendTable = TestTrendTable.fromJob(jobStageTestTrend);
        return trendMainDiv.replace("<!--companyName-->", companyOrgRepoBranchJob.getCompanyPojo().getCompanyName())
                           .replace("<!--orgName-->", companyOrgRepoBranchJob.getOrgPojo().getOrgName())
                           .replace("<!--repoName-->", companyOrgRepoBranchJob.getRepoPojo().getRepoName())
                           .replace("<!--branchName-->", companyOrgRepoBranchJob.getBranchPojo().getBranchName())
                           .replace("<!--jobInfo-->", renderJobInfo(companyOrgRepoBranchJob.getJobPojo().getJobInfo()))
                           .replace("<!--stageName-->", jobStageTestTrend.getStageName().getStageName())
                           .replace("<!--jobRunHeaders-->", renderJobRunHeaders(testTrendTable.getTestRunHeaders()))
                           .replace("<!--jobRunTestRows-->", renderJobRunTestRows(testTrendTable.getTestCaseTrendRows()))
                ;
    }

    static String renderJobInfo(String jobInfo) {
        TreeMap<String, String> jobInfoMap = StringMapUtil.stringToMap(jobInfo);

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
            builder.append("<th title=\"" + testRunHeader.getRunDate().toString() + "\"><a href=\"" + testRunHeader.getRunUri() + "\">" + testRunHeader.getJobRunCount() + "</a></th>");
        }
        return builder.toString();
    }

    static String renderJobRunTestRows(List<TestCaseTrendRow> testCaseTrendRows) {

        final String baseTestRow =
                """
                <tr class="test-row">
                    <td><!--testPackageName--></td>
                    <td><!--testSuiteName--></td>
                    <td><!--testCaseName--></td>
                    <td class="fail-messages"><!--failMessages--></td>
                    <td class="fail-since"><!--failSince--></td>
                    <!--avg30-->
                    <!--runStates-->
                </tr>
                """;
        StringBuilder builder = new StringBuilder();

        for (TestCaseTrendRow testCaseTrendRow : testCaseTrendRows) {
            StringBuilder runStatesBuilder = new StringBuilder();

            for (TestCaseRunGroupedState testCaseRunGroupedState : testCaseTrendRow.getTestRunGroupedStates()) {
                runStatesBuilder.append(renderTestRunState(testCaseRunGroupedState));
            }
            builder.append(
                    baseTestRow
                            .replace("<!--testPackageName-->", wrapNull(testCaseTrendRow.getTestPackage()))
                            .replace("<!--testSuiteName-->", wrapNull(testCaseTrendRow.getTestSuite()))
                            .replace("<!--testCaseName-->", wrapNull(testCaseTrendRow.getTestCase()))
                            .replace("<!--failMessages-->", renderFailMessages(testCaseTrendRow.getFailureMessageIndexMap()))
                            .replace("<!--failSince-->", renderFailSince(testCaseTrendRow.getFailSince()))
                            .replace("<!--avg30-->", renderSuccessPercent(testCaseTrendRow.getAvg30()))
                            .replace("<!--runStates-->", runStatesBuilder.toString())
            );
        }

        return builder.toString();
    }

    static String wrapNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    static String renderTestRunState(TestCaseRunGroupedState testCaseRunGroupedState) {
        final TestCaseRunState runState = testCaseRunGroupedState.getTestCaseRunState();
        String ret = runState.name();
        switch (runState) {
            case SUCCESS -> ret = "<td class=\"stage-pass\">S</td>" + ls;
            case SKIPPED -> ret = "<td class=\"stage-skip\">-</td>" + ls;
            case FAIL ->
                    ret = "<td class=\"stage-fail\">F<sub>" + testCaseRunGroupedState.getTestCaseRunStateGroup() + "</sub></td>" + ls;
        }
        return ret;
    }

    static String renderSuccessPercent(BigDecimal successPercent) {
        if (SuccessAverage.isSuccess(successPercent)) {
            return "<td class=\"pass-percent stage-success\">100%</td>";
        }
        return "<td class=\"pass-percent stage-fail\">" + successPercent.toPlainString() + "%</td>";
    }

    static String renderFailSince(Instant failSince) {
        if (failSince == null) {
            return "-";
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
                <details id="test-trend-context" open="open" class="test-trend-context">
                    <summary id="trend-context-summary" class="trend-context-summary ">Test trend context</summary>
                    <dl class="trend-context">
                        <dt class="dt-trend-context">company</dt>
                        <dd class="dd-trend-context"><!--companyName--></dd>
                        <dt class="dt-trend-context">org</dt>
                        <dd class="dd-trend-context"><!--orgName--></dd>
                        <dt class="dt-trend-context">repo</dt>
                        <dd class="dd-trend-context"><!--repoName--></dd>
                        <dt class="dt-trend-context">branch</dt>
                        <dd class="dd-trend-context"><!--branchName--></dd>
                        <dt class="dt-trend-context">jobInfo</dt>
                        <dd>
                            <!--jobInfo-->
                        </dd>
                        <dt class="dt-trend-context">stage</dt>
                        <dd><!--stageName--></dd>
                    </dl>
                    <br>
                </details>

                <!--todo: implement js filter functions and display-->
                <fieldset style="display:inline-block" id="test-filters" display:none>
                    <legend>Test Filters</legend>
                    <label for="test-suites">Suite</label>

                    <select name="test-suites" id="test-suites">
                        <option value="all" default>all</option>
                        <option value="Suite1">Suite1</option>
                        <option value="Suite1">Suite2</option>
                    </select>

                    <label for="test-classes">Classes</label>
                    <select name="test-classes" id="test-classes">
                        <option value="all" default>all</option>
                        <!--testClassOptions-->

                    </select>

                    <label for="test-classes">Cases</label>
                    <select name="test-cases" id="test-cases">
                        <option value="all" default>all</option>
                        <!--testCaseOptions-->
                    </select>

                    <label for="test-statuses">Status</label>
                    <select name="test-statuses" id="test-statuses">
                        <option value="all" default>all</option>
                        <option value="Fail">Fail</option>
                        <option value="Skip">Skip</option>
                        <option value="Success">Success</option>
                    </select>
                </fieldset>
                <br>

                <table class="sortable" style="vertical-align: top" id="test-case-trends">
                    <thead>
                    <tr class="test-row-header">
                        <th>Test Package</th>
                        <th>Test Suite</th>
                        <th>Test Case</th>
                        <th>Fail Messages</th>
                        <th>Fail Since</th>
                        <th>Avg(30)</th>
                        <!--jobRunHeaders-->
                    </tr>
                    </thead>
                    <tbody>
                        <!--jobRunTestRows-->
                    </tbody>
                </table>
            </div>
            """;
}