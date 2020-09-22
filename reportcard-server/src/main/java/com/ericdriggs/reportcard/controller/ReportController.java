package com.ericdriggs.reportcard.controller;

import com.ericdriggs.reportcard.model.ReportMetaData;
import com.ericdriggs.reportcard.model.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ReportController {

    @Autowired
    public ReportController(ReportControllerUtil reportControllerUtil) {
        this.reportControllerUtil = reportControllerUtil;
    }

    private final ReportControllerUtil reportControllerUtil;

    @PostMapping("/v1/xml")
    public ResponseEntity<TestResult> postXml(@RequestBody ReportMetaData reportMetatData, @RequestParam("files") MultipartFile[] files) {
        TestResult inserted = reportControllerUtil.doPostXml(reportMetatData, files);
        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

    @PostMapping("/v1/xml/junit")
    public ResponseEntity<TestResult> postXmlJunit(@RequestBody ReportMetaData reportMetatData, @RequestParam("file") MultipartFile file) {
        TestResult inserted = reportControllerUtil.doPostXmlJunit(reportMetatData, file);
        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

    @PostMapping("/v1/xml/surefire")
    public ResponseEntity<TestResult> postXmlSurefire(@RequestBody ReportMetaData reportMetatData, @RequestParam("files") MultipartFile[] files) {
        TestResult inserted = reportControllerUtil.doPostXmlSurefire(reportMetatData, files);
        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

}