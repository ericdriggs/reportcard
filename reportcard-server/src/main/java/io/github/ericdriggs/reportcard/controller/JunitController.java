package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.model.StagePathTestResultResponse;
import io.github.ericdriggs.reportcard.controller.util.KarateTarGzUtil;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.lock.LockService;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateCucumberConverter;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateEnricher;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateNativeFeature;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateNativeReportParser;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.ericdriggs.reportcard.util.StringMapUtil.decode;

@Slf4j
@RestController
@RequestMapping("/v1/api/junit")
@SuppressWarnings("unused")
public class JunitController {

    public static String storageKeyPath = "/v1/api/storage/key";

    @Autowired
    public JunitController(StoragePersistService storagePersistService,
                           TestResultPersistService testResultPersistService,
                           S3Service s3Service,
                           LockService lockService) {
        this.storagePersistService = storagePersistService;
        this.testResultPersistService = testResultPersistService;
        this.lockService = lockService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;
    private final LockService lockService;

    private final TestResultPersistService testResultPersistService;
    private final S3Service s3Service;

    @Operation(summary = "Post junit/surefire xmls for specified job stage")
    @PostMapping(path = "tar.gz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<StagePathTestResultResponse> postJunitXml(
            @Parameter(description = "Companies have orgs.")
            @RequestParam("company")
            String company,

            @Parameter(description = "Orgs have repos.")
            @RequestParam("org")
            String org,

            @Parameter(description = "Repos have branches.")
            @RequestParam("repo")
            String repo,

            @Parameter(description = "Branches have jobs.")
            @RequestParam("branch")
            String branch,

            @Parameter(description = "Comma separated key=value. Order does not matter. Trailing commas ignored. Each combination of job_info is a different job. Jobs have runs. Default: null", example = "application=foo-app,pipeline=staging")
            @RequestParam(value = "jobInfo", required = false)
            String jobInfo,

            @Parameter(description = "Optional UUID for a run. Runs have stages. Will be generated if missing.")
            @RequestParam(value = "runReference", required = false)
            UUID runReference,

            @Parameter(description = "Sha for the run.")
            @RequestParam("sha")
            String sha,

            @Parameter(description = "Stage name.")
            @RequestParam("stage")
            String stage,

            @Parameter(description = "Optional comma separated key=value links for the stage.", example = "build=https://jenkins.mycorp.com/job/myorg/job/myrepo/job/main/123")
            @RequestParam(value = "externalLinks", required = false)
            String externalLinks,

            @Parameter(description = "Junit and/or surefire xml files in the root of a .tar.gz file. " +
                                     "Used to generate a single test result. Test results contain test suites. Test suites contain test cases.")
            @RequestPart("junit.tar.gz")
            MultipartFile junitXmls
    ) {

        StageDetails stageDetails = StageDetails.builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .sha(sha)
                .stage(stage)
                .jobInfo(decode(StringMapUtil.stringToMap(jobInfo)))
                .runReference(runReference)
                .externalLinks(StringMapUtil.stringToMap(externalLinks))
                .build();
        try {
            List<String> testXmlContents = TestXmlTarGzUtil.getFileContentsFromTarGz(junitXmls);

            final StagePathTestResult stagePathTestResult = doPostJunitXml(stageDetails, testXmlContents);
            final StagePathTestResultResponse stagePathTestResultResponse = StagePathTestResultResponse.created(stagePathTestResult);
            return new ResponseEntity<>(stagePathTestResultResponse, HttpStatus.valueOf(stagePathTestResultResponse.getResponseDetails().getHttpStatus()));
        } catch (Exception ex) {
            log.error("postJunitXml - stageDetails: {}", stageDetails, ex);
            return StagePathTestResultResponse.fromException(ex).toResponseEntity();
        }
    }

    public StagePathTestResult doPostJunitXml(StageDetails stageDetails, List<String> testXmlContents) {
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);

        return testResultPersistService.insertTestResult(stageDetails, testResultModel);
    }

