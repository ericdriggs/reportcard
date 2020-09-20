package com.ericdriggs.reportcard.controller;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.ReportcardApplication;
import com.ericdriggs.reportcard.model.ReportMetatData;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")

public class ReportControllerTest {

    @Autowired
    public ReportControllerTest (ReportControllerUtil reportControllerUtil, ResourceReader resourceReader, ReportCardService reportCardService) {
        this.reportControllerUtil = reportControllerUtil;
        this.resourceReader = resourceReader;
        this.reportCardService = reportCardService;
        this.xmlJunit = resourceReader.resourceAsString("classpath:format-samples/sample-junit.xml");
        this.xmlSurefire = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");
    }

    private ReportControllerUtil reportControllerUtil;
    private ResourceReader resourceReader;
    private ReportCardService reportCardService;

    private String xmlJunit;
    private String xmlSurefire;

    //TODO: move to other class
    @Test
    public void testStatusTest() {
        Map<Integer, String> statuses = reportCardService.getTestStatuMap();
        assertEquals(8, statuses.size());
        for (TestStatus testStatus : TestStatus.values()) {
            assertEquals(testStatus.name(), statuses.get(testStatus.getStatusId()));
        }
    }

    @Test
    public void insertJunitTest() {


        final ReportMetatData reportMetatData = generateRandomReportMetaData();
        TestResult inserted = reportControllerUtil.doPostXmlJunit(reportMetatData, xmlJunit );
        assertNotNull(inserted);
        assertNotNull(inserted.getTestResultId());

        assertEquals(66, inserted.getTestSuites().size());
        assertEquals(685, inserted.getTests());
        assertEquals(0, inserted.getSkipped());
        assertEquals(0, inserted.getError());
        assertEquals(0, inserted.getFailure());
        assertEquals(false, inserted.getHasSkip());
        assertEquals(true, inserted.getIsSuccess());
        assertNotNull(inserted.getTestResultCreated());
        assertNotNull( LocalDateTime.now().isAfter(inserted.getTestResultCreated()));
        assertEquals(new BigDecimal("50.500"), inserted.getTime());
    }

    private static Random random = new Random();


    static ReportMetatData generateRandomReportMetaData() {

        long randLong = random.nextLong();
        ReportMetatData request =
                new ReportMetatData()
                        .setOrg("org" + randLong)
                        .setRepo("repo" + randLong)
                        .setApp("app" + randLong)
                        .setBranch("branch" + randLong)
                        .setBuildIdentifier("buildIdentifier" + randLong)
                        .setStage("stage" + randLong);

        return request;

    }

}
