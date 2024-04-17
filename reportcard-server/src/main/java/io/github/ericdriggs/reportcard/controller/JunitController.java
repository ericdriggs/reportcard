package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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


    //For internal testing only
    @PostMapping(path = "tar.gz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<StagePathTestResult> postJunitXml(
            @RequestParam("company") String company,
            @RequestParam("org") String org,
            @RequestParam("repo") String repo,
            @RequestParam("branch") String branch,
            @RequestParam("sha") String sha,
            @RequestParam("stage") String stage,
            @RequestParam(value = "jobInfo", required = false) String jobInfo,
            @RequestParam(value = "runReference", required = false) String runReference,
            @RequestParam(value = "externalLinks", required = false) String externalLinks,
            @RequestPart("junit.tar.gz") MultipartFile junitXmls
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



    @PostMapping(value = {"storage/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StagePathStorageTestResult> postStageJunitStorageTarGZ(
            @PathVariable("label") String label,
            @RequestPart("junit.tar.gz") MultipartFile junitXmls,
            @RequestPart("reports.tar.gz") MultipartFile reports,
            @RequestParam("company") String company,
            @RequestParam("org") String org,
            @RequestParam("repo") String repo,
            @RequestParam("branch") String branch,
            @RequestParam("sha") String sha,
            @RequestParam("stage") String stage,
            @RequestParam(value = "jobInfo", required = false) String jobInfo,
            @RequestParam(value = "runReference", required = false) String runReference,
            @RequestParam(value = "indexFile", required = false) String indexFile,
            @RequestParam(value = "storageType", required = false) StorageType storageType,
            @RequestParam(value = "externalLinks", required = false) String externalLinks) throws IOException {

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
        StagePathStorage stagePathStorage = doPostStageStorageTarGZ(stageId, label, reports, indexFile, storageType );

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