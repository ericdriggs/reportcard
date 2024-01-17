package io.github.ericdriggs.reportcard.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class DirectoryUploadResponse {

    @Builder.Default
    @NonNull
    List<FailedFileUploadResponse> failedFileUploadResponses = Collections.emptyList();

    @SneakyThrows(JsonProcessingException.class)
    public String toJson() {
        return SharedObjectMappers.simpleObjectMapper.writeValueAsString(this);
    }
}
