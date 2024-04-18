package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
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

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/junit")
@SuppressWarnings("unused")
public class JunitController {

    public static String storageKeyPath = "/v1/api/storage/key";

    @Autowired
    public JunitController(StoragePersistService storagePersistService, TestResultPersistService testResultPersistService, S3Service s3Service) {
        this.storagePersistService = storagePersistService;
        this.testResultPersistService = testResultPersistService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;

    private final TestResultPersistService testResultPersistService;
    private final S3Service s3Service;


    @Operation(summary = "Post junit/surefire xmls for specified job stage")
    @PostMapping(path = "tar.gz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<StagePathTestResult> postJunitXml(
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

            @Parameter(description = "Comma separated key=value. Order does not matter. Trailing commas ignored. Each combination of job_info is a different job. Jobs have runs. Default: null", example="application=foo-app,pipeline=staging")
            @RequestParam(value = "jobInfo", required = false)
            String jobInfo,

            @Parameter(description = "Optional unique identifier for a run. Runs have stages.")
            @RequestParam(value = "runReference", required = false)
            String runReference,

            @Parameter(description = "Sha for the run.")
            @RequestParam("sha")
            String sha,

            @Parameter(description = "Stage name.")
            @RequestParam("stage")
            String stage,

            @Parameter(description = "Optional comma separated key=value links for the stage.", example="build=https://jenkins.mycorp.com/job/myorg/job/myrepo/job/main/123")
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
                                                .jobInfo(StringMapUtil.stringToMap(jobInfo))
                                                .runReference(runReference)
                                                .externalLinks(StringMapUtil.stringToMap(externalLinks))
                                                .build();

        List<String> testXmlContents = TestXmlTarGzUtil.getFileContentsFromTarGz(junitXmls);

        StagePathTestResult stagePathTestResult = doPostJunitXml(stageDetails, testXmlContents);
        return new ResponseEntity<>(stagePathTestResult, HttpStatus.OK);
    }

    public StagePathTestResult doPostJunitXml(StageDetails stageDetails, List<String> testXmlContents) {
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);

        return testResultPersistService.insertTestResult(stageDetails, testResultModel);
    }

    @Operation(summary = "Post storage (usually html) and junit/surefire xmls for specified job stage.", description = "Single call which performs both /v1/api/junit/tar.gz and /v1/api/storage/stage/{stageId}/reports/{label}/tar.gz")
    @PostMapping(value = {"storage/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StagePathStorageTestResult> postStageJunitStorageTarGZ(

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

            @Parameter(description = "Comma separated key=value. Order does not matter. Trailing commas ignored. Each combination of job_info is a different job. Jobs have runs. Default: null", example="application=foo-app,pipeline=staging")
            @RequestParam(value = "jobInfo", required = false)
            String jobInfo,

            @Parameter(description = "Optional unique identifier for a run. Runs have stages. Default: generated UUID")
            @RequestParam(value = "runReference", required = false)
            String runReference,

            @Parameter(description = "Sha for the run.")
            @RequestParam("sha")
            String sha,

            @Parameter(description = "Stage name.")
            @RequestParam("stage")
            String stage,

            @Parameter(description = "Label for storage. Labels are unique per stage.")
            @PathVariable("label")
            String label,

            @Parameter(description = "Index file for html storage. Default: null")
            @RequestParam(value = "indexFile", required = false)
            String indexFile,

            @Parameter(description = "Storage type. Default: HTML")
            @RequestParam(value = "storageType", required = false)
            StorageType storageType,

            @Parameter(description = "Optional comma separated key=value links for the stage.", example="build=https://jenkins.mycorp.com/job/myorg/job/myrepo/job/main/123")
            @RequestParam(value = "externalLinks", required = false)
            String externalLinks,

            @Parameter(description = "Junit and/or surefire xml files in the root of a .tar.gz file. " +
                                     "Used to generate a single test result. Test results contain test suites. Test suites contain test cases.")
            @RequestPart("junit.tar.gz")
            MultipartFile junitXmls,

            @Parameter(description = "Files and folders to store. Usually combination of html/css/js.")
            @RequestPart("reports.tar.gz")
            MultipartFile reports

    ) throws IOException {

        if (storageType == null) {
            storageType = StorageType.HTML;
        }

        StageDetails stageDetails = StageDetails.builder()
                                                .company(company)
                                                .org(org)
                                                .repo(repo)
                                                .branch(branch)
                                                .sha(sha)
                                                .stage(stage)
                                                .jobInfo(StringMapUtil.stringToMap(jobInfo))
                                                .runReference(runReference)
                                                .externalLinks(StringMapUtil.stringToMap(externalLinks))
                                                .build();

        List<String> testXmlContents = TestXmlTarGzUtil.getFileContentsFromTarGz(junitXmls);
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);

        StagePathTestResult stagePathTestResult = testResultPersistService.insertTestResult(stageDetails, testResultModel);
        StagePath stagePath = stagePathTestResult.getStagePath();
        final Long stageId = stagePath.getStage().getStageId();
        StagePathStorage stagePathStorage = doPostStageStorageTarGZ(stageId, label, reports, indexFile, storageType);

        StagePathStorageTestResult stagePathStorageTestResult = new StagePathStorageTestResult(stagePathStorage, stagePathTestResult);
        return new ResponseEntity<>(stagePathStorageTestResult, HttpStatus.OK);
    }

    @SuppressWarnings("ReassignedVariable")

    protected StagePathStorage doPostStageStorageTarGZ(
            @PathVariable("stageId") Long stageId,
            @PathVariable("label") String label,
            @RequestPart("reports.tar.gz") MultipartFile file,
            @RequestParam(value = "indexFile", required = false) String indexFile,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {
        if (storageType == null) {
            storageType = StorageType.HTML;
        }
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        s3Service.uploadTarGZ(file, prefix);
        return storagePersistService.persistStoragePath(indexFile, label, prefix, stageId, storageType);
    }

}