package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class OrgReposBranchesCache extends AbstractAsyncCache<CompanyOrgDTO, Map<OrgPojo,Map<RepoPojo, Set<BranchPojo>>>> {
    public OrgReposBranchesCache(CompanyOrgDTO key) {
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
    protected Map<OrgPojo,Map<RepoPojo, Set<BranchPojo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getOrgReposBranches(key.getCompany(), key.getOrg());
    }

}
