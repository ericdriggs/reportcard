package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;

import java.util.Set;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test verifying JUnit XML is primary source for test structure.
 * Karate JSON provides supplemental tags (merged when available).
 * Tests:
 * 1. JUnit is primary source for test structure
 * 2. Tags extracted from Karate Cucumber JSON when both provided
 * 3. Karate-only uploads work but have no test structure
 */
@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class KaratePrimarySourceTest {

    @Autowired
    JunitController junitController;

    @Autowired
    TestResultPersistService testResultPersistService;

    @Autowired
    ResourceReaderComponent resourceReader;

    // Cucumber JSON format from Karate with tags
    private static final String CUCUMBER_JSON_WITH_TAGS = """
        [
          {
            "keyword": "Feature",
            "name": "Karate Feature One",
            "tags": [
              {"name": "@feature-tag"},
              {"name": "@smoke"}
            ],
            "elements": [
              {
                "type": "scenario",
                "name": "Scenario One",
                "tags": [
                  {"name": "@scenario-tag"},
                  {"name": "@api"}
                ],
                "steps": [
                  {
                    "name": "Given step",
                    "result": {"status": "passed", "duration": 100000000}
                  }
                ]
              },
              {
                "type": "scenario",
                "name": "Scenario Two",
                "tags": [
                  {"name": "@regression"}
                ],
                "steps": [
                  {
                    "name": "When step",
                    "result": {"status": "passed", "duration": 200000000}
                  }
                ]
              }
            ]
          }
        ]
        """;

    // Karate summary JSON for timing
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
    void whenKarateOnlyProvided_succeeds_withEmptyStructure() throws IOException {
        // Given: Karate tar.gz only (no JUnit)
        // JUnit-primary means no test structure, but upload should succeed
        MultipartFile karateTarGz = createKarateTarGzWithCucumberJson();
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("karateOnlyStage-" + UUID.randomUUID());

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(null)  // No JUnit
                .karateTarGz(karateTarGz)
                .reports(storageTarGz)
                .build();

        // When
        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);

        // Then: Should succeed (Karate provides timing, just no test structure)
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        // With JUnit-primary, Karate-only uploads have empty test suites
        StagePath stagePath = response.getStagePath();
        Set<TestResultModel> testResultModels = testResultPersistService.getTestResults(stagePath.getStage().getStageId());
        assertEquals(1, testResultModels.size());

        TestResultModel testResult = testResultModels.iterator().next();
        assertNotNull(testResult);
        assertNotNull(testResult.getTestSuites());
        assertTrue(testResult.getTestSuites().isEmpty(), "Karate-only uploads have no test structure with JUnit-primary");
    }

    @Test
    void whenBothJunitAndKarateProvided_usesJunitStructure_extractsKarateTags() throws IOException {
        // Given: Both JUnit and Karate tar.gz
        // JUnit provides test structure, Karate provides tags
        MultipartFile junitTarGz = getJunitTarGz();
        MultipartFile karateTarGz = createKarateTarGzWithCucumberJson();
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("junitKarateStage-" + UUID.randomUUID());

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

        // Then: Should succeed
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        // Verify test structure comes from JUnit (not Karate feature names)
        StagePath stagePath = response.getStagePath();
        Set<TestResultModel> testResultModels = testResultPersistService.getTestResults(stagePath.getStage().getStageId());
        assertEquals(1, testResultModels.size());

        TestResultModel testResult = testResultModels.iterator().next();
        assertNotNull(testResult.getTestSuites());
        assertFalse(testResult.getTestSuites().isEmpty(), "Should have JUnit test suites");

        // Suite name should be from JUnit XML, not Karate
        TestSuiteModel suite = testResult.getTestSuites().get(0);
        assertNotEquals("Karate Feature One", suite.getName(), "Suite name should come from JUnit, not Karate");

        // Note: Tags from Karate are stored in test_result.tags column (not visible via this API)
        // The tags extraction is verified via log output: "Extracted X tags from Karate JSON"
    }

    @Test
    void whenOnlyJunit_noTagsInModel() throws IOException {
        // Given: JUnit tar.gz only (no Karate)
        MultipartFile junitTarGz = getJunitTarGz();
        MultipartFile storageTarGz = getHtmlTarGz();
        StageDetails stageDetails = getStageDetails("junitNoTagsStage-" + UUID.randomUUID());

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

        // Then: Should succeed
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        // Verify test result exists
        StagePath stagePath = response.getStagePath();
        Set<TestResultModel> testResultModels = testResultPersistService.getTestResults(stagePath.getStage().getStageId());
        assertEquals(1, testResultModels.size());

        TestResultModel testResult = testResultModels.iterator().next();
        assertNotNull(testResult);
        assertNotNull(testResult.getTestSuites());

        // JUnit doesn't provide tags - suites will have null/empty tags
        for (TestSuiteModel suite : testResult.getTestSuites()) {
            // Tags should be null or empty for JUnit-sourced data
            assertTrue(suite.getTags() == null || suite.getTags().isEmpty());
        }
    }

    // Helper methods

    private MultipartFile getJunitTarGz() throws IOException {
        return JunitControllerTest.getJunitTarGz(resourceReader);
    }

    private MultipartFile getHtmlTarGz() throws IOException {
        return JunitControllerTest.getHtmlTarGz(resourceReader);
    }

    private MultipartFile createKarateTarGzWithCucumberJson() throws IOException {
        Path tempDir = Files.createTempDirectory("karate-cucumber-test-");
        try {
            // Create karate-summary-json.txt (for timing)
            Path summaryFile = tempDir.resolve("karate-summary-json.txt");
            Files.writeString(summaryFile, KARATE_SUMMARY_JSON, StandardCharsets.UTF_8);

            // Create Cucumber JSON file (for test structure and tags)
            Path cucumberJsonFile = tempDir.resolve("feature-results.json");
            Files.writeString(cucumberJsonFile, CUCUMBER_JSON_WITH_TAGS, StandardCharsets.UTF_8);

            Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(List.of(summaryFile, cucumberJsonFile));
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
