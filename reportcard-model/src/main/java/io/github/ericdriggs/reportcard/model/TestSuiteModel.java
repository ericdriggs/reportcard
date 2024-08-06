package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSuiteModel extends io.github.ericdriggs.reportcard.dto.TestSuite {

    @JsonIgnore
    final static Logger log = LoggerFactory.getLogger(TestResultModel.class);
    @JsonIgnore
    final static ObjectMapper mapper = SharedObjectMappers.ignoreUnknownObjectMapper;

    private List<TestCaseModel> testCases = new ArrayList<>();

    @JsonProperty("testCases")
    public List<TestCaseModel> getTestCases() {
        return testCases;
    }

    @JsonProperty("testCases")
    public TestSuiteModel setTestCases(List<TestCaseModel> testCases) {
        this.testCases = testCases;
        return this;
    }

    public TestSuiteModel addTestCase(TestCaseModel testCase) {
        this.testCases.add(testCase);
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
            if (!IsEmptyUtil.isCollectionEmpty(testCase.getTestCaseFaults())) {
                matched.add(testCase);
            }
        }
        return matched;
    }

    @JsonIgnore
    public TestStatus getTestStatus() {
        if (getError() > 0) {
            return TestStatus.ERROR;
        } else if (getFailure() > 0) {
            return TestStatus.FAILURE;
        } else if (getIsSuccess()) {
            return TestStatus.SUCCESS;
        }
        return TestStatus.SKIPPED;
    }

    @JsonIgnore
    public static String asJson(List<TestSuiteModel> testSuiteModels) {
        if (testSuiteModels == null) {
            return null;
        }
        if (testSuiteModels.isEmpty()) {
            return "[]";
        }
        try {
            return mapper.writeValueAsString(testSuiteModels);
        } catch (JsonProcessingException e) {
            log.error("failed to parse json for testResultModel: {}", testSuiteModels, e);
        }
        return null;
    }

}
