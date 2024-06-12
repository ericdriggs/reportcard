package io.github.ericdriggs.reportcard.model.trend;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
public class InstantRange {
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

}
