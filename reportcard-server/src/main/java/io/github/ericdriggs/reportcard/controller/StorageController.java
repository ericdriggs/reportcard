package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.storage.DirectoryUploadResponse;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/storage")
@SuppressWarnings("unused")
public class StorageController {

    public static String storageKeyPath = "/v1/api/storage/key";

    @Autowired
    public StorageController(StoragePersistService storagePersistService, TestResultPersistService testResultPersistService, S3Service s3Service) {
        this.storagePersistService = storagePersistService;
        this.testResultPersistService = testResultPersistService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;

    private final TestResultPersistService testResultPersistService;
    private final S3Service s3Service;

    @PostMapping(value = {"label/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
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
    @PostMapping(value = {"stage/{stageId}/reports/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StagePathStorage> postStageStorageTarGZ(
            @PathVariable("stageId") Long stageId,
            @PathVariable("label") String label,
            @RequestPart("reports.tar.gz") MultipartFile file,
            @RequestParam(value = "indexFile", required = false) String indexFile,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {

        return new ResponseEntity<>(doPostStageStorageTarGZ(stageId, label, file, indexFile, storageType), HttpStatus.OK);
    }

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

    @PostMapping(value = {"stage/{stageId}/reports/{label}/files"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StagePathStorage> postStageHtml(
            @PathVariable("stageId") Long stageId,
            @PathVariable("label") String label,
            @RequestPart("files") MultipartFile[] files, //TODO: single file
            @RequestParam(value = "indexFile", required = false) String indexFile
    ) {
        final StorageType storageType = StorageType.HTML;
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        s3Service.uploadDirectory(files, prefix);
        StagePathStorage stagePathStorage = storagePersistService.persistStoragePath(indexFile, label, prefix, stageId, storageType);
        return new ResponseEntity<>(stagePathStorage, HttpStatus.OK);
    }

    //For testing only
    //TODO: hide when not running locally
    @PostMapping(value = {"path/"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DirectoryUploadResponse> postStorageOnly(
            @RequestParam("storagePrefix") String storagePrefix,
            @RequestPart("files") MultipartFile[] files
    ) {
        return new ResponseEntity<>(s3Service.uploadDirectory(files, storagePrefix), HttpStatus.OK);
    }

    @GetMapping(value = {"key/**"})
    public ResponseEntity<?> getKey(HttpServletRequest request) {
        final String prefix = request.getRequestURI().split(request.getContextPath() + "/key/")[1];

        //TODO: implement static cache for html reporting css/js/fonts/images

        ListObjectsV2Response listResponse = s3Service.listObjects(prefix);
        if (isS3File(listResponse)) {
            return getKeyContents(prefix);
        } else {
            return browseS3(prefix, listResponse);
        }
    }

    protected boolean isS3File(ListObjectsV2Response response) {
        if (!CollectionUtils.isEmpty(response.commonPrefixes())) {
            return false;
        }

        if (CollectionUtils.isEmpty(response.contents())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no match found for key: " + response.prefix());
        }

        if (response.contents().size() != 1) {
            return false;
        }
        for (S3Object obj : response.contents()) {
            if (obj.size() > 0) {
                return true;
            }
        }
        return false;
    }

    protected ResponseEntity<?> getKeyContents(String prefix) {


        ResponseBytes<GetObjectResponse> responseBytes = s3Service.getObjectBytes(prefix);
        SdkHttpResponse sdkHttpResponse = responseBytes.response().sdkHttpResponse();
        if (!sdkHttpResponse.isSuccessful()) {
            return ResponseEntity.status(sdkHttpResponse.statusCode()).body(sdkHttpResponse.statusText().orElse(""));
        }

        final byte[] responseBody = responseBytes.asByteArray();
        List<String> contentTypeList = sdkHttpResponse.headers().get("Content-Type");
        String contentType = contentTypeList.stream().findFirst().orElse("unknown");
        return ResponseEntity
                .status(sdkHttpResponse.statusCode())
                .header("Content-type", contentType)
                .body(responseBody);
    }

    protected ResponseEntity<String> browseS3(String prefix, ListObjectsV2Response listResponse) {
        return ResponseEntity.ok(StorageHtmlHelper.getS3BrowsePage(listResponse, prefix));
    }





}