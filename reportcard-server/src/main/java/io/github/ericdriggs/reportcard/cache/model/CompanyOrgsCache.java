package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;

import java.util.Map;
import java.util.Set;

public class CompanyOrgsCache extends AbstractAsyncCache<String, Map<Company,Set<Org>>> {

    public static CompanyOrgsCache INSTANCE = new CompanyOrgsCache("company");

    public CompanyOrgsCache(String key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Company,Set<Org>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getCompanyOrgs();
    }
}
