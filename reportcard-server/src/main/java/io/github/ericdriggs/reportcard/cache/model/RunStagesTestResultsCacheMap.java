package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.OrgRepoBranchJobRun;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;

import java.util.Map;
import java.util.Set;

public class RunStagesTestResultsCacheMap extends AbstractAsyncCacheMap<OrgRepoBranchJobRun, Map<Run,Map<Stage, Set<TestResult>>>, RunStagesTestResultsCache> {

    public static RunStagesTestResultsCacheMap INSTANCE = new RunStagesTestResultsCacheMap();

    @Override
    protected RunStagesTestResultsCache newCache(OrgRepoBranchJobRun key) {
        return new RunStagesTestResultsCache(key);
    }
}
