package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;

import java.util.Map;
import java.util.Set;

public class BranchJobsRunsCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchDTO, Map<BranchPojo,Map<JobPojo, Set<RunPojo>>>, BranchJobsRunsCache> {

    public static BranchJobsRunsCacheMap INSTANCE = new BranchJobsRunsCacheMap();

    @Override
    protected BranchJobsRunsCache newCache(CompanyOrgRepoBranchDTO key) {
        return new BranchJobsRunsCache(key);
    }
}
