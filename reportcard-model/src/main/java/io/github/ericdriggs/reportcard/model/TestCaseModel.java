package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseModel extends io.github.ericdriggs.reportcard.dto.TestCase {

    public TestCaseModel() {

    }

    private TestStatus testStatus;

    @Builder.Default
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
        if (testCaseFaults != null) {
            this.testCaseFaults.addAll(testCaseFaults);
        }
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

    public TestCaseModel withTruncatedErrorMessages() {
        return this.toBuilder()
                .testCaseFaults(TestCaseFaultModel.withTruncatedErrorMessages(testCaseFaults))
                .build();
    }

    public static List<TestCaseModel> withTruncatedErrorMessages(List<TestCaseModel> testCaseModels) {
        if (testCaseModels == null) {
            return null;
        }
        List<TestCaseModel> ret = new ArrayList<>();
        for (TestCaseModel testCaseModel : testCaseModels) {
            ret.add(testCaseModel.withTruncatedErrorMessages());
        }
        return ret;
    }
}
