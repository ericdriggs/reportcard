package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CompanyOrgRepoBranchJobDTO {
    private final String company;
    private final String org;
    private final String repo;
    private final String branch;
    private final Long jobId;

    public CompanyOrgRepoBranchJobDTO(CompanyOrgRepoBranchJobRunStageDTO companyOrgRepoBranchJobRunStageDTO) {
        if (companyOrgRepoBranchJobRunStageDTO == null) {
            throw new NullPointerException("companyOrgRepoBranchJobRunStageDTO");
        }
        company = companyOrgRepoBranchJobRunStageDTO.getCompany();
        org = companyOrgRepoBranchJobRunStageDTO.getOrg();
        repo = companyOrgRepoBranchJobRunStageDTO.getRepo();
        branch = companyOrgRepoBranchJobRunStageDTO.getBranch();
        jobId = companyOrgRepoBranchJobRunStageDTO.getJobId();
    }
}
