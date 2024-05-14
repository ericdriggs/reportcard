package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record TestCaseGraph(
        Long testCaseId,
        Long testSuiteFk,
        Byte testStatusFk,
        String name,
        String className,
        BigDecimal time,
        String systemOut,
        String systemErr,
        String assertions,
        List<TestCaseFaultGraph> testCaseFaults
) implements TestCaseGraphBuilder.With {
}
