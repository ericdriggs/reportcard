package io.github.ericdriggs.reportcard.model.trend;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Builder
@Jacksonized
@Value
public class InstantRange {
    Instant start;
    Instant end;
}
