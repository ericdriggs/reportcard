package io.github.ericdriggs.reportcard.model.publication;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Map;

@Builder
@Jacksonized
@Value
public class FailedPublication {
    String companyName;
    String orgName;
    String repoName;
    String branchName;
    Long jobId;
    String jobInfo;
    Long runId;
    String runReference;
    String sha;
    Instant runDate;
    Long stageId;
    String stageName;
    Map<String, FailedPublicationStorage> storages;
}
