package com.ericdriggs.reportcard.model;

import java.util.ArrayList;
import java.util.List;

public class TestSuite extends com.ericdriggs.reportcard.db.tables.pojos.TestSuite {
    private List<TestCase> testCases = new ArrayList<>();

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public TestSuite setTestSuites( List<TestCase> testCases) {
        this.testCases = testCases;
        return this;
    }
}
