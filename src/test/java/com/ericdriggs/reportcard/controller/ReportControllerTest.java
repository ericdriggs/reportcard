package com.ericdriggs.reportcard.controller;

import com.ericdriggs.reportcard.ReportcardApplication;
import com.ericdriggs.reportcard.model.ReportMetatData;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")

public class ReportControllerTest {

    @Autowired
    public ReportControllerTest ( ReportControllerUtil reportControllerUtil, ResourceReader resourceReader) {
        this.reportControllerUtil = reportControllerUtil;
        this.resourceReader = resourceReader;
        this.xmlJunit = resourceReader.resourceAsString("classpath:format-samples/sample-junit.xml");
        this.xmlSurefire = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");
    }

    @Autowired
    private ReportControllerUtil reportControllerUtil;
    @Autowired
    private ResourceReader resourceReader;
    private String xmlJunit;
    private String xmlSurefire;

    @Test
    public void insertJunitTest() {
        final ReportMetatData reportMetatData = generateRandomReportMetaData();
        TestResult inserted = reportControllerUtil.doPostXmlJunit(reportMetatData, xmlJunit );
        assertNotNull(inserted);
        assertNotNull(inserted.getTestResultId());
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
