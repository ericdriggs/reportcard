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
}
