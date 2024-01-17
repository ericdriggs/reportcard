package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatus;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class JunitControllerTest {

    @Autowired
    JunitController junitController;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    BrowseService browseService;

    @Autowired
    S3Service s3Service;

    private final static ObjectMapper mappper = new ObjectMapper();

    @Test
    void postJunitTest() throws IOException {

        List<TestStatus> testStatuses = browseService.getAllTestStatuses();
        assertNotNull(testStatuses);
        log.info("testStatuses: {}", testStatuses);

        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFiles(TestResultPersistServiceTest.xmlPaths, resourceReader);
        String stageDetailsJson =
                """
                        {
                          "org": "org1",
                          "repo": "repo1",
                          "branch": "branch1",
                          "sha": "sha1",
                          "jobInfo": {
                            "host": "www.foo.com"
                          },
                          "runReference": "abc123",
                          "stage": "api"
                        }
                        """;

        StageDetails stageDetails = mappper.readValue(stageDetailsJson, StageDetails.class);
        ResponseEntity<Map<StagePath, TestResult>> response = junitController.postJunitXml(files, stageDetails);
        Map<StagePath, TestResult> result = response.getBody();
        assertNotNull(result);
        assertEquals(1, result.size());
        for (Map.Entry<StagePath, TestResult> entry : result.entrySet()) {
            final StagePath stagePath = entry.getKey();
            assertEquals("org1", stagePath.getOrg().getOrgName());
            assertEquals("repo1", stagePath.getRepo().getRepoName());
            assertEquals("branch1", stagePath.getBranch().getBranchName());
            assertEquals("{\"host\": \"www.foo.com\"}", stagePath.getJob().getJobInfo());
            assertEquals("abc123", stagePath.getRun().getRunReference());
            assertEquals("api", stagePath.getStage().getStageName());
            final TestResult testResult = entry.getValue();
            assertNotNull(testResult.getTests());
        }
    }
}
