package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder

public record TestResultGraph(
        Long testResultId,
        Long stageFk,
        Integer tests,
        Integer skipped,
        Integer error,
        Integer failure,
        BigDecimal time,
        Instant testResultCreated,
        String externalLinks,
        Boolean isSuccess,
        Boolean hasSkip,
        List<TestSuiteGraph> testSuites
) implements TestResultGraphBuilder.With {
    @JsonIgnore
    public TestResultPojo asTestResultPojo() {
        return TestResultPojo
                .builder()
                .testResultId(testResultId)
                .stageFk(stageFk)
                .tests(tests)
                .skipped(skipped)
                .error(error)
                .failure(failure)
                .time(time)
                .testResultCreated(testResultCreated)
                .externalLinks(externalLinks)
                .isSuccess(isSuccess)
                .hasSkip(hasSkip)
                .build();

    }
}
