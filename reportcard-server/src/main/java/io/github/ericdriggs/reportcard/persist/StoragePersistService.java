package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.daos.StorageDao;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathStorages;
import org.apache.commons.lang3.ObjectUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

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

    public StagePathStorages upsertStoragePath(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        final StagePath stagePath = getStagePath(stageFk);

        final StoragePojo storagePojo;
        {
            List<StoragePojo> existingStoragePojos = getExistingStoragePojos(indexFile, label, prefix, stageFk, storageType);
            final StorageStatus existingStorageStatus = getStorageStatus(indexFile, label, prefix, stageFk, storageType, existingStoragePojos);
            if (existingStorageStatus == StorageStatus.INCOMPLETE || existingStorageStatus == StorageStatus.COMPLETE) {
                storagePojo = existingStoragePojos.get(0);
            }
            else {
                storagePojo = insertStorage(indexFile, label, prefix, stageFk, storageType);
            }
        }
        return StagePathStorages.builder().stagePath(stagePath).storages(List.of(storagePojo)).build();
    }

    protected List<StoragePojo> getExistingStoragePojos(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        return dsl.select(STORAGE.fields())
                .from(STORAGE)
                .where(STORAGE.STAGE_FK.eq(stageFk).and(STORAGE.LABEL.eq(label)))
                .fetch()
                .into(StoragePojo.class);

    }


    //TOMAYBE: refactor into separate methods so don't throw as a side-effect of checking status.
    //This is the only place that validates that there aren't mismatched requests for the same stageFk / label
    protected StorageStatus getStorageStatus(String indexFile, String label, String prefix, Long stageFk, StorageType storageType, List<StoragePojo> storageMatches) {

        if (CollectionUtils.isEmpty(storageMatches) || (storageMatches.size() == 1 && storageMatches.get(0).getStorageId() == null)) {
            return StorageStatus.NOT_FOUND;
        }
        if (storageMatches.size() > 1) {
            throw new IllegalStateException(String.format("Found more than one storage matching stageFk: %d, label: %s", stageFk, label));
        }
        StoragePojo storagePojo = storageMatches.get(0);
        List<String> diffs = new ArrayList<>();
        if (ObjectUtils.compare(indexFile, storagePojo.getIndexFile()) != 0) {
            diffs.add("indexFile: " + indexFile + " != storagePojo.getIndexFile(): " + storagePojo.getIndexFile());
        }
        if (ObjectUtils.compare(prefix, storagePojo.getPrefix()) != 0) {
            diffs.add("prefix: " + prefix + " != storagePojo.getPrefix(): " + storagePojo.getPrefix());
        }
        if (StorageType.compare(storageType, storagePojo.getStorageType()) != 0) {
            diffs.add("storageType: " + storageType + " != storagePojo.getStorageType(): " + storagePojo.getStorageType());
        }
        if (!diffs.isEmpty()) {
            throw  new IllegalArgumentException("Storage record already exists with different parameters. Differences:  " + diffs);
        }
        if (storagePojo.getIsUploadComplete() ) {
            return StorageStatus.COMPLETE;
        }
        return StorageStatus.INCOMPLETE;
    }



    protected StoragePojo insertStorage(String indexFile, String label, String prefix, Long stageFk, StorageType storageType) {
        StoragePojo storage = new StoragePojo()
                .setIndexFile(indexFile)
                .setLabel(label)
                .setPrefix(prefix)
                .setStageFk(stageFk)
                .setStorageType(storageType.getStorageTypeId())
                .setIsUploadComplete(false);
        storageDao.insert(storage);
        return storage;
    }

    public void setUploadCompleted(String indexFile, String label, String prefix, Long stageId) {
        final int rowsEffected =  dsl.update(STORAGE)
                .set(STORAGE.IS_UPLOAD_COMPLETE,true)
                .where(STORAGE.STAGE_FK.eq(stageId).and(STORAGE.LABEL.eq(label))).execute();

        if (rowsEffected == 0) {
            log.warn("Zero rows set to completed for indexFile: " + indexFile + ", label: " + label + ", prefix: " + prefix);
        }
        if (rowsEffected > 1) {
            log.warn("More than 1 row set to completed for indexFile: " + indexFile + ", label: " + label + ", prefix: " + prefix);
        }
    }
}