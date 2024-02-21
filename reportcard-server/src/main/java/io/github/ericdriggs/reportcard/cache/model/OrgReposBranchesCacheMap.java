package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class OrgReposBranchesCacheMap extends AbstractAsyncCacheMap<CompanyOrgDTO, Map<Org,Map<Repo, Set<Branch>>>, OrgReposBranchesCache> {

    public static OrgReposBranchesCacheMap INSTANCE = new OrgReposBranchesCacheMap();
    @Override
    protected OrgReposBranchesCache newCache(CompanyOrgDTO key) {
        return new OrgReposBranchesCache(key);
    }
}
