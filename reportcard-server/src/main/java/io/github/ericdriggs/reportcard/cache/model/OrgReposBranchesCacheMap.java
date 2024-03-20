package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;

import java.util.Map;
import java.util.Set;

public class OrgReposBranchesCacheMap extends AbstractAsyncCacheMap<CompanyOrgDTO, Map<OrgPojo,Map<RepoPojo, Set<BranchPojo>>>, OrgReposBranchesCache> {

    public static OrgReposBranchesCacheMap INSTANCE = new OrgReposBranchesCacheMap();
    @Override
    protected OrgReposBranchesCache newCache(CompanyOrgDTO key) {
        return new OrgReposBranchesCache(key);
    }
}
