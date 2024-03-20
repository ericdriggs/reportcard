package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;

import java.util.Map;
import java.util.Set;

public class RepoBranchesJobsCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoDTO, Map<RepoPojo,Map<BranchPojo, Set<JobPojo>>>, RepoBranchesJobsCache> {

    public static RepoBranchesJobsCacheMap INSTANCE = new RepoBranchesJobsCacheMap();

    @Override
    protected RepoBranchesJobsCache newCache(CompanyOrgRepoDTO key) {
        return new RepoBranchesJobsCache(key);
    }
}

