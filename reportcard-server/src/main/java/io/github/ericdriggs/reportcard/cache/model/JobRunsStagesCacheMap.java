package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJob;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;

import java.util.Map;
import java.util.Set;

public class JobRunsStagesCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchJob, Map<Job, Map<Run, Set<Stage>>>, JobRunsStagesCache> {

    public static JobRunsStagesCacheMap INSTANCE = new JobRunsStagesCacheMap();

    @Override
    protected JobRunsStagesCache newCache(CompanyOrgRepoBranchJob key) {
        return new JobRunsStagesCache(key);
    }

}
