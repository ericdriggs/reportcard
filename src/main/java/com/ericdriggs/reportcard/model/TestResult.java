package com.ericdriggs.reportcard.model;

import java.util.ArrayList;
import java.util.List;

public class TestResult extends com.ericdriggs.reportcard.db.tables.pojos.TestResult {
    private List<TestSuite> testSuites =  new ArrayList<>();

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public TestResult setTestSuites( List<TestSuite> testSuites) {
        this.testSuites = testSuites;
        return this;
    }

}
