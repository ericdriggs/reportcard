package io.github.ericdriggs.reportcard.model.orgdashboard;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.model.graph.CompanyGraph;
import io.github.ericdriggs.reportcard.model.graph.OrgGraph;
import io.github.ericdriggs.reportcard.model.graph.RepoGraph;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Builder
@Jacksonized
@Value
public class OrgDashboard {
    CompanyPojo companyPojo;
    OrgPojo orgPojo;
    List<RepoGraph> repoGraph;

    public static OrgDashboard fromCompanyGraphs(List<CompanyGraph> companyGraphs) {

        if (CollectionUtils.isEmpty(companyGraphs)) {
            throw new NullPointerException("companyGraphs is empty");
        }
        if (companyGraphs.size() > 1) {
            throw new IllegalArgumentException("more than one company" + companyGraphs.stream().map(CompanyGraph::companyName).toList());
        }
        CompanyGraph companyGraph = companyGraphs.get(0);
        List<OrgGraph> orgs = companyGraph.orgs();
        if (CollectionUtils.isEmpty(orgs)) {
            throw new NullPointerException("orgs is empty");
        }
        if (companyGraph.orgs().size() > 1) {
            throw new IllegalArgumentException("more than one org: " + orgs.stream().map(OrgGraph::orgName).toList());
        }
        OrgGraph orgGraph = orgs.get(0);

        return OrgDashboard
                .builder()
                .companyPojo(companyGraph.asCompanyPojo())
                .orgPojo(orgGraph.asOrgPojo())
                .repoGraph(orgGraph.repos())
                .build();

    }
}
