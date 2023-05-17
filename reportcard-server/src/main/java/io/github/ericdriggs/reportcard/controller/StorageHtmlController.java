package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.storage.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage/html")
@SuppressWarnings("unused")
public class StorageHtmlController {

    @Autowired
    public StorageHtmlController(StoragePersistService storagePersistService, S3Service s3Service) {
        this.storagePersistService = storagePersistService;
        this.s3Service = s3Service;
    }

    private final StoragePersistService storagePersistService;
    private final S3Service s3Service;

    @PostMapping("stage/{stageId}")
    public ResponseEntity<Map<StagePath, Storage>> postHtml(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("indexFile") String indexFile,
            @PathVariable("stageId") Long stageId
    ) {
        final StorageType storageType = StorageType.HTML;
        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String storagePrefix = new StoragePath(stagePath).getPrefix(storageType);

        s3Service.uploadDirectory(files, storagePrefix);
        Map<StagePath, Storage> stagePathTestResultMap = storagePersistService.persistStoragePath(stageId, indexFile, storagePrefix, StorageType.HTML);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

}