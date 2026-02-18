package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

/**
 * Response for tag-based test queries.
 * Structure mirrors the schema hierarchy: org -> repo -> branch -> job -> run -> stage -> test.
 * The root collection type depends on query scope.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagQueryResponse {

    private QueryInfo query;

    // Results at different scope levels - only one will be populated
    private List<OrgResult> orgs;       // populated when querying at company scope
    private List<RepoResult> repos;     // populated when querying at org scope
    private List<BranchResult> branches; // populated when querying at repo scope
    private List<JobResult> jobs;       // populated when querying at branch or sha scope

    @Data
    @Builder
    public static class QueryInfo {
        private String scope;
        private String tags;
    }

    @Data
    @Builder
    public static class OrgResult {
        private Integer orgId;
        private String orgName;
        private List<RepoResult> repos;
    }

    @Data
    @Builder
    public static class RepoResult {
        private Integer repoId;
        private String repoName;
        private List<BranchResult> branches;
    }

    @Data
    @Builder
    public static class BranchResult {
        private Integer branchId;
        private String branchName;
        private List<JobResult> jobs;
    }

    @Data
    @Builder
    public static class JobResult {
        private Long jobId;
        private TreeMap<String, String> jobInfo;
        private List<RunResult> runs;
    }

    @Data
    @Builder
    public static class RunResult {
        private Long runId;
        private String sha;
        private Instant runDate;
        private List<StageResult> stages;
    }

    @Data
    @Builder
    public static class StageResult {
        private Long stageId;
        private String stageName;
        private List<TestInfo> tests;
    }

    @Data
    @Builder
    public static class TestInfo {
        private String testName;
        private String className;
        private String status;
    }
}