    @Operation(summary = "Post storage (usually html) and junit/surefire xmls for specified job stage.", description = "Single call which performs both /v1/api/junit/tar.gz and /v1/api/storage/stage/{stageId}/reports/{label}/tar.gz")
    @PostMapping(value = {"storage/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StagePathStorageResultCountResponse> postStageJunitStorageTarGZ(

            @Parameter(description = "Companies have orgs.")
            @RequestParam("company")
            String company,

            @Parameter(description = "Orgs have repos.")
            @RequestParam("org")
            String org,

            @Parameter(description = "Repos have branches.")
            @RequestParam("repo")
            String repo,

            @Parameter(description = "Branches have jobs.")
            @RequestParam("branch")
            String branch,

            @Parameter(description = "Comma separated key=value. Order does not matter. Trailing commas ignored. Each combination of job_info is a different job. Jobs have runs. Default: null", example = "application=foo-app,pipeline=staging")
            @RequestParam(value = "jobInfo", required = false)
            String jobInfo,

            @Parameter(description = "Optional UUID for a run. Runs have stages. Default: generated UUID")
            @RequestParam(value = "runReference", required = false)
            UUID runReference,

            @Parameter(description = "Sha for the run.")
            @RequestParam("sha")
            String sha,

            @Parameter(description = "Stage name.")
            @RequestParam("stage")
            String stage,

            @Parameter(description = "Label for storage html. Labels are unique per stage.")
            @PathVariable("label")
            String label,

            @Parameter(description = "Index file for html storage. Default: null")
            @RequestParam(value = "indexFile", required = false)
            String indexFile,

            @Parameter(description = "Optional comma separated key=value links for the stage.", example = "build=https://jenkins.mycorp.com/job/myorg/job/myrepo/job/main/123")
            @RequestParam(value = "externalLinks", required = false)
            String externalLinks,

            @Parameter(description = "Junit and/or surefire xml files. Required if karate.tar.gz not provided.")
            @RequestPart(value = "junit.tar.gz", required = false)
            MultipartFile junitXmls,

            @Parameter(description = "Karate test reports tar.gz containing karate-summary-json.txt for timing data.")
            @RequestPart(value = "karate.tar.gz", required = false)
            MultipartFile karateTarGz,

            @Parameter(description = "Files and folders to store in s3. Usually combination of html/css/js.")
            @RequestPart("storage.tar.gz")
            MultipartFile reports
    ) {
        StageDetails stageDetails = StageDetails.builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .sha(sha)
                .stage(stage)
                .jobInfo(decode(StringMapUtil.stringToMap(jobInfo)))
                .runReference(runReference)
                .externalLinks(StringMapUtil.stringToMap(externalLinks))
                .build();

        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .label(label)
                .indexFile(indexFile)
                .junitXmls(junitXmls)
                .karateTarGz(karateTarGz)
                .reports(reports)
                .build();
        try {
            log.info("postStageJunitStorageTarGZ request:  {}", req);
            StagePathStorageResultCountResponse response = lockService.criticalSectionCallable(this::doPostStageJunitStorageTarGZ, req, req.getStageDetails().getRunReference());
            log.info("postStageJunitStorageTarGZ response:  {}", response);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
        } catch (Exception ex) {
            log.error("postJunitXml - stageDetails: {}, label: {}", req.getStageDetails(), req.getLabel(), ex);
            return StagePathStorageResultCountResponse.fromException(ex).toResponseEntity();
        }
    }

    static void runPhaseNonBlocking(String phaseDescription, Runnable phase) {
        try {
            phase.run();
        } catch (Exception e) {
            log.warn("Failed to {}", phaseDescription, e);
        }
    }

    public StagePathStorageResultCountResponse doPostStageJunitStorageTarGZ(JunitHtmlPostRequest req) {
        // Validate at least one test result source
        boolean hasJunit = req.getJunitXmls() != null && !req.getJunitXmls().isEmpty();
        boolean hasKarate = req.getKarateTarGz() != null && !req.getKarateTarGz().isEmpty();

        if (!hasJunit && !hasKarate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "At least one of junit.tar.gz or karate.tar.gz must be provided");
        }

        final StagePath stagePath = testResultPersistService.getUpsertedStagePath(req.getStageDetails());
        final List<PublicationArchive> archives = publicationArchives(req, hasJunit, hasKarate);
        final List<StoragePojo> storages = getOrInsertStorages(stagePath, archives);
        uploadIncompleteArchives(stagePath, archives, storages);

        // JUnit is primary source for test structure (reliable per-stage)
        // Karate provides tags when available (merged in)
        TestResultModel testResultModel;
        List<String> allTags = null;

        if (hasJunit) {
            List<String> testXmlContents = TestXmlTarGzUtil.getFileContentsFromTarGz(req.getJunitXmls());
            testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);
        } else {
            // Karate-only upload (no JUnit XML)
            testResultModel = new TestResultModel();
            testResultModel.setTestSuites(new ArrayList<>());
        }

