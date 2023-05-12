package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;

import java.util.Map;
import java.util.Set;

public class BranchJobsExecutionsCacheMap extends AbstractAsyncCacheMap<OrgRepoBranch, Map<Branch,Map<Job, Set<Execution>>>, BranchJobsExecutionsCache> {

    public static BranchJobsExecutionsCacheMap INSTANCE = new BranchJobsExecutionsCacheMap();

    @Override
    protected BranchJobsExecutionsCache newCache(OrgRepoBranch key) {
        return new BranchJobsExecutionsCache(key);
    }
}
