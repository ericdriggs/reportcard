package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder
@Jacksonized
@Value
public class CompanyOrgRepoBranchJobStageName implements Comparable<CompanyOrgRepoBranchJobStageName> {
    CompanyPojo companyPojo;
    OrgPojo orgPojo;
    RepoPojo repoPojo;
    BranchPojo branchPojo;
    JobPojo jobPojo;
    String stageName;

    @Override
    public int compareTo(@NonNull CompanyOrgRepoBranchJobStageName that) {
        return CompareUtil.chainCompare(
                PojoComparators.compareCompany(companyPojo, that.companyPojo),
                PojoComparators.compareOrg(orgPojo, that.orgPojo),
                PojoComparators.compareRepo(repoPojo, that.repoPojo),
                PojoComparators.compareBranch(branchPojo, that.branchPojo),
                PojoComparators.compareJob(jobPojo, that.jobPojo),
                StringUtils.compare(stageName, that.stageName)
        );
    }
}
