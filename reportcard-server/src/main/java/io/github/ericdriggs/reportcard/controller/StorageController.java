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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage/")
@SuppressWarnings("unused")
public class StorageController {

    @Autowired
    public StorageController(StoragePersistService storagePersistService, S3Service s3Service) {
        this.storagePersistService = storagePersistService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;
    private final S3Service s3Service;

    @PostMapping(value = {"stage/html/{stageId}"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<StagePath, Storage>> postStageHtml(
            @RequestParam("indexFile") String label,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("indexFile") String indexFile,
            @PathVariable("stageId") Long stageId
    ) {
        final StorageType storageType = StorageType.HTML;
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath).getPrefix();

        s3Service.uploadDirectory(files, prefix);
        Map<StagePath, Storage> stagePathTestResultMap = storagePersistService.persistStoragePath(indexFile, label, prefix, stageId);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

    //For testing only
    //TODO: hide when not running locally
    @PostMapping(value = {"path/{storagePrefix}"},
            consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DirectoryUploadResponse> postStorageOnly(
            @PathVariable("storagePrefix") String storagePrefix,
            //@Schema(type = "string", format = "binary")
            @RequestPart("files") MultipartFile[] files
    ) {
        return new ResponseEntity<>(s3Service.uploadDirectory(files, storagePrefix), HttpStatus.OK);
    }

}