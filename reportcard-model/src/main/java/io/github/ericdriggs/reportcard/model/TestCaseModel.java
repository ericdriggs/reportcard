package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Data;

import java.util.*;

@Data
public class TestCaseModel extends io.github.ericdriggs.reportcard.dto.TestCase {
    private TestStatus testStatus;

    private List<TestCaseFaultModel> testCaseFaults = new ArrayList<>();

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

    public TestCaseModel addTestCaseFault(TestCaseFaultModel testCaseFault) {
        this.testCaseFaults.add(testCaseFault);
        return this;
    }

    public TestCaseModel addTestCaseFaults(Collection<TestCaseFaultModel> testCaseFaults) {
        this.testCaseFaults.addAll(testCaseFaults);
        return this;
    }

    public TestStatus getTestStatus() {
        if (testStatus == null && getTestStatusFk() != null) {
            setTestStatusFk(getTestStatusFk());
        }
        return testStatus;
    }

    public List<TestCaseFaultModel> getTestCaseFaults() {
        return testCaseFaults;
    }

    public ResultCount getResultCount() {
        return testStatus.getResultCount(getTime());
    }

    public boolean hasTestFault() {
        return !IsEmptyUtil.isCollectionEmpty(testCaseFaults);
    }
}
