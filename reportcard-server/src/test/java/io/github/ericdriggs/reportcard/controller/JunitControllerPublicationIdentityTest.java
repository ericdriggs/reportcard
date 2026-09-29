package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.gen.db.TestData;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.ericdriggs.reportcard.gen.db.Tables.STORAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JunitControllerPublicationIdentityTest {

    static final String SIMULATED_S3_FAILURE = "simulated S3 outage";
    static final Set<String> ALL_LABELS = Set.of("junit", "karate", "cucumber_html_tar_gz", "cucumber_html");

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

    static StageDetails uniqueStageDetails(String stageName) {
        return StageDetails.builder()
                .company(TestData.company)
                .org(TestData.org)
                .repo(TestData.repo)
                .branch(TestData.branch)
                .sha(UUID.randomUUID().toString().substring(0, 12))
                .jobInfo(TestData.jobInfo)
                .runReference(UUID.randomUUID())
                .stage(stageName)
                .build();
    }

    static String jobInfoParam() {
        return TestData.jobInfo.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));
    }

    MultipartFile karateTarGz() throws IOException {
        return JunitPrimarySourceTest.karateTarGz(List.of(
                "karate-summary-json.txt", JunitPrimarySourceTest.KARATE_SUMMARY_JSON,
                "feature-results.json", JunitPrimarySourceTest.CUCUMBER_JSON_WITH_TAGS));
    }

    JunitHtmlPostRequest request(StageDetails stageDetails, String label, boolean withKarate) throws IOException {
        return JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label(label)
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(JunitControllerTest.getJunitTarGz(resourceReader))
                .karateTarGz(withKarate ? karateTarGz() : null)
                .reports(JunitControllerTest.getHtmlTarGz(resourceReader))
                .build();
    }

    Map<String, StoragePojo> storagesByLabel(Long stageId) {
        return dsl.selectFrom(STORAGE)
                .where(STORAGE.STAGE_FK.eq(stageId))
                .fetchInto(StoragePojo.class)
                .stream()
                .collect(Collectors.toMap(StoragePojo::getLabel, Function.identity()));
    }

    void failEveryUpload() {
        doThrow(new RuntimeException(SIMULATED_S3_FAILURE))
                .when(s3Service).uploadTarGz(anyString(), anyBoolean(), any());
    }

    /**
     * Given a combined JUnit, Karate, and cucumber_html report request to POST /v1/api/junit/storage/{label}/tar.gz,
     * When the first S3 upload fails,
     * Expected the endpoint returns the structured upload error, the complete hierarchy exists, and exactly one
     * incomplete storage row exists per submitted archive label with the canonical prefix calculated from the
     * persisted hierarchy and the archive's storage type.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: combined publication creates the hierarchy and one incomplete storage identity per submitted archive
     */
    @Test
    void whenFirstUploadFails_expectIncompleteIdentityForEverySubmittedArchive() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("identityFirstUploadFails");
        failEveryUpload();

        final StagePathStorageResultCountResponse response = junitController.postStageJunitStorageTarGZ(
                stageDetails.getCompany(), stageDetails.getOrg(), stageDetails.getRepo(), stageDetails.getBranch(),
                jobInfoParam(), stageDetails.getRunReference(), stageDetails.getSha(), stageDetails.getStage(),
                "cucumber_html", TestResultPersistServiceTest.htmlIndexFile, null,
                JunitControllerTest.getJunitTarGz(resourceReader), karateTarGz(),
                JunitControllerTest.getHtmlTarGz(resourceReader)).getBody();
        assertNotNull(response);
        assertTrue(response.getResponseDetails().getHttpStatus() >= 400);
        assertThat(response.getResponseDetails().getDetail(), containsString(SIMULATED_S3_FAILURE));

        final StagePath stagePath = storagePersistService.getStagePath(stageDetails);
        assertTrue(stagePath.isComplete(), "hierarchy must be complete: " + stagePath.validate());
        assertEquals(stageDetails.getRunReference().toString(), stagePath.getRun().getRunReference());
        assertEquals(stageDetails.getStage(), stagePath.getStage().getStageName());

        final Map<String, StoragePojo> storages = storagesByLabel(stagePath.getStage().getStageId());
        assertEquals(ALL_LABELS, storages.keySet());

        final Map<String, StorageType> expectedTypes = Map.of(
                "junit", StorageType.JUNIT,
                "karate", StorageType.KARATE_JSON,
                "cucumber_html_tar_gz", StorageType.TAR_GZ,
                "cucumber_html", StorageType.HTML);
        for (Map.Entry<String, StoragePojo> entry : storages.entrySet()) {
            final String label = entry.getKey();
            final StoragePojo storage = entry.getValue();
            assertFalse(storage.getIsUploadComplete(), label + " must be incomplete");
            assertEquals(new StoragePath(stagePath, label).getPrefix(), storage.getPrefix(), label + " prefix");
            assertEquals(expectedTypes.get(label).getStorageTypeId(), storage.getStorageType(), label + " type");
        }
    }

    /**
     * Given a combined publication whose uploads all failed,
     * When the same request is submitted again and uploads succeed,
     * Expected the retry returns 201 and reuses the same run, stage, and storage rows (same ids, one row per
     * label), and every storage row becomes complete.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: combined publication reuses the canonical hierarchy and storage identities
     */
    @Test
    void whenRequestIsRetriedAfterUploadFailure_expectHierarchyAndIdentitiesReused() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("identityReused");
        failEveryUpload();
        final RuntimeException uploadFailure = assertThrows(RuntimeException.class,
                () -> junitController.doPostStageJunitStorageTarGZ(request(stageDetails, "cucumber_html", true)));
        assertThat(uploadFailure.getMessage(), containsString(SIMULATED_S3_FAILURE));

        final StagePath firstPath = storagePersistService.getStagePath(stageDetails);
        final Map<String, StoragePojo> firstStorages = storagesByLabel(firstPath.getStage().getStageId());
        assertEquals(ALL_LABELS, firstStorages.keySet());

        reset(s3Service);
        final StagePathStorageResultCountResponse retry =
                junitController.doPostStageJunitStorageTarGZ(request(stageDetails, "cucumber_html", true));
        assertEquals(201, retry.getResponseDetails().getHttpStatus());

        final StagePath retryPath = storagePersistService.getStagePath(stageDetails);
        assertEquals(firstPath.getRun().getRunId(), retryPath.getRun().getRunId());
        assertEquals(firstPath.getStage().getStageId(), retryPath.getStage().getStageId());

        final Map<String, StoragePojo> retryStorages = storagesByLabel(retryPath.getStage().getStageId());
        assertEquals(ALL_LABELS, retryStorages.keySet());
        for (String label : ALL_LABELS) {
            assertEquals(firstStorages.get(label).getStorageId(), retryStorages.get(label).getStorageId(), label);
            assertTrue(retryStorages.get(label).getIsUploadComplete(), label + " must be complete after retry");
        }
    }

    /**
     * Given a combined request with junit.tar.gz and an html report but no karate.tar.gz,
     * When the first S3 upload fails,
     * Expected exactly the junit and html storage rows exist, both incomplete, with no karate or
     * cucumber_html_tar_gz row.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: combined publication creates identities only for submitted archives
     */
    @Test
    void whenOnlyJunitAndReportAreSubmitted_expectIdentitiesOnlyForSubmittedArchives() {
        final StageDetails stageDetails = uniqueStageDetails("identityJunitAndReport");
        failEveryUpload();
        assertThrows(RuntimeException.class,
                () -> junitController.doPostStageJunitStorageTarGZ(request(stageDetails, "html", false)));

        final StagePath stagePath = storagePersistService.getStagePath(stageDetails);
        final Map<String, StoragePojo> storages = storagesByLabel(stagePath.getStage().getStageId());
        assertEquals(Set.of("junit", "html"), storages.keySet());
        storages.values().forEach(s -> assertFalse(s.getIsUploadComplete(), s.getLabel() + " must be incomplete"));
    }
}
