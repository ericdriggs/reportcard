package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.util.*;


public enum TestResultHtmlHelper {

    ;//static methods only

    private final static String ls = System.getProperty("line.separator");
//<!-- TODO: fix purple link, make visited darker if needed -->
//<!-- TODO: skipped links -->
//<!-- TODO: sort skipped alphabetically -->
//<!-- TODO: skipped tests should match class tests -->
//<!-- TODO: classes table space between lines -->
//<!-- TODO: space above and below classes detail table -->
//<!-- TODO: failed and skipped similar space vertically to overview -->
//<!-- TODO: testcase failure detail should default to just message (summary) and not full contents.-->

    public static String getTestResult(TestResultModel testResult) {

        final String baseTestResultHtml =
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <link rel="stylesheet" href="/css/sortable.min.css"/>
                    <script src="/js/sortable.min.js"></script>
                    
                    <link rel="stylesheet" href="/css/shared.css">
                    
                    <link rel="stylesheet" href="/css/test-result.css"/>
                    <script src="/js/test-result.js"></script>
                </head>
                <div id="content">
                    <div id="overview" class="section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Overview</h2>
                        <table class="classes show">
                            <thead>
                            <tr>
                                <th>Test Suite (Class)</th>
                                <th>Tests</th>
                                <th>Failure</th>
                                <th>skipped</th>
                                <th>Duration</th>
                                <th>Success rate</th>
                            </tr>
                            </thead>
                            <tbody>
                            <!--overviewClassRows-->
                            
                            </tbody>
                        </table>
                    </div>
                    <div id="failures" class="failures section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Failed</h2>
                        <ul class="show indent">
                            <!--failureItems-->
                        </ul>
                    </div>
                    <div id="skipped" class="skipped section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Skipped</h2>
                        <ul class="show indent">
                            <!--skippedItems-->
                        </ul>
                    </div>
                    <div id="classes" class="section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Test Suites (Classes)</h2>
                        <!--classRows-->
                    </div>
                </html>
                """;

        Map<TestSuiteModel, List<TestCaseModel>> failed = testResult.getTestCasesErrorOrFailure();
        Map<TestSuiteModel, List<TestCaseModel>> skipped = testResult.getTestCasesSkipped();

        final String overviewClassRows = getOverviewClassRows(testResult);
        final String failedItems = getFailures(failed);
        final String skippedItems = getSkipped(skipped);

        final String classRows = getClassRows(testResult);

        return baseTestResultHtml
                .replace("<!--overviewClassRows-->", overviewClassRows)
                .replace("<!--failureItems-->", failedItems)
                .replace("<!--skippedItems-->", skippedItems)
                .replace("<!--classRows-->", classRows);
    }

    public static String getOverviewClassRows(TestResultModel testResult) {

        StringBuilder sb = new StringBuilder();
        ResultCount totalResultCount = ResultCount.builder().build();
        for (TestSuiteModel testSuite : testResult.getTestSuites()) {
            ResultCount resultCount = testSuite.getResultCount();
            sb.append(getOverviewClassRow(testSuite.getName(), resultCount)).append(ls);
            totalResultCount = ResultCount.add(totalResultCount, resultCount);
        }
        sb.append(getOverviewTotalRow(totalResultCount));
        return sb.toString();
    }

    public static String getOverviewClassRow(String testSuiteName, ResultCount resultCount) {

        final String baseTestClassHtml =
                """
                <tr class="{statusClass}">
                    <td><a onclick="showChildrenById('{testSuiteName}')" href="#{testSuiteName}">{testSuiteName}</a></td>
                    <td class="number">{tests}</td>
                    <td class="number">{failure}</td>
                    <td class="number">{skipped}</td>
                    <td class="number">{duration}</td>
                    <td class="number">{successRate}</td>
                </tr>
                """;

        final String statusClass = getStatusClass(resultCount.getTestStatus());
        return baseTestClassHtml
                .replace("{statusClass}", statusClass)
                .replace("{testSuiteName}", testSuiteName)
                .replace("{tests}", Integer.toString(resultCount.getTests()))
                .replace("{failure}", Integer.toString(resultCount.getErrorsAndFailures()))
                .replace("{skipped}", Integer.toString(resultCount.getSkipped()))
                .replace("{duration}", NumberStringUtil.fromSecondBigDecimal(resultCount.getTime()))
                .replace("{successRate}", NumberStringUtil.percentFromBigDecimal(resultCount.getPassedPercent()));
    }

    public static String getOverviewTotalRow(ResultCount resultCount) {
        final String baseTestClassTotalHtml =
                """
                <tr>
                    <td class="number">Total</td>
                    <td class="number">{tests}</td>
                    <td class="number">{failure}</td>
                    <td class="number">{skipped}</td>
                    <td class="number">{duration}</td>
                    <td class="number">{successRate}</td>
                </tr>
                """;

        final String statusClass = getStatusClass(resultCount.getTestStatus());
        return baseTestClassTotalHtml
                .replace("{tests}", NumberStringUtil.toString(resultCount.getTests()))
                .replace("{failure}", NumberStringUtil.toString(resultCount.getErrorsAndFailures()))
                .replace("{skipped}", NumberStringUtil.toString(resultCount.getSkipped()))
                .replace("{duration}", NumberStringUtil.fromSecondBigDecimal(resultCount.getTime()))
                .replace("{successRate}", NumberStringUtil.percentFromBigDecimal(resultCount.getPassedPercent()));
    }

    static String getStatusClass(TestStatus testStatus) {
        if (testStatus.isErrorOrFailure()) {
            return "failures";
        } else if (testStatus.isSkipped()) {
            return "skipped";
        } else {
            return "success";
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static String getFailures(Map<TestSuiteModel, List<TestCaseModel>> testSuiteCasesMap) {
        final String baseFailureItem =
                """
                <li>
                  <a onclick="showChildrenById('{testSuiteName}')" href="#{testSuiteName}">{testName}</a>
                </li>
                """;

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TestSuiteModel, List<TestCaseModel>> entry : testSuiteCasesMap.entrySet()) {
            final TestSuiteModel testSuite = entry.getKey();
            final List<TestCaseModel> testCases = entry.getValue();
            for (TestCaseModel testCase : testCases) {
                sb.append(baseFailureItem
                        .replace("{testSuiteName}", testSuite.getName())
                        .replace("{testName}", testCase.getName())
                ).append(ls);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("DuplicatedCode")
    public static String getSkipped(Map<TestSuiteModel, List<TestCaseModel>> testSuiteCasesMap) {
        final String baseSkippedItem = "<li>{testSuiteName}.{testName}</li>";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TestSuiteModel, List<TestCaseModel>> entry : testSuiteCasesMap.entrySet()) {
            final TestSuiteModel testSuite = entry.getKey();
            final List<TestCaseModel> testCases = entry.getValue();
            for (TestCaseModel testCase : testCases) {
                sb.append(baseSkippedItem
                        .replace("{testSuiteName}", testSuite.getName())
                        .replace("{testName}", testCase.getName())
                ).append(ls);
            }
        }
        return sb.toString();
    }

    public static String getClassRows(TestResultModel testResult) {

        final String baseClassRow =
                """
                <div class="show" id="{testSuiteName}" class="{statusClass}">
                    <a href="#" class="testclass {statusClass}" onclick="toggleSiblings(this)">{testSuiteName}</a>
                    <table class="testcases hide">
                        <thead>
                        <tr>
                            <th>Test</th>
                            <th>Duration</th>
                            <th>Result</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!--testCaseRows-->
                        </tbody>
                    </table>
                    <!--failureMessages-->
                    <!--stdErr-->
                    <!--stdOut-->
                </div>
                """;

        StringBuilder sb = new StringBuilder();
        for (TestSuiteModel testSuite : testResult.getTestSuites()) {
            final List<TestCaseModel> testCases = testSuite.getTestCases();
            sb.append(baseClassRow
                    .replace("<!--testCaseRows-->", getTestCaseRows(testSuite, testCases))
                    .replace("<!--failureMessages-->", getFailureMessages(testSuite))
                    .replace("<!--stdErr-->", getStdErr(testSuite))
                    .replace("<!--stdOut-->", getStdOut(testSuite))
                    .replace("{testSuiteName}", testSuite.getName())
                    .replace("{statusClass}", getStatusClass(testSuite.getTestStatus()))
            ).append(ls);
        }
        return sb.toString();
    }

    public static String getTestCaseRows(TestSuiteModel testSuite, List<TestCaseModel> testCases) {

        final String baseTestCaseRow =
                """
                <tr class="{statusClass}" class="{testSuiteName}">
                    <td>{testName}</td>
                    <td>{duration}</td>
                    <td>{statusClass}</td>
                </tr>
                """;

        StringBuilder sb = new StringBuilder();
        for (TestCaseModel testCase : testCases) {
            sb.append(baseTestCaseRow
                    .replace("{testSuiteName}", testSuite.getName())
                    .replace("{testName}", testCase.getName())
                    .replace("{duration}", NumberStringUtil.fromSecondBigDecimal(testCase.getTime()))
                    .replace("{statusClass}", getStatusClass(testCase.getTestStatus()))
            );
        }
        return sb.toString();
    }

    public static String getFailureMessages(TestSuiteModel testSuite) {

        final List<TestCaseModel> testCasesWithFaults = testSuite.getTestCasesWithFaults();
        if (testCasesWithFaults.isEmpty()) {
            return "";
        }
        StringBuilder failureMessages = new StringBuilder();

        final String baseFailureMessages =
                """
                <div class="{statusClass} button-wrapper hide">
                        <button class="togglebutton" onclick="toggleSiblings(this)">Failure messages</button>
                        <ul class="hide">
                            <!--failureMessages-->
                        </ul>
                    </div>
                """;

        final String baseFailureMessage =
                """
                <li class="failures">
                    <a href="#{testCaseName}" onclick="toggleSiblings(this)"><!--testSuiteName--></a>
                    <!--faultMessage-->
                    <!--faultType-->
                    <pre class="failure-message show">
                        <!--faultValue-->
                    </pre>
                </li>
                """;

        for (TestCaseModel testCase : testCasesWithFaults) {
            for (TestCaseFaultModel testCaseFault : testCase.getTestCaseFaults()) {
                failureMessages.append(
                        baseFailureMessage
                                .replace("<!--faultMessage-->", "message: " + testCaseFault.getMessage() + "<br/>")
                                .replace("<!--faultType-->", "type: " + testCaseFault.getType() + "<br/>")
                                .replace("<!--faultValue-->", testCaseFault.getValue())
                                .replace("<!--testSuiteName-->", testSuite.getName())
                                .replace("{testCaseName}", testCase.getName())
                );
            }
        }

        return baseFailureMessages.replace("<!--failureMessages-->", failureMessages.toString());
    }

    public static String getStdErr(TestSuiteModel testSuite) {

        final String baseStdErr =
                """
                <div class="stderr button-wrapper hide">
                    <button class="togglebutton" onclick="toggleSiblings(this)">Standard error</button>
                    <pre class="stderr hide">
                        <!--stdErrText-->
                    </pre>
                </div>
                """;

        StringBuilder sb = new StringBuilder();

        if (testSuite.getSystemErr() != null) {
            sb.append(testSuite.getName() + ": <br/>" + testSuite.getSystemErr());
        }

        for (TestCaseModel testCase : testSuite.getTestCases()) {
            if (testCase.getSystemErr() != null) {
                sb.append(testSuite.getName() + "." + testCase.getName() + ": <br/>" + testSuite.getSystemErr());
            }
        }
        if (sb.isEmpty()) {
            return "";
        }
        return baseStdErr.replace("<!--stdErrText-->",sb.toString());
    }

    public static String getStdOut(TestSuiteModel testSuite) {

        final String baseStdOut =
                """
                <div class="stdout button-wrapper hide">
                    <button class="togglebutton" onclick="toggleSiblings(this)">Standard out</button>
                    <pre class="stdout hide">
                        <!--stdOutText-->
                    </pre>
                </div>
                """;
        StringBuilder sb = new StringBuilder();

        if (testSuite.getSystemOut() != null) {
            sb.append(testSuite.getName() + ": <br/>" + testSuite.getSystemOut());
        }

        for (TestCaseModel testCase : testSuite.getTestCases()) {
            if (testCase.getSystemOut() != null) {
                sb.append(testSuite.getName() + "." + testCase.getName() + ": <br/>" + testSuite.getSystemOut());
            }
        }
        if (sb.isEmpty()) {
            return "";
        }
        return baseStdOut.replace("<!--stdOutText-->",sb.toString());
    }


}
