package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record JobGraph(Long jobId,
                  Map<String,String> jobInfo,
                  Integer branchFk,
                  String jobInfoStr,
                  Instant lastRun,
                  List<RunGraph> runs) implements JobGraphBuilder.With {
}
