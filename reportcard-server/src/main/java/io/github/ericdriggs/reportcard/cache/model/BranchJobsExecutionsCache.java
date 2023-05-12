package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class BranchJobsExecutionsCache extends AbstractAsyncCache<OrgRepoBranch, Map<Branch,Map<Job, Set<Execution>>>> {
    public BranchJobsExecutionsCache(OrgRepoBranch key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTE;
    }

    @Override
    protected Map<Branch,Map<Job, Set<Execution>>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getBranchJobsExecutions(key.getOrg(), key.getRepo(), key.getBranch(), Collections.EMPTY_MAP);
    }
}
