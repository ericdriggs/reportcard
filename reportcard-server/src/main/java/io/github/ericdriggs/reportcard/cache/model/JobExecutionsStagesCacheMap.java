package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJob;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;

import java.util.Map;
import java.util.Set;

public class JobExecutionsStagesCacheMap extends AbstractAsyncCacheMap<OrgRepoBranchJob, Map<Job, Map<Execution, Set<Stage>>>, JobExecutionsStagesCache> {

    public static JobExecutionsStagesCacheMap INSTANCE = new JobExecutionsStagesCacheMap();

    @Override
    protected JobExecutionsStagesCache newCache(OrgRepoBranchJob key) {
        return new JobExecutionsStagesCache(key);
    }

}
