//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class StagePathStorageResultCount {
    StagePath stagePath;
    StoragePojo storage;
    ResultCount resultCount;

    public StagePathStorageResultCount(StagePathStorage stagePathStorage, StagePathTestResult stagePathTestResult) {
        if (stagePathStorage == null) {
            throw new NullPointerException("stagePathStorage");
        }
        if (stagePathTestResult == null) {
            throw new NullPointerException("stagePathTestResult");
        }

        if (stagePathStorage.getStagePath() == null) {
            throw new IllegalStateException("stagePathStorage.getStagePath() == null");
        }
        if (stagePathTestResult.getStagePath() == null) {
            throw new IllegalStateException("stagePathTestResult.getStagePath() == null");
        }

        if (stagePathStorage.getStagePath().compareTo(stagePathTestResult.getStagePath()) != 0) {
            throw new IllegalStateException("stagePathStorage.getStagePath(): " + stagePathStorage.getStagePath() + " != stagePathTestResult.getStagePath(): " + stagePathTestResult.getStagePath());
        }

        this.stagePath = stagePathStorage.getStagePath();
        this.storage = stagePathStorage.getStorage();
        this.resultCount = stagePathTestResult.getTestResult().getResultCount();
    }

    public List<String> getUrls() {
        return StagePathStorage.getUrls(stagePath, storage);
    }
}
