package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class BranchJobsRunsCache extends AbstractAsyncCache<OrgRepoBranch, Map<Branch,Map<Job, Set<Run>>>> {
    public BranchJobsRunsCache(OrgRepoBranch key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Branch,Map<Job, Set<Run>>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getBranchJobsRuns(key.getOrg(), key.getRepo(), key.getBranch(), Collections.EMPTY_MAP);
    }
}
