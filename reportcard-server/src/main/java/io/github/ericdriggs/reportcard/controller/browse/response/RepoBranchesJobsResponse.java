package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for /company/{company}/org/{org}/repo/{repo} endpoint.
 * Transforms Map&lt;RepoPojo, Map&lt;BranchPojo, Set&lt;JobPojo&gt;&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepoBranchesJobsResponse {

    private Integer repoId;
    private String repoName;
    private Integer orgFk;
    private List<BranchJobsEntry> branches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BranchJobsEntry {
        private Integer branchId;
        private String branchName;
        private Integer repoFk;
        private Instant lastRun;
        private List<JobEntry> jobs;

        public static BranchJobsEntry fromPojo(BranchPojo pojo, List<JobEntry> jobs) {
            if (pojo == null) {
                return null;
            }
            return BranchJobsEntry.builder()
                    .branchId(pojo.getBranchId())
                    .branchName(pojo.getBranchName())
                    .repoFk(pojo.getRepoFk())
                    .lastRun(pojo.getLastRun())
                    .jobs(jobs)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JobEntry {
        private static final ObjectMapper MAPPER = new ObjectMapper();

        private Long jobId;
        private JsonNode jobInfo;
        private Integer branchFk;
        private Instant lastRun;

        public static JobEntry fromPojo(JobPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return JobEntry.builder()
                    .jobId(pojo.getJobId())
                    .jobInfo(parseJobInfo(pojo.getJobInfo()))
                    .branchFk(pojo.getBranchFk())
                    .lastRun(pojo.getLastRun())
                    .build();
        }

        private static JsonNode parseJobInfo(String jobInfoStr) {
            if (jobInfoStr == null || jobInfoStr.isEmpty()) {
                return null;
            }
            try {
                return MAPPER.readTree(jobInfoStr);
            } catch (JsonProcessingException e) {
                return MAPPER.getNodeFactory().textNode(jobInfoStr);
            }
        }
    }

    public static RepoBranchesJobsResponse fromMap(Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>> map) {
        if (map == null || map.isEmpty()) {
            return RepoBranchesJobsResponse.builder()
                    .branches(new ArrayList<>())
                    .build();
        }

        Map.Entry<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoEntry = map.entrySet().iterator().next();
        RepoPojo repoPojo = repoEntry.getKey();
        Map<BranchPojo, Set<JobPojo>> branchJobsMap = repoEntry.getValue();

        List<BranchJobsEntry> branches = new ArrayList<>();
        if (branchJobsMap != null) {
            for (Map.Entry<BranchPojo, Set<JobPojo>> branchEntry : branchJobsMap.entrySet()) {
                BranchPojo branchPojo = branchEntry.getKey();
                Set<JobPojo> jobPojos = branchEntry.getValue();

                List<JobEntry> jobs = new ArrayList<>();
                if (jobPojos != null) {
                    for (JobPojo jobPojo : jobPojos) {
                        jobs.add(JobEntry.fromPojo(jobPojo));
                    }
                }

                branches.add(BranchJobsEntry.fromPojo(branchPojo, jobs));
            }
        }

        return RepoBranchesJobsResponse.builder()
                .repoId(repoPojo.getRepoId())
                .repoName(repoPojo.getRepoName())
                .orgFk(repoPojo.getOrgFk())
                .branches(branches)
                .build();
    }
}
