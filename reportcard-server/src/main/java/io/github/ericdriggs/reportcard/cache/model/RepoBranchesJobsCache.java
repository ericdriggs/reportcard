package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class RepoBranchesJobsCache extends AbstractAsyncCache<CompanyOrgRepoDTO, Map<RepoPojo,Map<BranchPojo, Set<JobPojo>>>> {
    public RepoBranchesJobsCache(CompanyOrgRepoDTO key) {
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
    protected Map<RepoPojo,Map<BranchPojo, Set<JobPojo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getRepoBranchesJobs(key.getCompany(), key.getOrg(), key.getRepo());
    }
}
