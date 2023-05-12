package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJob;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class JobExecutionsStagesCache extends AbstractAsyncCache<OrgRepoBranchJob, Map<Job,Map<Execution, Set<Stage>>>> {

    public JobExecutionsStagesCache(OrgRepoBranchJob key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTE;
    }

    @Override
    protected Map<Job,Map<Execution, Set<Stage>>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getJobExecutionsStages(key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId(), Collections.EMPTY_MAP);
    }
}
