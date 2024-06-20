package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.trend.CompanyOrgRepoBranchJobStageName;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class GraphServiceTest extends AbstractGraphServiceTest {
    @Autowired
    public GraphServiceTest(GraphService graphService) {
        super(graphService);
    }

    List<CompanyGraph> getTestCompanyGraphs() {
        final Instant start = Instant.parse("2000-01-01T01:00:00.00Z");
        final Instant end = Instant.parse("4000-01-01T01:00:00.00Z");
        final int maxRuns = 30;
        return graphService.getJobTrendCompanyGraphs(TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId, TestData.stage, start, end, maxRuns);
    }

    @Test
    void getJobTrendTest() {
        int maxRuns = 30;
        List<CompanyGraph> companyGraphs = getTestCompanyGraphs();
        JobStageTestTrend jobTestTrend = JobStageTestTrend.fromCompanyGraphs(companyGraphs, maxRuns);
        assertNotNull(jobTestTrend);
        final CompanyOrgRepoBranchJobStageName c = jobTestTrend.getCompanyOrgRepoBranchJobStageName();
        assertNotNull(c);
        assertNotNull(c.getCompanyPojo());
        assertNotNull(c.getCompanyPojo().getCompanyId());
        assertNotNull(c.getOrgPojo());
        assertNotNull(c.getOrgPojo().getOrgId());
        assertNotNull(c.getOrgPojo().getOrgName());
        assertNotNull(c.getRepoPojo());
        assertNotNull(c.getRepoPojo().getRepoId());
        assertNotNull(c.getRepoPojo().getRepoName());
        assertNotNull(c.getBranchPojo());
        assertNotNull(c.getBranchPojo().getBranchId());
        assertNotNull(c.getBranchPojo().getBranchName());
        assertNotNull(c.getRepoPojo());
        assertNotNull(c.getRepoPojo().getRepoId());
        assertNotNull(c.getRepoPojo().getRepoName());
        assertNotNull(c.getJobPojo());
        assertNotNull(c.getJobPojo().getJobId());
        assertNotNull(c.getJobPojo().getJobInfo());
        assertNotNull(jobTestTrend.getTestCaseTrends());
        assertNotNull(c.getStageName());
    }

    @Test
    void getCompanyGraphTest() throws JsonProcessingException {
        List<CompanyGraph> companyGraphs = getTestCompanyGraphs();
        assertFalse(CollectionUtils.isEmpty(companyGraphs));
        CompanyGraph companyGraph = companyGraphs.get(0);
        log.info("companyGraph: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(companyGraphs));

        {
            assertEquals(TestData.company, companyGraph.companyName());
            assertEquals(1, companyGraph.orgs().size());
        }

        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs()));
            final OrgGraph orgGraph = companyGraph.orgs().get(0);
            assertEquals(TestData.org, orgGraph.orgName());
            assertEquals(1, orgGraph.repos().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos()));
            final RepoGraph graph = companyGraph.orgs().get(0).repos().get(0);
            assertEquals(TestData.repo, graph.repoName());
            assertEquals(1, graph.branches().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches()));
            final BranchGraph graph = companyGraph.orgs().get(0).repos().get(0).branches().get(0);
            assertEquals(TestData.branch, graph.branchName());
            assertEquals(1, graph.jobs().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs()));
            final JobGraph graph = companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0);
            assertEquals(TestData.jobId, graph.jobId());
            assertEquals(1, graph.runs().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs()));
            final RunGraph graph = companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0);
            assertEquals(TestData.runReference, graph.runReference());
            assertEquals(1, graph.stages().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages()));
            final StageGraph graph = companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0);
            assertEquals(TestData.stage, graph.stageName());
            assertEquals(1, graph.testResults().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0).testResults()));
            final TestResultGraph graph = companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0).testResults().get(0);
            assertEquals(TestData.testResultId, graph.testResultId());
            assertEquals(1, graph.testSuites().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0).testResults().get(0).testSuites()));
            final TestSuiteGraph graph = companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0).testResults().get(0).testSuites().get(0);
            assertEquals(TestData.testSuiteId, graph.testSuiteId());
            assertEquals(2, graph.testCases().size());
        }
        {
            assertFalse(CollectionUtils.isEmpty(companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0).testResults().get(0).testSuites().get(0).testCases()));
            final List<TestCaseGraph> testCases = companyGraph.orgs().get(0).repos().get(0).branches().get(0).jobs().get(0).runs().get(0).stages().get(0).testResults().get(0).testSuites().get(0).testCases();
            final List<Long> testCaseIds = testCases.stream().map(t -> t.testCaseId()).toList();
            assertEquals(List.of(1L, 2L), testCaseIds);
        }
    }

}
