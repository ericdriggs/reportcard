package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrgRepoBranchJob {
    private final String org;
    private final String repo;
    private final String branch;
    private final Long jobId;
}
