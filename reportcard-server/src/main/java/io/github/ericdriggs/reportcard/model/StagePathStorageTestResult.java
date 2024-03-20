//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class StagePathStorageTestResult {
    StagePath stagePath;
    StoragePojo storage;
    TestResult testResult;

    public StagePathStorageTestResult(StagePathStorage stagePathStorage, StagePathTestResult stagePathTestResult) {
        if (stagePathStorage == null) {
            throw new NullPointerException("stagePathStorage");
        }
        if (stagePathTestResult == null) {
            throw new NullPointerException("stagepathTestResult");
        }

        if (stagePathStorage.getStagePath() == null) {
            throw new IllegalStateException("stagePathStorage.getStagePath() == null");
        }
        if (stagePathTestResult.getStagePath() == null) {
            throw new IllegalStateException("stagePathTestResult.getStagePath() == null");
        }

        if (stagePathStorage.getStagePath().compareTo(stagePathTestResult.getStagePath()) != 0) {
            throw new IllegalStateException("stagePathStorage.getStagePath(): " + stagePathStorage.getStagePath() + " != stagePathTestResult.getStagePath(): " + stagePathTestResult.getStagePath() );
        }

        this.stagePath = stagePathStorage.getStagePath();
        this.storage = stagePathStorage.getStorage();
        this.testResult = stagePathTestResult.getTestResult();
    }
}
