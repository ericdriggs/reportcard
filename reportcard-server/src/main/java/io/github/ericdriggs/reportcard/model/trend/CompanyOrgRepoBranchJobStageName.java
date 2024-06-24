package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class CompanyOrgRepoBranchJobStageName {
    CompanyPojo companyPojo;
    OrgPojo orgPojo;
    RepoPojo repoPojo;
    BranchPojo branchPojo;
    JobPojo jobPojo;
    String stageName;
}
