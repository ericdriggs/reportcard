package io.github.ericdriggs.reportcard.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompanyDTO {
    private final String company;

}
