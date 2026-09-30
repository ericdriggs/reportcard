package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.model.publication.FailedPublication;
import io.github.ericdriggs.reportcard.model.publication.FailedPublicationsResponse;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.github.ericdriggs.reportcard.gen.db.Tables.RUN;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class FailedPublicationSelectionTest {

    static final Instant NOW = Instant.parse("2101-01-01T00:00:00Z");
    static final Instant DATE_CUTOFF = NOW.minus(Duration.ofDays(1));
    static final Instant GRACE_CUTOFF = NOW.minus(Duration.ofMinutes(10));

    public static final String PASSING_XML = """
            <testsuite name="publication" tests="1" failures="0" errors="0" skipped="0" time="0.1">
              <testcase name="passes" classname="publication.Suite" time="0.1"/>
            </testsuite>
            """;

    @Autowired
    FailedPublicationService failedPublicationService;

    @Autowired
    StoragePersistService storagePersistService;

    @Autowired
    TestResultPersistService testResultPersistService;

    @Autowired
    DSLContext dsl;

    final String company = "fpsel-" + UUID.randomUUID().toString().substring(0, 8);

    public static StageDetails stageDetails(String company, String stageName) {
        return StageDetails.builder()
                .company(company)
                .org(TestData.org)
                .repo(TestData.repo)
                .branch(TestData.branch)
                .sha(UUID.randomUUID().toString().substring(0, 12))
                .jobInfo(TestData.jobInfo)
                .runReference(UUID.randomUUID())
                .stage(stageName)
                .build();
    }

    /** Creates a new run with one stage dated runDate, the given storage labels, and optionally a passing test result. */
    public static StagePath seedStage(StoragePersistService storagePersistService,
                                      TestResultPersistService testResultPersistService,
                                      DSLContext dsl,
                                      String company, String stageName, Instant runDate,
                                      List<String> labels, boolean withTestResult) {
        final StagePath stagePath = storagePersistService.getUpsertedStagePath(stageDetails(company, stageName));
        final Long stageId = stagePath.getStage().getStageId();
        for (String label : labels) {
            storagePersistService.upsertStoragePath(label + ".tar.gz", label,
                    new StoragePath(stagePath, label).getPrefix(), stageId, StorageType.TAR_GZ);
        }
        if (withTestResult) {
            testResultPersistService.doPostXmlString(stagePath.getRun().getRunId(), stageName, PASSING_XML);
        }
        dsl.update(RUN).set(RUN.RUN_DATE, runDate).where(RUN.RUN_ID.eq(stagePath.getRun().getRunId())).execute();
        return stagePath;
    }

    StagePath seed(String stageName, Instant runDate, List<String> labels, boolean withTestResult) {
        return seedStage(storagePersistService, testResultPersistService, dsl, company, stageName, runDate, labels, withTestResult);
    }

    static Long stageId(StagePath stagePath) {
        return stagePath.getStage().getStageId();
    }

    /**
     * Given runs dated dateCutoff-3d, dateCutoff-2d, dateCutoff-12h, and exactly dateCutoff-1d, created in that order,
     * When the boundary is computed for dateCutoff,
     * Expected boundaryRunId is the run dated dateCutoff-2d: the first run in descending run_id order whose run_date
     * is strictly earlier than dateCutoff minus one day.
     */
    @Test
    void whenOldRunsPrecedeTheWindow_expectBoundaryIsNewestRunEarlierThanDateCutoffMinusOneDay() {
        seed("boundaryA", DATE_CUTOFF.minus(Duration.ofDays(3)), List.of("junit"), false);
        final StagePath b = seed("boundaryB", DATE_CUTOFF.minus(Duration.ofDays(2)), List.of("junit"), false);
        seed("boundaryC", DATE_CUTOFF.minus(Duration.ofHours(12)), List.of("junit"), false);
        seed("boundaryD", DATE_CUTOFF.minus(Duration.ofDays(1)), List.of("junit"), false);

        assertEquals(b.getRun().getRunId(), failedPublicationService.findBoundaryRunId(DATE_CUTOFF));
    }

    /**
     * Given, in creation order, an in-window stage before the boundary run, the boundary run, and stages dated
     * dateCutoff-1s, exactly dateCutoff, exactly graceCutoff, graceCutoff+1s, in-window with a test result,
     * in-window without storage, and in-window with storage,
     * When candidates are selected for days=1 and olderThanMinutes=10 at now,
     * Expected only the dateCutoff, graceCutoff, and in-window-with-storage stages are returned, newest stage first,
     * both from findCandidateStageIds and from getFailedPublications, which echoes the derived cutoffs.
     */
    @Test
    void whenSelectingCandidates_expectBoundaryExactDateStorageAndTestResultPredicates() {
        final StagePath beforeBoundary = seed("beforeBoundary", NOW.minus(Duration.ofHours(1)), List.of("junit"), false);
        final StagePath boundary = seed("boundary", DATE_CUTOFF.minus(Duration.ofDays(2)), List.of("junit"), false);
        final StagePath beforeDateCutoff = seed("beforeDateCutoff", DATE_CUTOFF.minusSeconds(1), List.of("junit"), false);
        final StagePath atDateCutoff = seed("atDateCutoff", DATE_CUTOFF, List.of("junit"), false);
        final StagePath atGraceCutoff = seed("atGraceCutoff", GRACE_CUTOFF, List.of("junit"), false);
        final StagePath insideGrace = seed("insideGrace", GRACE_CUTOFF.plusSeconds(1), List.of("junit"), false);
        final StagePath published = seed("published", NOW.minus(Duration.ofMinutes(30)), List.of("junit"), true);
        final StagePath noStorage = seed("noStorage", NOW.minus(Duration.ofMinutes(30)), List.of(), false);
        final StagePath suspected = seed("suspected", NOW.minus(Duration.ofMinutes(30)), List.of("junit", "html"), false);

        final long boundaryRunId = failedPublicationService.findBoundaryRunId(DATE_CUTOFF);
        assertEquals(boundary.getRun().getRunId(), boundaryRunId);
        assertTrue(beforeBoundary.getRun().getRunId() < boundaryRunId);

        final List<Long> expected = List.of(stageId(suspected), stageId(atGraceCutoff), stageId(atDateCutoff));
        assertEquals(expected, failedPublicationService.findCandidateStageIds(boundaryRunId, DATE_CUTOFF, GRACE_CUTOFF, 200));

        final FailedPublicationsResponse response = failedPublicationService.getFailedPublications(1, 10, 200, NOW);
        assertEquals(DATE_CUTOFF, response.getDateCutoff());
        assertEquals(GRACE_CUTOFF, response.getGraceCutoff());
        assertEquals(expected, response.getFailedPublications().stream().map(FailedPublication::getStageId).toList());

        for (StagePath excluded : List.of(beforeBoundary, beforeDateCutoff, insideGrace, published, noStorage)) {
            assertFalse(response.getFailedPublications().stream().anyMatch(p -> p.getStageId().equals(stageId(excluded))),
                    excluded.getStage().getStageName() + " must be excluded");
        }
    }

    /**
     * Given a boundary run and two suspected stages where the newest stage has three storage rows,
     * When candidates are selected with limit 1 and limit 2,
     * Expected the limit counts stages (not storage rows): limit 1 returns only the newest stage with all three
     * labels, and limit 2 returns both stages newest first.
     */
    @Test
    void whenLimitIsSmallerThanCandidates_expectLimitAppliedToStagesBeforeStorageDetails() {
        final Instant now = NOW.plus(Duration.ofDays(10));
        final Instant dateCutoff = now.minus(Duration.ofDays(1));
        final Instant graceCutoff = now.minus(Duration.ofMinutes(10));
        seed("limitBoundary", dateCutoff.minus(Duration.ofDays(2)), List.of("junit"), false);
        final StagePath older = seed("limitOlder", now.minus(Duration.ofMinutes(40)), List.of("junit"), false);
        final StagePath newest = seed("limitNewest", now.minus(Duration.ofMinutes(30)), List.of("junit", "karate", "html"), false);

        final long boundaryRunId = failedPublicationService.findBoundaryRunId(dateCutoff);
        assertEquals(List.of(stageId(newest)), failedPublicationService.findCandidateStageIds(boundaryRunId, dateCutoff, graceCutoff, 1));
        assertEquals(List.of(stageId(newest), stageId(older)), failedPublicationService.findCandidateStageIds(boundaryRunId, dateCutoff, graceCutoff, 2));

        final FailedPublicationsResponse limited = failedPublicationService.getFailedPublications(1, 10, 1, now);
        assertEquals(1, limited.getFailedPublications().size());
        final FailedPublication row = limited.getFailedPublications().get(0);
        assertEquals(stageId(newest), row.getStageId());
        assertEquals(Set.of("junit", "karate", "html"), row.getStorages().keySet());
    }

    /**
     * Given a boundary run id, date cutoff, grace cutoff, and limit,
     * When the candidate stage-id query is rendered,
     * Expected it contains direct run_id, run_date lower-bound, and run_date upper-bound predicates, storage
     * existence and test-result absence subqueries, newest-stage ordering, and the limit, with no OR condition.
     */
    @Test
    void whenRenderingCandidateQuery_expectDirectPredicatesWithoutNullableOr() {
        final String sql = dsl.renderInlined(
                failedPublicationService.candidateStageIdsQuery(42L, DATE_CUTOFF, GRACE_CUTOFF, 7)).toLowerCase();

        assertFalse(sql.matches("(?s).*\\bor\\b.*"), "candidate query must not contain OR: " + sql);
        assertTrue(sql.matches("(?s).*`run_id`\\s*>\\s*42\\b.*"), sql);
        assertTrue(sql.matches("(?s).*`run_date`\\s*>=.*"), sql);
        assertTrue(sql.matches("(?s).*`run_date`\\s*<=.*"), sql);
        assertTrue(sql.matches("(?s).*\\bexists\\s*\\(.*`storage`.*"), sql);
        assertTrue(sql.matches("(?s).*\\bnot\\s+exists\\s*\\(.*`test_result`.*"), sql);
        assertTrue(sql.matches("(?s).*order by .*`stage_id`\\s+desc.*"), sql);
        assertTrue(sql.matches("(?s).*\\blimit\\s+7\\b.*"), sql);
    }

    /**
     * Given an empty stage-id list,
     * When getFailedPublications(List) is called directly,
     * Expected an empty list with no query run against the database.
     */
    @Test
    void whenStageIdsIsEmpty_expectEmptyListWithNoQuery() {
        assertEquals(List.of(), failedPublicationService.getFailedPublications(List.of()));
    }
}
