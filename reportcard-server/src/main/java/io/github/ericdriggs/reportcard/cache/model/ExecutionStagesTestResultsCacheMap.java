package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranch;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJobExecution;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;

import java.util.Map;
import java.util.Set;

public class ExecutionStagesTestResultsCacheMap extends AbstractAsyncCacheMap<OrgRepoBranchJobExecution, Map<Execution,Map<Stage, Set<TestResult>>>, ExecutionStagesTestResultsCache> {

    public static ExecutionStagesTestResultsCacheMap INSTANCE = new ExecutionStagesTestResultsCacheMap();

    @Override
    protected ExecutionStagesTestResultsCache newCache(OrgRepoBranchJobExecution key) {
        return new ExecutionStagesTestResultsCache(key);
    }
}
