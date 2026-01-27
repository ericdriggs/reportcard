package io.github.ericdriggs.reportcard.model.pipeline;

import io.github.ericdriggs.reportcard.model.graph.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class JobDashboardMetricsTest {

    @Test
    void testAvgRunDuration_withValidTiming() {
        // Create CompanyGraph with 2 runs, each run has 1 stage with 1 test_result
        // Run 1: start=T0, end=T0+100s -> 100s duration
        // Run 2: start=T0, end=T0+200s -> 200s duration
        // Expected avgRunDuration: 150.00 seconds

        Instant t0 = Instant.parse("2024-01-01T00:00:00Z");

        TestResultGraph testResult1 = TestResultGraphBuilder
                .builder()
                .testResultId(1L)
                .stageFk(1L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(100))
                .testResultCreated(t0)
                .startTime(t0)
                .endTime(t0.plusSeconds(100))
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        TestResultGraph testResult2 = TestResultGraphBuilder
                .builder()
                .testResultId(2L)
                .stageFk(2L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(200))
                .testResultCreated(t0)
                .startTime(t0)
                .endTime(t0.plusSeconds(200))
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        StageGraph stage1 = StageGraphBuilder
                .builder()
                .stageId(1L)
                .stageName("test")
                .runFk(1L)
                .testResults(List.of(testResult1))
                .build();

        StageGraph stage2 = StageGraphBuilder
                .builder()
                .stageId(2L)
                .stageName("test")
                .runFk(2L)
                .testResults(List.of(testResult2))
                .build();

        RunGraph run1 = RunGraphBuilder
                .builder()
                .runId(1L)
                .jobFk(1L)
                .jobRunCount(1)
                .runDate(t0)
                .isSuccess(true)
                .sha("abc123")
                .stages(List.of(stage1))
                .build();

        RunGraph run2 = RunGraphBuilder
                .builder()
                .runId(2L)
                .jobFk(1L)
                .jobRunCount(2)
                .runDate(t0.plusSeconds(300))
                .isSuccess(true)
                .sha("def456")
                .stages(List.of(stage2))
                .build();

        CompanyGraph companyGraph = createCompanyGraph(List.of(run1, run2));

        JobDashboardRequest request = JobDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .build();

        List<JobDashboardMetrics> metrics = JobDashboardMetrics.fromCompanyGraphs(List.of(companyGraph), request);

        assertEquals(1, metrics.size());
        JobDashboardMetrics metric = metrics.get(0);
        assertNotNull(metric.getAvgRunDuration());
        assertEquals(new BigDecimal("150.00"), metric.getAvgRunDuration());
    }

    @Test
    void testAvgRunDuration_withNullTiming() {
        // Create CompanyGraph with 3 runs
        // Run 1: start=NULL, end=NULL (old data)
        // Run 2: start=T0, end=T0+100s -> 100s duration
        // Run 3: start=T0, end=T0+200s -> 200s duration
        // Expected avgRunDuration: 150.00 seconds (Run 1 excluded from average)

        Instant t0 = Instant.parse("2024-01-01T00:00:00Z");

        TestResultGraph testResult1 = TestResultGraphBuilder
                .builder()
                .testResultId(1L)
                .stageFk(1L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(100))
                .testResultCreated(t0)
                .startTime(null)  // NULL timing
                .endTime(null)    // NULL timing
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        TestResultGraph testResult2 = TestResultGraphBuilder
                .builder()
                .testResultId(2L)
                .stageFk(2L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(100))
                .testResultCreated(t0)
                .startTime(t0)
                .endTime(t0.plusSeconds(100))
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        TestResultGraph testResult3 = TestResultGraphBuilder
                .builder()
                .testResultId(3L)
                .stageFk(3L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(200))
                .testResultCreated(t0)
                .startTime(t0)
                .endTime(t0.plusSeconds(200))
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        StageGraph stage1 = StageGraphBuilder
                .builder()
                .stageId(1L)
                .stageName("test")
                .runFk(1L)
                .testResults(List.of(testResult1))
                .build();

        StageGraph stage2 = StageGraphBuilder
                .builder()
                .stageId(2L)
                .stageName("test")
                .runFk(2L)
                .testResults(List.of(testResult2))
                .build();

        StageGraph stage3 = StageGraphBuilder
                .builder()
                .stageId(3L)
                .stageName("test")
                .runFk(3L)
                .testResults(List.of(testResult3))
                .build();

        RunGraph run1 = RunGraphBuilder
                .builder()
                .runId(1L)
                .jobFk(1L)
                .jobRunCount(1)
                .runDate(t0)
                .isSuccess(true)
                .sha("abc123")
                .stages(List.of(stage1))
                .build();

        RunGraph run2 = RunGraphBuilder
                .builder()
                .runId(2L)
                .jobFk(1L)
                .jobRunCount(2)
                .runDate(t0.plusSeconds(300))
                .isSuccess(true)
                .sha("def456")
                .stages(List.of(stage2))
                .build();

        RunGraph run3 = RunGraphBuilder
                .builder()
                .runId(3L)
                .jobFk(1L)
                .jobRunCount(3)
                .runDate(t0.plusSeconds(600))
                .isSuccess(true)
                .sha("ghi789")
                .stages(List.of(stage3))
                .build();

        CompanyGraph companyGraph = createCompanyGraph(List.of(run1, run2, run3));

        JobDashboardRequest request = JobDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .build();

        List<JobDashboardMetrics> metrics = JobDashboardMetrics.fromCompanyGraphs(List.of(companyGraph), request);

        assertEquals(1, metrics.size());
        JobDashboardMetrics metric = metrics.get(0);
        assertNotNull(metric.getAvgRunDuration());
        assertEquals(new BigDecimal("150.00"), metric.getAvgRunDuration());
    }

    @Test
    void testAvgRunDuration_allNullTiming() {
        // Create CompanyGraph with 2 runs, both have NULL timing
        // Expected avgRunDuration: null

        Instant t0 = Instant.parse("2024-01-01T00:00:00Z");

        TestResultGraph testResult1 = TestResultGraphBuilder
                .builder()
                .testResultId(1L)
                .stageFk(1L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(100))
                .testResultCreated(t0)
                .startTime(null)
                .endTime(null)
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        TestResultGraph testResult2 = TestResultGraphBuilder
                .builder()
                .testResultId(2L)
                .stageFk(2L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(100))
                .testResultCreated(t0)
                .startTime(null)
                .endTime(null)
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        StageGraph stage1 = StageGraphBuilder
                .builder()
                .stageId(1L)
                .stageName("test")
                .runFk(1L)
                .testResults(List.of(testResult1))
                .build();

        StageGraph stage2 = StageGraphBuilder
                .builder()
                .stageId(2L)
                .stageName("test")
                .runFk(2L)
                .testResults(List.of(testResult2))
                .build();

        RunGraph run1 = RunGraphBuilder
                .builder()
                .runId(1L)
                .jobFk(1L)
                .jobRunCount(1)
                .runDate(t0)
                .isSuccess(true)
                .sha("abc123")
                .stages(List.of(stage1))
                .build();

        RunGraph run2 = RunGraphBuilder
                .builder()
                .runId(2L)
                .jobFk(1L)
                .jobRunCount(2)
                .runDate(t0.plusSeconds(300))
                .isSuccess(true)
                .sha("def456")
                .stages(List.of(stage2))
                .build();

        CompanyGraph companyGraph = createCompanyGraph(List.of(run1, run2));

        JobDashboardRequest request = JobDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .build();

        List<JobDashboardMetrics> metrics = JobDashboardMetrics.fromCompanyGraphs(List.of(companyGraph), request);

        assertEquals(1, metrics.size());
        JobDashboardMetrics metric = metrics.get(0);
        assertNull(metric.getAvgRunDuration());
    }

    @Test
    void testAvgRunDuration_multiStageOverlapping() {
        // Create CompanyGraph with 1 run containing 2 overlapping stages
        // Stage 1 (build): start=T0, end=T0+60s
        // Stage 2 (test):  start=T0+30s, end=T0+90s (starts while build still running)
        // Wall clock time = max(end) - min(start) = T0+90s - T0 = 90s
        // Expected avgRunDuration: 90.00 seconds

        Instant t0 = Instant.parse("2024-01-01T00:00:00Z");

        TestResultGraph testResult1 = TestResultGraphBuilder
                .builder()
                .testResultId(1L)
                .stageFk(1L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(60))
                .testResultCreated(t0)
                .startTime(t0)
                .endTime(t0.plusSeconds(60))
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        TestResultGraph testResult2 = TestResultGraphBuilder
                .builder()
                .testResultId(2L)
                .stageFk(2L)
                .tests(1)
                .skipped(0)
                .error(0)
                .failure(0)
                .time(BigDecimal.valueOf(60))
                .testResultCreated(t0)
                .startTime(t0.plusSeconds(30))
                .endTime(t0.plusSeconds(90))
                .isSuccess(true)
                .hasSkip(false)
                .testSuites(Collections.emptyList())
                .build();

        StageGraph stage1 = StageGraphBuilder
                .builder()
                .stageId(1L)
                .stageName("build")
                .runFk(1L)
                .testResults(List.of(testResult1))
                .build();

        StageGraph stage2 = StageGraphBuilder
                .builder()
                .stageId(2L)
                .stageName("test")
                .runFk(1L)
                .testResults(List.of(testResult2))
                .build();

        RunGraph run1 = RunGraphBuilder
                .builder()
                .runId(1L)
                .jobFk(1L)
                .jobRunCount(1)
                .runDate(t0)
                .isSuccess(true)
                .sha("abc123")
                .stages(List.of(stage1, stage2))
                .build();

        CompanyGraph companyGraph = createCompanyGraph(List.of(run1));

        JobDashboardRequest request = JobDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .build();

        List<JobDashboardMetrics> metrics = JobDashboardMetrics.fromCompanyGraphs(List.of(companyGraph), request);

        assertEquals(1, metrics.size());
        JobDashboardMetrics metric = metrics.get(0);
        assertNotNull(metric.getAvgRunDuration());
        assertEquals(new BigDecimal("90.00"), metric.getAvgRunDuration());
    }

    // Helper method to create CompanyGraph with given runs
    private CompanyGraph createCompanyGraph(List<RunGraph> runs) {
        Instant now = Instant.now();

        JobGraph job = JobGraphBuilder
                .builder()
                .jobId(1L)
                .branchFk(1)
                .lastRun(now)
                .jobInfo(new TreeMap<>(Collections.singletonMap("foo", "bar")))
                .jobInfoStr("{\"foo\":\"bar\"}")
                .runs(runs)
                .build();

        BranchGraph branch = BranchGraphBuilder
                .builder()
                .branchId(1)
                .branchName("main")
                .repoFk(1)
                .lastRun(now)
                .jobs(List.of(job))
                .build();

        RepoGraph repo = RepoGraphBuilder
                .builder()
                .repoId(1)
                .repoName("repo1")
                .orgFk(1)
                .branches(List.of(branch))
                .build();

        OrgGraph org = OrgGraphBuilder
                .builder()
                .orgId(1)
                .orgName("org1")
                .companyFk(1)
                .repos(List.of(repo))
                .build();

        return CompanyGraphBuilder
                .builder()
                .companyId(1)
                .companyName("company1")
                .orgs(List.of(org))
                .build();
    }
}
