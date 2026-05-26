package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.controller.browse.response.FlatDashboardEntry;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseJsonControllerFlatDashboardTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerFlatDashboardTest(BrowseService browseService, BrowseJsonController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getRepoDashboardFlatJsonSuccessTest() {
        ResponseEntity<List<FlatDashboardEntry>> response =
            controller.getRepoDashboardFlatJson(
                TestData.repo, Collections.emptyList(), true, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        FlatDashboardEntry first = response.getBody().get(0);
        assertNotNull(first.getCompany());
        assertNotNull(first.getOrg());
        assertNotNull(first.getRepo());
        assertNotNull(first.getBranch());
        assertNotNull(first.getJobId());
        assertNotNull(first.getJobInfo());
        assertFalse(first.getJobInfo().isEmpty());
        assertNotNull(first.getRunId());
        assertNotNull(first.getJobRunCount());
        assertNotNull(first.getSha());
        assertNotNull(first.getRunDate());
        assertNotNull(first.getIsSuccess());
        assertNotNull(first.getUrl());
        assertTrue(first.getUrl().contains("/company/"));
        assertTrue(first.getUrl().contains("/run/"));
        assertNotNull(first.getStageName());
        assertNotNull(first.getStorageUrls());
    }

    @Test
    void getRepoDashboardFlatJsonHasCorrectContextTest() {
        ResponseEntity<List<FlatDashboardEntry>> response =
            controller.getRepoDashboardFlatJson(
                TestData.repo, Collections.emptyList(), true, null);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        for (FlatDashboardEntry entry : response.getBody()) {
            assertEquals(TestData.repo, entry.getRepo());
            assertNotNull(entry.getCompany());
            assertNotNull(entry.getOrg());
            assertNotNull(entry.getBranch());
            assertNotNull(entry.getJobId());
            assertNotNull(entry.getJobInfo());
            assertNotNull(entry.getRunId());
            assertNotNull(entry.getJobRunCount());
            assertNotNull(entry.getSha());
            assertNotNull(entry.getRunDate());
            assertNotNull(entry.getIsSuccess());
            assertNotNull(entry.getUrl());
            assertNotNull(entry.getStageName());
            assertNotNull(entry.getStorageUrls());
        }
    }

    @Test
    void getRepoDashboardFlatWithJobInfoJsonSuccessTest() {
        String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);
        ResponseEntity<List<FlatDashboardEntry>> response =
            controller.getRepoDashboardFlatWithJobInfoJson(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        FlatDashboardEntry first = response.getBody().get(0);
        assertNotNull(first.getCompany());
        assertNotNull(first.getOrg());
        assertNotNull(first.getRepo());
        assertNotNull(first.getBranch());
        assertNotNull(first.getJobId());
        assertNotNull(first.getJobInfo());
        assertNotNull(first.getRunId());
        assertNotNull(first.getJobRunCount());
        assertNotNull(first.getSha());
        assertNotNull(first.getRunDate());
        assertNotNull(first.getIsSuccess());
        assertNotNull(first.getUrl());
        assertNotNull(first.getStageName());
        assertNotNull(first.getStorageUrls());
    }

    @Test
    void getRepoDashboardFlatWithJobInfoMatchesUnfilteredTest() {
        String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

        ResponseEntity<List<FlatDashboardEntry>> filteredResponse =
            controller.getRepoDashboardFlatWithJobInfoJson(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        ResponseEntity<List<FlatDashboardEntry>> unfilteredResponse =
            controller.getRepoDashboardFlatJson(
                TestData.repo, Collections.emptyList(), true, null);

        assertNotNull(filteredResponse.getBody());
        assertNotNull(unfilteredResponse.getBody());
        assertEquals(unfilteredResponse.getBody().size(), filteredResponse.getBody().size());
    }

    @Test
    void getRepoDashboardFlatWithNonMatchingJobInfoReturnsEmptyTest() {
        ResponseEntity<List<FlatDashboardEntry>> response =
            controller.getRepoDashboardFlatWithJobInfoJson(
                TestData.repo, "application=NONEXISTENT",
                Collections.emptyList(), true, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
