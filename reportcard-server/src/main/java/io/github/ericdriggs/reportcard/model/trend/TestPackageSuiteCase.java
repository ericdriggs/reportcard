package io.github.ericdriggs.reportcard.model.trend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

import static io.github.ericdriggs.reportcard.cache.EmptyUtil.emptyIfNull;

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

    @JsonIgnore
    public static String getPackageName(TestPackageSuiteCase t) {
        if (t == null) {
            return "";
        }
        return emptyIfNull(t.testPackageName);
    }


    @JsonIgnore
    public static String getTestSuiteName(TestPackageSuiteCase t) {
        if (t == null) {
            return "";
        }
        return emptyIfNull(t.testSuiteName);
    }

    @JsonIgnore
    public static String getTestCaseName(TestPackageSuiteCase t) {
        if (t == null) {
            return "";
        }
        return emptyIfNull(t.testCaseName);
    }

}
