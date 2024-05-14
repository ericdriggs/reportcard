package io.github.ericdriggs.reportcard.model.trend;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.TreeMap;

@Builder
@Jacksonized
@Value
public class JobTestTrend {

    CompanyOrgRepoBranchJob companyOrgRepoBranchJob;
    StageName stageName;
    TreeMap<TestCaseName, TreeMap<RunPojo, TrendTestCase>> testCaseTrends;
    InstantRange range;
    Instant generated;
}
