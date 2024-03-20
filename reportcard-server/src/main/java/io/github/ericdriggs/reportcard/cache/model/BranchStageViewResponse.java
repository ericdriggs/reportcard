package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class BranchStageViewResponse {
    CompanyOrgRepoBranch companyOrgRepoBranch;
    Map<JobRun, Map<StageTestResult, Set<StoragePojo>>> jobRun_StageTestResult_StoragesMap;
}
