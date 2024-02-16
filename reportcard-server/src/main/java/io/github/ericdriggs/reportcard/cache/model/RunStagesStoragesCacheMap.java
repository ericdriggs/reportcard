package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRun;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;

import java.util.Map;
import java.util.Set;

public class RunStagesStoragesCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchJobRun, Map<Run,Map<Stage, Set<Storage>>>, RunStagesStoragesCache> {

    public static RunStagesStoragesCacheMap INSTANCE = new RunStagesStoragesCacheMap();

    @Override
    protected RunStagesStoragesCache newCache(CompanyOrgRepoBranchJobRun key) {
        return new RunStagesStoragesCache(key);
    }
}
