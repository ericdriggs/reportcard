package io.github.ericdriggs.reportcard.storage;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FailedFileUploadResponse {
    String request;
    Throwable exception;
}
