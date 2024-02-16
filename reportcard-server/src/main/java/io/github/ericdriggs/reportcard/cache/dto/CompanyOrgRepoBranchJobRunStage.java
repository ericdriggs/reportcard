package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CompanyOrgRepoBranchJobRunStage {
    private final String company;
    private final String org;
    private final String repo;
    private final String branch;
    private final Long jobId;
    private final Long runId;
    private final String stageName;

    public static CompanyOrgRepoBranchJobRunStage truncateCompany(CompanyOrgRepoBranchJobRunStage o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStage.builder()
                                              .company(o.company)
                                              .build();
    }

    public static CompanyOrgRepoBranchJobRunStage truncateOrg(CompanyOrgRepoBranchJobRunStage o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStage.builder()
                                              .company(o.company)
                                              .org(o.org)
                                              .build();
    }

    public static CompanyOrgRepoBranchJobRunStage truncateRepo(CompanyOrgRepoBranchJobRunStage o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStage.builder()
                                              .company(o.company)
                                              .org(o.org)
                                              .repo(o.repo)
                                              .build();
    }

    public static CompanyOrgRepoBranchJobRunStage truncateBranch(CompanyOrgRepoBranchJobRunStage o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStage.builder()
                                              .company(o.company)
                                              .org(o.org)
                                              .repo(o.repo)
                                              .branch(o.branch)
                                              .build();
    }

    public static CompanyOrgRepoBranchJobRunStage truncateJob(CompanyOrgRepoBranchJobRunStage o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStage.builder()
                                              .company(o.company)
                                              .org(o.org)
                                              .repo(o.repo)
                                              .branch(o.branch)
                                              .jobId(o.jobId)
                                              .build();
    }

    public static CompanyOrgRepoBranchJobRunStage truncateRun(CompanyOrgRepoBranchJobRunStage o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStage.builder()
                                              .company(o.company)
                                              .org(o.org)
                                              .repo(o.repo)
                                              .branch(o.branch)
                                              .jobId(o.jobId)
                                              .runId(o.runId)
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

                                if (stageName != null) {
                                    sb.append("/stage/" + stageName);
                                }
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
