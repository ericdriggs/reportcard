package io.github.ericdriggs.reportcard.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathStorages;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Builder
@Jacksonized
@Value
public class StagePathStorageResponse {

    StagePath stagePath;
    List<StoragePojo> storages;
    ResponseDetails responseDetails;

    public static StagePathStorageResponse ok(StagePathStorages s) {
        return StagePathStorageResponse.builder()
                .stagePath(s.getStagePath())
                .storages(s.getStorages())
                .responseDetails(ResponseDetails.ok())
                .build();
    }

    public static StagePathStorageResponse created(StagePathStorages s) {
        return StagePathStorageResponse.builder()
                .stagePath(s.getStagePath())
                .storages(s.getStorages())
                .responseDetails(ResponseDetails.created(s.getUrls()))
                .build();
    }

    public static StagePathStorageResponse fromException(Exception ex) {
        return StagePathStorageResponse.builder()
                .responseDetails(ResponseDetails.fromException(ex))
                .build();
    }

    public ResponseEntity<StagePathStorageResponse> toResponseEntity() {
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
