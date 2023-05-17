package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.daos.StorageDao;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;
import io.github.ericdriggs.reportcard.model.StagePath;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class StoragePersistService extends StagePathPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    final protected StorageDao storageDao;

    @Autowired
    public StoragePersistService(DSLContext dsl) {
        super(dsl);
        storageDao = new StorageDao(dsl.configuration());
    }

//    public Map<StagePath, Storage> persistStoragePath(StageDetails stageDetails, String indexFile, String storagePath, StorageType storageType) {
//        StagePath stagePath = getOrInsertStagePath(stageDetails);
//        return persistStoragePath(stagePath, indexFile, storagePath, storageType);
//
//    }
//
//    public Map<StagePath, Storage> persistStoragePath(Long runId, String stageName, String indexFile, String storagePath, StorageType storageType) {
//        StagePath stagePath = getOrInsertStage(runId, stageName);
//        return persistStoragePath(stagePath, indexFile, storagePath, storageType);
//    }

    public Map<StagePath, Storage> persistStoragePath(Long stageId, String indexFile, String storagePath, StorageType storageType) {
        StagePath stagePath = getStagePath(stageId);
        return persistStoragePath(stagePath, indexFile, storagePath, storageType);
    }

    public Map<StagePath, Storage> persistStoragePath(StagePath stagePath, String indexFile, String storagePath, StorageType storageType) {
        return Collections.singletonMap(stagePath, insertStorage(stagePath, indexFile, storagePath, storageType));
    }

    protected Storage insertStorage(StagePath stagePath, String indexFile, String storagePath, StorageType storageType) {
        Storage storage = new Storage()
                .setIndexfile(indexFile)
                .setPath(storagePath)
                .setType(storageType.name())
                .setStageFk(stagePath.getStage().getStageId());

        storageDao.insert(storage);
        return storage;
    }

}