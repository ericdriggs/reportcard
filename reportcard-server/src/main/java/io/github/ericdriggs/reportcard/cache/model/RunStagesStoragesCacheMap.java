package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;

import java.util.Map;
import java.util.Set;

public class RunStagesStoragesCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchJobRunDTO, Map<RunPojo, Map<StagePojo, Set<StoragePojo>>>, RunStagesStoragesCache> {

    public static RunStagesStoragesCacheMap INSTANCE = new RunStagesStoragesCacheMap();

    @Override
    protected RunStagesStoragesCache newCache(CompanyOrgRepoBranchJobRunDTO key) {
        return new RunStagesStoragesCache(key);
    }
}
