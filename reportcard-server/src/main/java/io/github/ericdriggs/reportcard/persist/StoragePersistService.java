package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.daos.StorageDao;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathStorage;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public StagePathStorage persistStoragePath(String indexFile, String label, String prefix, Long stageId, String storagePath, StorageType storageType) {
        return persistStoragePath(indexFile, label, prefix, stageId, storageType);
    }

    public StagePathStorage persistStoragePath(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        final StagePath stagePath = getStagePath(stageFk);
        final StoragePojo storage = insertStorage(indexFile, label, prefix, stageFk, storageType);
        return StagePathStorage.builder().stagePath(stagePath).storage(storage).build();
    }

    protected StoragePojo insertStorage(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        StoragePojo storage = new StoragePojo()
                .setIndexFile(indexFile)
                .setLabel(label)
                .setPrefix(prefix)
                .setStageFk(stageFk)
                .setStorageType(storageType.getStorageTypeId());
        storageDao.insert(storage);
        return storage;
    }
}