package io.github.ericdriggs.reportcard.controller.graph.trend;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.model.TestCaseFaultModel;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.model.trend.TestPackageSuiteCase;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Builder
@Jacksonized
@Value
public class TestTrendTable {
    TreeSet<TestRunHeader> testRunHeaders;
    List<TestCaseTrendRow> testCaseTrendRows;

    public static TestTrendTable fromJob(JobStageTestTrend jobStageTestTrend) {

        TreeMap<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testCaseTrends = jobStageTestTrend.getTestCaseTrends();
        TreeSet<TestRunHeader> testRunHeaders = new TreeSet<>();

        List<TestCaseTrendRow> testCaseTrendRows = new ArrayList<>();
        boolean isFirst = true;

        for (Map.Entry<TestPackageSuiteCase, TreeMap<RunPojo, TestCaseModel>> testSuiteEntry : testCaseTrends.entrySet()) {
            final TestPackageSuiteCase testPackageSuiteCase = testSuiteEntry.getKey();
            final String testPackageName = testPackageSuiteCase.getTestPackageName();
            final String testSuiteName = testPackageSuiteCase.getTestSuiteName();
            final String testCaseName = testPackageSuiteCase.getTestCaseName();

            final TreeMap<RunPojo, TestCaseModel> runTestCaseMap = testSuiteEntry.getValue();

            if (!CollectionUtils.isEmpty(runTestCaseMap)) {

                SuccessAverage success30 = new SuccessAverage(30);
                SuccessAverage successTotal = new SuccessAverage(runTestCaseMap.size());

                FailureMessageIndexMap failureMessageIndexMap = FailureMessageIndexMap.builder().build();
                TreeSet<TestCaseRunGroupedState> testRunGroupedStates = new TreeSet<>();
                boolean hasSkip = false;
                Map<TestPackageSuiteCase,Instant> testCaseFailSinceMap = new HashMap<>();

                Instant failSince = null;
                boolean hasPassed = false;
                BigDecimal totalDuration = BigDecimal.ZERO;
                for (Map.Entry<RunPojo, TestCaseModel> runEntry : runTestCaseMap.entrySet()) {
                    final RunPojo runPojo = runEntry.getKey();
                    final TestCaseModel testCaseModel = runEntry.getValue();
                    if (isFirst) {
                        testRunHeaders.add(TestRunHeader.fromRunPojo(jobStageTestTrend.getCompanyOrgRepoBranchJobStageName(), runPojo));
                    }
                    final TestStatus testStatus = TestStatus.fromStatusId(testCaseModel.getTestStatusFk());

                    successTotal.incrementTotalCount();
                    if (testStatus.isSuccess()) {
                        successTotal.incrementSuccessCount();
                        hasPassed = true;
                    } else if (testStatus.isSkipped()) {
                        hasSkip = true;
                    }

                    if (success30.getTotalCount() < success30.getMaxCount()) {
                        if (testStatus.isSuccess()) {
                            success30.incrementSuccessCount();
                        }
                        success30.incrementTotalCount();
                    }

                    if (testStatus.isErrorOrFailure() ) {
                        //runs are in descending order so failSince should only be updated until hasPassed
                        if (!hasPassed) {
                            failSince = runPojo.getRunDate();
                        }
                    }

                    final BigDecimal testRunDuration = testCaseModel.getTime() == null ? BigDecimal.ZERO : testCaseModel.getTime();
                    totalDuration = totalDuration.add(testRunDuration);

                    final String failureMessage = getTruncatedFailureMessage(testCaseModel.getTestCaseFaults());
                    testRunGroupedStates.add(TestCaseRunGroupedState.factory(runPojo.getRunId(), testStatus, failureMessageIndexMap, failureMessage));
                }

                final BigDecimal averageDurationSeconds = runTestCaseMap.isEmpty() ? BigDecimal.ZERO : totalDuration.divide(new BigDecimal(runTestCaseMap.size()), RoundingMode.HALF_UP).setScale(0, RoundingMode.HALF_UP);

                isFirst = false;

                testCaseTrendRows.add(TestCaseTrendRow
                        .builder()
                        .successPercent(successTotal.successPercent())
                        .averageDurationSeconds(averageDurationSeconds)
                        .hasSkip(hasSkip)
                        .failureMessageIndexMap(failureMessageIndexMap)
                        .testPackageSuiteCase(TestPackageSuiteCase.builder().testPackageName(testPackageName).testSuiteName(testSuiteName).testCaseName(testCaseName).build())
                        .failSince(failSince)
                        .testRunGroupedStates(testRunGroupedStates)
                        .build());
            }
        }
        return TestTrendTable.builder()
                             .testRunHeaders(testRunHeaders)
                             .testCaseTrendRows(testCaseTrendRows)
                             .build();
    }

    static String getTruncatedFailureMessage(List<TestCaseFaultModel> testCaseFaultModels) {
        if (CollectionUtils.isEmpty(testCaseFaultModels)) {
            return null;
        }
        String testCaseFailureMessage = "";
        for (TestCaseFaultModel testCaseFaultModel : testCaseFaultModels) {
            String failureMessage = getTruncatedFailureMessage(testCaseFaultModel);
            if (!StringUtils.isEmpty(failureMessage)) {
                testCaseFailureMessage = failureMessage;
                break;
            }
        }
        return testCaseFailureMessage;
    }

    static String getTruncatedFailureMessage(TestCaseFaultModel testCaseFaultModel) {
        final int maxLines = 5;
        final int maxChars = 1000;

        if (testCaseFaultModel == null) {
            return "";
        }
        String errorMessage = testCaseFaultModel.getMessage();
        if (StringUtils.isEmpty(errorMessage)) {
            errorMessage = testCaseFaultModel.getValue();
        }
        errorMessage = StringUtils.left(errorMessage, maxChars);

        int truncatePosition = StringUtils.ordinalIndexOf(errorMessage, "\n", maxLines);
        if (truncatePosition <= 0) {
            truncatePosition = errorMessage.length();
        }
        return errorMessage.substring(0, truncatePosition);
    }

}
