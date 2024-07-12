package io.github.ericdriggs.reportcard.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathStorageResultCount;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Builder
@Jacksonized
@Value
public class StagePathStorageResultCountResponse {
    StagePath stagePath;
    List<StoragePojo> storages;
    ResultCount resultCount;
    ResponseDetails responseDetails;

    public static StagePathStorageResultCountResponse ok(StagePathStorageResultCount s) {
        return StagePathStorageResultCountResponse.builder()
                .stagePath(s.getStagePath())
                .storages(s.getStorages())
                .resultCount(s.getResultCount())
                .responseDetails(ResponseDetails.ok())
                .build();
    }

    public static StagePathStorageResultCountResponse created(StagePathStorageResultCount s) {
        return StagePathStorageResultCountResponse.builder()
                .stagePath(s.getStagePath())
                .storages(s.getStorages())
                .resultCount(s.getResultCount())
                .responseDetails(ResponseDetails.created(s.getUrls()))
                .build();
    }

    public static StagePathStorageResultCountResponse fromException(Exception ex) {
        return StagePathStorageResultCountResponse.builder()
                .responseDetails(ResponseDetails.fromException(ex))
                .build();
    }

    public ResponseEntity<StagePathStorageResultCountResponse> toResponseEntity() {
        return new ResponseEntity<>(this, HttpStatus.valueOf(getHttpStatusCode()));
    }

    @JsonIgnore
    public int getHttpStatusCode() {
        if (responseDetails == null) {
            return 500;
        }
        return responseDetails.getHttpStatus();
    }
}
