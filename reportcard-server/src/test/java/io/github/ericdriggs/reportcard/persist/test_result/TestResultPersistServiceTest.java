package io.github.ericdriggs.reportcard.persist.test_result;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")

public class TestResultPersistServiceTest {

    @Autowired
    public TestResultPersistServiceTest(ResourceReaderComponent resourceReader, TestResultPersistService testResultPersistService) {
        this.testResultPersistService = testResultPersistService;
        this.xmlJunit = resourceReader.resourceAsString("classpath:format-samples/sample-junit-small.xml");
        //this.xmlSurefire = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");

        MultipartFile[] files = new MultipartFile[1];
        {
            MockMultipartFile mockMultipartFile
                    = new MockMultipartFile(
                    "file",
                    "junit.xml",
                    MediaType.APPLICATION_XML_VALUE,
                    xmlJunit.getBytes()
            );
            files[0] = mockMultipartFile;
        }
        this.mulipartFiles = files;
    }

    //private final ResourceReaderComponent resourceReader;
    private final TestResultPersistService testResultPersistService;

    private final String xmlJunit;
    private final MultipartFile[] mulipartFiles;

    @Test
    public void testStatusTest() {
        Map<Byte, String> statuses = testResultPersistService.getTestStatusMap();
        assertEquals(8, statuses.size());
        for (TestStatus testStatus : TestStatus.values()) {
            assertEquals(testStatus.name(), statuses.get(testStatus.getStatusId()));
        }
    }

    @Test
    public void insertJunitMultipleStagesTest() throws JsonProcessingException {

        final StageDetails stageDetails = generateRandomReportMetaData();

        Long runId;
        {
            Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXmlStrings(stageDetails, Collections.singletonList(xmlJunit));
            validateInsertTestResult(stageDetails, stagePathTestResultMap);
            runId = stagePathTestResultMap.keySet().stream().findFirst().orElseThrow().getRun().getRunId();
        }

        {
            final String stageName = "stage2";
            stageDetails.setStage(stageName);
            Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXml(runId, stageName, mulipartFiles);
            validateInsertTestResult(stageDetails, stagePathTestResultMap);
        }

        {
            final String stageName = "stage3";
            stageDetails.setStage(stageName);
            Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXmlStrings(stageDetails, Collections.singletonList(xmlJunit));
            validateInsertTestResult(stageDetails, stagePathTestResultMap);
            runId = stagePathTestResultMap.keySet().stream().findFirst().orElseThrow().getRun().getRunId();
        }

    }

    @Test
    public void insertArrayTest() throws JsonProcessingException {

        final StageDetails stageDetails = generateRandomReportMetaData();
        Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXml(stageDetails, mulipartFiles);
        validateInsertTestResult(stageDetails, stagePathTestResultMap);
    }

    private void validateInsertTestResult(StageDetails stageDetails, Map<StagePath, TestResult> stagePathTestResultMap) {
        assertEquals(1, stagePathTestResultMap.size());

        TestResult inserted = null;
        StagePath stagePath = null;

        for (Map.Entry<StagePath, TestResult> entry : stagePathTestResultMap.entrySet()) {
            stagePath = entry.getKey();
            inserted = entry.getValue();
        }

        assertNotNull(inserted);
        assertNotNull(inserted.getTestResultId());

        assertEquals(1, inserted.getTestSuites().size());
        Assertions.assertEquals(3, inserted.getTests());
        Assertions.assertEquals(0, inserted.getSkipped());
        Assertions.assertEquals(0, inserted.getError());
        Assertions.assertEquals(0, inserted.getFailure());
        assertEquals(false, inserted.getHasSkip());
        assertEquals(true, inserted.getIsSuccess());
        assertNotNull(inserted.getTestResultCreated());
        assertTrue(LocalDateTime.now().isAfter(inserted.getTestResultCreated()));
        Assertions.assertEquals(new BigDecimal("0.256"), inserted.getTime());

        validateStagePath(stageDetails, stagePath);
    }

    private final static Random random = new Random();

    static StageDetails generateRandomReportMetaData() {

        long randLong = random.nextLong();
        StageDetails request =
                new StageDetails()
                        .setOrg("org" + randLong)
                        .setRepo("repo" + randLong)
                        .setBranch("branch" + randLong)
                        .setSha("sha" + randLong)
                        .setJobInfo(TestData.jobInfo)
                        .setRunReference("runReference" + randLong)
                        .setStage("stage" + randLong);
        return request;
    }

    private void validateStagePath(StageDetails reportMetaData, StagePath stagePath) {
        Assertions.assertEquals(reportMetaData.getOrg(), stagePath.getOrg().getOrgName());
        Assertions.assertEquals(reportMetaData.getRepo(), stagePath.getRepo().getRepoName());
        Assertions.assertEquals(reportMetaData.getBranch(), stagePath.getBranch().getBranchName());
        Assertions.assertEquals(reportMetaData.getSha(), stagePath.getRun().getSha());

        JsonAssert.assertJsonEquals(reportMetaData.getJobInfo(), stagePath.getJob().getJobInfo());

        Assertions.assertEquals(reportMetaData.getRunReference(), stagePath.getRun().getRunReference());
        Assertions.assertEquals(reportMetaData.getStage(), stagePath.getStage().getStageName());
    }

}
