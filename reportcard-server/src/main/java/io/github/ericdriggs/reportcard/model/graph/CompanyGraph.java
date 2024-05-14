package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record CompanyGraph(
        Integer companyId,
        String companyName,
        List<OrgGraph> orgs
) implements CompanyGraphBuilder.With {
}
