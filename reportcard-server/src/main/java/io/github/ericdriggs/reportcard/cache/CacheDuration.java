package io.github.ericdriggs.reportcard.cache;

import java.time.Duration;

public enum CacheDuration implements SyncAsyncDuration {

    DAY(Duration.ofHours(24)),
    HOUR(Duration.ofMinutes(60)),
    MINUTE(Duration.ofSeconds(60));

    CacheDuration(Duration expireDuration) {
        this.expireDuration = expireDuration;
        this.refreshDuration = expireDuration.dividedBy(10);
    }

    private Duration expireDuration;
    private Duration refreshDuration;

    public Duration getExpireDuration() {
        return expireDuration;
    }

    public Duration getRefreshDuration() {
        return refreshDuration;
    }

    public static SyncAsyncDuration MINUTES(int minutes) {
        return new SyncAsyncDuration() {
            @Override
            public Duration getExpireDuration() {
                return Duration.ofMinutes(minutes);
            }

            @Override
            public Duration getRefreshDuration() {
                return Duration.ofMinutes(minutes).dividedBy(10);
            }
        };
    }


}
