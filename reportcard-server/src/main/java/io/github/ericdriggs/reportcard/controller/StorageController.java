package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.storage.DirectoryUploadResponse;
import io.github.ericdriggs.reportcard.storage.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/api/storage")
@SuppressWarnings("unused")
public class StorageController {

    public static String storageKeyPath = "/v1/api/storage/key";



    @Autowired
    public StorageController(StoragePersistService storagePersistService, S3Service s3Service) {
        this.storagePersistService = storagePersistService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;
    private final S3Service s3Service;

    @SuppressWarnings("ReassignedVariable")
    @PostMapping(value = {"stage/{stageId}/reports/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<StagePath, Storage>> postStageReportsTarGZ(
            @PathVariable("stageId") Long stageId,
            @PathVariable("label") String label,
            @RequestPart("reports.tar.gz") MultipartFile file,
            @RequestParam(value = "indexFile", required = false) String indexFile,
            @RequestParam(value="storageType", required = false) StorageType storageType)
    {
        if (storageType == null) {
            storageType = StorageType.HTML;
        }
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        s3Service.uploadTarGZ(file, prefix);
         Map<StagePath, Storage> stagePathTestResultMap = storagePersistService.persistStoragePath(indexFile, label, prefix, stageId, storageType);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

    @PostMapping(value = {"stage/{stageId}/reports/{label}/files"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<StagePath, Storage>> postStageHtml(
            @PathVariable("stageId") Long stageId,
            @PathVariable("label") String label,
            @RequestPart("files") MultipartFile[] files,
            @RequestParam(value = "indexFile", required = false) String indexFile
    ) {
        final StorageType storageType = StorageType.HTML;
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        s3Service.uploadDirectory(files, prefix);
        Map<StagePath, Storage> stagePathTestResultMap = storagePersistService.persistStoragePath(indexFile, label, prefix, stageId, storageType);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }




    //For testing only
    //TODO: hide when not running locally
    @PostMapping(value = {"path/"},
            consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DirectoryUploadResponse> postStorageOnly(
            @RequestParam("storagePrefix") String storagePrefix,
            @RequestPart("files") MultipartFile[] files
    ) {
        return new ResponseEntity<>(s3Service.uploadDirectory(files, storagePrefix), HttpStatus.OK);
    }

    @GetMapping(value = {"key/**"})
    public ResponseEntity<?> getKeyContents(HttpServletRequest request)
    {
        final String key = request.getRequestURI().split(request.getContextPath() + "/key/")[1];

        ResponseBytes<GetObjectResponse> responseBytes = s3Service.getObjectBytes(key);
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

}