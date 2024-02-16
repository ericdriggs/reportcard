package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder(toBuilder = true)
public class BranchStageViewResponse {
    CompanyOrgRepoBranch companyOrgRepoBranch;
    Map<Job, Map<Run, Map<Stage,TestResultStorages>>> jobRunStageResultsMap;
}
