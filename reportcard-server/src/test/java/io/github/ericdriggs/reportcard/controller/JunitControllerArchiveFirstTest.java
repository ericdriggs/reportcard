package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.ResponseDetails;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.ericdriggs.reportcard.gen.db.Tables.STORAGE;
import static io.github.ericdriggs.reportcard.gen.db.Tables.TEST_RESULT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JunitControllerArchiveFirstTest {

    static final String SIMULATED_REPORT_FAILURE = "simulated S3 failure for html report";
    static final String SIMULATED_PERSIST_FAILURE = "simulated test result persistence failure";

    @Autowired
    JunitController junitController;

    @SpyBean
    StoragePersistService storagePersistService;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    DSLContext dsl;

    @SpyBean
    S3Service s3Service;

    @SpyBean
    TestResultPersistService testResultPersistService;

    record UploadObservation(String prefix, int testResults, Boolean uploadComplete) {}

    static StageDetails uniqueStageDetails(String stageName) {
        return JunitControllerPublicationIdentityTest.uniqueStageDetails(stageName);
    }

    static String jobInfoParam() {
        return JunitControllerPublicationIdentityTest.jobInfoParam();
    }

    static MultipartFile malformedJunitTarGz() throws IOException {
        final Path dir = Files.createTempDirectory("malformed-junit-");
        try {
            final Path xml = dir.resolve("not-junit.xml");
            Files.writeString(xml, "<notjunit/>", StandardCharsets.UTF_8);
            final Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(List.of(xml));
            final byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);
            return new MockMultipartFile("junit.tar.gz", "junit.tar.gz", MediaType.APPLICATION_OCTET_STREAM_VALUE, bytes);
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(dir.toFile());
        }
    }

    MultipartFile karateTarGz() throws IOException {
        return JunitPrimarySourceTest.karateTarGz(List.of(
                "karate-summary-json.txt", JunitPrimarySourceTest.KARATE_SUMMARY_JSON,
                "feature-results.json", JunitPrimarySourceTest.CUCUMBER_JSON_WITH_TAGS));
    }

    JunitHtmlPostRequest request(StageDetails stageDetails, MultipartFile junit, MultipartFile karate) throws IOException {
        return JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label("html")
                .indexFile(TestResultPersistServiceTest.htmlIndexFile)
                .junitXmls(junit)
                .karateTarGz(karate)
                .reports(JunitControllerTest.getHtmlTarGz(resourceReader))
                .build();
    }

    StagePathStorageResultCountResponse postViaEndpoint(StageDetails stageDetails, MultipartFile junit, MultipartFile karate) throws IOException {
        return junitController.postStageJunitStorageTarGZ(
                stageDetails.getCompany(), stageDetails.getOrg(), stageDetails.getRepo(), stageDetails.getBranch(),
                jobInfoParam(), stageDetails.getRunReference(), stageDetails.getSha(), stageDetails.getStage(),
                "html", TestResultPersistServiceTest.htmlIndexFile, null,
                junit, karate, JunitControllerTest.getHtmlTarGz(resourceReader)).getBody();
    }

    Long stageId(StageDetails stageDetails) {
        final StagePath stagePath = storagePersistService.getStagePath(stageDetails);
        assertNotNull(stagePath.getStage(), "stage must exist once uploads have started");
        return stagePath.getStage().getStageId();
    }

    Map<String, StoragePojo> storagesByLabel(Long stageId) {
        return dsl.selectFrom(STORAGE).where(STORAGE.STAGE_FK.eq(stageId))
                .fetchInto(StoragePojo.class).stream()
                .collect(Collectors.toMap(StoragePojo::getLabel, Function.identity()));
    }

    int testResultCount(Long stageId) {
        return dsl.fetchCount(TEST_RESULT, TEST_RESULT.STAGE_FK.eq(stageId));
    }

    void assertRetainedInS3(StoragePojo storage) {
        assertTrue(storage.getIsUploadComplete(), storage.getLabel() + " must be complete");
        assertFalse(s3Service.listObjectsForPrefixNoDelimiter(storage.getPrefix()).contents().isEmpty(),
                storage.getLabel() + " archive must exist in S3 under " + storage.getPrefix());
    }

    static void assertStructuredError(ResponseDetails details, String expectedDetail) {
        assertTrue(details.getHttpStatus() >= 400, "error status expected, got " + details.getHttpStatus());
        assertNotNull(details.getProblemType());
        assertNotNull(details.getStackTrace());
        assertThat(details.getDetail(), containsString(expectedDetail));
    }

    List<UploadObservation> probeUploads() {
        final List<UploadObservation> observations = Collections.synchronizedList(new ArrayList<>());
        doAnswer(invocation -> {
            final String prefix = invocation.getArgument(0);
            final StoragePojo storage = dsl.selectFrom(STORAGE).where(STORAGE.PREFIX.eq(prefix))
                    .fetchOptionalInto(StoragePojo.class).orElse(null);
            final int testResults = storage == null ? -1 : testResultCount(storage.getStageFk());
            observations.add(new UploadObservation(prefix, testResults, storage == null ? null : storage.getIsUploadComplete()));
            return invocation.callRealMethod();
        }).when(s3Service).uploadTarGz(anyString(), anyBoolean(), any());
        return observations;
    }

    /**
     * Given a combined request whose junit.tar.gz contains XML that is not JUnit or Surefire,
     * When the request is posted,
     * Expected the existing structured error response is returned, the junit and html archives are complete
     * and retained in S3, and no test result exists for the stage.
     */
    @Test
    void whenParsingFailsAfterUploads_expectArchivesRetainedAndNoTestResult() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("archiveFirstParseFailure");

        final StagePathStorageResultCountResponse response = postViaEndpoint(stageDetails, malformedJunitTarGz(), null);
        assertStructuredError(response.getResponseDetails(), "not a junit or surefire xml");

        final Long stageId = stageId(stageDetails);
        final Map<String, StoragePojo> storages = storagesByLabel(stageId);
        assertEquals(Set.of("junit", "html"), storages.keySet());
        storages.values().forEach(this::assertRetainedInS3);
        assertEquals(0, testResultCount(stageId));
    }

    /**
     * Given a valid combined request,
     * When test-result persistence fails after parsing,
     * Expected the structured error is returned, the junit and html archives are complete and retained in S3,
     * and no test result exists for the stage.
     */
    @Test
    void whenPersistenceFails_expectArchivesRetainedAndNoTestResult() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("archiveFirstPersistFailure");
        doThrow(new IllegalStateException(SIMULATED_PERSIST_FAILURE))
                .when(testResultPersistService).insertTestResult(any(TestResultModel.class));

        final StagePathStorageResultCountResponse response =
                postViaEndpoint(stageDetails, JunitControllerTest.getJunitTarGz(resourceReader), null);
        assertStructuredError(response.getResponseDetails(), SIMULATED_PERSIST_FAILURE);

        final Long stageId = stageId(stageDetails);
        final Map<String, StoragePojo> storages = storagesByLabel(stageId);
        assertEquals(Set.of("junit", "html"), storages.keySet());
        storages.values().forEach(this::assertRetainedInS3);
        assertEquals(0, testResultCount(stageId));
    }

    /**
     * Given a combined request with malformed junit.tar.gz, valid karate.tar.gz, and an html report,
     * When the html report upload fails,
     * Expected the response carries the S3 failure (not a parse error, proving parsing never ran), the
     * junit and karate archives stay complete in S3, the html row stays incomplete, and no test result exists.
     */
    @Test
    void whenReportUploadFails_expectEarlierUploadsRetainedAndParsingSkipped() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("archiveFirstReportUploadFailure");
        doThrow(new RuntimeException(SIMULATED_REPORT_FAILURE))
                .when(s3Service).uploadTarGz(argThat(prefix -> prefix != null && prefix.endsWith("/html")), anyBoolean(), any());

        final StagePathStorageResultCountResponse response =
                postViaEndpoint(stageDetails, malformedJunitTarGz(), karateTarGz());
        assertStructuredError(response.getResponseDetails(), SIMULATED_REPORT_FAILURE);
        assertThat(response.getResponseDetails().getDetail(), not(containsString("not a junit or surefire xml")));

        final Long stageId = stageId(stageDetails);
        final Map<String, StoragePojo> storages = storagesByLabel(stageId);
        assertEquals(Set.of("junit", "karate", "html"), storages.keySet());
        assertRetainedInS3(storages.get("junit"));
        assertRetainedInS3(storages.get("karate"));
        assertFalse(storages.get("html").getIsUploadComplete(), "failed html upload must stay incomplete");
        assertEquals(0, testResultCount(stageId));
    }

    void assertArchiveFirstSuccess(StageDetails stageDetails, MultipartFile junit, MultipartFile karate, Set<String> expectedLabels) throws IOException {
        final List<UploadObservation> observations = probeUploads();

        final StagePathStorageResultCountResponse response =
                junitController.doPostStageJunitStorageTarGZ(request(stageDetails, junit, karate));
        assertEquals(201, response.getResponseDetails().getHttpStatus());

        final Long stageId = stageId(stageDetails);
        final Map<String, StoragePojo> storages = storagesByLabel(stageId);
        assertEquals(expectedLabels, storages.keySet());
        assertEquals(expectedLabels.size(), observations.size(), "one upload per submitted archive: " + observations);
        assertEquals(storages.values().stream().map(StoragePojo::getPrefix).collect(Collectors.toSet()),
                observations.stream().map(UploadObservation::prefix).collect(Collectors.toSet()));
        for (UploadObservation observation : observations) {
            assertEquals(0, observation.testResults(), "no test result may exist while uploading " + observation.prefix());
            assertEquals(Boolean.FALSE, observation.uploadComplete(), "row must be incomplete until its upload succeeds: " + observation.prefix());
        }
        storages.values().forEach(this::assertRetainedInS3);

        assertEquals(1, testResultCount(stageId));
        final Integer persistedTests = dsl.select(TEST_RESULT.TESTS).from(TEST_RESULT)
                .where(TEST_RESULT.STAGE_FK.eq(stageId)).fetchOne(TEST_RESULT.TESTS);
        assertNotNull(persistedTests);
        assertTrue(persistedTests > 0, "persisted test result must contain the parsed tests");
    }

    /**
     * Given a combined request with junit.tar.gz and an html report,
     * When it is posted,
     * Expected 201, each archive is uploaded while its row is incomplete and before any test result exists,
     * every row ends complete in S3, and exactly one valid test result exists for the stage.
     */
    @Test
    void whenJunitOnlyPublicationSucceeds_expectEveryUploadBeforeTestResult() throws IOException {
        assertArchiveFirstSuccess(uniqueStageDetails("archiveFirstJunitOnly"),
                JunitControllerTest.getJunitTarGz(resourceReader), null, Set.of("junit", "html"));
    }

    /**
     * Given a combined request with karate.tar.gz and an html report but no junit.tar.gz,
     * When it is posted,
     * Expected 201, archives are uploaded before any test result exists, and exactly one valid test result exists.
     */
    @Test
    void whenKarateOnlyPublicationSucceeds_expectEveryUploadBeforeTestResult() throws IOException {
        assertArchiveFirstSuccess(uniqueStageDetails("archiveFirstKarateOnly"),
                null, karateTarGz(), Set.of("karate", "html"));
    }

    /**
     * Given a combined request with junit.tar.gz, karate.tar.gz, and an html report,
     * When it is posted,
     * Expected 201, archives are uploaded before any test result exists, and exactly one valid test result exists.
     */
    @Test
    void whenJunitAndKaratePublicationSucceeds_expectEveryUploadBeforeTestResult() throws IOException {
        assertArchiveFirstSuccess(uniqueStageDetails("archiveFirstJunitAndKarate"),
                JunitControllerTest.getJunitTarGz(resourceReader), karateTarGz(), Set.of("junit", "karate", "html"));
    }

    /**
     * Given a combined request whose test-result persistence fails on the first attempt,
     * When the same publication is retried after persistence starts succeeding again,
     * Expected the retry returns 201 without re-uploading either archive, and exactly one test result exists.
     */
    @Test
    void whenPersistenceFails_thenRetried_expectNoReuploadAndOneTestResult() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("archiveFirstPersistFailureRetried");
        doThrow(new IllegalStateException(SIMULATED_PERSIST_FAILURE))
                .when(testResultPersistService).insertTestResult(any(TestResultModel.class));

        final StagePathStorageResultCountResponse first =
                postViaEndpoint(stageDetails, JunitControllerTest.getJunitTarGz(resourceReader), null);
        assertStructuredError(first.getResponseDetails(), SIMULATED_PERSIST_FAILURE);

        doCallRealMethod().when(testResultPersistService).insertTestResult(any(TestResultModel.class));
        final StagePathStorageResultCountResponse retry =
                postViaEndpoint(stageDetails, JunitControllerTest.getJunitTarGz(resourceReader), null);
        assertEquals(201, retry.getResponseDetails().getHttpStatus());

        verify(s3Service, times(2)).uploadTarGz(anyString(), anyBoolean(), any());
        assertEquals(1, testResultCount(stageId(stageDetails)));
    }

    /**
     * Given a combined request with junit.tar.gz, karate.tar.gz, and an html report,
     * When the storage-identity insert fails for the second archive (karate), before any upload starts,
     * Expected no upload is attempted, only the first archive's (junit) storage-identity row exists and is
     * incomplete, and no test result exists.
     */
    @Test
    void whenStorageIdentityInsertFailsForSecondArchive_expectNoUploadsAndFirstIdentityOnly() throws IOException {
        final StageDetails stageDetails = uniqueStageDetails("archiveFirstIdentityInsertFailure");
        final String SIMULATED_IDENTITY_FAILURE = "simulated storage identity insert failure";
        doThrow(new RuntimeException(SIMULATED_IDENTITY_FAILURE))
                .when(storagePersistService).getOrInsertStorage(any(), any(), eq("karate"), any());

        final StagePathStorageResultCountResponse response =
                postViaEndpoint(stageDetails, JunitControllerTest.getJunitTarGz(resourceReader), karateTarGz());
        assertStructuredError(response.getResponseDetails(), SIMULATED_IDENTITY_FAILURE);

        verify(s3Service, never()).uploadTarGz(anyString(), anyBoolean(), any());
        final Long stageId = stageId(stageDetails);
        assertEquals(Set.of("junit"), storagesByLabel(stageId).keySet(),
                "only the archive processed before the failure gets an identity row");
        assertFalse(storagesByLabel(stageId).get("junit").getIsUploadComplete());
        assertEquals(0, testResultCount(stageId));
    }
}
