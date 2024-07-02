package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record RepoGraph(
        Integer repoId,
        String repoName,
        Integer orgFk,
        List<BranchGraph> branches
) {

    @JsonIgnore
    public RepoPojo asRepoPojo() {
        return new RepoPojo(repoId, repoName, orgFk);
    }
}
