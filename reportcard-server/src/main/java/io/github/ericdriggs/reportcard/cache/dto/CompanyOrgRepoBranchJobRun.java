package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CompanyOrgRepoBranchJobRun {
    private final String company;
    private final String org;
    private final String repo;
    private final String branch;
    private final Long jobId;
    private final Long runId;

    public static CompanyOrgRepoBranchJobRun truncateCompany(CompanyOrgRepoBranchJobRun o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRun.builder()
                                         .company(o.company)
                                         .build();
    }

    public static CompanyOrgRepoBranchJobRun truncateOrg(CompanyOrgRepoBranchJobRun o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRun.builder()
                                         .company(o.company)
                                         .org(o.org)
                                         .build();
    }

    public static CompanyOrgRepoBranchJobRun truncateRepo(CompanyOrgRepoBranchJobRun o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRun.builder()
                                         .company(o.company)
                                         .org(o.org)
                                         .repo(o.repo)
                                         .build();
    }

    public static CompanyOrgRepoBranchJobRun truncateBranch(CompanyOrgRepoBranchJobRun o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRun.builder()
                                         .company(o.company)
                                         .org(o.org)
                                         .repo(o.repo)
                                         .branch(o.branch)
                                         .build();
    }

    public static CompanyOrgRepoBranchJobRun truncateJob(CompanyOrgRepoBranchJobRun o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRun.builder()
                                         .company(o.company)
                                         .org(o.org)
                                         .repo(o.repo)
                                         .branch(o.branch)
                                         .jobId(o.jobId)
                                         .build();
    }

}
