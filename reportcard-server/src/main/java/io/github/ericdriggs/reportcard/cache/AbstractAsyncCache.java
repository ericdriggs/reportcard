package io.github.ericdriggs.reportcard.cache;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A cache which supports asynchronous refreshing.
 * Usage: instantiate sub-class and call getCache() every time you want to access the cache.
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractAsyncCache<K, V> {


    private static final Class<?> clazz = MethodHandles.lookup().lookupClass();
    protected final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    protected final K key;
    protected V cache = null;
    protected LocalDateTime lastUpdated;

    public AbstractAsyncCache(K key) {
        this.key = key;
    }

    /**
     * If the cache has a value which is not expired, it will return that value immediately.
     * If cache is older than getAsyncRefreshableAge, will asynchronously update the cache value.
     * If cache does not have a value, will block until value is synchronously updated.
     *
     * @return the cache.
     */
    public V getCache() {
        refresh();
        return cache;
    }

    protected abstract SyncAsyncDuration getSyncAsyncDuration();

    /**
     * @return the age when the cache is eligible for asynchronous refresh.
     * If <code>NULL</code>, the cache will not refresh asynchronously
     */
    protected Duration getAsyncRefreshableAge() {
        return getSyncAsyncDuration().getRefreshDuration();
    }

    /**
     * @return the age at which the cache refresh will be synchronous (blocking).
     * If <code>NULL</code>, the cache will never expire
     */
    protected Duration getExpiredAge() {
        return getSyncAsyncDuration().getExpireDuration();
    }

    /**
     * @return the new value for the cache
     */
    protected abstract V getUpdatedCacheValue();

    /**
     * @return if the cache does not have a value
     */
    private boolean isCacheEmpty() {
        return ObjectUtils.isEmpty(cache);
    }

    private void setLastUpdatedNow() {
        if (!isCacheEmpty()) {
            this.lastUpdated = LocalDateTime.now();
        }
    }

    private boolean isAsyncRefreshable() {
        if (isExpired() || getAsyncRefreshableAge() == null) {
            return false;
        }

        return LocalDateTime.now().isAfter(lastUpdated.plusSeconds(getAsyncRefreshableAge().toSeconds()));
    }

    private boolean isExpired() {
        if (lastUpdated == null || isCacheEmpty()) {
            return true;
        } else if (getExpiredAge() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(lastUpdated.plusSeconds(getExpiredAge().toSeconds()));
    }

    private void refresh() {
        if (isExpired()) {
            updateCacheSync();
        } else if (isAsyncRefreshable()) {
            updateCacheAsync();
        }
    }

    private void updateCacheAsync() {
        log.info(clazz.getName() + "::updateCacheAsync for key:" + key.toString());
        Runnable backGroundRunnable = this::updateCacheSync;
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }

    private synchronized void updateCacheSync() {
        log.info(clazz.getName() + "::updateCacheSync for key:" + key.toString());
        cache = getUpdatedCacheValue();
        setLastUpdatedNow();
    }

}