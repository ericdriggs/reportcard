package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseUIControllerJobInfoTest extends AbstractBrowseServiceTest {

    private final BrowseUIController controller;
    private final String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

    @Autowired
    public BrowseUIControllerJobInfoTest(BrowseService browseService, BrowseUIController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getLatestRunStagesFromJobInfoSuccessTest() {
        ResponseEntity<String> response =
            controller.getLatestRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getLatestRunStageTestResultFromJobInfoSuccessTest() {
        ResponseEntity<String> response =
            controller.getLatestRunStageTestResultFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getRunStagesFromJobInfoSuccessTest() {
        ResponseEntity<String> response =
            controller.getRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getTestResultNaturalKeysSuccessTest() {
        ResponseEntity<String> response =
            controller.getTestResultNaturalKeys(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, 1, TestData.stage);

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
            () -> controller.getLatestRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, realisticJobInfo));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().contains("foo-app"));
        assertTrue(ex.getReason().contains("build.corp.jenkins.com"));
        assertTrue(ex.getReason().contains("dev_cp-3"));
    }

    @Test
    void getLatestRunStagesFromJobInfoMatchesJobIdTest() {
        ResponseEntity<String> jobInfoResponse =
            controller.getLatestRunStagesFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch, jobInfoString);

        ResponseEntity<String> jobIdResponse =
            controller.getLatestRunStages(
                TestData.company, TestData.org, TestData.repo, TestData.branch, TestData.jobId);

        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(jobIdResponse.getBody(), jobInfoResponse.getBody());
    }

    @Test
    void getLatestRunStageTestResultFromJobInfoMatchesJobIdTest() {
        ResponseEntity<String> jobInfoResponse =
            controller.getLatestRunStageTestResultFromJobInfo(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoString, TestData.stage);

        ResponseEntity<String> jobIdResponse =
            controller.getLatestRunStageTestResult(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                TestData.jobId, TestData.stage);

        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(jobIdResponse.getBody());
        assertEquals(jobIdResponse.getBody(), jobInfoResponse.getBody());
    }
}
