package io.github.ericdriggs.reportcard.controller.graph.trend;

import io.github.ericdriggs.reportcard.model.TestStatus;

public enum TestCaseRunState {

    SUCCESS,
    FAIL,
    SKIPPED;


    public static TestCaseRunState fromTestStatusFk(Byte  testStatusFk) {
        TestStatus testStatus = TestStatus.fromStatusId(testStatusFk);
        if (testStatus.isErrorOrFailure()) {
            return FAIL;
        } else if(testStatus.isSkipped()) {
            return SKIPPED;
        }
        return SUCCESS;
    }
}