        // Merge suites, tags, and timestamps from Karate JSON when available. Cucumber-tag enrichment
        // and native-timestamp enrichment are independent phases — one throwing must never prevent
        // the other from running (see hard-400 check below for karate-only).
        if (hasKarate) {
            List<TestSuiteModel> karateSuites = new ArrayList<>();
            List<String> allTagsResult = new ArrayList<>();
            allTags = allTagsResult;

            runPhaseNonBlocking("extract/parse Karate cucumber JSON, continuing without cucumber tags", () -> {
                String cucumberJson = KarateTarGzUtil.extractCucumberJson(req.getKarateTarGz());
                if (cucumberJson != null && !cucumberJson.isBlank()) {
                    List<TestSuiteModel> karateSuitesResult = KarateEnricher.enrichTags(testResultModel, cucumberJson);
                    karateSuites.addAll(karateSuitesResult);
                    allTagsResult.addAll(KarateCucumberConverter.collectAllTags(karateSuites));
                    log.info("Extracted {} tags from Karate JSON", allTagsResult.size());

                    // JUnit is the source of record for test structure when present.
                    // Karate suites only become the structure for karate-only uploads.
                    if (!hasJunit && !karateSuites.isEmpty()) {
                        testResultModel.setTestSuites(karateSuites);
                        testResultModel.updateTotalsFromTestSuites();
                        log.info("Using {} Karate suites for test_suites_json", karateSuites.size());
                    }
                }
            });

            runPhaseNonBlocking("extract/parse Karate native JSON, continuing without timestamp enrichment", () -> {
                List<String> nativeJsons = KarateTarGzUtil.extractKarateNativeJsons(req.getKarateTarGz());
                List<KarateNativeFeature> rawFeatures = new ArrayList<>();
                for (String nativeJson : nativeJsons) {
                    KarateNativeFeature feature = KarateNativeReportParser.parse(nativeJson);
                    if (feature != null) {
                        rawFeatures.add(feature);
                    }
                }

                if (!rawFeatures.isEmpty()) {
                    List<TestSuiteModel> nativeSuites = KarateEnricher.enrichTimestamps(testResultModel, rawFeatures);

                    // Stage timing derives from ALL parsed native scenarios (via the adapted suites), matched or not.
                    Instant stageStart = null;
                    Instant stageEnd = null;
                    for (TestSuiteModel nativeSuite : nativeSuites) {
                        for (TestCaseModel nativeCase : nativeSuite.getTestCases()) {
                            if (nativeCase.getStartTime() != null) {
                                if (stageStart == null || nativeCase.getStartTime().isBefore(stageStart)) {
                                    stageStart = nativeCase.getStartTime();
                                }
                            }
                            if (nativeCase.getEndTime() != null) {
                                if (stageEnd == null || nativeCase.getEndTime().isAfter(stageEnd)) {
                                    stageEnd = nativeCase.getEndTime();
                                }
                            }
                        }
                    }
                    testResultModel.setStartTime(stageStart);
                    testResultModel.setEndTime(stageEnd);
                }
            });
        }

        if (!hasJunit && testResultModel.getTestSuites().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "karate.tar.gz could not be parsed and no junit.tar.gz was provided");
        }

        // Insert test result (tags passed to persistence layer for future storage)
        StagePathTestResult stagePathTestResult = testResultPersistService.insertTestResult(
                stagePath, testResultModel, allTags);

        StagePathStorageResultCount stagePathStorageResultCount =
            new StagePathStorageResultCount(stagePath, storages, stagePathTestResult);
        return StagePathStorageResultCountResponse.created(stagePathStorageResultCount);
    }

    record PublicationArchive(String label, String indexFile, StorageType storageType, boolean expand, MultipartFile tarGz) {}

    static List<PublicationArchive> publicationArchives(JunitHtmlPostRequest req, boolean hasJunit, boolean hasKarate) {
        List<PublicationArchive> archives = new ArrayList<>();
        if (hasJunit) {
            archives.add(new PublicationArchive("junit", "junit.tar.gz", StorageType.JUNIT, false, req.getJunitXmls()));
        }
        if (hasKarate) {
            archives.add(new PublicationArchive("karate", "karate.tar.gz", StorageType.KARATE_JSON, false, req.getKarateTarGz()));
        }
        final MultipartFile reports = req.getReports();
        if (reports != null && !reports.isEmpty()) {
            // Store original tar.gz for cucumber_html before expanding; form field name becomes filename in S3
            if ("cucumber_html".equals(req.getLabel())) {
                archives.add(new PublicationArchive(StorageType.TAR_GZ.toLabel("cucumber_html"), reports.getName(), StorageType.TAR_GZ, false, reports));
            }
            archives.add(new PublicationArchive(req.getLabel(), req.getIndexFile(), StorageType.HTML, true, reports));
        }
        return archives;
    }

    List<StoragePojo> getOrInsertStorages(StagePath stagePath, List<PublicationArchive> archives) {
        List<StoragePojo> storages = new ArrayList<>();
        for (PublicationArchive archive : archives) {
            storages.add(storagePersistService.getOrInsertStorage(stagePath, archive.indexFile(), archive.label(), archive.storageType()));
        }
        return storages;
    }

    void uploadIncompleteArchives(StagePath stagePath, List<PublicationArchive> archives, List<StoragePojo> storages) {
        final Long stageId = stagePath.getStage().getStageId();
        for (int i = 0; i < archives.size(); i++) {
            final PublicationArchive archive = archives.get(i);
            final StoragePojo storage = storages.get(i);
            if (!storage.getIsUploadComplete()) {
                s3Service.uploadTarGz(storage.getPrefix(), archive.expand(), archive.tarGz());
                storagePersistService.setUploadCompleted(storage.getIndexFile(), storage.getLabel(), storage.getPrefix(), stageId);
                storage.setIsUploadComplete(true);
            }
        }
    }

}