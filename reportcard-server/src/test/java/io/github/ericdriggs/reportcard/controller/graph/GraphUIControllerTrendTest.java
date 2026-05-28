package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class GraphUIControllerTrendTest extends AbstractBrowseServiceTest {

    private final GraphUIController controller;

    @Autowired
    public GraphUIControllerTrendTest(BrowseService browseService, GraphUIController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getJobStageTestTrendReturnsHtmlTest() {
        ResponseEntity<String> response = controller.getJobStageTestTrend(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                TestData.jobId, TestData.stage, null, null, 30);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}
