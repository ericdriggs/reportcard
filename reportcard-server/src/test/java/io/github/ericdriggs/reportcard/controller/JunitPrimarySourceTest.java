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
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import org.jooq.DSLContext;
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
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.github.ericdriggs.reportcard.gen.db.tables.TestResultTable.TEST_RESULT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JunitPrimarySourceTest {

    static final String JUNIT_SUITE_NAME =
            "Untitled suite in /Users/niko/Sites/casperjs/tests/suites/casper/agent.js";

    static final String CUCUMBER_JSON_WITH_TAGS = """
        [
          {
            "keyword": "Feature",
            "name": "Karate Feature One",
            "tags": [{"name": "@feature-tag"}],
            "elements": [
              {
                "type": "scenario",
                "name": "Scenario One",
                "tags": [{"name": "@scenario-tag"}],
                "steps": [{"name": "Given step", "result": {"status": "passed", "duration": 100000000}}]
              },
              {
                "type": "scenario",
                "name": "Scenario Two",
                "tags": [{"name": "@regression"}],
                "steps": [{"name": "When step", "result": {"status": "passed", "duration": 200000000}}]
              }
            ]
          }
        ]
        """;

    static final String KARATE_SUMMARY_JSON = """
        {
          "version": "1.2.0",
          "threads": 1,
          "featuresPassed": 1,
          "featuresFailed": 0,
          "featuresSkipped": 0,
          "resultDate": "2026-01-20 03:00:56 PM",
          "elapsedTime": 5000.0,
          "totalTime": 5000.0
        }
        """;

    @Autowired
    JunitController junitController;

    @Autowired
    TestResultPersistService testResultPersistService;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    DSLContext dsl;

    StageDetails stageDetails(String stageName) {
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

    static MultipartFile karateTarGz(List<String> fileNamesAndContents) throws IOException {
        Path tempDir = Files.createTempDirectory("karate-junit-primary-");
        try {
            java.util.List<Path> paths = new java.util.ArrayList<>();
            for (int i = 0; i < fileNamesAndContents.size(); i += 2) {
                Path f = tempDir.resolve(fileNamesAndContents.get(i));
                Files.writeString(f, fileNamesAndContents.get(i + 1), StandardCharsets.UTF_8);
                paths.add(f);
            }
            Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(paths);
            byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);
            return new MockMultipartFile("karate.tar.gz", "karate.tar.gz",
                    MediaType.APPLICATION_OCTET_STREAM_VALUE, bytes);
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    MultipartFile defaultKarateTarGz() throws IOException {
        return karateTarGz(List.of(
                "karate-summary-json.txt", KARATE_SUMMARY_JSON,
                "feature-results.json", CUCUMBER_JSON_WITH_TAGS));
    }

    JunitHtmlPostRequest request(StageDetails stage, MultipartFile junit, MultipartFile karate) throws IOException {
        return JunitHtmlPostRequest.builder()
                .stageDetails(stage)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(junit)
                .karateTarGz(karate)
                .reports(JunitControllerTest.getHtmlTarGz(resourceReader))
                .build();
    }

    TestResultModel storedTestResult(StagePath stagePath) {
        Set<TestResultModel> models =
                testResultPersistService.getTestResults(stagePath.getStage().getStageId());
        assertEquals(1, models.size());
        return models.iterator().next();
    }

    @Test
    void whenBothJunitAndKarate_junitStructureIsSourceOfRecord() throws IOException {
        StageDetails stage = stageDetails("junitPrimaryStage-" + UUID.randomUUID());
        JunitHtmlPostRequest req = request(stage,
                JunitControllerTest.getJunitTarGz(resourceReader), defaultKarateTarGz());

        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        TestResultModel stored = storedTestResult(response.getStagePath());
        assertEquals(1, stored.getTestSuites().size(), "junit fixture has exactly one suite");
        assertEquals(JUNIT_SUITE_NAME, stored.getTestSuites().get(0).getName(),
                "suite structure must come from junit, not karate");
        assertEquals(2, stored.getTests(), "junit fixture count");
        assertEquals(1, stored.getFailure(), "junit fixture has 1 failure; karate fixture has 0");
    }

    @Test
    void retryWithoutKarate_afterJunitKaratePublish_isIdempotent() throws IOException {
        StageDetails stage = stageDetails("retryStage-" + UUID.randomUUID());

        StagePathStorageResultCountResponse first = junitController.doPostStageJunitStorageTarGZ(
                request(stage, JunitControllerTest.getJunitTarGz(resourceReader), defaultKarateTarGz()));
        assertEquals(201, first.getResponseDetails().getHttpStatus());
        Long firstId = storedTestResult(first.getStagePath()).getTestResultId();

        StagePathStorageResultCountResponse retry = assertDoesNotThrow(() ->
                junitController.doPostStageJunitStorageTarGZ(
                        request(stage, JunitControllerTest.getJunitTarGz(resourceReader), null)),
                "junit-only retry after junit+karate publish must not fail");
        assertEquals(201, retry.getResponseDetails().getHttpStatus());
        assertEquals(firstId, storedTestResult(retry.getStagePath()).getTestResultId(),
                "retry must resolve to the same test_result row");
    }

    @Test
    void karateOnlyCorruptTarGz_returns400() throws IOException {
        StageDetails stage = stageDetails("karateCorruptStage-" + UUID.randomUUID());
        MultipartFile corrupt = new MockMultipartFile("karate.tar.gz", "karate.tar.gz",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, "not a tar.gz".getBytes(StandardCharsets.UTF_8));

        org.springframework.web.server.ResponseStatusException ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> junitController.doPostStageJunitStorageTarGZ(request(stage, null, corrupt)));
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void karateOnlyWithNoCucumberJson_returns400() throws IOException {
        StageDetails stage = stageDetails("karateEmptyStage-" + UUID.randomUUID());
        MultipartFile summaryOnly = karateTarGz(List.of("karate-summary-json.txt", KARATE_SUMMARY_JSON));

        org.springframework.web.server.ResponseStatusException ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> junitController.doPostStageJunitStorageTarGZ(request(stage, null, summaryOnly)));
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void junitWithCorruptKarate_publishSucceedsWithJunitData() throws IOException {
        StageDetails stage = stageDetails("nonBlockingStage-" + UUID.randomUUID());
        MultipartFile corrupt = new MockMultipartFile("karate.tar.gz", "karate.tar.gz",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, "not a tar.gz".getBytes(StandardCharsets.UTF_8));

        StagePathStorageResultCountResponse response = assertDoesNotThrow(() ->
                junitController.doPostStageJunitStorageTarGZ(
                        request(stage, JunitControllerTest.getJunitTarGz(resourceReader), corrupt)),
                "corrupt karate.tar.gz must never fail a junit publish");
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        TestResultModel stored = storedTestResult(response.getStagePath());
        assertEquals(JUNIT_SUITE_NAME, stored.getTestSuites().get(0).getName());
        assertEquals(2, stored.getTests());
    }

    static final String NATIVE_KARATE_JSON = """
        {
          "packageQualifiedName": "feature-results",
          "relativePath": "feature-results.feature",
          "scenarioResults": [
            {"name": "Scenario One", "line": 3, "startTime": 1773863417611, "endTime": 1773863452232, "failed": false},
            {"name": "Scenario Two", "line": 9, "startTime": 1773863418000, "endTime": 1773863455999, "failed": false}
          ]
        }
        """;

    @Test
    void stageTimingComesFromNativeEpochs_notResultDateString() throws IOException {
        StageDetails stage = stageDetails("epochTimingStage-" + UUID.randomUUID());
        MultipartFile karate = karateTarGz(List.of(
                "karate-summary-json.txt", KARATE_SUMMARY_JSON,
                "feature-results.json", CUCUMBER_JSON_WITH_TAGS,
                "feature-results.karate-json.txt", NATIVE_KARATE_JSON));

        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(
                request(stage, JunitControllerTest.getJunitTarGz(resourceReader), karate));
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        Long stageId = response.getStagePath().getStage().getStageId();
        TestResultRecord record = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.STAGE_FK.eq(stageId)).fetchOne();
        assertNotNull(record);
        assertNotNull(record.getStartTime(), "stage start_time must be populated");
        assertNotNull(record.getEndTime(), "stage end_time must be populated");

        // min scenario start / max scenario end, UTC; DATETIME(0) column rounds sub-second precision
        assertEquals(Instant.parse("2026-03-18T19:50:18Z"),
                record.getStartTime().atZone(java.time.ZoneOffset.UTC).toInstant(),
                "start_time must be epoch-derived (a resultDate-parsed value would be hours off)");
        assertEquals(Instant.parse("2026-03-18T19:50:56Z"),
                record.getEndTime().atZone(java.time.ZoneOffset.UTC).toInstant(),
                "end_time must be max scenario endTime");
    }

    @Test
    void junitKaratePublish_populatesFlatTagsColumn() throws IOException {
        StageDetails stage = stageDetails("tagsStage-" + UUID.randomUUID());
        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(
                request(stage, JunitControllerTest.getJunitTarGz(resourceReader), defaultKarateTarGz()));
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        Long stageId = response.getStagePath().getStage().getStageId();
        TestResultRecord record = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.STAGE_FK.eq(stageId)).fetchOne();
        assertNotNull(record.getTags(), "flat tags column must be populated when karate provides tags");
        String tags = String.valueOf(record.getTags());
        assertTrue(tags.contains("feature-tag"), "collected tags: " + tags);
        assertTrue(tags.contains("scenario-tag"), "collected tags: " + tags);
    }

    static final String SYNTHETIC_NATIVE_KARATE_JSON = """
        {
          "packageQualifiedName": "synthetic-feature-three",
          "relativePath": "synthetic-feature-three.feature",
          "scenarioResults": [
            {"name": "synthetic scenario three", "line": 1, "startTime": 1700000000000, "endTime": 1700000060000, "failed": false},
            {"name": "synthetic scenario four", "line": 2, "startTime": 1700000030000, "endTime": 1700000090000, "failed": false}
          ]
        }
        """;

    @Test
    void oneMalformedNativeJsonAmongValidOnes_enrichesFromValidFileOnly() throws IOException {
        StageDetails stage = stageDetails("mixedNativeStage-" + UUID.randomUUID());
        MultipartFile karate = karateTarGz(List.of(
                "karate-summary-json.txt", KARATE_SUMMARY_JSON,
                "feature-results.json", CUCUMBER_JSON_WITH_TAGS,
                "feature-results.karate-json.txt", SYNTHETIC_NATIVE_KARATE_JSON,
                "garbage.karate-json.txt", "not valid json at all {{{"));

        StagePathStorageResultCountResponse response = assertDoesNotThrow(() ->
                junitController.doPostStageJunitStorageTarGZ(
                        request(stage, JunitControllerTest.getJunitTarGz(resourceReader), karate)),
                "one malformed native-json file must not block enrichment from the valid one");
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        TestResultModel stored = storedTestResult(response.getStagePath());
        assertEquals(Instant.ofEpochMilli(1700000000000L), stored.getStartTime(),
                "valid native file's min scenario start must still be applied despite the malformed sibling file");
        assertEquals(Instant.ofEpochMilli(1700000090000L), stored.getEndTime(),
                "valid native file's max scenario end must still be applied despite the malformed sibling file");
    }
}
