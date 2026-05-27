package io.github.ericdriggs.reportcard.controller.graph.trend;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class TestRowSummary {

    @Builder.Default
    Integer tests = 0;

    @Builder.Default
    Integer fail = 0;

    @Builder.Default
    Integer success = 0;

    public static TestRowSummary fromTestTrendTable(TestTrendTable testTrendTable) {
        int tests = 0;
        int success = 0;
        int fail = 0;

        List<TestCaseTrendRow> rows = testTrendTable.getTestCaseTrendRows();
        if (rows == null) {
            return TestRowSummary.builder().build();
        }

        for (TestCaseTrendRow row : rows) {
            tests++;
            FailureMessageIndexMap fmim = row.getFailureMessageIndexMap();
            if (fmim == null || fmim.getFailureMessageIndexMap().isEmpty()) {
                success++;
            } else {
                fail++;
            }
        }
        return TestRowSummary.builder().tests(tests).fail(fail).success(success).build();
    }
}
