package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record CompanyGraph(
        Integer companyId,
        String companyName,
        List<OrgGraph> orgs
) implements CompanyGraphBuilder.With {

    @JsonIgnore
    public CompanyPojo asCompanyPojo() {
        return CompanyPojo.builder().companyId(companyId).companyName(companyName).build();
    }
}
