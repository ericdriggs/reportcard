package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompanyOrgRepoDTO {
    private final String company;
    private final String org;
    private final String repo;
}
