package io.github.ericdriggs.reportcard.model.publication;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

@Builder
@Jacksonized
@Value
public class FailedPublicationsResponse {
    Integer days;
    Integer olderThanMinutes;
    Integer limit;
    Instant dateCutoff;
    Instant graceCutoff;
    List<FailedPublication> failedPublications;
}
