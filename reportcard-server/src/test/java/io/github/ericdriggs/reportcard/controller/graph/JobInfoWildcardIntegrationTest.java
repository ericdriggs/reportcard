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
public class JobInfoWildcardIntegrationTest {

    @Autowired
    private GraphUIController graphUIController;

    @Autowired
    private GraphJsonController graphJsonController;

    @Test
    public void testPrefixWildcard() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife",
                List.of("pipeline:*cp3"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSuffixWildcard() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife",
                List.of("pipeline:dev*"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testContainsWildcard() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife",
                List.of("pipeline:*dev*"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testExactMatch() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife",
                List.of("pipeline:dev-cp3"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testMultipleFilters() {
        ResponseEntity<List<JobDashboardMetrics>> response = graphJsonController.getJobDashboardJson("hulu", "SubLife",
                List.of("pipeline:dev*", "application:*service"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUIEndpointWithWildcard() {
        ResponseEntity<String> response = graphUIController.getJobDashboard("hulu", "SubLife",
                List.of("pipeline:dev*"), 90);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
