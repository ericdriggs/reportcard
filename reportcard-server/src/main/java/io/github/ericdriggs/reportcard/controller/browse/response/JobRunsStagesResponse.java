package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for /company/{company}/org/{org}/repo/{repo}/branch/{branch}/job/{jobId} endpoint.
 * Transforms Map&lt;JobPojo, Map&lt;RunPojo, Set&lt;StagePojo&gt;&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class JobRunsStagesResponse {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long jobId;
    private JsonNode jobInfo;
    private Integer branchFk;
    private Instant lastRun;
    private List<RunStagesEntry> runs;

    private static JsonNode parseJobInfo(String jobInfoStr) {
        if (jobInfoStr == null || jobInfoStr.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(jobInfoStr);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse jobInfo as JSON, returning as text. jobInfo='{}', error='{}'",
                    jobInfoStr, e.getMessage());
            return MAPPER.getNodeFactory().textNode(jobInfoStr);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RunStagesEntry {
        private Long runId;
        private String runReference;
        private Long jobFk;
        private Integer jobRunCount;
        private String sha;
        private Instant runDate;
        private Boolean isSuccess;
        private List<StageEntry> stages;

        public static RunStagesEntry fromPojo(RunPojo pojo, List<StageEntry> stages) {
            if (pojo == null) {
                return null;
            }
            return RunStagesEntry.builder()
                    .runId(pojo.getRunId())
                    .runReference(pojo.getRunReference())
                    .jobFk(pojo.getJobFk())
                    .jobRunCount(pojo.getJobRunCount())
                    .sha(pojo.getSha())
                    .runDate(pojo.getRunDate())
                    .isSuccess(pojo.getIsSuccess())
                    .stages(stages)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StageEntry {
        private Long stageId;
        private String stageName;
        private Long runFk;

        public static StageEntry fromPojo(StagePojo pojo) {
            if (pojo == null) {
                return null;
            }
            return StageEntry.builder()
                    .stageId(pojo.getStageId())
                    .stageName(pojo.getStageName())
                    .runFk(pojo.getRunFk())
                    .build();
        }
    }

    public static JobRunsStagesResponse fromMap(Map<JobPojo, Map<RunPojo, Set<StagePojo>>> map) {
        if (map == null || map.isEmpty()) {
            return JobRunsStagesResponse.builder()
                    .runs(new ArrayList<>())
                    .build();
        }

        Map.Entry<JobPojo, Map<RunPojo, Set<StagePojo>>> jobEntry = map.entrySet().iterator().next();
        JobPojo jobPojo = jobEntry.getKey();
        Map<RunPojo, Set<StagePojo>> runStagesMap = jobEntry.getValue();

        List<RunStagesEntry> runs = new ArrayList<>();
        if (runStagesMap != null) {
            for (Map.Entry<RunPojo, Set<StagePojo>> runEntry : runStagesMap.entrySet()) {
                RunPojo runPojo = runEntry.getKey();
                Set<StagePojo> stagePojos = runEntry.getValue();

                List<StageEntry> stages = new ArrayList<>();
                if (stagePojos != null) {
                    for (StagePojo stagePojo : stagePojos) {
                        stages.add(StageEntry.fromPojo(stagePojo));
                    }
                }

                runs.add(RunStagesEntry.fromPojo(runPojo, stages));
            }
        }

        return JobRunsStagesResponse.builder()
                .jobId(jobPojo.getJobId())
                .jobInfo(parseJobInfo(jobPojo.getJobInfo()))
                .branchFk(jobPojo.getBranchFk())
                .lastRun(jobPojo.getLastRun())
                .runs(runs)
                .build();
    }
}
