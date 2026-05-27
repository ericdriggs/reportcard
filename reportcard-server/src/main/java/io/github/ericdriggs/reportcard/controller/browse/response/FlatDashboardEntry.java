package io.github.ericdriggs.reportcard.controller.browse.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
@Jacksonized
public class FlatDashboardEntry {
    String company;
    String org;
    String repo;
    String branch;
    Long jobId;
    Map<String, String> jobInfo;
    Long runId;
    Integer jobRunCount;
    String sha;
    Instant runDate;
    Boolean isSuccess;
    String url;
    String stageName;
    Map<String, String> storageUrls;
}
