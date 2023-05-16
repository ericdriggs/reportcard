package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.TestResultUploadService;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
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
    public ResponseEntity<Map<StagePath,TestResult>> postJunitXml(@JsonProperty @RequestPart("reportMetaData") ReportMetaData reportMetaData, @RequestParam("files") MultipartFile[] files) {
        Map<StagePath,TestResult> stagePathTestResultMap = uploadService.doPostXml(reportMetaData, files);
        log.info("post success for reportMetaData: " + reportMetaData);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

}