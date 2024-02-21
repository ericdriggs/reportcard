package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;

import java.util.Map;
import java.util.Set;

public class BranchJobsRunsCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchDTO, Map<Branch,Map<Job, Set<Run>>>, BranchJobsRunsCache> {

    public static BranchJobsRunsCacheMap INSTANCE = new BranchJobsRunsCacheMap();

    @Override
    protected BranchJobsRunsCache newCache(CompanyOrgRepoBranchDTO key) {
        return new BranchJobsRunsCache(key);
    }
}
