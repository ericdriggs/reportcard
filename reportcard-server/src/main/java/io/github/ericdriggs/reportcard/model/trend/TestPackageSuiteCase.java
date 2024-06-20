package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@Builder
@Jacksonized
@Value
public class TestPackageSuiteCase implements Comparable<TestPackageSuiteCase> {
    String testPackageName;
    String testSuiteName;
    String testCaseName;

    @Override
    public int compareTo(TestPackageSuiteCase that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(this.testPackageName, that.testPackageName),
                StringUtils.compare(this.testSuiteName, that.testSuiteName),
                StringUtils.compare(this.testCaseName, that.testCaseName)
        );
    }

    public TestPackageSuiteCase(String testPackageName, String testSuiteName, String testCaseName) {
        this.testPackageName = testPackageName;
        this.testSuiteName = testSuiteName;
        this.testCaseName = testCaseName;
    }
}
