package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class GraphJsonControllerJobInfoTest extends AbstractBrowseServiceTest {

    private final GraphJsonController controller;
    private final String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

    @Autowired
    public GraphJsonControllerJobInfoTest(BrowseService browseService, GraphJsonController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getJobStageTestTrendFromJobInfoSuccessTest() {
        ResponseEntity<JobStageTestTrend> response =
            controller.getJobStageTestTrendFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTestCaseTrends());
        assertFalse(response.getBody().getTestCaseTrends().isEmpty());
    }

    @Test
    void jobInfoPreservesSpecialCharsTest() {
        String realisticJobInfo = "application=foo-app,host=build.corp.jenkins.com,pipeline=re-lease_candidate";
        // Parsing succeeds; 404 means lookup ran but no match — not a parse failure
        org.springframework.web.server.ResponseStatusException ex = assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.getJobStageTestTrendFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                realisticJobInfo, TestData.stage, null, null));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().contains("foo-app"));
        assertTrue(ex.getReason().contains("build.corp.jenkins.com"));
        assertTrue(ex.getReason().contains("re-lease_candidate"));
    }

    @Test
    void getJobStageTestTrendFromJobInfoMatchesJobIdTest() {
        ResponseEntity<JobStageTestTrend> jobInfoResponse =
            controller.getJobStageTestTrendFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage, null, null);

        ResponseEntity<JobStageTestTrend> jobIdResponse =
            controller.getJobStageTestTrend(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                TestData.jobId, TestData.stage, null, null);

        assertEquals(jobIdResponse.getStatusCode(), jobInfoResponse.getStatusCode());
        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(
            jobIdResponse.getBody().getCompanyOrgRepoBranchJobStageName(),
            jobInfoResponse.getBody().getCompanyOrgRepoBranchJobStageName());
        assertEquals(
            jobIdResponse.getBody().getTestCaseTrends().size(),
            jobInfoResponse.getBody().getTestCaseTrends().size());
    }
}
