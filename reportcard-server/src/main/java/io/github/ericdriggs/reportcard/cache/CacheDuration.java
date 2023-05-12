package io.github.ericdriggs.reportcard.cache;

import java.time.Duration;

public enum CacheDuration implements SyncAsyncDuration {

    DAY(Duration.ofHours(24)),
    HOURS(Duration.ofHours(6)),
    HOUR(Duration.ofMinutes(60)),
    MINUTES_FIVE(Duration.ofMinutes(5)),
    MINUTES_TWO(Duration.ofMinutes(2)),
    MINUTE(Duration.ofSeconds(60));

    CacheDuration(Duration expireDuration) {
        this.expireDuration = expireDuration;
        this.refreshDuration = expireDuration.dividedBy(12);
    }

    private Duration expireDuration;
    private Duration refreshDuration;

    public Duration getExpireDuration() {
        return expireDuration;
    }

    public Duration getRefreshDuration() {
        return refreshDuration;
    }
}
