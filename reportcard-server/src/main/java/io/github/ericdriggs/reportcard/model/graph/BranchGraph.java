package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record BranchGraph(
        Integer branchId,
        String branchName,
        Integer repoFk,
        Instant lastRun,
        List<JobGraph> jobs)
        implements BranchGraphBuilder.With {
}
