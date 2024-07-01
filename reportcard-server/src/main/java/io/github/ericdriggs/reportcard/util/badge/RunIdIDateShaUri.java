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
public class RunIdIDateShaUri {
    Integer runCount;
    Instant runDate;
    String sha;
    URI uri;

    public static RunIdIDateShaUri fromRunGraph(RunGraph runGraph, CompanyOrgRepoBranchJobRunStageDTO path) {

        return RunIdIDateShaUri
                .builder()
                .runCount(runGraph.jobRunCount())
                .runDate(runGraph.runDate())
                .sha(runGraph.sha())
                .uri(URI.create(path.toUrlPath()))
                .build();
    }
}
