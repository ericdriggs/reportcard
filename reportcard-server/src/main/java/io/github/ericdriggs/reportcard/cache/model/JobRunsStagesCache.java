package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class JobRunsStagesCache extends AbstractAsyncCache<CompanyOrgRepoBranchJobDTO, Map<JobPojo,Map<RunPojo, Set<StagePojo>>>> {

    public JobRunsStagesCache(CompanyOrgRepoBranchJobDTO key) {
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
    protected Map<JobPojo,Map<RunPojo, Set<StagePojo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getJobRunsStages(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId());
    }
}
