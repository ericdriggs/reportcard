package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class RunStagesTestResultsCache extends AbstractAsyncCache<CompanyOrgRepoBranchJobRunDTO, Map<RunPojo,Map<StagePojo, Set<TestResultPojo>>>> {

    public RunStagesTestResultsCache(CompanyOrgRepoBranchJobRunDTO key) {
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
    protected Map<RunPojo,Map<StagePojo, Set<TestResultPojo>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getRunStagesTestResults(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId(), key.getRunId());
    }
}
