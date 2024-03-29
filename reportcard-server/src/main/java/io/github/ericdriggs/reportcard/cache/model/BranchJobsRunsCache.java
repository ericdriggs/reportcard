package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BranchJobsRunsCache extends AbstractAsyncCache<CompanyOrgRepoBranchDTO, Map<BranchPojo,Map<JobPojo, Set<RunPojo>>>> {
    public BranchJobsRunsCache(CompanyOrgRepoBranchDTO key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return SyncAsyncDuration
                .builder()
                .expireDuration(Duration.ofMinutes(30))
                .refreshDuration(Duration.ofMinutes(2))
                .build();
    }

    @Override
    protected Map<BranchPojo,Map<JobPojo, Set<RunPojo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getBranchJobsRuns(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), new TreeMap<>());
    }
}
