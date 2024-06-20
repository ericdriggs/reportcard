package io.github.ericdriggs.reportcard.controller.graph.trend;

import io.github.ericdriggs.reportcard.model.TestStatus;

public enum TestCaseRunState {

    SUCCESS,
    FAIL,
    SKIPPED;

    public static TestCaseRunState fromTestStatusFk(Byte testStatusFk) {
        TestStatus testStatus = TestStatus.fromStatusId(testStatusFk);
        if (testStatus.isErrorOrFailure()) {
            return FAIL;
        } else if (testStatus.isSkipped()) {
            return SKIPPED;
        }
        return SUCCESS;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isFail() {
        return this == FAIL;
    }

    public boolean isSkipped() {
        return this == SKIPPED;
    }

}
