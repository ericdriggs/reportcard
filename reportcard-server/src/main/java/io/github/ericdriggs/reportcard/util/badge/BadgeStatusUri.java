package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.net.URI;
import java.time.Instant;

@Builder
@Jacksonized
@Value
public class BadgeStatusUri {
    BadgeStatus badgeStatus;
    URI uri;

    public static BadgeStatusUri fromRunGraph(RunGraph runGraph, CompanyOrgRepoBranchJobRunStageDTO path) {

        BadgeStatus badgeStatus = runGraph.isSuccess() ? BadgeStatus.PASS : BadgeStatus.FAIL;
        return BadgeStatusUri
                .builder()
                .badgeStatus(badgeStatus)
                .uri(URI.create(path.toUrlPath()))
                .build();
    }

}
