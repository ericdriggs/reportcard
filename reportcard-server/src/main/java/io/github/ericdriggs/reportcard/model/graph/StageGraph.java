package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record StageGraph(
        Long stageId,
        String stageName,
        Long runFk,
        List<TestResultGraph> testResults,
        List<StorageGraph> storages
) implements StageGraphBuilder.With {

    @JsonIgnore
    public StagePojo asStagePojo() {
        return StagePojo.builder()
                        .stageId(stageId)
                        .stageName(stageName)
                        .runFk(runFk)
                        .build();
    }
}
