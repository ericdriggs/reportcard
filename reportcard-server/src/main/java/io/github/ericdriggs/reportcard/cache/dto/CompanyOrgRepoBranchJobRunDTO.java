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
public class CompanyOrgRepoBranchJobRunDTO implements Comparable<CompanyOrgRepoBranchJobRunDTO> {

    String company;
    String org;
    String repo;
    String branch;
    Long jobId;
    Long runId;

    @Override
    public int compareTo(@NonNull CompanyOrgRepoBranchJobRunDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo),
                StringUtils.compare(branch, that.branch),
                CompareUtil.compareLong(jobId, that.jobId),
                CompareUtil.compareLong(runId, that.runId)
        );
    }
}
