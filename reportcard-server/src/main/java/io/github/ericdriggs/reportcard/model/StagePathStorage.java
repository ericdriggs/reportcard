//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.controller.html.StorageHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class StagePathStorage {
    StagePath stagePath;
    StoragePojo storage;

    public List<String> getUrls() {
        return getUrls(stagePath, storage);
    }

    static List<String> getUrls(StagePath stagePath, StoragePojo storage) {
        List<String> urls = new ArrayList<>();
        if (stagePath != null) {
            urls.add(stagePath.getUrl());
        }
        if (storage != null) {
            urls.add(StorageHtmlHelper.getStorageUrl(storage));
        }
        return urls;
    }
}
