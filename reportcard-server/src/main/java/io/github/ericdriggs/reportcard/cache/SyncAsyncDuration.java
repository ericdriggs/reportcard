package io.github.ericdriggs.reportcard.cache;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;

@Value
@Builder
public class SyncAsyncDuration implements HasSyncAsyncDuration {
    Duration expireDuration;
    Duration refreshDuration;
}
