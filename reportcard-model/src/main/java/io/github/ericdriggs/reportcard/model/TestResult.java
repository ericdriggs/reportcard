package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestResult extends io.github.ericdriggs.reportcard.pojos.TestResult {
    private List<TestSuite> testSuites = new ArrayList<>();

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public TestResult setTestSuites( List<TestSuite> testSuites) {
        this.testSuites = testSuites;
        return this;
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

    public TestResult setExternalLinksMap(Map<String,String> externalLinksMap) {
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
        ResultCount resultCount = new ResultCount();
        for (TestSuite testSuite : testSuites) {
            ResultCount testSuiteResultCount = testSuite.getResultCount();
            resultCount = resultCount.add(testSuiteResultCount);
        }
        return resultCount;
    }
}
