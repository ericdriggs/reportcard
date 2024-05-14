package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder

public record TestResultGraph(
        Long testResultId,
        Long stageFk,
        Integer tests,
        Integer skipped,
        Integer error,
        Integer failure,
        BigDecimal time,
        Instant testResultCreated,
        String externalLinks,
        Boolean isSuccess,
        Boolean hasSkip,
        List<TestSuiteGraph> testSuites
) implements TestResultGraphBuilder.With {
}
