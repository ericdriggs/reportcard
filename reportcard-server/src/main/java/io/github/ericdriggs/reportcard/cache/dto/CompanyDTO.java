package io.github.ericdriggs.reportcard.cache.dto;

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
public class CompanyDTO implements Comparable<CompanyDTO> {
    String company;

    @Override
    public int compareTo(@NonNull CompanyDTO that) {
        return StringUtils.compare(company, that.company);
    }
}
