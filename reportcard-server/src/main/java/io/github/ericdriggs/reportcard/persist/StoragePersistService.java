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

    public Map<StagePath, Storage> persistStoragePath(String indexFile, String label, String prefix, Long stageId, String storagePath, StorageType storageType) {
        return persistStoragePath(indexFile, label, prefix, stageId, storageType);
    }

    public Map<StagePath, Storage> persistStoragePath(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        final StagePath stagePath = getStagePath(stageFk);
        final Storage storage = insertStorage(indexFile, label, prefix, stageFk, storageType);
        return Collections.singletonMap(stagePath, storage);
    }

    protected Storage insertStorage(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        Storage storage = new Storage()
                .setIndexFile(indexFile)
                .setLabel(label)
                .setPrefix(prefix)
                .setStageFk(stageFk)
                .setStorageType(storageType.getStorageTypeId());
        storageDao.insert(storage);
        return storage;
    }

}