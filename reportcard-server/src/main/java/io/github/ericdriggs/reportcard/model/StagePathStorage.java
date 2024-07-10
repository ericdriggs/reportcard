//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class StagePathStorage {
    StagePath stagePath;
    StoragePojo storage;

    public Map<String,String> getUrls() {
        return getUrls(stagePath, storage);
    }

    static Map<String,String> getUrls(StagePath stagePath, StoragePojo storage) {
        Map<String,String> urls = new LinkedHashMap<>();
        if (stagePath != null) {
            urls.put("stage", stagePath.getUrl());
        }
        if (storage != null) {
            urls.put("storage", StorageHtmlHelper.getStorageUrl(storage));
        }
        return urls;
    }
}
