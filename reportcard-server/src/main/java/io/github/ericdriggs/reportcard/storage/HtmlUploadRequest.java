package io.github.ericdriggs.reportcard.storage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HtmlUploadRequest {
    private final Long stageId;
    private final String indexFile;
}
