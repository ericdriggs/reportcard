package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.failures.DailyTestAggregation;
import io.github.ericdriggs.reportcard.model.failures.FailuresDashboard;
import io.github.ericdriggs.reportcard.model.failures.FailuresDashboardRequest;
import io.github.ericdriggs.reportcard.model.failures.TestCaseFailureSummary;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GraphServiceFailuresTest extends AbstractGraphServiceTest {

    private final TestResultPersistService testResultPersistService;

    @Autowired
    public GraphServiceFailuresTest(GraphService graphService, TestResultPersistService testResultPersistService) {
        super(graphService);
        this.testResultPersistService = testResultPersistService;
    }

    @BeforeAll
    void insertFailuresTestData() {
        TreeMap<String, String> jobInfo = new TreeMap<>();
        jobInfo.put("pipeline", "bat_failures_test");

        for (int i = 1; i <= 5; i++) {
            StageDetails request = StageDetails.builder()
                    .company("company1")
                    .org("org1")
                    .repo("failures_test_repo")
                    .branch("main")
                    .sha("sha" + i)
                    .jobInfo(jobInfo)
                    .runReference(UUID.randomUUID())
                    .stage("bat")
                    .build();

            TestResultModel testResult = new TestResultModel();
            TestSuiteModel suite = new TestSuiteModel();
            suite.setName("BatSuite");
            suite.setPackageName("com.test.bat");

            // alwaysFailing: fails every run
            TestCaseModel alwaysFailing = TestCaseModel.builder().build();
            alwaysFailing.setName("alwaysFailing");
            alwaysFailing.setTestStatus(TestStatus.FAILURE);
            alwaysFailing.setTestStatusFk(TestStatus.FAILURE.getStatusId());
            TestCaseFaultModel fault = TestCaseFaultModel.builder().build();
            fault.setFaultContextFk(FaultContext.FAILURE.getFaultContextId());
            fault.setMessage("always fails");
            fault.setType("AssertionError");
            fault.setValue("expected true but got false");
            alwaysFailing.addTestCaseFault(fault);

            // alwaysPassing: passes every run
            TestCaseModel alwaysPassing = TestCaseModel.builder().build();
            alwaysPassing.setName("alwaysPassing");
            alwaysPassing.setTestStatus(TestStatus.SUCCESS);
            alwaysPassing.setTestStatusFk(TestStatus.SUCCESS.getStatusId());

            suite.setTestCases(List.of(alwaysFailing, alwaysPassing));
            suite.setTests(2);
            suite.setFailure(1);
            suite.setError(0);
            suite.setSkipped(0);
            testResult.getTestSuites().add(suite);
            testResult.updateTotalsFromTestSuites();

            testResultPersistService.insertTestResult(request, testResult);
        }
    }

    @Test
    void getFailingRunIds_returnsRunsForMatchingJobInfo() {
        FailuresDashboardRequest request = FailuresDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .jobInfoKey("pipeline")
                .jobInfoValues(List.of("bat_failures_test"))
                .days(365)
                .failureThreshold(50)
                .build();

        Long[] runIds = graphService.getFailingRunIds(request);
        assertNotNull(runIds);
        assertTrue(runIds.length >= 5, "Should find at least 5 runs, got: " + runIds.length);
    }

    @Test
    void getFailingRunIds_returnsEmptyForNonMatchingJobInfo() {
        FailuresDashboardRequest request = FailuresDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .jobInfoKey("pipeline")
                .jobInfoValues(List.of("nonexistent_pipeline"))
                .days(365)
                .failureThreshold(50)
                .build();

        Long[] runIds = graphService.getFailingRunIds(request);
        assertNotNull(runIds);
        assertEquals(0, runIds.length, "Should find no runs for nonexistent pipeline");
    }

    @Test
    void getFailingTestSummaries_returnsTestsBelowThreshold() {
        FailuresDashboardRequest request = FailuresDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .jobInfoKey("pipeline")
                .jobInfoValues(List.of("bat_failures_test"))
                .days(365)
                .failureThreshold(50)
                .build();

        Long[] runIds = graphService.getFailingRunIds(request);
        assertTrue(runIds.length > 0, "Precondition: must have run IDs");

        List<TestCaseFailureSummary> summaries =
                graphService.getFailingTestSummaries(runIds, request.getFailureThreshold());
        assertNotNull(summaries);
        assertFalse(summaries.isEmpty(), "Should find alwaysFailing test");
        for (var summary : summaries) {
            assertTrue(summary.getSuccessPercent().doubleValue() < request.getFailureThreshold(),
                    "All returned tests should be below threshold: " + summary);
        }
        // alwaysFailing has 0% success — should definitely be in the list
        assertTrue(summaries.stream().anyMatch(s -> "alwaysFailing".equals(s.getCaseName())),
                "Should find 'alwaysFailing' test case");
        // alwaysPassing should NOT be in the list (100% success)
        assertTrue(summaries.stream().noneMatch(s -> "alwaysPassing".equals(s.getCaseName())),
                "Should NOT find 'alwaysPassing' test case");
    }

    @Test
    void getDailyAggregations_returnsDailyBreakdown() {
        FailuresDashboardRequest request = FailuresDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .jobInfoKey("pipeline")
                .jobInfoValues(List.of("bat_failures_test"))
                .days(365)
                .failureThreshold(50)
                .build();

        Long[] runIds = graphService.getFailingRunIds(request);
        assertTrue(runIds.length > 0, "Precondition: must have run IDs");

        List<DailyTestAggregation> daily = graphService.getDailyAggregations(runIds);
        assertNotNull(daily);
        assertFalse(daily.isEmpty(), "Should have at least one day of data");
        for (var day : daily) {
            assertNotNull(day.getDate());
            assertTrue(day.getTotalTests() > 0);
            assertEquals(day.getPassCount() + day.getFailCount(), day.getTotalTests());
            assertNotNull(day.getPassPercent());
        }
    }

    @Test
    void getFailuresDashboard_returnsFullResponse() {
        FailuresDashboardRequest request = FailuresDashboardRequest.builder()
                .company("company1")
                .org("org1")
                .jobInfoKey("pipeline")
                .jobInfoValues(List.of("bat_failures_test"))
                .days(365)
                .failureThreshold(80)
                .build();

        FailuresDashboard dashboard = graphService.getFailuresDashboard(request);
        assertNotNull(dashboard);
        assertNotNull(dashboard.getRequest());
        assertNotNull(dashboard.getFailingTests());
        assertNotNull(dashboard.getDailyAggregations());
        assertNotNull(dashboard.getGenerated());
        assertEquals("company1", dashboard.getRequest().getCompany());
    }
}
