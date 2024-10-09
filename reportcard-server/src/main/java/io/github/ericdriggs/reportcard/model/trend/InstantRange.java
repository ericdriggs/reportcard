package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.ObjectUtils;

import java.time.Instant;

@Data
public class InstantRange implements Comparable<InstantRange> {
    Instant start;
    Instant end;

    public void updateRange(Instant instant) {
        if (instant != null) {
            if (start == null || instant.isBefore(start)) {
                start = instant;
            }
            if (end == null || instant.isAfter(end)) {
                end = instant;
            }
        }
    }

    @Override
    public int compareTo(@NonNull InstantRange that) {
        return CompareUtil.chainCompare(
                ObjectUtils.compare(start, that.start),
                ObjectUtils.compare(start, that.start)
        );
    }
}
