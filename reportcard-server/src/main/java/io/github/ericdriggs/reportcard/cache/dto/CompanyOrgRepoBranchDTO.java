package io.github.ericdriggs.reportcard.cache.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
@Value
public class CompanyOrgRepoBranchDTO implements Comparable<CompanyOrgRepoBranchDTO> {
    String company;
    String org;
    String repo;
    String branch;

    @JsonIgnore
    public CompanyOrgRepoBranchJobRunStageDTO toCompanyOrgRepoBranchJobRunStageDTO() {
        return toCompanyOrgRepoBranchJobRunStageDTO(null, null, null);
    }


    @JsonIgnore
    public CompanyOrgRepoBranchJobRunStageDTO toCompanyOrgRepoBranchJobRunStageDTO(Long jobId, Long runId, String stageName) {
        return new CompanyOrgRepoBranchJobRunStageDTO(company, org, repo, branch, jobId, runId, stageName);
    }

    @Override
    public int compareTo(@NonNull CompanyOrgRepoBranchDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo),
                StringUtils.compare(branch, that.branch)
        );
    }
}
