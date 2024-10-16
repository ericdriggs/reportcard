package io.github.ericdriggs.reportcard.cache.dto;

import io.github.ericdriggs.reportcard.util.CompareUtil;
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
public class CompanyOrgRepoBranchJobInfoDTO implements Comparable<CompanyOrgRepoBranchJobInfoDTO> {

    String company;
    String org;
    String repo;
    String branch;
    TreeMap<String,String> jobInfo;

    @Override
    public int compareTo(@NonNull CompanyOrgRepoBranchJobInfoDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo),
                StringUtils.compare(branch, that.branch),
                CompareUtil.compareComparableMap(jobInfo, that.jobInfo)
        );
    }
}
