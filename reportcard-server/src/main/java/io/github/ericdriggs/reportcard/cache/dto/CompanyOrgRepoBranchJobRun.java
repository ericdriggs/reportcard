package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    public String toUrlPath() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(company)) {
            sb.append("/company/" + company);

            if (!StringUtils.isEmpty(org)) {
                sb.append("/org/" + org);

                if (!StringUtils.isEmpty(repo)) {
                    sb.append("/repo/" + repo);

                    if (!StringUtils.isEmpty(branch)) {
                        sb.append("/branch/" + branch);

                        if (jobId != null) {
                            sb.append("/job/" + jobId);

                            if (runId != null) {
                                sb.append("/run/" + runId);
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
