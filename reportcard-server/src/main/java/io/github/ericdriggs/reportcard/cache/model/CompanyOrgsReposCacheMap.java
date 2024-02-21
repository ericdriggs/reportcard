package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class CompanyOrgsReposCacheMap extends AbstractAsyncCacheMap<CompanyDTO, Map<Company,Map<Org, Set<Repo>>>, CompanyOrgsReposCache> {

    public static CompanyOrgsReposCacheMap INSTANCE = new CompanyOrgsReposCacheMap();
    @Override
    protected CompanyOrgsReposCache newCache(CompanyDTO key) {
        return new CompanyOrgsReposCache(key);
    }
}
