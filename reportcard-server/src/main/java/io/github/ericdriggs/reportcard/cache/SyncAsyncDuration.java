package io.github.ericdriggs.reportcard.cache;

import java.time.Duration;

public interface SyncAsyncDuration {

    Duration getExpireDuration();
    Duration getRefreshDuration();
}
