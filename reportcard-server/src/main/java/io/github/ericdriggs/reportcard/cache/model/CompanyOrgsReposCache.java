package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class CompanyOrgsReposCache extends AbstractAsyncCache<CompanyDTO, Map<Company,Map<Org, Set<Repo>>>> {
    public CompanyOrgsReposCache(CompanyDTO key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Company,Map<Org, Set<Repo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getCompanyOrgsRepos(key.getCompany());
    }

}
