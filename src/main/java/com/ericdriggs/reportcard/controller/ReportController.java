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
import com.ericdriggs.reportcard.xml.XmlUtil;
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
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ReportController {


    @Autowired
    public ReportController(ReportCardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    private final ReportCardService reportCardService;

    @PostMapping("/v1/xml")
    public ResponseEntity<TestResult> postXml(@RequestBody ReportMetatData reportMetatData, @RequestParam("files") MultipartFile[] files) {
        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(file.toString()));

        TestResult inserted = doPostXml(reportMetatData, xmlStrings);
        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

    public TestResult doPostXml(ReportMetatData reportMetatData, List<String> xmlStrings) {
        TestResult inserted = null;
        if (xmlStrings == null || xmlStrings.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing files");
        } else if (xmlStrings.size() > 1) {
            for (String xmlString : xmlStrings) {
                if (!"testsuite".equals(XmlUtil.getXmlRootElementName(xmlString))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Multiple files only supported with surefire format");
                }
            }
            inserted = doPostXmlSurefire(reportMetatData, xmlStrings);
        } else { //xmlString.size == 1
            String xmlString = xmlStrings.get(0);
            String rootElementName = XmlUtil.getXmlRootElementName(xmlString);
            if ("testsuite".equals(rootElementName)) {
                inserted = doPostXmlSurefire(reportMetatData, xmlStrings);
            } else if ("testsuites".equals(rootElementName)) {
                inserted = doPostXmlJunit(reportMetatData, xmlString);
            }
        }
        return inserted;
    }

    @PostMapping("/v1/xml/junit")
    public ResponseEntity<TestResult> postXmlJunit(@RequestBody ReportMetatData reportMetatData, @RequestParam("file") MultipartFile file) {

        TestResult inserted = doPostXmlJunit(reportMetatData, file.toString());
        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

    public TestResult doPostXmlJunit(ReportMetatData reportMetatData, String xmlString) {
        reportMetatData.validateAndSetDefaults();
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        TestResult testResult = JunitConvertersUtil.modelMapper.map(testsuites, TestResult.class);
        BuildStagePath buildStagePath = reportCardService.getBuildStagePath(reportMetatData);
        testResult.setBuildStageFk(buildStagePath.getBuildStage().getBuildStageId());
        return reportCardService.insertTestResult(testResult);
    }

    @PostMapping("/v1/xml/surefire")
    public ResponseEntity<TestResult> postXmlSurefire(@RequestBody ReportMetatData reportMetatData, @RequestParam("files") MultipartFile[] files) {

        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(file.toString()));

        TestResult inserted = doPostXmlSurefire(reportMetatData, xmlStrings);
        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }

    public TestResult doPostXmlSurefire(ReportMetatData reportMetatData, List<String> xmlStrings) {
        reportMetatData.validateAndSetDefaults();
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuites(xmlStrings);
        TestResult testResult = SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
        BuildStagePath buildStagePath = reportCardService.getBuildStagePath(reportMetatData);
        testResult.setBuildStageFk(buildStagePath.getBuildStage().getBuildStageId());
        return reportCardService.insertTestResult(testResult);
    }

}