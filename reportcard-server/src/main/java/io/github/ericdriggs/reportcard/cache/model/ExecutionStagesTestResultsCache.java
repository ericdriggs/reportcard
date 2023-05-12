package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCache;
import io.github.ericdriggs.reportcard.cache.CacheDuration;
import io.github.ericdriggs.reportcard.cache.SyncAsyncDuration;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJob;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJobExecution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Execution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult;

import java.util.Map;
import java.util.Set;

public class ExecutionStagesTestResultsCache extends AbstractAsyncCache<OrgRepoBranchJobExecution, Map<Execution,Map<Stage, Set<TestResult>>>> {

    public ExecutionStagesTestResultsCache(OrgRepoBranchJobExecution key) {
        super(key);
    }

    @Override
    protected SyncAsyncDuration getSyncAsyncDuration() {
        return CacheDuration.MINUTES(5);
    }

    @Override
    protected Map<Execution,Map<Stage, Set<TestResult>>> getUpdatedCacheValue() {
        return StaticReportCardService.INSTANCE.getExecutionStagesTestResults(key.getOrg(), key.getRepo(), key.getBranch(), key.getJobId(), key.getExecutionId());
    }
}
