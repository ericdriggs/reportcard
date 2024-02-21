package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;

import java.util.Map;
import java.util.Set;

public class JobRunsStagesCache extends AbstractAsyncCache<CompanyOrgRepoBranchJobDTO, Map<Job,Map<Run, Set<Stage>>>> {

    public JobRunsStagesCache(CompanyOrgRepoBranchJobDTO key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Job,Map<Run, Set<Stage>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getJobRunsStages(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId());
    }
}
