package io.github.ericdriggs.reportcard.cache;

import java.time.Duration;

public interface HasSyncAsyncDuration {
    Duration getExpireDuration();
    Duration getRefreshDuration();
}
