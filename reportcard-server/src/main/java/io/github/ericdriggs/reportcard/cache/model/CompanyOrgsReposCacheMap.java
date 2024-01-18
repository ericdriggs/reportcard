package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyName;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class CompanyOrgsReposCacheMap extends AbstractAsyncCacheMap<CompanyName, Map<Company,Map<Org, Set<Repo>>>, CompanyOrgsReposCache> {

    public static CompanyOrgsReposCacheMap INSTANCE = new CompanyOrgsReposCacheMap();
    @Override
    protected CompanyOrgsReposCache newCache(CompanyName key) {
        return new CompanyOrgsReposCache(key);
    }
}
