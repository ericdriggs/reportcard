package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
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
 * Response DTO for /company/{company}/org/{org}/repo/{repo}/branch/{branch} endpoint.
 * Transforms Map&lt;BranchPojo, Map&lt;JobPojo, Set&lt;RunPojo&gt;&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BranchJobsRunsResponse {

    private Integer branchId;
    private String branchName;
    private Integer repoFk;
    private Instant lastRun;
    private List<JobRunsEntry> jobs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Slf4j
    public static class JobRunsEntry {
        private static final ObjectMapper MAPPER = new ObjectMapper();

        private Long jobId;
        private JsonNode jobInfo;
        private Integer branchFk;
        private Instant lastRun;
        private List<RunEntry> runs;

        public static JobRunsEntry fromPojo(JobPojo pojo, List<RunEntry> runs) {
            if (pojo == null) {
                return null;
            }
            return JobRunsEntry.builder()
                    .jobId(pojo.getJobId())
                    .jobInfo(parseJobInfo(pojo.getJobInfo()))
                    .branchFk(pojo.getBranchFk())
                    .lastRun(pojo.getLastRun())
                    .runs(runs)
                    .build();
        }

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
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RunEntry {
        private Long runId;
        private String runReference;
        private Long jobFk;
        private Integer jobRunCount;
        private String sha;
        private Instant runDate;
        private Boolean isSuccess;

        public static RunEntry fromPojo(RunPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return RunEntry.builder()
                    .runId(pojo.getRunId())
                    .runReference(pojo.getRunReference())
                    .jobFk(pojo.getJobFk())
                    .jobRunCount(pojo.getJobRunCount())
                    .sha(pojo.getSha())
                    .runDate(pojo.getRunDate())
                    .isSuccess(pojo.getIsSuccess())
                    .build();
        }
    }

    public static BranchJobsRunsResponse fromMap(Map<BranchPojo, Map<JobPojo, Set<RunPojo>>> map) {
        if (map == null || map.isEmpty()) {
            return BranchJobsRunsResponse.builder()
                    .jobs(new ArrayList<>())
                    .build();
        }

        Map.Entry<BranchPojo, Map<JobPojo, Set<RunPojo>>> branchEntry = map.entrySet().iterator().next();
        BranchPojo branchPojo = branchEntry.getKey();
        Map<JobPojo, Set<RunPojo>> jobRunsMap = branchEntry.getValue();

        List<JobRunsEntry> jobs = new ArrayList<>();
        if (jobRunsMap != null) {
            for (Map.Entry<JobPojo, Set<RunPojo>> jobEntry : jobRunsMap.entrySet()) {
                JobPojo jobPojo = jobEntry.getKey();
                Set<RunPojo> runPojos = jobEntry.getValue();

                List<RunEntry> runs = new ArrayList<>();
                if (runPojos != null) {
                    for (RunPojo runPojo : runPojos) {
                        runs.add(RunEntry.fromPojo(runPojo));
                    }
                }

                jobs.add(JobRunsEntry.fromPojo(jobPojo, runs));
            }
        }

        return BranchJobsRunsResponse.builder()
                .branchId(branchPojo.getBranchId())
                .branchName(branchPojo.getBranchName())
                .repoFk(branchPojo.getRepoFk())
                .lastRun(branchPojo.getLastRun())
                .jobs(jobs)
                .build();
    }
}
