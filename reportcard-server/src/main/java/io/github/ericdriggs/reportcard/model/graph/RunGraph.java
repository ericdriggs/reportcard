package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record RunGraph(
        Long runId,
        String runReference,
        Long jobFk,
        Integer jobRunCount,
        String sha,
        Instant runDate,
        Boolean isSuccess,
        List<StageGraph> stages)
        implements RunGraphBuilder.With {
}
