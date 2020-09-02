package com.ericdriggs.reportcard.model;

public class TestCase extends com.ericdriggs.reportcard.db.tables.pojos.TestCase {

    private TestStatus testStatus;

    public TestCase setTestStatus() {
        if (this.getTestStatusFk() == null) {
            throw new NullPointerException("this.getTestStatusFk()");
        }
        this.testStatus = TestStatus.fromStatusId(this.getTestStatusFk().intValue());
        return this;
    }

    public TestStatus getTestStatus() {
        return testStatus;
    }




}
