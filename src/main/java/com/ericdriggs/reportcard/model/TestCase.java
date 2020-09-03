package com.ericdriggs.reportcard.model;

public class TestCase extends com.ericdriggs.reportcard.db.tables.pojos.TestCase {

    private TestStatus testStatus;

    public TestCase setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
        setTestStatusFk(testStatus.getStatusId().byteValue());
        return this;
    }

    public TestCase setTestStatusFk(Integer testStatusFk) {
        setTestStatusFk(testStatusFk.byteValue());
        setTestStatus(TestStatus.fromStatusId(testStatusFk));
        return this;
    }

    public TestStatus getTestStatus() {
        if (testStatus == null && getTestStatusFk() != null) {
            setTestStatusFk(getTestStatusFk());
        }
        return testStatus;
    }
}
