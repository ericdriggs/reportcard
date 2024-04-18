package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
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

    static StageDetails getStageDetails(String stageName) {
        return StageDetails.builder()
                           .company(TestData.company)
                           .org(TestData.org)
                           .repo(TestData.repo)
                           .branch(TestData.branch)
                           .sha(TestData.sha)
                           .jobInfo(TestData.jobInfo)
                           .runReference(TestData.runReference)
                           .stage(stageName)
                           .build();
    }

    @Test
    void postJunitTest() throws IOException {

        final String stage = "postJunitTest";
        final String xmlClassPath = "format-samples/sample-junit-small.xml";
        postJunitFixture(stage, xmlClassPath);
    }

    @Test
    void postJunitFailureTest() {
        final String stage = "postJunitFailureTest";
        final String xmlClassPath = "format-samples/fault/junit-faults.xml";
        StagePathTestResult stagePathTestResult = postJunitFixture(stage, xmlClassPath);
        TestResultModel testResult = stagePathTestResult.getTestResult();
        assertEquals(1, testResult.getTestSuites().size());
        final TestSuiteModel testSuite = testResult.getTestSuites().get(0);
        assertEquals(5, testSuite.getTests());
        assertEquals(4, testSuite.getTestCasesWithFaults().size());
        assertTestCaseFaults(testSuite);
    }

    @Test
    void postSurefireFailureTest() {
        final String stage = "postSurefireFailureTest";
        final String xmlClassPath = "format-samples/fault/surefire-faults.xml";
        StagePathTestResult stagePathTestResult = postJunitFixture(stage, xmlClassPath);
        TestResultModel testResult = stagePathTestResult.getTestResult();
        assertEquals(1, testResult.getTestSuites().size());
        final TestSuiteModel testSuite = testResult.getTestSuites().get(0);
        assertEquals(4, testSuite.getTests());
        assertEquals(3, testSuite.getTestCasesWithFaults().size());
        assertTestCaseFaults(testSuite);
    }

    static void assertTestCaseFaults(TestSuiteModel testSuite) {
        for (TestCaseModel testCase : testSuite.getTestCasesWithFaults()) {
            assertNotNull(testCase.getTestCaseFaults());
            assertEquals(1, testCase.getTestCaseFaults().size());
            final TestCaseFaultModel testCaseFault = testCase.getTestCaseFaults().get(0);
            assertNotNull(testCaseFault.getMessage());
            assertNotNull(testCaseFault.getType());
            assertNotNull(testCaseFault.getValue());
        }
    }


    StagePathTestResult postJunitFixture(String stage, String xmlResourcePath) {
        final StageDetails stageDetails = getStageDetails(stage);

        String xmlString = ResourceReader.resourceAsString(xmlResourcePath);

        StagePathTestResult result = junitController.doPostJunitXml(stageDetails, List.of(xmlString));
        assertNotNull(result);

        final StagePath stagePath = result.getStagePath();
        assertStageDetails(stageDetails, stagePath);

        final TestResultModel testResult = result.getTestResult();
        assertNotNull(testResult.getTests());
        return result;
    }

    static void assertStageDetails(StageDetails stageDetails, StagePath stagePath) {
        assertEquals(stageDetails.getCompany(), stagePath.getCompany().getCompanyName());
        assertEquals(stageDetails.getOrg(), stagePath.getOrg().getOrgName());
        assertEquals(stageDetails.getRepo(), stagePath.getRepo().getRepoName());
        assertEquals(stageDetails.getBranch(), stagePath.getBranch().getBranchName());
        assertEquals(stageDetails.getJobInfoJson(), stagePath.getJob().getJobInfo());
        assertEquals(stageDetails.getRunReference(), stagePath.getRun().getRunReference());
        assertEquals(stageDetails.getStage(), stagePath.getStage().getStageName());
    }
}
