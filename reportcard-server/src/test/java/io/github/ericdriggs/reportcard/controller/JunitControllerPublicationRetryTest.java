package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JunitControllerPublicationRetryTest {

    static final Pattern DATE_SEGMENT = Pattern.compile("/(\\d{4}-\\d{2}-\\d{2})/");
    static final Set<String> LABELS = Set.of("junit", "karate", "html");

    @Autowired
    JunitController junitController;

    @Autowired
    StoragePersistService storagePersistService;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    DSLContext dsl;

    @SpyBean
    S3Service s3Service;

    record SeededAttempt(StagePath stagePath, Map<String, StoragePojo> storages, Map<String, String> todayPrefixes) {}

    static String priorUtcDay(String prefix) {
        final Matcher matcher = DATE_SEGMENT.matcher(prefix);
        assertTrue(matcher.find(), "prefix must contain a UTC date segment: " + prefix);
        final String priorDay = LocalDate.parse(matcher.group(1)).minusDays(1).toString();
        return prefix.substring(0, matcher.start(1)) + priorDay + prefix.substring(matcher.end(1));
    }

    MultipartFile karateTarGz() throws IOException {
        return JunitPrimarySourceTest.karateTarGz(List.of(
                "karate-summary-json.txt", JunitPrimarySourceTest.KARATE_SUMMARY_JSON,
                "feature-results.json", JunitPrimarySourceTest.CUCUMBER_JSON_WITH_TAGS));
    }

    MultipartFile archiveFor(String label) throws IOException {
        return switch (label) {
            case "junit" -> JunitControllerTest.getJunitTarGz(resourceReader);
            case "karate" -> karateTarGz();
            default -> JunitControllerTest.getHtmlTarGz(resourceReader);
        };
    }

    JunitHtmlPostRequest request(StageDetails stageDetails) throws IOException {
        return JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(archiveFor("junit"))
                .karateTarGz(archiveFor("karate"))
                .reports(archiveFor("html"))
                .build();
    }

    SeededAttempt seedPriorDayAttempt(StageDetails stageDetails, Set<String> completeLabels) throws IOException {
        final StagePath stagePath = storagePersistService.getUpsertedStagePath(stageDetails);
        final Long stageId = stagePath.getStage().getStageId();
        final Map<String, String> todayPrefixes = new HashMap<>();
        final Map<String, Object[]> identity = Map.of(
                "junit", new Object[]{"junit.tar.gz", StorageType.JUNIT, false},
                "karate", new Object[]{"karate.tar.gz", StorageType.KARATE_JSON, false},
                "html", new Object[]{TestResultPersistServiceTest.htmlIndexFile, StorageType.HTML, true});
        for (String label : LABELS) {
            final String todayPrefix = new StoragePath(stagePath, label).getPrefix();
            todayPrefixes.put(label, todayPrefix);
            final String priorPrefix = priorUtcDay(todayPrefix);
            final Object[] spec = identity.get(label);
            final boolean complete = completeLabels.contains(label);
            if (complete) {
                s3Service.uploadTarGz(priorPrefix, (Boolean) spec[2], archiveFor(label));
            }
            dsl.insertInto(STORAGE)
                    .set(STORAGE.STAGE_FK, stageId)
                    .set(STORAGE.LABEL, label)
                    .set(STORAGE.PREFIX, priorPrefix)
                    .set(STORAGE.INDEX_FILE, (String) spec[0])
                    .set(STORAGE.STORAGE_TYPE, ((StorageType) spec[1]).getStorageTypeId())
                    .set(STORAGE.IS_UPLOAD_COMPLETE, complete)
                    .execute();
        }
        clearInvocations(s3Service);
        return new SeededAttempt(stagePath, storagesByLabel(stageId), todayPrefixes);
    }

    Map<String, StoragePojo> storagesByLabel(Long stageId) {
        return dsl.selectFrom(STORAGE).where(STORAGE.STAGE_FK.eq(stageId))
                .fetchInto(StoragePojo.class).stream()
                .collect(Collectors.toMap(StoragePojo::getLabel, Function.identity()));
    }

    void assertOneLogicalPublication(StageDetails stageDetails, SeededAttempt seeded) {
        final Long runId = seeded.stagePath().getRun().getRunId();
        final Long stageId = seeded.stagePath().getStage().getStageId();
        assertEquals(1, dsl.fetchCount(RUN, RUN.RUN_REFERENCE.eq(stageDetails.getRunReference().toString())));
        assertEquals(1, dsl.fetchCount(STAGE, STAGE.RUN_FK.eq(runId)));
        assertEquals(1, dsl.fetchCount(TEST_RESULT, TEST_RESULT.STAGE_FK.eq(stageId)));

        final StagePath retryPath = storagePersistService.getStagePath(stageDetails);
        assertEquals(runId, retryPath.getRun().getRunId());
        assertEquals(stageId, retryPath.getStage().getStageId());

        final Map<String, StoragePojo> after = storagesByLabel(stageId);
        assertEquals(LABELS, after.keySet());
        for (String label : LABELS) {
            final StoragePojo before = seeded.storages().get(label);
            assertEquals(before.getStorageId(), after.get(label).getStorageId(), label + " storage id reused");
            assertEquals(before.getPrefix(), after.get(label).getPrefix(), label + " persisted prefix kept");
            assertTrue(after.get(label).getIsUploadComplete(), label + " complete");
            assertFalse(s3Service.listObjectsForPrefixNoDelimiter(before.getPrefix()).contents().isEmpty(),
                    label + " archive exists under its persisted prefix");
            assertTrue(s3Service.listObjectsForPrefixNoDelimiter(seeded.todayPrefixes().get(label)).contents().isEmpty(),
                    label + " must not be written under a second, recomputed prefix");
        }
    }

    /**
     * Given an earlier attempt on a prior UTC day left the hierarchy and three incomplete storage rows,
     * When the same publication is retried,
     * Expected 201, each archive is uploaded once to its persisted prefix, the run, stage, and storage rows are
     * reused, and exactly one test result exists.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: retry reuses run, stage, and storage records and uploads incomplete storage
     */
    @Test
    void whenRetryingAfterAllUploadsFailed_expectPersistedIdentitiesUploadedAndOneTestResult() throws IOException {
        final StageDetails stageDetails = JunitControllerPublicationIdentityTest.uniqueStageDetails("retryAllIncomplete");
        final SeededAttempt seeded = seedPriorDayAttempt(stageDetails, Set.of());

        final StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(request(stageDetails));
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        verify(s3Service, times(3)).uploadTarGz(anyString(), anyBoolean(), any());
        for (String label : LABELS) {
            verify(s3Service).uploadTarGz(eq(seeded.storages().get(label).getPrefix()), anyBoolean(), any());
        }
        assertOneLogicalPublication(stageDetails, seeded);
    }

    /**
     * Given an earlier attempt on a prior UTC day completed junit and karate uploads but not the html report,
     * When the same publication is retried,
     * Expected 201, only the html archive is uploaded (to its persisted prefix), complete storage is skipped,
     * and exactly one test result exists.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: retry uploads only incomplete storage and skips complete storage
     */
    @Test
    void whenRetryingWithOnlyReportIncomplete_expectOnlyReportUploaded() throws IOException {
        final StageDetails stageDetails = JunitControllerPublicationIdentityTest.uniqueStageDetails("retryReportIncomplete");
        final SeededAttempt seeded = seedPriorDayAttempt(stageDetails, Set.of("junit", "karate"));

        final StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(request(stageDetails));
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        verify(s3Service, times(1)).uploadTarGz(anyString(), anyBoolean(), any());
        verify(s3Service).uploadTarGz(eq(seeded.storages().get("html").getPrefix()), eq(true), any());
        assertOneLogicalPublication(stageDetails, seeded);
    }

    /**
     * Given an earlier attempt on a prior UTC day completed every upload but persisted no test result,
     * When the same publication is retried twice,
     * Expected each retry returns 201 without any upload, the first retry parses and persists the test result,
     * and one run, one stage, and one test result exist after both retries.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: retry with complete storage still parses and persists one test result
     */
    @Test
    void whenRetryingWithAllStorageCompleteAndNoTestResult_expectNoUploadsAndOneTestResult() throws IOException {
        final StageDetails stageDetails = JunitControllerPublicationIdentityTest.uniqueStageDetails("retryAllComplete");
        final SeededAttempt seeded = seedPriorDayAttempt(stageDetails, LABELS);
        assertEquals(0, dsl.fetchCount(TEST_RESULT, TEST_RESULT.STAGE_FK.eq(seeded.stagePath().getStage().getStageId())));

        assertEquals(201, junitController.doPostStageJunitStorageTarGZ(request(stageDetails)).getResponseDetails().getHttpStatus());
        assertEquals(201, junitController.doPostStageJunitStorageTarGZ(request(stageDetails)).getResponseDetails().getHttpStatus());

        verify(s3Service, never()).uploadTarGz(anyString(), anyBoolean(), any());
        assertOneLogicalPublication(stageDetails, seeded);
    }
}
