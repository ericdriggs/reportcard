package io.github.ericdriggs.reportcard.cache.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
@Value
public class CompanyOrgRepoBranchJobRunStageDTO
        implements Comparable<CompanyOrgRepoBranchJobRunStageDTO> {
    String company;
    String org;
    String repo;
    String branch;
    Long jobId;
    Long runId;
    String stageName;

    @JsonIgnore
    public static CompanyOrgRepoBranchJobRunStageDTO truncateCompany(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                .company(o.company)
                .build();
    }

    @JsonIgnore
    public static CompanyOrgRepoBranchJobRunStageDTO truncateOrg(CompanyOrgRepoBranchJobRunStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobRunStageDTO.builder()
                .company(o.company)
                .org(o.org)
                .build();
    }

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    public String toHtmlId() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(company)) {
            sb.append("company-" + company);

            if (!StringUtils.isEmpty(org)) {
                sb.append("_org-" + org);

                if (!StringUtils.isEmpty(repo)) {
                    sb.append("_repo-" + repo);

                    if (!StringUtils.isEmpty(branch)) {
                        sb.append("_branch-" + branch);

                        if (jobId != null) {
                            sb.append("_jobid-" + jobId);

                            if (runId != null) {
                                sb.append("_runid-" + runId);

                                if (stageName != null) {
                                    sb.append("_stage-" + stageName);
                                }
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    @JsonIgnore
    public static CompanyOrgRepoBranchJobRunStageDTO fromStagePath(StagePath stagePath) {
        if (stagePath == null) {
            return CompanyOrgRepoBranchJobRunStageDTO.builder().build();
        }
        CompanyOrgRepoBranchJobRunStageDTOBuilder builder = CompanyOrgRepoBranchJobRunStageDTO.builder();
        if (stagePath.getCompany() != null && stagePath.getCompany().getCompanyName() != null) {
            builder.company(stagePath.getCompany().getCompanyName());

            if (stagePath.getOrg() != null && stagePath.getOrg().getOrgName() != null) {
                builder.org(stagePath.getOrg().getOrgName());

                if (stagePath.getRepo() != null && stagePath.getRepo().getRepoName() != null) {
                    builder.repo(stagePath.getRepo().getRepoName());

                    if (stagePath.getBranch() != null && stagePath.getBranch().getBranchName() != null) {
                        builder.branch(stagePath.getBranch().getBranchName());

                        if (stagePath.getJob() != null && stagePath.getJob().getJobId() != null) {
                            builder.jobId(stagePath.getJob().getJobId());

                            if (stagePath.getRun() != null && stagePath.getRun().getRunId() != null) {
                                builder.runId(stagePath.getRun().getRunId());

                                if (stagePath.getStage() != null && stagePath.getStage().getStageName() != null) {
                                    builder.stageName(stagePath.getStage().getStageName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public int compareTo(@NonNull CompanyOrgRepoBranchJobRunStageDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo),
                StringUtils.compare(branch, that.branch),
                CompareUtil.compareLong(jobId, that.jobId),
                CompareUtil.compareLong(runId, that.runId),
                StringUtils.compare(stageName, that.stageName)
        );
    }
}
