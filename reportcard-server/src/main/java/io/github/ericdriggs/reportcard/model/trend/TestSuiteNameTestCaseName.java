package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@Builder
@Jacksonized
@Value
public class TestSuiteNameTestCaseName implements Comparable<TestSuiteNameTestCaseName> {
    String testSuiteName;
    String testCaseName;

    @Override
    public int compareTo(TestSuiteNameTestCaseName that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(this.testSuiteName, that.testSuiteName),
                StringUtils.compare(this.testCaseName, that.testCaseName)
        );
    }

    public TestSuiteNameTestCaseName(String testSuiteName, String testCaseName) {
        this.testSuiteName = testSuiteName;
        this.testCaseName = testCaseName;
    }
}
