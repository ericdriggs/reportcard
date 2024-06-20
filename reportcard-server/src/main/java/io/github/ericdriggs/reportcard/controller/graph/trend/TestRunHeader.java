package io.github.ericdriggs.reportcard.controller.graph.trend;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.model.trend.CompanyOrgRepoBranchJob;
import io.github.ericdriggs.reportcard.model.trend.StageName;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Builder
@Jacksonized
@Value
public class TestRunHeader implements Comparable<TestRunHeader> {
    long runId;
    long jobRunCount;
    String runUri;
    Instant runDate;

    public static TestRunHeader fromRunPojo(CompanyOrgRepoBranchJob c, StageName stageName, RunPojo runPojo) {

        final String runUri = CompanyOrgRepoBranchJobRunStageDTO
                .builder()
                .company(c.getCompanyPojo().getCompanyName())
                .org(c.getOrgPojo().getOrgName())
                .repo(c.getRepoPojo().getRepoName())
                .branch(c.getBranchPojo().getBranchName())
                .jobId(c.getJobPojo().getJobId())
                .stageName(stageName.getStageName())
                .runId(runPojo.getRunId())
                .build()
                .toUrlPath();
        return TestRunHeader.builder()
                            .jobRunCount(runPojo.getJobRunCount())
                            .runId(runPojo.getRunId())
                            .runDate(runPojo.getRunDate())
                            .runUri(runUri)
                            .build();
    }

    @Override
    public int compareTo(TestRunHeader that) {
        //in descending order
        return Long.compare(that.getRunId(), this.getRunId());
    }
}
