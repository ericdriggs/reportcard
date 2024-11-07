package io.github.ericdriggs.reportcard.cache.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

import java.util.TreeMap;

@AllArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
@Value
public class CompanyOrgRepoBranchJobInfoRunCountStageDTO
        implements Comparable<CompanyOrgRepoBranchJobInfoRunCountStageDTO> {
    String company;
    String org;
    String repo;
    String branch;
    TreeMap<String,String> jobInfo;
    Integer runCount;
    String stageName;

    @JsonIgnore
    public static CompanyOrgRepoBranchJobInfoRunCountStageDTO truncateRun(CompanyOrgRepoBranchJobInfoRunCountStageDTO o) {
        if (o == null) {
            return null;
        }
        return CompanyOrgRepoBranchJobInfoRunCountStageDTO.builder()
                .company(o.company)
                .org(o.org)
                .repo(o.repo)
                .branch(o.branch)
                .jobInfo(o.jobInfo)
                .runCount(o.runCount)
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

                        if (jobInfo != null) {
                            sb.append("/jobinfo/" + StringMapUtil.toEqualsCsv(jobInfo));

                            if (runCount != null) {
                                sb.append("/runcount/" + runCount);

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

                        if (jobInfo != null) {
                            sb.append("_jobinfo-" + StringMapUtil.valuesOnlyColonSeparated(jobInfo));

                            if (runCount != null) {
                                sb.append("_runcount-" + runCount);

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
    public static CompanyOrgRepoBranchJobInfoRunCountStageDTO fromStagePath(StagePath stagePath) {
        if (stagePath == null) {
            return CompanyOrgRepoBranchJobInfoRunCountStageDTO.builder().build();
        }
        CompanyOrgRepoBranchJobInfoRunCountStageDTO.CompanyOrgRepoBranchJobInfoRunCountStageDTOBuilder builder = CompanyOrgRepoBranchJobInfoRunCountStageDTO.builder();
        if (stagePath.getCompany() != null && stagePath.getCompany().getCompanyName() != null) {
            builder.company(stagePath.getCompany().getCompanyName());

            if (stagePath.getOrg() != null && stagePath.getOrg().getOrgName() != null) {
                builder.org(stagePath.getOrg().getOrgName());

                if (stagePath.getRepo() != null && stagePath.getRepo().getRepoName() != null) {
                    builder.repo(stagePath.getRepo().getRepoName());

                    if (stagePath.getBranch() != null && stagePath.getBranch().getBranchName() != null) {
                        builder.branch(stagePath.getBranch().getBranchName());

                        if (stagePath.getJob() != null && stagePath.getJob().getJobId() != null) {
                            builder.jobInfo(StringMapUtil.jsonToMap(stagePath.getJob().getJobInfo()));

                            if (stagePath.getRun() != null && stagePath.getRun().getRunId() != null) {
                                builder.runCount(stagePath.getRun().getJobRunCount());

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
    public int compareTo(@NonNull CompanyOrgRepoBranchJobInfoRunCountStageDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo),
                StringUtils.compare(branch, that.branch),
                CompareUtil.compareComparableMap(jobInfo, that.jobInfo),
                CompareUtil.compareInteger(runCount, that.runCount),
                StringUtils.compare(stageName, that.stageName)
        );
    }
}
