//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
@AllArgsConstructor
public class StagePathStorageResultCount {
    StagePath stagePath;
    List<StoragePojo> storages;
    ResultCount resultCount;

    public StagePathStorageResultCount(StagePath stagePath, List<StoragePojo> storages, StagePathTestResult stagePathTestResult) {
        if (stagePath == null) {
            throw new NullPointerException("stagePath");
        }
        if (storages == null) {
            throw new NullPointerException("storages");
        }
        if (stagePathTestResult == null) {
            throw new NullPointerException("stagePathTestResult");
        }


        if (stagePath.compareTo(stagePathTestResult.getStagePath()) != 0) {
            throw new IllegalStateException("stagePath: " + stagePath + " != stagePathTestResult.getStagePath(): " + stagePathTestResult.getStagePath());
        }

        this.stagePath = stagePath;
        this.storages = storages;
        this.resultCount = stagePathTestResult.getTestResult().getResultCount();
    }

    public Map<String,String> getUrls() {
        return StagePathStorages.getUrls(stagePath, storages);
    }
}
