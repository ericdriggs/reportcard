package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for /company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId}/run/{runId} endpoint.
 * Transforms Map&lt;RunPojo, Map&lt;StagePojo, Set&lt;TestResultPojo&gt;&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunStagesTestResultsResponse {

    private Long runId;
    private String runReference;
    private Long jobFk;
    private Integer jobRunCount;
    private String sha;
    private Instant runDate;
    private Boolean isSuccess;
    private List<StageTestResultsEntry> stages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StageTestResultsEntry {
        private Long stageId;
        private String stageName;
        private Long runFk;
        private List<TestResultEntry> testResults;

        public static StageTestResultsEntry fromPojo(StagePojo pojo, List<TestResultEntry> testResults) {
            if (pojo == null) {
                return null;
            }
            return StageTestResultsEntry.builder()
                    .stageId(pojo.getStageId())
                    .stageName(pojo.getStageName())
                    .runFk(pojo.getRunFk())
                    .testResults(testResults)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TestResultEntry {
        private Long testResultId;
        private Long stageFk;
        private Integer tests;
        private Integer skipped;
        private Integer error;
        private Integer failure;
        private BigDecimal time;
        private Instant testResultCreated;
        private String externalLinks;
        private Boolean isSuccess;
        private Boolean hasSkip;

        public static TestResultEntry fromPojo(TestResultPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return TestResultEntry.builder()
                    .testResultId(pojo.getTestResultId())
                    .stageFk(pojo.getStageFk())
                    .tests(pojo.getTests())
                    .skipped(pojo.getSkipped())
                    .error(pojo.getError())
                    .failure(pojo.getFailure())
                    .time(pojo.getTime())
                    .testResultCreated(pojo.getTestResultCreated())
                    .externalLinks(pojo.getExternalLinks())
                    .isSuccess(pojo.getIsSuccess())
                    .hasSkip(pojo.getHasSkip())
                    .build();
        }
    }

    public static RunStagesTestResultsResponse fromMap(Map<RunPojo, Map<StagePojo, Set<TestResultPojo>>> map) {
        if (map == null || map.isEmpty()) {
            return RunStagesTestResultsResponse.builder()
                    .stages(new ArrayList<>())
                    .build();
        }

        Map.Entry<RunPojo, Map<StagePojo, Set<TestResultPojo>>> runEntry = map.entrySet().iterator().next();
        RunPojo runPojo = runEntry.getKey();
        Map<StagePojo, Set<TestResultPojo>> stageTestResultsMap = runEntry.getValue();

        List<StageTestResultsEntry> stages = new ArrayList<>();
        if (stageTestResultsMap != null) {
            for (Map.Entry<StagePojo, Set<TestResultPojo>> stageEntry : stageTestResultsMap.entrySet()) {
                StagePojo stagePojo = stageEntry.getKey();
                Set<TestResultPojo> testResultPojos = stageEntry.getValue();

                List<TestResultEntry> testResults = new ArrayList<>();
                if (testResultPojos != null) {
                    for (TestResultPojo testResultPojo : testResultPojos) {
                        testResults.add(TestResultEntry.fromPojo(testResultPojo));
                    }
                }

                stages.add(StageTestResultsEntry.fromPojo(stagePojo, testResults));
            }
        }

        return RunStagesTestResultsResponse.builder()
                .runId(runPojo.getRunId())
                .runReference(runPojo.getRunReference())
                .jobFk(runPojo.getJobFk())
                .jobRunCount(runPojo.getJobRunCount())
                .sha(runPojo.getSha())
                .runDate(runPojo.getRunDate())
                .isSuccess(runPojo.getIsSuccess())
                .stages(stages)
                .build();
    }
}
