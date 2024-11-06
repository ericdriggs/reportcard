package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuperBuilder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSuiteModel extends io.github.ericdriggs.reportcard.dto.TestSuite {

    public TestSuiteModel() {

    }

    @JsonIgnore
    final static Logger log = LoggerFactory.getLogger(TestResultModel.class);
    @JsonIgnore
    final static ObjectMapper mapper = SharedObjectMappers.ignoreUnknownObjectMapper;

    @Builder.Default
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

    @JsonProperty("testStatus")
    public TestStatus getTestStatus() {
        if (getError() != null && getError() > 0) {
            return TestStatus.ERROR;
        } else if (getFailure() != null && getFailure() > 0) {
            return TestStatus.FAILURE;
        } else if (( getSkipped() != null && getSkipped() > 0) || getTests() == null || getTests() == 0) {
            return TestStatus.SKIPPED;
        }
        if (getIsSuccess() != null) {
            if (getIsSuccess()) {
                return TestStatus.SUCCESS;
            } else {
                return TestStatus.FAILURE;
            }
        }
        return calculateTestStatus();
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
    TestStatus calculateTestStatus() {
        TestStatus testStatus = TestStatus.SKIPPED;

        for (TestCaseModel testCase : testCases) {
            if (testCase.getTestStatus() == TestStatus.ERROR) {
                return TestStatus.ERROR;
            }
            if (testCase.getTestStatus() == TestStatus.FAILURE) {
                return TestStatus.FAILURE;
            }
            if (testCase.getTestStatus() == TestStatus.SUCCESS) {
                testStatus = TestStatus.SUCCESS;
            }
        }
        return testStatus;
    }

    @JsonIgnore
    public static String asJsonWithTruncatedErrorMessages(List<TestSuiteModel> testSuiteModels) {
        if (testSuiteModels == null) {
            return null;
        }
        if (testSuiteModels.isEmpty()) {
            return "[]";
        }
        try {
            return mapper.writeValueAsString(TestSuiteModel.withTruncatedErrorMessages(testSuiteModels));
        } catch (JsonProcessingException e) {
            log.error("failed to parse json for testResultModel: {}", testSuiteModels, e);
        }
        return null;
    }


    public TestSuiteModel withTruncatedErrorMessages() {
        return this.toBuilder()
                .testCases(TestCaseModel.withTruncatedErrorMessages(testCases))
                .build();
    }

    public static List<TestSuiteModel> withTruncatedErrorMessages(List<TestSuiteModel> testSuiteModels) {
        if (testSuiteModels == null) {
            return null;
        }
        List<TestSuiteModel> ret = new ArrayList<>();
        for (TestSuiteModel testSuiteModel : testSuiteModels) {
            ret.add(testSuiteModel.withTruncatedErrorMessages());
        }
        return ret;
    }


    @JsonIgnore
    public static List<TestSuiteModel> fromJson(String json) {
        final TestSuiteModel[] testSuiteModels = SharedObjectMappers.readValueOrDefault(json, TestSuiteModel[].class, new TestSuiteModel[0]);
        return Arrays.asList(testSuiteModels);
    }

    //currently unused
    @JsonIgnore
    private void updateTotals() {
        if (testCases == null) {
            return;
        }
        boolean hasSkip = false;
        boolean isSuccess = true;
        int errorCount = 0;
        int failCount = 0;
        int skipCount = 0;
        int testCount = 0;
        BigDecimal time = BigDecimal.ZERO;
        for (TestCaseModel testCase : testCases) {
            testCount++;
            if (testCase.getTestStatus() == TestStatus.ERROR) {
                errorCount++;
                isSuccess = false;
            }
            if (testCase.getTestStatus() == TestStatus.FAILURE) {
                failCount++;
                isSuccess = false;
            }
            if (testCase.getTestStatus() == TestStatus.SKIPPED) {
                hasSkip = true;
                skipCount++;
            }
            if (testCase.getTime() != null) {
                time = time.add(testCase.getTime());
            }
        }
        setError(errorCount);
        setIsSuccess(isSuccess);
        setFailure(failCount);
        setHasSkip(hasSkip);
        setSkipped(skipCount);
        setTests(testCount);;
        setTime(time);
    }

}
