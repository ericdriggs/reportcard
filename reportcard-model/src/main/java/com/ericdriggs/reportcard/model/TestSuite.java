package com.ericdriggs.reportcard.model;

import com.ericdriggs.reportcard.xml.ResultCount;
import java.util.ArrayList;
import java.util.List;

public class TestSuite extends com.ericdriggs.reportcard.pojos.TestSuite {
    private List<TestCase> testCases = new ArrayList<>();

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public TestSuite setTestCases( List<TestCase> testCases) {
        this.testCases = testCases;
        return this;
    }


    public ResultCount getResultCount() {
        ResultCount resultCount = new ResultCount();
        for(TestCase testCase : testCases) {
            resultCount.add(testCase.getResultCount());
        }
        return resultCount;
    }

    public static ResultCount getResultCount(List<TestSuite> testSuites) {
        ResultCount resultCount = new ResultCount();
        for (TestSuite testSuite : testSuites) {
            resultCount.add(testSuite.getResultCount());
        }
        return resultCount;
    }
}
