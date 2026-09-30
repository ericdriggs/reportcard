package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.cache.model.BranchStageViewResponse;
import io.github.ericdriggs.reportcard.cache.model.JobRun;
import io.github.ericdriggs.reportcard.cache.model.StaticBrowseService;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.JunitControllerTest;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StageTestResultPojo;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.model.branch.BranchJobLatestRunMap;
import io.github.ericdriggs.reportcard.model.branch.RunStorageTestResult;
import io.github.ericdriggs.reportcard.model.graph.BranchGraph;
import io.github.ericdriggs.reportcard.model.graph.JobGraph;
import io.github.ericdriggs.reportcard.model.graph.RepoGraph;
import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import io.github.ericdriggs.reportcard.model.graph.StageGraph;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardRequest;
import io.github.ericdriggs.reportcard.model.publication.FailedPublication;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PublishedVisibilityTest {

    static final String PUBLISHED_BRANCH = "published";
    static final String HIERARCHY_BRANCH = "hierarchyonly";

    @Autowired
    StoragePersistService storagePersistService;

    @Autowired
    TestResultPersistService testResultPersistService;

    @Autowired
    BrowseService browseService;

    @Autowired
    GraphService graphService;

    @Autowired
    FailedPublicationService failedPublicationService;

    @Autowired
    S3Service s3Service;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DSLContext dsl;

    record Fixture(String company, String sha1, String sha2,
                   StagePath run1Api, StagePath run1Ui, StoragePojo run1UiJunit,
                   StagePath run2Api, StagePath run3Api, Instant job1LastRunAfterPublish) {
        Long run1() { return run1Api.getRun().getRunId(); }
        Long run2() { return run2Api.getRun().getRunId(); }
        Long job1() { return run1Api.getJob().getJobId(); }
        Long job2() { return run3Api.getJob().getJobId(); }
    }

    static StageDetails details(String company, String branch, TreeMap<String, String> jobInfo,
                                UUID runReference, String sha, String stage) {
        return StageDetails.builder()
                .company(company).org(TestData.org).repo(TestData.repo).branch(branch)
                .jobInfo(jobInfo).runReference(runReference).sha(sha).stage(stage)
                .build();
    }

    StagePath hierarchyOnlyStage(StageDetails stageDetails) {
        final StagePath stagePath = storagePersistService.getUpsertedStagePath(stageDetails);
        storagePersistService.upsertStoragePath("junit.tar.gz", "junit",
                new StoragePath(stagePath, "junit").getPrefix(), stagePath.getStage().getStageId(), StorageType.JUNIT);
        return stagePath;
    }

    StoragePojo junitStorage(Long stageId) {
        return dsl.selectFrom(STORAGE).where(STORAGE.STAGE_FK.eq(stageId).and(STORAGE.LABEL.eq("junit")))
                .fetchOneInto(StoragePojo.class);
    }

    Fixture fixture() throws IOException {
        final String company = "visibility-" + UUID.randomUUID().toString().substring(0, 8);
        final TreeMap<String, String> job2Info = new TreeMap<>(Map.of("application", "hierarchyonlyapp", "pipeline", "hierarchyonly"));
        final String sha1 = UUID.randomUUID().toString().substring(0, 12);
        final String sha2 = UUID.randomUUID().toString().substring(0, 12);
        final UUID run1Ref = UUID.randomUUID();

        testResultPersistService.doPostXmlString(details(company, PUBLISHED_BRANCH, TestData.jobInfo, run1Ref, sha1, "api"),
                FailedPublicationSelectionTest.PASSING_XML);
        final StagePath run1Api = storagePersistService.getStagePath(details(company, PUBLISHED_BRANCH, TestData.jobInfo, run1Ref, sha1, "api"));
        final Instant job1LastRun = dsl.select(JOB.LAST_RUN).from(JOB).where(JOB.JOB_ID.eq(run1Api.getJob().getJobId())).fetchOne(JOB.LAST_RUN);

        final StagePath run1Ui = hierarchyOnlyStage(details(company, PUBLISHED_BRANCH, TestData.jobInfo, run1Ref, sha1, "ui"));
        final StoragePojo uiJunit = junitStorage(run1Ui.getStage().getStageId());
        s3Service.uploadTarGz(uiJunit.getPrefix(), false, JunitControllerTest.getJunitTarGz(resourceReader));
        storagePersistService.setUploadCompleted(uiJunit.getIndexFile(), uiJunit.getLabel(), uiJunit.getPrefix(), run1Ui.getStage().getStageId());

        final StagePath run2Api = hierarchyOnlyStage(details(company, PUBLISHED_BRANCH, TestData.jobInfo, UUID.randomUUID(), sha2, "api"));
        final StagePath run3Api = hierarchyOnlyStage(details(company, HIERARCHY_BRANCH, job2Info, UUID.randomUUID(), sha1, "api"));
        return new Fixture(company, sha1, sha2, run1Api, run1Ui, junitStorage(run1Ui.getStage().getStageId()),
                run2Api, run3Api, job1LastRun);
    }

    static Set<Long> runIds(BranchStageViewResponse response) {
        return response.getJobRun_StageTestResult_StoragesMap().keySet().stream()
                .map(JobRun::getRun).map(RunPojo::getRunId).collect(Collectors.toSet());
    }

    static void assertEveryStageHasTestResult(BranchStageViewResponse response) {
        for (Map<StageTestResultPojo, Set<StoragePojo>> stages : response.getJobRun_StageTestResult_StoragesMap().values()) {
            for (StageTestResultPojo stage : stages.keySet()) {
                assertNotNull(stage.getTestResultPojo(), "stage without test result shown: " + stage.getStage().getStageName());
            }
        }
    }

    static Set<String> stageNames(BranchStageViewResponse response) {
        return response.getJobRun_StageTestResult_StoragesMap().values().stream()
                .flatMap(stages -> stages.keySet().stream())
                .map(stage -> stage.getStage().getStageName())
                .collect(Collectors.toSet());
    }

    /**
     * Given a published run with a hierarchy-only stage, a newer hierarchy-only run, and a hierarchy-only job,
     * When branch, job, job-info, and job-run browse history is read,
     * Expected only run 1 and its api stage appear (the hierarchy-only run and stage do not consume history
     * positions), the retained ui archive is still served by direct storage access, and every hierarchy-only stage
     * is reported by failed-publication selection.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: normal browse history excludes stages without test results while retained artifacts stay reachable
     */
    @Test
    void whenBrowsingHistory_expectOnlyStagesWithTestResults() throws IOException {
        final Fixture f = fixture();
        final String c = f.company();

        final BranchStageViewResponse latestOne = browseService.getStageViewForBranch(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, 1);
        assertEquals(Set.of(f.run1()), runIds(latestOne));
        assertEquals(Set.of("api"), stageNames(latestOne));
        assertEveryStageHasTestResult(latestOne);

        final BranchStageViewResponse branchHistory = browseService.getStageViewForBranch(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, 10);
        assertEquals(Set.of(f.run1()), runIds(branchHistory));
        assertEquals(Set.of("api"), stageNames(branchHistory));
        assertEveryStageHasTestResult(branchHistory);

        final BranchStageViewResponse jobHistory = browseService.getStageViewForJob(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, f.job1(), 10);
        assertEquals(Set.of(f.run1()), runIds(jobHistory));
        assertEquals(Set.of("api"), stageNames(jobHistory));

        final BranchStageViewResponse jobInfoHistory = browseService.getStageViewForJobInfo(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, TestData.jobInfo);
        assertEquals(Set.of(f.run1()), runIds(jobInfoHistory));
        assertEquals(Set.of("api"), stageNames(jobInfoHistory));

        final Map<RunPojo, Set<StagePojo>> jobRunsStages = browseService.getJobRunsStages(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, f.job1())
                .values().iterator().next();
        assertEquals(Set.of(f.run1()), jobRunsStages.keySet().stream().map(RunPojo::getRunId).collect(Collectors.toSet()));
        assertEquals(Set.of("api"), jobRunsStages.values().stream().flatMap(Set::stream).map(StagePojo::getStageName).collect(Collectors.toSet()));

        final Map<JobPojo, Set<RunPojo>> branchJobsRuns = browseService.getBranchJobsRuns(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, Collections.emptyMap())
                .values().iterator().next();
        assertEquals(Set.of(f.run1()), branchJobsRuns.values().stream().flatMap(Set::stream).map(RunPojo::getRunId)
                .filter(Objects::nonNull).collect(Collectors.toSet()));

        assertEquals(200, restTemplate.getForEntity(BrowseHtmlHelper.getStorageKey(f.run1UiJunit()), byte[].class).getStatusCodeValue(),
                "retained archive of a hierarchy-only stage remains directly accessible");

        final Instant now = Instant.now().plus(Duration.ofMinutes(2));
        final Set<Long> reported = failedPublicationService.getFailedPublications(1, 1, 200, now).getFailedPublications().stream()
                .map(FailedPublication::getStageId).collect(Collectors.toSet());
        assertTrue(reported.containsAll(Set.of(f.run1Ui().getStage().getStageId(), f.run2Api().getStage().getStageId(),
                f.run3Api().getStage().getStageId())), "hierarchy-only stages remain in failed-publication reporting");
        assertFalse(reported.contains(f.run1Api().getStage().getStageId()));
    }

    /**
     * Given the same fixture,
     * When latest-run, latest-run-id, run, and SHA views are read,
     * Expected the latest run per stage is run 1 with only its api stage, the latest run id is run 1, the run view of
     * run 1 omits the ui stage, sha 1 lists only run 1, and sha 2 (hierarchy-only run 2) lists no runs.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: latest-run and SHA views exclude runs and stages without test results
     */
    @Test
    void whenViewingLatestRunAndSha_expectPublishedRunsAndStagesOnly() throws IOException {
        final Fixture f = fixture();
        final String c = f.company();

        final BranchJobLatestRunMap latest = graphService.getBranchJobLatestRunMap(c, TestData.org, TestData.repo, PUBLISHED_BRANCH);
        assertEquals(1, latest.getJobStageLatestMap().size());
        final TreeMap<String, RunStorageTestResult> stageLatest = latest.getJobStageLatestMap().firstEntry().getValue();
        assertEquals(Set.of("api"), stageLatest.keySet());
        assertEquals(f.run1(), stageLatest.get("api").getRunPojo().getRunId());

        assertEquals(f.run1(), browseService.getLatestRunId(f.job1()));

        final BranchStageViewResponse runView = graphService.getRunBranchStageViewResponse(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, f.job1(), f.run1());
        assertEquals(Set.of("api"), stageNames(runView));

        final Set<Long> sha1Runs = browseService.getBranchJobsRunsForSha(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, f.sha1())
                .values().stream().flatMap(jobs -> jobs.values().stream()).flatMap(Set::stream)
                .map(RunPojo::getRunId).collect(Collectors.toSet());
        assertEquals(Set.of(f.run1()), sha1Runs);

        final Set<Long> sha2Runs = browseService.getBranchJobsRunsForSha(c, TestData.org, TestData.repo, PUBLISHED_BRANCH, f.sha2())
                .values().stream().flatMap(jobs -> jobs.values().stream()).flatMap(Set::stream)
                .map(RunPojo::getRunId).collect(Collectors.toSet());
        assertEquals(Set.of(), sha2Runs);
    }

    /**
     * Given the same fixture,
     * When the org dashboard, pipeline dashboard, and job/branch recency are read,
     * Expected dashboards show only run 1 with stages that have test results and omit job 2; the pipeline metrics
     * count one run with 100% pass rate and omit job 2; job 1 last_run is unchanged by hierarchy-only run 2; and
     * job 2 and its branch have no last_run.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: dashboard graphs, recency, and success metrics exclude runs and stages without test results
     */
    @Test
    void whenViewingDashboardsRecencyAndSuccess_expectPublishedRunsOnly() throws IOException {
        final Fixture f = fixture();
        final String c = f.company();

        final OrgDashboard orgDashboard = graphService.getOrgDashboard(c, TestData.org, null,
                new ArrayList<>(List.of(PUBLISHED_BRANCH, HIERARCHY_BRANCH)), false, 30);
        assertPublishedOnly(orgDashboard.getRepoGraphs(), f);

        final List<JobDashboardMetrics> pipeline = graphService.getPipelineDashboard(
                JobDashboardRequest.builder().company(c).days(30).build());
        assertEquals(1, pipeline.size(), "hierarchy-only job must be omitted: " + pipeline);
        final JobDashboardMetrics job1 = pipeline.get(0);
        assertEquals(PUBLISHED_BRANCH, job1.getBranch());
        assertEquals(1, job1.getTotalRuns());
        assertEquals(1, job1.getPassingRuns());
        assertEquals(0, job1.getJobPassPercent().compareTo(BigDecimal.valueOf(100)));

        assertEquals(f.job1LastRunAfterPublish(),
                dsl.select(JOB.LAST_RUN).from(JOB).where(JOB.JOB_ID.eq(f.job1())).fetchOne(JOB.LAST_RUN));
        assertNull(dsl.select(JOB.LAST_RUN).from(JOB).where(JOB.JOB_ID.eq(f.job2())).fetchOne(JOB.LAST_RUN));
        assertNull(dsl.select(BRANCH.LAST_RUN).from(BRANCH)
                .where(BRANCH.BRANCH_ID.eq(f.run3Api().getBranch().getBranchId())).fetchOne(BRANCH.LAST_RUN));
    }

    /**
     * Given a set that iterates a non-null Instant before a null element (as a NULL branch or job last_run does),
     * When BrowseHtmlHelper.mostRecent is called,
     * Expected the null element is ignored and the latest non-null Instant is returned; a set of only nulls yields null.
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: browse date aggregation ignores missing last_run values
     */
    @Test
    void whenMostRecentSeesNullAfterValue_expectLatestNonNullInstant() {
        final Instant older = Instant.parse("2026-01-01T00:00:00Z");
        final Instant newer = Instant.parse("2026-01-02T00:00:00Z");
        final Set<Instant> valueThenNull = new LinkedHashSet<>();
        valueThenNull.add(older);
        valueThenNull.add(null);
        valueThenNull.add(newer);
        assertEquals(newer, BrowseHtmlHelper.mostRecent(valueThenNull));

        final Set<Instant> onlyNull = new LinkedHashSet<>();
        onlyNull.add(null);
        assertNull(BrowseHtmlHelper.mostRecent(onlyNull));
    }

    /**
     * Given one repo with a published branch and job and a hierarchy-only branch and job whose last_run is NULL
     * because their only run has no test result,
     * When the company, org, repo, both branch, and the hierarchy-only job HTML browse pages are requested,
     * Expected every page returns 200 (no 5xx), and the published repo, branch, and job appear on their parent pages
     * (the company page lists orgs from a JVM-wide cache that may predate this fixture, so only its status is checked).
     * Ticket: reportcard_store-s3-before-test-persistence · Behavior: browse pages render when branches and jobs have no published runs or a NULL last_run
     */
    @Test
    void whenBrowsingEntitiesWithoutPublishedRuns_expectPagesRender() throws IOException {
        final Fixture f = fixture();
        assertNull(dsl.select(JOB.LAST_RUN).from(JOB).where(JOB.JOB_ID.eq(f.job2())).fetchOne(JOB.LAST_RUN),
                "precondition: hierarchy-only job has NULL last_run");
        assertNull(dsl.select(BRANCH.LAST_RUN).from(BRANCH)
                        .where(BRANCH.BRANCH_ID.eq(f.run3Api().getBranch().getBranchId())).fetchOne(BRANCH.LAST_RUN),
                "precondition: hierarchy-only branch has NULL last_run");

        final String company = "/company/" + f.company();
        final String org = company + "/org/" + TestData.org;
        final String repo = org + "/repo/" + TestData.repo;
        final String publishedBranch = repo + "/branch/" + PUBLISHED_BRANCH;
        final String hierarchyBranch = repo + "/branch/" + HIERARCHY_BRANCH;
        final String hierarchyJob = hierarchyBranch + "/job/" + f.job2();

        // HTML caches read through a JVM-wide static that the most recently created Spring context owns
        final Object previousBrowseService = ReflectionTestUtils.getField(StaticBrowseService.class, "INSTANCE");
        new StaticBrowseService().setReportCardService(browseService);
        try {
            renderedHtml(company);
            assertTrue(renderedHtml(org).contains(TestData.repo), "org page lists the repo");
            assertTrue(renderedHtml(repo).contains(PUBLISHED_BRANCH), "repo page lists the published branch");
            assertTrue(renderedHtml(publishedBranch).contains("/job/" + f.job1()), "published branch page links job 1");
            renderedHtml(hierarchyBranch);
            renderedHtml(hierarchyJob);
        } finally {
            ReflectionTestUtils.setField(StaticBrowseService.class, "INSTANCE", previousBrowseService);
        }
    }

    String renderedHtml(String path) {
        final ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
        assertEquals(200, response.getStatusCodeValue(), path + " -> " + response.getBody());
        assertNotNull(response.getBody());
        return response.getBody();
    }

    static void assertPublishedOnly(List<RepoGraph> repoGraphs, Fixture f) {
        final List<JobGraph> jobs = repoGraphs.stream()
                .flatMap(repo -> repo.branches().stream())
                .filter(branch -> branch.jobs() != null)
                .flatMap(branch -> branch.jobs().stream())
                .filter(job -> job.runs() != null && !job.runs().isEmpty())
                .toList();
        assertEquals(List.of(f.job1()), jobs.stream().map(JobGraph::jobId).toList(), "only the published job has runs");
        final List<RunGraph> runs = jobs.get(0).runs();
        assertEquals(Set.of(f.run1()), runs.stream().map(RunGraph::runId).collect(Collectors.toSet()));
        for (RunGraph run : runs) {
            for (StageGraph stage : run.stages()) {
                assertFalse(stage.testResults() == null || stage.testResults().isEmpty(),
                        "dashboard stage without test result: " + stage.stageName());
            }
        }
        final Set<String> branchesWithRuns = repoGraphs.stream().flatMap(repo -> repo.branches().stream())
                .filter(branch -> branch.jobs() != null && branch.jobs().stream().anyMatch(j -> j.runs() != null && !j.runs().isEmpty()))
                .map(BranchGraph::branchName).collect(Collectors.toSet());
        assertEquals(Set.of(PUBLISHED_BRANCH), branchesWithRuns);
    }
}
