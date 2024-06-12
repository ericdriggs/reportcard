package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record JobGraph(Long jobId,
                       Map<String, String> jobInfo,
                       Integer branchFk,
                       String jobInfoStr,
                       Instant lastRun,
                       List<RunGraph> runs) implements JobGraphBuilder.With {

    @JsonIgnore
    public JobPojo asJobPojo() {
        return JobPojo.builder().jobId(jobId).jobInfo(jobInfoStr).jobInfoStr(jobInfoStr).lastRun(lastRun).build();
    }
}
