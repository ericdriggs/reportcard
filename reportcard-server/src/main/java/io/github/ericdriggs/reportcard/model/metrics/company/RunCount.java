package io.github.ericdriggs.reportcard.model.metrics.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.github.ericdriggs.reportcard.util.CompareUtil.chainCompare;
import static io.github.ericdriggs.reportcard.xml.ResultCount.addIntegers;

@Builder
@Jacksonized
@Data
public class RunCount implements Comparable<RunCount> {
    @Builder.Default
    Integer runs = 0;
    @Builder.Default
    Integer successfulRuns = 0;
    @Builder.Default
    Integer failedRuns = 0;

    public BigDecimal getRunSuccessPercent() {
        if (runs == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(successfulRuns * 100).divide(new BigDecimal(runs), 2, RoundingMode.HALF_UP);
    }

    @Override
    public int compareTo(RunCount that) {
        return chainCompare(
                CompareUtil.compareInteger(runs, that.runs),
                CompareUtil.compareInteger(successfulRuns, that.successfulRuns),
                CompareUtil.compareInteger(failedRuns, that.failedRuns)
        );
    }

    @JsonIgnore
    public static RunCount add(RunCount o1, RunCount o2) {
        if (o1 == null) {
            return o2;
        }
        return RunCount.builder()
                .runs(addIntegers(o1.getRuns(), o2.getRuns()))
                .successfulRuns(addIntegers(o1.getSuccessfulRuns(), o2.getSuccessfulRuns()))
                .failedRuns(addIntegers(o1.getFailedRuns(), o2.getFailedRuns()))
                .build();
    }

    @JsonIgnore
    public static List<String> diff(RunCount o1, RunCount o2) {
        if (o1 == null && o2 == null) {
            return Collections.emptyList();
        } else if (o1 == null) {
            return Collections.singletonList("runCount o1 is NULL, o2 is not NULL");
        } else if (o2 == null) {
            return Collections.singletonList("runCount o1 is not NULL, o2 is NULL");
        }

        List<String> diffs = new ArrayList<>();

        if (!Objects.equals(o1.runs, o2.runs)) {
            diffs.add("o1.runs: " + o1.runs + " != o2.runs: " + o2.runs);
        }

        if (!Objects.equals(o1.failedRuns, o2.failedRuns)) {
            diffs.add("o1.failedRuns: " + o1.failedRuns + " != o2.failedRuns: " + o2.failedRuns);
        }

        if (!Objects.equals(o1.successfulRuns, o2.successfulRuns)) {
            diffs.add("o1.successfulRuns: " + o1.successfulRuns + " != o2.successfulRuns: " + o2.successfulRuns);
        }

        return diffs;
    }
}