package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;

import java.util.Map;
import java.util.Set;

public class RunStagesStoragesCache extends AbstractAsyncCache<CompanyOrgRepoBranchJobRunDTO, Map<Run,Map<Stage, Set<Storage>>>> {

    public RunStagesStoragesCache(CompanyOrgRepoBranchJobRunDTO key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Run,Map<Stage, Set<Storage>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getRunStagesStorages(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId(), key.getRunId());
    }
}
