package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
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

    @JsonIgnore
    public RunPojo asRunPojo() {
        return RunPojo.builder()
                      .runId(runId)
                      .runReference(runReference)
                      .jobFk(jobFk)
                      .jobRunCount(jobRunCount)
                      .sha(sha)
                      .runDate(runDate)
                      .isSuccess(isSuccess)
                      .build();
    }
}
