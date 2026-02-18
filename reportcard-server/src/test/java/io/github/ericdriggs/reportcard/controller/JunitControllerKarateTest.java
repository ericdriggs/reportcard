package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

import static io.github.ericdriggs.reportcard.gen.db.tables.TestResultTable.TEST_RESULT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Karate upload support in JunitController.
 * Tests four scenarios:
 * 1. JUnit-only upload (backwards compatibility)
 * 2. Karate-only upload (new functionality)
 * 3. JUnit + Karate combined upload
 * 4. Neither JUnit nor Karate (should return 400)
 */
@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class JunitControllerKarateTest {

    @Autowired
    JunitController junitController;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    DSLContext dsl;

    private static final String KARATE_SUMMARY_JSON = """
        {
          "version": "1.4.1",
          "threads": 1,
          "featuresPassed": 1,
          "featuresFailed": 0,
          "featuresSkipped": 0,
          "scenariosPassed": 2,
          "resultDate": "2026-01-20 03:00:56 PM",
          "elapsedTime": 5000.0,
          "totalTime": 5000.0
        }
        """;

    private StageDetails getStageDetails(String stageName) {
        return StageDetails.builder()
                .company(TestData.company)
                .org(TestData.org)
                .repo(TestData.repo)
                .branch(TestData.branch)
                .sha(TestData.sha)
                .jobInfo(TestData.jobInfo)
                .runReference(UUID.randomUUID())
                .stage(stageName)
                .build();
    }

    @Test
    void testJunitOnlyUpload_backwardsCompatible() throws IOException {
        // Given: JUnit tar.gz only (existing flow)
        MultipartFile junitTarGz = getJunitTarGz();
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("junitOnlyStage");

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(junitTarGz)
                .karateTarGz(null)
                .reports(storageTarGz)
                .build();

        // When
        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);

        // Then: Should succeed as before
        assertEquals(201, response.getResponseDetails().getHttpStatus());
        assertNotNull(response.getStagePath());
        assertNotNull(response.getStorages());
        // Should have junit and html storages (2 storages)
        assertEquals(2, response.getStorages().size());
    }

    @Test
    void testKarateOnlyUpload_newFunctionality() throws IOException {
        // Given: Karate tar.gz only (new flow)
        MultipartFile karateTarGz = createKarateTarGz();
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("karateOnlyStage");

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(null)
                .karateTarGz(karateTarGz)
                .reports(storageTarGz)
                .build();

        // When
        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);

        // Then: Should succeed with karate storage
        assertEquals(201, response.getResponseDetails().getHttpStatus());
        assertNotNull(response.getStagePath());
        assertNotNull(response.getStorages());
        // Should have karate and html storages (2 storages)
        assertEquals(2, response.getStorages().size());

        // Verify test_result has timing data from karate-summary-json.txt
        Long stageId = response.getStagePath().getStage().getStageId();
        assertNotNull(stageId, "stageId should not be null");

        TestResultRecord testResult = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.STAGE_FK.eq(stageId))
                .fetchOne();
        assertNotNull(testResult, "testResult should exist in database for stage");
        assertNotNull(testResult.getStartTime(), "startTime should be populated from Karate timing");
        assertNotNull(testResult.getEndTime(), "endTime should be populated from Karate timing");
    }

    @Test
    void testJunitAndKarateCombinedUpload() throws IOException {
        // Given: Both JUnit and Karate tar.gz
        MultipartFile junitTarGz = getJunitTarGz();
        MultipartFile karateTarGz = createKarateTarGz();
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("combinedStage");

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(junitTarGz)
                .karateTarGz(karateTarGz)
                .reports(storageTarGz)
                .build();

        // When
        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);

        // Then: Should succeed with all storages
        assertEquals(201, response.getResponseDetails().getHttpStatus());
        assertNotNull(response.getStagePath());
        assertNotNull(response.getStorages());
        // Should have junit, karate, and html storages (3 storages)
        assertEquals(3, response.getStorages().size());
    }

    @Test
    void testNeitherJunitNorKarate_returns400() throws IOException {
        // Given: Neither JUnit nor Karate provided
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("neitherStage");

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(null)
                .karateTarGz(null)
                .reports(storageTarGz)
                .build();

        // When/Then: Should throw 400 Bad Request
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            junitController.doPostStageJunitStorageTarGZ(req)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getReason().contains("junit.tar.gz or karate.tar.gz"));
    }

    @Test
    void testEmptyJunitAndEmptyKarate_returns400() throws IOException {
        // Given: Empty MultipartFiles for both (isEmpty() returns true)
        MultipartFile emptyJunit = new MockMultipartFile("junit.tar.gz", new byte[0]);
        MultipartFile emptyKarate = new MockMultipartFile("karate.tar.gz", new byte[0]);
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("emptyStage");

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(emptyJunit)
                .karateTarGz(emptyKarate)
                .reports(storageTarGz)
                .build();

        // When/Then: Should throw 400 Bad Request
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            junitController.doPostStageJunitStorageTarGZ(req)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // Helper methods to create test tar.gz files

    private MultipartFile getJunitTarGz() throws IOException {
        return JunitControllerTest.getJunitTarGz(resourceReader);
    }

    private MultipartFile getHtmlTarGz() throws IOException {
        return JunitControllerTest.getHtmlTarGz(resourceReader);
    }

    private MultipartFile createKarateTarGz() throws IOException {
        Path tempDir = Files.createTempDirectory("karate-test-");
        try {
            Path summaryFile = tempDir.resolve("karate-summary-json.txt");
            Files.writeString(summaryFile, KARATE_SUMMARY_JSON, StandardCharsets.UTF_8);

            Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(List.of(summaryFile));
            byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);

            return new MockMultipartFile(
                    "karate.tar.gz",
                    "karate.tar.gz",
                    MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    bytes
            );
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
        }
    }
}
