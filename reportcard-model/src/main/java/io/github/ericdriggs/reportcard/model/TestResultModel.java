package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.util.*;

public class TestResultModel extends io.github.ericdriggs.reportcard.dto.TestResult {

    private List<TestSuiteModel> testSuites = new ArrayList<>();

    public List<TestSuiteModel> getTestSuites() {
        return testSuites;
    }

    public TestResultModel setTestSuites(List<TestSuiteModel> testSuites) {
        this.testSuites = testSuites;
        this.updateTotalsFromTestSuites();
        return this;
    }

    public TestResultModel() {
    }

    public TestResultModel(List<TestSuiteModel> from) {
        this.testSuites = new ArrayList<>(from);
        this.updateTotalsFromTestSuites();
    }

    public TestResultModel copy() {
        return new TestResultModel(this.getTestSuites());
    }

    public void updateTotalsFromTestSuites() {
        ResultCount resultCount = getResultCount();
        this.setError(resultCount.getErrors());
        this.setFailure(resultCount.getFailures());
        this.setSkipped(resultCount.getSkipped());
        this.setTests(resultCount.getTests());
        this.setTime(resultCount.getTime());
        this.setHasSkip(resultCount.getSkipped() > 0);
        this.setIsSuccess(resultCount.getErrors() == 0 && resultCount.getFailures() == 0 );
    }

    public TestResultModel setExternalLinksMap(Map<String,String> externalLinksMap) {
        this.setExternalLinks(getExternalLinksJson(externalLinksMap));
        return this;
    }


    /**
     * Gets value from super or calculates it (super may be <code>null</code> if row just inserted)
     * @return whether was successful
     */
    @Override
    public Boolean getIsSuccess() {
        if (super.getIsSuccess() != null) {
            return super.getIsSuccess();
        } else {
            int failure = Objects.requireNonNullElse(super.getFailure(), 0);
            int error = Objects.requireNonNullElse(super.getError(), 0);
            int skipped = Objects.requireNonNullElse(super.getSkipped(), 0);

            return failure + error + skipped == 0;
        }
    }

    /**
     * Gets value from super or calculates it (super may be <code>null</code> if row just inserted)
     * @return whether has skip
     */
    @Override
    public Boolean getHasSkip() {
        if (super.getHasSkip() != null) {
            return super.getHasSkip();
        } else {
            int skipped = Objects.requireNonNullElse(super.getSkipped(), 0);
            return skipped > 0;
        }
    }

    final static ObjectMapper mapper = new ObjectMapper();
    protected String getExternalLinksJson(Map<String,String> externalLinksMap) {
        if (externalLinksMap == null)  {
            return null;
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(externalLinksMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultCount getResultCount() {
        ResultCount resultCount = ResultCount.builder().build();
        for (TestSuiteModel testSuite : testSuites) {
            ResultCount testSuiteResultCount = testSuite.getResultCount();
            resultCount = ResultCount.add(resultCount, testSuiteResultCount);
        }
        return resultCount;
    }

    public TestResultModel add(TestResultModel that) {
        if (that == null ) {
            throw new NullPointerException("that");
        }

        if (that.getTestSuites() == null) {
            throw new NullPointerException("that.getTestSuites()");
        }

        List<TestSuiteModel> combined = new ArrayList<>(this.getTestSuites());
        combined.addAll(that.getTestSuites());

        return new TestResultModel(combined);
    }
    @JsonIgnore
    public Map<TestSuiteModel, List<TestCaseModel>> getTestCasesSkipped() {
        Map<TestSuiteModel, List<TestCaseModel>> matched = new TreeMap<>(ModelComparators.TEST_SUITE_CASE_INSENSITIVE_ORDER);
        for(TestSuiteModel testSuite : testSuites) {
            for (TestCaseModel testCase : testSuite.getTestCases()) {
                if (testCase.getTestStatus().isSkipped()) {
                    matched.computeIfAbsent(testSuite, k -> new ArrayList<>());
                    matched.get(testSuite).add(testCase);
                }
            }
        }
        return matched;
    }

    @JsonIgnore
    public Map<TestSuiteModel, List<TestCaseModel>> getTestCasesErrorOrFailure() {
        Map<TestSuiteModel, List<TestCaseModel>> matched = new TreeMap<>(ModelComparators.TEST_SUITE_CASE_INSENSITIVE_ORDER);
        for(TestSuiteModel testSuite : testSuites) {
            for (TestCaseModel testCase : testSuite.getTestCases()) {
                if (testCase.getTestStatus().isErrorOrFailure()) {
                    matched.computeIfAbsent(testSuite, k -> new ArrayList<>());
                    matched.get(testSuite).add(testCase);
                }
            }
        }
        return matched;
    }


    @JsonIgnore
    public Map<TestSuiteModel, List<TestCaseModel>> getTestCasesWithFaults() {
        Map<TestSuiteModel, List<TestCaseModel>> matched = new TreeMap<>(ModelComparators.TEST_SUITE_CASE_INSENSITIVE_ORDER);
        for(TestSuiteModel testSuite : testSuites) {
            for (TestCaseModel testCase : testSuite.getTestCases()) {
                if (IsEmptyUtil.isCollectionEmpty(testCase.getTestCaseFaults())) {
                    matched.computeIfAbsent(testSuite, k -> new ArrayList<>());
                    matched.get(testSuite).add(testCase);
                }
            }
        }
        return matched;
    }

}
