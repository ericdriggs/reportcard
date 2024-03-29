//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StagePathStorage {
    StagePath stagePath;
    StoragePojo storage;
}
