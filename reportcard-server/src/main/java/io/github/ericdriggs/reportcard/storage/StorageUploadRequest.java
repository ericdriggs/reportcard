package io.github.ericdriggs.reportcard.storage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageUploadRequest {
    private final Long stageId;
}
