package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CompanyOrgRepoBranchJobRunStageDTO {
    private final String company;
    private final String org;
    private final String repo;
    private final String branch;
    private final Long jobId;
    private final Long runId;
    private final String stageName;

    public static CompanyOrgRepoBranchJobRunStageDTO truncateCompany(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                                                 .company(o.company)
                                                 .build();
    }

    public static CompanyOrgRepoBranchJobRunStageDTO truncateOrg(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                                                 .company(o.company)
                                                 .org(o.org)
                                                 .build();
    }

    public static CompanyOrgRepoBranchJobRunStageDTO truncateRepo(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                                                 .company(o.company)
                                                 .org(o.org)
                                                 .repo(o.repo)
                                                 .build();
    }

    public static CompanyOrgRepoBranchJobRunStageDTO truncateBranch(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                                                 .company(o.company)
                                                 .org(o.org)
                                                 .repo(o.repo)
                                                 .branch(o.branch)
                                                 .build();
    }

    public static CompanyOrgRepoBranchJobRunStageDTO truncateJob(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                                                 .company(o.company)
                                                 .org(o.org)
                                                 .repo(o.repo)
                                                 .branch(o.branch)
                                                 .jobId(o.jobId)
                                                 .build();
    }

    public static CompanyOrgRepoBranchJobRunStageDTO truncateRun(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
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
