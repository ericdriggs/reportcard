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
public class CompanyOrgRepoDTO implements Comparable<CompanyOrgRepoDTO> {
    String company;
    String org;
    String repo;

    @Override
    public int compareTo(@NonNull CompanyOrgRepoDTO that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org),
                StringUtils.compare(repo, that.repo)
        );
    }
}
