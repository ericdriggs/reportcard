package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CompanyOrgRepoBranchJobRunDTO {
    private final String company;
    private final String org;
    private final String repo;
    private final String branch;
    private final Long jobId;
    private final Long runId;
}
