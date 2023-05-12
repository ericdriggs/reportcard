package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class RepoBranchesJobsCache extends AbstractAsyncCache<OrgRepo, Map<Repo,Map<Branch, Set<Job>>>> {
    public RepoBranchesJobsCache(OrgRepo key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTE;
    }

    @Override
    protected Map<Repo,Map<Branch, Set<Job>>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getRepoBranchesJobs(key.getOrg(), key.getRepo());
    }
}
