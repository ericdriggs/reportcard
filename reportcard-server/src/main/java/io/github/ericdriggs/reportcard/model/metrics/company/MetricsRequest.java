package io.github.ericdriggs.reportcard.model.metrics.company;

import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ObjectUtils;

@Builder
@Jacksonized
@Value
public class MetricsRequest implements Comparable<MetricsRequest> {
    @NonNull InstantRange range;
    @Builder.Default
    MetricsFilter excluded = MetricsFilter.builder().build();
    @Builder.Default
    MetricsFilter required = MetricsFilter.builder().build();
    @Builder.Default
    boolean shouldIncludeDefaultBranches = true;

    @Override
    public int compareTo(@NonNull MetricsRequest that) {
        return CompareUtil.chainCompare(
                ObjectUtils.compare(range, that.range),
                ObjectUtils.compare(excluded, that.excluded),
                ObjectUtils.compare(required, that.required),
                ObjectUtils.compare(shouldIncludeDefaultBranches, that.shouldIncludeDefaultBranches)
        );
    }
}