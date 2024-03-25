package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.model.StageTestResultPojo;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class BranchStageViewResponse {
    CompanyOrgRepoBranch companyOrgRepoBranch;
    Map<JobRun, Map<StageTestResultPojo, Set<StoragePojo>>> jobRun_StageTestResult_StoragesMap;
}
