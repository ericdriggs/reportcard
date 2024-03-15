package io.github.ericdriggs.reportcard.model;

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

    public String getClassname() {
        Set<String> classNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (TestCase testCase : testCases) {
            if (testCase.getClassName() != null) {
                classNames.add(testCase.getClassName());
            }
        }
        return String.join(";", classNames);
    }
}
