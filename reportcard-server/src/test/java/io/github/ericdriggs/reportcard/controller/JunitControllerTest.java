package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.ResponseDetails;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private TestResultPersistService testResultPersistService;

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
        assertTrue(testSuite.getHasSkip());
        assertEquals(TestStatus.ERROR, testSuite.getTestStatus());
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
        assertTrue(testSuite.getHasSkip());
        assertEquals(TestStatus.ERROR, testSuite.getTestStatus());
        assertEquals(4, testSuite.getTests());
        assertEquals(3, testSuite.getTestCasesWithFaults().size());
        assertTestCaseFaults(testSuite);
    }

    @Test
    void postJunitHtmlSurefireFormat() throws IOException {
        final MultipartFile junitTarGz = getSurefireTarGz(resourceReader);
        doPostHtmlTest(junitTarGz);
    }

    @Test
    void postJunitHtmlJunitFormat() throws IOException {
        final MultipartFile junitTarGz = getJunitTarGz(resourceReader);
        doPostHtmlTest(junitTarGz);
    }

    void doPostHtmlTest(MultipartFile junitTarGz) throws IOException {

        final String stageName = "apiTest";
        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFilesFromPathStrings(TestResultPersistServiceTest.htmlPaths, resourceReader);
        String indexFile = TestResultPersistServiceTest.htmlIndexFile;
        final String label = "cucumber_html";

        Path tempTarGz = null;
        try {
            tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);

            final StageDetails stageDetails = getStageDetails(stageName);

            final MultipartFile htmlTarGz = getHtmlTarGz(resourceReader);

            final JunitHtmlPostRequest req =
                    JunitHtmlPostRequest.builder()
                            .stageDetails(stageDetails)
                            .label(label)
                            .indexFile(indexFile)
                            .junitXmls(junitTarGz)
                            .reports(htmlTarGz)
                            .build();
            StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);
            assertNotNull(response);

            final String expectedStageUrl = "/company/company1/org/org1/repo/repo1/branch/master/job/1/run/1/stage/apiTest";
            {
                final ResponseDetails responseDetails = response.getResponseDetails();
                assertEquals(201, responseDetails.getHttpStatus());
                assertNull(responseDetails.getDetail());
                assertNull(responseDetails.getStackTrace());
                assertNull(responseDetails.getProblemInstance());
                assertNull(responseDetails.getProblemType());
                final Map<String, String> createdUrls = responseDetails.getCreatedUrls();
                assertEquals(3, createdUrls.size());
                assertEquals(createdUrls.get("stage"), expectedStageUrl);
                {
                    final String htmlUrl = createdUrls.get("cucumber_html");
                    assertThat(htmlUrl, matchesPattern("/v1/api/storage/key/rc/company1/org1/repo1/master/.*/1/1/apiTest/cucumber_html/html-samples/foo/index.html"));
                }
                {
                    final String junitUrl = createdUrls.get("junit");
                    assertThat(junitUrl, matchesPattern("/v1/api/storage/key/rc/company1/org1/repo1/master/.*/1/1/apiTest/junit"));
                }
            }
            {
                StagePath stagePath = response.getStagePath();
                System.out.println("stagePath: " + stagePath);
                assertEquals("company1", stagePath.getCompany().getCompanyName());
                assertEquals("org1", stagePath.getOrg().getOrgName());
                assertEquals("repo1", stagePath.getRepo().getRepoName());
                assertEquals("master", stagePath.getBranch().getBranchName());
                assertJsonEquals("{\"host\": \"foocorp.jenkins.com\", \"pipeline\": \"foopipeline\", \"application\": \"fooapp\"}",
                        stagePath.getJob().getJobInfo());
                assertEquals(TestData.runReference.toString(), stagePath.getRun().getRunReference());
                assertEquals(stageName, stagePath.getStage().getStageName());
                assertEquals(expectedStageUrl, stagePath.getUrl());

                Set<TestResultModel> testResultModels = testResultPersistService.getTestResults(stagePath.getStage().getStageId());
                assertEquals(1, testResultModels.size());

                TestResultModel testResultModel = testResultModels.iterator().next();
                assertNotEquals("[]", testResultModel.getTestSuitesJson());
                final List<TestSuiteModel> testSuiteModels = testResultModel.getTestSuites();
                assertEquals(1, testSuiteModels.size());

                TestSuiteModel testSuiteModel = testSuiteModels.iterator().next();
                final List<TestCaseModel> testCaseModels = testSuiteModel.getTestCases();
                assertEquals(2, testCaseModels.size());

                for (TestCaseModel testCaseModel : testCaseModels) {
                    final List<TestCaseFaultModel> testCaseFaults = testCaseModel.getTestCaseFaults();
                    if ("Default user agent matches /CasperJS/".equals(testCaseModel.getName())) {
                        assertEquals(0, testCaseFaults.size());
                    } else if ("defaultTestValueIs_Value".equals(testCaseModel.getName())) {
                        assertEquals(TestStatus.FAILURE, testCaseModel.getTestStatus());
                        assertEquals(1, testCaseFaults.size());
                    } else {
                        assertEquals(0, testCaseFaults.size());
                    }
                }

            }

            {//storage assertions
                List<StoragePojo> storages = response.getStorages();
                for (StoragePojo storage : storages) {
                    assertNotNull(storage.getStorageId());
                    assertNotNull(storage.getLabel());

                    System.out.println("storage: " + storage);
                    assertTrue(storage.getIsUploadComplete());

                }

                {
                    ListObjectsV2Response s3Objects = s3Service.listObjectsForBucket();

                    assertNotNull(s3Objects.contents());
                    assertThat(s3Objects.contents().size(), is(greaterThanOrEqualTo(3)));
                    System.out.println(s3Objects);

                    assertNotNull(s3Objects.contents());
                    assertThat(s3Objects.contents().size(), is(greaterThanOrEqualTo(3)));
                    System.out.println(s3Objects);

                    {
                        List<String> s3Keys = s3Objects.contents().stream().map(S3Object::key).toList();
                        Integer cucumberCount = 0;
                        Integer junitCount = 0;
                        for (String s3Key : s3Keys) {
                            if (s3Key.contains("cucumber_html")) {
                                cucumberCount++;
                            }
                            if (s3Key.contains("junit.tar.gz")) {
                                junitCount++;
                            }

                        }

                        final Integer expectedJunitCount = 1;
                        final Integer expectedCucumberCount = 3;

                        assertEquals(expectedCucumberCount, cucumberCount);
                        assertEquals(expectedJunitCount, junitCount);
                    }

                }
            }

        } finally {
            if (tempTarGz != null) {
                Files.delete(tempTarGz);
            }
        }

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
        assertEquals(stageDetails.getRunReference().toString(), stagePath.getRun().getRunReference());
        assertEquals(stageDetails.getStage(), stagePath.getStage().getStageName());
    }

    public static MultipartFile getJunitTarGz(ResourceReaderComponent resourceReader) throws IOException {
        return getTestTarGz(resourceReader, "junit.tar.gz", List.of(TestResultPersistServiceTest.junitXmlPath));
    }

    public static MultipartFile getSurefireTarGz(ResourceReaderComponent resourceReader) throws IOException {
        return getTestTarGz(resourceReader, "junit.tar.gz", List.of(TestResultPersistServiceTest.surefireXmlPath));
    }

    public static MultipartFile getHtmlTarGz(ResourceReaderComponent resourceReader) throws IOException {
        return getTestTarGz(resourceReader, "storage.tar.gz", TestResultPersistServiceTest.htmlPaths);
    }

    public static MockMultipartFile getTestTarGz(ResourceReaderComponent resourceReader, String tarGzFileName, List<String> filePaths) throws IOException {
        Path tempTarGz = null;
        try {
            MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFilesFromPathStrings(filePaths, resourceReader);
            tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
            return new MockMultipartFile(
                    tarGzFileName,
                    tarGzFileName,
                    MediaType.ALL_VALUE,
                    Files.newInputStream(tempTarGz)
            );
        } catch (Exception ex) {
            if (tempTarGz != null) {
                Files.delete(tempTarGz);
            }
            throw ex;
        }
    }

}
