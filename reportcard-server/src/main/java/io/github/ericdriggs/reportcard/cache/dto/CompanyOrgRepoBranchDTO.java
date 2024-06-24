package io.github.ericdriggs.reportcard.cache.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompanyOrgRepoBranchDTO {
    private final String company;
    private final String org;
    private final String repo;
    private final String branch;

    @JsonIgnore
    public CompanyOrgRepoBranchJobRunStageDTO toCompanyOrgRepoBranchJobRunStageDTO() {
        return toCompanyOrgRepoBranchJobRunStageDTO(null, null, null);
    }


    @JsonIgnore
    public CompanyOrgRepoBranchJobRunStageDTO toCompanyOrgRepoBranchJobRunStageDTO(Long jobId, Long runId, String stageName) {
        return CompanyOrgRepoBranchJobRunStageDTO
                .builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .jobId(jobId)
                .runId(runId)
                .stageName(stageName)
                .build();

    }
}
