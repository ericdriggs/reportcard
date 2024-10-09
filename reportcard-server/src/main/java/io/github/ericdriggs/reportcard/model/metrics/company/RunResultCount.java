package io.github.ericdriggs.reportcard.model.metrics.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.graph.TestResultGraph;
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

@Data
@Jacksonized
@Builder(toBuilder = true)
public class RunResultCount implements Comparable<RunResultCount> {
    @Builder.Default
    Integer runs = 0;
    @Builder.Default
    Integer successfulRuns = 0;
    @Builder.Default
    Integer failedRuns = 0;

    @Builder.Default
    Integer errors = 0;
    @Builder.Default
    Integer failures = 0;
    @Builder.Default
    Integer skipped = 0;
    @Builder.Default
    Integer successes = 0;
    @Builder.Default
    Integer tests = 0;
    @Builder.Default
    BigDecimal time = BigDecimal.ZERO;

    public static RunResultCount fromTestResultGraph(TestResultGraph testResultGraph) {

        return RunResultCount.builder()
                .runs(1)
                .successfulRuns(testResultGraph.isSuccess() ? 1 : 0)
                .failedRuns(testResultGraph.isSuccess() ? 0 : 1)
                .errors(testResultGraph.error())
                .failures(testResultGraph.failure())
                .skipped(testResultGraph.skipped())
                .successes(testResultGraph.tests() - testResultGraph.error() - testResultGraph.failure() - testResultGraph.skipped())
                .tests(testResultGraph.tests())
                .time(testResultGraph.time())
                .build();
    }


    @JsonIgnore
    public TestStatus getTestStatus() {
        if (errors > 0) {
            return TestStatus.ERROR;
        } else if (failures > 0) {
            return TestStatus.FAILURE;
        } else if (skipped > 0) {
            return TestStatus.SKIPPED;
        }
        return TestStatus.SUCCESS;
    }

    public Integer getErrorsAndFailures() {
        return errors + failures;
    }

    /**
     * Adds a RunResultCount to the current RunResultCount
     *
     * @param that a ResultCount
     * @return a new ResultCount sum of this and that
     */
    @JsonIgnore
    public void add(RunResultCount that) {

        runs = addIntegers(runs, that.runs);
        successfulRuns = addIntegers(successfulRuns, that.successfulRuns);
        failedRuns = addIntegers(failedRuns, that.failedRuns);

        errors = addIntegers(errors, that.getErrors());
        failures = addIntegers(failures, that.getFailures());
        skipped = addIntegers(skipped, that.getSkipped());
        successes = addIntegers(successes, that.getSuccesses());
        tests = addIntegers(tests, that.getTests());
        time = addBigDecimal(time, that.getTime());
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

    /**
     * @return percent of tests which passed (skipped tests are excluded from total)
     */
    @JsonProperty("passedPercent")
    public BigDecimal getPassedPercent() {

        if (tests == null || tests == 0) {
            return BigDecimal.ZERO;
        }

        final int skippedCount = skipped == null ? 0 : skipped;
        if (tests - skippedCount == 0) {
            return BigDecimal.ZERO;
        }
        final int successCount = successes == null ? 0 : successes;

        final int errorCount = errors == null ? 0 : errors;
        final int failureCount = failures == null ? 0 : failures;


        //@SuppressWarnings("WrapperTypeMayBePrimitive")
        final int errorFailureCount = errorCount + failureCount;

        try {
            return BigDecimal.valueOf(
                    100 * (double) successCount /
                            (double) (tests - skippedCount)
            ).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @JsonIgnore
    private Integer zeroIfNull(Integer val) {
        if (val == null) {
            return 0;
        }
        return val;
    }

    @JsonIgnore
    private BigDecimal zeroIfNull(BigDecimal val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        return val;
    }

    /**
     * Adds two Integers
     *
     * @param thiz an Integer, may be null
     * @param that an Integer, may be null
     * @return an Integer sum of thiz and that, may be 0 but never null
     */
    @JsonIgnore
    private static Integer addIntegers(Integer thiz, Integer that) {
        if (thiz == null) {
            thiz = 0;
        }
        if (that == null) {
            that = 0;
        }
        return thiz + that;
    }

    /**
     * Adds two Integers
     *
     * @param thiz an Integer, may be null
     * @param that an Integer, may be null
     * @return an Integer sum of thiz and that, may be zero, never null
     */
    @JsonIgnore
    private static BigDecimal addBigDecimal(BigDecimal thiz, BigDecimal that) {
        if (thiz == null) {
            thiz = BigDecimal.ZERO;
        }
        if (that == null) {
            that = BigDecimal.ZERO;
        }
        return thiz.add(that);
    }

    @JsonIgnore
    @Override
    public int compareTo(RunResultCount that) {
        if (that == null) {
            return 1;
        }
        return chainCompare(
                errors.compareTo(that.errors),
                failures.compareTo(that.failures),
                skipped.compareTo(that.skipped),
                successes.compareTo(that.successes),
                tests.compareTo(that.tests),
                //round time for comparison since precision may change after persist/read
                CompareUtil.compareBigDecimalAsBigInteger(this.time, that.time)
        );
    }

    @JsonIgnore
    public static List<String> diff(RunResultCount o1, RunResultCount o2) {
        if (o1 == null && o2 == null) {
            return Collections.emptyList();
        } else if (o1 == null) {
            return Collections.singletonList("o1 is NULL, o2 is not NULL");
        } else if (o2 == null) {
            return Collections.singletonList("o1 is not NULL, o2 is NULL");
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

        if (!Objects.equals(o1.errors, o2.errors)) {
            diffs.add("o1.errors: " + o1.errors + " != o2.errors: " + o2.errors);
        }

        if (!Objects.equals(o1.failures, o2.failures)) {
            diffs.add("o1.failures: " + o1.failures + " != o2.failures: " + o2.failures);
        }

        if (!Objects.equals(o1.skipped, o2.skipped)) {
            diffs.add("o1.skipped: " + o1.skipped + " != o2.skipped: " + o2.skipped);
        }

        if (!Objects.equals(o1.tests, o2.tests)) {
            diffs.add("o1.tests: " + o1.tests + " != o2.tests: " + o2.tests);
        }

        if (!Objects.equals(o1.successes, o2.successes)) {
            diffs.add("o1.successes: " + o1.successes + " != o2.successes: " + o2.successes);
        }

        //round time for comparison since precision may change after persist/read
        if (CompareUtil.compareBigDecimalAsBigInteger(o1.time, o2.time) != 0) {
            diffs.add("o1.time: " + o1.time + " != o2.time: " + o2.time);
        }
        return diffs;
    }


}