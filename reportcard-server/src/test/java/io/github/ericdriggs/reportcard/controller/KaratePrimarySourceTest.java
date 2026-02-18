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
 * Integration test verifying Karate Cucumber JSON is primary source for test structure when available.
 * Tests:
 * 1. Karate-only uploads use Karate structure and tags
 * 2. When both JUnit and Karate provided, Karate structure is used (has tags)
 * 3. JUnit-only uploads use JUnit structure (no tags)
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
    void whenKarateOnlyProvided_usesKarateStructure() throws IOException {
        // Given: Karate tar.gz only (no JUnit)
        // Karate is primary source - should use Karate structure and tags
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

        // Then: Should succeed with Karate structure
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        StagePath stagePath = response.getStagePath();
        Set<TestResultModel> testResultModels = testResultPersistService.getTestResults(stagePath.getStage().getStageId());
        assertEquals(1, testResultModels.size());

        TestResultModel testResult = testResultModels.iterator().next();
        assertNotNull(testResult);
        assertNotNull(testResult.getTestSuites());
        assertFalse(testResult.getTestSuites().isEmpty(), "Karate-only uploads should have test structure from Karate");

        // Suite name should come from Karate feature
        TestSuiteModel suite = testResult.getTestSuites().get(0);
        assertEquals("Karate Feature One", suite.getName(), "Suite name should come from Karate feature");
    }

    @Test
    void whenBothJunitAndKarateProvided_usesKarateStructure() throws IOException {
        // Given: Both JUnit and Karate tar.gz
        // Karate is primary - provides test structure with tags
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

        // Verify test structure comes from Karate (primary source)
        StagePath stagePath = response.getStagePath();
        Set<TestResultModel> testResultModels = testResultPersistService.getTestResults(stagePath.getStage().getStageId());
        assertEquals(1, testResultModels.size());

        TestResultModel testResult = testResultModels.iterator().next();
        assertNotNull(testResult.getTestSuites());
        assertFalse(testResult.getTestSuites().isEmpty(), "Should have Karate test suites");

        // Suite name should be from Karate (primary source when both provided)
        TestSuiteModel suite = testResult.getTestSuites().get(0);
        assertEquals("Karate Feature One", suite.getName(), "Suite name should come from Karate (primary source)");
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
