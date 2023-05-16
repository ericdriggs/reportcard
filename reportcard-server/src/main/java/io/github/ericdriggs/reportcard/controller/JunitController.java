package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.TestResultUploadService;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.TestResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/junit")
@SuppressWarnings("unused")
public class JunitController {

    @Autowired
    public JunitController(TestResultUploadService uploadService) {
        this.uploadService = uploadService;
    }

    private final TestResultUploadService uploadService;

    @PostMapping("")
    public ResponseEntity<Map<StagePath,TestResult>> postJunitXml(@JsonProperty @RequestPart("stageDetails") StageDetails stageDetails, @RequestParam("files") MultipartFile[] files) {
        Map<StagePath,TestResult> stagePathTestResultMap = uploadService.doPostXml(stageDetails, files);
        log.info("post success for postJunitXml -- stageDetails: {}", stageDetails);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

    @PostMapping("run/{runId}/stage/{stage}")
    public ResponseEntity<Map<StagePath,TestResult>> postJunitXmlNewStageForRun(
            @RequestParam("files") MultipartFile[] files,
            @PathVariable("runId") Long runId,
            @PathVariable("stage") String stage
    ) {
        Map<StagePath,TestResult> stagePathTestResultMap = uploadService.doPostXml(runId, stage, files);
        log.info("post success for postJunitXmlAddStage -- runId: {}. stage: {}: ", runId, stage);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

}