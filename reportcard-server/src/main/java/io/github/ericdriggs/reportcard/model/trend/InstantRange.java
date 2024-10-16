package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.time.Instant;
import java.util.Comparator;

@AllArgsConstructor
@Builder
@Jacksonized
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

    public static final Comparator<InstantRange> DESCENDING = new InstantRangeDescendingComparator();

    private static class InstantRangeDescendingComparator implements Comparator<InstantRange>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -4657423078433214625L;

        public int compare(InstantRange val1, InstantRange val2) {
            if (val1 == null || val2 == null) {
                return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
            }
            return val2.compareTo(val1);
        }
    }

}
