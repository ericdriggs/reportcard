package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.controller.browse.response.TestTrendResponse;
import io.github.ericdriggs.reportcard.controller.browse.response.TrendDetail;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseJsonControllerTrendTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerTrendTest(BrowseService browseService, BrowseJsonController browseJsonController) {
        super(browseService);
        this.controller = browseJsonController;
    }

    @Test
    void getJobStageTestTrendJsonSuccessTest() {
        ResponseEntity<TestTrendResponse> response = controller.getJobStageTestTrendJson(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                TestData.jobId, TestData.stage, null, null, 30, TrendDetail.full, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTrendResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getRuns());
        assertNotNull(body.getTestCases());
        assertNotNull(body.getSummary());

        // Test data has 1 run with test cases
        assertFalse(body.getRuns().isEmpty(), "Expected at least one run");
        assertFalse(body.getTestCases().isEmpty(), "Expected at least one test case");
        assertTrue(body.getSummary().getTotalTests() > 0, "Expected totalTests > 0");

        // Verify sparse runStates structure
        TestTrendResponse.TestCaseTrendEntry firstTestCase = body.getTestCases().get(0);
        assertNotNull(firstTestCase.getRunStates(), "runStates should not be null");
        assertNotNull(firstTestCase.getTestPackageName());
        assertNotNull(firstTestCase.getTestSuiteName());
        assertNotNull(firstTestCase.getTestCaseName());
    }

    @Test
    void getJobStageTestTrendFromJobInfoJsonSuccessTest() {
        String jobInfoStr = "application=fooapp,host=foocorp.jenkins.com,pipeline=foopipeline";
        ResponseEntity<TestTrendResponse> response = controller.getJobStageTestTrendFromJobInfoJson(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                jobInfoStr, TestData.stage, null, null, 30, TrendDetail.full, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTrendResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getRuns().isEmpty(), "Expected at least one run");
        assertFalse(body.getTestCases().isEmpty(), "Expected at least one test case");
        assertTrue(body.getSummary().getTotalTests() > 0, "Expected totalTests > 0");
    }

    @Test
    void getJobStageTestTrendJsonEmptyResultTest() {
        // Non-existent job should return empty response
        ResponseEntity<TestTrendResponse> response = controller.getJobStageTestTrendJson(
                TestData.company, TestData.org, TestData.repo, TestData.branch,
                999999L, TestData.stage, null, null, 30, TrendDetail.full, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTrendResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getRuns().isEmpty());
        assertTrue(body.getTestCases().isEmpty());
        assertEquals(0, body.getSummary().getTotalTests());
    }
}
