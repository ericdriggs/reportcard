package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.controller.browse.response.*;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StageTestResultModel;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseJsonControllerJobInfoTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;
    private final BrowseService browseService;
    private final String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

    @Autowired
    public BrowseJsonControllerJobInfoTest(BrowseService browseService, BrowseJsonController controller) {
        super(browseService);
        this.browseService = browseService;
        this.controller = controller;
    }

    @Test
    void getJobRunsStagesFromJobInfoSuccessTest() {
        ResponseEntity<JobRunsStagesResponse> response =
            controller.getJobRunsStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JobRunsStagesResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(TestData.jobId, body.getJobId());
        assertNotNull(body.getRuns());
        assertFalse(body.getRuns().isEmpty());
    }

    @Test
    void jobInfoPreservesSpecialCharsTest() {
        String realisticJobInfo = "application=foo-app,host=build.corp.jenkins.com,pipeline=re-lease_candidate";
        org.springframework.web.server.ResponseStatusException ex = assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.getJobRunsStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, realisticJobInfo, null));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().contains("foo-app"));
        assertTrue(ex.getReason().contains("build.corp.jenkins.com"));
        assertTrue(ex.getReason().contains("re-lease_candidate"));
    }

    @Test
    void getJobRunsStagesFromJobInfoMatchesJobIdTest() {
        ResponseEntity<JobRunsStagesResponse> jobInfoResponse =
            controller.getJobRunsStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString, null);

        ResponseEntity<JobRunsStagesResponse> jobIdResponse =
            controller.getJobRunsStages(
                TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId, null);

        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(jobIdResponse.getBody().getJobId(), jobInfoResponse.getBody().getJobId());
        assertEquals(jobIdResponse.getBody().getRuns().size(), jobInfoResponse.getBody().getRuns().size());
    }

    @Test
    void getStagesByJobInfoAndRunCountSuccessTest() {
        ResponseEntity<RunStagesTestResultsResponse> response =
            controller.getStagesByJobInfoAndRunCount(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString, 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        RunStagesTestResultsResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getStages());
        assertFalse(body.getStages().isEmpty());
    }

    @Test
    void getStagesByJobInfoAndRunCountMatchesLatestTest() {
        ResponseEntity<RunStagesTestResultsResponse> runCountResponse =
            controller.getStagesByJobInfoAndRunCount(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString, 1);

        ResponseEntity<RunStagesTestResultsResponse> latestResponse =
            controller.getLatestRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString);

        assertNotNull(runCountResponse.getBody());
        assertNotNull(latestResponse.getBody());
        assertEquals(latestResponse.getBody().getRunId(), runCountResponse.getBody().getRunId());
        assertEquals(latestResponse.getBody().getStages().size(), runCountResponse.getBody().getStages().size());
        assertEquals(
            latestResponse.getBody().getStages().get(0).getStageId(),
            runCountResponse.getBody().getStages().get(0).getStageId());
    }

    @Test
    void getLatestRunStagesFromJobInfoSuccessTest() {
        ResponseEntity<RunStagesTestResultsResponse> response =
            controller.getLatestRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        RunStagesTestResultsResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getRunId());
        assertNotNull(body.getStages());
        assertFalse(body.getStages().isEmpty());
    }

    @Test
    void getLatestRunStagesFromJobInfoMatchesJobIdTest() {
        ResponseEntity<RunStagesTestResultsResponse> jobInfoResponse =
            controller.getLatestRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString);

        ResponseEntity<RunStagesTestResultsResponse> jobIdResponse =
            controller.getLatestRunStages(
                TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId);

        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(jobIdResponse.getBody().getRunId(), jobInfoResponse.getBody().getRunId());
    }

    @Test
    void getStageTestResultsByJobInfoAndRunCountSuccessTest() {
        ResponseEntity<StageTestResultModel> response =
            controller.getStageTestResultsByJobInfoAndRunCount(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, 1, TestData.stage);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        StageTestResultModel body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getStage());
        assertEquals(TestData.stage, body.getStage().getStageName());
        assertNotNull(body.getTestResult());
    }

    @Test
    void getStageTestResultsByJobInfoAndRunCountMatchesLatestTest() {
        ResponseEntity<StageTestResultModel> runCountResponse =
            controller.getStageTestResultsByJobInfoAndRunCount(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, 1, TestData.stage);

        ResponseEntity<StageTestResultModel> latestResponse =
            controller.getLatestRunStageTestResultsFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage);

        assertNotNull(runCountResponse.getBody());
        assertNotNull(latestResponse.getBody());
        assertEquals(latestResponse.getBody().getStage().getStageId(),
            runCountResponse.getBody().getStage().getStageId());
        assertEquals(latestResponse.getBody().getTestResult().getTestResultId(),
            runCountResponse.getBody().getTestResult().getTestResultId());
    }

    @Test
    void getLatestRunStageTestResultsFromJobInfoSuccessTest() {
        ResponseEntity<StageTestResultModel> response =
            controller.getLatestRunStageTestResultsFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        StageTestResultModel body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getStage());
        assertEquals(TestData.stage, body.getStage().getStageName());
    }

    @Test
    void getLatestRunStageTestResultsFromJobInfoMatchesJobIdTest() {
        ResponseEntity<StageTestResultModel> jobInfoResponse =
            controller.getLatestRunStageTestResultsFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage);

        ResponseEntity<StageTestResultModel> jobIdResponse =
            controller.getLatestRunStageTestResults(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                TestData.jobId, TestData.stage);

        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(jobIdResponse.getBody().getStage().getStageId(),
            jobInfoResponse.getBody().getStage().getStageId());
        assertEquals(jobIdResponse.getBody().getTestResult().getTestResultId(),
            jobInfoResponse.getBody().getTestResult().getTestResultId());
    }
}
