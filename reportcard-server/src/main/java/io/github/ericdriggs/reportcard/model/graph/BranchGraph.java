package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
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
        List<JobGraph> jobs) {

    @JsonIgnore
    public BranchPojo asBranchPojo() {
        return new BranchPojo(branchId, branchName, repoFk, lastRun);
    }
}
