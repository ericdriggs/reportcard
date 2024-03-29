package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StagePathStorage {
    StagePath stagePath;
    Storage storage;
}
