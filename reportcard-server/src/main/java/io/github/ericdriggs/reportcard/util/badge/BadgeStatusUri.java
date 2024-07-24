package io.github.ericdriggs.reportcard.util.badge;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.net.URI;

@Builder
@Jacksonized
@Value
public class BadgeStatusUri {
    BadgeStatus badgeStatus;
    URI uri;
}
