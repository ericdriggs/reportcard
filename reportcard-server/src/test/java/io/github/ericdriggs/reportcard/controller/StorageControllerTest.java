package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathStorage;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.DirectoryUploadResponse;
import io.github.ericdriggs.reportcard.storage.S3Service;
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
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
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

    @Test
    void postStorageOnlyTest() throws IOException {

        final String prefix = "abcd1234";
        MultipartFile file = TestResultPersistServiceTest.getMockJunitMultipartFile(resourceReader);
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
        final String label = "htmlSample";
        ResponseEntity<StagePathStorage> responseEntity = storageController.postStageHtml(stageId, label, files, indexFile);
        assertNotNull(responseEntity);
        StagePathStorage stagePathStorage = responseEntity.getBody();

        assertNotNull(stagePathStorage);

        StagePath stagePath = stagePathStorage.getStagePath();
        Storage storage = stagePathStorage.getStorage();

        assertNotNull(storage.getStorageId());
        assertNotNull(storage.getLabel());

        System.out.println("stagePath: " + stagePath);
        System.out.println("storage: " + storage);


        ListObjectsV2Response s3Objects = s3Service.listObjectsForBucket();
        assertNotNull(s3Objects.contents());
        assertThat(s3Objects.contents().size(), is(greaterThanOrEqualTo(3)));
        System.out.println(s3Objects);
    }
}
