package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record OrgGraph(Integer orgId,
                       String orgName,
                       Integer companyFk,
                       List<RepoGraph> repos) {

    @JsonIgnore
    public OrgPojo asOrgPojo() {
        return new OrgPojo(orgId, orgName, companyFk);
    }
}
