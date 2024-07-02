package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record TestSuiteGraph(
        Long testSuiteId,
        Long testResultFk,
        String name,
        Integer tests,
        Integer skipped,
        Integer error,
        Integer failure,
        BigDecimal time,
        String packageName,
        String group,
        String properties,
        Boolean isSuccess,
        Boolean hasSkip,
        List<TestCaseGraph> testCases
) {
}
