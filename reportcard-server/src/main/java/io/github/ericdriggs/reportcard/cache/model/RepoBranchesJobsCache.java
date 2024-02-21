package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class RepoBranchesJobsCache extends AbstractAsyncCache<CompanyOrgRepoDTO, Map<Repo,Map<Branch, Set<Job>>>> {
    public RepoBranchesJobsCache(CompanyOrgRepoDTO key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Repo,Map<Branch, Set<Job>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getRepoBranchesJobs(key.getCompany(), key.getOrg(), key.getRepo());
    }
}
