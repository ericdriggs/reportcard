package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class CompanyOrgsCache extends AbstractAsyncCache<String, Map<CompanyPojo,Set<OrgPojo>>> {

    public static CompanyOrgsCache INSTANCE = new CompanyOrgsCache("company");

    public CompanyOrgsCache(String key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return SyncAsyncDuration
                .builder()
                .expireDuration(Duration.ofHours(4))
                .refreshDuration(Duration.ofMinutes(2))
                .build();
    }

    @Override
    protected Map<CompanyPojo,Set<OrgPojo>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getCompanyOrgs();
    }
}
