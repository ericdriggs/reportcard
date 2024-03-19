package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.util.*;

public class TestSuite extends io.github.ericdriggs.reportcard.pojos.TestSuite {

    private List<TestCase> testCases = new ArrayList<>();

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public TestSuite setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
        return this;
    }

    public ResultCount getResultCount() {
        ResultCount resultCount = ResultCount.builder().build();
        for (TestCase testCase : testCases) {
            resultCount = ResultCount.add(resultCount, testCase.getResultCount());
        }
        return resultCount;
    }

    public static ResultCount getResultCount(List<TestSuite> testSuites) {
        ResultCount resultCount = ResultCount.builder().build();
        for (TestSuite testSuite : testSuites) {
            resultCount = ResultCount.add(resultCount, testSuite.getResultCount());
        }
        return resultCount;
    }

    @JsonIgnore
    public List<TestCase> getTestCasesSkipped() {
        List<TestCase> matched = new ArrayList<>();
        for (TestCase testCase : testCases) {
            if (testCase.getTestStatus().isSkipped()) {
                matched.add(testCase);
            }
        }
        return matched;
    }

    @JsonIgnore
    public List<TestCase> getTestCasesErrorOrFailure() {
        List<TestCase> matched = new ArrayList<>();
        for (TestCase testCase : testCases) {
            if (testCase.getTestStatus().isErrorOrFailure()) {
                matched.add(testCase);
            }
        }
        return matched;
    }


    @JsonIgnore
    public List<TestCase> getTestCasesWithFaults() {
        List<TestCase> matched = new ArrayList<>();
        for (TestCase testCase : testCases) {
            if (IsEmptyUtil.isCollectionEmpty(testCase.getTestCaseFaults())) {
                matched.add(testCase);
            }
        }
        return matched;
    }


}
