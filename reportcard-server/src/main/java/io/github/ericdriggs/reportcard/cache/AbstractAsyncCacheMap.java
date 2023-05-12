package io.github.ericdriggs.reportcard.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public abstract class AbstractAsyncCacheMap<K, V, C extends AbstractAsyncCache<K, V>> {

    private  final Class<?> clazz = this.getClass();
    protected final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final Map<K, C> keyCacheMap = new ConcurrentHashMap<>();

    /**
     * 
     * @param key the key for the cache
     * @return the value for provided key
     */
    public V getValue(K key) {
        synchronized (clazz.getName() + "::getValue" + String.valueOf(key)) {
            if (keyCacheMap.get(key) == null) {
                log.info(clazz.getName() + "::getValue, Creating cache map entry for key: " + key.toString());
                keyCacheMap.put(key, newCache(key));
            }
            C cache = keyCacheMap.get(key);
            return cache.getCache();
        }
    }

    /**
     * Instantiates a cache for the given key.
     * @param key the key for the map
     * @return an  instance of a cache for the provided key
     */
    protected abstract C newCache(K key);
}
