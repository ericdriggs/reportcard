package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResponse;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.storage.DirectoryUploadResponse;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    //For internal testing only.
    public ResponseEntity<DirectoryUploadResponse> postStorageOnly(
            @RequestParam("storagePrefix") String storagePrefix,
            @RequestPart("files") MultipartFile[] files
    ) {
        return new ResponseEntity<>(s3Service.uploadDirectory(storagePrefix, files), HttpStatus.OK);
    }

    @Operation(summary = "Retrieves s3 object or folder for path. Folders are browsable")
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

    @Operation(summary = "Prefer junit controller unless storing multiple types of reports per stage. Post storage (usually html) for specified job stage.")
    @PostMapping(value = {"stage/{stageId}/reports/{label}/tar.gz"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StagePathStorageResponse> postStageStorageTarGZ(

            @Parameter(description = "generated id for the stage. See response from junit post.")
            @PathVariable("stageId") Long stageId,

            @Parameter(description = "Label for storage. Labels are unique per stage.")
            @PathVariable("label") String label,

            @Parameter(description = "Index file for html storage. Default: null")
            @RequestParam(value = "indexFile", required = false)
            String indexFile,

            @Parameter(description = "Storage type. Default: HTML")
            @RequestParam(value = "storageType", required = false)
            StorageType storageType,

            @RequestParam(required = false, defaultValue = "true") boolean shouldExpand,

            @Parameter(description = "Files and folders to store. Usually combination of html/css/js.")
            @RequestPart("storage.tar.gz") MultipartFile file
    ) {
        try {
            final StagePathStorages stagePathStorage = doPostStageStorageTarGZ(stageId, label, file, indexFile, storageType, shouldExpand);
            return StagePathStorageResponse.created(stagePathStorage).toResponseEntity();
        } catch (Exception ex) {
            return StagePathStorageResponse.fromException(ex).toResponseEntity();
        }
    }

    protected StagePathStorages doPostStageStorageTarGZ(
            @PathVariable("stageId") Long stageId,
            @PathVariable("label") String label,
            @RequestPart("storage.tar.gz") MultipartFile file,
            @RequestParam(value = "indexFile", required = false) String indexFile,
            @RequestParam(value = "storageType", required = false) StorageType storageType,
            @RequestParam(required = false, defaultValue = "true") boolean shouldExpand
    )

    {
        if (storageType == null) {
            storageType = StorageType.HTML;
        }
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();

        s3Service.uploadTarGz(prefix, shouldExpand, file);
        return storagePersistService.persistStoragePath(indexFile, label, prefix, stageId, storageType);
    }

}