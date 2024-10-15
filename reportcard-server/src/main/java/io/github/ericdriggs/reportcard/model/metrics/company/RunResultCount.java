package io.github.ericdriggs.reportcard.model.metrics.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.graph.TestResultGraph;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.github.ericdriggs.reportcard.util.CompareUtil.chainCompare;
import static io.github.ericdriggs.reportcard.xml.ResultCount.addIntegers;

@Data
@Jacksonized
@Builder(toBuilder = true)
public class RunResultCount implements Comparable<RunResultCount> {
    RunCount runCount;
    ResultCount resultCount;

    @JsonIgnore
    @Override
    public int compareTo(RunResultCount that) {
        if (that == null) {
            return 1;
        }
        return chainCompare(
                ObjectUtils.compare(runCount, that.runCount),
                ObjectUtils.compare(resultCount, that.resultCount)
        );
    }

    public static RunResultCount fromTestResultGraph(TestResultGraph testResultGraph) {

        ResultCount resultCount = ResultCount.builder()
                .errors(testResultGraph.error())
                .failures(testResultGraph.failure())
                .skipped(testResultGraph.skipped())
                .successes(testResultGraph.tests() - testResultGraph.error() - testResultGraph.failure() - testResultGraph.skipped())
                .tests(testResultGraph.tests())
                .time(testResultGraph.time())
                .build();

        RunCount runCount = RunCount.builder()
                .runs(1)
                .successfulRuns(testResultGraph.isSuccess() ? 1 : 0)
                .failedRuns(testResultGraph.isSuccess() ? 0 : 1)
                .build();

        return RunResultCount.builder()
                .runCount(runCount)
                .resultCount(resultCount)
                .build();
    }

    /**
     * Adds a RunResultCount to the current RunResultCount
     *
     * @param that a ResultCount
     * @return a new ResultCount sum of this and that
     */
    @JsonIgnore
    public void add(RunResultCount that) {
        runCount = RunCount.add(runCount, that.runCount);
        resultCount = ResultCount.add(resultCount, that.resultCount);
    }

    @JsonIgnore
    public void add(TestResultGraph that) {
        add(RunResultCount.fromTestResultGraph(that));
    }

    @JsonIgnore
    public static RunResultCount aggregate(List<RunResultCount> resultCounts) {
        RunResultCount resultCount = RunResultCount.builder().build();
        for (RunResultCount r : resultCounts) {
            resultCount.add(r);
        }
        return resultCount;
    }

    @JsonIgnore
    public static List<String> diff(RunResultCount o1, RunResultCount o2) {
        if (o1 == null && o2 == null) {
            return Collections.emptyList();
        } else if (o1 == null) {
            return Collections.singletonList("runResultCount o1 is NULL, o2 is not NULL");
        } else if (o2 == null) {
            return Collections.singletonList("runResultCount o1 is not NULL, o2 is NULL");
        }

        List<String> diffs = new ArrayList<>();

        diffs.addAll(RunCount.diff(o1.runCount, o2.runCount));
        diffs.addAll(ResultCount.diff(o1.resultCount, o2.resultCount));
        return diffs;
    }


}