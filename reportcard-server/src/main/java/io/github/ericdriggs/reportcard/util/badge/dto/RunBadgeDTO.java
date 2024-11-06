package io.github.ericdriggs.reportcard.util.badge.dto;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import io.github.ericdriggs.reportcard.util.badge.BadgeStatus;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.net.URI;
import java.time.Instant;

@Builder
@Jacksonized
@Value
public class RunBadgeDTO {
    BadgeStatus badgeStatus;
    Instant runDate;
    String sha;
    URI uri;


    public static RunBadgeDTO fromRunGraph(RunGraph runGraph, CompanyOrgRepoBranchJobRunStageDTO path) {

        BadgeStatus badgeStatus = BadgeStatus.fromRunGraph(runGraph);
        return RunBadgeDTO
                .builder()
                .badgeStatus(badgeStatus)
                .runDate(runGraph.runDate())
                .sha(runGraph.sha())
                .uri(URI.create(path.toUrlPath()))
                .build();
    }

}
