//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
@Jacksonized
@Value
public class StagePathStorages {
    StagePath stagePath;
    @Builder.Default
    List<StoragePojo> storages = new ArrayList<>();

    public Map<String,String> getUrls() {
        return getUrls(stagePath, storages);
    }

    static Map<String,String> getUrls(StagePath stagePath, List<StoragePojo> storages) {
        Map<String,String> urls = new LinkedHashMap<>();
        if (stagePath != null) {
            urls.put("stage", stagePath.getUrl());
        }
        if (!CollectionUtils.isEmpty(storages)) {
            for (StoragePojo storage : storages) {
                urls.put(storage.getLabel(), StorageHtmlHelper.getStorageURI(storage).toString());
            }

        }
        return urls;
    }

    @JsonIgnore
    public boolean isComplete() {
        if (CollectionUtils.isEmpty(storages)) {
            return false;
        }
        boolean isComplete = true;
        for (StoragePojo storage : storages) {
            if (!storage.getIsUploadComplete() ) {
                isComplete = false;
            }
        }
        return isComplete;
    }

    @JsonIgnore
    public void setComplete() {
        if (CollectionUtils.isEmpty(storages)) {
            return;
        }
        for (StoragePojo storage : storages) {
            storage.setIsUploadComplete(true);
        }
    }

    public static StagePathStorages merge(StagePathStorages s1, StagePathStorages s2) {
        List<String> missingErrors = new ArrayList<>();
        missingErrors.addAll(missingErrors(s1, "s1"));
        missingErrors.addAll(missingErrors(s2, "s2"));
        if (!missingErrors.isEmpty()) {
            throw new IllegalArgumentException("missing variables: " + String.join(", ", missingErrors));
        }

        if (!s1.getStagePath().equals(s2.getStagePath())) {
            throw new IllegalArgumentException("s1.getStagePath(): " + s1.getStagePath()+ "  != s2.getStagePath(): " + s2.getStagePath());
        }

        List<StoragePojo> storages = new ArrayList<>(s1.getStorages());
        storages.addAll(s2.getStorages());
        return StagePathStorages.builder()
                .stagePath(s1.getStagePath())
                .storages(storages)
                .build();
    }

    protected static List<String> missingErrors(StagePathStorages stagePathStorages, String name) {
        List<String> missingErrors = new ArrayList<>();
        if (stagePathStorages == null) {
            missingErrors.add(name);
        } else {
            if (stagePathStorages.getStagePath() == null) {
                missingErrors.add(name + ".getStagePath()");
            }
            if (stagePathStorages.getStorages() == null) {
                missingErrors.add(name + ".getStorages()");
            }
        }
        return missingErrors;
    }
}
