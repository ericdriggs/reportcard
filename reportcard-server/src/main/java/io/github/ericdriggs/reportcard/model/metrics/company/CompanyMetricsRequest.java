package io.github.ericdriggs.reportcard.model.metrics.company;

import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Builder
@Jacksonized
@Value
public class CompanyMetricsRequest implements Comparable<CompanyMetricsRequest> {
    @NonNull InstantRange range;
    @NonNull
    String companyName;
    @Builder.Default
    CompanyMetricsFilter exclude = CompanyMetricsFilter.builder().build();
    @Builder.Default
    CompanyMetricsFilter required = CompanyMetricsFilter.builder().build();
    @Builder.Default
    boolean shouldIncludeDefaultBranches = true;

    @Override
    public int compareTo(@NonNull CompanyMetricsRequest that) {
        return CompareUtil.chainCompare(
                ObjectUtils.compare(range, that.range),
                StringUtils.compare(companyName, that.companyName),
                ObjectUtils.compare(exclude, that.exclude),
                ObjectUtils.compare(required, that.required),
                ObjectUtils.compare(shouldIncludeDefaultBranches, that.shouldIncludeDefaultBranches)
        );
    }
}