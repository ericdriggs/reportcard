package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;

import java.util.Map;
import java.util.Set;

public class JobRunsStagesCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchJobDTO, Map<JobPojo, Map<RunPojo, Set<StagePojo>>>, JobRunsStagesCache> {

    public static JobRunsStagesCacheMap INSTANCE = new JobRunsStagesCacheMap();

    @Override
    protected JobRunsStagesCache newCache(CompanyOrgRepoBranchJobDTO key) {
        return new JobRunsStagesCache(key);
    }

}
