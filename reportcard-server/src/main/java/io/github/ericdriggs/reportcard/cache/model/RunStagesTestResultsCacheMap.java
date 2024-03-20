package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.cache.AbstractAsyncCacheMap;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunDTO;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;

import java.util.Map;
import java.util.Set;

public class RunStagesTestResultsCacheMap extends AbstractAsyncCacheMap<CompanyOrgRepoBranchJobRunDTO, Map<RunPojo,Map<StagePojo, Set<TestResultPojo>>>, RunStagesTestResultsCache> {

    public static RunStagesTestResultsCacheMap INSTANCE = new RunStagesTestResultsCacheMap();

    @Override
    protected RunStagesTestResultsCache newCache(CompanyOrgRepoBranchJobRunDTO key) {
        return new RunStagesTestResultsCache(key);
    }
}
