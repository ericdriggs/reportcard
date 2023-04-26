package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;

public class TestCase extends io.github.ericdriggs.reportcard.pojos.TestCase {
    private TestStatus testStatus;

    public TestCase setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
        this.testStatusFk = testStatus.getStatusId();
        return this;
    }

    public TestCase setTestStatusFk(Byte testStatusFk) {
        this.testStatus = TestStatus.fromStatusId(testStatusFk);
        this.testStatusFk = testStatusFk;
        return this;
    }

    public TestStatus getTestStatus() {
        if (testStatus == null && getTestStatusFk() != null) {
            setTestStatusFk(getTestStatusFk());
        }
        return testStatus;
    }

    public ResultCount getResultCount() {
        return testStatus.getResultCount(getTime());
    }
}
