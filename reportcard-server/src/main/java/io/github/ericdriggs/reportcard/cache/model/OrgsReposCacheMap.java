package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class OrgsReposCacheMap extends AbstractAsyncCacheMap<String, Map<Org,Set<Repo>>, OrgsReposCache> {
    public static OrgsReposCacheMap INSTANCE = new OrgsReposCacheMap();
    @Override
    protected OrgsReposCache newCache(String key) {
        return OrgsReposCache.INSTANCE;
    }
}
