package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Data;

import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseModel extends io.github.ericdriggs.reportcard.dto.TestCase {
    private TestStatus testStatus;

    private List<TestCaseFaultModel> testCaseFaults = new ArrayList<>();

    @JsonProperty("testCaseFaults")
    public List<TestCaseFaultModel> getTestCaseFaults() {
        return testCaseFaults;
    }

    @JsonProperty("testCaseFaults")
    public TestCaseModel setTestCaseFaults(List<TestCaseFaultModel> testCaseFaults) {
        this.testCaseFaults = testCaseFaults;
        return this;
    }

    @JsonProperty("testStatus")
    public TestStatus getTestStatus() {
        if (testStatus == null && getTestStatusFk() != null) {
            setTestStatusFk(getTestStatusFk());
        }
        return testStatus;
    }

    @JsonProperty("testStatus")
    public TestCaseModel setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
        this.testStatusFk = testStatus.getStatusId();
        return this;
    }

    public TestCaseModel setTestStatusFk(Byte testStatusFk) {
        this.testStatus = TestStatus.fromStatusId(testStatusFk);
        this.testStatusFk = testStatusFk;
        return this;
    }

    @JsonIgnore
    public TestCaseModel addTestCaseFault(TestCaseFaultModel testCaseFault) {
        this.testCaseFaults.add(testCaseFault);
        return this;
    }

    @JsonIgnore
    public TestCaseModel addTestCaseFaults(Collection<TestCaseFaultModel> testCaseFaults) {
        this.testCaseFaults.addAll(testCaseFaults);
        return this;
    }

    @JsonIgnore
    public ResultCount getResultCount() {
        return testStatus.getResultCount(getTime());
    }

    @JsonIgnore
    public boolean hasTestFault() {
        return !IsEmptyUtil.isCollectionEmpty(testCaseFaults);
    }
}
