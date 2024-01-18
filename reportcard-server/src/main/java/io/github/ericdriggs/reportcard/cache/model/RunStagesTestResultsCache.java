package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRun;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult;

import java.util.Map;
import java.util.Set;

public class RunStagesTestResultsCache extends AbstractAsyncCache<CompanyOrgRepoBranchJobRun, Map<Run,Map<Stage, Set<TestResult>>>> {

    public RunStagesTestResultsCache(CompanyOrgRepoBranchJobRun key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Run,Map<Stage, Set<TestResult>>> getUpdatedCacheValue() {
        return StaticBrowseService.getInstance().getRunStagesTestResults(key.getCompany(), key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId(), key.getRunId());
    }
}
