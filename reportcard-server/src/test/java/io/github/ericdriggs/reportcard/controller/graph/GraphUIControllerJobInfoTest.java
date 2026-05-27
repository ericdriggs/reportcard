package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class GraphUIControllerJobInfoTest extends AbstractBrowseServiceTest {

    private final GraphUIController controller;
    private final String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

    @Autowired
    public GraphUIControllerJobInfoTest(BrowseService browseService, GraphUIController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getJobStageTestTrendFromJobInfoSuccessTest() {
        ResponseEntity<String> response =
            controller.getJobStageTestTrendFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage, null, null, 30);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void jobInfoPreservesSpecialCharsTest() {
        String realisticJobInfo = "application=foo-app,host=build.corp.jenkins.com,pipeline=dev_cp-3";
        org.springframework.web.server.ResponseStatusException ex = assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.getJobStageTestTrendFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                realisticJobInfo, TestData.stage, null, null, 30));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().contains("foo-app"));
        assertTrue(ex.getReason().contains("build.corp.jenkins.com"));
        assertTrue(ex.getReason().contains("dev_cp-3"));
    }

    @Test
    void getJobStageTestTrendFromJobInfoMatchesJobIdTest() {
        ResponseEntity<String> jobInfoResponse =
            controller.getJobStageTestTrendFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage, null, null, 30);

        ResponseEntity<String> jobIdResponse =
            controller.getJobStageTestTrend(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                TestData.jobId, TestData.stage, null, null, 30);

        assertEquals(jobIdResponse.getStatusCode(), jobInfoResponse.getStatusCode());
        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(jobIdResponse.getBody(), jobInfoResponse.getBody());
    }
}
