package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

@Builder
@Jacksonized
@Value
public class StorageUri implements Comparable<StorageUri> {
    String label;
    String uri;

    @Override
    public int compareTo(StorageUri that) {
        return CompareUtil.chainCompare(
                StringUtils.compare(this.label, that.label),
                StringUtils.compare(this.uri, that.uri)
        );
    }
}
