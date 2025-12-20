package io.github.ericdriggs.reportcard.model.pipeline;

import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import io.github.ericdriggs.reportcard.model.graph.TestResultGraph;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
@Jacksonized
@Value
public class PipelineResultCount implements Comparable<PipelineResultCount> {
    @Builder.Default
    Integer totalRuns = 0;
    @Builder.Default
    Integer passingRuns = 0;
    @Builder.Default
    Integer totalTests = 0;
    @Builder.Default
    Integer passingTests = 0;
    @Builder.Default
    Instant lastPassingRun = null;
    
    public void add(TestResultGraph testResult, RunGraph run) {
        // This would need to be mutable for accumulation
        // In practice, we'd use a mutable accumulator and convert to immutable at the end
    }
    
    public BigDecimal getJobPassPercent() {
        if (totalRuns == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(passingRuns * 100.0 / totalRuns).setScale(1, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTestPassPercent() {
        if (totalTests == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(passingTests * 100.0 / totalTests).setScale(1, RoundingMode.HALF_UP);
    }
    
    public Integer getDaysSincePassingRun() {
        if (lastPassingRun == null) return null;
        return (int) ChronoUnit.DAYS.between(lastPassingRun, Instant.now());
    }
    
    @Override
    public int compareTo(PipelineResultCount that) {
        return Integer.compare(this.totalRuns, that.totalRuns);
    }
    
    // Mutable accumulator for building
    public static class Accumulator {
        private int totalRuns = 0;
        private int passingRuns = 0;
        private int totalTests = 0;
        private int passingTests = 0;
        private Instant lastPassingRun = null;
        
        public void add(TestResultGraph testResult, RunGraph run) {
            totalRuns++;
            if (run.isSuccess()) {
                passingRuns++;
                if (lastPassingRun == null || run.runDate().isAfter(lastPassingRun)) {
                    lastPassingRun = run.runDate();
                }
            }
            
            totalTests += testResult.tests();
            if (testResult.isSuccess()) {
                passingTests += testResult.tests();
            }
        }
        
        public PipelineResultCount build() {
            return PipelineResultCount.builder()
                    .totalRuns(totalRuns)
                    .passingRuns(passingRuns)
                    .totalTests(totalTests)
                    .passingTests(passingTests)
                    .lastPassingRun(lastPassingRun)
                    .build();
        }
    }
}