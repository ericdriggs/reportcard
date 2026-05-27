package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class GraphUIControllerRepoDashboardJobInfoTest extends AbstractBrowseServiceTest {

    private final GraphUIController controller;
    private final String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

    @Autowired
    public GraphUIControllerRepoDashboardJobInfoTest(BrowseService browseService, GraphUIController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getRepoDashboardWithJobInfoSuccessTest() {
        ResponseEntity<String> response = controller.getRepoDashboardWithJobInfo(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getRepoDashboardWithJobInfoMatchesUnfilteredTest() {
        ResponseEntity<String> jobInfoResponse = controller.getRepoDashboardWithJobInfo(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        ResponseEntity<String> unfilteredResponse = controller.getRepoDashboard(
                TestData.repo, Collections.emptyList(), true, null);

        assertEquals(unfilteredResponse.getStatusCode(), jobInfoResponse.getStatusCode());
        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(unfilteredResponse.getBody());
        assertEquals(unfilteredResponse.getBody(), jobInfoResponse.getBody());
    }
}
