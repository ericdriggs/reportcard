package com.ericdriggs.reportcard.model;

import java.util.ArrayList;
import java.util.List;

public class TestSuite extends com.ericdriggs.reportcard.db.tables.pojos.TestSuite {
    private List<TestCase> testCases = new ArrayList<>();

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public TestSuite setTestCases( List<TestCase> testCases) {
        this.testCases = testCases;
        return this;
    }

    public boolean calcIsSuccess() {
        return getFailure() == 0 && getError() == 0;
    }
    //TODO: serialize and desrialize properties from json to Map<String,String>
}
