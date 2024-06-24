package io.github.ericdriggs.reportcard.model.trend;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@Builder
@Jacksonized
@Value
public class TestCaseName implements Comparable<TestCaseName> {
    String testCaseName;

    @Override
    public int compareTo(TestCaseName that) {
        return StringUtils.compare(this.testCaseName, that.testCaseName);
    }
}
