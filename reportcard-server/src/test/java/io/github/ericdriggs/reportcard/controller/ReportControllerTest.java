package io.github.ericdriggs.reportcard.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

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

public class ReportControllerTest {

    @Autowired
    public ReportControllerTest(ResourceReaderComponent resourceReader, TestResultPersistService testResultPersistService) {
        this.testResultPersistService = testResultPersistService;
        this.xmlJunit = resourceReader.resourceAsString("classpath:format-samples/sample-junit.xml");
        this.xmlSurefire = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");
    }

    //private final ResourceReaderComponent resourceReader;
    private final TestResultPersistService testResultPersistService;

    private final String xmlJunit;
    private final String xmlSurefire;

    @Test
    public void testStatusTest() {
        Map<Byte, String> statuses = testResultPersistService.getTestStatusMap();
        assertEquals(8, statuses.size());
        for (TestStatus testStatus : TestStatus.values()) {
            assertEquals(testStatus.name(), statuses.get(testStatus.getStatusId()));
        }
    }

    @Test
    public void insertJunitTest() throws JsonProcessingException {

        final StageDetails reportMetatData = generateRandomReportMetaData();
        Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXmlStrings(reportMetatData, Collections.singletonList(xmlJunit));

        assertEquals(1, stagePathTestResultMap.size());

        TestResult inserted = null;
        StagePath stagePath = null;

        for (Map.Entry<StagePath, TestResult> entry : stagePathTestResultMap.entrySet()) {
            stagePath = entry.getKey();
            inserted = entry.getValue();
        }

        assertNotNull(inserted);
        assertNotNull(inserted.getTestResultId());

        assertEquals(66, inserted.getTestSuites().size());
        Assertions.assertEquals(685, inserted.getTests());
        Assertions.assertEquals(0, inserted.getSkipped());
        Assertions.assertEquals(0, inserted.getError());
        Assertions.assertEquals(0, inserted.getFailure());
        assertEquals(false, inserted.getHasSkip());
        assertEquals(true, inserted.getIsSuccess());
        assertNotNull(inserted.getTestResultCreated());
        assertTrue(LocalDateTime.now().isAfter(inserted.getTestResultCreated()));
        Assertions.assertEquals(new BigDecimal("50.500"), inserted.getTime());

        validateStagePath(reportMetatData, stagePath);
    }

    @Test
    public void insertMultipleTest() throws JsonProcessingException {

        final StageDetails reportMetatData = generateRandomReportMetaData();
        Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXmlStrings(reportMetatData, Collections.singletonList(xmlSurefire));

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
        Assertions.assertEquals(1, inserted.getSkipped());
        Assertions.assertEquals(1, inserted.getError());
        Assertions.assertEquals(1, inserted.getFailure());
        assertEquals(true, inserted.getHasSkip());
        assertEquals(false, inserted.getIsSuccess());
        assertNotNull(inserted.getTestResultCreated());
        assertTrue(LocalDateTime.now().isAfter(inserted.getTestResultCreated()));
        Assertions.assertEquals(new BigDecimal("0.014"), inserted.getTime());

        validateStagePath(reportMetatData, stagePath);
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
                        .setJobInfo(TestData.metadata)
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
