package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class CompanyOrgsReposCache extends AbstractAsyncCache<CompanyDTO, Map<CompanyPojo,Map<OrgPojo, Set<RepoPojo>>>> {
    public CompanyOrgsReposCache(CompanyDTO key) {
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
    protected Map<CompanyPojo,Map<OrgPojo, Set<RepoPojo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getCompanyOrgsRepos(key.getCompany());
    }

}
