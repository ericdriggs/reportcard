package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response for tag-based test queries.
 * Tests grouped by hierarchy, latest run per job.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagQueryResponse {

    /**
     * Query parameters that produced this response.
     */
    private QueryInfo query;

    /**
     * Results grouped by remaining hierarchy levels.
     * Structure varies based on query scope:
     * - Company scope: branch -> sha -> job -> JobResult
     * - Org scope: branch -> sha -> job -> JobResult
     * - Repo scope: branch -> sha -> job -> JobResult
     * - Branch scope: sha -> job -> JobResult
     * - Sha scope: job -> JobResult
     */
    private Map<String, Map<String, Map<String, JobResult>>> results;

    @Data
    @Builder
    public static class QueryInfo {
        private String scope;
        private String tags;
    }

    @Data
    @Builder
    public static class JobResult {
        private Instant runDate;
        private List<String> tests;
    }
}
