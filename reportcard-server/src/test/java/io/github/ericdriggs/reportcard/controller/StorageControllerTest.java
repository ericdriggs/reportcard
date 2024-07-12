package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.ResponseDetails;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.DirectoryUploadResponse;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class StorageControllerTest {

    @Autowired
    StorageController storageController;

    @Autowired
    JunitController junitController;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    BrowseService browseService;

    @Autowired
    S3Service s3Service;

    private final static ObjectMapper mappper = new ObjectMapper();


    //only used for troubleshooting manually
    @Disabled
    @Test
    void postStorageOnlyTest() {

        final String prefix = "abcd1234";
        MultipartFile file = TestResultPersistServiceTest.getMockJunitMultipartFile(resourceReader, "classpath:format-samples/sample-junit-small.xml");
        MultipartFile[] files = new MultipartFile[1];
        files[0] = file;

        ResponseEntity<DirectoryUploadResponse> responseEntity = storageController.postStorageOnly(prefix, files);
        DirectoryUploadResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getFailedFileUploadResponses());
        assertEquals(0, response.getFailedFileUploadResponses().size());

        ListObjectsV2Response s3Objects = s3Service.listObjectsForBucket();
        assertNotNull(s3Objects.contents());
        assertThat(s3Objects.contents().size(), is(greaterThanOrEqualTo(1)));
        System.out.println(s3Objects);
    }

    @Test
    void postStorageToStageTest() throws IOException {
        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFilesFromPathStrings(TestResultPersistServiceTest.htmlPaths, resourceReader);
        String indexFile = TestResultPersistServiceTest.htmlIndexFile;
        Long stageId = 1L;
        final String label = "cucumber_html";

        Path tempTarGz = null;
        try {
            tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
            MockMultipartFile junitTarGz = new MockMultipartFile(
                    "junit.tar.gz",
                    "junit.tar.gz",
                    MediaType.ALL_VALUE,
                    Files.newInputStream(tempTarGz)
            );
            ResponseEntity<StagePathStorageResponse> responseEntity = storageController.postStageStorageTarGZ(stageId, label,  indexFile, StorageType.HTML, junitTarGz);
            assertNotNull(responseEntity);
            StagePathStorageResponse response = responseEntity.getBody();

            final String expectedStageUrl = "/company/company1/org/org1/repo/repo1/branch/master/job/1/run/1/stage/api";
            {
                final ResponseDetails responseDetails = response.getResponseDetails();
                assertEquals(201, responseDetails.getHttpStatus());
                assertNull(responseDetails.getDetail());
                assertNull(responseDetails.getStackTrace());
                assertNull(responseDetails.getProblemInstance());
                assertNull(responseDetails.getProblemType());
                final Map<String, String> createdUrls = responseDetails.getCreatedUrls();
                assertEquals(2, createdUrls.size());
                assertEquals(createdUrls.get("stage"), expectedStageUrl);
                {
                    final String htmlUrl = createdUrls.get("cucumber_html");
                    assertThat(htmlUrl, matchesPattern("/v1/api/storage/key/rc/company1/org1/repo1/master/.*/1/1/api/cucumber_html/html-samples/foo/index.html"));
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
                assertEquals("runReference1", stagePath.getRun().getRunReference());
                assertEquals("api", stagePath.getStage().getStageName());
                assertEquals(expectedStageUrl, stagePath.getUrl());
            }

            {
                List<StoragePojo> storages = response.getStorages();
                for (StoragePojo storage : storages) {
                    assertNotNull(storage.getStorageId());
                    assertNotNull(storage.getLabel());

                    System.out.println("storage: " + storage);

                    ListObjectsV2Response s3Objects = s3Service.listObjectsForBucket();
                    assertNotNull(s3Objects.contents());
                    assertThat(s3Objects.contents().size(), is(greaterThanOrEqualTo(3)));
                    System.out.println(s3Objects);
                }
            }


        } finally {
            if (tempTarGz != null) {
                Files.delete(tempTarGz);
            }
        }

    }
}
