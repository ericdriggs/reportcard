package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class RepoBranchesJobsCache extends AbstractAsyncCache<CompanyOrgRepoDTO, Map<Repo,Map<Branch, Set<Job>>>> {
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
    protected Map<Repo,Map<Branch, Set<Job>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getRepoBranchesJobs(key.getCompany(), key.getOrg(), key.getRepo());
    }
}
