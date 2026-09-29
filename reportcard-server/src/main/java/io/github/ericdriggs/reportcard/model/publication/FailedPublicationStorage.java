package io.github.ericdriggs.reportcard.model.publication;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class FailedPublicationStorage {
    Long storageId;
    String label;
    String storageType;
    Boolean isUploadComplete;
    String url;
}
