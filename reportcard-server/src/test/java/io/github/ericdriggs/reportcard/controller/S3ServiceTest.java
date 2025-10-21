package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.DirectoryUploadResponse;
import io.github.ericdriggs.reportcard.storage.S3Service;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class S3ServiceTest {

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    S3Service s3Service;

    @Autowired
    StoragePersistService storagePersistService;

    @Test
    void tempDirectoryCleanupAfterSuccessfulUpload() throws IOException {
        Long stageId = 1L;
        final String label = "temp_cleanup_success";
        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFilesFromPathStrings(TestResultPersistServiceTest.htmlPaths, resourceReader);

        Path tempTarGz = null;
        int tempDirCountBefore = countReportcardTempDirectories();
        
        try {
            tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
            MockMultipartFile junitTarGz = new MockMultipartFile(
                    "junit.tar.gz",
                    "junit.tar.gz",
                    MediaType.ALL_VALUE,
                    Files.newInputStream(tempTarGz)
            );

            final StagePath stagePath = storagePersistService.getStagePath(stageId);
            final String prefix = new StoragePath(stagePath, label).getPrefix();

            s3Service.uploadTarGz(prefix, true, junitTarGz);
            
            int tempDirCountAfter = countReportcardTempDirectories();
            assertEquals(tempDirCountBefore, tempDirCountAfter, "Temp directories should be cleaned up after successful upload");

        } finally {
            if (tempTarGz != null) {
                Files.deleteIfExists(tempTarGz);
            }
        }
    }

    @Test
    void tempDirectoryCleanupAfterFailedUpload() throws IOException {
        int tempDirCountBefore = countReportcardTempDirectories();
        
        MockMultipartFile invalidTarGz = new MockMultipartFile(
                "invalid.tar.gz",
                "invalid.tar.gz",
                MediaType.ALL_VALUE,
                "invalid tar content".getBytes()
        );

        assertThrows(Exception.class, () -> {
            s3Service.uploadTarGz("test-prefix", true, invalidTarGz);
        });
        
        int tempDirCountAfter = countReportcardTempDirectories();
        assertEquals(tempDirCountBefore, tempDirCountAfter, "Temp directories should be cleaned up even after failed upload");
    }

    @Test
    void noTempDirectoryAccumulationAfterMultipleOperations() throws IOException {
        Long stageId = 1L;
        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFilesFromPathStrings(TestResultPersistServiceTest.htmlPaths, resourceReader);

        int tempDirCountBefore = countReportcardTempDirectories();
        
        for (int i = 0; i < 3; i++) {
            Path tempTarGz = null;
            try {
                tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
                MockMultipartFile junitTarGz = new MockMultipartFile(
                        "junit.tar.gz",
                        "junit.tar.gz",
                        MediaType.ALL_VALUE,
                        Files.newInputStream(tempTarGz)
                );

                final StagePath stagePath = storagePersistService.getStagePath(stageId);
                final String prefix = new StoragePath(stagePath, "multi_test_" + i).getPrefix();

                s3Service.uploadTarGz(prefix, true, junitTarGz);
                
            } finally {
                if (tempTarGz != null) {
                    Files.deleteIfExists(tempTarGz);
                }
            }
        }
        
        int tempDirCountAfter = countReportcardTempDirectories();
        assertEquals(tempDirCountBefore, tempDirCountAfter, "No temp directories should accumulate after multiple operations");
    }

    private int countReportcardTempDirectories() {
        try {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            try (Stream<Path> stream = Files.list(tempDir)) {
                return (int) stream
                        .filter(Files::isDirectory)
                        .filter(path -> path.getFileName().toString().startsWith("reportcard-"))
                        .count();
            }
        } catch (IOException e) {
            log.warn("Failed to count temp directories", e);
            return 0;
        }
    }

    @Test
    void givenExistingUpload_WhenReUpload_ThenSkipExistingFilesTest() throws IOException {

        Long stageId = 1L;
        final String label = "cucumber_html";
        MultipartFile[] files = TestResultPersistServiceTest.getMockMultipartFilesFromPathStrings(TestResultPersistServiceTest.htmlPaths, resourceReader);

        Path tempTarGz = null;
        try {
            tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
            MockMultipartFile junitTarGz = new MockMultipartFile(
                    "junit.tar.gz",
                    "junit.tar.gz",
                    MediaType.ALL_VALUE,
                    Files.newInputStream(tempTarGz)
            );

            final StagePath stagePath = storagePersistService.getStagePath(stageId);
            final String prefix = new StoragePath(stagePath, label).getPrefix();

            final DirectoryUploadResponse response1 = s3Service.uploadTarGz(prefix, true, junitTarGz);
            assertFalse(response1.isAlreadyUploaded());

            final DirectoryUploadResponse response2 = s3Service.uploadTarGz(prefix, true, junitTarGz);
            assertTrue(response2.isAlreadyUploaded());

        } catch (Exception ex) {
            if (tempTarGz != null) {
                Files.delete(tempTarGz);
            }
        }
    }

}
