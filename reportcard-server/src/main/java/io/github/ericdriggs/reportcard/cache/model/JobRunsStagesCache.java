package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJob;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;

import java.util.Map;
import java.util.Set;

public class JobRunsStagesCache extends AbstractAsyncCache<OrgRepoBranchJob, Map<Job,Map<Run, Set<Stage>>>> {

    public JobRunsStagesCache(OrgRepoBranchJob key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Job,Map<Run, Set<Stage>>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getJobRunsStages(key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId());
    }
}
