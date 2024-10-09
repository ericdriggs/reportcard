package io.github.ericdriggs.reportcard.cache.dto;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
@Value
public class CompanyOrgDTO implements Comparable<CompanyOrgDTO> {
    String company;
    String org;

    @Override
    public int compareTo(@NonNull CompanyOrgDTO that) {

        return CompareUtil.chainCompare(
                StringUtils.compare(company, that.company),
                StringUtils.compare(org, that.org)
        );
    }
}
