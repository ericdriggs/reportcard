package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;

import java.util.Map;
import java.util.Set;

public class CompanyOrgsReposCacheMap extends AbstractAsyncCacheMap<CompanyDTO, Map<CompanyPojo,Map<OrgPojo, Set<RepoPojo>>>, CompanyOrgsReposCache> {

    public static CompanyOrgsReposCacheMap INSTANCE = new CompanyOrgsReposCacheMap();
    @Override
    protected CompanyOrgsReposCache newCache(CompanyDTO key) {
        return new CompanyOrgsReposCache(key);
    }
}
