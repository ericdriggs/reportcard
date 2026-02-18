package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.model.StagePathTestResultResponse;
import io.github.ericdriggs.reportcard.controller.util.KarateTarGzUtil;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.lock.LockService;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateConvertersUtil;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateCucumberConverter;
import io.github.ericdriggs.reportcard.model.converter.karate.KarateSummary;
import io.github.ericdriggs.reportcard.persist.StagePathPersistService;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
                           StagePathPersistService stagePathPersistService,
                           S3Service s3Service,
                           LockService lockService) {
        this.storagePersistService = storagePersistService;
        this.testResultPersistService = testResultPersistService;
        this.stagePathPersistService = stagePathPersistService;
        this.lockService = lockService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;
    private final LockService lockService;
    private final StagePathPersistService stagePathPersistService;

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

    public StagePathStorageResultCountResponse doPostStageJunitStorageTarGZ(JunitHtmlPostRequest req) {
        // Validate at least one test result source
        boolean hasJunit = req.getJunitXmls() != null && !req.getJunitXmls().isEmpty();
        boolean hasKarate = req.getKarateTarGz() != null && !req.getKarateTarGz().isEmpty();

        if (!hasJunit && !hasKarate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "At least one of junit.tar.gz or karate.tar.gz must be provided");
        }

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

        // Merge suites and tags from Karate Cucumber JSON when available (graceful failure)
        if (hasKarate) {
            try {
                String cucumberJson = KarateTarGzUtil.extractCucumberJson(req.getKarateTarGz());
                if (cucumberJson != null && !cucumberJson.isBlank()) {
                    List<TestSuiteModel> karateSuites = KarateCucumberConverter.fromCucumberJson(cucumberJson);
                    allTags = KarateCucumberConverter.collectAllTags(karateSuites);
                    log.info("Extracted {} tags from Karate JSON", allTags.size());

                    // Use Karate suites for test_suites_json (they contain embedded tags)
                    // For Karate-only: use karateSuites directly
                    // For JUnit+Karate: prefer karateSuites (has tags) over JUnit (no tags)
                    if (!karateSuites.isEmpty()) {
                        testResultModel.setTestSuites(karateSuites);
                        testResultModel.updateTotalsFromTestSuites();
                        log.info("Using {} Karate suites for test_suites_json", karateSuites.size());
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to extract tags from Karate JSON, continuing without tags", e);
            }
        }

        // Insert test result (tags passed to persistence layer for future storage)
        StagePathTestResult stagePathTestResult = testResultPersistService.insertTestResult(
                req.getStageDetails(), testResultModel, allTags);
        StagePath stagePath = stagePathTestResult.getStagePath();
        final Long stageId = stagePath.getStage().getStageId();
        final Long testResultId = stagePathTestResult.getTestResult().getTestResultId();

        // Process Karate timing data
        if (hasKarate) {
            processKarateTiming(testResultId, req.getKarateTarGz());
        }

        // Store files in S3
        StagePathStorages stagePathStorages;
        {
            List<StagePathStorages> storagesList = new ArrayList<>();

            if (hasJunit) {
                storagesList.add(storeJunit(stageId, req.getJunitXmls()));
            }
            if (hasKarate) {
                storagesList.add(storeKarate(stageId, req.getKarateTarGz()));
            }
            if (req.getReports() != null && !req.getReports().isEmpty()) {
                storagesList.add(storeHtml(stageId, req.getLabel(), req.getReports(), req.getIndexFile()));
            }

            stagePathStorages = StagePathStorages.merge(storagesList.toArray(new StagePathStorages[0]));
        }

        StagePathStorageResultCount stagePathStorageResultCount =
            new StagePathStorageResultCount(stagePathStorages.getStagePath(), stagePathStorages.getStorages(), stagePathTestResult);
        return StagePathStorageResultCountResponse.created(stagePathStorageResultCount);
    }

    protected StagePathStorages storeHtml(
            Long stageId,
            String label,
            MultipartFile tarGz,
            String indexFile) {

        StorageType storageType = StorageType.HTML;

        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        StagePathStorages stagePathStorages = storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, storageType);
        if (!stagePathStorages.isComplete()) {
            s3Service.uploadTarGz(prefix, true, tarGz);
            storagePersistService.setUploadCompleted(indexFile, label, prefix, stageId);
            stagePathStorages.setComplete();
        }
        return stagePathStorages;
    }

    protected StagePathStorages storeJunit(
            Long stageId,
            MultipartFile tarGz) {

        final String label = "junit";
        StorageType storageType = StorageType.JUNIT;

        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        StagePathStorages stagePathStorages = storagePersistService.upsertStoragePath(null, label, prefix, stageId, storageType);
        if (!stagePathStorages.isComplete()) {
            s3Service.uploadTarGz(prefix, false, tarGz);
            storagePersistService.setUploadCompleted(null, label, prefix, stageId);
            stagePathStorages.setComplete();
        }

        return stagePathStorages;
    }

    /**
     * Processes Karate timing data and updates test_result record.
     */
    private void processKarateTiming(Long testResultId, MultipartFile karateTarGz) {
        if (karateTarGz == null || karateTarGz.isEmpty()) {
            return;
        }

        String summaryJson = KarateTarGzUtil.extractKarateSummaryJson(karateTarGz);
        if (summaryJson == null) {
            log.warn("karate-summary-json.txt not found in karate.tar.gz for testResultId: {}", testResultId);
            return;
        }

        KarateSummary summary = KarateConvertersUtil.parseKarateSummary(summaryJson);
        if (summary == null) {
            log.warn("Failed to parse karate-summary-json.txt for testResultId: {}", testResultId);
            return;
        }

        LocalDateTime endTime = KarateConvertersUtil.parseResultDate(summary.getResultDate());
        LocalDateTime startTime = KarateConvertersUtil.calculateStartTime(endTime, summary.getElapsedTime());

        Instant startInstant = toInstant(startTime);
        Instant endInstant = toInstant(endTime);

        stagePathPersistService.updateTestResultTiming(testResultId, startInstant, endInstant);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneOffset.UTC).toInstant();
    }

    /**
     * Stores Karate tar.gz in S3 with KARATE_JSON storage type.
     */
    protected StagePathStorages storeKarate(Long stageId, MultipartFile tarGz) {
        final String label = "karate";
        StorageType storageType = StorageType.KARATE_JSON;

        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        StagePathStorages stagePathStorages = storagePersistService.upsertStoragePath(null, label, prefix, stageId, storageType);
        if (!stagePathStorages.isComplete()) {
            s3Service.uploadTarGz(prefix, false, tarGz);  // false = don't expand
            storagePersistService.setUploadCompleted(null, label, prefix, stageId);
            stagePathStorages.setComplete();
        }
        return stagePathStorages;
    }

}