package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.model.failures.FailuresDashboard;
import io.github.ericdriggs.reportcard.persist.AbstractGraphServiceTest;
import io.github.ericdriggs.reportcard.persist.GraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseJsonControllerFailuresTest extends AbstractGraphServiceTest {

    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerFailuresTest(GraphService graphService, BrowseJsonController controller) {
        super(graphService);
        this.controller = controller;
    }

    @Test
    void getFailuresDashboardForCompany_returnsOk() {
        ResponseEntity<FailuresDashboard> response = controller.getFailuresDashboardForCompany(
                "company1", 365, 50, null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getRequest());
        assertEquals("company1", response.getBody().getRequest().getCompany());
        assertNull(response.getBody().getRequest().getOrg());
        assertNotNull(response.getBody().getFailingTests());
        assertNotNull(response.getBody().getGenerated());
    }

    @Test
    void getFailuresDashboardForCompany_malformedJobInfo_returns400() {
        org.springframework.web.server.ResponseStatusException ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> controller.getFailuresDashboardForCompany(
                        "company1", 365, 50, null, List.of("nocolon"), null)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getReason().contains("jobInfo must be in format"));
    }

    @Test
    void getFailuresDashboardForOrg_returnsOk() {
        ResponseEntity<FailuresDashboard> response = controller.getFailuresDashboardForOrg(
                "company1", "org1", 365, 50, null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getRequest());
        assertEquals("company1", response.getBody().getRequest().getCompany());
        assertEquals("org1", response.getBody().getRequest().getOrg());
        assertNotNull(response.getBody().getFailingTests());
        assertNotNull(response.getBody().getDailyAggregations());
        assertNotNull(response.getBody().getGenerated());
    }
}
