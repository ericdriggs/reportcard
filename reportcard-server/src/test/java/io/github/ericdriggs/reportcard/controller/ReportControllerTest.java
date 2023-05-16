package io.github.ericdriggs.reportcard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.AbstractReportCardService;
import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.UploadService;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
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
    public ReportControllerTest(ReportControllerUtil reportControllerUtil, ResourceReaderComponent resourceReader, UploadService uploadService) {
        this.reportControllerUtil = reportControllerUtil;
        //this.resourceReader = resourceReader;
        this.uploadService = uploadService;
        this.xmlJunit = resourceReader.resourceAsString("classpath:format-samples/sample-junit.xml");
        this.xmlSurefire = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");
    }

    private final ReportControllerUtil reportControllerUtil;
    //private final ResourceReaderComponent resourceReader;
    private final UploadService uploadService;

    private final String xmlJunit;
    private final String xmlSurefire;

    @Test
    public void testStatusTest() {
        Map<Byte, String> statuses = uploadService.getTestStatusMap();
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
                        .setJobInfo(TestData.metadata)
                        .setRunReference("executionReference" + randLong)
                        .setStage("stage" + randLong);
        return request;

    }

    private void validateMetadata(ReportMetaData reportMetaData) throws JsonProcessingException {
        RunStagePath runStagePath =  uploadService.getRunStagePath(reportMetaData);
        Assertions.assertEquals(reportMetaData.getOrg(), runStagePath.getOrg().getOrgName() );
        Assertions.assertEquals(reportMetaData.getRepo(), runStagePath.getRepo().getRepoName() );
        Assertions.assertEquals(reportMetaData.getBranch(), runStagePath.getBranch().getBranchName() );
        Assertions.assertEquals(reportMetaData.getSha(), runStagePath.getRun().getSha() );

        JsonAssert.assertJsonEquals(reportMetaData.getJobInfo(), runStagePath.getJob().getJobInfo());


        Assertions.assertEquals(reportMetaData.getRunReference(), runStagePath.getRun().getRunReference() );
        Assertions.assertEquals(reportMetaData.getStage(), runStagePath.getStage().getStageName() );
    }

}
