package com.ericdriggs.reportcard.controller;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.model.ExecutionStagePath;
import com.ericdriggs.reportcard.model.ReportMetaData;
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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ReportControllerUtil {

    @Autowired
    public ReportControllerUtil(ReportCardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    private final ReportCardService reportCardService;

    public TestResult getTestResult(Long testResultId) {
        return reportCardService.getTestResult(testResultId);
    }

    public TestResult doPostXml(ReportMetaData reportMetatData, MultipartFile[] files) {
        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(fileToString(file)));

        return doPostXmlStrings(reportMetatData, xmlStrings);
    }

    public TestResult doPostXmlStrings(ReportMetaData reportMetatData, List<String> xmlStrings) {
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

    public TestResult doPostXmlJunit(ReportMetaData reportMetatData, MultipartFile file) {
        return doPostXmlJunit(reportMetatData, fileToString(file));
    }

    public TestResult doPostXmlJunit(ReportMetaData reportMetatData, String xmlString) {
        reportMetatData.validateAndSetDefaults();
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        TestResult testResult = JunitConvertersUtil.modelMapper.map(testsuites, TestResult.class);
        ExecutionStagePath buildStagePath = reportCardService.getOrInsertExecutionStagePath(reportMetatData);
        testResult.setStageFk(buildStagePath.getStage().getStageId());
        return reportCardService.insertTestResult(testResult);
    }

    public TestResult doPostXmlSurefire(ReportMetaData reportMetatData, MultipartFile[] files) {

        List<String> xmlStrings = new ArrayList<>();
        for (MultipartFile file : files) {
            xmlStrings.add(file.toString());
        }
        return doPostXmlSurefire(reportMetatData, xmlStrings);
    }

    public TestResult doPostXmlSurefire(ReportMetaData reportMetatData, List<String> xmlStrings) {
        reportMetatData.validateAndSetDefaults();
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuites(xmlStrings);
        TestResult testResult = SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
        ExecutionStagePath buildStagePath = reportCardService.getOrInsertExecutionStagePath(reportMetatData);
        testResult.setStageFk(buildStagePath.getStage().getStageId());
        return reportCardService.insertTestResult(testResult);
    }

    public static String fileToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
