package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.time.Duration;
import java.util.*;

public class TestCase extends io.github.ericdriggs.reportcard.pojos.TestCase {
    private TestStatus testStatus;

    private final List<TestCaseFault> testCaseFaults = new ArrayList<>();

    public TestCase setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
        this.testStatusFk = testStatus.getStatusId();
        return this;
    }

    public TestCase setTestStatusFk(Byte testStatusFk) {
        this.testStatus = TestStatus.fromStatusId(testStatusFk);
        this.testStatusFk = testStatusFk;
        return this;
    }

    public TestCase addTestCaseFault(TestCaseFault testCaseFault) {
        this.testCaseFaults.add(testCaseFault);
        return this;
    }

    public TestCase addTestCaseFaults(Collection<TestCaseFault> testCaseFaults) {
        this.testCaseFaults.addAll(testCaseFaults);
        return this;
    }

    public TestStatus getTestStatus() {
        if (testStatus == null && getTestStatusFk() != null) {
            setTestStatusFk(getTestStatusFk());
        }
        return testStatus;
    }

    public List<TestCaseFault> getTestCaseFaults() {
        return testCaseFaults;
    }

    public Duration getDuration() {
        if (time == null) {
            return Duration.ofSeconds(0);
        }
        return Duration.ofSeconds(time.toBigInteger().longValue());
    }
    public ResultCount getResultCount() {
        return testStatus.getResultCount(getTime());
    }

    public boolean hasTestFault() {
        return !IsEmptyUtil.isCollectionEmpty(testCaseFaults);
    }
}
