package io.github.ericdriggs.reportcard.controller.model;

import io.github.ericdriggs.reportcard.model.StageDetails;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Jacksonized
@Value
public class JunitHtmlPostRequest {
    StageDetails stageDetails;
    String label;
    String indexFile;
    MultipartFile junitXmls;
    MultipartFile karateTarGz;
    MultipartFile reports;
}
