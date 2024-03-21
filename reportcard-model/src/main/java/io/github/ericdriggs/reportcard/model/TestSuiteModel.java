package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Builder;

import java.util.*;

public class TestSuiteModel extends io.github.ericdriggs.reportcard.dto.TestSuite {

    private List<TestCaseModel> testCases = new ArrayList<>();

    public List<TestCaseModel> getTestCases() {
        return testCases;
    }

    public TestSuiteModel setTestCases(List<TestCaseModel> testCases) {
        this.testCases = testCases;
        return this;
    }

    @JsonIgnore
    public ResultCount getResultCount() {
        ResultCount resultCount = ResultCount.builder().build();
        for (TestCaseModel testCase : testCases) {
            resultCount = ResultCount.add(resultCount, testCase.getResultCount());
        }
        return resultCount;
    }

    @JsonIgnore
    public static ResultCount getResultCount(List<TestSuiteModel> testSuites) {
        ResultCount resultCount = ResultCount.builder().build();
        for (TestSuiteModel testSuite : testSuites) {
            resultCount = ResultCount.add(resultCount, testSuite.getResultCount());
        }
        return resultCount;
    }

    @JsonIgnore
    public List<TestCaseModel> getTestCasesSkipped() {
        List<TestCaseModel> matched = new ArrayList<>();
        for (TestCaseModel testCase : testCases) {
            if (testCase.getTestStatus().isSkipped()) {
                matched.add(testCase);
            }
        }
        return matched;
    }

    @JsonIgnore
    public List<TestCaseModel> getTestCasesErrorOrFailure() {
        List<TestCaseModel> matched = new ArrayList<>();
        for (TestCaseModel testCase : testCases) {
            if (testCase.getTestStatus().isErrorOrFailure()) {
                matched.add(testCase);
            }
        }
        return matched;
    }


    @JsonIgnore
    public List<TestCaseModel> getTestCasesWithFaults() {
        List<TestCaseModel> matched = new ArrayList<>();
        for (TestCaseModel testCase : testCases) {
            if (IsEmptyUtil.isCollectionEmpty(testCase.getTestCaseFaults())) {
                matched.add(testCase);
            }
        }
        return matched;
    }


}
