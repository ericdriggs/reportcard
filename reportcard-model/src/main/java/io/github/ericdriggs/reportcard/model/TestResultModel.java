package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestResultModel extends io.github.ericdriggs.reportcard.dto.TestResult {


    private List<TestSuiteModel> testSuites = new ArrayList<>();

    /**
     * Flattened tags JSON string for storage in test_result.tags column.
     * Stored as JSON array string like ["foo", "bar"].
     */
    private String tags;

    @JsonIgnore
    public String getTags() {
        return tags;
    }

    @JsonIgnore
    public TestResultModel setTags(String tags) {
        this.tags = tags;
        return this;
    }

    /**
     * Maximum length for individual tags due to multi-value index CHAR(25) constraint.
     */
    public static final int MAX_TAG_LENGTH = 25;

    /**
     * Convenience setter that converts List to JSON string.
     * Tags longer than MAX_TAG_LENGTH are truncated to fit the index.
     */
    @JsonIgnore
    public TestResultModel setTagsList(List<String> tagsList) {
        if (tagsList == null || tagsList.isEmpty()) {
            this.tags = null;
        } else {
            try {
                List<String> truncatedTags = truncateTags(tagsList);
                this.tags = mapper.writeValueAsString(truncatedTags);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize tags to JSON: {}", tagsList, e);
                this.tags = null;
            }
        }
        return this;
    }

    /**
     * Truncates tags to MAX_TAG_LENGTH to fit the multi-value index constraint.
     */
    @JsonIgnore
    public static List<String> truncateTags(List<String> tags) {
        if (tags == null) {
            return null;
        }
        List<String> truncated = new ArrayList<>(tags.size());
        for (String tag : tags) {
            if (tag == null) {
                continue;
            }
            if (tag.length() > MAX_TAG_LENGTH) {
                truncated.add(tag.substring(0, MAX_TAG_LENGTH));
            } else {
                truncated.add(tag);
            }
        }
        return truncated;
    }

    @JsonProperty("testSuites")
    public List<TestSuiteModel> getTestSuites() {
        return testSuites;
    }

    @JsonProperty("testCases")
    public TestResultModel setTestSuites(List<TestSuiteModel> testSuites) {
        this.testSuites = testSuites;
        this.updateTotalsFromTestSuites();
        return this;
    }

    @JsonIgnore
    public static String asJson(TestResultModel testResultModel) {
        if(testResultModel == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(testResultModel);
        } catch (JsonProcessingException e) {
            log.error("failed to parse json for testResultModel: {}", testResultModel, e);
        }
        return null;
    }

    @JsonIgnore
    final static Logger log = LoggerFactory.getLogger(TestResultModel.class);
    @JsonIgnore
    final static ObjectMapper mapper = SharedObjectMappers.ignoreUnknownObjectMapper;



    @JsonIgnore
    public TestResultModel addTestSuite(TestSuiteModel testSuite) {
        this.testSuites.add(testSuite);
        return this;
    }

    @JsonIgnore
    public TestResultModel() {
    }

    @JsonIgnore
    public TestResultModel(List<TestSuiteModel> from) {
        this.testSuites = new ArrayList<>(from);
        this.updateTotalsFromTestSuites();
    }

    @JsonIgnore
    public TestResultModel copy() {
        return new TestResultModel(this.getTestSuites());
    }

    @JsonIgnore
    public void updateTotalsFromTestSuites() {
        ResultCount resultCount = getResultCount();
        this.setError(resultCount.getErrors());
        this.setFailure(resultCount.getFailures());
        this.setSkipped(resultCount.getSkipped());
        this.setTests(resultCount.getTests());
        this.setTime(resultCount.getTime());
        this.setHasSkip(resultCount.getSkipped() > 0 || resultCount.getTests() == 0);
        this.setIsSuccess(resultCount.getErrors() == 0 && resultCount.getFailures() == 0 && resultCount.getTests() > 0);
    }

    @JsonIgnore
    public TestResultModel setExternalLinksMap(Map<String,String> externalLinksMap) {
        this.setExternalLinks(getExternalLinksJson(externalLinksMap));
        return this;
    }


    /**
     * Gets value from super or calculates it (super may be <code>null</code> if row just inserted)
     * @return whether was successful
     */
    @Override
    @JsonIgnore
    public Boolean getIsSuccess() {
        if (super.getIsSuccess() != null) {
            return super.getIsSuccess() && super.getTests() > 0;
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
    @JsonIgnore
    public Boolean getHasSkip() {
        if (super.getHasSkip() != null) {
            return super.getHasSkip();
        } else {
            int skipped = Objects.requireNonNullElse(super.getSkipped(), 0);
            return skipped > 0 || super.getTests() == 0;
        }
    }

    @JsonIgnore
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

    @JsonIgnore
    public ResultCount getResultCount() {
        ResultCount resultCount = ResultCount.builder().build();
        for (TestSuiteModel testSuite : testSuites) {
            ResultCount testSuiteResultCount = testSuite.getResultCount();
            resultCount = ResultCount.add(resultCount, testSuiteResultCount);
        }
        return resultCount;
    }

    @JsonIgnore
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
