package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

//@Component
public class OrgsReposCache extends AbstractAsyncCache<String, Map<Org,Set<Repo>>> {

    public static OrgsReposCache INSTANCE = new OrgsReposCache("org");

    public OrgsReposCache(String key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Org,Set<Repo>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getOrgsRepos();
    }
}
