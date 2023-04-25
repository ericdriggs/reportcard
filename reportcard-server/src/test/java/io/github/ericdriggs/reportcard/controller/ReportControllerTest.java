package io.github.ericdriggs.reportcard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.ReportCardService;
import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
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
    public ReportControllerTest(ReportControllerUtil reportControllerUtil, ResourceReader resourceReader, ReportCardService reportCardService) {
        this.reportControllerUtil = reportControllerUtil;
        this.resourceReader = resourceReader;
        this.reportCardService = reportCardService;
        this.xmlJunit = resourceReader.resourceAsString("classpath:format-samples/sample-junit.xml");
        this.xmlSurefire = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");
    }

    private final ReportControllerUtil reportControllerUtil;
    private final ResourceReader resourceReader;
    private final ReportCardService reportCardService;

    private final String xmlJunit;
    private final String xmlSurefire;

    @Test
    public void testStatusTest() {
        Map<Byte, String> statuses = reportCardService.getTestStatusMap();
        assertEquals(8, statuses.size());
        for (TestStatus testStatus : TestStatus.values()) {
            assertEquals(testStatus.name(), statuses.get(testStatus.getStatusId()));
        }
    }

    @Test
    public void insertJunitTest() throws JsonProcessingException {

        final ReportMetaData reportMetatData = generateRandomReportMetaData();
        TestResult inserted = reportControllerUtil.doPostXmlJunit(reportMetatData, xmlJunit);
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

        validateMetadata(reportMetatData);
    }



    @Test
    public void insertSurefireTest() throws JsonProcessingException {


        final ReportMetaData reportMetatData = generateRandomReportMetaData();
        TestResult inserted = reportControllerUtil.doPostXmlSurefire(reportMetatData, Collections.singletonList(xmlSurefire));
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

        validateMetadata(reportMetatData);
    }

    private final static Random random = new Random();


    static ReportMetaData generateRandomReportMetaData() {

        long randLong = random.nextLong();
        ReportMetaData request =
                new ReportMetaData()
                        .setOrg("org" + randLong)
                        .setRepo("repo" + randLong)
                        .setBranch("branch" + randLong)
                        .setSha("sha" + randLong)
                        .setMetadata(TestData.metadata)
                        .setExternalExecutionId("externalExecutionId" + randLong)
                        .setStage("stage" + randLong);
        return request;

    }

    private void validateMetadata(ReportMetaData reportMetaData) throws JsonProcessingException {
        ExecutionStagePath executionStagePath =  reportCardService.getExecutionStagePath(reportMetaData);
        Assertions.assertEquals(reportMetaData.getOrg(), executionStagePath.getOrg().getOrgName() );
        Assertions.assertEquals(reportMetaData.getRepo(), executionStagePath.getRepo().getRepoName() );
        Assertions.assertEquals(reportMetaData.getBranch(), executionStagePath.getBranch().getBranchName() );
        Assertions.assertEquals(reportMetaData.getSha(), executionStagePath.getSha().getSha() );

        JsonCompare.assertJsonEquals(reportMetaData.getMetadata(), executionStagePath.getContext().getMetadata());


        Assertions.assertEquals(reportMetaData.getExternalExecutionId(), executionStagePath.getExecution().getExecutionExternalId() );
        Assertions.assertEquals(reportMetaData.getStage(), executionStagePath.getStage().getStageName() );
    }

}
