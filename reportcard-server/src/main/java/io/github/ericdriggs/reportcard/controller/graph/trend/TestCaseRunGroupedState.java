package io.github.ericdriggs.reportcard.controller.graph.trend;

import io.github.ericdriggs.reportcard.model.TestStatus;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class TestCaseRunGroupedState implements Comparable<TestCaseRunGroupedState> {
    Long runId;
    TestCaseRunState testCaseRunState;

    /**
     * Used to distinguish types of failure based on differing error messages. Null if not failure or error
     */
    @Builder.Default
    Integer testCaseRunStateGroup = null;

    public static TestCaseRunGroupedState factory(Long runId, TestStatus testStatus, FailureMessageIndexMap failureMessageIndexMap, String testFailureMessage) {

        TestCaseRunState testCaseRunState = null;
        Integer testCaseRunStateGroup = null;

        if (testStatus == null) {
            throw new NullPointerException("testStatus");
        }
        if (testStatus == TestStatus.SUCCESS) {
            testCaseRunState = TestCaseRunState.SUCCESS;

        } else if (testStatus == TestStatus.SKIPPED) {
            testCaseRunState = TestCaseRunState.SKIPPED;
        } else if (testStatus.isErrorOrFailure()) {
            if (failureMessageIndexMap == null) {
                throw new NullPointerException("failureMessageIndexMap");
            }
            testCaseRunState = TestCaseRunState.FAIL;
            testCaseRunStateGroup = failureMessageIndexMap.getFailureIndex(testFailureMessage);
        } else {
            throw new IllegalStateException("no handling for testStatus: " + testStatus.name());
        }
        return TestCaseRunGroupedState.builder()
                .runId(runId)
                .testCaseRunState(testCaseRunState)
                .testCaseRunStateGroup(testCaseRunStateGroup)
                .build();

    }

    @Override
    public int compareTo(TestCaseRunGroupedState that) {
        //that first since descending
        return Long.compare(that.runId, this.runId);
    }
}
