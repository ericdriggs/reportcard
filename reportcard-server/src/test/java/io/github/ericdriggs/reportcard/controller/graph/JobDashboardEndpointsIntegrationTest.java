package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JobDashboardEndpointsIntegrationTest {

    @Autowired
    private GraphUIController graphUIController;

    @Autowired
    private GraphJsonController graphJsonController;

    @Test
    public void testJobDashboardUIEndpoint() {
        ResponseEntity<String> response = graphUIController.getJobDashboard("hulu", "SubLife", null, 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testJobDashboardUIEndpointWithJobInfo() {
        ResponseEntity<String> response = graphUIController.getJobDashboard("hulu", "SubLife", 
                List.of("pipeline:dev-cp3"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testJobDashboardUIEndpointWithDays() {
        ResponseEntity<String> response = graphUIController.getJobDashboard("hulu", "SubLife", null, 30);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testJobDashboardUIEndpointWithWildcard() {
        ResponseEntity<String> response = graphUIController.getJobDashboard("hulu", "SubLife", 
                List.of("pipeline:dev*"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testJobDashboardJsonEndpoint() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife", null, 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testJobDashboardJsonEndpointWithJobInfo() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife", 
                List.of("pipeline:dev-cp3"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
