package io.github.ericdriggs.reportcard.cache.dto;

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
public class CompanyOrgRepoBranchJobDTO implements Comparable<CompanyOrgRepoBranchJobDTO> {

    String company;
    String org;
    String repo;
    String branch;
    Long jobId;

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

    @Override
    public int compareTo(@NonNull CompanyOrgRepoBranchJobDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo),
                StringUtils.compare(branch, that.branch),
                CompareUtil.compareLong(jobId, that.jobId)
        );
    }
}
