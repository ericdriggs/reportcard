package io.github.ericdriggs.reportcard.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Jacksonized
@Value
public class StagePathTestResultResponse {
    StagePath stagePath;
    TestResultModel testResult;
    ResponseDetails responseDetails;

    public static StagePathTestResultResponse ok(StagePathTestResult s) {
        return StagePathTestResultResponse.builder()
                .stagePath(s.getStagePath())
                .testResult(s.getTestResult())
                .responseDetails(ResponseDetails.ok())
                .build();
    }

    public static StagePathTestResultResponse created(StagePathTestResult s) {
        return StagePathTestResultResponse.builder()
                .stagePath(s.getStagePath())
                .testResult(s.getTestResult())
                .responseDetails(ResponseDetails.created(s.getUrls()))
                .build();
    }

    public static StagePathTestResultResponse fromException(Exception ex) {
        return StagePathTestResultResponse.builder()
                .responseDetails(ResponseDetails.fromException(ex))
                .build();
    }

    public ResponseEntity<StagePathTestResultResponse> toResponseEntity() {
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
