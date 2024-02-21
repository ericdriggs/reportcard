package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class OrgReposBranchesCache extends AbstractAsyncCache<CompanyOrgDTO, Map<Org,Map<Repo, Set<Branch>>>> {
    public OrgReposBranchesCache(CompanyOrgDTO key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Org,Map<Repo, Set<Branch>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getOrgReposBranches(key.getCompany(), key.getOrg());
    }

}
