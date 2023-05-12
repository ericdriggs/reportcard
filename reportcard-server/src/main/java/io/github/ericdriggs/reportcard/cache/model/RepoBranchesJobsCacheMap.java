package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;

import java.util.Map;
import java.util.Set;

public class RepoBranchesJobsCacheMap extends AbstractAsyncCacheMap<OrgRepo, Map<Repo,Map<Branch, Set<Job>>>, RepoBranchesJobsCache> {

    public static RepoBranchesJobsCacheMap INSTANCE = new RepoBranchesJobsCacheMap();

    @Override
    protected RepoBranchesJobsCache newCache(OrgRepo key) {
        return new RepoBranchesJobsCache(key);
    }
}

