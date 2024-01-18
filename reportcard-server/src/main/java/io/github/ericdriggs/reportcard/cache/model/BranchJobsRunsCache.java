package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BranchJobsRunsCache extends AbstractAsyncCache<CompanyOrgRepoBranch, Map<Branch,Map<Job, Set<Run>>>> {
    public BranchJobsRunsCache(CompanyOrgRepoBranch key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Branch,Map<Job, Set<Run>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getBranchJobsRuns(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), new TreeMap<>());
    }
}
