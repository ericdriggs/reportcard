package io.github.ericdriggs.reportcard.cache;

import java.time.Duration;

public enum CacheDuration implements HasSyncAsyncDuration {

    DAY(Duration.ofHours(24)),
    HOURS_4(Duration.ofMinutes(60)),
    HOUR(Duration.ofMinutes(60)),

    MINUTES_30(Duration.ofMinutes(30)),
    MINUTES_15(Duration.ofMinutes(15)),
    MINUTES_5(Duration.ofMinutes(5));

    private final static int syncAsyncRatio = 20;

    CacheDuration(Duration expireDuration) {
        this.syncAsyncDuration = fromExpireDuration(expireDuration);
    }

    private final SyncAsyncDuration syncAsyncDuration;

    public Duration getExpireDuration() {
        return syncAsyncDuration.getExpireDuration();
    }

    public Duration getRefreshDuration() {
        return syncAsyncDuration.getRefreshDuration();
    }

    public static SyncAsyncDuration fromExpireDuration(Duration expireDuration) {
        return SyncAsyncDuration
                .builder()
                .expireDuration(expireDuration)
                .refreshDuration(expireDuration.dividedBy(syncAsyncRatio))
                .build();

    }

}
