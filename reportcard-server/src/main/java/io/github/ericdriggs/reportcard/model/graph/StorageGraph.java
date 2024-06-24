package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record StorageGraph(
        Long storageId,
        Long stageFk,
        String label,
        String prefix,
        String indexFile,
        Integer storageType) {

    @JsonIgnore
    public StoragePojo asStoragePojo() {
        return StoragePojo.builder()
                          .storageId(storageId)
                          .stageFk(stageFk)
                          .label(label)
                          .prefix(prefix)
                          .indexFile(indexFile)
                          .storageType(storageType)
                          .build();
    }
}
