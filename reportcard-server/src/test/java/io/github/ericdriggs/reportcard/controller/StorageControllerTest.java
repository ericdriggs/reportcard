package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class StorageControllerTest {

    @Autowired
    StorageController storageController;

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
        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFiles(TestResultPersistServiceTest.xmlPaths, resourceReader);

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
}
