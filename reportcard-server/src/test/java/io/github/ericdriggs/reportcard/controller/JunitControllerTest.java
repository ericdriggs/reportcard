package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestStatusPojo;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
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
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

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

        List<TestStatusPojo> testStatuses = browseService.getAllTestStatuses();
        assertNotNull(testStatuses);
        log.info("testStatuses: {}", testStatuses);


        StageDetails stageDetails =
                StageDetails.builder()
                        .company("company1")
                        .org("org1")
                        .repo("repo1")
                        .branch("branch1")
                        .sha("sha1")
                        .jobInfo(new TreeMap<>(Collections.singletonMap("host", "www.foo.com")))
                        .runReference("abc123")
                        .stage("api")
                        .build();

        System.out.println(mappper.writerWithDefaultPrettyPrinter().writeValueAsString(stageDetails));

        MultipartFile file = TestResultPersistServiceTest.getMockJunitMultipartFile(resourceReader);
        String stageDetailsJson =
                """
                {
                  "company" : "company1",
                  "org" : "org1",
                  "repo" : "repo1",
                  "branch" : "branch1",
                  "sha" : "sha1",
                  "jobInfo" : {
                    "host" : "www.foo.com"
                  },
                  "runReference" : "abc123",
                  "stage" : "api",
                  "externalLinks" : null
                }""";


        StageDetails stageDetailsParsed = mappper.readValue(stageDetailsJson, StageDetails.class);
        ResponseEntity<StagePathTestResult> response = junitController.postJunitXmlStageDetails(stageDetails, file);
        StagePathTestResult result = response.getBody();
        assertNotNull(result);

        final StagePath stagePath = result.getStagePath();
        assertEquals("company1", stagePath.getCompany().getCompanyName());
        assertEquals("org1", stagePath.getOrg().getOrgName());
        assertEquals("repo1", stagePath.getRepo().getRepoName());
        assertEquals("branch1", stagePath.getBranch().getBranchName());
        assertEquals("{\"host\": \"www.foo.com\"}", stagePath.getJob().getJobInfo());
        assertEquals("abc123", stagePath.getRun().getRunReference());
        assertEquals("api", stagePath.getStage().getStageName());
        final TestResultModel testResult = result.getTestResult();
        assertNotNull(testResult.getTests());

    }
}
