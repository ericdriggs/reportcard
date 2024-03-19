package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.time.Duration;
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

    public static String getTestResult(TestResult testResult) {

        final String baseTestResultHtml =
                """
                <html>
                <head>
                    <link rel="stylesheet" href="/css/sortable.min.css"/>
                    <script src="/js/sortable.min.js"></script>
                    
                    <link rel="stylesheet" href="/css/shared.css">
                    
                    <link rel="stylesheet" href="/css/test-result.css"/>
                    <link rel="stylesheet" href="/js/test-result.js"/>
                </head>
                <div id="content">
                    <div id="overview" class="section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Overview</h2>
                        <table class="classes show">
                            <thead>
                            <tr>
                                <th>Class</th>
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
                        <ul class="show">
                            <!--failureItems-->
                        </ul>
                    </div>
                    <div id="skipped" class="skipped section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Skipped</h2>
                        <ul class="show">
                            <!--skippedItems-->
                        </ul>
                    </div>
                    <div id="classes" class="section">
                        <h2 class="toggle" dataref="#" onclick="toggleSiblings(this)">Classes</h2>
                        <!--classRows-->
                    </div>
                </html>
                """;

        Map<TestSuite, List<TestCase>> failed = testResult.getTestCasesErrorOrFailure();
        Map<TestSuite, List<TestCase>> skipped = testResult.getTestCasesSkipped();

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

    public static String getOverviewClassRows(TestResult testResult) {

        StringBuilder sb = new StringBuilder();
        ResultCount totalResultCount = ResultCount.builder().build();
        for (TestSuite testSuite : testResult.getTestSuites()) {
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
                    <td>{tests}</td>
                    <td>{failure}</td>
                    <td>{skipped}</td>
                    <td>{duration}</td>
                    <td>{successRate}</td>
                </tr>
                """;

        final String statusClass = getStatusClass(resultCount.getTestStatus());
        return baseTestClassHtml
                .replace("{statusClass}", statusClass)
                .replace("{testSuiteName}", testSuiteName)
                .replace("{tests}", Integer.toString(resultCount.getTests()))
                .replace("{failure}", Integer.toString(resultCount.getErrorsAndFailures()))
                .replace("{skipped}", Integer.toString(resultCount.getSkipped()))
                .replace("{duration}", Duration.ofSeconds(resultCount.getTime().toBigInteger().longValue()).toString())
                .replace("{successRate}", Integer.toString(resultCount.getPassedPercent().toBigInteger().intValue()));
    }

    public static String getOverviewTotalRow(ResultCount resultCount) {
        final String baseTestClassTotalHtml =
                """
                <tr>
                    <td>Total</td>
                    <td>{tests}</td>
                    <td>{failure}</td>
                    <td>{skipped}</td>
                    <td>{duration}</td>
                    <td>{successRate}</td>
                </tr>
                """;

        final String statusClass = getStatusClass(resultCount.getTestStatus());
        return baseTestClassTotalHtml
                .replace("{tests}", Integer.toString(resultCount.getTests()))
                .replace("{failure}", Integer.toString(resultCount.getErrorsAndFailures()))
                .replace("{skipped}", Integer.toString(resultCount.getSkipped()))
                .replace("{duration}", Duration.ofSeconds(resultCount.getTime().toBigInteger().longValue()).toString())
                .replace("{successRate}", Integer.toString(resultCount.getPassedPercent().toBigInteger().intValue()));
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
    public static String getFailures(Map<TestSuite, List<TestCase>> testSuiteCasesMap) {
        final String baseFailureItem =
                """
                <li>
                  <a onclick="showChildrenById('{testSuiteName}')" href="#{testSuiteName}">{testName}</a>
                </li>
                """;

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TestSuite, List<TestCase>> entry : testSuiteCasesMap.entrySet()) {
            final TestSuite testSuite = entry.getKey();
            final List<TestCase> testCases = entry.getValue();
            for (TestCase testCase : testCases) {
                sb.append(baseFailureItem
                        .replace("{testSuiteName}", testSuite.getName())
                        .replace("{testName}", testCase.getName())
                ).append(ls);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("DuplicatedCode")
    public static String getSkipped(Map<TestSuite, List<TestCase>> testSuiteCasesMap) {
        final String baseSkippedItem = "<li>{testSuiteName}.{testName}</li>";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TestSuite, List<TestCase>> entry : testSuiteCasesMap.entrySet()) {
            final TestSuite testSuite = entry.getKey();
            final List<TestCase> testCases = entry.getValue();
            for (TestCase testCase : testCases) {
                sb.append(baseSkippedItem
                        .replace("{testSuiteName}", testSuite.getName())
                        .replace("{testName}", testCase.getName())
                ).append(ls);
            }
        }
        return sb.toString();
    }

    public static String getClassRows(TestResult testResult) {

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
                        <!--testCaseRows-->
                    </table>
                    <!--failureMessages-->
                    <!--stdErr-->
                    <!--stdOut-->
                </div>
                """;

        StringBuilder sb = new StringBuilder();
        for (TestSuite testSuite : testResult.getTestSuites()) {
            final List<TestCase> testCases = testSuite.getTestCases();
            sb.append(baseClassRow
                    .replace("<!--testCaseRows-->", getTestCaseRows(testSuite, testCases))
                    .replace("<!--failureMessages-->", getFailureMessages(testSuite))
                    .replace("<!--stdErr-->", getStdErr(testSuite))
                    .replace("<!--stdOut-->", getStdOut(testSuite))
            ).append(ls);
        }
        return sb.toString();
    }

    public static String getTestCaseRows(TestSuite testSuite, List<TestCase> testCases) {

        final String baseTestCaseRow =
                """
                <tr class="{statusClass}" class="{testSuiteName}">
                    <td>{testName}</td>
                    <td>{duration}</td>
                    <td>{statusClass}</td>
                </tr>
                """;

        StringBuilder sb = new StringBuilder();
        for (TestCase testCase : testCases) {
            sb.append(baseTestCaseRow
                    .replace("{testSuiteName}", testSuite.getName())
                    .replace("{testName}", testCase.getName())
                    .replace("{statusClass", getStatusClass(testCase.getTestStatus()))
            );
        }
        return sb.toString();
    }

    public static String getFailureMessages(TestSuite testSuite) {
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
                    <a href="#" onclick="toggleSiblings(this)"><!--testSuiteName--></a>
                    <!--faultMessage-->
                    <!--faultType-->
                    <pre class="failure-message show">
                        <!--faultValue-->
                    </pre>
                </li>
                """;

        for (TestCase testCase : testSuite.getTestCases()) {
            if (testCase.hasTestFault()) {
                for (TestCaseFault testCaseFault : testCase.getTestCaseFaults()) {
                    failureMessages.append(
                            baseFailureMessage
                                    .replace("<!--faultMessage-->", "message: " + testCaseFault.getMessage() + "<br/>")
                                    .replace("<!--faultType-->", "type: " + testCaseFault.getType() + "<br/>")
                                    .replace("<!--faultValue-->", testCaseFault.getValue())
                    );
                }

            }
        }
        return baseFailureMessages.replace("<!--failureMessages-->", failureMessages.toString());
    }

    public static String getStdErr(TestSuite testSuite) {
        StringBuilder sb = new StringBuilder();

        if (testSuite.getSystemErr() != null) {
            sb.append(testSuite.getName() + ": <br/>" + testSuite.getSystemErr());
        }

        for (TestCase testCase : testSuite.getTestCases()) {
            if (testCase.getSystemErr() != null) {
                sb.append(testSuite.getName() + "." + testCase.getName() + ": <br/>" + testSuite.getSystemErr());
            }
        }
        return baseStdErr.replace("<!--stdErrText-->",sb.toString());
    }

    public static String getStdOut(TestSuite testSuite) {
        StringBuilder sb = new StringBuilder();

        if (testSuite.getSystemOut() != null) {
            sb.append(testSuite.getName() + ": <br/>" + testSuite.getSystemOut());
        }

        for (TestCase testCase : testSuite.getTestCases()) {
            if (testCase.getSystemOut() != null) {
                sb.append(testSuite.getName() + "." + testCase.getName() + ": <br/>" + testSuite.getSystemOut());
            }
        }
        return baseStdOut.replace("<!--stdOutText-->",sb.toString());
    }



    public static final String baseStdErr =
            """
            <div class="stderr button-wrapper hide">
                <button class="togglebutton" onclick="toggleSiblings(this)">Standard error</button>
                <pre class="stderr hide">
                    <!--stdErrText-->
                </pre>
            </div>
            """;
    public static final String baseStdOut =
            """
            <div class="stdout button-wrapper hide">
                <button class="togglebutton" onclick="toggleSiblings(this)">Standard out</button>
                <pre class="stdout hide">
                    <!--stdOutText-->
                </pre>
            </div>
            """;
    /*
    <div class="show" id="com.foo.qe.metrics.OpenApiCoverageMetricsPublisherTest" class="skipped">
                        <a href="#" class="testclass skipped" onclick="toggleSiblings(this)">com.foo.qe.metrics.OpenApiCoverageMetricsPublisherTest</a>
                        <table class="testcases hide">
                            <thead>
                            <tr>
                                <th>Test</th>
                                <th>Duration</th>
                                <th>Result</th>
                            </tr>
                            </thead>
                            <tr class="skipped" class="com.foo.FooTest">
                                <td>publishCoverageMetricsPostTest()</td>
                                <td>0s</td>
                                <td>skipped</td>
                            </tr>
                        </table>
                    </div>
                    <div class="show" id="com.foo.qe.metrics.TestMetricsPublisherTest" class="passed">
                        <a href="#" class="testclass passed" onclick="toggleSiblings(this)">com.foo.qe.metrics.TestMetricsPublisherTest</a>
                        <table class="testcases hide">
                            <thead>
                            <tr>
                                <th>Test</th>
                                <th>Duration</th>
                                <th>Result</th>
                            </tr>
                            </thead>
                            <tr class="passed" class="com.foo.FooTest">
                                <td>getTestResultTest()</td>
                                <td>0s</td>
                                <td>passed</td>
                            </tr>
                            <tr class="skipped" class="com.foo.FooTest">
                                <td>publishTestMetricsTest()</td>
                                <td>0s</td>
                                <td>skipped</td>
                            </tr>
                        </table>
                    </div>
                    <div class="show" id="com.foo.coverage.PathRegexUtilTest" class="success">
                        <a href="#" class="testclass success" onclick="toggleSiblings(this)">com.foo.coverage.PathRegexUtilTest</a>
                        <table class="testcases hide">
                            <thead>
                            <tr>
                                <th>Test</th>
                                <th>Duration</th>
                                <th>Result</th>
                            </tr>
                            </thead>
                            <tr class="success" class="com.foo.FooTest">
                                <td>longPathRegexTest</td>
                                <td>0s</td>
                                <td>passed</td>
                            </tr>
                            <tr class="passed" class="com.foo.FooTest">
                                <td>shortPathRegexTest</td>
                                <td>0s</td>
                                <td>passed</td>
                            </tr>
                        </table>
                    </div>
                    <div class="show" id="com.foo.FooTest" class="failures">
                        <a href="#" class="testclass failures" onclick="toggleSiblings(this)">com.foo.FooTest</a>
                        <table class="testcases hide">
                            <thead>
                            <tr>
                                <th>Test</th>
                                <th>Duration</th>
                                <th>Result</th>
                            </tr>
                            </thead>
                            <tr class="failures" class="com.foo.FooTest">
                                <td>failTest</td>
                                <td>0.262s</td>
                                <td>failed</td>
                            </tr>
                            <tr class="passed" class="com.foo.FooTest">
                                <td>passTest</td>
                                <td>0.262s</td>
                                <td>passed</td>
                            </tr>
                            <tr class="skipped" class="com.foo.FooTest">
                                <td>skipTest</td>
                                <td>0.262s</td>
                                <td>skipped</td>
                            </tr>
                        </table>
                        <div class="failures button-wrapper hide">
                            <button class="togglebutton" onclick="toggleSiblings(this)">Failure messages</button>
                            <ul class="hide">
                                <li class="failures">
                                    <a href="#" onclick="toggleSiblings(this)">com.foo.FooTest</a>
                                    <pre class="failure-message show">org.opentest4j.AssertionFailedError: expected: &lt;a&gt; but was: &lt;b&gt;
            	at app//org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:55)
            	at app//org.junit.jupiter.api.AssertionUtils.failNotEqual(AssertionUtils.java:62)
            	at app//org.junit.jupiter.api.AssertEquals.assertEquals(AssertEquals.java:182)
            	at app//org.junit.jupiter.api.AssertEquals.assertEquals(AssertEquals.java:177)
            	at app//org.junit.jupiter.api.Assertions.assertEquals(Assertions.java:1141)
            	at app//com.foo.FooTest.failTest(FooTest.java:17)
            				</pre>
                                </li>
                            </ul>
                        </div>
                        <div class="stderr button-wrapper hide">
                            <button class="togglebutton" onclick="toggleSiblings(this)">Standard error</button>
                            <pre class="stderr hide">13:50:12.083 [Test worker] INFO  com.foo.FooTest - stderr entry 1
            13:51:12.083 [Test worker] INFO  com.foo.FooTest - stderr entry 2
            			</pre>
                        </div>
                        <div class="stdout button-wrapper hide">
                            <button class="togglebutton" onclick="toggleSiblings(this)">Standard out</button>
                            <pre class="stdout hide">13:50:12.083 [Test worker] INFO  com.foo.FooTest - stdout entry 1
            			</pre>
                        </div>
                    </div>
     */

}
