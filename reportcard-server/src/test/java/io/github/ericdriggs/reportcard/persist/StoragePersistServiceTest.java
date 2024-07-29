package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathStorages;
import io.github.ericdriggs.reportcard.model.StoragePath;
import io.github.ericdriggs.reportcard.persist.test_result.TestResultPersistServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ReportcardApplication.class})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class StoragePersistServiceTest {

    @Autowired
    StoragePersistService storagePersistService;


    @Test
    void givenIncompleteStorage_WhenRePersist_ThenSuccess() throws IOException {
        String indexFile = TestResultPersistServiceTest.htmlIndexFile;
        Long stageId = 1L;
        final String label = "cucumber_html";

        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();
        final StorageType storageType = StorageType.HTML;

        StagePathStorages stagePathStorages = storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, storageType);
        StagePathStorages stagePathStorages2 = storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, storageType);
        assertEquals(stagePathStorages, stagePathStorages2);
    }

    @Test
    void givenCompleteStorage_WhenRePersist_ThenSuccess() {
        String indexFile = TestResultPersistServiceTest.htmlIndexFile;
        Long stageId = 1L;
        final String label = "cucumber_html";

        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();
        final StorageType storageType = StorageType.HTML;

        final StagePathStorages stagePathStorages = storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, storageType);
        storagePersistService.setUploadCompleted(indexFile, label, prefix, stageId, storageType);
        final StagePathStorages stagePathStorages2 =     storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, storageType);
        assertEquals(stagePathStorages, stagePathStorages2);
    }

    @Test
    void givenIncompleteStorage_WhenPersistWithDifferentStorage_ThenThrow() {
        String indexFile = TestResultPersistServiceTest.htmlIndexFile;
        Long stageId = 1L;
        final String label = "cucumber_html";

        final StagePath stagePath = storagePersistService.getStagePath(stageId);
        final String prefix = new StoragePath(stagePath, label).getPrefix();
        final StorageType storageType = StorageType.HTML;

        storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, storageType);
        storagePersistService.setUploadCompleted(indexFile, label, prefix, stageId, storageType);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            storagePersistService.upsertStoragePath(indexFile + "a", label, prefix, stageId, storageType);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            storagePersistService.upsertStoragePath(indexFile, label, prefix+ "a", stageId, storageType);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            storagePersistService.upsertStoragePath(indexFile, label, prefix, stageId, StorageType.XML);
        });
    }

}
