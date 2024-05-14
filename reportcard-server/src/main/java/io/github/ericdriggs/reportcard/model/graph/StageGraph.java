package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record StageGraph(
        Long stageId,
        String stageName,
        Long runFk,
        List<TestResultGraph> testResults
) implements StageGraphBuilder.With {
}
