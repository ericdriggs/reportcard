package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.JunitController;
import io.github.ericdriggs.reportcard.controller.model.StagePathTestResultResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.model.publication.FailedPublication;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PublishedRunStateTest {

    static final Instant NOW = Instant.parse("2103-01-01T00:00:00Z");

    static final String FAILING_XML = """
            <testsuite name="publication" tests="1" failures="1" errors="0" skipped="0" time="0.1">
              <testcase name="fails" classname="publication.Suite" time="0.1">
                <failure message="expected failure" type="AssertionError">expected failure</failure>
              </testcase>
            </testsuite>
            """;

    @Autowired
    StoragePersistService storagePersistService;

    @SpyBean
    TestResultPersistService testResultPersistService;

    @Autowired
    FailedPublicationService failedPublicationService;

    @Autowired
    JunitController junitController;

    @Autowired
    DSLContext dsl;

    final String company = "runstate-" + UUID.randomUUID().toString().substring(0, 8);

    StageDetails stageDetails(UUID runReference, String sha, String stageName) {
        return FailedPublicationSelectionTest.stageDetails(company, stageName).toBuilder()
                .runReference(runReference).sha(sha).build();
    }

    Boolean runIsSuccess(Long runId) {
        return dsl.select(RUN.IS_SUCCESS).from(RUN).where(RUN.RUN_ID.eq(runId)).fetchOne(RUN.IS_SUCCESS);
    }

    Instant jobLastRun(Long jobId) {
        return dsl.select(JOB.LAST_RUN).from(JOB).where(JOB.JOB_ID.eq(jobId)).fetchOne(JOB.LAST_RUN);
    }

    Instant branchLastRun(Integer branchId) {
        return dsl.select(BRANCH.LAST_RUN).from(BRANCH).where(BRANCH.BRANCH_ID.eq(branchId)).fetchOne(BRANCH.LAST_RUN);
    }

    int testResultCount(Long stageId) {
        return dsl.fetchCount(TEST_RESULT, TEST_RESULT.STAGE_FK.eq(stageId));
    }

    /**
     * Given a new run reference,
     * When the hierarchy is created,
     * Expected the new run row and the returned run have is_success=false.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: a new run starts unsuccessful
     */
    @Test
    void whenRunIsCreated_expectRunUnsuccessful() {
        final StagePath stagePath = storagePersistService.getUpsertedStagePath(
                FailedPublicationSelectionTest.stageDetails(company, "created"));
        assertFalse(stagePath.getRun().getIsSuccess());
        assertFalse(runIsSuccess(stagePath.getRun().getRunId()));
    }

    /**
     * Given a new run and a passing test result,
     * When the last-run update fails inside test-result persistence,
     * Expected the failure propagates, no test result is persisted, the run stays unsuccessful, and job and branch
     * last_run are unchanged.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: test-result persistence, run-success recomputation, and last-run updates are atomic
     */
    @Test
    void whenLastRunUpdateFailsDuringPersistence_expectNoTestResultAndUnchangedRunState() {
        final StagePath stagePath = storagePersistService.getUpsertedStagePath(
                FailedPublicationSelectionTest.stageDetails(company, "atomic"));
        final Long runId = stagePath.getRun().getRunId();
        final Instant jobLastRunBefore = jobLastRun(stagePath.getJob().getJobId());
        final Instant branchLastRunBefore = branchLastRun(stagePath.getBranch().getBranchId());
        dsl.update(RUN).set(RUN.IS_SUCCESS, false).where(RUN.RUN_ID.eq(runId)).execute();

        final TestResultModel model = JunitSurefireXmlParseUtil.parseTestXml(List.of(FailedPublicationSelectionTest.PASSING_XML));
        doThrow(new IllegalStateException("simulated last_run failure"))
                .when(testResultPersistService).updateLastRunToNow(any(StagePath.class));

        final IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> testResultPersistService.insertTestResult(stagePath, model));
        assertEquals("simulated last_run failure", thrown.getMessage());

        assertEquals(0, testResultCount(stagePath.getStage().getStageId()));
        assertFalse(runIsSuccess(runId));
        assertEquals(jobLastRunBefore, jobLastRun(stagePath.getJob().getJobId()));
        assertEquals(branchLastRunBefore, branchLastRun(stagePath.getBranch().getBranchId()));
    }

    /**
     * Given an unsuccessful run with one stage and no test results,
     * When a passing test result is persisted,
     * Expected the run becomes successful in the database and in the returned stage path, and job and branch
     * last_run are set to the persistence time.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: run success and recency derive from persisted test results
     */
    @Test
    void whenPassingResultIsPersistedForUnsuccessfulRun_expectRunPromotedToSuccessfulAndRecencyUpdated() {
        final StageDetails details = FailedPublicationSelectionTest.stageDetails(company, "promoted");
        final StagePath stagePath = storagePersistService.getUpsertedStagePath(details);
        final Long runId = stagePath.getRun().getRunId();
        dsl.update(RUN).set(RUN.IS_SUCCESS, false).where(RUN.RUN_ID.eq(runId)).execute();

        final Instant before = Instant.now().minusSeconds(1);
        final StagePathTestResult result = testResultPersistService.doPostXmlString(details, FailedPublicationSelectionTest.PASSING_XML);

        assertTrue(runIsSuccess(runId));
        assertTrue(result.getStagePath().getRun().getIsSuccess());
        assertFalse(jobLastRun(stagePath.getJob().getJobId()).isBefore(before));
        assertFalse(branchLastRun(stagePath.getBranch().getBranchId()).isBefore(before));
    }

    /**
     * Given a run whose first stage has storage but no test result,
     * When a passing stage and then a failing stage are persisted in the same run,
     * Expected the run is unsuccessful with only the hierarchy-only stage, successful after the passing stage
     * (the hierarchy-only stage does not count), unsuccessful after the failing stage, and the hierarchy-only stage
     * is reported by failed-publication selection.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: multi-stage run success uses only stages with persisted test results
     */
    @Test
    void whenRunHasHierarchyOnlyAndPublishedStages_expectSuccessFromPublishedStagesOnly() {
        FailedPublicationSelectionTest.seedStage(storagePersistService, testResultPersistService, dsl,
                company, "boundary", NOW.minus(Duration.ofDays(3)), List.of("junit"), false);

        final UUID runReference = UUID.randomUUID();
        final String sha = UUID.randomUUID().toString().substring(0, 12);
        final StagePath hierarchyStage = storagePersistService.getUpsertedStagePath(stageDetails(runReference, sha, "hierarchyOnly"));
        storagePersistService.upsertStoragePath("junit.tar.gz", "junit",
                new StoragePath(hierarchyStage, "junit").getPrefix(), hierarchyStage.getStage().getStageId(), StorageType.JUNIT);
        final Long runId = hierarchyStage.getRun().getRunId();
        assertFalse(runIsSuccess(runId), "hierarchy-only run must be unsuccessful");

        testResultPersistService.doPostXmlString(stageDetails(runReference, sha, "passing"), FailedPublicationSelectionTest.PASSING_XML);
        assertEquals(runId, storagePersistService.getStagePath(stageDetails(runReference, sha, "passing")).getRun().getRunId());
        assertTrue(runIsSuccess(runId), "one passing persisted stage makes the run successful");

        testResultPersistService.doPostXmlString(stageDetails(runReference, sha, "failing"), FAILING_XML);
        assertFalse(runIsSuccess(runId), "a failing persisted stage makes the run unsuccessful");

        dsl.update(RUN).set(RUN.RUN_DATE, NOW.minus(Duration.ofMinutes(30))).where(RUN.RUN_ID.eq(runId)).execute();
        final List<Long> reported = failedPublicationService.getFailedPublications(1, 10, 200, NOW)
                .getFailedPublications().stream().map(FailedPublication::getStageId).toList();
        assertEquals(List.of(hierarchyStage.getStage().getStageId()), reported,
                "only the hierarchy-only stage is reported; published stages are not");
    }

    static MultipartFile junitTarGz(String xml) throws IOException {
        final Path dir = Files.createTempDirectory("run-state-junit-");
        try {
            final Path file = dir.resolve("result.xml");
            Files.writeString(file, xml, StandardCharsets.UTF_8);
            final Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(List.of(file));
            final byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);
            return new MockMultipartFile("junit.tar.gz", "junit.tar.gz", MediaType.APPLICATION_OCTET_STREAM_VALUE, bytes);
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(dir.toFile());
        }
    }

    StagePathTestResultResponse postJunitOnly(UUID runReference, String sha, String stage, String xml) throws IOException {
        return junitController.postJunitXml(company, "org1", "repo1", "main",
                "application=runstateapp,pipeline=junitonly", runReference, sha, stage, null, junitTarGz(xml)).getBody();
    }

    /**
     * Given a new run published through the JUnit-only XML route (POST /v1/api/junit/tar.gz),
     * When a failure is injected after the test-result insert inside persistence (the last-run update throws),
     * Expected the route returns the structured error, no test result exists, and the run is unsuccessful; when the
     * passing result is posted again without the failure, the run is promoted to successful and job and branch
     * last_run are set.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: JUnit-only publication persists test results atomically and derives run success and recency
     */
    @Test
    void whenJunitOnlyPersistenceFailsAfterInsert_expectRollbackThenPromotionOnRetry() throws IOException {
        final UUID runReference = UUID.randomUUID();
        final String sha = UUID.randomUUID().toString().substring(0, 12);
        doThrow(new IllegalStateException("simulated last_run failure"))
                .when(testResultPersistService).updateLastRunToNow(any(StagePath.class));

        final StagePathTestResultResponse failed = postJunitOnly(runReference, sha, "junitOnly", FailedPublicationSelectionTest.PASSING_XML);
        assertNotNull(failed);
        assertTrue(failed.getResponseDetails().getHttpStatus() >= 400);

        final StageDetails details = StageDetails.builder()
                .company(company).org("org1").repo("repo1").branch("main")
                .jobInfo(new java.util.TreeMap<>(java.util.Map.of("application", "runstateapp", "pipeline", "junitonly")))
                .runReference(runReference).sha(sha).stage("junitOnly").build();
        final StagePath stagePath = storagePersistService.getStagePath(details);
        assertNotNull(stagePath.getStage(), "hierarchy exists after the failed attempt");
        assertEquals(0, testResultCount(stagePath.getStage().getStageId()), "insert must roll back with the failed last_run update");
        assertFalse(runIsSuccess(stagePath.getRun().getRunId()), "run stays unsuccessful after the failed attempt");

        reset(testResultPersistService);
        final Instant before = Instant.now().minusSeconds(1);
        final StagePathTestResultResponse retried = postJunitOnly(runReference, sha, "junitOnly", FailedPublicationSelectionTest.PASSING_XML);
        assertEquals(201, retried.getResponseDetails().getHttpStatus());
        assertEquals(1, testResultCount(stagePath.getStage().getStageId()));
        assertTrue(runIsSuccess(stagePath.getRun().getRunId()), "passing retry promotes the run");
        assertFalse(jobLastRun(stagePath.getJob().getJobId()).isBefore(before));
        assertFalse(branchLastRun(stagePath.getBranch().getBranchId()).isBefore(before));
    }
}
