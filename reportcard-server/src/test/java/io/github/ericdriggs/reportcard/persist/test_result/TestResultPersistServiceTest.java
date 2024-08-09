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
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")

public class TestResultPersistServiceTest {

    public static String junitXmlPath = "classpath:format-samples/sample-junit-small.xml";
    public static String surefireXmlPath = "classpath:format-samples/sample-surefire-small.xml";
    public static final String htmlIndexFile = "html-samples/foo/index.html";
    public static List<String> htmlPaths = List.of("classpath:html-samples/foo/index.html", "classpath:html-samples/foo/other.html", "classpath:html-samples/foo/nested/nested.html");

    @Autowired
    public TestResultPersistServiceTest(ResourceReaderComponent resourceReader, TestResultPersistService testResultPersistService) {
        this.testResultPersistService = testResultPersistService;

        this.xmlJunit = resourceReader.resourceAsString(junitXmlPath);
        this.mulipartFile = getMockJunitMultipartFile(resourceReader,"classpath:format-samples/sample-junit-small.xml");
    }


    public static MultipartFile getMockJunitMultipartFile(ResourceReaderComponent resourceReader, String xmlClassPath) {
        String xmlJunit = resourceReader.resourceAsString(xmlClassPath);
        return new MockMultipartFile(
                "file",
                "junit.xml",
                MediaType.APPLICATION_XML_VALUE,
                xmlJunit.getBytes()
        );
    }


    public static MultipartFile[] getMockMultipartFilesFromPathStrings(List<String> resourceClasspaths, ResourceReaderComponent resourceReader) {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (String resourceClassPath : resourceClasspaths) {
            multipartFiles.add(getMockMultipartFile(resourceClassPath, resourceReader));
        }
        return multipartFiles.toArray(new MultipartFile[0]);
    }


    public static MultipartFile getMockMultipartFile(String resourceClasspath, ResourceReaderComponent resourceReader)  {

            String fileName = resourceClasspath.substring(resourceClasspath.lastIndexOf('/') + 1).trim();
            String contents = resourceReader.resourceAsString(resourceClasspath);

            return new MockMultipartFile(
                    fileName,
                    fileName,
                    MediaType.ALL_VALUE,
                    contents.getBytes()
            );

    }

    //private final ResourceReaderComponent resourceReader;
    private final TestResultPersistService testResultPersistService;

    private final String xmlJunit;
    private final MultipartFile mulipartFile;

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

        final StageDetails stageDetails1 = generateRandomReportMetaData();

        Long runId;
        {
            StagePathTestResult stagePathTestResult = testResultPersistService.doPostXmlString(stageDetails1, xmlJunit);
            validateInsertTestResult(stageDetails1, stagePathTestResult);
            runId = stagePathTestResult.getStagePath().getRun().getRunId();
        }

        {
            final String stageName = "stage2";
            StageDetails stageDetails2 = stageDetails1.toBuilder().stage(stageName).build();
            StagePathTestResult stagePathTestResult = testResultPersistService.doPostXml(runId, stageName, mulipartFile);
            validateInsertTestResult(stageDetails2, stagePathTestResult);
            assertEquals(runId, stagePathTestResult.getStagePath().getRun().getRunId());
        }

        {
            final String stageName = "stage3";
            StageDetails stageDetails3 = stageDetails1.toBuilder().stage(stageName).build();
            StagePathTestResult stagePathTestResult = testResultPersistService.doPostXmlString(stageDetails3, xmlJunit);
            validateInsertTestResult(stageDetails3, stagePathTestResult);
            assertEquals(runId, stagePathTestResult.getStagePath().getRun().getRunId());
        }

    }

    @Test
    public void insertArrayTest() throws JsonProcessingException {

        final StageDetails stageDetails = generateRandomReportMetaData();
        StagePathTestResult stagePathTestResult = testResultPersistService.doPostXml(stageDetails, mulipartFile);
        validateInsertTestResult(stageDetails, stagePathTestResult);
    }

    private void validateInsertTestResult(StageDetails stageDetails, StagePathTestResult stagePathTestResult) {
        assertNotNull(stagePathTestResult);


        StagePath stagePath = stagePathTestResult.getStagePath();
        assertNotNull(stagePath);
        assertEquals(false, stagePath.getRun().getIsSuccess());

        TestResultModel inserted = stagePathTestResult.getTestResult();
        assertNotNull(inserted);
        assertNotNull(inserted.getTestResultId());

        assertEquals(1, inserted.getTestSuites().size());
        Assertions.assertEquals(2, inserted.getTests());
        Assertions.assertEquals(0, inserted.getSkipped());
        Assertions.assertEquals(0, inserted.getError());
        Assertions.assertEquals(1, inserted.getFailure());
        assertEquals(false, inserted.getHasSkip());
        assertEquals(false, inserted.getIsSuccess());
        assertEquals(false, inserted.getIsSuccess());
        assertNotNull(inserted.getTestResultCreated());
        assertTrue(Instant.now().isAfter(inserted.getTestResultCreated()));
        Assertions.assertEquals(new BigDecimal("50.5"), inserted.getTime());

        validateStagePath(stageDetails, stagePath);
    }

    private final static Random random = new Random();

    static StageDetails generateRandomReportMetaData() {

        long randLong = random.nextLong();
        StageDetails request =
                StageDetails.builder()
                        .company("company" + randLong)
                        .org("org" + randLong)
                        .repo("repo" + randLong)
                        .branch("branch" + randLong)
                        .sha("sha" + randLong)
                        .jobInfo(TestData.jobInfo)
                        .runReference(UUID.randomUUID())
                        .stage("stage" + randLong)
                        .build();
        return request;
    }

    private void validateStagePath(StageDetails reportMetaData, StagePath stagePath) {
        Assertions.assertEquals(reportMetaData.getCompany(), stagePath.getCompany().getCompanyName());
        Assertions.assertEquals(reportMetaData.getOrg(), stagePath.getOrg().getOrgName());
        Assertions.assertEquals(reportMetaData.getRepo(), stagePath.getRepo().getRepoName());
        Assertions.assertEquals(reportMetaData.getBranch(), stagePath.getBranch().getBranchName());
        Assertions.assertEquals(reportMetaData.getSha(), stagePath.getRun().getSha());

        JsonAssert.assertJsonEquals(reportMetaData.getJobInfo(), stagePath.getJob().getJobInfo());

        Assertions.assertEquals(reportMetaData.getRunReference().toString(), stagePath.getRun().getRunReference());
        Assertions.assertEquals(reportMetaData.getStage(), stagePath.getStage().getStageName());
    }

}
