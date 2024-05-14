package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record OrgGraph(Integer orgId,
                  String orgName,
                  Integer companyFk,
                  List<RepoGraph> repos ) implements OrgGraphBuilder.With{
}
