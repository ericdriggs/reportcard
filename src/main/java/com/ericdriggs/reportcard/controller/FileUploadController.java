package com.ericdriggs.reportcard.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.model.BuildStagePath;
import com.ericdriggs.reportcard.model.ReportMetatData;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import com.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil;
import com.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import com.ericdriggs.reportcard.xml.junit.Testsuites;
import com.ericdriggs.reportcard.xml.surefire.SurefireParserUtil;
import com.ericdriggs.reportcard.xml.surefire.Testsuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {


    @Autowired
    public FileUploadController(ReportCardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    private ReportCardService reportCardService;

    @PostMapping("/v1/xml/junit")
    public ResponseEntity<TestResult> postXmlJunit(@RequestBody ReportMetatData reportMetatData, @RequestParam("file") MultipartFile file) {

        reportMetatData.validateAndSetDefaults();
        Testsuites testsuites = JunitParserUtil.parseTestSuites(file.toString());
        TestResult testResult = JunitConvertersUtil.modelMapper.map(testsuites, TestResult.class);
        BuildStagePath buildStagePath = reportCardService.getBuildStagePath(reportMetatData);
        testResult.setBuildStageFk(buildStagePath.getBuildStage().getBuildStageId());
        TestResult inserted = reportCardService.insertTestResult(testResult);

        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

    @PostMapping("/v1/xml/surefire")
    public ResponseEntity<TestResult> postXmlSurefire(@RequestBody ReportMetatData reportMetatData, @RequestParam("files") MultipartFile[] files) {

        List<String> fileList = new ArrayList<>();
        Arrays.asList(files)
                .stream()
                .forEach(file -> fileList.add(file.toString()));

        reportMetatData.validateAndSetDefaults();
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuites(fileList);
        TestResult testResult = SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
        BuildStagePath buildStagePath = reportCardService.getBuildStagePath(reportMetatData);
        testResult.setBuildStageFk(buildStagePath.getBuildStage().getBuildStageId());
        TestResult inserted = reportCardService.insertTestResult(testResult);

        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }


}